package grpc;

public class GrpcGame {
	
	private String[] characterNames;
	private String[] aiNames;
	private int gameNumber;
	private boolean runFlag;
	
	public GrpcGame() {
		this.characterNames = new String[2];
		this.aiNames = new String[2];
		this.gameNumber = 1;
		this.runFlag = false;
	}
	
	public String getCharacterName(boolean player) {
		return this.characterNames[player ? 0 : 1];
	}
	
	public void setCharacterName(boolean player, String characterName) {
		this.characterNames[player ? 0 : 1] = characterName;
	}
	
	public String getAIName(boolean player) {
		return this.aiNames[player ? 0 : 1];
	}
	
	public void setAIName(boolean player, String aiName) {
		this.aiNames[player ? 0 : 1] = aiName;
	}
	
	public int getGameNumber() {
		return this.gameNumber;
	}
	
	public void setGameNumber(int gameNumber) {
		this.gameNumber = gameNumber;
	}
	
	public boolean getRunFlag() {
		return this.runFlag;
	}
	
	public void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}
	
}
