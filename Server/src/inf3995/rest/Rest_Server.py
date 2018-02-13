"""Rest_Server class"""


import datetime
import json

import cherrypy

from inf3995.core.Program_Options import *


@cherrypy.expose
class Rest_Server(object):
	__POSSIBLE_DEVICES = ["pc", "tablet", "mobile"]
	__UNKNOWN_DEVICE = "n/a"
	
	def __init__(self):
		self.__sessions = {}
	
	def get_config(self, request, url):
		switch = {
			"basic"       : self.get_config_basic,
			"rockets"     : self.get_config_rockets,
			"map"         : self.get_config_map,
			"miscFiles"   : self.get_config_miscfiles,
			"deviceTypes" : self.get_config_devicetypes
		}
		return switch.get(url[0], Rest_Server._raise_error_404)(request, url[1:len(url)])
	
	def get_config_basic(self, request, url):
		if not self._is_logged_in():
			Rest_Server._raise_http_error(401)
		
		if len(url) == 0:
			result = {
				"otherPort" : Program_Options.get_value("server"),
				"layout"    : Program_Options.get_value("rocket"),
				"map"       : Program_Options.get_value("map")
			}
			cherrypy.response.headers["Content-Type"] = "application/json"
			return json.dumps(result, indent=2).encode("utf-8")
		else:
			Rest_Server._raise_http_error(404)
	
	def get_config_rockets(self, request, url):
		if not self._is_logged_in():
			Rest_Server._raise_http_error(401)
		
		if len(url) == 0:
			return "All the rockets!"
		elif len(url) == 1:
			return "Just this rocket : " + url[0]
		else:
			Rest_Server._raise_http_error(404)
	
	def get_config_map(self, request, url):
		if not self._is_logged_in():
			Rest_Server._raise_http_error(401)
		
		if len(url) == 0:
			result = {
				"map" : Program_Options.get_value("map"),
			}
			cherrypy.response.headers["Content-Type"] = "application/json"
			return json.dumps(result, indent=2).encode("utf-8")
		else:
			Rest_Server._raise_http_error(404)
	
	def get_config_miscfiles(self, request, url):
		if not self._is_logged_in():
			Rest_Server._raise_http_error(401)
		
		if len(url) == 0:
			return "All the files!"
		elif len(url) == 1:
			return "Just this file : " + url[0]
		else:
			Rest_Server._raise_http_error(404)
	
	def get_config_devicetypes(self, *args):
		result = {}
		for i, dev in enumerate(Rest_Server.__POSSIBLE_DEVICES):
			result["deviceType" + str(i + 1)] = dev
		result["unknown"] = Rest_Server.__UNKNOWN_DEVICE
		cherrypy.response.headers["Content-Type"] = "application/json"
		return json.dumps(result, indent=2).encode("utf-8")
	
	def post_users(self, request, url):
		switch = {
			"login"  : self.post_users_login,
			"logout" : self.post_users_logout,
		}
		return switch.get(url[0], Rest_Server._raise_error_404)(request, url[1:len(url)])
	
	def post_users_login(self, request, url):
		if len(url) == 0:
			data = request.json
			if self._is_valid_login_info(data):
				self.__sessions[cherrypy.session.id] = {}
				session = self._get_session()
				session["user"] = data["username"]
				session["logged"] = True
				session["ip"] = request.remote.ip
				session["datetime"] = datetime.datetime.now()
				session["device"] = self._get_device_from_request(request)
			else:
				Rest_Server._raise_http_error(401)
		else:
			Rest_Server._raise_http_error(404)
	
	def post_users_logout(self, request, url):
		if len(url) == 0:
			data = request.json
			if len(data) == 1 and "username" in data:
				if "username" in data and data["username"] == cherrypy.session["user"]:
					self.__sessions.pop(cherrypy.session.id)
					cherrypy.session.clear()
					cherrypy.session.delete()
				else:
					Rest_Server._raise_http_error(401)
			else:
				Rest_Server._raise_http_error(400)
		else:
			Rest_Server._raise_http_error(404)
	
	def GET(self, *args):
		if len(args) > 1 and args[0] == "config":
			return self.get_config(cherrypy.request, args[1:len(args)])
		else:
			Rest_Server._raise_http_error(404)
	
	@cherrypy.tools.accept(media="application/json")
	@cherrypy.tools.json_in()
	def POST(self, *args):
		if len(args) == 2 and args[0] == "users":
			return self.post_users(cherrypy.request, args[1:len(args)])
		else:
			Rest_Server._raise_http_error(404)
	
	@staticmethod
	def _raise_http_error(code):
		raise cherrypy.HTTPError(code)
	
	@staticmethod
	def _raise_error_404(*args):
		raise Rest_Server._raise_http_error(404)
	
	def _is_logged_in(self):
		return cherrypy.session.get("logged", False)
	
	def _is_valid_login_info(self, data):
		expected = [{"username" : "foo", "password" : "password1234"},
		            {"username" : "bar", "password" : "password4321"},
		            {"username" : "qux", "password" : "1234password"}]
		
		is_valid = False
		for x in expected:
			is_valid = data["username"] == x["username"] and \
			           data["password"] == x["password"]
			if is_valid:
				return True
		return False
	
	def _get_session(self):
		return self.__sessions[cherrypy.session.id]
	
	def _get_device_from_request(self, request):
		data = request.json
		if "device" in data:
			if data["device"].lower() in Rest_Server.__POSSIBLE_DEVICES:
				return data["device"].lower()
			else:
				return Rest_Server.__UNKNOWN_DEVICE
		else:
			# TODO: Parse the User-Agent string to try and find the device type.
			return Rest_Server.__UNKNOWN_DEVICE


