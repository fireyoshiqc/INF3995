"""Rcu_Ring_Buffer class"""


class Rcu_Ring_Buffer(object):
	class Reader(object):
		def __init__(self, parent = None):
			self._parent = parent
		
		def get_buffer_size(self):
			return self._parent._buffer_size
		
		def get(self, index = None):
			if index == None:
				return self._parent._buffer[self._get_buffer_index()]
			else:
				return self._parent._buffer[index] 
		
		def get_production_index(self):
			return self._get_next_index()
		
		def _get_buffer_index(self):
			return self._parent._buffer_index
		
		def _get_next_index(self):
			return (self._get_buffer_index() + 1) % self._parent._buffer_size
		
		def _get_buffer(self):
			return self._parent._buffer
	
	class Producer(object):
		def __init__(self, parent = None):
			self._parent = parent
			self.__production_index = 1
		
		def get_buffer_size(self):
			return self._parent._buffer_size
		
		def get(self, index = None):
			return self._parent.reader().get(index)
		
		def set(self, value):
			self.set_next_production_elem(value)
			self.publish()
		
		def get_next_production_elem(self):
			self.__production_index = self._get_next_index()
			return self._parent._buffer[self.__production_index]
		
		def set_next_production_elem(self, value):
			self.__production_index = self._get_next_index()
			self._parent._buffer[self.__production_index] = value
		
		def get_production_index(self):
			return self._get_next_index()
		
		def publish(self):
			self._parent._buffer_index = self.__production_index
		
		def _get_buffer_index(self):
			return self._parent._buffer_index
		
		def _get_next_index(self):
			return (self._get_buffer_index() + 1) \
			       % self._parent._buffer_size
	
	def __init__(self, buffer_size, cls = None):
		self._buffer_size = buffer_size
		self._buffer_index = 0
		self._buffer = []
		for i in range(0, self._buffer_size):
			self._buffer.append(cls())
	
	def get(self, index = None):
		return self.reader().get(index)
	
	def set(self, value):
		self.producer().set(value)
	
	def get_buffer_size(self):
		return self._buffer_size
	
	def reader(self):
		return Rcu_Ring_Buffer.Reader(self)
	
	def producer(self):
		return Rcu_Ring_Buffer.Producer(self)

