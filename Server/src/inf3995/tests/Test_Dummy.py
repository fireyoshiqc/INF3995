""" Test_Dummy class """


import unittest


class Test_Dummy(unittest.TestCase):
	def test_ok(self):
		the_answer = 42
		self.assertEqual(the_answer, 42)

