package service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import aiinterface.SoundDesignAIInterface;
import grpc.ObserverGameState;
import informationcontainer.RoundResult;
import setting.GameSetting;
import struct.FrameData;
import struct.GameData;
import util.SocketUtil;

public class SocketGenerativeSound implements SoundDesignAIInterface {

	private boolean cancelled;
	
	private DataInputStream din;
	private DataOutputStream dout;
	
	private FrameData frameData;
	private byte[] input;
	
	public SocketGenerativeSound() {
		this.cancelled = true;
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
	
	public void initializeSocket(Socket client) throws IOException {
		if (!this.cancelled) {
			this.cancel();
		}
		
		this.cancelled = false;
		
		this.din = new DataInputStream(client.getInputStream());
		this.dout = new DataOutputStream(client.getOutputStream());
	}

	@Override
	public void initialize(GameData gameData) {
		try {
			SocketUtil.socketSend(dout, new byte[] { 1 }, false);
			SocketUtil.socketSend(dout, ObserverGameState.newInitializeState(gameData).toProto().toByteArray(), true);
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
	public void processing() {
		if (this.cancelled) return;
		
		ObserverGameState gameState = ObserverGameState.newProcessingState(frameData, null, null);
		try {
			SocketUtil.socketSend(dout, new byte[] { 1 }, false);
			SocketUtil.socketSend(dout, gameState.toProto().toByteArray(), true);
			
			this.input = SocketUtil.socketRecv(din, -1);
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public void roundEnd(RoundResult roundResult) {
		if (this.cancelled) return;
		
		try {
			SocketUtil.socketSend(dout, new byte[] { 1 }, false);
			SocketUtil.socketSend(dout, ObserverGameState.newRoundEndState(roundResult).toProto().toByteArray(), true);
			
			if (roundResult.getRound() >= GameSetting.ROUND_MAX) {
				SocketUtil.socketSend(dout, new byte[] { 1 }, false);
				SocketUtil.socketSend(dout, ObserverGameState.newGameEndState().toProto().toByteArray(), true);
			}
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public byte[] input() {
		return this.input;
	}

	@Override
	public void close() {
		
	}
	
}
