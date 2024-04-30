package grpc;

public enum StateFlag {
	
	INITIALIZE("initialize", 2),
	INIT_ROUND("init_round", 3),
	PROCESSING("processing", 1),
	ROUND_END("round_end", 3),
	GAME_END("game_end", 3),
	CANCELLED("cancelled", 4),
	;
	
	private String name;
	private int priority;
	
	StateFlag(String name, int priority) {
		this.name = name;
		this.priority = priority;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
}
