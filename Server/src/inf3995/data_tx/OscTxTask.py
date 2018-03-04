"""OscTxTask class"""


from inf3995.core.AbstractTaskNode import *
from inf3995.data_tx.OscSender import *
from inf3995.core.ProgramOptions import *


class OscTxTask(AbstractTaskNode):
	def __init__(self):
		super(OscTxTask, self).__init__(False, 1024)
		udp_port = ProgramOptions.get_value("server")
		self.__sender = OscSender("/inf3995-03/flight-data", udp_port)
	
	def init(self):
		pass
	
	def on_first_run(self):
		pass
	
	def handle_data(self):
		data = self._get_source_data()
		self.__sender.update_value(data)
		self.__sender.send_message()
	
	def cleanup(self):
		pass
	
	def get_sender(self):
		return self.__sender

