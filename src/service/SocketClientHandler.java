package service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HexFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import grpc.ObserverGameState;
import informationcontainer.RoundResult;
import manager.InputManager;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;

public class SocketClientHandler implements Runnable {
	
	private SocketClientType clientType;
	private DataInputStream din;
	private DataOutputStream dout;
	private Thread thread;
	private BlockingQueue<ObserverGameState> stateQueue;
	private boolean cancelled;
	
	public SocketClientHandler(Socket client, SocketClientType clientType) throws IOException {
		this.clientType = clientType;
		this.din = new DataInputStream(client.getInputStream());
		this.dout = new DataOutputStream(client.getOutputStream());
		this.stateQueue = new LinkedBlockingQueue<>(1);
		this.cancelled = false;
	}
	
	public SocketClientType getClientType() {
		return clientType;
	}
	
	public Thread getThread() {
		return thread;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	private void enqueueState(ObserverGameState gameState) {
		if (stateQueue.remainingCapacity() < 1) {
			stateQueue.clear();
			Logger.getAnonymousLogger().log(Level.WARNING, "Consumer unable to consume game state. Clear game state queue...");
		}
		
		this.stateQueue.add(gameState);
	}
	
	public void initialize(GameData gameData) {
		enqueueState(ObserverGameState.newInitializeState(gameData));
	}
	
	public void processingGame(FrameData frameData) {
		enqueueState(ObserverGameState.newProcessingState(frameData, null, null));
	}
	
	public void roundEnd(RoundResult roundResult) {
		enqueueState(ObserverGameState.newRoundEndState(roundResult));
	}
	
	public void startThread() {
		thread = new Thread(this);
		thread.start();
	}
	
	private void socketSend(byte[] dataBytes, boolean withHeader) throws IOException {
		if (withHeader) {
			int dataLength = dataBytes.length;
			byte[] lengthBytes = ByteBuffer.allocate(4)
					.order(ByteOrder.LITTLE_ENDIAN)
					.putInt(dataLength)
					.array();
			dout.write(lengthBytes);
		}
		dout.write(dataBytes);
	}
	
	private byte[] socketRecv(int dataLength) throws IOException {
		if (dataLength == -1) {
			byte[] lengthBytes = din.readNBytes(4);
			dataLength = ByteBuffer.wrap(lengthBytes)
					.order(ByteOrder.LITTLE_ENDIAN)
					.getInt();
		}
		return din.readNBytes(dataLength);
	}
	
	private void playAgentProcess() {
		
	}
	
	private void generativeSoundAgentProcess() {
		try {
			ObserverGameState state = stateQueue.take();
			
			socketSend(HexFormat.of().parseHex("01"), false);
			socketSend(state.toProto().toByteArray(), true);
			
			byte[] byteArray = socketRecv(-1);
			if (byteArray.length != 8192) {
		        InputManager.getInstance().setAudioData(null);
				Logger.getAnonymousLogger().log(Level.WARNING, "Audio data format mismatch");
				return;
			}
			
	        InputManager.getInstance().setAudioData(new AudioData(byteArray));
		} catch (Exception e) {
			setCancelled(true);
		}
	}
	
	@Override
	public void run() {
		while (!isCancelled() && !Thread.currentThread().isInterrupted()) {
			if (clientType == SocketClientType.PLAY_AGENT) {
				playAgentProcess();
			} else if (clientType == SocketClientType.GENERATIVE_SOUND_AGENT) {
				generativeSoundAgentProcess();
			}
		}
	}
	
	public void close() throws IOException {
		if (!isCancelled()) {
			socketSend(HexFormat.of().parseHex("00"), false);
			setCancelled(true);
		}
		
		this.thread.interrupt();
		this.stateQueue.clear();
		
		this.din = null;
		this.dout = null;
		this.thread = null;
		this.stateQueue = null;
	}
	
}
