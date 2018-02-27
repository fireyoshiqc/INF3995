"""Rx Data class"""

class RxData():
	def __init__(self, sid, dest_serial, dest_type, src_serial,
				 src_type, data1, data2):
		self.sid = sid
		self.dest_serial = dest_serial
		self.dest_type = dest_type
		self.src_serial = src_serial
		self.src_type = src_type
		self.data1 = data1
		self.data2 = data2
