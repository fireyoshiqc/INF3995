"""EventLog class"""


import time

import inf3995.utils as utils


class EventLog(object):
	def __init__(self):
		BufferType = utils.RcuRingBuffer
		ProducerType = utils.SyncRcuRingBufferProducer
		
		self.__event_buffer = BufferType(1024, utils.SyncData)
		self.__producer = ProducerType(self.__event_buffer.producer())
	
	def build_reader(self):
		return utils.SyncRcuRingBufferQReader(self.__producer)
	
	def log_info(self, msg):
		self._log_event("INFO", msg)
	
	def log_debug(self, msg):
		self._log_event("DEBUG", msg)
	
	def log_warning(self, msg):
		self._log_event("WARNING", msg)
	
	def log_error(self, msg):
		self._log_event("ERROR", msg)
	
	@staticmethod
	def _get_date_time_str(now):
		return time.strftime("[%Y-%m-%d %H:%M:%S]", now)
	
	
	def _log_event(self, event_type, event_msg):
		header_str = EventLog._get_date_time_str(time.localtime()) + \
		             "[" + event_type + "] "
		formatted_msg = event_msg.replace("\n", "\n" + (len(header_str) * " "))
		event_str = header_str + formatted_msg
		self.__producer.set(event_str)
