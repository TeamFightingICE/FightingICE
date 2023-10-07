package grpc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
	private BlockingQueue<ObserverGameState> stateQueue;
	
	public ObserverRunnable(StreamObserver<SpectatorGameState> observer) {
		this.cancelled = false;
		this.observer = observer;
		this.stateQueue = new LinkedBlockingQueue<>(1);
	}
	
	public void cancel() {
		this.enqueue(new ObserverGameState());
		this.cancelled = true;
	}
	
	public void enqueue(ObserverGameState data) {
		if (!this.stateQueue.isEmpty() && data.compareTo(this.stateQueue.peek()) >= 0) {
			this.stateQueue.clear();
		}
		
		try {
			this.stateQueue.put(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
			try {
				ObserverGameState data = this.stateQueue.take();
				
				if (data.getStateFlag() == StateFlag.INITIALIZE) {
					SpectatorGameState response = SpectatorGameState.newBuilder()
			  				.setStateFlag(GrpcFlag.INITIALIZE)
			  				.setGameData(GrpcUtil.convertGameData(data.getGameData()))
			  				.build();
					this.send(response);
				} else if (data.getStateFlag() == StateFlag.PROCESSING) {
					SpectatorGameState response = SpectatorGameState.newBuilder()
							.setStateFlag(GrpcFlag.PROCESSING)
							.setFrameData(GrpcUtil.convertFrameData(data.getFrameData()))
							.setScreenData(GrpcUtil.convertScreenData(data.getScreenData()))
							.setAudioData(GrpcUtil.convertAudioData(data.getAudioData()))
							.build();
					this.send(response);
				} else if (data.getStateFlag() == StateFlag.ROUND_END) {
					RoundResult roundResult = data.getRoundResult();
					boolean isGameEnd = roundResult.getRound() >= GameSetting.ROUND_MAX;
					SpectatorGameState response = SpectatorGameState.newBuilder()
							.setStateFlag(isGameEnd ? GrpcFlag.GAME_END : GrpcFlag.ROUND_END)
							.setRoundResult(GrpcUtil.convertRoundResult(roundResult))
			  				.build();
					this.send(response);
				} else if (data.getStateFlag() == StateFlag.CANCELLED) {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "Observer thread is stopped.");
	}

}
