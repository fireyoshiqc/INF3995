"""DummyTaskNode class"""


import sys

import inf3995.core
from inf3995.core.AbstractTaskNode import *


class DummyTaskNode(AbstractTaskNode):
	def __init__(self):
		super(DummyTaskNode, self).__init__(True, 1024)
		self.__event_logger = inf3995.core.ApplicationManager().get_event_logger()
	
	def init(self):
		msg = "In the beginning, God created the heavens and the earth..."
		# print(msg)
		# sys.stdout.flush()
		self.__event_logger.log_info(msg)
	
	def on_first_run(self):
		msg = "And Abram went up out of Egypt, he, and his wife..."
		# print(msg)
		# sys.stdout.flush()
		self.__event_logger.log_info(msg)
	
	def handle_data(self):
		msg = "But the men of Sodom were wicked and sinners before the Lord exceedingly."
		# print(msg)
		# sys.stdout.flush()
		self.__event_logger.log_info(msg)
	
	def cleanup(self):
		msg = "...Cain rose up against Abel his brother, and slew him."
		# print(msg)
		# sys.stdout.flush()
		self.__event_logger.log_info(msg)


