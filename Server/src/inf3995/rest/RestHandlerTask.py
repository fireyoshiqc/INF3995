"""Rest_Handler_Task class"""


import socket

import cherrypy

from inf3995.core.AbstractTaskNode import *
# import inf3995.core
from inf3995.rest.RestServer import *


class RestHandlerTask(AbstractTaskNode):
	def __init__(self):
		super(RestHandlerTask, self).__init__(False, 0)
		
		self.__server = None
	
	def init(self):
		self.__server = RestServer()
		cherrypy.config.update("config/cherrypy.server.conf")
		cherrypy.tree.mount(self.__server, "/", "config/cherrypy.app.conf")
		
		if cherrypy.config.get("server.socket_host", "") == "":
			this_ip = socket.gethostbyname(socket.gethostname())
			cherrypy.config.update({"server.socket_host" : this_ip})
	
	def on_first_run(self):
		cherrypy.engine.start()
	
	def handle_data(self):
		pass
	
	def cleanup(self):
		cherrypy.engine.exit()
		cherrypy.engine.block()

