package service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import informationcontainer.RoundResult;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;

public class SocketServer {

	private int serverPort;
	private ServerSocket server;
	private Thread serverThread;
	private List<SocketClientHandler> clientList;
	
	public static SocketServer getInstance() {
        return SocketServerHolder.instance;
    }

    private static class SocketServerHolder {
        private static final SocketServer instance = new SocketServer(12345);
    }
	
	public SocketServer(int serverPort) {
		this.serverPort = serverPort;
		this.clientList = new ArrayList<>();
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public void startServer() throws IOException {
		server = new ServerSocket(serverPort);
		
		serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					try {
						Socket client = server.accept();
						DataInputStream din = new DataInputStream(client.getInputStream());
						byte[] data = din.readNBytes(1);
						
						if (data[0] == 1) {
							// TODO: Play Agent Gateway
						} else if (data[0] == 2) {
							// TODO: Run Game
						} else if (data[0] == 3) {
							// Generative Sound Gateway
							SocketClientHandler clientHandler = new SocketClientHandler(client, SocketClientType.GENERATIVE_SOUND_AGENT);
							clientHandler.startThread();
							clientList.add(clientHandler);
							Logger.getAnonymousLogger().log(Level.INFO, "Client connected as Sound Generative AI");
						}
					} catch (IOException e) {
						if (!Thread.currentThread().isInterrupted()) Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
					}
				}
			}
		});
		serverThread.start();
		
    	Logger.getAnonymousLogger().log(Level.INFO, "Socket server is started, listening on " + serverPort);
	}
	
	public void stopServer() throws IOException {
		for (SocketClientHandler client: clientList) {
			client.close();
		}
		serverThread.interrupt();
		server.close();
		
		serverThread = null;
		clientList.clear();
		
    	Logger.getAnonymousLogger().log(Level.INFO, "Socket server is stopped");
	}
	
	private void removeCancelledClients() {
		Iterator<SocketClientHandler> iter = clientList.iterator();
		while (iter.hasNext()) {
			SocketClientHandler client = iter.next();
			if (client.isCancelled()) {
				iter.remove();
			}
		}
	}
	
	public void initialize(GameData gameData) {
		removeCancelledClients();
		for (SocketClientHandler client: clientList) {
			client.initialize(gameData);
		}
	}
	
	public void processingGame(FrameData frameData, ScreenData screenData, AudioData audioData) {
		removeCancelledClients();
		for (SocketClientHandler client: clientList) {
			client.processingGame(frameData);
		}
	}
	
	public void roundEnd(RoundResult roundResult) {
		removeCancelledClients();
		for (SocketClientHandler client: clientList) {
			client.roundEnd(roundResult);
		}
	}
	
}
