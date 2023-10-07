package grpc;

import java.util.logging.Level;
import java.util.logging.Logger;

import informationcontainer.RoundResult;
import io.grpc.stub.StreamObserver;
import protoc.EnumProto.GrpcFlag;
import protoc.ServiceProto.SpectatorGameState;
import setting.GameSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;
import util.GrpcUtil;

public class ObserverRunnable implements Runnable {

	private GameData gameData;
	private FrameData frameData;
	private ScreenData screenData;
	private AudioData audioData;
	private RoundResult roundResult;
	
	private GrpcFlag grpcFlag;
	private boolean cancelled;
	private Object waitObj;
	private StreamObserver<SpectatorGameState> observer;
	
	public ObserverRunnable(StreamObserver<SpectatorGameState> observer) {
		this.cancelled = false;
		this.waitObj = new Object();
		this.observer = observer;
	}
	
	public void cancel() {
		this.cancelled = true;
		this.notifyRunnable();
	}
	
	public void setInitializeData(GameData gameData) {
		this.grpcFlag = GrpcFlag.INITIALIZE;
		this.gameData = gameData;
		this.notifyRunnable();
	}
	
	public void setProcessingData(FrameData frameData, ScreenData screenData, AudioData audioData) {
		this.grpcFlag = GrpcFlag.PROCESSING;
		this.frameData = frameData;
		this.screenData = screenData;
		this.audioData = audioData;
		this.notifyRunnable();
	}
	
	public void setRoundEndData(RoundResult roundResult) {
		this.grpcFlag = GrpcFlag.ROUND_END;
		this.roundResult = roundResult;
		this.notifyRunnable();
	}
	
	private void notifyRunnable() {
		synchronized (this.waitObj) {
			this.waitObj.notifyAll();
		}
	}
	
	private void send(SpectatorGameState state) {
		if (!this.cancelled) {
			this.observer.onNext(state);
		}
	}
	
	@Override
	public void run() {
		Logger.getAnonymousLogger().log(Level.INFO, "Observer thread is started.");
		
		while (!this.cancelled) {
			synchronized (this.waitObj) {
				try {
	                this.waitObj.wait();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
			}
			
			if (this.grpcFlag == GrpcFlag.INITIALIZE) {
				SpectatorGameState response = SpectatorGameState.newBuilder()
		  				.setStateFlag(GrpcFlag.INITIALIZE)
		  				.setGameData(GrpcUtil.convertGameData(gameData))
		  				.build();
				this.send(response);
				
				this.gameData = null;
			} else if (this.grpcFlag == GrpcFlag.PROCESSING) {
				SpectatorGameState response = SpectatorGameState.newBuilder()
						.setStateFlag(GrpcFlag.PROCESSING)
						.setFrameData(GrpcUtil.convertFrameData(frameData))
						.setScreenData(GrpcUtil.convertScreenData(screenData))
						.setAudioData(GrpcUtil.convertAudioData(audioData))
						.build();
				this.send(response);

				
				this.frameData = null;
				this.screenData = null;
				this.audioData = null;
			} else if (this.grpcFlag == GrpcFlag.ROUND_END) {
				boolean isGameEnd = roundResult.getRound() >= GameSetting.ROUND_MAX;
				SpectatorGameState response = SpectatorGameState.newBuilder()
						.setStateFlag(isGameEnd ? GrpcFlag.GAME_END : GrpcFlag.ROUND_END)
						.setRoundResult(GrpcUtil.convertRoundResult(roundResult))
		  				.build();
				this.send(response);
				
				this.roundResult = null;
			}
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "Observer thread is stopped.");
	}

}
