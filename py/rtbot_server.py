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
    conn, addr = s.accept()
    print 'Connected by', addr
    while 1:
        data = conn.recv(1024)
        print data
        if not data: break
        COMMANDS.append(data)
    conn.close()
