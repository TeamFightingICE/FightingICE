from py4j.java_gateway import get_field

class Machete(object):
	def __init__(self, gateway):
		self.gateway = gateway

		self.close()

	def getCharacter(self):
		# Select the player ZEN as per 2015 competition rules
		return "ZEN"

	def close(self):
		# Reset all members
		self.inputKey = None
		self.player = None
		self.frameData = None
		self.cc = None
		self.simulator = None
		self.gameData = None

	def getInformation(self, frameData):
		# Load the frame data every time getInformation gets called
		self.frameData = frameData
		
    # please define this method when you use FightingICE version 3.20 or later
	def roundEnd(self, x, y, z):
		print(x)
		print(y)
		print(z)
        
	def initialize(self, gameData, player):
		# Initialize the global variables at the start of the round
		self.inputKey = self.gateway.jvm.structs.Key()
		self.player = player
		self.frameData = self.gateway.jvm.structs.FrameData()
		self.cc = self.gateway.jvm.commandcenter.CommandCenter()
		self.gameData = gameData
		self.simulator = self.gameData.getSimulator()
		self.isGameJustStarted = True

		return 0

	def input(self):
		# The input is set up to the global variable inputKey

		# which is modified in the processing part
		return self.inputKey

	def processing(self):

		# First we check whether we are at the end of the round
		if self.frameData.getEmptyFlag() or self.frameData.getRemainingTime() <= 0:
			self.isGameJustStarted = True
			return

		if not self.isGameJustStarted:
			# Simulate the delay and look ahead 2 frames. The simulator class exists already in FightingICE
			self.frameData = self.simulator.simulate(self.frameData, self.player, None, None, 17)
			
			#You can pass actions to the simulator by writing as follows:
			#actions = self.gateway.jvm.java.util.ArrayDeque()
			#actions.add(self.gateway.jvm.enumerate.Action.STAND_A)
			#self.frameData = self.simulator.simulate(self.frameData, self.player, actions, actions, 17)
		else:
			# If the game just started, no point on simulating
			self.isGameJustStarted = False

		self.cc.setFrameData(self.frameData, self.player)

		distance = self.cc.getDistanceX()

		my = self.cc.getMyCharacter()
		energy = my.getEnergy()
		my_x = get_field(my, 'x')
		my_state = get_field(my, "state")

		opp = self.cc.getEnemyCharacter()
		opp_x = get_field(opp, 'x')
		opp_state = get_field(opp, "state")

		xDifference = my_x - opp_x

		if self.cc.getskillFlag():
			# If there is a previous "command" still in execution, then keep doing it
			self.inputKey = self.cc.getSkillKey()
			return

		# We empty the keys and cancel skill just in case
		self.inputKey.empty()
		self.cc.skillCancel()
		
		# Following is the brain of the reflex agent. It determines distance to the enemy

		# and the energy of our agent and then it performs an action
		if (get_field(opp, "energy") >= 300) and ((get_field(my, "hp") - get_field(opp, "hp")) <= 300):
			# If the opp has 300 of energy, it is dangerous, so better jump!!

			# If the health difference is high we are dominating so we are fearless :)
			self.cc.commandCall("FOR_JUMP _B B B")
		elif not my_state.equals(self.gateway.jvm.enumerate.State.AIR) and not my_state.equals(self.gateway.jvm.enumerate.State.DOWN):
			# If not in air
			if distance > 150:
				# If its too far, then jump to get closer fast
				self.cc.commandCall("FOR_JUMP")
			elif energy >= 300:
				# High energy projectile
				self.cc.commandCall("STAND_D_DF_FC")
			elif (distance > 100) and (energy >= 50):
				# Perform a slide kick
				self.cc.commandCall("STAND_D_DB_BB")
			elif opp_state.equals(self.gateway.jvm.enumerate.State.AIR): # If enemy on Air
				# Perform a big punch
				self.cc.commandCall("STAND_F_D_DFA")
			elif distance > 100:
				# Perform a quick dash to get closer
				self.cc.commandCall("6 6 6")
			else:
				# Perform a kick in all other cases, introduces randomness
				self.cc.commandCall("B")
		elif ((distance <= 150) and (my_state.equals(self.gateway.jvm.enumerate.State.AIR) or my_state.equals(self.gateway.jvm.enumerate.State.DOWN))
			and (((self.gameData.getStageXMax() - my_x) >= 200) or (xDifference > 0)) 
			and ((my_x >= 200) or xDifference < 0)):
			# Conditions to handle game corners
			if energy >= 5:
				# Perform air down kick when in air
				self.cc.commandCall("AIR_DB")
			else:
				# Perform a kick in all other cases, introduces randomness
				self.cc.commandCall("B")
		else:
			# Perform a kick in all other cases, introduces randomness
			self.cc.commandCall("B")

	class Java:
		implements = ["gameInterface.AIInterface"]