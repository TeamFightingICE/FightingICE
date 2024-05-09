package service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import manager.InputManager;
import struct.AudioData;
import util.SocketUtil;

public class SocketClientHandler {
	
	private SocketClientType clientType;
	private DataInputStream din;
	private DataOutputStream dout;
	private Thread thread;
	private boolean cancelled;
	private boolean waitForInput;
	
	public SocketClientHandler(Socket client, SocketClientType clientType) throws IOException {
		this.clientType = clientType;
		this.din = new DataInputStream(client.getInputStream());
		this.dout = new DataOutputStream(client.getOutputStream());
		this.cancelled = false;
		this.waitForInput = false;
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
	
	public void produce(byte[] byteArray, boolean spawnWaitThread) {
		try {
			if (waitForInput && spawnWaitThread) return;
			
			SocketUtil.socketSend(dout, new byte[] { 1 }, false);
			SocketUtil.socketSend(dout, byteArray, true);
			
			if (spawnWaitThread) {
				waitForInput = true;
				new Thread(waitForInput()).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			setCancelled(true);
		}
	}
	
	private Runnable waitForInput() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					byte[] byteArray = SocketUtil.socketRecv(din, -1);
					
					if (clientType == SocketClientType.GENERATIVE_SOUND_AGENT) {
						if (byteArray.length != 3200 && byteArray.length != 6400) {
					        byteArray = new byte[6400];
							Logger.getAnonymousLogger().log(Level.WARNING, "Audio data format mismatch");
						}

				        InputManager.getInstance().setAudioData(new AudioData(byteArray));
					}
					
			        waitForInput = false;
				} catch (Exception ex) {
					setCancelled(true);
				}
			}
			
		};
	}
	
	public void close() throws IOException {
		if (!isCancelled()) {
			SocketUtil.socketSend(dout, new byte[] { 0 }, false);
			setCancelled(true);
		}
		
		this.din = null;
		this.dout = null;
		this.thread = null;
	}
	
}
