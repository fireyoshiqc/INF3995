"""AbstractOscMsgDataElem class and associated functions"""


import abc
import ctypes

from pythonosc.parsing import osc_types


class AbstractOscMsgDataElem(object):
	__metaclass__ = abc.ABCMeta
	def __init__(self, offset = 0):
		self.offset = offset
	
	@property
	@abc.abstractmethod
	def key(self):
		pass
	
	@property
	@abc.abstractmethod
	def type_tag(self):
		pass
	
	@abc.abstractmethod
	def encode(self):
		pass
	
	@abc.abstractmethod
	def update(self, other):
		pass


def encode_osc_data(type, data):
	if type == "i":
		value = ctypes.c_int32(ctypes.c_uint32(int(data)).value).value
		return osc_types.write_int(value)
	elif type == "f":
		return osc_types.write_float(float(data))
	elif type == "s":
		return osc_types.write_string(data)
	elif type == "b":
		return osc_types.write_blob(data)
	else:
		raise RuntimeError("Unknown OSC type")

