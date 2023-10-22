package grpc;

import informationcontainer.RoundResult;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import protoc.ServiceProto.SpectateRequest;
import protoc.ServiceProto.SpectatorGameState;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;

public class ObserverAgent {
	
	private int interval;
	private boolean frameDataFlag;
	private boolean screenDataFlag;
	private boolean audioDataFlag;
	
	private boolean cancelled;
	private StreamObserver<SpectatorGameState> responseObserver;
	private ObserverRunnable runnable;
	
	private Thread currentThread;
	
	public ObserverAgent() {
		this.cancelled = true;
	}
	
	public void register(SpectateRequest request, StreamObserver<SpectatorGameState> responseObserver) {
		if (!this.isCancelled()) {
			this.notifyOnCompleted();
		}
		
		((ServerCallStreamObserver<SpectatorGameState>) responseObserver).setOnCancelHandler(new Runnable() {
			@Override
			public void run() {
				ObserverAgent.this.cancel();
			}
		});
		
		this.interval = request.getInterval();
		this.frameDataFlag = request.getFrameDataFlag();
		this.screenDataFlag = request.getScreenDataFlag();
		this.audioDataFlag = request.getAudioDataFlag();
		
		this.cancelled = false;
		this.responseObserver = responseObserver;
		this.runnable = new ObserverRunnable(this.responseObserver);
		this.currentThread = new Thread(this.runnable);
		this.currentThread.start();
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public void cancel() {
		this.responseObserver = null;
		this.cancelled = true;
		this.runnable.cancel();
		this.currentThread = null;
	}
	
	public void notifyOnCompleted() {
		if (!this.isCancelled()) {
			this.responseObserver.onCompleted();
			this.cancel();
		}
	}
	
	public void onInitialize(GameData gameData) {
		if (this.isCancelled()) {
			return;
		}

		this.runnable.enqueue(ObserverGameState.newInitializeState(gameData));
	}
	
	public void onGameUpdate(FrameData frameData, ScreenData screenData, AudioData audioData) {
		if (this.isCancelled()) {
			return;
		}
		
		if (frameData.getFramesNumber() % this.interval == 0) {
			if (!this.frameDataFlag) frameData = null;
			if (!this.screenDataFlag) screenData = null;
			if (!this.audioDataFlag) audioData = null;
			
			this.runnable.enqueue(ObserverGameState.newProcessingState(frameData, screenData, audioData));
		}
	}
	
	public void onRoundEnd(RoundResult roundResult) {
		if (this.isCancelled()) {
			return;
		}
		
		this.runnable.enqueue(ObserverGameState.newRoundEndState(roundResult));
	}

}
