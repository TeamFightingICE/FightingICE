package grpc;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import protoc.EnumProto.GrpcFlag;
import protoc.ServiceProto.SpectatorGameState;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;
import util.GrpcUtil;

public class ObserverAgent {
	
	private boolean cancelled;
	private StreamObserver<SpectatorGameState> responseObserver;
	
	private FrameData frameData;
	private AudioData audioData;
	private ScreenData screenData;
	
	public ObserverAgent() {
		this.cancelled = true;
	}
	
	public void initialize(GameData gameData) {
		this.frameData = new FrameData();
		this.audioData = new AudioData();
		this.screenData = new ScreenData();
		
		this.rpcWarmingUp();
	}
	
	public void register(StreamObserver<SpectatorGameState> responseObserver) {
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
		this.cancelled = false;
		this.responseObserver = responseObserver;
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
	
	public void rpcWarmingUp() {
		if (this.isCancelled()) {
			return;
		}
		
		// Warming up RPC streaming
        Logger.getAnonymousLogger().log(Level.INFO, "Warming up RPC streaming for observer");
		for (int i = 0; i < 100; i++) {
			SpectatorGameState response = SpectatorGameState.newBuilder()
	  				.setStateFlag(GrpcFlag.EMPTY)
	  				.build();
			this.onNext(response);
		}
	}
	
	public void setInformation(FrameData frameData, AudioData audioData, ScreenData screenData) {
		this.frameData = frameData;
		this.screenData = screenData;
		this.audioData = audioData;
	}
	
	public void onGameUpdate() {
		SpectatorGameState response = SpectatorGameState.newBuilder()
  				.setStateFlag(GrpcFlag.PROCESSING)
  				.setFrameData(GrpcUtil.convertFrameData(frameData))
  				.setScreenData(GrpcUtil.convertScreenData(screenData, 96, 64, true))
  				.setAudioData(GrpcUtil.convertAudioData(audioData))
  				.build();
		this.onNext(response);
	}
	
	public void onNext(SpectatorGameState state) {
		if (!this.isCancelled()) {
			this.responseObserver.onNext(state);
		}
	}

}
