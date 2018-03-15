"""UsersViewerTask class"""


import time
import sys
import platform
import subprocess
import os

import inf3995.core
from inf3995.core.AbstractTaskNode import *
from inf3995.utils.SimpleNamedPipe import *
from inf3995.view.SecondConsole import SignalCodes, PIPE_NAME


class UsersViewerTask(AbstractTaskNode):
	def __init__(self, rest_server):
		super(UsersViewerTask, self).__init__(False, 0)
		self.__event_logger = inf3995.core.ApplicationManager().get_event_logger()
		self.__rest_server = rest_server
		self.__aux_process = None
		self.__pipe = None
		self.__last_sessions = None
	
	def init(self):
		self.__pipe = SimpleNamedPipeServer(PIPE_NAME, PipeMode.WRITER)
		creation_flags = 0
		if platform.system() == "Windows":
			creation_flags = subprocess.CREATE_NEW_CONSOLE
			opt_shell = False
		
		current_file_dir = os.path.dirname(os.path.realpath(__file__))
		src_file_path = os.path.join(current_file_dir, "SecondConsole.py")
		self.__aux_process = subprocess.Popen([sys.executable, src_file_path],
		                                bufsize=0, universal_newlines=False,
		                                creationflags=creation_flags)
	
	def on_first_run(self):
		pass
	
	def handle_data(self):
		try:
			sessions = self.__rest_server.get_all_sessions()
			if sessions == self.__last_sessions:
				return
			msg_str = " --- Connected Clients --- " + "\n\n"
			for id, session in sessions.items():
				msg_str += session["user"] + "\n"
				msg_str += "* Session ID  : " + str(id) + "\n" + \
				           "* IP address  : " + session["ip"] + "\n" + \
				           "* Device type : " + session["device"] + "\n\n"
			self.__pipe.write(bytes(msg_str, "utf-8"))
			self.__last_sessions = sessions
		except Exception as e:
			self.__event_logger.log_error(str(e))
	
	def cleanup(self):
		msg = bytearray(1)
		msg = SignalCodes.EXIT
		self.__pipe.write(bytes(msg))
		self.__pipe.close()
		self.__aux_process.terminate()
