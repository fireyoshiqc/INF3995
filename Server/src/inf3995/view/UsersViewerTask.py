"""UsersViewerTask class"""


import time
import sys
from subprocess import Popen, PIPE, CREATE_NEW_CONSOLE, STDOUT

import inf3995.core
from inf3995.core.AbstractTaskNode import *
from inf3995.utils.SimpleNamedPipe import *


aux_console_code = """
import sys
import os

try:
	from inf3995.utils.SimpleNamedPipe import *
	
	pipe = SimpleNamedPipeClient("inf3995-users-view", PipeMode.READER)
	
	while True:
		foo = pipe.read()
		os.system("cls")
		print(foo.decode("utf-8"))
		sys.stdout.flush()
		
except Exception as e:
	print(e)
	while True: pass
"""


class UsersViewerTask(AbstractTaskNode):
	def __init__(self, rest_server):
		super(UsersViewerTask, self).__init__(False, 0)
		self.__event_logger = inf3995.core.ApplicationManager().get_event_logger()
		self.__rest_server = rest_server
		self.__aux_process = None
		self.__pipe = None
	
	def init(self):
		self.__pipe = SimpleNamedPipeServer("inf3995-users-view", PipeMode.WRITER)
		self.__aux_process = Popen([sys.executable, "-c", aux_console_code],
		                           stdin=None, stdout=None, stderr=None,
		                           bufsize=0, universal_newlines=False,
		                           creationflags=CREATE_NEW_CONSOLE)
	
	def on_first_run(self):
		pass
	
	def handle_data(self):
		try:
			msg_str = " --- Connected Clients --- " + "\n\n"
			sessions = self.__rest_server.get_all_sessions()
			for id, session in sessions.items():
				msg_str += session["user"] + "\n"
				msg_str += "* Session ID  : " + str(id) + "\n" + \
				           "* IP address  : " + session["ip"] + "\n" + \
				           "* Device type : " + session["device"] + "\n\n"
			self.__pipe.write(bytes(msg_str, "utf-8"))
		except Exception as e:
			self.__event_logger.log_error(str(e))
	
	def cleanup(self):
		self.__pipe.close()
		self.__aux_process.terminate()
