import sys
from rtbot import *
import logging
import time
import SocketServer
import socket
import threading
import Queue
import signal

FORMAT = '%(asctime)s %(levelname)s [%(filename)s:%(lineno)d] %(message)s'
DATE_FORMAT = '%H%M%S'

def main():
  logging.basicConfig(level=logging.INFO, format=FORMAT, datefmt=DATE_FORMAT) 
  global robot
  robot = Rtbot(sys.argv[1])
  robot.start()
  #robot.start_server(80)
  # Driving code
  robot.SafeDrive({'distance':1414 })
  robot.SafeDrive({'radius':1, 'angle':32 })
  robot.SafeDrive({})
  robot.SafeDrive({'distance':800, 'velocity':-100, 'ignore_cliff':True })
  robot.SafeDrive({'radius':-1, 'angle':74 })
  robot.SafeDrive({'distance':1054 })
  robot.SafeDrive({'radius':1, 'angle':74 })
  robot.SafeDrive({})
  robot.SafeDrive({'distance':546, 'velocity':-100, 'ignore_bump':True })#546
  robot.SafeDrive({'radius':-1, 'angle':74 })
  robot.SafeDrive({'sonar':16})
  robot.SafeDrive({'velocity':-200})
main() 
