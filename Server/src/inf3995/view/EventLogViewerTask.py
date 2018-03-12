"""EventLogViewerTask class"""


import sys

import inf3995.core
from inf3995.core.AbstractTaskNode import *


class EventLogViewerTask(AbstractTaskNode):
	def __init__(self):
		super(EventLogViewerTask, self).__init__(False, 1024)
		self.__event_logger = inf3995.core.ApplicationManager().get_event_logger()
		self.__event_queue = self.__event_logger.build_reader()
	
	def init(self):
		pass
	
	def on_first_run(self):
		pass
	
	def handle_data(self):
		self.__show_new_events()
	
	def cleanup(self):
		self.__show_new_events()
	
	def __show_new_events(self):
		while self.__event_queue.has_new_data():
			event_str = self.__event_queue.get()
			print(event_str)
			self._produce_data(event_str)
		sys.stdout.flush()
