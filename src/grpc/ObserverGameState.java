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
	
	private ObserverGameState() {
		this.stateFlag = StateFlag.CANCELLED;
	}
	
	private ObserverGameState(GameData gameData) {
		this.stateFlag = StateFlag.INITIALIZE;
		this.gameData = gameData;
	}
	
	private ObserverGameState(FrameData frameData, ScreenData screenData, AudioData audioData) {
		this.stateFlag = StateFlag.PROCESSING;
		this.frameData = frameData;
		this.screenData = screenData;
		this.audioData = audioData;
	}
	
	private ObserverGameState(RoundResult roundResult) {
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
	
	public static ObserverGameState newCancelledState() {
		return new ObserverGameState();
	}
	
	public static ObserverGameState newInitializeState(GameData gameData) {
		return new ObserverGameState(gameData);
	}
	
	public static ObserverGameState newProcessingState(FrameData frameData, ScreenData screenData, AudioData audioData) {
		return new ObserverGameState(frameData, screenData, audioData);
	}
	
	public static ObserverGameState newRoundEndState(RoundResult roundResult) {
		return new ObserverGameState(roundResult);
	}
	
}
