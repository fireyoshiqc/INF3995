"""CSV Parser class"""

import csv
import time
from enum import Enum

from inf3995.core.AbstractTaskNode import *
from inf3995.core.ApplicationManager import *
from inf3995.data_rx.RxData import RxData

# TODO: Use connector file argument
CSV_LOG_FILE = 'flight_logs/test_flight_logs/test_baddirection.csv'
CSV_LOG_FORMAT = 'Temps (s);Direction;Mod. source;Ser. source;Mod. dest;Ser. dest;ID message;Donnée 1;Donnée 2'
CSV_LOG_N_COLUMNS = 9

class _Direction(Enum):
	IN = 1  # Start at 1 because 0 is False in a boolean sense
	OUT = 2

class CSVReaderTask(AbstractTaskNode):
	def __init__(self):
		super(CSVReaderTask, self).__init__(is_queued_input_data = False, buffer_size = 1024)
		self.csv_file = open(CSV_LOG_FILE, 'r', encoding='utf-8')
		self.csv_reader = csv.reader(self.csv_file, delimiter=';')
		self.next_line = ''
		self.start_time = time.clock()

	def init(self):
		pass

	def on_first_run(self):
		try:
			self.next_line = self.csv_reader.__next__()
		except StopIteration:
			print(__name__ + ': End of CSV file.')
			# TODO : Call ApplicationManager exit
			exit()
			#ApplicationManager().exit()

		csv_log_format = ';'.join(self.next_line)
		# TODO : Throw an exception if CSV file is bad
		if csv_log_format != CSV_LOG_FORMAT:
			print(__name__ + ': Bad CSV file.')
			exit()
			#ApplicationManager().exit()

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
			try:
				self.next_line = self.csv_reader.__next__()
			except StopIteration:
				print(__name__ + ': End of CSV file.')
				# TODO: Call ApplicationManager exit
				exit()
				#ApplicationManager().exit()

			# Skip log messages with the wrong number of elements
			if len(self.next_line) is not CSV_LOG_N_COLUMNS:
				continue

			# Skip log messages with invalid directions or messages
			# marked as outgoing
			direction = self.next_line[1]
			if direction not in _Direction.__members__ \
					or _Direction[direction] == _Direction.OUT:
				continue

			data = RxData(sid=self.next_line[6],
						  dest_serial=self.next_line[2],
						  dest_type=self.next_line[3],
						  src_serial=self.next_line[4],
						  src_type=self.next_line[5],
						  data1=self.next_line[7],
						  data2=self.next_line[8])
			# TODO: Publish the data point
			print(self.next_line)

			timestamp = float(self.next_line[0])
			if timestamp < 0:
				continue
			if timestamp > log_time:
				break;

	def cleanup(self):
		self.csv_file.close()
		print('CSV Reader cleanup.')
