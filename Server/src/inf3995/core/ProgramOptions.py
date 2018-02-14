"""ProgramOptions class"""


import argparse


class ProgramOptions(object):
	__parser = None
	__options = None
	
	@staticmethod
	def configure_and_parse(argv):
		desc = "Server program to communicate between an Oronos " \
		       "rocket and multiple PC/tablet clients."
		
		epi = "Copyright team INF3995-03. All rights reserved."
		
		ProgramOptions.__parser = argparse.ArgumentParser(description=desc,
		                                                   epilog=epi)
		
		ProgramOptions.__add_baudrate_arg()
		ProgramOptions.__add_connector_type_arg()
		ProgramOptions.__add_connector_file_arg()
		ProgramOptions.__add_port_arg()
		ProgramOptions.__add_rocket_arg()
		ProgramOptions.__add_map_arg()
		ProgramOptions.__add_run_tests_arg()
		
		args = ProgramOptions.__parser.parse_args(argv[1:len(argv)])
		ProgramOptions.__options = vars(args)
	
	@staticmethod
	def get_value(key):
		value = ProgramOptions.__options.get(key, None)
		if value is not None and isinstance(value, list) and len(value) == 1:
			return value[0]
		else:
			return value
	
	@staticmethod
	def set_value(key, value):
		ProgramOptions.__options[key] = value
	
	@staticmethod
	def __add_baudrate_arg():
		parser = ProgramOptions.__parser
		description = "Serial port baudrate (serial connector only)."
		parser.add_argument("-b", "--baudrate",
		                    dest="baudrate",
		                    action="store", nargs=1,
		                    type=int, metavar="BAUDRATE",
		                    default=None,
		                    help=description)
	
	@staticmethod
	def __add_connector_type_arg():
		parser = ProgramOptions.__parser
		description = "Source of data and type of execution. " \
		              "(default : simulation)"
		parser.add_argument("-c", "--connector-type",
		                    dest="connector-type",
		                    action="store", nargs=1,
		                    type=str,
		                    default="simulation",
		                    choices=["serial", "simulation", "emulation"],
		                    help=description)
	
	@staticmethod
	def __add_connector_file_arg():
		parser = ProgramOptions.__parser
		description = "Argument for the type of connector, i.e. an input COM " \
		              "port in serial mode, a CSV file in simulation, or an " \
		              "output COM port and a CSV file in emulation."
		parser.add_argument("-f", "--connector-file",
		                    dest="connector-file",
		                    action="store", nargs=1,
		                    type=str, metavar="CONNECTOR_FILE",
		                    default=None,
		                    help=description)
	
	@staticmethod
	def __add_port_arg():
		parser = ProgramOptions.__parser
		description = "UDP server port on which flight data will be sent to " \
		              "possibly multiple ground station. " \
		              "(default : 3000)"
		parser.add_argument("-s", "--server",
		                    dest="server",
		                    action="store", nargs="?",
		                    type=int, metavar="PORT",
		                    default=3000,
		                    help=description)
	
	@staticmethod
	def __add_rocket_arg():
		parser = ProgramOptions.__parser
		description = "The XML file containing the rocket configuration. " \
		              "E.g.: 10_polaris.xml " \
		              "(default : 11_valkyrieM2.xml)"
		parser.add_argument("-r", "--rocket",
		                    dest="rocket",
		                    action="store", nargs=1,
		                    type=str, metavar="ROCKET",
		                    default="11_valkyrieM2.xml",
		                    help=description)
	
	@staticmethod
	def __add_map_arg():
		parser = ProgramOptions.__parser
		description = "The name of the map in /Configs/Other/Maps.xml. " \
		              "E.g.: motel_6. " \
		              "(default : spaceport_america)"
		parser.add_argument("-m", "--map",
		                    dest="map",
		                    action="store", nargs=1,
		                    type=str, metavar="MAP",
		                    default="spaceport_america",
		                    help=description)
	
	@staticmethod
	def __add_run_tests_arg():
		parser = ProgramOptions.__parser
		description = "Run unit tests instead of server app."
		parser.add_argument("-t", "--run-tests",
		                    dest="run-tests",
		                    action="store_true",
		                    default=False,
		                    help=description)

