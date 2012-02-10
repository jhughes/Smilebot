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
import socket

class Robot_Server(threading.Thread):

  def __init__(self, robot):
    threading.Thread.__init__(self)
    self.rtbot = robot

  def run(self):
    HOST = ''                # Symbolic name meaning all available interfaces
    PORT = 5000              # Arbitrary non-privileged port
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind((HOST, PORT))
    s.listen(1)
    while 1:
      print "waiting for connection"
      conn, addr = s.accept()
      print 'Connected by', addr
      while 1:
        try:
          data = conn.recv(1024)
          if not data: break
          print data
          print eval(data)
          COMMANDS.append(data)
        except Exception as exception:
          print exception
      COMMANDS.append("{'command':'stop'}")
      print "connection broke"
      conn.close()
