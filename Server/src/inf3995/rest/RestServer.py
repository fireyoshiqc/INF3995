"""RestServer class"""


import datetime
import json
import os.path
import threading
import time
import mimetypes

import cherrypy

import inf3995.core
from inf3995.core.ProgramOptions import *
from inf3995.rest.AuthenticationManager import *
from inf3995.settings.CANSid import *
from inf3995.settings.ModuleTypes import *


@cherrypy.expose
class RestServer(object):
	__POSSIBLE_DEVICES = ["PC", "Tablet", "Mobile"]
	__UNKNOWN_DEVICE = "N/A"
	__MISC_FILES_DIR = "miscFiles"
	__ROCKETS_DIR = "rockets"
	
	def __init__(self):
		self.__event_logger = inf3995.core.ApplicationManager().get_event_logger()
		self.__sessions = {}
		self.__sessions_mutex = threading.RLock()
		self.__skip_auth = inf3995.core.ProgramOptions.get_value("skip-auth")
		self.__auth_manager = AuthenticationManager()
		settings = inf3995.core.ApplicationManager().get_settings_manager().settings
		self.__auth_manager.load_users(settings["Authentication"]["users_file"])
		self.__users_timeout = float(settings["REST"]["users_timeout_minutes"]) * 60.0
		self.__add_ip_callbacks = []
		self.__remove_ip_callbacks = []
	
	def get_all_sessions(self):
		self.__sessions_mutex.acquire()
		result = dict(self.__sessions)
		self.__sessions_mutex.release()
		return result
	
	def register_ip_callbacks(self, add_ip_fn, remove_ip_fn):
		if add_ip_fn is not None:
			self.__add_ip_callbacks.append(add_ip_fn)
		if remove_ip_fn is not None:
			self.__remove_ip_callbacks.append(remove_ip_fn)
	
	def drop_dead_clients(self):
		self.__sessions_mutex.acquire()
		now = time.monotonic()
		dead_clients = []
		for id, session in self.__sessions.items():
			if now - session["heartbeat"] > self.__users_timeout:
				self.__event_logger.log_info("Client at IP " + session["ip"] + \
				                             " timed out")
				dead_clients.append(id)
		self.__sessions_mutex.release()
		
		for id in dead_clients:
			self._remove_session(id)
	
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
			"basic"           : self.get_config_basic,
			"rockets"         : self.get_config_rockets,
			"map"             : self.get_config_map,
			"miscFiles"       : self.get_config_miscfiles,
			"deviceTypes"     : self.get_config_devicetypes,
			"canSid"          : self.get_config_cansid,
			"canDataTypes"    : self.get_config_candatatypes,
			"canMsgDataTypes" : self.get_config_canmsgdatatypes,
			"canModuleTypes"  : self.get_config_canmoduletypes,
			"timeout"         : self.get_config_timeout
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
				# cherrypy.lib.static.serve_file(os.getcwd() + "/" + file_path)
				# There seems to be some trouble with the file serving by
				# Cherrypy, so we return the content as XML directly.
				content_type = "application/xml; charset=utf-8"
				cherrypy.response.headers["Content-Type"] = content_type
				return open(file_path, "rb").read()
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
			file_path = os.path.join(*([self.__MISC_FILES_DIR] + list(url)))
			file_path = os.path.abspath(os.path.join(os.getcwd(), file_path))
			return self._serve_file_as_download(file_path)
	
	def get_config_devicetypes(self, request, url):
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		result = {}
		for i, dev in enumerate(RestServer.__POSSIBLE_DEVICES):
			result["deviceType" + str(i + 1)] = dev
		result["unknown"] = RestServer.__UNKNOWN_DEVICE
		cherrypy.response.headers["Content-Type"] = "application/json"
		return json.dumps(result, indent=2).encode("utf-8")
	
	def get_config_cansid(self, request, url):
		self._check_if_logged_in()
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		result = collections.OrderedDict([])
		for name, member in CANSid.__members__.items():
			result[name] = member.value
		
		cherrypy.response.headers["Content-Type"] = "application/json"
		return json.dumps(result, indent=2).encode("utf-8")
	
	def get_config_candatatypes(self, request, url):
		self._check_if_logged_in()
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		result = collections.OrderedDict([])
		for name, member in CANDataType.__members__.items():
			result[name] = member.value
		
		cherrypy.response.headers["Content-Type"] = "application/json"
		return json.dumps(result, indent=2).encode("utf-8")
	
	def get_config_canmsgdatatypes(self, request, url):
		self._check_if_logged_in()
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		result = collections.OrderedDict([])
		for name, member in CANSid.__members__.items():
			result[name] = (CANMsgDataTypes[member][0].name,
			                CANMsgDataTypes[member][1].name)
		
		cherrypy.response.headers["Content-Type"] = "application/json"
		return json.dumps(result, indent=2).encode("utf-8")
	
	def get_config_canmoduletypes(self, request, url):
		self._check_if_logged_in()
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		result = collections.OrderedDict([])
		for name, member in ModuleType.__members__.items():
			result[name] = member.value
		
		cherrypy.response.headers["Content-Type"] = "application/json"
		return json.dumps(result, indent=2).encode("utf-8")
	
	def get_config_timeout(self, request, url):
		self._check_if_logged_in()
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		result = {"timeoutMinutes" : self.__users_timeout / 60.0}
		cherrypy.response.headers["Content-Type"] = "application/json"
		return json.dumps(result, indent=2).encode("utf-8")
	
	def post_users(self, request, url):
		switch = {
			"login"     : self.post_users_login,
			"logout"    : self.post_users_logout,
			"heartbeat" : self.post_users_heartbeat
		}
		default = RestServer._raise_error_404
		return switch.get(url[0], default)(request, url[1:len(url)])
	
	def post_users_login(self, request, url):
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		data = request.json
		if self._is_valid_login_info(data):
			self.__sessions_mutex.acquire()
			
			cherrypy.session["logged"] = True
			self.__sessions[cherrypy.session.id] = {}
			session = self.__sessions[cherrypy.session.id]
			session["user"] = data["username"]
			session["logged"] = True
			session["ip"] = request.remote.ip
			session["datetime"] = datetime.datetime.now()
			session["device"] = self._get_device_from_request(request)
			session["heartbeat"] = time.monotonic()
			
			self.__event_logger.log_info("User login : '" + session["user"] + "'" + \
			                             " at IP " + session["ip"])
			
			for fn in self.__add_ip_callbacks:
				fn(session["ip"])
			
			self.__sessions_mutex.release()
		else:
			RestServer._raise_http_error(401)
	
	def post_users_logout(self, request, url):
		self._check_if_logged_in()
		
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		data = request.json
		if "username" in data:
			session = self._get_session()
			can_delete = session is not None and \
			             (self.__skip_auth or data["username"] == session["user"])
			if can_delete:
				self._remove_session(cherrypy.session.id)
			else:
				RestServer._raise_http_error(401)
		else:
			RestServer._raise_http_error(400)
	
	def post_users_heartbeat(self, request, url):
		self._check_if_logged_in()
		
		if len(url) != 0:
			RestServer._raise_http_error(404)
		
		self.__sessions_mutex.acquire()
		session = self._get_session()
		session["heartbeat"] = time.monotonic()
		self.__sessions_mutex.release()
	
	@staticmethod
	def _raise_http_error(code):
		raise cherrypy.HTTPError(code)
	
	@staticmethod
	def _raise_error_404(*args):
		raise RestServer._raise_http_error(404)
	
	@staticmethod
	def _find_all_files_in_dir(dir_path, add_n_files):
		all_files = []
		for root, subdirs, files in os.walk(dir_path):
			for f in files:
				filename = root + "/" + f
				filename = filename.replace("\\", "/")
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
			return data["device"]
		else:
			# TODO: Parse the User-Agent string to try and find the device type.
			return RestServer.__UNKNOWN_DEVICE

	def _remove_session(self, id):
		session = self.__sessions.get(id, None)
		if session is None:
			return
		self.__event_logger.log_info("User logout : '" + session["user"] + "'")
		for fn in self.__remove_ip_callbacks:
			fn(session["ip"])
		
		self.__sessions_mutex.acquire()
		self.__sessions.pop(id)
		self.__sessions_mutex.release()

	def _serve_file_as_download(self, file_path):
		if os.path.isfile(file_path):
			file_name = os.path.basename(file_path)
			file_ext = os.path.splitext(file_name)[1]
			default_type = "application/octet-stream"
			content_type = mimetypes.types_map.get(file_ext, default_type)
			file_content = open(file_path, "rb").read()
			cherrypy.response.headers["Content-Type"] = content_type
			cherrypy.response.headers["Content-Disposition"] = "attachment; filename=\"%s\"" % os.path.basename(file_path)
			return file_content
		else:
			RestServer._raise_http_error(404)

