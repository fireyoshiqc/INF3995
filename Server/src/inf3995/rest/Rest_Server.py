"""Rest_Server class"""


import datetime
import json

import cherrypy

from inf3995.core.Program_Options import *


@cherrypy.expose
class Rest_Server(object):
	def __init__(self):
		self.__sessions = {}
	
	@staticmethod
	def raise_http_error(code):
		raise cherrypy.HTTPError(code)
	
	@staticmethod
	def raise_error_404(*args):
		raise Rest_Server.raise_http_error(404)
	
	def is_logged_in(self):
		return cherrypy.session.get("logged", False)
	
	def get_config(self, request, url):
		switch = {
			"basic"     : self.get_config_basic,
			"rockets"   : self.get_config_rockets,
			"map"       : self.get_config_map,
			"miscFiles" : self.get_config_miscfiles
		}
		return switch.get(url[0], Rest_Server.raise_error_404)(request, url[1:len(url)])
	
	def get_config_basic(self, request, url):
		if len(url) == 0:
			result = {
				"otherPort" : Program_Options.get_value("server"),
				"layout"    : Program_Options.get_value("rocket"),
				"map"       : Program_Options.get_value("map")
			}
			cherrypy.response.headers["Content-Type"] = "application/json"
			return json.dumps(result, indent=2).encode("utf-8")
		else:
			Rest_Server.raise_http_error(404)
	
	def get_config_rockets(self, request, url):
		if len(url) == 0:
			return "All the rockets!"
		elif len(url) == 1:
			return "Just this rocket : " + url[0]
		else:
			Rest_Server.raise_http_error(404)
	
	def get_config_map(self, request, url):
		if len(url) == 0:
			return "There is nothing more dangerous than a Lieutenant with a map."
		else:
			Rest_Server.raise_http_error(404)
	
	def get_config_miscfiles(self, request, url):
		if len(url) == 0:
			return "All the files!"
		elif len(url) == 1:
			return "Just this file : " + url[0]
		else:
			Rest_Server.raise_http_error(404)
	
	def post_users(self, request, url):
		switch = {
			"login"  : self.post_users_login,
			"logout" : self.post_users_logout,
		}
		return switch.get(url[0], Rest_Server.raise_error_404)(request, url[1:len(url)])
	
	def post_users_login(self, request, url):
		if len(url) == 0:
			expected = [{"username" : "foo", "password" : "password1234"},
			            {"username" : "bar", "password" : "password4321"},
			            {"username" : "qux", "password" : "1234password"}]
			
			data = request.json
			if data in expected:
				cherrypy.session["user"] = data["username"]
				cherrypy.session["logged"] = True
				cherrypy.session["ip"] = request.remote.ip
				cherrypy.session["datetime"] = datetime.datetime.now()
				cherrypy.session["user_agent"] = request.headers.get("User-Agent")
				self.__sessions[cherrypy.session.id] = dict(cherrypy.session)
			else:
				Rest_Server.raise_http_error(401)
		else:
			Rest_Server.raise_error_404
	
	def post_users_logout(self, request, url):
		if len(url) == 0:
			data = request.json
			if len(data) == 1 and "username" in data:
				if "username" in data and data["username"] == cherrypy.session["user"]:
					self.__sessions.pop(cherrypy.session.id)
					cherrypy.session.clear()
					cherrypy.session.delete()
				else:
					Rest_Server.raise_http_error(401)
			else:
				Rest_Server.raise_http_error(400)
		else:
			Rest_Server.raise_http_error(404)
	
	def GET(self, *args):
		if self.is_logged_in():
			if len(args) > 1 and args[0] == "config":
				return self.get_config(cherrypy.request, args[1:len(args)])
			else:
				Rest_Server.raise_http_error(404)
		else:
			raise Rest_Server.raise_http_error(401)
	
	@cherrypy.tools.accept(media="application/json")
	@cherrypy.tools.json_in()
	def POST(self, *args):
		if len(args) == 2 and args[0] == "users":
			return self.post_users(cherrypy.request, args[1:len(args)])
		else:
			Rest_Server.raise_http_error(404)


