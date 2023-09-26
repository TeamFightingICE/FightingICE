package grpc;

import informationcontainer.RoundResult;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import protoc.EnumProto.GrpcFlag;
import protoc.ServiceProto.SpectateRequest;
import protoc.ServiceProto.SpectatorGameState;
import setting.GameSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;
import util.GrpcUtil;

public class ObserverAgent {
	
	private int interval;
	private boolean cancelled;
	private StreamObserver<SpectatorGameState> responseObserver;
	
	private GameData gameData;
	private FrameData frameData;
	private AudioData audioData;
	private ScreenData screenData;
	
	public ObserverAgent() {
		this.cancelled = true;
		this.frameData = new FrameData();
		this.audioData = new AudioData();
		this.screenData = new ScreenData();
	}
	
	public void initialize(GameData gameData) {
		this.gameData = gameData;
		this.onInitialize();
	}
	
	public void register(SpectateRequest request, StreamObserver<SpectatorGameState> responseObserver) {
		if (!this.isCancelled()) {
			this.notifyOnCompleted();
			this.cancel();
		}
		
		((ServerCallStreamObserver<SpectatorGameState>) responseObserver).setOnCancelHandler(new Runnable() {
			@Override
			public void run() {
				ObserverAgent.this.cancel();
			}
		});
		this.interval = request.getInterval();
		this.cancelled = false;
		this.responseObserver = responseObserver;
	}
	
	public int getInterval() {
		return this.interval;
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public void cancel() {
		this.responseObserver = null;
		this.cancelled = true;
	}
	
	public void notifyOnCompleted() {
		if (!this.isCancelled()) {
			this.responseObserver.onCompleted();
		}
	}
	
	public void setInformation(FrameData frameData, AudioData audioData, ScreenData screenData) {
		this.frameData = frameData;
		this.screenData = screenData;
		this.audioData = audioData;
	}
	
	public void onInitialize() {
		SpectatorGameState response = SpectatorGameState.newBuilder()
  				.setStateFlag(GrpcFlag.INITIALIZE)
  				.setGameData(GrpcUtil.convertGameData(gameData))
  				.build();
		this.onNext(response);
	}
	
	public void onGameUpdate() {
		if (frameData.getFramesNumber() % this.getInterval() == 0) {
			SpectatorGameState response = SpectatorGameState.newBuilder()
	  				.setStateFlag(GrpcFlag.PROCESSING)
	  				.setFrameData(GrpcUtil.convertFrameData(frameData))
	  				.setScreenData(GrpcUtil.convertScreenData(screenData, 960, 640, false))
	  				.setAudioData(GrpcUtil.convertAudioData(audioData))
	  				.build();
			this.onNext(response);
		}
	}
	
	public void onRoundEnd(RoundResult roundResult) {
		boolean isGameEnd = roundResult.getRound() >= GameSetting.ROUND_MAX;
		
		SpectatorGameState response = SpectatorGameState.newBuilder()
				.setStateFlag(isGameEnd ? GrpcFlag.GAME_END : GrpcFlag.ROUND_END)
				.setRoundResult(GrpcUtil.convertRoundResult(roundResult))
  				.build();
		this.onNext(response);
		
		this.frameData = new FrameData();
	}
	
	public void onNext(SpectatorGameState state) {
		if (!this.isCancelled()) {
			this.responseObserver.onNext(state);
		}
	}

}
