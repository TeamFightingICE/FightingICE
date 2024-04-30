package service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import manager.InputManager;
import struct.AudioData;

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
			
			socketSend(new byte[] { 1 }, false);
			socketSend(byteArray, true);
			
			if (spawnWaitThread) {
				waitForInput = true;
				new Thread(waitForInput()).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			setCancelled(true);
		}
	}
	
	private void socketSend(byte[] byteArray, boolean withHeader) throws IOException {
		if (withHeader) {
			int dataLength = byteArray.length;
			byte[] lengthBytes = ByteBuffer.allocate(4)
					.order(ByteOrder.LITTLE_ENDIAN)
					.putInt(dataLength)
					.array();
			dout.write(lengthBytes);
		}
		dout.write(byteArray);
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
	
	private Runnable waitForInput() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					byte[] byteArray = socketRecv(-1);
					
					if (byteArray.length != 8192) {
				        byteArray = new byte[8192];
						Logger.getAnonymousLogger().log(Level.WARNING, "Audio data format mismatch");
					}

			        InputManager.getInstance().setAudioData(new AudioData(byteArray));
			        waitForInput = false;
				} catch (Exception ex) {
					setCancelled(true);
				}
			}
			
		};
	}
	
	public void close() throws IOException {
		if (!isCancelled()) {
			socketSend(new byte[] { 0 }, false);
			setCancelled(true);
		}
		
		this.din = null;
		this.dout = null;
		this.thread = null;
	}
	
}
