package grpc;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import informationcontainer.RoundResult;
import io.grpc.stub.StreamObserver;
import protoc.EnumProto.GrpcFlag;
import protoc.ServiceProto.SpectatorGameState;
import setting.GameSetting;
import util.GrpcUtil;

public class ObserverRunnable implements Runnable {
	
	private boolean cancelled;
	private StreamObserver<SpectatorGameState> observer;
	private Queue<ObserverDataPack> stateQueue;
	
	public ObserverRunnable(StreamObserver<SpectatorGameState> observer) {
		this.cancelled = false;
		this.observer = observer;
		this.stateQueue = new LinkedList<>();
	}
	
	public void cancel() {
		this.cancelled = true;
	}
	
	public void enqueue(ObserverDataPack data) {
		if (data.getGrpcFlag() != GrpcFlag.PROCESSING || !this.stateQueue.isEmpty()) {
			this.stateQueue.clear();
		}
		
		this.stateQueue.offer(data);
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
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			ObserverDataPack data = this.stateQueue.poll();
			if (data != null) {
				if (data.getGrpcFlag() == GrpcFlag.INITIALIZE) {
					SpectatorGameState response = SpectatorGameState.newBuilder()
			  				.setStateFlag(GrpcFlag.INITIALIZE)
			  				.setGameData(GrpcUtil.convertGameData(data.getGameData()))
			  				.build();
					this.send(response);
				} else if (data.getGrpcFlag() == GrpcFlag.PROCESSING) {
					SpectatorGameState response = SpectatorGameState.newBuilder()
							.setStateFlag(GrpcFlag.PROCESSING)
							.setFrameData(GrpcUtil.convertFrameData(data.getFrameData()))
							.setScreenData(GrpcUtil.convertScreenData(data.getScreenData()))
							.setAudioData(GrpcUtil.convertAudioData(data.getAudioData()))
							.build();
					this.send(response);
				} else if (data.getGrpcFlag() == GrpcFlag.ROUND_END) {
					RoundResult roundResult = data.getRoundResult();
					boolean isGameEnd = roundResult.getRound() >= GameSetting.ROUND_MAX;
					SpectatorGameState response = SpectatorGameState.newBuilder()
							.setStateFlag(isGameEnd ? GrpcFlag.GAME_END : GrpcFlag.ROUND_END)
							.setRoundResult(GrpcUtil.convertRoundResult(roundResult))
			  				.build();
					this.send(response);
				}
			}
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "Observer thread is stopped.");
	}

}
