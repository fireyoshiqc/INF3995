"""SettingsManager class"""


import configparser


class SettingsManager(object):
	def __init__(self):
		__settings = {}
	
	@property
	def settings(self):
		return self.__settings
	
	@settings.setter
	def settings(self, settings):
		self.__settings = settings
	
	def load_settings_from_file(self, file_name):
		parser = configparser.ConfigParser()
		parser.read_file(open(file_name, "r"))
		self.__settings = dict(parser)
	
	def save_settings_to_file(self, file_name):
		parser = configparser.ConfigParser()
		parser.read_dict(self.__settings)
		parser.write(open(file_name, "w"))

