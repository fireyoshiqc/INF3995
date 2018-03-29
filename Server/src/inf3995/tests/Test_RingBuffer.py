"""Test_RingBuffer class"""


import unittest
import time
import threading
import sys

import inf3995.utils as utils

"""
Test of the ring buffer readers (normal and queued) using the transition tree
formal testing method.

The states in the transition tree are as follow :
  * S1 : Has no new data
  * S2 : Has new data

Tests for the buffer overflow counter of the queued reader.

Tests synchronised buffers using threaded readers and a slow producer.
"""
class Test_RingBuffer(unittest.TestCase):
	_BUFFER_SIZE = 8
	_LAST_PRODUCER_ITERATION = _BUFFER_SIZE * 2 - 1
	_PRODUCER_WAIT = 0.050
	_FAST_READER_WAIT = 0.001
	_SLOW_READER_WAIT = 0.050
	
	def setUp(self):
		self._rcu = utils.RcuRingBuffer(self._BUFFER_SIZE, utils.SyncData)
		self._producer = utils.SyncRcuRingBufferProducer(self._rcu.producer())
		self._reader = utils.SyncRcuRingBufferReader(self._producer)
		self._q_reader = utils.SyncRcuRingBufferQReader(self._producer)
	
	def tearDown(self):
		pass
	
	def test_reader_s1_s1(self):
		self.assertFalse(self._reader.has_new_data())
		self._reader.get()
		self.assertFalse(self._reader.has_new_data())
	
	def test_reader_s1_s2_s2(self):
		self.assertFalse(self._reader.has_new_data())
		self._producer.set(1)
		self._producer.set(2)
		self.assertTrue(self._reader.has_new_data())
	
	def test_reader_s1_s2_s1(self):
		self.assertFalse(self._reader.has_new_data())
		self._producer.set(1)
		self._producer.set(2)
		self.assertTrue(self._reader.has_new_data())
		value = self._reader.get()
		self.assertFalse(self._reader.has_new_data())
		self.assertEqual(value, 2)
	
	def test_q_reader_s1_s1(self):
		self.assertFalse(self._q_reader.has_new_data())
		value = self._q_reader.get()
		self.assertFalse(self._q_reader.has_new_data())
		self.assertTrue(value is None)
	
	def test_q_reader_s1_s2_s2(self):
		self.assertFalse(self._q_reader.has_new_data())
		self._producer.set(1)
		self._producer.set(2)
		self.assertTrue(self._q_reader.has_new_data())
	
	def test_q_reader_s1_s2_s1(self):
		self.assertFalse(self._q_reader.has_new_data())
		self._producer.set(1)
		self._producer.set(2)
		self.assertTrue(self._q_reader.has_new_data())
		value = self._q_reader.get()
		self.assertTrue(self._q_reader.has_new_data())
		self.assertEqual(value, 1)
		value = self._q_reader.get()
		self.assertFalse(self._q_reader.has_new_data())
		self.assertEqual(value, 2)
	
	def test_q_reader_overflow(self):
		self.assertFalse(self._q_reader.has_new_data())
		for i in range(1, self._BUFFER_SIZE + 2):
			self._producer.set(i)
		self.assertTrue(self._q_reader.has_new_data())
		value = self._q_reader.get()
		# The ring buffer has overflowed, therefore the 1 at the begining has
		# been overwritten by _BUFFER_SIZE + 1
		self.assertEqual(value, self._BUFFER_SIZE + 1)
		self.assertEqual(self._q_reader.get_n_overflows(), 1)
		
		# Do another round...
		for i in range(1, self._BUFFER_SIZE + 3):
			self._producer.set(i)
		value = self._q_reader.get()
		self.assertEqual(value, self._BUFFER_SIZE + 2)
		self.assertEqual(self._q_reader.get_n_overflows(), 2)
	
	def test_sync_fast_not_queued(self):
		self._do_sync_test(True, False)
	
	def test_sync_fast_queued(self):
		self._do_sync_test(True, True)
	
	def test_sync_slow_not_queued(self):
		self._do_sync_test(False, False)
	
	def test_sync_slow_queued(self):
		self._do_sync_test(False, True)
	
	def _do_sync_test(self, fast, queued):
		reader_result = []
		stop_thread = False
		reader_fn = None
		if fast:
			if queued:
				reader_fn = self._fast_queued_reader_fn
			else:
				reader_fn = self._fast_reader_fn
		else:
			if queued:
				reader_fn = self._slow_queued_reader_fn
			else:
				reader_fn = self._slow_reader_fn
		reader_thread = threading.Thread(target=reader_fn,
		                                 args=(reader_result, stop_thread,))
		reader_thread.start()
		
		expected = []
		self._produce_values(expected)
		
		reader_thread.join(Test_RingBuffer._BUFFER_SIZE * 2 *
		                   Test_RingBuffer._PRODUCER_WAIT * 4)
		if reader_thread.is_alive():
			stop_thread = True
			reader_thread.join()
		
		self.assertFalse(stop_thread)
		if not fast and not queued:
			min_expected_length = Test_RingBuffer._LAST_PRODUCER_ITERATION / 2 + 1
			self.assertGreater(len(reader_result), min_expected_length)
			self.assertLess(len(reader_result), len(expected))
		else:
			self.assertEqual(len(reader_result), len(expected))
			self.assertEqual(reader_result, expected)
	
	def _produce_values(self, result):
		for i in range(0, Test_RingBuffer._LAST_PRODUCER_ITERATION + 1):
			time.sleep(Test_RingBuffer._PRODUCER_WAIT)
			self._producer.set(i)
			result.append(i)
	
	def _fast_reader_fn(self, result, must_stop):
		previous_iteration = -1
		done = False
		while not done:
			new_data = self._reader.wait_for_new_data(0.01)
			
			value = self._reader.get()
			if new_data:
				result.append(value)
				previous_iteration = value
			done = must_stop or \
			       previous_iteration == Test_RingBuffer._LAST_PRODUCER_ITERATION
	
	def _fast_queued_reader_fn(self, result, must_stop):
		previous_iteration = -1
		done = False
		while not done:
			new_data = self._q_reader.wait_for_new_data(0.01)
			
			value = self._q_reader.get()
			if new_data:
				result.append(value)
				previous_iteration = value
			
			done = must_stop or \
			       previous_iteration == Test_RingBuffer._LAST_PRODUCER_ITERATION
	
	def _slow_reader_fn(self, result, must_stop):
		previous_iteration = -1
		done = False
		while not done:
			new_data = self._reader.wait_for_new_data(0.01)
			
			value = self._reader.get()
			if new_data:
				result.append(value)
				previous_iteration = value
				time.sleep(Test_RingBuffer._PRODUCER_WAIT * 1.5)
			
			done = must_stop or \
			       previous_iteration == Test_RingBuffer._LAST_PRODUCER_ITERATION
	
	def _slow_queued_reader_fn(self, result, must_stop):
		previous_iteration = -1
		done = False
		while not done:
			new_data = self._reader.wait_for_new_data(0.01)
			
			value = self._q_reader.get()
			if new_data:
				result.append(value)
				previous_iteration = value
				time.sleep(Test_RingBuffer._PRODUCER_WAIT * 1.5)
			
			done = must_stop or \
			       previous_iteration == Test_RingBuffer._LAST_PRODUCER_ITERATION

