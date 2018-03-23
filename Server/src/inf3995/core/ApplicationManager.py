"""ApplicationManager class"""


import signal
import time
import sys
import keyboard
import unittest
from enum import Enum

import inf3995.rest as rest
import inf3995.data_rx as data_rx
import inf3995.data_tx as data_tx
from inf3995.core.ProgramOptions import *
from inf3995.core.WorkerThread import *
from inf3995.logging.EventLog import *
from inf3995.core.DummyTaskNode import *
from inf3995.settings.CANSidParser import CANSidParser
from inf3995.settings.SettingsManager import *
from inf3995.logging.DataLoggerTask import *
from inf3995.logging.EventLoggerTask import *
from inf3995.view.EventLogViewerTask import *
from inf3995.view.UsersViewerTask import *

class _ConnectorType(Enum):
	SERIAL = 1  # Start at 1 because 0 is False in a boolean sense
	SIMULATION = 2

class ApplicationManager(object):
	__instance = None
	
	def __new__(cls):
		if ApplicationManager.__instance is None:
			ApplicationManager.__instance = object.__new__(cls)
			instance = ApplicationManager.__instance
			instance.__quit = False
			instance.__exit_code = 0
			instance.__task_nodes = []
			instance.__worker_threads = []
			instance.__settings_manager = SettingsManager()
			instance.__startup_date_time_str = ""
			instance.__event_log = EventLog()
		
		return ApplicationManager.__instance
	
	def startup(self, argv):
		self.__register_signal_handlers()
		
		self.__startup_date_time_str = time.strftime("%Y_%m_%d_%H_%M_%S",
		                                             time.localtime())
		
		ProgramOptions.configure_and_parse(argv)
		
		if len(argv) == 1:
			# TODO: Show GUI to enter the options visually
			print("And God said, Let there be a GUI: and there was a GUI (someday maybe)." "\n")
			sys.stdout.flush()
		
		self.__quit = False
		self.__exit_code = 0
		
		self.__settings_manager.load_settings_from_file("config/settings.ini")
		
		self.__register_key_handlers()
		
		if ProgramOptions.get_value("run-tests"):
			print("This will run the test suite instead of the server." "\n")
			sys.stdout.flush()
			return
		elif ProgramOptions.get_value("edit-passwords"):
			print("This will run the interactive password editor." "\n")
			sys.stdout.flush()
			return
		
		self.__setup_task_nodes()
	
	def execute(self):
		if ProgramOptions.get_value("run-tests"):
			return self.__run_tests()
		elif ProgramOptions.get_value("edit-passwords"):
			return self.__run_password_editor()
		
		# No need to start the threads if exit is already signalled
		if self.__quit:
			return self.__exit_code
		
		self.__start_threads()
		
		while not self.__quit:
			# TODO: Do something productive?
			time.sleep(0.1)
		
		self.__join_threads()
		
		return self.__exit_code
	
	def exit(self, exit_code = 0):
		self.__exit_code = exit_code
		self.__quit = True
	
	def get_settings_manager(self):
		return self.__settings_manager
	
	def get_event_logger(self):
		return self.__event_log
	
	def get_startup_date_time_str(self):
		return self.__startup_date_time_str
	
	@staticmethod
	def __key_handler(event):
		if event.name == "esc":
			ApplicationManager().exit()
	
	def __register_signal_handlers(self):
		pass
	
	def __register_key_handlers(self):
		try:
			keyboard.hook(ApplicationManager.__key_handler)
		except OSError:
			self.__event_log.log_warning("Keyboard hook not registered, ESC key will not work")
	
	def __setup_task_nodes(self):
		# TODO: Build the task nodes
		dummy_node = DummyTaskNode()
		rest_node = rest.RestHandlerTask()
		data_logger_node = DataLoggerTask()
		users_viewer_node = UsersViewerTask(rest_node.get_server_app())
		log_viewer_node = EventLogViewerTask()
		event_logger_node = EventLoggerTask()

		baudrate = ProgramOptions.get_value('baudrate')
		connector_type = ProgramOptions.get_value('connector-type').upper()
		connector_file = ProgramOptions.get_value('connector-file')
		# TODO: Figure out a cleaner way to do this to avoid code repetition
		if _ConnectorType[connector_type] == _ConnectorType.SERIAL:
			rx_node = data_rx.USBReaderTask(serial_port=connector_file,
			                                baudrate=baudrate)
			osc_tx_node = data_tx.OscTxTask()
			osc_sender = osc_tx_node.get_sender()
			rest_server = rest_node.get_server_app()
			rest_server.register_ip_callbacks(osc_sender.add_socket,
			                                  osc_sender.remove_socket)
			tx_node = osc_tx_node
		elif _ConnectorType[connector_type] == _ConnectorType.SIMULATION:
			rx_node = data_rx.CSVReaderTask(log_file=connector_file)
			osc_tx_node = data_tx.OscTxTask()
			osc_sender = osc_tx_node.get_sender()
			rest_server = rest_node.get_server_app()
			rest_server.register_ip_callbacks(osc_sender.add_socket,
			                                  osc_sender.remove_socket)
			tx_node = osc_tx_node
		else:
			# This shouldn't happen because the inputs are filtered
			# in ProgramOptions on startup
			self.__event_log.log_error(__name__ + ": Unrecognized connector type")
			ApplicationManager().exit(1)

		# TODO: Move to settings manager
		CANSidParser()
		
		# TODO: Connect the nodes
		tx_node.connect_to_source(rx_node)
		data_logger_node.connect_to_source(rx_node)
		event_logger_node.connect_to_source(log_viewer_node)

		# TODO: Build the worker threads
		# self.__build_thread([dummy_node], 0.5)
		self.__build_thread([rest_node])
		self.__build_thread([rx_node])
		self.__build_thread([tx_node], 20.0)
		self.__build_thread([data_logger_node])
		self.__build_thread([log_viewer_node, users_viewer_node], 15.0)
		self.__build_thread([event_logger_node])
	
	def __build_thread(self, task_nodes, max_freq = None):
		worker = WorkerThread(max_freq)
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
	
	def __run_tests(self):
		test_program = unittest.main(module="inf3995.tests", argv=sys.argv[0:1],
		                             verbosity=2).result
		return not test_program.result.wasSuccessful()
	
	def __run_password_editor(self):
		settings = self.get_settings_manager().settings
		users_file = settings["Authentication"]["users_file"]
		auth_manager = rest.AuthenticationManager()
		auth_manager.launch_interactive_editor(users_file)
		
		return 0

