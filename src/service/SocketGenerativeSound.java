package service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import aiinterface.SoundDesignAIInterface;
import informationcontainer.RoundResult;
import protoc.EnumProto.GrpcFlag;
import protoc.ServiceProto.PlayerGameState;
import protoc.ServiceProto.SpectateRequest;
import setting.GameSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import util.GrpcUtil;
import util.SocketUtil;

public class SocketGenerativeSound implements SoundDesignAIInterface {

	private boolean cancelled;
	private boolean keepAlive;
	
	private DataInputStream din;
	private DataOutputStream dout;
	
	private FrameData frameData;
	private AudioData audioData;
	
	public SocketGenerativeSound() {
		this.cancelled = true;
		this.keepAlive = false;
		this.frameData = new FrameData();
		this.audioData = new AudioData();
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public boolean isKeepAlive() {
		return this.keepAlive;
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
		this.keepAlive = false;
		this.din = null;
		this.dout = null;
	}
	
	public void initializeSocket(Socket client, SpectateRequest request) throws IOException {
		if (!this.cancelled) {
			this.cancel();
		}
		
		this.cancelled = false;
		this.keepAlive = request.getKeepAlive();
		
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
	public AudioData input() {
		return this.audioData;
	}
	
	@Override
	public void processing() {
		if (this.cancelled) return;
		
		PlayerGameState.Builder builder = PlayerGameState.newBuilder()
				.setStateFlag(GrpcFlag.PROCESSING)
  				.setFrameData(this.frameData.toProto());
		
		try {
			SocketUtil.socketSend(dout, new byte[] { 1 }, false);
			SocketUtil.socketSend(dout, builder.build().toByteArray(), true);

			byte[] inputAsBytes = SocketUtil.socketRecv(din, -1);
			this.audioData = new AudioData(inputAsBytes);
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
