package service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import aiinterface.AIInterface;
import informationcontainer.RoundResult;
import protoc.EnumProto.GrpcFlag;
import protoc.MessageProto.GrpcKey;
import protoc.ServiceProto.InitializeRequest;
import protoc.ServiceProto.PlayerGameState;
import setting.GameSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.ScreenData;
import util.GrpcUtil;
import util.SocketUtil;

public class SocketPlayer implements AIInterface {

	private boolean cancelled;
	private String name;
	private boolean blind;
	
	private DataInputStream din;
	private DataOutputStream dout;
	
	private boolean isControl;
	private FrameData frameData;
	private AudioData audioData;

	private ScreenData screenData;
	private FrameData nonDelayFrameData;
	
	private Key input;
	
	public SocketPlayer() {
		this.cancelled = true;
		this.input = new Key();
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isBlind() {
		return this.blind;
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
	
	public void initializeSocket(Socket client, InitializeRequest request) throws IOException {
		if (!this.cancelled) {
			this.cancel();
		}
		
		this.cancelled = false;
		
		this.name = request.getPlayerName();
		this.blind = request.getIsBlind();
		
		this.din = new DataInputStream(client.getInputStream());
		this.dout = new DataOutputStream(client.getOutputStream());
	}
	
	@Override
	public int initialize(GameData gameData, boolean playerNumber) {
		PlayerGameState state = PlayerGameState.newBuilder()
				.setStateFlag(GrpcFlag.INITIALIZE)
				.setGameData(GrpcUtil.convertGameData(gameData))
  				.build();
		
		try {
			SocketUtil.socketSend(dout, new byte[] { 1 }, false);
			SocketUtil.socketSend(dout, state.toByteArray(), true);
			return 0;
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
			this.cancelled = true;
			return 1;
		}
	}

	@Override
	public void getInformation(FrameData frameData, boolean isControl) {
		this.frameData = frameData;
		this.isControl = isControl;
	}
	
	@Override
	public void getScreenData(ScreenData screenData) {
		this.screenData = screenData;
	}
	
	@Override
	public void getAudioData(AudioData audioData) {
		this.audioData = audioData;
	}
	
	public void getNonDelayFrameData(FrameData frameData) {
		this.nonDelayFrameData = frameData;
	}

	@Override
	public Key input() {
		return this.input;
	}

	@Override
	public void processing() {
		if (this.cancelled) return;
		
		PlayerGameState.Builder builder = PlayerGameState.newBuilder()
				.setStateFlag(GrpcFlag.PROCESSING)
				.setIsControl(this.isControl)
  				.setFrameData(this.frameData.toProto())
  				.setAudioData(this.audioData.toProto());
		
		if (this.nonDelayFrameData != null) {
			builder.setNonDelayFrameData(this.nonDelayFrameData.toProto());
		}
		
		try {
			SocketUtil.socketSend(dout, new byte[] { 1 }, false);
			SocketUtil.socketSend(dout, builder.build().toByteArray(), true);

			byte[] keyAsBytes = SocketUtil.socketRecv(din, -1);
			this.input = GrpcUtil.fromGrpcKey(GrpcKey.parseFrom(keyAsBytes));
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
	public void close()  {
		
	}

}
