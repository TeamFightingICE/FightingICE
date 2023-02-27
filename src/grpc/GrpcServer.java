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
import io.grpc.ServerInterceptors;

public class GrpcServer {
	
  	private Server server;
  	private GrpcGame game;
	private PlayerAgent[] players;
	private ObserverAgent observer;
  	
  	public GrpcServer() {
  		this.game = new GrpcGame();
  		this.players = new PlayerAgent[] {new PlayerAgent(), new PlayerAgent()};
  		this.observer = new ObserverAgent();
  	}

  	public void start(int port) throws IOException {
  		ServiceImpl service = new ServiceImpl(this);
    	server = ServerBuilder.forPort(port)
    			.addService(ServerInterceptors.intercept(service, new RequestInterceptor()))
    			.executor(Executors.newFixedThreadPool(4))
    			.build();
    	
    	server.start();
    	Logger.getAnonymousLogger().log(Level.INFO, "Server started, listening on " + port);
  	}

  	public void stop() throws InterruptedException {
	  	if (server != null) {
	  		this.players[0].onCompleted();
	  		this.players[1].onCompleted();
	  		this.observer.notifyOnCompleted();
	  		server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    	}
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
  	
  	public void release() {
  		for (int i = 0; i < 2; i++) {
  			if (!this.players[i].isCancelled()) {
  				this.players[i].onCompleted();
  				this.players[i].cancel();
  			}
  		}
  	}
  	
}
