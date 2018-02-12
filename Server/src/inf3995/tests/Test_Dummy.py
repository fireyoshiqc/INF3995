""" Test_Dummy class """


import unittest


class Test_Dummy(unittest.TestCase):
	def setUp(self):
		print("\n" "Hi!")
	
	def tearDown(self):
		print("\n" "Bye bye!")
	
	def test_ok(self):
		the_answer = 42
		self.assertEqual(the_answer, 42)
	
	def test_not_ok(self):
		power_level = 42
		self.assertGreater(power_level, 9000)

