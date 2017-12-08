
Here is how to get started quickly. If you need more infomation, please read below contents.

1. Update FightingICE.jar

2. Add py4j.jar into lib and set path on your eclipse

3. Start the FightingICE with argument “—py4j”

4. Execute Main~.py 
	e.g.) python Main_PyAIvsPyAI.py -n 3
	In this, case, you are able to do 3 games repeatedly.




//——————————————————————————————————————————————————————————————————//

In FightingICE you can control the launching of games and the AIs in Python with PYJ4.
You just need to use these arguments to launch the Java application.

--py4j --port PORT_NUMBER

The port is optional, by default it's 4242.
Now FightingICE is expecting that you launch the python application. (You can also directly launch the Java application from Python)

Here is the base of the python code that connect to the gateway server (on port 4242) and get back the manager. 

from py4j.java_gateway import JavaGateway, GatewayParameters, CallbackServerParameters, get_field
gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
manager = gateway.entry_point

The python AIs just use the same interface that the java's one (AIInterface)
And you can create a basic AI like that.

class KickAI(object):
	def __init__(self, gateway):
		self.gateway = gateway

	def close(self):
		pass
	
	# Please define this method when you use FightingICE version 3.20 or later
	def roundEnd(self, p1Hp, p2Hp, frames):
    	pass
    	
	# Please define this method when you use FightingICE version 4.00 or later
	def getScreenData(self, screenData):
    	pass

	def getInformation(self, frameData):
		# Getting the frame data of the current frame
		self.frameData = frameData

	def initialize(self, gameData, player):
		# Initializng the command center, the simulator and some other things
		self.inputKey = self.gateway.jvm.struct.Key()
		self.frameData = self.gateway.jvm.struct.FrameData()
		self.cc = self.gateway.jvm.aiinterface.CommandCenter()

		self.player = player
		self.gameData = gameData
		self.simulator = self.gameData.getSimulator()

		return 0

	def input(self):
		# Return the input for the current frame
		return self.inputKey

	def processing(self):
		# Just compute the input for the current frame
		if self.frameData.getEmptyFlag() or self.frameData.getRemainingTime() <= 0:
			self.isGameJustStarted = True
			return

		self.cc.setFrameData(self.frameData, self.player)

		if self.cc.getSkillFlag():
			self.inputKey = self.cc.getSkillKey()
			return

		# Just spam kick
		self.cc.commandCall("B")

	# This part is mandatory
	class Java:
		implements = ["aiinterface.AIInterface"]

Now that you have your AI, you have to register it to the manager like that.

manager.registerAI("KickAI", KickAI(gateway))

And with that you can just start a new game or a series of games.

print("Start game")

game = manager.createGame("ZEN", "ZEN", "Machete", "KickAI", 3)
manager.runGame(game)

print("After game")
sys.stdout.flush()

print("End of games")
gateway.close_callback_server()
gateway.close()

The method runGame will just wait the end of the game before returning, and you can't launch multiple games in parrallel on the same Java application.