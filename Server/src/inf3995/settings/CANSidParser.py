"""CAN Sid Parser class"""


import csv
from enum import Enum
from pathlib import Path

from inf3995.settings.CANSid import CANSid
import inf3995.core


CAN_SID_FILE = 'config/CANSid.csv'

class _DataTypes(Enum):
	INT = 1  # Start at 1 because 0 is False in a boolean sense
	FLOAT = 2
	UNSIGNED = 3
	TIMESTAMP = 4
	MAGIC = 5
	NONE = 6

class CANSidParser:
	"""Contains CAN sid information"""

	# can_sid_info: Stores the textual name, data types, and
	# comment associated with an SID numerical value
	can_sid_info = {}

	MAX_EMERGENCY_EVENT_SID = 256

	def __init__(self):
		# Check that specified CAN Sid file exists
		can_sid_file = Path(CAN_SID_FILE)
		if not can_sid_file.is_file():
			print(__name__ + ': Cannot find file ' + CAN_SID_FILE)
			inf3995.core.ApplicationManager().exit(0)
			return

		with open(CAN_SID_FILE, 'r', encoding='utf-8') as can_sid_file:
			csv_reader = csv.reader(can_sid_file, delimiter=';')
			for line_no, values in enumerate(csv_reader):
				if line_no == 0:
					continue

				if not values or values[0] == '':
					# Empty line or section title
					continue

				# Skip messages we are not interested in
				# Skip 'MOTOR' messages
				sid_name = values[0]
				if 'MOTOR' in sid_name:
					continue

				# Skip '(To the rocket)' messages
				comment = values[3]
				if '(To the rocket)' in comment:
					continue

				# Skip and warn about invalid data types
				data1_type = values[1]
				try:
					_DataTypes[data1_type]
				except KeyError:
					print(__name__ + ' : Skipping CAN message '
						+ sid_name + '. Invalid data 1 type : '
						+ data1_type)

				data2_type = values[2]
				try:
					_DataTypes[data2_type]
				except KeyError:
					print(__name__ + ' : Skipping CAN message '
						+ sid_name + '. Invalid data 2 type : '
						+ data2_type)

				# Store SID info
				sid = CANSid[sid_name]
				CANSidParser.can_sid_info[sid] = {
											'sid_name': sid_name,
											'data1_type':data1_type,
											'data2_type':data2_type,
											'comment': comment}

				#print(values)  # Uncomment to print parsed SID info
			# print(self.can_sid_info)
