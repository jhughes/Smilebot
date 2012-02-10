from pyrobot import *
from rtbot import *
from rtbot_server import *
from collections import deque
import sys
import signal
import logging
import time
import socket
import struct
import thread

class Robot_Controller(threading.Thread):

  def __init__(self, robot):
    threading.Thread.__init__(self)
    self.rtbot = robot

  def run(self):
    while 1:
      if len(COMMANDS) > 0:
        command = COMMANDS.popleft()
        stop_state = self.rtbot.safe_drive(command)
        if stop_state == None: 
          break
        STOP_STATES.append(stop_state)
        
