"""NamedPipe class"""

import platform
import sys
import os
import enum

if platform.system() == "Windows":
	import wpipe


@enum.unique
class PipeMode(enum.IntEnum):
	WRITER = 1,
	READER = 2;


class SimpleNamedPipeServer(object):
	def __init__(self, name, mode):
		self.__pipe = None
		self.__mode = mode
		if mode == PipeMode.WRITER:
			self.__open_writer_pipe(name)
		elif mode == PipeMode.READER:
			self.__open_reader_pipe(name)
		else:
			raise RuntimeError("Unknown pipe open mode")
	
	def write(self, data):
		if self.__mode == PipeMode.READER:
			raise RuntimeError("Cannot write to reader pipe")
		
		self.__write_to_clients(data)
	
	def read(self):
		if self.__mode == PipeMode.READER:
			raise RuntimeError("Cannot read from writer pipe")
		
		return self.__read_from_clients()
	
	def close(self):
		self.__close_pipe()
	
	if platform.system() == "Windows":
		def __open_writer_pipe(self, name):
			self.__pipe = wpipe.Server(name, wpipe.Mode.Writer)
		
		def __open_reader_pipe(self, name):
			self.__pipe = wpipe.Server(name, wpipe.Mode.Reader)
		
		def __write_to_clients(self, data):
			for client in self.__pipe:
				client.write(data)
		
		def __read_from_clients(self):
			result = []
			for client in self.__pipe:
				if client.canread():
					result.append(client.read())
				else:
					result.append(None)
		
		def __close_pipe(self):
			self.__pipe.shutdown()
	else:
		def __open_writer_pipe(self, name):
			raise NotImplementedError()
		
		def __open_reader_pipe(self, name):
			raise NotImplementedError()
		
		def __write_to_clients(self, name):
			raise NotImplementedError()
		
		def __read_from_clients(self):
			raise NotImplementedError()
		
		def __close_pipe(self):
			raise NotImplementedError()


class SimpleNamedPipeClient(object):
	def __init__(self, name, mode):
		self.__pipe = None
		self.__mode = mode
		if mode == PipeMode.WRITER:
			self.__open_writer_pipe(name)
		elif mode == PipeMode.READER:
			self.__open_reader_pipe(name)
		else:
			raise RuntimeError("Unknown pipe open mode")
	
	def write(self, data):
		if self.__mode == PipeMode.READER:
			raise RuntimeError("Cannot write to reader pipe")
		
		self.__write(data)
	
	def read(self):
		if self.__mode == PipeMode.WRITER:
			raise RuntimeError("Cannot read from writer pipe")
		
		return self.__read()
	
	if platform.system() == "Windows":
		def __open_writer_pipe(self, name):
			self.__pipe = wpipe.Client(name, wpipe.Mode.Writer)
		
		def __open_reader_pipe(self, name):
			self.__pipe = wpipe.Client(name, wpipe.Mode.Reader)
		
		def __write(self, data):
			self.__pipe.write(data)
		
		def __read(self):
			return self.__pipe.read()
	else:
		def __open_writer_pipe(self, name):
			raise NotImplementedError()
		
		def __open_reader_pipe(self, name):
			raise NotImplementedError()
		
		def __write_to_clients(self, name):
			raise NotImplementedError()
		
		def __read_from_clients(self):
			raise NotImplementedError()
	

