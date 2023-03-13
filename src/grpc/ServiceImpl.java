package grpc;

import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;
import protoc.ServiceGrpc;
import protoc.ServiceProto.InitializeRequest;
import protoc.ServiceProto.InitializeResponse;
import protoc.ServiceProto.ParticipateRequest;
import protoc.ServiceProto.PlayerGameState;
import protoc.ServiceProto.PlayerInput;
import protoc.ServiceProto.RunGameRequest;

public class ServiceImpl extends ServiceGrpc.ServiceImplBase {
	
	private GrpcServer server;
	
	public ServiceImpl(GrpcServer server) {
		this.server = server;
	}
	
	@Override
	public void runGame(RunGameRequest request, StreamObserver<Empty> responseObserver) {
		String characterName1 = request.getCharacter1();
		String characterName2 = request.getCharacter2();
		String aiName1 = request.getPlayer1();
		String aiName2 = request.getPlayer2();
		int gameNumber = request.getGameNumber();
		
		this.server.runGame(characterName1, characterName2, aiName1, aiName2, gameNumber);
		responseObserver.onNext(Empty.getDefaultInstance());
		responseObserver.onCompleted();
	}
	
	@Override
	public void initialize(InitializeRequest request, StreamObserver<InitializeResponse> responseObserver) {
		PlayerAgent player = server.getPlayer(request.getPlayerNumber());
		player.initializeRPC(request);
		InitializeResponse response = InitializeResponse.newBuilder()
				.setPlayerUuid(player.getPlayerUuid().toString())
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
	
	@Override
	public void participate(ParticipateRequest request, StreamObserver<PlayerGameState> responseObserver) {
		PlayerAgent player = server.getPlayerWithUuid(request.getPlayerUuid());
		player.participateRPC(responseObserver);
	}
	
	@Override
	public void input(PlayerInput request, StreamObserver<Empty> responseObserver) {
		PlayerAgent player = server.getPlayerWithUuid(request.getPlayerUuid());
		player.onInputReceived(request);
		responseObserver.onNext(Empty.getDefaultInstance());
		responseObserver.onCompleted();
	}
	
}
