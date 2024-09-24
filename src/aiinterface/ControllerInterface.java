package aiinterface;

import informationcontainer.RoundResult;
import struct.AudioData;
import struct.FrameData;
import struct.ScreenData;

public interface ControllerInterface {
	
	public void setFrameData(FrameData fd, ScreenData sd, AudioData ad);
	public void informRoundResult(RoundResult roundResult);
	public void gameEnd();
	public void clear();
	
}
