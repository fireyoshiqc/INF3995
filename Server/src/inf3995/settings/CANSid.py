"""CAN Sid class"""

CAN_SID_FILE = 'config/CANSid.csv'


#Dynamic enumssssssss! :D :D
class CANSid:
	def __init__(self):
		self.

		with open(CAN_SID_FILE, 'r') as can_sid_file:
			for line in can_sid_file:
				() = line.split(';')  # Split into the different fields? & then insert into a dynamic enum?


