"""CSV Reader class"""

import csv
import time
from enum import Enum

from inf3995.settings.CANSid import CANSid
from inf3995.settings.ModuleTypes import ModuleType, ALL_SERIAL_NBS
from inf3995.core.AbstractTaskNode import *
# from inf3995.core.ApplicationManager import *
import inf3995.core
from inf3995.data_rx.RxData import RxData

CSV_LOG_FORMAT = 'Temps (s);Direction;Mod. source;Ser. source;Mod. dest;Ser. dest;ID message;Donnée 1;Donnée 2'
CSV_LOG_N_COLUMNS = 9
ALL_SERIAL_NBS_STR = 'ALL_SERIAL_NBS'

class _Direction(Enum):
	IN = 1  # Start at 1 because 0 is False in a boolean sense
	OUT = 2

class CSVReaderTask(AbstractTaskNode):
	def __init__(self, log_file):
		super(CSVReaderTask, self).__init__(is_queued_input_data = False, buffer_size = 1024)
		self.__event_logger = inf3995.core.ApplicationManager().get_event_logger()
		try:
			self.csv_file = open(log_file, 'r', encoding='utf-8')
		except FileNotFoundError as e:
			# Print statement used because logger task hasn't started yet
			print(__name__ + ': ' + str(e))
			inf3995.core.ApplicationManager().exit(1)
			return
		self.csv_reader = csv.reader(self.csv_file, delimiter=';')
		self.next_line = ''
		self.start_time = time.clock()

	def init(self):
		pass

	def on_first_run(self):
		try:
			self.next_line = self.csv_reader.__next__()
		except StopIteration:
			self.__event_logger.log_debug(__name__ + ": End of CSV file.")
			inf3995.core.ApplicationManager().exit(0)
			return

		csv_log_format = ';'.join(self.next_line)
		if csv_log_format != CSV_LOG_FORMAT:
			self.__event_logger.log_error(__name__ + ": CSV file is the"
				" incorrect format. Expected format: " + CSV_LOG_FORMAT)
			# If we don't do this, we shall slumber for a hundred years
			self.csv_file.close()
			inf3995.core.ApplicationManager().exit(1)
			return

	def handle_data(self):
		"""
			Reads log data until the data timestamp is larger than the
			time since the server started. The time since the server
			started corresponds to where the log should be in its
			execution.
		"""
		current_time = time.clock()
		log_time = current_time - self.start_time  # TODO: Find a better variable name

		while True:
			if self.csv_file.closed:
				return
			
			try:
				self.next_line = self.csv_reader.__next__()
			except StopIteration:
				self.__event_logger.log_debug(__name__ + ": End of CSV file.")
				# If we don't do this, we shall slumber for a hundred years
				self.csv_file.close()
				inf3995.core.ApplicationManager().exit(0)
				return

			# Skip log messages with the wrong number of elements
			if len(self.next_line) is not CSV_LOG_N_COLUMNS:
				continue

			# Skip log messages with invalid directions or messages
			# marked as outgoing
			direction = self.next_line[1]
			if direction not in _Direction.__members__ \
					or _Direction[direction] == _Direction.OUT:
				continue

			# Skip log messages with invalid CAN SIDs
			try:
				sid_name = self.next_line[6]
				sid = CANSid[sid_name]
			except KeyError as e:
				self.__event_logger.log_error(__name__ + ": KeyError: " + str(e))
				continue

			# Skip log messages with invalid source or destination type
			try:
				src_name = self.next_line[2]
				src_type = ModuleType[src_name]
				dest_name = self.next_line[4]
				dest_type = ModuleType[dest_name]
			except KeyError as e:
				self.__event_logger.log_error(__name__ + ": KeyError: " + str(e))
				continue

			# Skip log messages with invalid PCB serial numbers
			try:
				src_serial = int(self.next_line[3])
				if src_serial < 0:
					continue
			except ValueError as e:
				if self.next_line[3] == ALL_SERIAL_NBS_STR:
					src_serial = ALL_SERIAL_NBS
				else:
					self.__event_logger.log_error(__name__ + ": ValueError: " + str(e))
					continue

			try:
				dest_serial = int(self.next_line[5])
				if dest_serial < 0:
					continue
			except ValueError as e:
				if self.next_line[5] == ALL_SERIAL_NBS_STR:
					dest_serial = ALL_SERIAL_NBS
				else:
					self.__event_logger.log_error(__name__ + ": ValueError: " + str(e))
					continue

			# Put data point in outgoing data buffer
			data = RxData(sid=sid,
						  src_type=src_type,
						  src_serial=src_serial,
						  dest_type=dest_type,
						  dest_serial=dest_serial,
						  data1=self.next_line[7],
						  data2=self.next_line[8])
			# print(self.next_line)
			self._produce_data(data)

			timestamp = float(self.next_line[0])
			if timestamp < 0:
				continue
			if timestamp > log_time:
				break;

	def cleanup(self):
		self.csv_file.close()
		self.__event_logger.log_debug(__name__ + ': Cleanup.')
