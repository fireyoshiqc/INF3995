"""EventLoggerTask class"""


import inf3995.core
from inf3995.core.AbstractTaskNode import *


class EventLoggerTask(AbstractTaskNode):
	def __init__(self):
		super(EventLoggerTask, self).__init__(True, 0)
		self.__log_file = None
	
	def init(self):
		startup_time = inf3995.core.ApplicationManager().get_startup_date_time_str()
		log_file_name = startup_time + ".log"
		
		self.__log_file = open(log_file_name, "w", encoding="utf-8")
	
	def on_first_run(self):
		pass
	
	def handle_data(self):
		self.__write_new_events()
	
	def cleanup(self):
		self.__write_new_events()
		self.__log_file.close()
	
	def __write_new_events(self):
		while self._has_new_data():
			self.__log_file.write(self._get_source_data() + "\n")

