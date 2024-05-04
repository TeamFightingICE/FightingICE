package grpc;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {
	
  	private Server server;
  	private GrpcGame game;
	private PlayerAgent[] players;
	private ObserverAgent observer;
	private boolean start;
	
	public static GrpcServer getInstance() {
        return GrpcServerHolder.instance;
    }

    private static class GrpcServerHolder {
        private static final GrpcServer instance = new GrpcServer();
    }
  	
  	public GrpcServer() {
  		this.game = new GrpcGame();
  		this.players = new PlayerAgent[] {new PlayerAgent(), new PlayerAgent()};
  		this.observer = new ObserverAgent();
  	}

  	public void start(int port) throws IOException {
  		ServiceImpl service = new ServiceImpl(this);
    	server = ServerBuilder.forPort(port)
    			.addService(service)
    			.executor(Executors.newFixedThreadPool(6))
    			.build();
    	
    	server.start();
    	this.start = true;
    	Logger.getAnonymousLogger().log(Level.INFO, "gRPC server is started, listening on " + port);
  	}

  	public void stop() throws InterruptedException {
	  	if (server != null) {
	  		this.start = false;
	  		this.close();
	  		server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
	    	Logger.getAnonymousLogger().log(Level.INFO, "gRPC server is stopped");
    	}
  	}
  	
  	public boolean isStart() {
  		return this.start;
  	}
  	
  	public PlayerAgent getPlayer(boolean playerNumber) {
  		return this.players[playerNumber ? 0 : 1];
  	}
  	
  	public ObserverAgent getObserver() {
  		return this.observer;
  	}
  	
  	public PlayerAgent getPlayerWithUuid(String playerUuid) {
  		Optional<PlayerAgent> player =  Arrays.stream(this.players)
  				.filter(x -> playerUuid.equals(x.getPlayerUuid().toString()))
  				.findFirst();
  		return player.isPresent() ? player.get() : new PlayerAgent();
  	}
  	
  	public GrpcGame getGame() {
  		return this.game;
  	}
  	
  	public void runGame(String characterName1, String characterName2, String aiName1, String aiName2, int gameNumber) {
  		this.game.setCharacterName(true, characterName1);
  		this.game.setCharacterName(false, characterName2);
  		this.game.setAIName(true, aiName1);
  		this.game.setAIName(false, aiName2);
  		this.game.setGameNumber(gameNumber);
  		
  		this.game.setRunFlag(true);
  	}
  	
  	public void close() {
  		for (int i = 0; i < 2; i++) {
  			if (!this.players[i].isCancelled()) {
  				this.players[i].notifyOnCompleted();
  			}
  		}
  		
  		this.observer.notifyOnCompleted();
  	}
  	
}
