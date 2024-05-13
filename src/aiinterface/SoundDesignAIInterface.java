package aiinterface;

import informationcontainer.RoundResult;
import struct.FrameData;
import struct.GameData;

public interface SoundDesignAIInterface {
	
	void initialize(GameData gameData);
	void getInformation(FrameData frameData);
	void processing();
	void roundEnd(RoundResult roundResult);
	byte[] input();
	void close();
	
}
