"""RateWaiter class"""


import time


class RateWaiter(object):
	def __init__(self, period = 0.0):
		self.__next_time = time.monotonic()
		self.__period = period
	
	@property
	def period(self):
		return self.__period
	
	@period.setter
	def period(self, period):
		self.__period = period
	
	def wait_next(self):
		current = time.monotonic()
		if current < self.__next_time - self.__period:
			self.__next_time = current
		self.__next_time += self.__period
		if current < self.__next_time:
			time.sleep(max(0.0, self.__next_time - current))
		elif self.__next_time < current - self.__period:
			self.__next_time = current

