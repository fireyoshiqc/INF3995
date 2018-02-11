"""Application_Manager class"""


import signal
import time
import sys
import keyboard

import inf3995.rest as rest
from inf3995.core.Program_Options import *
from inf3995.core.Worker_Thread import *
from inf3995.core.Dummy_Task_Node import *


class Application_Manager(object):
	__instance = None
	
	def __new__(cls):
		if Application_Manager.__instance == None:
			Application_Manager.__instance = object.__new__(cls)
			instance = Application_Manager.__instance
			instance.__quit = False
			instance.__exit_code = 0
			instance.__task_nodes = []
			instance.__worker_threads = []
		
		return Application_Manager.__instance
	
	def startup(self, argv):
		self.__register_signal_handlers()
		
		Program_Options.configure_and_parse(argv)
		if len(argv) == 1:
			# TODO: Show GUI to enter the options visually
			print("And God said, Let there be a GUI: and there was a GUI (someday maybe)." "\n")
		
		self.__quit = False
		self.__exit_code = 0
		
		# TODO: Load settings
		
		self.__register_key_handlers()
		
		self.__setup_task_nodes()
	
	def execute(self):
		self.__start_threads()
		
		while not self.__quit:
			# TODO: Do something productive?
			time.sleep(0.1)
		
		self.__join_threads()
		
		return self.__exit_code
	
	def exit(self, exit_code = 0):
		self.__exit_code = exit_code
		self.__quit = True
	
	@staticmethod
	def __key_handler(event):
		if event.name == "esc":
			Application_Manager().exit()
	
	def __register_signal_handlers(self):
		pass
	
	def __register_key_handlers(self):
		keyboard.hook(Application_Manager.__key_handler)
	
	def __setup_task_nodes(self):
		# TODO: Build the task nodes
		dummy_node = Dummy_Task_Node()
		rest_node = rest.Rest_Handler_Task()
		
		# TODO: Connect the nodes
		
		# TODO: Build the worker threads
		# self.__build_thread([dummy_node], 0.5)
		self.__build_thread([rest_node])
	
	def __build_thread(self, task_nodes, max_freq = None):
		worker = Worker_Thread(max_freq)
		for node in task_nodes:
			worker.add_task_node(node)
		self.__worker_threads.append(worker)
	
	def __start_threads(self):
		for wt in self.__worker_threads:
			wt.init_task_nodes()
		
		if not self.__quit:
			for wt in self.__worker_threads:
				wt.start_paused()
			
			time.sleep(0.01)
			
			for wt in self.__worker_threads:
				wt.unpause()
	
	def __join_threads(self):
		for wt in self.__worker_threads:
			wt.finish(60.0)
			wt.terminate()
		
		for wt in self.__worker_threads:
			wt.cleanup_task_nodes()
		
		self.__worker_threads.clear()
		self.__task_nodes.clear()

