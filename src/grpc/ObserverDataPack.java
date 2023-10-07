package grpc;

import informationcontainer.RoundResult;
import protoc.EnumProto.GrpcFlag;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;

public class ObserverDataPack {

	private GrpcFlag grpcFlag;
	
	private GameData gameData;
	private FrameData frameData;
	private ScreenData screenData;
	private AudioData audioData;
	private RoundResult roundResult;
	
	public ObserverDataPack(GameData gameData) {
		this.grpcFlag = GrpcFlag.INITIALIZE;
		this.gameData = gameData;
	}
	
	public ObserverDataPack(FrameData frameData, ScreenData screenData, AudioData audioData) {
		this.grpcFlag = GrpcFlag.PROCESSING;
		this.frameData = frameData;
		this.screenData = screenData;
		this.audioData = audioData;
	}
	
	public ObserverDataPack(RoundResult roundResult) {
		this.grpcFlag = GrpcFlag.ROUND_END;
		this.roundResult = roundResult;
	}
	
	public GrpcFlag getGrpcFlag() {
		return this.grpcFlag;
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
	
}
