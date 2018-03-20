import sys
import os
import enum
import platform

from inf3995.utils.SimpleNamedPipe import *


PIPE_NAME = "/tmp/inf3995-users-view"


@enum.unique
class SignalCodes(enum.IntEnum):
	OK = 0,
	EXIT = 1;


class SecondConsole(object):
	@staticmethod
	def run():
		pipe = SimpleNamedPipeClient(PIPE_NAME, PipeMode.READER)
		print("--- Connected Clients ---" "\n")
		sys.stdout.flush()
		
		while True:
			msg = pipe.read()
			if msg is None:
				continue
			elif len(msg) == 1:
				if msg[0] == SignalCodes.EXIT:
					return
				else:
					continue
			
			# Yes, I know it's ugly, shut up!
			if platform.system() == "Windows":
				os.system("cls")
			else:
				os.system("clear")
			
			print("--- Connected Clients ---" "\n")
			print(msg.decode("utf-8"))
			sys.stdout.flush()


if __name__ == "__main__":
	SecondConsole.run()

