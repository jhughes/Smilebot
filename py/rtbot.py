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
BUFFER_SIZE = 1024
COMMANDS = deque([])
ACK = "ACK\n"
CONFIG_CMD = "__cfg_"
INTERRUPT_RECEIVED = False

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

  # Drive safely based on a set of conditions
  def SafeDrive(self, conditions):
    INTERRUPT_RECEIVED = False
    velocity = conditions.get('velocity', VELOCITY_SLOW)
    radius = conditions.get('radius', RADIUS_STRAIGHT)
    if 'radius' in conditions:
      radius = conditions['radius']
    else:
      radius = RADIUS_STRAIGHT
    try:
      self.Drive(velocity, radius)
      self.distance_traveled = 0
      self.degrees_rotated = 0
      self.sensors.GetAll()
      stop_reason = self.ShouldKeepDriving(conditions)
      while not stop_reason:
        self.sensors.GetAll()
        self.distance_traveled += abs(self.sensors.data['distance']) # in case we're going backwards we get the magnitude of distance traveled
        self.degrees_rotated += abs(self.sensors.data['angle'])
        stop_reason = self.ShouldKeepDriving(conditions)
    except Exception as exception:
      stop_reason = 'exception'
      print exception
    finally:
      self.Stop()
      self.stop_state = { "stop_reason": stop_reason, "distance_traveled":self.distance_traveled, "degrees_rotated": self.degrees_rotated, "velocity": velocity, "radius": radius }
      return self.stop_state

  # Check if the robot should keep driving based on current sensor conditions
  # What if we're just turning in place? Should we only check sonar and distance traveled if we are moving forward?
  # what about cliff and bump sensors while going backwards?
  def ShouldKeepDriving(self, conditions):
    if len(COMMANDS) > 0:
      return 'interrupt'

    # bumps
    if not 'ignore_bump' in conditions or not conditions['ignore_bump']:
      for bump in self.bumps:
        if self.sensors.data[bump]:
          print bump
          return 'bump'

    # wheel drops
    if not 'ignore_wheel_drop' in conditions or not conditions['ignore_wheel_drop']:
      for wheel_drop in self.wheel_drops:
        if self.sensors.data[wheel_drop]:
          print wheel_drop
          return 'wheel_drop'

    # cliffs
    if not 'ignore_cliff' in conditions or not conditions['ignore_cliff']:
      for cliff in self.cliffs:
        if self.sensors.data[cliff]:
          print cliff
          return 'cliff'

    # sonar
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
