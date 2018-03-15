"""OscSender class"""


import socket
import sys

import inf3995.core
from inf3995.data_tx.OscMsgBuilder import *
from inf3995.data_tx.CanDataMsgElem import *
from inf3995.data_tx.ModulesMsgElem import *


class OscSender(object):
	def __init__(self, can_data_osc_address, modules_osc_address, udp_port):
		self.__event_logger = inf3995.core.ApplicationManager().get_event_logger()
		self.__socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		self.__socket.setblocking(False)
		self.__udp_port = udp_port
		self.__targets = {}
		self.__msg_builders = [OscMsgBuilder(can_data_osc_address),
		                       OscMsgBuilder(modules_osc_address)]
		self.__msg_elem_types = [CanDataMsgElem, ModulesMsgElem]
		self.__msg_buffers = [self.__msg_builders[0].build_msg(),
		                      self.__msg_builders[1].build_msg()]
	
	def add_socket(self, ipv4_address):
		if ipv4_address in self.__targets:
			self.__targets[ipv4_address] += 1
		else:
			self.__targets[ipv4_address] = 1
			self.__event_logger.log_info("Added UDP socket : " + ipv4_address)
	
	def remove_socket(self, ipv4_address):
		if ipv4_address in self.__targets:
			self.__targets[ipv4_address] -= 1
			if self.__targets[ipv4_address] == 0:
				del self.__targets[ipv4_address]
	
	def update_value(self, can_rx_data_elem):
		for i in range(0, len(self.__msg_builders)):
			msg_builder = self.__msg_builders[i]
			ElemType = self.__msg_elem_types[i]
			msg_buffer = self.__msg_buffers[i]
			
			data_elem = ElemType(can_rx_data_elem)
			is_new = msg_builder.set_elem(data_elem)
			if is_new:
				self.__msg_buffers[i] = msg_builder.build_msg()
			else:
				msg_builder.write_elem_in_buffer(data_elem, self.__msg_buffers[i])
	
	def send_message(self):
		for buf in self.__msg_buffers:
			for target in self.__targets:
				self.__socket.sendto(buf, (target, self.__udp_port))

