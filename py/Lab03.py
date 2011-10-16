import sys
from rtbot import *
from rtbot_server import *
from rtbot_controller import *
import logging
import time
import SocketServer
import socket
import threading
import Queue
import signal

FORMAT = '%(asctime)s %(levelname)s [%(filename)s:%(lineno)d] %(message)s'
DATE_FORMAT = '%H%M%S'

TTY = "/dev/ttyS1"

def main():
  if len(sys.argv) < 3:
    print "run again with arguments: address port"
    return
  HOST = sys.argv[1]
  PORT = int(sys.argv[2])
  logging.basicConfig(level=logging.INFO, format=FORMAT, datefmt=DATE_FORMAT) 
  global robot
  robot = Rtbot(TTY)
  robot.start()
  driver = Robot_Controller(robot)
  driver.start()
  #robot.safe_drive('{"command": "forward"}')
  #robot.safe_drive('{"command": "left", "angle": 90}')
  #COMMANDS.append('{"command": "right"}')
  server = SocketServer.TCPServer((HOST, PORT), Robot_Server)
  server.serve_forever()
  driver.join()
main() 
