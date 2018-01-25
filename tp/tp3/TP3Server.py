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
        template = """
        <html>
        <head><meta charset="UTF-8"></head>
        <body>
            <a href="http://polymtl.ca">Poly Mtl</a>
        </body>
        </html>
        """
        return template

    @cherrypy.expose
    def test3(self):
        """test3 endpoint, returns a PNG file of a cute python"""
        path = os.path.join(os.getcwd(), os.path.dirname(__file__), 'python-art.png')
        return static.serve_file(path, 'image/png', 'inline', os.path.basename(path))

def error_404(status, message, traceback, version):
    """404 error handler function"""
    template = """
    <html>
    <head><meta charset="UTF-8"></head>
    <body>
        <h1>404 Page Non Trouvée</h1>
        <img src="/python-404.png" alt="Sad Python 404">
        <h2>Détails</h2>
        <p>{0}</p>
        <p>{1}</p>
        <p>{2}</p>
        <p>CherryPy version : {3}</p>
    </body>
    </html>
    """
    return template.format(status, message, traceback, version)

if __name__ == '__main__':
    CONF = {
        'global': {
            'server.socket_port': 80,
            'log.access_file': 'requests.log',
            'log.error_file': 'errors.log',
            'error_page.404': error_404
        },
        '/': {
            'tools.sessions.on': True,
            'tools.staticdir.root': os.path.abspath(os.getcwd()),
            'tools.staticdir.on': True,
            'tools.staticdir.dir': ''
        }
    }

cherrypy.quickstart(TP3Server(), '/', CONF)
