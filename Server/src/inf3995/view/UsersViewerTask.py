"""UsersViewerTask class"""


import sys
from subprocess import Popen, PIPE, CREATE_NEW_CONSOLE

import inf3995.core
from inf3995.core.AbstractTaskNode import *


aux_console_code = """
import sys
import time

while True:
	sys.stdout.write("Hello, world!" + "\\n")
	sys.stdout.flush()
	time.sleep(1.0)
"""

class UsersViewerTask(AbstractTaskNode):
	def __init__(self):
		super(UsersViewerTask, self).__init__(False, 0)
		self.__aux_process = None
	
	def init(self):
		self.__aux_process = Popen([sys.executable, "-c", aux_console_code],
		                           stdin=PIPE, bufsize=1, universal_newlines=True,
		                           creationflags=CREATE_NEW_CONSOLE)
	
	def on_first_run(self):
		pass
	
	def handle_data(self):
		pass
	
	def cleanup(self):
		self.__aux_process.terminate()
