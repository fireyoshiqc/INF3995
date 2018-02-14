"""Test_RingBuffer class"""


import unittest

import inf3995.utils as utils

"""
Test of the ring buffer readers (normal and queued) using the transition tree
formal testing method.

The states in the transition tree are as follow :
  * S1 : Has no new data
  * S2 : Has new data

Pretty self-explanatory I suppose.
"""
class Test_RingBuffer(unittest.TestCase):
	_BUFFER_SIZE = 16
	
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

