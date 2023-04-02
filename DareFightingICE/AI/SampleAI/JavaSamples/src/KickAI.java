import aiinterface.AIInterface;
import aiinterface.CommandCenter;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.RoundResult;
import struct.ScreenData;

public class KickAI implements AIInterface {

	private boolean blindFlag;
	private boolean playerNumber;
	private FrameData frameData;
	private Key key;
	private CommandCenter cc;
	
	public KickAI() {
		this.blindFlag = false;
	}
	
	@Override
	public String name() {
		return this.getClass().getName();
	}
	
	@Override
	public boolean isBlind() {
		return this.blindFlag;
	}

	@Override
	public void initialize(GameData gameData, boolean playerNumber) {
		this.playerNumber = playerNumber;
		
		this.key = new Key();
		this.cc = new CommandCenter();
	}
	
	@Override
	public void getInformation(FrameData frameData, boolean isControl, FrameData nonDelay) {
		this.frameData = frameData;
		this.cc.setFrameData(frameData, playerNumber);
	}
	
	@Override
	public void getScreenData(ScreenData screenData) {
		
	}

	@Override
	public void getAudioData(AudioData audioData) {
		
	}

	@Override
	public Key input() {
		return this.key;
	}

	@Override
	public void processing() {
		if (frameData.getEmptyFlag() || frameData.getFramesNumber() <= 0) {
			return;
		}
		
		if (cc.getSkillFlag()) {
			key = cc.getSkillKey();
		} else {
			key.empty();
			cc.skillCancel();
			
			cc.commandCall("B");
		}
	}
	
	@Override
	public void roundEnd(RoundResult roundResult) {
		System.out.println(roundResult.getRemainingHPs()[0] + " " + roundResult.getRemainingHPs()[1] + " " + roundResult.getElapsedFrame());
	}
	
	@Override
	public void gameEnd() {
		System.out.println("game end");
	}
	
}
