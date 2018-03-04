"""OscSender class"""


import socket

from inf3995.data_tx.OscMsgData import *


class OscSender(object):
	def __init__(self, osc_address, udp_port):
		self.__msg_data = OscMsgData(osc_address)
		self.__socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		self.__socket.setblocking(0)
		self.__udp_port = udp_port
		self.__targets = {}
		self.__msg_buffer = self.__msg_data.build_msg()
	
	def add_socket(self, ipv4_address):
		if ipv4_address not in self.__targets:
			self.__targets[ipv4_address] = None
			print("Added socket : " + ipv4_address)
	
	def remove_socket(self, ipv4_address):
		del self.__targets[ipv4_address]
		print("Removed socket : " + ipv4_address)
	
	def update_value(self, can_value):
		is_new = self.__msg_data.update_elem(can_value)
		if is_new:
			self.__msg_buffer = self.__msg_data.build_msg()
		else:
			self.__msg_data.write_elem_in_buffer(can_value.sid,
			                                     self.__msg_buffer)
	
	def send_message(self):
		for target in self.__targets:
			self.__socket.sendto(self.__msg_buffer,
			                     (target, self.__udp_port))

