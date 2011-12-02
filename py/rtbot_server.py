from pyrobot import *
from rtbot import *
from rtbot_controller import *
from collections import deque
import sys
import signal
import logging
import time
import struct
import os
import SocketServer

class ThreadedTCPRequestHandler(SocketServer.StreamRequestHandler):

  allow_reuse_address = True

  def handle(self):
    print "handling!"
    try: 
      message = self.rfile.readline().strip()
      print "Message: ", message
      if(message == 'shutdown'):
        COMMANDS.append({'command':'shutdown'})
        self.server.shutdown()
        return
      COMMANDS.append(message)
    except Exception as exception:
      print exception

class ThreadedTCPServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):
  pass
