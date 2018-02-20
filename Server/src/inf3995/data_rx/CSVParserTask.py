"""CSV Parser class"""

import csv
import time

from inf3995.core.AbstractTaskNode import *
from inf3995.core.ApplicationManager import *

# TODO: Use connector file argument
CSV_LOG_FILE = 'flight_logs/test.csv'
CSV_LOG_FORMAT = 'Temps (s);Direction;Mod. source;Ser. source;Mod. dest;Ser. dest;ID message;Donnée 1;Donnée 2'

class CSVParserTask(AbstractTaskNode):
	def __init__(self):
		super(CSVParserTask, self).__init__(is_queued_input_data = False, buffer_size = 1024)
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

		csv_log_format = ';'.join(self.next_line)
		# TODO : Throw an exception if CSV file is bad
		if csv_log_format != CSV_LOG_FORMAT:
			print(__name__ + ': Bad CSV file.')
			exit()

	def handle_data(self):
		# Catch up to current position in the log
		current_time = time.clock()
		log_time = current_time - self.start_time  # TODO: Find a better variable name

		while True:
			try:
				self.next_line = self.csv_reader.__next__()
			except StopIteration:
				print(__name__ + ': End of CSV file.')
				# TODO: Call ApplicationManager exit
				exit()

			# TODO: Publish the data point

			timestamp = float(self.next_line[0])
			if timestamp > log_time:
				break;

		# TODO: Continue publishing data when it's required

	def cleanup(self):
		self.csv_file.close()
		print('CSV Reader cleanup.')
