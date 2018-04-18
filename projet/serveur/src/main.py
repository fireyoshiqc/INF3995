#!/usr/bin/env python3
"""Server - INF3995"""


import sys
import time

import inf3995.core as core
import inf3995.utils as utils


# main
if __name__ == "__main__":
	app = core.ApplicationManager()
	
	app.startup(sys.argv)
	
	sys.exit(app.execute())

