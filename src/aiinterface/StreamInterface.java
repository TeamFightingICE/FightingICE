package aiinterface;

import informationcontainer.RoundResult;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;

public interface StreamInterface {

	void initialize(GameData gameData);
	void getInformation(FrameData frameData);
	void getAudioData(AudioData audioData);
	void getScreenData(ScreenData screenData);
	void processing();
	void roundEnd(RoundResult roundResult);
	void close();
	
}
