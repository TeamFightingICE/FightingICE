package service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import aiinterface.StreamInterface;
import informationcontainer.RoundResult;
import protoc.EnumProto.GrpcFlag;
import protoc.ServiceProto.PlayerGameState;
import protoc.ServiceProto.SpectateRequest;
import setting.GameSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;
import util.GrpcUtil;
import util.SocketUtil;

public class SocketStream implements StreamInterface {

	private boolean cancelled;
	
	private boolean frameDataFlag;
	private boolean audioDataFlag;
	private boolean screenDataFlag;
	
	private DataInputStream din;
	private DataOutputStream dout;
	
	private FrameData frameData;
	private AudioData audioData;
	private ScreenData screenData;
	
	public SocketStream() {
		this.cancelled = true;
		this.frameData = new FrameData();
		this.audioData = new AudioData();
		this.screenData = new ScreenData();
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public void cancel() {
		if (!this.cancelled) {
			try {
				SocketUtil.socketSend(dout, new byte[] { 0 }, false);
			} catch (IOException e) {
				Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
			}
		}
		
		this.cancelled = true;
		this.din = null;
		this.dout = null;
	}
	
	public void initializeSocket(Socket client, SpectateRequest request) throws IOException {
		if (!this.cancelled) {
			this.cancel();
		}
		
		this.cancelled = false;
		this.frameDataFlag = request.getFrameDataFlag();
		this.audioDataFlag = request.getAudioDataFlag();
		this.screenDataFlag = request.getScreenDataFlag();
		
		this.din = new DataInputStream(client.getInputStream());
		this.dout = new DataOutputStream(client.getOutputStream());
	}
	
	@Override
	public void initialize(GameData gameData) {
		PlayerGameState state = PlayerGameState.newBuilder()
				.setStateFlag(GrpcFlag.INITIALIZE)
				.setGameData(GrpcUtil.convertGameData(gameData))
  				.build();
		
		try {
			SocketUtil.socketSend(dout, new byte[] { 1 }, false);
			SocketUtil.socketSend(dout, state.toByteArray(), true);
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
			this.cancelled = true;
		}
	}

	@Override
	public void getInformation(FrameData frameData) {
		this.frameData = frameData;
	}

	@Override
	public void getAudioData(AudioData audioData) {
		this.audioData = audioData;
	}

	@Override
	public void getScreenData(ScreenData screenData) {
		this.screenData = screenData;
	}

	@Override
	public void processing() {
		if (this.cancelled) return;
		
		PlayerGameState.Builder builder = PlayerGameState.newBuilder()
				.setStateFlag(GrpcFlag.PROCESSING);
		
		if (this.frameDataFlag) {
			builder.setFrameData(this.frameData.toProto());
		}
		
		if (this.audioDataFlag) {
			builder.setAudioData(this.audioData.toProto());
		}
		
		if (this.screenDataFlag) {
			builder.setScreenData(this.screenData.toProto());
		}
		
		try {
			SocketUtil.socketSend(dout, new byte[] { 1 }, false);
			SocketUtil.socketSend(dout, builder.build().toByteArray(), true);
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public void roundEnd(RoundResult roundResult) {
		if (this.cancelled) return;
		
		boolean isGameEnd = roundResult.getRound() >= GameSetting.ROUND_MAX;
		
		PlayerGameState state = PlayerGameState.newBuilder()
				.setStateFlag(isGameEnd ? GrpcFlag.GAME_END : GrpcFlag.ROUND_END)
				.setRoundResult(GrpcUtil.convertRoundResult(roundResult))
  				.build();
		
		try {
			SocketUtil.socketSend(dout, new byte[] { 1 }, false);
			SocketUtil.socketSend(dout, state.toByteArray(), true);
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public void close() {
		
	}

}
