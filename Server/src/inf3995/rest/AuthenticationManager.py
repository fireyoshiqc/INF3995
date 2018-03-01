"""AuthenticationManager class"""


import base64
import hashlib
import os

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
	
	def is_valid_login_info(self, username, password):
		if username in self.__users:
			salt = self.__users[username]["salt"]
			password_hash = self._hash_password(password, salt)
			return password_hash == self.__users[username]["password"]
		else:
			return False
	
	def add_user(self, username, password):
		if username.strip() == "":
			raise RuntimeError("Invalid username")
		
		salt = os.urandom(64)
		password_hash = self._hash_password(password, salt)
		self.__users[username] = {"password" : password_hash, "salt" : salt}
	
	def remove_user(self, username):
		if username in self.__users:
			del self.__users[username]
	
	def _hash_password(self, password, salt):
		result = password.encode("utf-8")
		for i in range(0, 0xCAFE):
			passhash = hashlib.sha512()
			passhash.update(result + salt)
			result = passhash.digest()
		return result

