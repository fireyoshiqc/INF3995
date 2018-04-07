"""Server GUI - Startup options"""


import sys
import os
import collections
import pickle
from PyQt5.QtWidgets import (QApplication, QWidget, QToolTip,
	QPushButton, QDesktopWidget, QComboBox, QGridLayout, QLabel,
	QLineEdit, QCheckBox)
from PyQt5.QtGui import QIcon, QFont


#ORONOS_LOGO = '../../../ressources/logo-oronos.png'
ORONOS_LOGO = '../ressources/logo-oronos.png'
MAP_LOCATIONS = ['spaceport america', 'motel 6', 'convention center',
					'st-pie de guire']
#ROCKETS_DIR = '../../../working_dir/rockets'
ROCKETS_DIR = 'rockets'
CONNECTOR_TYPES = ['serial', 'simulation']
PREFERENCES_FILE = 'config/server-prefs.conf'

class GUIProgramOptions:
	def __init__(self, baudrate='921600', connector_type='serial', connector_file='',
					rocket='', map_location='', save_preferences=True):
		self.baudrate = baudrate
		self.connector_type = connector_type
		self.connector_file = connector_file
		self.rocket = rocket
		self.map = map_location
		self.save_preferences = save_preferences

class StartScreen(QWidget):
	def __init__(self):
		super().__init__()

		self.program_options = []
		self.start_server = False

		self.initUI()

	def initUI(self):
		saved_preferences = []
		try:
			with open(PREFERENCES_FILE, 'rb') as preferences_file:
				saved_preferences = pickle.load(preferences_file)
		except FileNotFoundError as e:
			print(__name__ + ': Server preferences file '
					+ str(PREFERENCES_FILE) + ' not found')
			saved_preferences = GUIProgramOptions(save_preferences=False)

		grid = QGridLayout()
		grid.setSpacing(5)

		self.connector_type = QComboBox()
		self.connector_type.addItems(CONNECTOR_TYPES)
		#self.connector_type.currentIndexChanged.connect(self.connector_type_change)
		grid.addWidget(self.connector_type, 1, 0)
		self.connector_file = QLineEdit()
		grid.addWidget(self.connector_file, 1, 1)

		self.baudrate_label = QLabel('Baudrate')
		grid.addWidget(self.baudrate_label, 2, 0)
		self.baudrate = QLineEdit()
		self.baudrate.setText('921600')
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

		if saved_preferences.save_preferences:
			self.connector_type.setCurrentText(saved_preferences.connector_type)
			self.connector_file.setText(saved_preferences.connector_file)
			self.baudrate.setText(saved_preferences.baudrate)
			self.rocket_layout.setCurrentText(saved_preferences.rocket)
			self.map.setCurrentText(saved_preferences.map)

		self.save_preferences = QCheckBox('Save preferences')
		self.save_preferences.setChecked(True)
		grid.addWidget(self.save_preferences, 6, 1)

		self.start_button = QPushButton('Start')
		grid.addWidget(self.start_button, 7, 1)
		self.start_button.clicked.connect(lambda: self.collect_arguments())

		self.setLayout(grid)

		self.resize(350, 300)
		self.center()
		self.setWindowTitle('Options - Oronos Groundstation')
		#self.setStyleSheet('background: url(\"../ressources/soviet.jpg\") ; background-position: center;')
		self.setWindowIcon(QIcon(ORONOS_LOGO))
		self.show()

	def collect_arguments(self):
		self.program_options = GUIProgramOptions(
							baudrate=self.baudrate.text(),
							connector_type=self.connector_type.currentText(),
							connector_file=self.connector_file.text(),
							rocket=self.rocket_layout.currentText(),
							map_location=self.map.currentText(),
							save_preferences=self.save_preferences.isChecked())
		# print(self.program_options.baudrate, self.program_options.connector_type, self.program_options.connector_file,
		# 	  self.program_options.rocket, self.program_options.map, self.program_options.save_preferences)

		# Save preferences
		with open(PREFERENCES_FILE, 'wb') as preferences_file:
			pickle.dump(self.program_options, preferences_file,
							pickle.HIGHEST_PROTOCOL)

		self.start_server = True
		self.close()

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

	def closeEvent(self, event):
		if self.start_server != True:
			print(__name__ + ': Exiting server')
			sys.exit(0)
