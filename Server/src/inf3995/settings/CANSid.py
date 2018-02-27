"""CAN Sid class"""


import csv


CAN_SID_FILE = 'config/CANSid.csv'

class CANSid:
	"""Contains CAN sid information"""
	def __init__(self):
		#can_sid_info: Stores the textual name, data types, and
		#comment associated with an SID numerical value
		self.can_sid_info = {}

		#can_sid_names: Stores the numerical value associated
		#with the textual name of an SID. For CSV parsing.
		self.can_sid_names = {}

		# TODO: Add error checking so don't start server if cannot parse SIDs

		with open(CAN_SID_FILE, 'r', encoding='utf-8') as can_sid_file:
			csv_reader = csv.reader(can_sid_file, delimiter=';')
			sid_int = 0
			for line_no, values in enumerate(csv_reader):
				if line_no == 0:
					continue

				if not values or values[0] == '':
					# Empty line or section title
					continue

				# Store SID info
				self.can_sid_info[sid_int] = {
												'sid_name': values[0],
												'data1_type':values[1],
												'data2_type':values[2],
												'comment': values[3]}

				# Store SID name
				sid_name = self.can_sid_info[sid_int]['sid_name']
				self.can_sid_names[sid_name] = sid_int

				#print(values)  # Uncomment to print parsed SID info
				sid_int = sid_int + 1
			#print(self.can_sid_info)
			#print(self.can_sid_names)
