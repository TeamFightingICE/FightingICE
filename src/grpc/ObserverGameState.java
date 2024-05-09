package grpc;

import informationcontainer.RoundResult;
import protoc.EnumProto.GrpcFlag;
import protoc.ServiceProto.SpectatorGameState;
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
	
	private ObserverGameState(StateFlag stateFlag) {
		this.stateFlag = stateFlag;
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
	
	public SpectatorGameState toProto() {
		SpectatorGameState response = null;
		
		if (this.getStateFlag() == StateFlag.INITIALIZE) {
			response = SpectatorGameState.newBuilder()
	  				.setStateFlag(GrpcFlag.INITIALIZE)
	  				.setGameData(this.gameData.toProto())
	  				.build();
		} else if (this.getStateFlag() == StateFlag.INIT_ROUND) {
			response = SpectatorGameState.newBuilder()
					.setStateFlag(GrpcFlag.INIT_ROUND)
					.build();
		} else if (this.getStateFlag() == StateFlag.PROCESSING) {
			response = SpectatorGameState.newBuilder()
					.setStateFlag(GrpcFlag.PROCESSING)
					.setFrameData(this.frameData.toProto())
					.build();
		} else if (this.getStateFlag() == StateFlag.ROUND_END) {
			response = SpectatorGameState.newBuilder()
					.setStateFlag(GrpcFlag.ROUND_END)
					.setRoundResult(this.roundResult.toProto())
	  				.build();
		} else if (this.getStateFlag() == StateFlag.GAME_END) {
			response = SpectatorGameState.newBuilder()
					.setStateFlag(GrpcFlag.GAME_END)
					.build();
		}
		
		return response;
	}

	@Override
	public int compareTo(ObserverGameState o) {
		return this.stateFlag.getPriority() - o.stateFlag.getPriority();
	}
	
	public static ObserverGameState newCancelledState() {
		return new ObserverGameState(StateFlag.CANCELLED);
	}
	
	public static ObserverGameState newInitializeState(GameData gameData) {
		return new ObserverGameState(gameData);
	}
	
	public static ObserverGameState newInitRoundState() {
		return new ObserverGameState(StateFlag.INIT_ROUND);
	}
	
	public static ObserverGameState newProcessingState(FrameData frameData, ScreenData screenData, AudioData audioData) {
		return new ObserverGameState(frameData, screenData, audioData);
	}
	
	public static ObserverGameState newRoundEndState(RoundResult roundResult) {
		return new ObserverGameState(roundResult);
	}
	
	public static ObserverGameState newGameEndState() {
		return new ObserverGameState(StateFlag.GAME_END);
	}
	
}
