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
HOST = ''
PORT = 5000

def main():
  logging.basicConfig(level=logging.INFO, format=FORMAT, datefmt=DATE_FORMAT) 
  global robot
  robot = Rtbot(sys.argv[1])
  robot.start()
  server = Robot_Server(PORT)
  server.start()
  driver = Robot_Controller(robot)
  driver.start()
  server.join()
main() 
