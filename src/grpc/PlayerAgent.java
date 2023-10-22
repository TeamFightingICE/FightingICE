package grpc;

import java.util.UUID;

import informationcontainer.RoundResult;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import manager.InputManager;
import protoc.EnumProto.GrpcFlag;
import protoc.ServiceProto.InitializeRequest;
import protoc.ServiceProto.PlayerGameState;
import protoc.ServiceProto.PlayerInput;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.ScreenData;
import util.GrpcUtil;

public class PlayerAgent {
	
	private UUID playerUuid;
	private boolean cancelled;
	
	private boolean playerNumber;
	private String playerName;
	private boolean blind;
	
	private boolean isControl;
	private FrameData frameData;
	private AudioData audioData;
	private ScreenData screenData;
	private FrameData nonDelayFrameData;
	
	private StreamObserver<PlayerGameState> responseObserver;
	private boolean waitFlag;
	
	public PlayerAgent() {
		this.playerUuid = UUID.randomUUID();
		this.cancelled = true;

		this.waitFlag = false;
		
		this.isControl = false;
		this.frameData = new FrameData();
		this.audioData = new AudioData();
		this.screenData = new ScreenData();
	}
	
	public void initializeRPC(InitializeRequest request) {
		this.playerName = request.getPlayerName();
		this.blind = request.getIsBlind();
	}
	
	public void participateRPC(StreamObserver<PlayerGameState> responseObserver) {
		if (!this.isCancelled()) {
			this.notifyOnCompleted();
		}
		
		((ServerCallStreamObserver<PlayerGameState>) responseObserver).setOnCancelHandler(new Runnable() {
			@Override
			public void run() {
				PlayerAgent.this.cancel();
			}
		});
		this.cancelled = false;
		this.responseObserver = responseObserver;
	}
	
	public void initialize(GameData gameData, boolean playerNumber) {
		this.playerNumber = playerNumber;
		this.onInitialize(gameData);
	}
	
	public UUID getPlayerUuid() {
		return this.playerUuid;
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public String getPlayerName() {
		return this.playerName;
	}
	
	public boolean isBlind() {
		return this.blind || LaunchSetting.noVisual[this.playerNumber ? 0 : 1];
	}
	
	public boolean isGameStarted() {
		return !this.frameData.getEmptyFlag() && this.frameData.getFramesNumber() > 0;
	}
	
	public boolean isReady() {
		return !this.waitFlag;
	}
	
	public void cancel() {
		this.responseObserver = null;
		this.cancelled = true;
	}
	
	public void notifyOnCompleted() {
		if (!this.isCancelled()) {
			this.responseObserver.onCompleted();
			this.cancel();
		}
	}
	
	public void setInformation(boolean isControl, FrameData frameData, AudioData audioData, 
			ScreenData screenData, FrameData nonDelayFrameData) {
		this.isControl = isControl;
		this.frameData = frameData;
		this.nonDelayFrameData = nonDelayFrameData;
		this.screenData = screenData;
		this.audioData = audioData;
		if (this.isBlind()) {
			this.frameData.removeVisualData();
			this.screenData = null;
		}
	}
	
	public void onInitialize(GameData gameData) {
		PlayerGameState response = PlayerGameState.newBuilder()
				.setStateFlag(GrpcFlag.INITIALIZE)
				.setGameData(GrpcUtil.convertGameData(gameData))
  				.build();
		this.onNext(response);
	}
	
	public void onGameUpdate() {
		if (this.isReady() && this.isGameStarted()) {
			this.waitFlag = true;
		}
		
		PlayerGameState.Builder response = PlayerGameState.newBuilder()
				.setStateFlag(GrpcFlag.PROCESSING)
				.setIsControl(isControl)
  				.setFrameData(GrpcUtil.convertFrameData(frameData))
  				.setScreenData(GrpcUtil.convertScreenData(screenData, 96, 64, true))
  				.setAudioData(GrpcUtil.convertAudioData(audioData));
		if (LaunchSetting.nonDelay[playerNumber ? 0 : 1]) {
			response.setNonDelayFrameData(GrpcUtil.convertFrameData(nonDelayFrameData));
		}
		this.onNext(response.build());
	}
	
	public void onRoundEnd(RoundResult roundResult) {
		this.waitFlag = false;
		boolean isGameEnd = roundResult.getRound() >= GameSetting.ROUND_MAX;
		
		PlayerGameState response = PlayerGameState.newBuilder()
				.setStateFlag(isGameEnd ? GrpcFlag.GAME_END : GrpcFlag.ROUND_END)
				.setRoundResult(GrpcUtil.convertRoundResult(roundResult))
  				.build();
		this.onNext(response);
		
		this.frameData = new FrameData();
	}
	
	public void onInputReceived(PlayerInput pAction) {
		if (this.isGameStarted()) {
			Key key = GrpcUtil.fromGrpcKey(pAction.getInputKey());
			InputManager.getInstance().setInput(playerNumber, key);
		}
		
		if (this.waitFlag) {
    		this.waitFlag = false;
    	}
	}
	
	public void onNext(PlayerGameState state) {
		if (!this.isCancelled()) {
			this.responseObserver.onNext(state);
		}
	}
	
}
