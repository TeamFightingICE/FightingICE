from py4j.java_gateway import get_field

class KickAI(object):
    def __init__(self, gateway):
        self.gateway = gateway
        
    def close(self):
        pass
        
    def getInformation(self, frameData, isControl):
        # Getting the frame data of the current frame
        self.frameData = frameData
        self.cc.setFrameData(self.frameData, self.player)
    # please define this method when you use FightingICE version 3.20 or later
    def roundEnd(self, x, y, z):
    	print(x)
    	print(y)
    	print(z)
    	
    # please define this method when you use FightingICE version 4.00 or later
    def getScreenData(self, sd):
    	pass
        
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
        if self.frameData.getEmptyFlag() or self.frameData.getRemainingFramesNumber() <= 0:
                self.isGameJustStarted = True
                return
                
        if self.cc.getSkillFlag():
                self.inputKey = self.cc.getSkillKey()
                return
            
        self.inputKey.empty()
        self.cc.skillCancel()     

        # Just spam kick
        self.cc.commandCall("B")
                        
    # This part is mandatory
    class Java:
        implements = ["aiinterface.AIInterface"]
        
