

import csv


DEMO_DURATION_SECONDS = 40
NEW_FILE_START = 110

with open('../../working_dir/flight_logs/polaris_modified.csv', 'r', encoding='utf-8') as input_file, \
		open('../../working_dir/flight_logs/test_flight_logs/polaris_modified_shorter.csv', 'w', encoding='utf-8') as output_file, \
		open('../../working_dir/flight_logs/polaris.csv', 'r', encoding='utf-8') as timestamp_file:
	input_reader = csv.reader(input_file, delimiter=';')
	timestamp_reader = csv.reader(timestamp_file, delimiter=';')

	# Discard first line of timestamp file
	timestamp_reader.__next__()

	csv_columns = input_reader.__next__()
	output_file.write(';'.join(csv_columns) + '\n')
	print(';'.join(csv_columns))

	# Read lines until where you want the file to start
	next_line = input_reader.__next__()
	next_timestamp = next_line[0]
	while float(next_timestamp) < NEW_FILE_START:
		next_line = input_reader.__next__()
		next_timestamp = next_line[0]

	for i, line in enumerate(input_file):
		if i == 0:
			continue

		next_line = input_reader.__next__()

		# Determine timestamp
		next_timestamp = timestamp_reader.__next__()[0]
		# Maximum duration of test CSV is 5 minutes
		if float(next_timestamp) >= DEMO_DURATION_SECONDS:
			break

		next_line[0] = next_timestamp
		output_file.write(';'.join(next_line) + '\n')
