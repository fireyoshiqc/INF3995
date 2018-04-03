"""Server GUI - Startup options"""

# This module is inspired by this tutorial: http://zetcode.com/gui/pyqt5/firstprograms/


import sys
import os
import collections
from PyQt5.QtWidgets import (QApplication, QWidget, QToolTip,
	QPushButton, QDesktopWidget, QComboBox, QGridLayout, QLabel,
	QLineEdit, QCheckBox)
from PyQt5.QtGui import QIcon, QFont


ORONOS_LOGO = '../../../ressources/logo-oronos.png'
MAP_LOCATIONS = ['spaceport america', 'motel 6', 'convention center',
					'st-pie de guire']
ROCKETS_DIR = '../../../working_dir/rockets'
CONNECTOR_TYPES = ['Serial', 'Simulation']

class StartScreen(QWidget):
	def __init__(self):
		super().__init__()

		self.initUI()

	def initUI(self):
		QToolTip.setFont(QFont('SansSerif', 10))

		self.setToolTip('This is a <b>QWidget</b> widget')

		grid = QGridLayout()
		grid.setSpacing(5)

		self.connector_type = QComboBox()
		self.connector_type.addItems(CONNECTOR_TYPES)
		#self.connector_type.currentIndexChanged.connect(self.selection_change)
		grid.addWidget(self.connector_type, 1, 0)
		self.connector_file = QLineEdit()
		grid.addWidget(self.connector_file, 1, 1)

		self.baudrate_label = QLabel('Baudrate')
		grid.addWidget(self.baudrate_label, 2, 0)
		# TODO: Add default value that makes sense e.g. 921600
		self.baudrate = QLineEdit()
		grid.addWidget(self.baudrate, 2, 1)

		self.rocket_label = QLabel('Rocket Layout')
		grid.addWidget(self.rocket_label, 3, 0)
		self.rocket_layout = QComboBox()
		rockets = self.find_all_files_in_dir(ROCKETS_DIR)
		self.rocket_layout.addItems(rockets)
		grid.addWidget(self.rocket_layout, 3, 1)

		self.map_label = QLabel('GPS Map')
		grid.addWidget(self.map_label, 4, 0)
		self.map = QComboBox()
		self.map.addItems(MAP_LOCATIONS)
		grid.addWidget(self.map, 4, 1)

		self.keep_preferences = QCheckBox('Keep preferences')
		grid.addWidget(self.keep_preferences, 6, 1)

		self.start_button = QPushButton('Start')
		grid.addWidget(self.start_button, 7, 1)

		# button = QPushButton('Button', self)
		# button.setToolTip('This is a <b>QPushButton</b> widget')
		# button.resize(button.sizeHint())
		# button.move(100, 50)

		self.setLayout(grid)

		self.resize(350, 300)
		self.center()
		self.setWindowTitle('Options - Oronos Groundstation')
		#self.setStyleSheet('background: url(\"../ressources/soviet.jpg\") ; background-position: center;')
		self.setWindowIcon(QIcon(ORONOS_LOGO))
		self.show()

	def center(self):
		qr = self.frameGeometry()
		cp = QDesktopWidget().availableGeometry().center()
		qr.moveCenter(cp)
		self.move(qr.topLeft())

	def selection_change(self, i):
		print('Items in the list are :')

		for count in range(self.combo_box.count()):
			print(self.combo_box.itemText(count))
		print('Current index', i, 'selection changed ', self.combo_box.currentText())

	def find_all_files_in_dir(self, dir_path):
		all_files = []
		foo = os.walk(dir_path)
		for root, subdirs, files in os.walk(dir_path):
			for f in files:
				filename = root + "/" + f
				filename = filename.replace("\\", "/");
				filename = filename.replace(dir_path, "")
				if filename[0] == "/":
					filename = filename[1:len(filename)]
				all_files.append(filename)

		return all_files

if __name__ == '__main__':
	app = QApplication(sys.argv)
	ex = StartScreen()
	sys.exit(app.exec_())
