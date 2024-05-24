package service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import grpc.GrpcGame;
import protoc.EnumProto.GrpcStatusCode;
import protoc.ServiceProto.InitializeRequest;
import protoc.ServiceProto.RunGameRequest;
import protoc.ServiceProto.RunGameResponse;
import protoc.ServiceProto.SpectateRequest;
import setting.FlagSetting;
import util.SocketUtil;

public class SocketServer {

	private boolean open;
	private String serverHost;
	private int serverPort;
	private GrpcGame game;
	private ServerSocket server;
	private Thread serverThread;
	private SocketPlayer[] players;
	private SocketGenerativeSound generativeSound;
	private List<SocketStream> streams;
	
	public static SocketServer getInstance() {
        return SocketServerHolder.instance;
    }

    private static class SocketServerHolder {
        private static final SocketServer instance = new SocketServer();
    }
	
	public SocketServer() {
		this.open = false;
		this.serverHost = System.getenv("SERVER_HOST");
		
		if (this.serverHost == null) {
			this.serverHost = "0.0.0.0";
		}
		
		this.game = new GrpcGame();
		this.players = new SocketPlayer[] { new SocketPlayer(), new SocketPlayer() };
		this.generativeSound = new SocketGenerativeSound();
		this.streams = new ArrayList<>();
	}
	
	public boolean isOpen() {
		return this.open;
	}
	
	public String getServerHost() {
		return this.serverHost;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public GrpcGame getGame() {
		return this.game;
	}
	
	public SocketPlayer getPlayer(int index) {
		return this.players[index];
	}
	
	public SocketGenerativeSound getGenerativeSound() {
		return this.generativeSound;
	}
	
	public List<SocketStream> getStreams() {
		return this.streams;
	}
	
	public RunGameResponse callRunGame(RunGameRequest request) {
		GrpcStatusCode statusCode;
		String responseMessage;
		
		if (!FlagSetting.enablePyftgMode) {
			statusCode = GrpcStatusCode.FAILED;
			responseMessage = "The game is not in auto mode.";
		} else if (!FlagSetting.isPyftgReady) {
			statusCode = GrpcStatusCode.FAILED;
			responseMessage = "The game is not ready for running the game.";
		} else {
			String characterName1 = request.getCharacter1();
			String characterName2 = request.getCharacter2();
			String aiName1 = request.getPlayer1();
			String aiName2 = request.getPlayer2();
			int gameNumber = request.getGameNumber();
			
			game.setCharacterName(true, characterName1);
	  		game.setCharacterName(false, characterName2);
	  		game.setAIName(true, aiName1);
	  		game.setAIName(false, aiName2);
	  		game.setGameNumber(gameNumber);
	  		game.setRunFlag(true);
			
			statusCode = GrpcStatusCode.SUCCESS;
			responseMessage = "Success";
		}
		
		RunGameResponse response = RunGameResponse.newBuilder()
				.setStatusCode(statusCode)
				.setResponseMessage(responseMessage)
				.build();
		
		return response;
	}
	
	public void startServer(int serverPort) throws IOException {
		this.serverPort = serverPort;
		server = new ServerSocket();
		server.bind(new InetSocketAddress(this.serverHost, serverPort));;
		
		serverThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Socket client = server.accept();
					client.setTcpNoDelay(true);
					
					DataInputStream din = new DataInputStream(client.getInputStream());
					DataOutputStream dout = new DataOutputStream(client.getOutputStream());
					byte[] data = SocketUtil.socketRecv(din, 1);
					
					if (data[0] == 1) {
						// Play Agent Gateway
						byte[] requestAsBytes = SocketUtil.socketRecv(din, -1);
						InitializeRequest request = InitializeRequest.parseFrom(requestAsBytes);
						players[request.getPlayerNumber() ? 0 : 1].initializeSocket(client, request);
						Logger.getAnonymousLogger().log(Level.INFO, "Client connected as Game Playing AI");
					} else if (data[0] == 2) {
						// Run Game Gateway
						byte[] requestAsBytes = SocketUtil.socketRecv(din, -1);
						RunGameRequest request = RunGameRequest.parseFrom(requestAsBytes);
						RunGameResponse response = callRunGame(request);
						byte[] responseAsBytes = response.toByteArray();
						SocketUtil.socketSend(dout, responseAsBytes, true);
						Logger.getAnonymousLogger().log(Level.INFO, "Received run game request");
					} else if (data[0] == 3) {
						// Generative Sound Gateway
						byte[] requestAsBytes = SocketUtil.socketRecv(din, -1);
						SpectateRequest request = SpectateRequest.parseFrom(requestAsBytes);
						generativeSound.initializeSocket(client, request);
						Logger.getAnonymousLogger().log(Level.INFO, "Client connected as Sound Generative AI");
					} else if (data[0] == 4) {
						// Stream Gateway
						byte[] requestAsBytes = SocketUtil.socketRecv(din, -1);
						SpectateRequest request = SpectateRequest.parseFrom(requestAsBytes);
						SocketStream stream = new SocketStream();
						stream.initializeSocket(client, request);
						this.streams.add(stream);
						Logger.getAnonymousLogger().log(Level.INFO, "Client connected as Stream Handler");
					}
				} catch (IOException e) {
					if (!Thread.currentThread().isInterrupted()) Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
				}
			}
		});
		serverThread.start();
		
		this.open = true;
	}
	
	public void stopServer() throws IOException {
		this.notifyTaskFinished();
		
		serverThread.interrupt();
		server.close();
		
		serverThread = null;
		this.open = false;
    	Logger.getAnonymousLogger().log(Level.INFO, "Socket server is stopped");
	}
	
	public void notifyTaskFinished() {
		for (int i = 0; i < 2; i++) {
			this.players[i].cancel();;
		}
		
		if (!this.generativeSound.isKeepAlive()) {
			this.generativeSound.cancel();
		}
		
		Iterator<SocketStream> streamsIter = this.streams.iterator();
		while (streamsIter.hasNext()) {
			SocketStream streamClient = streamsIter.next();
			if (!streamClient.isKeepAlive()) {
				streamClient.cancel();
				streamsIter.remove();
			}
		}
	}
	
}
