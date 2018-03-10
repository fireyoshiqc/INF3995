"""DataLoggerTask class"""


import os
import time

import inf3995.core
from inf3995.core.AbstractTaskNode import *
import inf3995.data_rx


class DataLoggerTask(AbstractTaskNode):
	def __init__(self): 
		super(DataLoggerTask, self).__init__(True, 0)
		
		self.__log_file = None
		self.__start_time = 0.0
	
	def init(self):
		startup_time = inf3995.core.ApplicationManager().get_startup_date_time_str()
		log_file_name = startup_time + ".csv"
		
		self.__log_file = open(log_file_name, "w", encoding="utf-8")
		print(log_file_name)
	
	def on_first_run(self):
		self.__start_time = time.monotonic()
		self.__log_file.write("Temps (s);Direction;Mod. source;Ser. source;" + \
		                      "Mod. dest;Ser. dest;ID message;Donnée 1;Donnée 2" + "\n")
		self.__log_file.flush()
	
	def handle_data(self):
		can_data = self._get_source_data()
		self.__log_file.write(str(time.monotonic() - self.__start_time) + ";")
		self.__log_file.write(str(can_data.data1) + "\n")
	
	def cleanup(self):
		self.__log_file.close()
	
	def get_server_app(self):
		return self.__server

