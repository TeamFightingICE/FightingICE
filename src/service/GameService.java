package service;

import java.util.List;

import loader.ResourceLoader;
import manager.InputManager;
import setting.FlagSetting;
import setting.LaunchSetting;

public class GameService {
	
	private boolean runFlag;
	private boolean closeFlag;
	
	private List<String> allAiNames;
	
	public static GameService getInstance() {
        return GameServiceHolder.instance;
    }

    private static class GameServiceHolder {
        private static final GameService instance = new GameService();
    }
	
	public GameService() {
		this.runFlag = false;
		this.closeFlag = false;
		
		this.allAiNames = ResourceLoader.getInstance().loadFileNames("./data/ai", ".jar");
	}
	
	public void setCharacterName(boolean player, String characterName) {
		LaunchSetting.characterNames[player ? 0 : 1] = characterName;
	}
	
	public void setAIName(boolean player, String aiName) {
		int i = player ? 0 : 1;
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
		LaunchSetting.repeatNumber = gameNumber;
		if (gameNumber > 1) {
			FlagSetting.automationFlag = true;
		}
	}
	
	public void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}
	
	public void setCloseFlag(boolean closeFlag) {
		this.closeFlag = closeFlag;
	}
	
	public boolean getRunFlag() {
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
	
	public boolean getCloseFlag() {
		return this.closeFlag;
	}
	
}
