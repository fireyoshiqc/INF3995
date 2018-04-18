"""Rest_Handler_Task class"""


import socket

import cherrypy

from inf3995.core.AbstractTaskNode import *
import inf3995.core
from inf3995.rest.RestServer import *


class RestHandlerTask(AbstractTaskNode):
	def __init__(self):
		super(RestHandlerTask, self).__init__(False, 0)
		
		self.__server = RestServer()
	
	def init(self):
		settings = inf3995.core.ApplicationManager().get_settings_manager().settings
		server_config_file = settings["REST"]["server_config_file"]
		app_config_file = settings["REST"]["app_config_file"]
		timeout = settings["REST"]["users_timeout_minutes"]
		cherrypy.config.update(server_config_file)
		cherrypy.tree.mount(self.__server, "/", app_config_file)
		cherrypy.tree.apps.get("").config["/"]["tools.sessions.timeout"] = int(float(timeout)) + 1
		
		if cherrypy.config.get("server.socket_host", "") == "":
			this_ip = socket.gethostbyname(socket.gethostname())
			cherrypy.config.update({"server.socket_host" : this_ip})
	
	def on_first_run(self):
		cherrypy.engine.start()
	
	def handle_data(self):
		self.__server.drop_dead_clients()
	
	def cleanup(self):
		cherrypy.engine.exit()
		cherrypy.engine.block()
	
	def get_server_app(self):
		return self.__server

