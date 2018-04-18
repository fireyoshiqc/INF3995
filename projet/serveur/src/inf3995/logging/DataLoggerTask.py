"""DataLoggerTask class"""


import os
import time

import inf3995.core
from inf3995.core.AbstractTaskNode import *
import inf3995.data_rx
from inf3995.settings.ModuleTypes import *
from inf3995.settings.CANSid import *


class DataLoggerTask(AbstractTaskNode):
	def __init__(self): 
		super(DataLoggerTask, self).__init__(True, 0)
		
		self.__log_file = None
		self.__start_time = 0.0
	
	def init(self):
		startup_time = inf3995.core.ApplicationManager().get_startup_date_time_str()
		log_file_name = startup_time + ".csv"
		
		self.__log_file = open(log_file_name, "w", encoding="utf-8")
	
	def on_first_run(self):
		self.__start_time = time.monotonic()
		self.__log_file.write("Temps (s);Direction;Mod. source;Ser. source;" + \
		                      "Mod. dest;Ser. dest;ID message;Donnée 1;Donnée 2" + "\n")
		self.__log_file.flush()
	
	def handle_data(self):
		can_data = self._get_source_data()
		src_serial_str = "ALL_SERIAL_NBS" if can_data.src_serial == ALL_SERIAL_NBS \
		                 else str(can_data.src_serial)
		dest_serial_str = "ALL_SERIAL_NBS" if can_data.dest_serial == ALL_SERIAL_NBS \
		                  else str(can_data.dest_serial)
		self.__log_file.write(str(time.monotonic() - self.__start_time) + ";" + \
		                      "IN" + ";" + \
		                      can_data.src_type.name + ";" + \
		                      src_serial_str + ";" + \
		                      can_data.dest_type.name + ";" + \
		                      dest_serial_str + ";" + \
		                      can_data.sid.name + ";" + \
		                      str(can_data.data1) + ";" + \
		                      str(can_data.data2) + "\n")
	
	def cleanup(self):
		self.__log_file.close()
	
	def get_server_app(self):
		return self.__server

