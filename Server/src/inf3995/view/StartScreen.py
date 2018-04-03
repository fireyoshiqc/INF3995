"""Server GUI - Startup options"""

# This module is inspired by this tutorial: http://zetcode.com/gui/pyqt5/firstprograms/


import sys
from PyQt5.QtWidgets import (QApplication, QWidget, QToolTip,
								QPushButton, QDesktopWidget)
from PyQt5.QtGui import QIcon, QFont


ORONOS_LOGO = '../../../ressources/logo-oronos.png'

class StartScreen(QWidget):
	def __init__(self):
		super().__init__()

		self.initUI()

	def initUI(self):
		QToolTip.setFont(QFont('SansSerif', 10))

		self.setToolTip('This is a <b>QWidget</b> widget')

		button = QPushButton('Button', self)
		button.setToolTip('This is a <b>QPushButton</b> widget')
		button.resize(button.sizeHint())
		button.move(100, 50)

		self.resize(400, 500)
		self.center()
		self.setWindowTitle('Options - Oronos Groundstation')
		self.setWindowIcon(QIcon(ORONOS_LOGO))
		self.show()

	def center(self):
		qr = self.frameGeometry()
		cp = QDesktopWidget().availableGeometry().center()
		qr.moveCenter(cp)
		self.move(qr.topLeft())

if __name__ == '__main__':
	app = QApplication(sys.argv)
	ex = StartScreen()
	sys.exit(app.exec_())
