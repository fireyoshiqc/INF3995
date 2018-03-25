"""OscSender class"""


import socket
import sys
import threading

import inf3995.core
from inf3995.data_tx.OscMsgBuilder import *
from inf3995.data_tx.CanDataMsgElem import *
from inf3995.data_tx.ModulesMsgElem import *


class OscSender(object):
	def __init__(self, can_data_osc_address, modules_osc_address, udp_port):
		self.__event_logger = inf3995.core.ApplicationManager().get_event_logger()
		self.__socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		self.__socket.setblocking(True)
		self.__udp_port = udp_port
		self.__targets = {}
		self.__targets_mutex = threading.Lock()
		self.__msg_builders = [OscMsgBuilder(can_data_osc_address),
		                       OscMsgBuilder(modules_osc_address)]
		self.__msg_elem_types = [CanDataMsgElem, ModulesMsgElem]
		self.__msg_buffers = [self.__msg_builders[0].build_msg(),
		                      self.__msg_builders[1].build_msg()]
	
	def add_socket(self, ipv4_address):
		if ipv4_address in self.__targets:
			self.__targets[ipv4_address] += 1
		else:
			self.__add_new_target(ipv4_address)
			self.__event_logger.log_info("Added UDP socket : " + ipv4_address)
	
	def remove_socket(self, ipv4_address):
		if ipv4_address in self.__targets:
			self.__targets[ipv4_address] -= 1
			if self.__targets[ipv4_address] == 0:
				self.__remove_target(ipv4_address)
				self.__event_logger.log_info("Removed UDP socket : " + ipv4_address)
	
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
		self.__targets_mutex.acquire()
		for buf in self.__msg_buffers:
			for target in self.__targets:
				try:
					self.__socket.sendto(buf, (target, self.__udp_port))
				except BlockingIOError:
					self.__event_logger.log_error("A UDP packet could not be sent to " + target)
		self.__targets_mutex.release()
	
	def __add_new_target(self, ipv4_address):
		self.__targets_mutex.acquire()
		self.__targets[ipv4_address] = 1
		self.__targets_mutex.release()
	
	def __remove_target(self, ipv4_address):
		self.__targets_mutex.acquire()
		del self.__targets[ipv4_address]
		self.__targets_mutex.release()

