"""AbstractTaskNode class"""


import abc

import inf3995.utils as utils


class BasicTaskNodeProducer(object):
	def __init__(self, buffer_size):
		if buffer_size > 0:
			Buffer_Type = utils.RcuRingBuffer
			Producer_Type = utils.SyncRcuRingBufferProducer
			
			self._output_data_buffer = Buffer_Type(buffer_size, utils.SyncData)
			self._production_rcu = Producer_Type(self._output_data_buffer.producer())
	
	def _produce_data(self, value):
		if self._production_rcu != None:
			self._production_rcu.set(value)


class AbstractTaskNode(BasicTaskNodeProducer):
	__metaclass__ = abc.ABCMeta
	
	def __init__(self, is_queued_input_data = False, buffer_size = 1024):
		super(AbstractTaskNode, self).__init__(buffer_size)
		self.__is_queued_input_data = is_queued_input_data
		self.__output_data_buffer = None
		if buffer_size > 0:
			self.__output_data_buffer = utils.RcuRingBuffer(buffer_size,
			                                                  utils.SyncData)
		self.__data_reader = None
		self.__first_run = True
		self.__is_finishing = False
		self.__is_finished = False
	
	def run(self):
		if self.__first_run:
			self.on_first_run()
			self.__first_run = False
		
		if not self.__is_finished:
			if self._has_new_data():
				if self.__is_queued_input_data and self.__data_reader != None:
					while self._has_new_data():
						self.handle_data()
				else:
					self.handle_data()
		
		self.__is_finished = self.__is_finishing
	
	def finish(self):
		self.__is_finishing = True
	
	def connect_to_source(self, task_node):
		# TODO: Get the buffer from task_node and build a data reader around it.
		out_buf = task_node.get_output_buffer()
		if self.__is_queued_input_data:
			self.__data_reader = utils.SyncRcuRingBufferQReader(out_buf)
		else:
			self.__data_reader = utils.SyncRcuRingBufferReader(out_buf)
	
	def is_finished(self):
		return self.__is_finished
	
	def get_output_buffer(self):
		return self._production_rcu
	
	@abc.abstractmethod
	def init(self):
		pass
	
	@abc.abstractmethod
	def on_first_run(self):
		pass
	
	@abc.abstractmethod
	def handle_data(self):
		pass 
	
	@abc.abstractmethod
	def cleanup(self):
		pass
	
	def _get_source_data(self):
		if self.__data_reader != None:
			return self.__data_reader.get()
		else:
			return None
	
	def _has_new_data(self):
		if self.__data_reader != None:
			return self.__data_reader.has_new_data()
		else:
			return not self.__is_finishing

