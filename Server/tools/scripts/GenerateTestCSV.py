"""
Script for generating the test CSV
"""


import csv
import random


FIVE_MINUTES_IN_SECONDS = 300
START_AGRUM_TIMEOUT = 5
END_AGRUM_TIMEOUT = 10
START_BAD_VOLTAGE = 15
END_BAD_VOLTAGE = 30

# TODO: Make sure data set allows all required custom updates
with open('../../working_dir/flight_logs/valkyrie_ii.csv', 'r', encoding='utf-8') as input_file, \
	open('../../working_dir/flight_logs/test_flight_logs/split_ICM_ACCEL_X.csv', 'r', encoding='utf-8') as acc_x_input, \
	open('../../working_dir/flight_logs/test_flight_logs/test_remise_out.csv', 'w', encoding='utf-8') as output_file, \
	open('../../working_dir/flight_logs/valkyrie_ii.csv', 'r', encoding='utf-8') as timestamp_file:
		input_reader = csv.reader(input_file, delimiter=';')
		timestamp_reader = csv.reader(timestamp_file, delimiter=';')
		acc_x_reader = csv.reader(acc_x_input, delimiter=';')

		# Discard first line of timestamp file
		timestamp_reader.__next__()

		csv_columns = input_reader.__next__()
		output_file.write(';'.join(csv_columns) + '\n')
		print(';'.join(csv_columns))

		filler_msg = []

		for i, line in enumerate(input_file):
			if i == 0:
				continue

			next_line = input_reader.__next__()

			# Check timestamp
			next_timestamp = next_line[0]
			# Maximum duration of test CSV is 5 minutes
			if float(next_timestamp) >= FIVE_MINUTES_IN_SECONDS:
				break

			# Get filler messages for planned AGRUM timeout
			if float(next_timestamp) < END_AGRUM_TIMEOUT:
				module_type = next_line[2]
				if 'AGRUM' not in module_type:
					filler_msg = next_line

			# Demonstrate AGRUM timeout
			if (float(next_timestamp) > START_AGRUM_TIMEOUT) \
					and (float(next_timestamp) < END_AGRUM_TIMEOUT):
				# Get rid of all AGRUM messages during timeout
				if 'AGRUM' in module_type:
					next_line = filler_msg
			# Demonstrate bad value
			elif (float(next_timestamp) > START_BAD_VOLTAGE) \
					and (float(next_timestamp) < END_BAD_VOLTAGE):
				sid_name = next_line[6]
				if 'RPM_45V' in sid_name:
					# Replace good data with random negative number
					next_line[7] = str(random.uniform(0,1) - 2)
					#print(next_timestamp, sid_name)
					print(next_line)
			else:
				# Replace x acceleration with actual rocket launch
				sid_name = next_line[6]
				if 'ICM_ACC_X' in sid_name:
					new_acc = acc_x_reader.__next__()[1]
					next_line[7] = new_acc

					# Check what time the launch starts
					if '10.' in str(new_acc):
						print(new_acc, next_timestamp)

				# TODO: Put more interesting GPS data?
				# Might be easier that it actually adjusts to position
				# if nothing is moving?
				# Use Yohan's fusée data from ciel d'octobre

				# if 'RPM_45V' in sid_name:
				# 	print(next_timestamp, sid_name)
				# 	v_count = v_count + 1

			#print(next_line)
			output_file.write(';'.join(next_line) + '\n')
