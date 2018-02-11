"""Rest_Handler_Task class"""


import socket

import cherrypy

from inf3995.core.Ab_Task_Node import *
# import inf3995.core
from inf3995.rest.Rest_Server import *


class Rest_Handler_Task(Ab_Task_Node):
	def __init__(self):
		super(Rest_Handler_Task, self).__init__(False, 0)
		
		self.__server = None
	
	def init(self):
		self.__server = Rest_Server()
		this_ip = socket.gethostbyname(socket.gethostname())
		cherrypy.config.update("config/cherrypy.server.conf")
		cherrypy.tree.mount(self.__server, "/", "config/cherrypy.app.conf")
		cherrypy.config.update({"server.socket_host" : this_ip})
	
	def on_first_run(self):
		cherrypy.engine.start()
	
	def handle_data(self):
		pass
	
	def cleanup(self):
		cherrypy.engine.exit()
		cherrypy.engine.block()

