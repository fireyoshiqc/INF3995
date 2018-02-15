"""CSV Parser class"""

import csv

from inf3995.core.AbstractTaskNode import *


class CSVParserTask(AbstractTaskNode):
	def __init__(self):
		super(CSVParserTask, self).__init__(is_queued_input_data = False, buffer_size = 1024)
