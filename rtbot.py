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
#place further functions in the Rtbot class e.x.
# def somefunction(some_argvs):
#   some code
#=============================================================

	# Drive forward safely
	def SafeDrive(self, velocity, radius, conditions):
		print 'Driving'
		self.Drive(velocity, radius)
		total_distance = 0
		try:
			while True:
				self.sensors.GetAll()
				total_distance += self.sensors.data['distance']
				conditions['total distance'] = total_distance
				if self.ShouldStopDriving(conditions):
					print 'Stopping'
					self.Stop()
					break
		except Exception as exception:
			print exception
			self.Stop()

	# Check if the robot should stop based on current sensor conditions
	def ShouldStopDriving(self, conditions):
		# bump
		if self.sensors.GetBump():
			print 'bump'
			return True

		# distance traveled
		if 'distance' in conditions and conditions['total distance'] >= conditions['distance']:
			print 'distance {0} reached'.format(conditions['distance'])
			return True

		# sonar
		if 'sonar' in conditions and self.sensors.data['sonar'] < conditions['sonar']:
			print '{0} {1}'.format(self.sensors.data['sonar'], conditions['sonar'])
			return True

		# cliff

		# wheel drop

		# finally
		return False
