"""OscMsgData class and associated functions"""

import ctypes

from pythonosc.parsing import osc_types

from inf3995.settings.CANSid import *


class CanDataElem(object):
	def __init__(self, can_data, offset):
		self.offset = offset
		self.type1 = get_osc_type_from_can_type(CANMsgDataTypes[can_data.sid][0])
		self.data1 = can_data.data1
		self.type2 = get_osc_type_from_can_type(CANMsgDataTypes[can_data.sid][1])
		self.data2 = can_data.data2
		self.src_type = can_data.src_type
		self.src_no = can_data.src_serial
		self.counter = 0


class OscMsgData(object):
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
	
	def update_elem(self, can_data):
		is_new = not can_data.sid in self.__data
		if is_new:
			new_elem = CanDataElem(can_data, self.__size)
			new_type_tag = self.__type_tag + "i" + new_elem.type1 + \
			               new_elem.type2 + "iii"
			offset_diff = len(osc_types.write_string(new_type_tag)) - \
			              len(osc_types.write_string(self.__type_tag))
			self.__data[can_data.sid] = new_elem
			self.__type_tag = new_type_tag
			self.__size += offset_diff + 6 * 4
			for id in self.__data:
				self.__data[id].offset += offset_diff
		else:
			self.__data[can_data.sid].data1 = can_data.data1
			self.__data[can_data.sid].data2 = can_data.data2
			self.__data[can_data.sid].src_type = can_data.src_type
			self.__data[can_data.sid].src_no = can_data.src_serial
			self.__data[can_data.sid].counter += 1
		
		return is_new
	
	def write_elem_in_buffer(self, sid, buffer):
		elem = self.__data[sid]
		elem_bytes = self.__encode_data("i", int(sid)) + \
		             self.__encode_data(elem.type1, elem.data1) + \
		             self.__encode_data(elem.type2, elem.data2) + \
		             self.__encode_data("i", int(elem.src_type)) + \
		             self.__encode_data("i", int(elem.src_no)) + \
		             self.__encode_data("i", int(elem.counter))
		
		buffer[elem.offset : elem.offset + len(elem_bytes)] = elem_bytes
	
	def build_msg(self):
		msg_buffer = bytearray(self.__size)
		header_bytes = osc_types.write_string(self.__address) + \
		               osc_types.write_string(self.__type_tag)
		msg_buffer[0 : len(header_bytes)] = header_bytes
		for id in self.__data:
			self.write_elem_in_buffer(id, msg_buffer)
		return msg_buffer
	
	def __encode_data(self, type, data):
		if type == "i":
			value = ctypes.c_int32(ctypes.c_uint32(int(data)).value).value
			return osc_types.write_int(value)
		elif type == "f":
			return osc_types.write_float(float(data))
		else:
			raise RuntimeError("Unknown OSC type")


def get_osc_type_from_can_type(can_type):
	TYPE_ASSOCIATIONS = {CANDataType.UNKNOWN   : "",
	                     CANDataType.INT       : "i",
	                     CANDataType.FLOAT     : "f",
	                     CANDataType.UNSIGNED  : "i",
	                     CANDataType.TIMESTAMP : "i",
	                     CANDataType.MAGIC     : "i",
	                     CANDataType.NONE      : "i"}
	
	return TYPE_ASSOCIATIONS.get(can_type, "")