"""RestServer class"""


import datetime
import json
import os
import collections

import cherrypy

import inf3995.core
from inf3995.core.ProgramOptions import *
from inf3995.rest.AuthenticationManager import *


@cherrypy.expose
class RestServer(object):
	__POSSIBLE_DEVICES = ["pc", "tablet", "mobile"]
	__UNKNOWN_DEVICE = "n/a"
	__MISC_FILES_DIR = "miscFiles"
	__ROCKETS_DIR = "rockets"
	
	def __init__(self):
		self.__sessions = {}
		self.__skip_auth = inf3995.core.ProgramOptions.get_value("skip-auth")
		self.__auth_manager = AuthenticationManager()
		settings = inf3995.core.ApplicationManager().get_settings_manager().settings
		self.__auth_manager.load_users(settings["Authentication"]["users_file"])
		self.__add_ip_callbacks = []
		self.__remove_ip_callbacks = []
	
	def get_all_sessions(self):
		return self.__sessions
	
	def register_ip_callbacks(self, add_ip_fn, remove_ip_fn):
		if add_ip_fn is not None:
			self.__add_ip_callbacks.append(add_ip_fn)
		if remove_ip_fn is not None:
			self.__remove_ip_callbacks.append(remove_ip_fn)
	
	def GET(self, *args):
		if len(args) > 1 and args[0] == "config":
			return self.get_config(cherrypy.request, args[1:len(args)])
		else:
			# TODO: Return something when the client asks for "/"? Like a help
			# page or something?
			RestServer._raise_http_error(404)
	
	@cherrypy.tools.accept(media="application/json")
	@cherrypy.tools.json_in()
	def POST(self, *args):
		if len(args) == 2 and args[0] == "users":
			return self.post_users(cherrypy.request, args[1:len(args)])
		else:
			RestServer._raise_http_error(404)
	
	def get_config(self, request, url):
		switch = {
			"basic"       : self.get_config_basic,
			"rockets"     : self.get_config_rockets,
			"map"         : self.get_config_map,
			"miscFiles"   : self.get_config_miscfiles,
			"deviceTypes" : self.get_config_devicetypes
		}
		default = RestServer._raise_error_404
		return switch.get(url[0], default)(request, url[1:len(url)])
	
	def get_config_basic(self, request, url):
		self._check_if_logged_in()
		
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		result = collections.OrderedDict([
			("otherPort", ProgramOptions.get_value("server")),
			("layout"   , ProgramOptions.get_value("rocket")),
			("map"      , ProgramOptions.get_value("map"))
		])
		cherrypy.response.headers["Content-Type"] = "application/json"
		return json.dumps(result, indent=2).encode("utf-8")
	
	def get_config_rockets(self, request, url):
		self._check_if_logged_in()
		
		rockets_dir = RestServer.__ROCKETS_DIR
		if len(url) == 0:
			all_files = RestServer._find_all_files_in_dir(rockets_dir, False)
			
			cherrypy.response.headers["Content-Type"] = "application/json"
			return json.dumps(all_files, indent=2).encode("utf-8")
		else:
			file_path = "/".join([rockets_dir] + list(url))
			if os.path.isfile(file_path):
				cherrypy.lib.static.serve_file(os.getcwd() + "/" + file_path)
			else:
				RestServer._raise_http_error(404)
	
	def get_config_map(self, request, url):
		self._check_if_logged_in()
		
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		result = {
			"map" : ProgramOptions.get_value("map"),
		}
		cherrypy.response.headers["Content-Type"] = "application/json"
		return json.dumps(result, indent=2).encode("utf-8")
	
	def get_config_miscfiles(self, request, url):
		self._check_if_logged_in()
		
		misc_files_dir = RestServer.__MISC_FILES_DIR
		if len(url) == 0:
			all_files = RestServer._find_all_files_in_dir(misc_files_dir, True)
			
			cherrypy.response.headers["Content-Type"] = "application/json"
			return json.dumps(all_files, indent=2).encode("utf-8")
		else:
			file_path = "/".join([misc_files_dir] + list(url))
			if os.path.isfile(file_path):
				cherrypy.lib.static.serve_file(os.getcwd() + "/" + file_path)
			else:
				RestServer._raise_http_error(404)
	
	def get_config_devicetypes(self, request, url):
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		result = {}
		for i, dev in enumerate(RestServer.__POSSIBLE_DEVICES):
			result["deviceType" + str(i + 1)] = dev
		result["unknown"] = RestServer.__UNKNOWN_DEVICE
		cherrypy.response.headers["Content-Type"] = "application/json"
		return json.dumps(result, indent=2).encode("utf-8")
	
	def post_users(self, request, url):
		switch = {
			"login"  : self.post_users_login,
			"logout" : self.post_users_logout,
		}
		default = RestServer._raise_error_404
		return switch.get(url[0], default)(request, url[1:len(url)])
	
	def post_users_login(self, request, url):
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		data = request.json
		if self._is_valid_login_info(data):
			cherrypy.session["logged"] = True
			self.__sessions[cherrypy.session.id] = {}
			session = self.__sessions[cherrypy.session.id]
			session["user"] = data["username"]
			session["logged"] = True
			session["ip"] = request.remote.ip
			session["datetime"] = datetime.datetime.now()
			session["device"] = self._get_device_from_request(request)
			
			for fn in self.__add_ip_callbacks:
				fn(session["ip"])
		else:
			RestServer._raise_http_error(401)
	
	def post_users_logout(self, request, url):
		self._check_if_logged_in()
		
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		data = request.json
		if len(data) == 1 and "username" in data:
			session = self._get_session()
			if "username" in data and data["username"] == session["user"]:
				for fn in self.__remove_ip_callbacks:
					fn(session["ip"])
				
				self.__sessions.pop(cherrypy.session.id)
				cherrypy.session.clear()
				cherrypy.session.delete()
			else:
				RestServer._raise_http_error(401)
		else:
			RestServer._raise_http_error(401)
	
	@staticmethod
	def _raise_http_error(code):
		raise cherrypy.HTTPError(code)
	
	@staticmethod
	def _raise_error_404(*args):
		raise RestServer._raise_http_error(404)
	
	@staticmethod
	def _find_all_files_in_dir(dir_path, add_n_files):
		all_files = []
		foo = os.walk(dir_path)
		for root, subdirs, files in os.walk(dir_path):
			for f in files:
				filename = root + "/" + f
				filename = filename.replace("\\", "/");
				filename = filename.replace(dir_path, "")
				if filename[0] == "/":
					filename = filename[1:len(filename)]
				all_files.append(("file" + str(len(all_files) + 1), filename,))
		
		if add_n_files:
			all_files = [("nFiles", len(all_files),)] + all_files
		return collections.OrderedDict(all_files)
	
	def _check_if_logged_in(self):
		if not self.__skip_auth and self._get_session() is None:
			RestServer._raise_http_error(401)
	
	def _get_session(self):
		return self.__sessions.get(cherrypy.session.id, None)
	
	def _is_valid_login_info(self, data):
		if self.__skip_auth:
			return True
		
		if "username" in data and "password" in data:
			return self.__auth_manager.is_valid_login_info(data["username"],
			                                               data["password"])
		else:
			return False
	
	def _get_device_from_request(self, request):
		data = request.json
		if "device" in data:
			if data["device"].lower() in RestServer.__POSSIBLE_DEVICES:
				return data["device"].lower()
			else:
				return RestServer.__UNKNOWN_DEVICE
		else:
			# TODO: Parse the User-Agent string to try and find the device type.
			return RestServer.__UNKNOWN_DEVICE

