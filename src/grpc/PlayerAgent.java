package grpc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import aiinterface.ThreadController;
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
	private boolean gameStarted;
	private boolean waitFlag;
	
	public PlayerAgent() {
		this.playerUuid = UUID.randomUUID();
		this.cancelled = true;

		this.gameStarted = false;
		this.waitFlag = false;
	}
	
	public void initializeRPC(InitializeRequest request) {
		this.playerName = request.getPlayerName();
		this.blind = request.getIsBlind();
	}
	
	public void participateRPC(StreamObserver<PlayerGameState> responseObserver) {
		if (!this.isCancelled()) {
			this.onCancel();
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
		
		this.isControl = false;
		this.frameData = new FrameData();
		this.audioData = new AudioData();
		this.screenData = new ScreenData();
		
		//this.rpcWarmingUp();
		this.onInitialize(gameData);
	}
	
	public void cancel() {
		this.responseObserver = null;
		this.cancelled = true;
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
		return this.gameStarted;
	}
	
	public boolean isReady() {
		return !this.waitFlag;
	}
	
	public void rpcWarmingUp() {
        Logger.getAnonymousLogger().log(Level.INFO, "Warming up RPC streaming for P" + (playerNumber ? "1" : "2"));
		for (int i = 0; i < 100; i++) {
			PlayerGameState response = PlayerGameState.newBuilder()
					.setStateFlag(GrpcFlag.EMPTY)
	  				.build();
			this.onNext(response);
		}
	}
	
	public void setInformation(boolean isControl, FrameData frameData, AudioData audioData, 
			ScreenData screenData, FrameData nonDelayFrameData) {
		this.isControl = isControl;
		this.frameData = frameData;
		this.nonDelayFrameData = nonDelayFrameData;
		this.screenData = screenData;
		if (this.isBlind()) {
			this.frameData.removeVisualData();
		}
		this.audioData = audioData;
	}
	
	public void onInitialize(GameData gameData) {
		PlayerGameState response = PlayerGameState.newBuilder()
				.setStateFlag(GrpcFlag.INITIALIZE)
				.setGameData(GrpcUtil.convertGameData(gameData))
  				.build();
		this.onNext(response);
		
		this.gameStarted = true;
	}
	
	public void onGameUpdate() {
		if (!this.waitFlag) {
			this.startTimer(frameData.getFramesNumber());
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
		this.exportGrpcPerfAsCsv();
		boolean isGameEnd = roundResult.getRound() >= GameSetting.ROUND_MAX;
		
		PlayerGameState response = PlayerGameState.newBuilder()
				.setStateFlag(isGameEnd ? GrpcFlag.GAME_END : GrpcFlag.ROUND_END)
				.setRoundResult(GrpcUtil.convertRoundResult(roundResult))
  				.build();
		this.onNext(response);
		
		if (isGameEnd) {
			this.gameStarted = false;
		}
	}
	
	public void onInputReceived(PlayerInput pAction) {
		if (this.isGameStarted()) {
			Key key = GrpcUtil.fromGrpcKey(pAction.getInputKey());
			InputManager.getInstance().setInput(playerNumber, key);
		}
		
		if (this.waitFlag) {
    		this.endTimer();
    		this.waitFlag = false;
    	}
	}
	
	public void onNext(PlayerGameState state) {
		if (!this.isCancelled()) {
			this.responseObserver.onNext(state);
		}
	}
	
	public void onCancel() {
		this.cancel();
		this.responseObserver.onCompleted();
	}
	
	public void onCompleted() {
		if (!this.isCancelled()) {
			this.responseObserver.onCompleted();
		}
	}
    
	//to be delete when launch
    private int fn = -1;
    private long ts = System.nanoTime();
    private List<Integer> fns = new ArrayList<>();
    private List<Double> ms = new ArrayList<>();
    
    public void startTimer(int fn) {
    	this.fn = fn;
    	this.ts = System.nanoTime();
    }
    
    public void endTimer() {
    	this.fns.add(this.fn);
        this.ms.add((System.nanoTime() - this.ts) / 1000000.0);
    }

    private void exportGrpcPerfAsCsv() {
    	try {
            String pNumber = String.format("P%s", playerNumber ? "1" : "2");
			String timeInfo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss", Locale.ENGLISH));
        	String fileName = String.format("log/grpc/%s_%s.csv", pNumber, timeInfo);
			FileWriter writer = new FileWriter(new File(fileName));
			writer.write("frame_number,processing_time\n");
			for (int i = 0; i < ms.size(); i++) {
				writer.write(String.format("%s,%s\n", fns.get(i), ms.get(i)));
			}
			writer.close();
	        
	    	double mean = ms.stream().reduce(0.0, (a, b) -> a + b) / ms.size();
	    	double std = ms.stream().reduce(0.0, (a, b) -> a += Math.abs(b - mean)) / ms.size();
	    	Logger.getAnonymousLogger().log(Level.INFO, String.format("%s Average processing time: %.4f ms (%.4f std.dev.)", pNumber, mean, std));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	this.fns.clear();
    	this.ms.clear();
    }
	
}
