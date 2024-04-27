package grpc;

public enum StateFlag {
	
	INITIALIZE("initialize", 2),
	PROCESSING("processing", 1),
	ROUND_END("round_end", 3),
	CANCELLED("cancelled", 4),
	INIT_ROUND("init_round", 5)
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
