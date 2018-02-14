"""Synchronised ring buffer classes"""


import abc
import time
import threading
import math

from inf3995.utils.RcuRingBuffer import *


class SyncData:
	def __init__(self, publish_count = 0, value = None):
		self.__publish_count = publish_count
		self.__value = value
	
	@property
	def publish_count(self):
		return self.__publish_count
	
	@publish_count.setter
	def publish_count(self, publish_count):
		self.__publish_count = publish_count
	
	@property
	def value(self):
		return self.__value
	
	@value.setter
	def value(self, value):
		self.__value = value


class SyncRcuRingBufferBase(object):
	def __init__(self, rcu):
		self._rcu = rcu
		self._current_publish_count = 0
		self._events = [threading.Event(), threading.Event()]
	
	def get_publish_count(self):
		return self._current_publish_count
	
	def _get_current_event_index(self):
		return self._current_publish_count & 1
	
	def _get_next_event_index(self):
		return (self._current_publish_count + 1) & 1


class SyncRcuRingBufferProducer(SyncRcuRingBufferBase):
	def __init__(self, rcu):
		super(SyncRcuRingBufferProducer, self).__init__(rcu)
	
	def get(self, index = None):
		return self._rcu.get(index)
	
	def set(self, value):
		self.set_next_production_elem(value)
		self.publish()
	
	def set_next_production_elem(self, value):
		data = SyncData(self._current_publish_count, value)
		self._rcu.set_next_production_elem(data)
	
	def publish(self):
		self._set_event()
		self.publish_without_events()
		self._reset_event()
	
	def publish_without_events(self):
		self._current_publish_count += 1
		data = self._rcu.get_next_production_elem()
		data.publish_count = self._current_publish_count
		self._rcu.set_next_production_elem(data)
		self._rcu.publish()
	
	def get_buffer_size(self):
		return self._rcu.get_buffer_size()
	
	def get_production_index(self):
		return self._rcu.get_production_index()
	
	def _set_event(self):
		self._events[self._get_current_event_index()].set()
	
	def _reset_event(self):
		self._events[self._get_current_event_index()].clear()


class AbstractSyncRcuRingBufferReader(SyncRcuRingBufferBase):
	__metaclass__ = abc.ABCMeta
	
	def __init__(self, rcu):
		super(AbstractSyncRcuRingBufferReader, self).__init__(rcu)
	
	@abc.abstractmethod
	def has_new_data(self):
		pass
	
	def wait_for_new_data(self, timeout = None):
		if timeout is not None and timeout > 0.0:
			return self.has_new_data() and self._wait_for_new_event(timeout)
		else:
			return self.has_new_data() and self._wait_for_new_event(None)
	
	def _wait_for_new_event(self, timeout):
		return self._events[self._get_next_event_index()].wait(timeout)


class SyncRcuRingBufferReader(AbstractSyncRcuRingBufferReader):
	def __init__(self, rcu):
		super(SyncRcuRingBufferReader, self).__init__(rcu)
	
	def get(self):
		data = self._rcu.get()
		if data is None:
			return None
		self._current_publish_count = data.publish_count
		return data.value
	
	def has_new_data(self):
		return self._rcu.get() is not None and \
		       self._rcu.get().publish_count != self._current_publish_count


class SyncRcuRingBufferQReader(AbstractSyncRcuRingBufferReader):
	def __init__(self, rcu):
		super(SyncRcuRingBufferQReader, self).__init__(rcu)
		self._circular_buffer_index = 1;
		self._n_overflows = 0;
		n = math.sqrt(self._rcu.get_buffer_size()) // 2
		self._n_frames_to_skip_on_overflow = min(1, n)
	
	def get(self):
		if self._circular_buffer_index != self._rcu.get_production_index():
			data = self._rcu.get(self._circular_buffer_index)
			self._circular_buffer_index = (self._circular_buffer_index + 1) \
			                              % self._rcu.get_buffer_size()
			if data.publish_count > self._current_publish_count + 1:
				self._n_overflows += 1
				self._circular_buffer_index += self._n_frames_to_skip_on_overflow
			self._current_publish_count = data.publish_count
			return data.value
		else:
			return None
	
	def has_new_data(self):
		return self._circular_buffer_index != self._rcu.get_production_index() \
		       and self._rcu.get().publish_count != self._current_publish_count
	
	def get_n_overflows(self):
		return self._n_overflows

