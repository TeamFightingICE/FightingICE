from py4j.java_gateway import get_field

class DisplayInfo(object):
    def __init__(self, gateway):
        self.gateway = gateway

        self.width = 96 # The width of the display to obtain
        self.height = 64 # The height of the display to obtain
        self.grayscale = True # The display's color to obtain true for grayscale, false for RGB
        
    def close(self):
        pass

    def initialize(self, gameData, player):
        self.inputKey = self.gateway.jvm.struct.Key()
        self.frameData = self.gateway.jvm.struct.FrameData()
        self.cc = self.gateway.jvm.aiinterface.CommandCenter()
            
        self.player = player
        self.gameData = gameData
                
        return 0
        
    # please define this method when you use FightingICE version 3.20 or later
    def roundEnd(self, x, y, z):
        print(x)
        print(y)
        print(z)
        
    # Please define this method when you use FightingICE version 4.00 or later
    def getScreenData(self, sd):
        self.screenData = sd
        
    def getInformation(self, frameData):
        self.frameData = frameData
        self.cc.setFrameData(self.frameData, self.player)
        
    def input(self):
        return self.inputKey
        
    def processing(self):

        if self.frameData.getEmptyFlag() or self.frameData.getRemainingFramesNumber() <= 0:
            self.isGameJustStarted = True
            return
  
        if self.cc.getSkillFlag():
            self.inputKey = self.cc.getSkillKey()
            return

        self.inputKey.empty()
        self.cc.skillCancel()

        # get display pixel data
        displayBuffer = self.screenData.getDisplayByteBufferAsBytes(self.width, self.height, self.grayscale)

        # calcultate the distance
        distance = self.calculateDistance(displayBuffer)

        if distance == -1:
            self.cc.commandCall("STAND_A") # default action
        else:
            close = 80 * self.width / 960
            far = 200 * self.width / 960

            # conduct action according to the distance based on pixel data
            if distance < close:
                self.cc.commandCall("CROUCH_B")
            elif distance >= close and distance < far:
                self.cc.commandCall("STAND_FB")
            else:
                self.cc.commandCall("STAND_D_DF_FA")
                        
    def calculateDistance(self, displayBuffer):
        previousPixel = 0
        leftCharacterX = -1
        rightCharacterX = -1

        for y in range(self.height):
            # when searching for the same row is over, reset each data
            previousPixel = 0
            leftCharacterX = -1
            rightCharacterX = -1

            for x in range(self.width):
                currentPixel = displayBuffer[y * self.width + x]

                # record x coordinate of the character on right side
                if currentPixel and previousPixel == 0 and leftCharacterX != -1:
                    rightCharacterX = x - 1
                    return abs(leftCharacterX - rightCharacterX)

                # record x coordinate of the character on left side
                if previousPixel and currentPixel == 0:
                    leftCharacterX = x - 1

                # update pixel data
                previousPixel = currentPixel

        return -1

    # This part is mandatory
    class Java:
        implements = ["aiinterface.AIInterface"]
