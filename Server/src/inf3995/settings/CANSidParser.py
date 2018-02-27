"""CAN Sid Parser class"""


from inf3995.settings.CANSid import CANSid
import csv


CAN_SID_FILE = 'config/CANSid.csv'

class CANSidParser:
	"""Contains CAN sid information"""
	def __init__(self):
		#can_sid_info: Stores the textual name, data types, and
		#comment associated with an SID numerical value
		self.can_sid_info = {}

		# TODO: Add error checking so don't start server if cannot parse SIDs

		with open(CAN_SID_FILE, 'r', encoding='utf-8') as can_sid_file:
			csv_reader = csv.reader(can_sid_file, delimiter=';')
			for line_no, values in enumerate(csv_reader):
				if line_no == 0:
					continue

				if not values or values[0] == '':
					# Empty line or section title
					continue

				# Store SID info
				sid_name = values[0]
				sid = CANSid[sid_name]
				self.can_sid_info[sid] = {
												'sid_name': values[0],
												'data1_type':values[1],
												'data2_type':values[2],
												'comment': values[3]}

				#print(values)  # Uncomment to print parsed SID info
			print(self.can_sid_info)
