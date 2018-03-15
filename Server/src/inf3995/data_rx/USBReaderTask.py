"""USB Reader class"""


#import serial  # This library isn't available in this commit
import base64
from struct import unpack
from Bitfield import Bitfield
from enum import Enum
# For debugging: Uncomment if using the Oronos version for comparison
#from binascii import hexlify
#TODO: Check if it's ok that we use this CRC calculation
from binascii import crc32

from inf3995.core.AbstractTaskNode import *
from inf3995.data_rx.RxData import RxData
from inf3995.settings.CANSidParser import *
#import inf3995.core

class _DataType(Enum):
	INT = 1  # Start at 1 because 0 is False in a boolean sense
	FLOAT = 2
	UNSIGNED = 3
	TIMESTAMP = 4
	MAGIC = 5
	NONE = 6

class USBReaderTask(AbstractTaskNode):
	def __init__(self, serial_port, baudrate):
		super(USBReaderTask, self).__init__(is_queued_input_data = False,
											buffer_size = 1024)
		# self.stream = serial.Serial(serial_port, baudrate,
		# 							timeout=0,
		# 							stopbits=serial.STOPBITS_ONE,
		# 							parity=serial.PARITY_NONE)
		# TODO Fix crash when connector file doesn't exist
		# try:
		# 	self.stream = serial.Serial(serial_port, baudrate,
		# 							timeout=0,
		# 							stopbits=serial.STOPBITS_ONE,
		# 							parity=serial.PARITY_NONE)
		# except serial.serialutil.SerialException as e:
		# 	print(__name__ + ': ' + str(e))
		# 	inf3995.core.ApplicationManager().exit(0)
		# 	return

	def init(self):
		pass

	def on_first_run(self):
		pass

	def handle_data(self):
		#s = self.stream.read(100)
		# Will the messages always be the same length?
		# Can I read a certain length instead of using readline()?
		#s = self.stream.readline()

		with open('flight_logs/test_flight_logs/polaris_data_encoded.txt', 'r', encoding='utf-8') as file:
			for line in file:
			# For debugging: Reducing the flow of data. Indent the
			# code block if uncommented
			#for idx, line in enumerate(file):
				#if idx == 0 or idx == 1:
				#print(idx, line)

				msg_decoded = base64.b64decode(line)
				msg_int = int.from_bytes(msg_decoded, byteorder='little', signed=False) # Represent serialized message as an integer **from little endian
				msg_bitfield = Bitfield(msg_int)
				# For debugging: Print entire decoded msg in binary
				#print(bin(msg_bitfield))

				# Discard corrupted messages (Check CRC validity)
				decoded_crc32 = int(msg_bitfield[96:128])
				calculated_crc32 = crc32(msg_decoded[0:12])
				# For debugging: Print calculated and decoded CRCs in
				# binary and then as integers
				#print(bin(int.from_bytes(msg_decoded[12:16], byteorder='little', signed=False)), bin(int(msg_bitfield[96:128])))
				#print(decoded_crc32, calculated_crc32)
				if decoded_crc32 != calculated_crc32:
					#TODO: When it's time to get rid of the for replace all the continues with returns
					continue

				# Discard all messages with invalid CAN serial ID
				try:
					sid = int(msg_bitfield[0:11])
					can_sid_info = CANSidParser.can_sid_info[sid]
				except KeyError as e:
					print(__name__ + ': KeyError: ' + str(e))
					continue

				#TODO: Check if dest_type in ModuleTypes?
				dest_serial = int(msg_bitfield[11:15])
				dest_type = int(msg_bitfield[15:20])
				src_serial = int(msg_bitfield[20:24])
				src_type = int(msg_bitfield[24:29])
				# For debugging: Print fields in binary and then as
				# integers
				#print(bin(sid), bin(dest_serial), bin(dest_type), bin(src_serial), bin(src_type))
				#print(sid, dest_serial, dest_type, src_serial, src_type)

				#TODO: Make function that returns casting type (int, float, unsigned?)
				# What do with the types that only use part of the room (there is room for 2 32bit numbers)
				# Parse data 1
				# Les 3 bits inutilisés ne sont pas traités
				data1_type = can_sid_info['data1_type']
				if _DataType[data1_type] == _DataType.INT:
					data1 = int(msg_bitfield[32:64])
				elif _DataType[data1_type] == _DataType.FLOAT:
					#data1 = float(msg_bitfield[32:64]) # Produces a different result for some reason
					data1 = unpack('f', msg_decoded[4:8])[0]
				elif _DataType[data1_type] == _DataType.UNSIGNED:
					# TODO: Make sure is interpreted as unsigned?
					data1 = int(msg_bitfield[32:64])
				elif _DataType[data1_type] == _DataType.TIMESTAMP:
					# TODO: Make sure is interpreted as unsigned?
					data1 = int(msg_bitfield[32:64])
				elif _DataType[data1_type] == _DataType.MAGIC:
					# TODO: Special traitement because it's in hex?
					data1 = int(msg_bitfield[32:64])
				else:
					# Unrecognized data type
					continue

				# Parse data 2
				# All data types possible in second data field?
				data2_type = can_sid_info['data2_type']
				if _DataType[data2_type] == _DataType.INT:
					data2 = int(msg_bitfield[64:96])
				elif _DataType[data2_type] == _DataType.FLOAT:
					#data2 = float(msg_bitfield[64:96])  # Produces a different result for some reason
					data2 = unpack('f', msg_decoded[8:12])[0]
				elif _DataType[data2_type] == _DataType.UNSIGNED:
					# TODO: Make sure is interpreted as unsigned?
					data2 = int(msg_bitfield[64:96])
				elif _DataType[data2_type] == _DataType.TIMESTAMP:
					data2 = int(msg_bitfield[64:96])
				elif _DataType[data2_type] == _DataType.MAGIC:
					# TODO: Special traitement because it's in hex?
					data2 = int(msg_bitfield[64:96])
				elif _DataType[data2_type] == _DataType.NONE:
					data2 = int(msg_bitfield[64:96])
				else:
					# Unrecognized data type
					continue

				# For debugging: Print data1 & data2 in binary and then
				# as integers
				#print(bin(msg_bitfield[32:64]), bin(msg_bitfield[64:97]))
				#print(data1, data2)

				data = RxData(sid=sid,
							  src_type=src_type,
							  src_serial=src_serial,
							  dest_type=dest_type,
							  dest_serial=dest_serial,
							  data1=data1,
							  data2=data2)
				self._produce_data(data)

				# # Oronos code for result comparison
				# decodedSID = ((msg_decoded[1] & 0b00000111) << 8) | msg_decoded[0]
				# decodedDestSerial = (msg_decoded[1] & 0b01111000) >> 3
				# decodedDestID = ((msg_decoded[2] & 0b00001111) << 1) | ((msg_decoded[1] & 0b10000000) >> 7)
				# decodedSrcSerial = (msg_decoded[2] & 0b11110000) >> 4
				# decodedSrcID = msg_decoded[3] & 0b00011111
				# print(decodedSID, decodedDestSerial, decodedDestID, decodedSrcSerial, decodedSrcID)
				#
				# # Interpréter correctement le data1
				# if _DataType[data1_type] == _DataType.FLOAT:
				# 	decodedData1 = unpack('f', msg_decoded[4:8])[0]
				# else:
				# 	decodedData1 = unpack('I', msg_decoded[4:8])[0]
				#
				# # Interpréter correctement le data2
				# if _DataType[data2_type] == _DataType.FLOAT:
				# 	decodedData2 = unpack('f', msg_decoded[8:12])[0]
				# else:
				# 	decodedData2 = unpack('I', msg_decoded[8:12])[0]
				#
				# #print(msg_decoded[4:8], msg_decoded[8:12])
				# #print(hexlify(msg_decoded[4:8]), hexlify(msg_decoded[8:12]))
				# print(bin(int.from_bytes(msg_decoded[4:8], byteorder='little', signed=False)),
				# 	  bin(int.from_bytes(msg_decoded[8:12], byteorder='little', signed=False)))
				# print(decodedData1, decodedData2)

	def cleanup(self):
		print('USBReaderTask: Cleanup')
		# TODO Don't try and close stream that was never opened?
		self.stream.close()
