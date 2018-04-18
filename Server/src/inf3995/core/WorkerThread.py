"""WorkerThread class"""


import threading
import time

import inf3995.utils as utils


class WorkerThread(object):
	def __init__(self, name, max_run_frequency):
		self.__task_nodes = []
		self.__thread = None
		self.__is_running = False
		self.__is_finishing = False
		self.__is_paused = False
		self.__name = name
		period = 0.0
		if max_run_frequency is not None:
			period = max(0.0, 1.0 / max_run_frequency)
		self.__RateWaiter = utils.RateWaiter(period)
	
	def add_task_node(self, task_node):
		if task_node is not None:
			self.__task_nodes.append(task_node)
	
	def init_task_nodes(self):
		for node in self.__task_nodes:
			node.init()
	
	def cleanup_task_nodes(self):
		for node in self.__task_nodes:
			node.cleanup()
	
	def start(self):
		if not self.__is_running:
			self.__is_paused = False
			self.__thread = threading.Thread(target=self.__run_thread,
			                                 name=self.__name)
			self.__thread.start()
	
	def start_paused(self):
		if not self.__is_running:
			self.__is_paused = True
			self.__thread = threading.Thread(target=self.__run_thread)
			self.__thread.start()
	
	def finish(self, timeout):
		if not self.__is_running:
			return False
		
		for node in self.__task_nodes:
			node.finish()
		self.__is_finishing = True
		
		timeout_time = time.monotonic() + timeout
		while self.__is_running and time.monotonic() < timeout_time:
			time.sleep(0)
		
		if not self.__is_running:
			self.__thread.join()
		
		return self.__is_running
	
	def pause(self):
		self.__is_paused = True
	
	def unpause(self):
		self.__is_paused = False
	
	def terminate(self):
		if self.__thread.is_alive():
			return
		
		if not self.__is_running:
			self.__thread.join()
		else:
			# TODO: Kill it with fire! (native thread kill)
			pass
		
		self.__is_running = False
	
	def is_running(self):
		return self.__is_running
	
	def is_paused(self):
		return self.__is_paused
	
	def __run_thread(self):
		self.__is_finishing = False
		self.__is_running = True

		while self.__is_running:
			if self.__is_paused:
				time.sleep(0)
				continue
			
			for node in self.__task_nodes:
				node.run()
			
			if self.__is_finishing:
				done = True
				for node in self.__task_nodes:
					done &= node.is_finished()
				self.__is_running = not done
			else:
				self.__RateWaiter.wait_next()
			
			time.sleep(0.001)

