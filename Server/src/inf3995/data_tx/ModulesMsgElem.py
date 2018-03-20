"""ModulesMsgElem class"""

import ctypes
import abc

from pythonosc.parsing import osc_types

from inf3995.data_tx.AbstractOscMsgDataElem import *


class ModulesMsgElem(AbstractOscMsgDataElem):
	def __init__(self, can_rx_data):
		self.module_type = can_rx_data.src_type
		self.module_serial_no = can_rx_data.src_serial
		self.counter = 0
	
	@property
	def key(self):
		return self.module_type
	
	@property
	def type_tag(self):
		return "iii"
	
	def encode(self):
		return encode_osc_data("i", int(self.module_type)) + \
		       encode_osc_data("i", int(self.module_serial_no)) + \
		       encode_osc_data("i", int(self.counter))
	
	def update(self, other):
		self.counter += 1

