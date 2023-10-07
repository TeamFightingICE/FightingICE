package grpc;

import informationcontainer.RoundResult;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;

public class ObserverGameState implements Comparable<ObserverGameState> {

	private StateFlag stateFlag;
	
	private GameData gameData;
	private FrameData frameData;
	private ScreenData screenData;
	private AudioData audioData;
	private RoundResult roundResult;
	
	public ObserverGameState() {
		this.stateFlag = StateFlag.CANCELLED;
	}
	
	public ObserverGameState(GameData gameData) {
		this.stateFlag = StateFlag.INITIALIZE;
		this.gameData = gameData;
	}
	
	public ObserverGameState(FrameData frameData, ScreenData screenData, AudioData audioData) {
		this.stateFlag = StateFlag.PROCESSING;
		this.frameData = frameData;
		this.screenData = screenData;
		this.audioData = audioData;
	}
	
	public ObserverGameState(RoundResult roundResult) {
		this.stateFlag = StateFlag.ROUND_END;
		this.roundResult = roundResult;
	}
	
	public StateFlag getStateFlag() {
		return this.stateFlag;
	}
	
	public GameData getGameData() {
		return this.gameData;
	}
	
	public FrameData getFrameData() {
		return this.frameData;
	}
	
	public ScreenData getScreenData() {
		return this.screenData;
	}
	
	public AudioData getAudioData() {
		return this.audioData;
	}
	
	public RoundResult getRoundResult() {
		return this.roundResult;
	}

	@Override
	public int compareTo(ObserverGameState o) {
		return this.stateFlag.getPriority() - o.stateFlag.getPriority();
	}
	
}
