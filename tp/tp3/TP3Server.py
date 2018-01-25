#!/usr/bin/env python3
"""Small CherryPy server to run a REST API. TP3 - INF3995"""

import os
import os.path
import cherrypy
from cherrypy.lib import static


class TP3Server(object):
    """Class to define the different server endpoints"""
    @cherrypy.expose
    def index(self):
        """Homepage"""
        return "Hello World!"

    @cherrypy.expose
    def test1(self):
        """test1 endpoint, returns a string"""
        return "test 1 est bon..."

    @cherrypy.expose
    def test2(self):
        """test2 endpoint, returns an HTML link"""
        return """
    <html>
    <head></head>
    <body>
        <a href="http://polymtl.ca">Poly Mtl</a>
    </body>
    </html>
    """

    @cherrypy.expose
    def test3(self):
        """test3 endpoint, returns a PNG file of a cute python"""
        path = os.path.join(os.getcwd(), os.path.dirname(__file__), 'python-art.png')
        return static.serve_file(path, 'image/png', 'inline', os.path.basename(path))


if __name__ == '__main__':
    CONF = {
        'global': {
            'server.socket_port': 80,
            'log.access_file': 'requests.log',
            'log.error_file': 'errors.log'
        },
        '/': {
            'tools.sessions.on': True,
            'tools.staticdir.root': os.path.abspath(os.getcwd()),
            
        }
    }

cherrypy.quickstart(TP3Server(), '/', CONF)
