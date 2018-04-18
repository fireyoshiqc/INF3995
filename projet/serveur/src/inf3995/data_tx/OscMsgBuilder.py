"""OscMsgBuilder class"""


import abc
import ctypes
import sys

from pythonosc.parsing import osc_types

from inf3995.data_tx.AbstractOscMsgDataElem import *


class OscMsgBuilder(object):
	__metaclass__ = abc.ABCMeta
	
	def __init__(self, address):
		self.__address = address
		self.__type_tag = ","
		self.__size = len(osc_types.write_string(self.__address)) + \
		              len(osc_types.write_string(self.__type_tag))
		self.__data = {}
	
	@property
	def size(self):
		return self.__size
	
	@property
	def address(self):
		return self.__address
	
	@property
	def type_tag(self):
		return self.__type_tag
	
	@property
	def data(self):
		return self.__data
	
	def set_elem(self, data_elem):
		is_new = data_elem.key not in self.__data
		if is_new:
			new_type_tag = self.__type_tag + data_elem.type_tag
			offset_diff = len(osc_types.write_string(new_type_tag)) - \
			              len(osc_types.write_string(self.__type_tag))
			data_elem.offset = self.__size
			self.__data[data_elem.key] = data_elem
			self.__type_tag = new_type_tag
			self.__size += offset_diff + len(data_elem.encode())
			for key, elem in self.__data.items():
				elem.offset += offset_diff
		else:
			self.__data[data_elem.key].update(data_elem)
		
		return is_new
	
	def write_elem_in_buffer(self, data_elem, buffer):
		elem = self.__data[data_elem.key]
		elem_bytes = elem.encode()
		buffer[elem.offset : elem.offset + len(elem_bytes)] = elem_bytes
	
	def build_msg(self):
		msg_buffer = bytearray(self.__size)
		header_bytes = osc_types.write_string(self.__address) + \
		               osc_types.write_string(self.__type_tag)
		msg_buffer[0 : len(header_bytes)] = header_bytes
		for key, elem in self.__data.items():
			self.write_elem_in_buffer(elem, msg_buffer)
		return msg_buffer

