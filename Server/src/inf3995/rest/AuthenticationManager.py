"""AuthenticationManager class"""


import base64
import hashlib
import os
import collections

from lxml import etree


class AuthenticationManager(object):
	def __init__(self):
		self.__users = {}
	
	def load_users(self, filename):
		tree = etree.parse(filename)
		root = tree.getroot()
		if root.tag != "Users":
			raise RuntimeError("Root of XML must be \"Users\"")
		
		for elem in root.findall("User"):
			is_valid_elem = "username" in elem.attrib and \
			                "password" in elem.attrib and \
			                "salt" in elem.attrib
			if not is_valid_elem:
				raise RuntimeError("Missing user login information")
			
			password_hash = base64.b64decode(elem.attrib["password"])
			salt = base64.b64decode(elem.attrib["salt"])
			user_info = {"password" : password_hash, "salt" : salt}
			self.__users[elem.attrib["username"]] = user_info
	
	def save_users(self, filename):
		tree = etree.ElementTree(etree.Element("User"))
		root = tree.getroot()
		root.tag = "Users"
		
		for username, user_info in self.__users.items():
			elem = etree.SubElement(root, "User")
			elem.set("username", username)
			elem.set("password", base64.b64encode(user_info["password"]))
			elem.set("salt", base64.b64encode(user_info["salt"]))
		
		xmlFile = open(filename, "wb")
		xmlFile.write(etree.tostring(tree, encoding="UTF-8", method="xml",
		                             xml_declaration=True, pretty_print=True))
		xmlFile.close()
	
	def is_valid_login_info(self, username, password):
		if username in self.__users:
			salt = self.__users[username]["salt"]
			password_hash = self.__hash_password(password, salt)
			return password_hash == self.__users[username]["password"]
		else:
			return False
	
	def add_user(self, username, password):
		if username.strip() == "":
			raise RuntimeError("Invalid username")
		
		salt = os.urandom(64)
		password_hash = self.__hash_password(password, salt)
		self.__users[username] = {"password" : password_hash, "salt" : salt}
	
	def remove_user(self, username):
		if username in self.__users:
			del self.__users[username]
	
	def has_user(self, username):
		return username in self.__users
	
	def launch_interactive_editor(self, filename):
		users_file = filename
		self.load_users(users_file)
		print("Editing user credentials in \"" + users_file + "\"")
		self.__show_possible_commands()
		
		while True:
			print("\n" "Enter the username to edit or a /command:")
			input_value = input("> ").strip()
			input_info = self.__handle_input_value(input_value, users_file)
			if input_info.is_command:
				if input_info.should_delete:
					print("Must enter username first.")
				elif input_info.should_quit:
					break
				continue
			username = input_value
			
			# TODO: Check username validity.
			
			print("Enter password for \"" + username + "\" or a /command:")
			input_value = input("> ").strip()
			input_info = self.__handle_input_value(input_value, users_file)
			if input_info.is_command:
				if input_info.should_delete:
					self.remove_user(username)
				elif input_info.should_quit:
					break
				continue
			password = input_value
			self.add_user(username, password)
	
	def __hash_password(self, password, salt):
		result = password.encode("utf-8")
		for i in range(0, 0xCAFE):
			passhash = hashlib.sha512()
			passhash.update(result + salt)
			result = passhash.digest()
		return result
	
	def __show_possible_commands(self):
		print("Possible commands:")
		print("\t" + "/help     Show this help text")
		print("\t" + "/save     Save changes to file")
		print("\t" + "/reload   Reload file (discarding local changes)")
		print("\t" + "/cancel   Cancel current edit")
		print("\t" + "/delete   Delete user (after entering username)")
		print("\t" + "/exit     Quit without saving")
	
	def __handle_input_value(self, input_value, users_file):
		ParseResult = collections.namedtuple("ParseResult", ["is_command",
		                                                      "should_quit",
		                                                      "should_delete"])
		should_quit = False
		should_delete = False
		is_command = input_value[0] == "/"
		if is_command:
			if input_value == "/help":
				self.__show_possible_commands()
			elif input_value == "/save":
				self.save_users(users_file)
			elif input_value == "/reload":
				self.load_users(users_file)
			elif input_value == "/save":
				self.save_users(users_file)
			elif input_value == "/cancel":
				pass
			elif input_value == "/delete":
				should_delete = True
			elif input_value == "/exit":
				should_quit = True
			else:
				print("Unknown command.")
		
		return ParseResult(is_command, should_quit, should_delete)

