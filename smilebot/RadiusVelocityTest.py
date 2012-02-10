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
  robot = Rtbot("/dev/ttyS1")
  robot.start()
  # Driving code
  while True:
    radius = eval(raw_input('radius: '))
    velocity = eval(raw_input('velocity: '))
    message = "'command':'forward', 'radius':{0}, 'velocity':{1}".format(radius, velocity)
    print message
    robot.safe_drive("{" + message + "}")
main() 
