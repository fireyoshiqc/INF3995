"""CanDataMsgElem class and associated functions and types"""

import ctypes
import abc

from pythonosc.parsing import osc_types

from inf3995.data_tx.AbstractOscMsgDataElem import *
from inf3995.settings.CANSid import *


class CanDataMsgElem(AbstractOscMsgDataElem):
	def __init__(self, can_rx_data):
		self.can_sid = can_rx_data.sid
		self.type1 = get_osc_type_from_can_type(CANMsgDataTypes[self.can_sid][0])
		self.data1 = can_rx_data.data1
		self.type2 = get_osc_type_from_can_type(CANMsgDataTypes[self.can_sid][1])
		self.data2 = can_rx_data.data2
		self.src_type = can_rx_data.src_type
		self.src_serial_no = can_rx_data.src_serial
		self.counter = 0
	
	@property
	def key(self):
		return self.can_sid
	
	@property
	def type_tag(self):
		return "i" + self.type1 + self.type2 + "iii"
	
	def encode(self):
		return encode_osc_data("i", int(self.can_sid.value)) + \
		       encode_osc_data(self.type1, self.data1) + \
		       encode_osc_data(self.type2, self.data2) + \
		       encode_osc_data("i", int(self.src_type)) + \
		       encode_osc_data("i", int(self.src_serial_no)) + \
		       encode_osc_data("i", int(self.counter))
	
	def update(self, other):
		self.data1 = other.data1
		self.data2 = other.data2
		self.src_type = other.src_type
		self.src_serial_no = other.src_serial_no
		self.counter += 1


def get_osc_type_from_can_type(can_type):
	TYPE_ASSOCIATIONS = {CANDataType.UNKNOWN   : "",
	                     CANDataType.INT       : "i",
	                     CANDataType.FLOAT     : "f",
	                     CANDataType.UNSIGNED  : "i",
	                     CANDataType.TIMESTAMP : "i",
	                     CANDataType.MAGIC     : "i",
	                     CANDataType.NONE      : "i"}
	
	return TYPE_ASSOCIATIONS.get(can_type, "")
