"""ModulesMsgElem class"""

import ctypes
import abc

from pythonosc.parsing import osc_types

from inf3995.data_tx.AbstractOscMsgDataElem import *


class ModulesMsgElem(AbstractOscMsgDataElem):
	def __init__(self, can_rx_data):
		self.module_type = can_rx_data.src_type
		self.module_serial_no = can_rx_data.src_serial
		self.identifier = (self.module_type << 16) | self.module_serial_no
		self.counter = 0
	
	@property
	def key(self):
		return self.identifier
	
	@property
	def type_tag(self):
		return "ii"
	
	def encode(self):
		return encode_osc_data("i", int(self.identifier)) + \
		       encode_osc_data("i", int(self.counter))
	
	def update(self, other):
		self.counter += 1

