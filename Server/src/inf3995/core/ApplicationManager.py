"""ApplicationManager class"""


import signal
import time
import sys
import keyboard
import unittest

import inf3995.rest as rest
from inf3995.core.ProgramOptions import *
from inf3995.core.WorkerThread import *
from inf3995.core.DummyTaskNode import *


class ApplicationManager(object):
	__instance = None
	
	def __new__(cls):
		if ApplicationManager.__instance is None:
			ApplicationManager.__instance = object.__new__(cls)
			instance = ApplicationManager.__instance
			instance.__quit = False
			instance.__exit_code = 0
			instance.__task_nodes = []
			instance.__WorkerThreads = []
		
		return ApplicationManager.__instance
	
	def startup(self, argv):
		self.__register_signal_handlers()
		
		ProgramOptions.configure_and_parse(argv)
		if ProgramOptions.get_value("run-tests"):
			print("This will run the test suite instead of the server." "\n")
			return
		
		if len(argv) == 1:
			# TODO: Show GUI to enter the options visually
			print("And God said, Let there be a GUI: and there was a GUI (someday maybe)." "\n")
		
		self.__quit = False
		self.__exit_code = 0
		
		# TODO: Load settings
		
		self.__register_key_handlers()
		
		self.__setup_task_nodes()
	
	def execute(self):
		if ProgramOptions.get_value("run-tests"):
			return self.__run_tests()
		
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
			ApplicationManager().exit()
	
	def __register_signal_handlers(self):
		pass
	
	def __register_key_handlers(self):
		keyboard.hook(ApplicationManager.__key_handler)
	
	def __setup_task_nodes(self):
		# TODO: Build the task nodes
		dummy_node = DummyTaskNode()
		rest_node = rest.Rest_Handler_Task()
		
		# TODO: Connect the nodes
		
		# TODO: Build the worker threads
		self.__build_thread([dummy_node], 0.5)
		self.__build_thread([rest_node])
	
	def __build_thread(self, task_nodes, max_freq = None):
		worker = WorkerThread(max_freq)
		for node in task_nodes:
			worker.add_task_node(node)
		self.__WorkerThreads.append(worker)
	
	def __start_threads(self):
		for wt in self.__WorkerThreads:
			wt.init_task_nodes()
		
		if not self.__quit:
			for wt in self.__WorkerThreads:
				wt.start_paused()
			
			time.sleep(0.01)
			
			for wt in self.__WorkerThreads:
				wt.unpause()
	
	def __join_threads(self):
		for wt in self.__WorkerThreads:
			wt.finish(60.0)
			wt.terminate()
		
		for wt in self.__WorkerThreads:
			wt.cleanup_task_nodes()
		
		self.__WorkerThreads.clear()
		self.__task_nodes.clear()
	
	def __run_tests(self):
		test_program = unittest.main(module="inf3995.tests", argv=sys.argv[0:1],
		                             verbosity=2).result
		return not test_program.result.wasSuccessful()

