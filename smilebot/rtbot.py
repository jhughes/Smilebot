from pyrobot import *
from collections import deque
import sys
import signal
import logging
import time
import socket
import struct
import thread

#=============================================================
# put defines here e.x.
HOST = ""
PORT = 0
COMMANDS = deque([])
STOP_STATES = deque([])
CONNECTIONS = []
MIN_SONAR_DISTANCE = 28
MAX_FORWARD_VELOCITY = 300
MAX_TURN_VELOCITY = 150


#=============================================================
# define the Rtbot class to init and start itself
class Rtbot(Create):
  distance_traveled = 0
  degrees_rotated = 0
  stop_state = {}
  bumps = ['bump-left','bump-right']
  wheel_drops = ['wheel-drop-caster','wheel-drop-left','wheel-drop-right']
  cliffs = ['cliff-right','cliff-front-right','cliff-front-left','cliff-left']

  def __init__(self, tty='/dev/ttyUSB0'):
    super(Create, self).__init__(tty)
    self.sci.AddOpcodes(CREATE_OPCODES)
    self.sensors = CreateSensors(self)
    self.safe = False  # Use full mode for control.

  def start(self):
    logging.debug('Starting up the Rtbot.')
    self.SoftReset()
    self.Control()

#=============================================================
# Robot Functions
#=============================================================

  def safe_drive(self, command):
    try:
      # check that the command is a dictionary
      command_dict = eval(command)
      if not type(command_dict) is dict or len(command_dict) == 0:
        raise Exception("Command is not a dictionary")

      # check that the command has a valid command inside
      cmd = command_dict.get('command', None)
      if cmd == None or cmd == 'shutdown':
        raise Exception("No valid command exists or command was to shutdown")

      # create conditions based on the given command dictionary
      conditions = {}
      if cmd == 'forward':
        conditions['velocity'] = min(command_dict.get('velocity', MAX_FORWARD_VELOCITY), MAX_FORWARD_VELOCITY)
        conditions['radius'] = command_dict.get('radius', RADIUS_STRAIGHT)
        conditions['sonar'] = max(command_dict.get('sonar', MIN_SONAR_DISTANCE), MIN_SONAR_DISTANCE)
      elif cmd == 'left':
        conditions['velocity'] = min(command_dict.get('velocity', MAX_TURN_VELOCITY), MAX_TURN_VELOCITY)
        conditions['radius'] = command_dict.get('radius', RADIUS_TURN_IN_PLACE_CCW)
      elif cmd == 'right':
        conditions['velocity'] = min(command_dict.get('velocity', MAX_TURN_VELOCITY), MAX_TURN_VELOCITY)
        conditions['radius'] = command_dict.get('radius', RADIUS_TURN_IN_PLACE_CW)
      elif cmd == 'stop':
        conditions['velocity'] = 0
      else:
        print "command not recognized!"
        return None

      # add stop conditions in they are in the command
      if 'distance' in command:
        conditions['distance'] = abs(command_dict.get('distance', 0))
      if 'angle' in command:
        conditions['angle'] = abs(command_dict.get('angle', 0))

      #perform a conditional drive with safe conditions
      stop_state = self.conditional_drive(conditions)
      
      # back up if we bumped into something or are at a cliff
      if stop_state['stop_reason'] == "bump" or stop_state['stop_reason'] == "cliff":
        backup_state = self.back_up()
        stop_state['distance_traveled'] -= backup_state['distance_traveled']
        stop_state['sonar'] = backup_state['sonar']
      # make a sound if we have a wheel drop to alert user of our situation
      elif stop_state['stop_reason'] == "wheel_drop":
        self.play_sound()

      return stop_state

    except Exception as exception:
      print exception
    
    return None

  # Drive based on a set of conditions
  def conditional_drive(self, conditions):
    try:
      velocity = conditions.get('velocity', 0)
      radius = conditions.get('radius', RADIUS_STRAIGHT)
      self.distance_traveled = 0
      self.degrees_rotated = 0
      self.sensors.GetAll()
      stop_reason = self.should_keep_driving(conditions)
      if not stop_reason:
        self.Drive(velocity, radius)
      while not stop_reason:
        self.distance_traveled += abs(self.sensors.data['distance'])
        self.degrees_rotated += abs(self.sensors.data['angle'])
        self.sensors.GetAll()
        stop_reason = self.should_keep_driving(conditions)
    except Exception as exception:
      stop_reason = 'exception'
      print exception
    finally:
      if stop_reason != 'interrupt':
        self.Stop()
      self.stop_state = { "stop_reason": stop_reason, "distance_traveled":self.distance_traveled, "degrees_rotated": self.degrees_rotated, "sonar": self.sensors.data['user-analog-input'],"velocity": velocity, "radius": radius }
      return self.stop_state

  # Check if the robot should keep driving based on current sensor conditions
  def should_keep_driving(self, conditions):
    if len(COMMANDS) > 0:
      return 'interrupt'

    # bumps
    if not 'ignore_bump' in conditions or not conditions['ignore_bump']:
      for bump in self.bumps:
        if self.sensors.data[bump]:
          print bump
          return 'bump'

    # cliffs
    if not 'ignore_cliff' in conditions or not conditions['ignore_cliff']:
      for cliff in self.cliffs:
        if self.sensors.data[cliff]:
          print cliff
          return 'cliff'

    # wheel drops
    if not 'ignore_wheel_drop' in conditions or not conditions['ignore_wheel_drop']:
      for wheel_drop in self.wheel_drops:
        if self.sensors.data[wheel_drop]:
          print wheel_drop
          return 'wheel_drop'

    # sonar
    #print self.sensors.data['user-analog-input']
    if 'sonar' in conditions and self.sensors.data['user-analog-input'] < conditions['sonar']:
      print 'Sonar {0} {1}'.format(self.sensors.data['user-analog-input'], conditions['sonar'])
      return 'sonar'

    # distance traveled
    if 'distance' in conditions and self.distance_traveled >= conditions['distance']:
      print 'Traveled {0}'.format(self.distance_traveled)
      return 'distance'

    # angle
    if 'angle' in conditions and self.degrees_rotated >= conditions['angle']:
      print 'Rotated {0}'.format(self.degrees_rotated)
      return 'angle'

    # Keep Driving
    return None

  def back_up(self):
    try:
      self.Drive(-100, RADIUS_STRAIGHT)
      self.distance_traveled = 0
      self.sensors.GetAll()
      while self.should_keep_backing_up():
        self.distance_traveled += abs(self.sensors.data['distance'])
        self.sensors.GetAll()
    except Exception as exception:
      print exception
    finally:
      self.Stop()
      self.stop_state = { "distance_traveled":self.distance_traveled, "sonar": self.sensors.data['user-analog-input'] }
      return self.stop_state

  def should_keep_backing_up(self):

    # wheel drop
    for wheel_drop in self.wheel_drops:
      if self.sensors.data[wheel_drop]:
        return False

    # bumps
    for bump in self.bumps:
      if self.sensors.data[bump]:
        return True

    # cliffs
    for cliff in self.cliffs:
      if self.sensors.data[cliff]:
        return True
    return False

  def play_sound(self):
    # TODO: play a horrible death noise
    print "beep"
