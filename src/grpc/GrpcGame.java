package grpc;

import java.util.List;

import loader.ResourceLoader;
import manager.InputManager;
import service.SocketPlayer;
import service.SocketServer;
import setting.FlagSetting;
import setting.LaunchSetting;

public class GrpcGame {
	
	private String[] characterNames;
	private String[] aiNames;
	private int gameNumber;
	private boolean runFlag;
	
	private List<String> allAiNames;
	
	public GrpcGame() {
		this.characterNames = new String[2];
		this.aiNames = new String[2];
		this.gameNumber = 1;
		this.runFlag = false;
		

		this.allAiNames = ResourceLoader.getInstance().loadFileNames("./data/ai", ".jar");
	}
	
	public void setCharacterName(boolean player, String characterName) {
		this.characterNames[player ? 0 : 1] = characterName;
		LaunchSetting.characterNames[player ? 0 : 1] = characterName;
	}
	
	public void setAIName(boolean player, String aiName) {
		int i = player ? 0 : 1;
		this.aiNames[i] = aiName;
		if (aiName != null && !aiName.equalsIgnoreCase("Keyboard")) {
			LaunchSetting.deviceTypes[i] = allAiNames.contains(aiName) 
					? InputManager.DEVICE_TYPE_AI : InputManager.DEVICE_TYPE_EXTERNAL;
			LaunchSetting.aiNames[player ? 0 : 1] = aiName;
		} else {
			LaunchSetting.deviceTypes[i] = InputManager.DEVICE_TYPE_KEYBOARD;
			LaunchSetting.aiNames[i] = "Keyboard";
		}
	}
	
	public void setGameNumber(int gameNumber) {
		this.gameNumber = gameNumber;
		LaunchSetting.repeatNumber = gameNumber;
		if (gameNumber > 1) {
			FlagSetting.automationFlag = true;
		}
	}
	
	public void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}
	
	public boolean isReady() {
		if (!this.runFlag) return false;
		
		boolean ans = true;
		for (int i = 0; i < 2; i++) {
			if (LaunchSetting.deviceTypes[i] == InputManager.DEVICE_TYPE_EXTERNAL) {
				SocketPlayer playerClient = SocketServer.getInstance().getPlayer(i);
				ans = ans && !playerClient.isCancelled();
			}
		}
		
		return ans;
	}
	
}
