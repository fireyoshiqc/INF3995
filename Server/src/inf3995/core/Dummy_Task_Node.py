"""Dummy_Task_Node class"""


import time

from inf3995.core.Ab_Task_Node import *


class Dummy_Task_Node(Ab_Task_Node):
	def __init__(self):
		super(Dummy_Task_Node, self).__init__(True, 1024)
	
	def init(self):
		print("In the beginning, God created the heavens and the earth...")
	
	def on_first_run(self):
		print("And Abram went up out of Egypt, he, and his wife...")
	
	def handle_data(self):
		print("But the men of Sodom were wicked and sinners before the Lord exceedingly.")
	
	def cleanup(self):
		print("...Cain rose up against Abel his brother, and slew him.")

