"""Bitfield.py
	Représente un bitfield d'un seul entier 32 bits (correspondant à un data CAN).
	Basé sur https://code.activestate.com/recipes/113799/

Auteur:
	Frédéric Fortier

Copyright 2017. Oronos Polytechnique. Tous droits réservés
"""

class Bitfield:
	"""
	Classe bitfield, utilisable pas mal comme une liste. Exemples d'utilisation:
	k = Bitfield(32768)
	k[3:7] = 5
	print(str(k[15]))
	print(str(k[4:7]))
	print(str(int(k)))
	print(bin(int(k)))
	
	"C:\Program Files\Python35\python.exe" C:/Oronos/GitEmbarque/groundstation/Util/Bitfield.py
	1
	2
	32808
	0b1000000000101000
	"""
	def __init__(self, value=0):
		"""
		:param value: Valeur (entière) du bitfield.

		"""
		self._d = value

	def __getitem__(self, index):
		if type(index) is int:
			return (self._d >> index) & 1
		elif type(index) is slice:
			mask = 2 ** (index.stop - index.start) - 1
			return (self._d >> index.start) & mask
		else:
			raise TypeError("Unsupported operand type: " + str(type(index)))

	def __setitem__(self, index, value):
		if type(index) is int:
			value = (value & 1) << index
			mask = 1 << index
			self._d = (self._d & ~mask) | value
		elif type(index) is slice:
			mask = 2 ** (index.stop - index.start) - 1
			value = (value & mask) << index.start
			mask = mask << index.start
			self._d = (self._d & ~mask) | value
			return (self._d >> index.start) & mask
		else:
			raise TypeError("Unsupported operand type: " + str(type(index)))

	def __int__(self):
		return self._d

	__index__ = __int__
