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

import grpc.ObserverGameState;
import informationcontainer.RoundResult;
import protoc.ServiceProto.InitializeRequest;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;
import util.SocketUtil;

public class SocketServer {

	private int serverPort;
	private ServerSocket server;
	private Thread serverThread;
	private List<SocketClientHandler> clientList;
	private SocketPlayer[] players;
	
	public static SocketServer getInstance() {
        return SocketServerHolder.instance;
    }

    private static class SocketServerHolder {
        private static final SocketServer instance = new SocketServer(12345);
    }
	
	public SocketServer(int serverPort) {
		this.serverPort = serverPort;
		this.clientList = new ArrayList<>();
		this.players = new SocketPlayer[] { new SocketPlayer(), new SocketPlayer() };
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
						byte[] data = SocketUtil.socketRecv(din, 1);
						
						if (data[0] == 1) {
							// Play Agent Gateway
							byte[] requestAsBytes = SocketUtil.socketRecv(din, -1);
							InitializeRequest request = InitializeRequest.parseFrom(requestAsBytes);
							players[request.getPlayerNumber() ? 0 : 1].initializeSocket(client, request);
						} else if (data[0] == 2) {
							// TODO: Run Game
						} else if (data[0] == 3) {
							// Generative Sound Gateway
							SocketClientHandler clientHandler = new SocketClientHandler(client, SocketClientType.GENERATIVE_SOUND_AGENT);
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
		byte[] byteArray = ObserverGameState.newInitializeState(gameData).toProto().toByteArray();
		for (SocketClientHandler client: clientList) {
			client.produce(byteArray, false);
		}
	}
	
	public void initRound() {
		removeCancelledClients();
		byte[] byteArray = ObserverGameState.newInitRoundState().toProto().toByteArray();
		for (SocketClientHandler client: clientList) {
			client.produce(byteArray, false);
		}
	}
	
	public void processingGame(FrameData frameData, ScreenData screenData, AudioData audioData) {
		removeCancelledClients();
		byte[] byteArray = ObserverGameState.newProcessingState(frameData, screenData, audioData).toProto().toByteArray();
		for (SocketClientHandler client: clientList) {
			client.produce(byteArray, true);
		}
	}
	
	public void roundEnd(RoundResult roundResult) {
		removeCancelledClients();
		byte[] byteArray = ObserverGameState.newRoundEndState(roundResult).toProto().toByteArray();
		for (SocketClientHandler client: clientList) {
			client.produce(byteArray, false);
		}
	}
	
	public void gameEnd() {
		removeCancelledClients();
		byte[] byteArray = ObserverGameState.newGameEndState().toProto().toByteArray();
		for (SocketClientHandler client: clientList) {
			client.produce(byteArray, false);
		}
	}
	
}
