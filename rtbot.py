from pyrobot import *
import sys
import logging
import time
#=============================================================
# put defines here e.x.
# define_name = define value
#=============================================================
# define the Rtbot class to init and start itself
class Rtbot(Create):

  distance_traveled = 0
  degrees_rotated = 0
  stop_cases = ['bump-left','bump-right','wheel-drop-caster','wheel-drop-left','wheel-drop-right','cliff-right','cliff-front-right','cliff-front-left','cliff-left']

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
    velocity = conditions['velocity'] or VELOCITY_SLOW
    radius = conditions['radius'] or RADIUS_STRAIGHT
    try:
      self.Drive(velocity, radius)
      distance_traveled = 0
      degrees_rotated = 0
      keep_driving, stop_reason = self.ShouldKeepDriving(conditions)
      while keep_driving:
        self.sensors.GetAll()
        distance_traveled += abs(self.sensors.data['distance']) # in case we're going backwards we get the magnitude of distance traveled
        degrees_rotated += abs(self.sensors.data['angle'])
        keep_driving, stop_reason = self.ShouldKeepDriving(conditions)
    except Exception as exception:
      stop_reason = 'exception'
      print exception
    finally:
      print 'Stopping'
      self.Stop()
      return stop_reason

  # Check if the robot should keep driving based on current sensor conditions
  # What if we're just turning in place? Should we only check sonar and distance traveled if we are moving forward?
  def ShouldKeepDriving(self, conditions):
    # bumps, wheel drops, cliffs
    for stop_case in stop_cases:
      if self.sensors.data[stop_case]:
        print stop_case
        return False, stop_case

    # sonar
    if 'sonar' in conditions and self.sensors.data['user-analog-input'] < conditions['sonar']:
      print 'Sonar {0} {1}'.format(self.sensors.data['user-analog-input'], conditions['sonar'])
      return False, 'sonar'

    # distance traveled
    if 'distance' in conditions and distance_traveled >= conditions['distance']:
      print 'Traveled {0}'.format(distance_traveled)
      return False, 'distance'

    # angle
    if 'angle' in conditions and degrees_rotated >= conditions['angle']:
      print 'Rotated {0}'.format(degrees_rotated)
      return False, 'angle'

    # Keep Driving
    return True, 'none'
