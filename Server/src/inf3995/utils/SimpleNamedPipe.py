"""NamedPipe class"""

import platform
import sys
import enum

if platform.system() == "Windows":
	import wpipe
else:
	import os
	import errno


@enum.unique
class PipeMode(enum.IntEnum):
	WRITER = 1,
	READER = 2;


class SimpleNamedPipeServer(object):
	def __init__(self, name, mode):
		self.__pipe = None
		self.__mode = mode
		self.__name = name
		if mode == PipeMode.WRITER:
			self.__open_writer_pipe()
		elif mode == PipeMode.READER:
			self.__open_reader_pipe()
		else:
			raise RuntimeError("Unknown pipe open mode")
	
	def write(self, data):
		if self.__mode == PipeMode.READER:
			raise RuntimeError("Cannot write to reader pipe")
		
		self.__write_to_clients(data)
	
	def read(self):
		if self.__mode == PipeMode.WRITER:
			raise RuntimeError("Cannot read from writer pipe")
		
		return self.__read_from_clients()
	
	def close(self):
		self.__close_pipe()
	
	if platform.system() == "Windows":
		def __open_writer_pipe(self):
			self.__pipe = wpipe.Server(self.__name, wpipe.Mode.Writer)
		
		def __open_reader_pipe(self):
			self.__pipe = wpipe.Server(self.__name, wpipe.Mode.Reader)
		
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
		def __open_writer_pipe(self):
			self.__unlink_pipe()
			os.mkfifo(self.__name, 0o666)
			self.__pipe = os.open(self.__name, os.O_WRONLY)
		
		def __open_reader_pipe(self):
			self.__unlink_pipe()
			os.mkfifo(self.__name, 0o666)
			self.__pipe = os.open(self.__name, os.O_RDONLY | os.O_NONBLOCK)
		
		def __write_to_clients(self, data):
			os.write(self.__pipe, data)
		
		def __read_from_clients(self):
			try:
				msg = os.read(self.__pipe, 4096)
				if len(msg) == 0:
					return [None]
				return [msg]
			except OSError as e:
				if e.errno == errno.EAGAIN or e.errno == errno.EWOULDBLOCK:
					return [None]
				else:
					raise
			except:
				raise
		
		def __close_pipe(self):
			os.close(self.__pipe)
			self.__unlink_pipe()
		
		def __unlink_pipe(self):
			try:
				os.unlink(self.__name)
			except FileNotFoundError:
				pass
			except:
				raise



class SimpleNamedPipeClient(object):
	def __init__(self, name, mode):
		self.__pipe = None
		self.__mode = mode
		self.__name = name
		if mode == PipeMode.WRITER:
			self.__open_writer_pipe()
		elif mode == PipeMode.READER:
			self.__open_reader_pipe()
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
		def __open_writer_pipe(self):
			self.__pipe = wpipe.Client(self.__name, wpipe.Mode.Writer)
		
		def __open_reader_pipe(self):
			self.__pipe = wpipe.Client(self.__name, wpipe.Mode.Reader)
		
		def __write(self, data):
			self.__pipe.write(data)
		
		def __read(self):
			return self.__pipe.read()
	else:
		def __open_writer_pipe(self):
			self.__pipe = os.open(self.__name, os.O_WRONLY)
		
		def __open_reader_pipe(self):
			self.__pipe = os.open(self.__name, os.O_RDONLY | os.O_NONBLOCK)
		
		def __write(self, data):
			os.write(self.__pipe, data)
		
		def __read(self):
			try:
				msg = os.read(self.__pipe, 4096)
				if len(msg) == 0:
					raise RuntimeError("Pipe closed")
				return [msg]
			except OSError as e:
				if e.errno == errno.EAGAIN or e.errno == errno.EWOULDBLOCK:
					return [None]
				else:
					raise
			except:
				raise
	

