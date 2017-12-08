package python;

import manager.InputManager;
import setting.FlagSetting;
import setting.LaunchSetting;

public class PyGame {

	private String[] characterNames;
	private String[] aiNames;
	private int num;

	public Object end;

	public PyGame(PyManager manager, String c1, String c2, String name1, String name2, int num) {
		this.characterNames = new String[2];
		this.aiNames = new String[2];
		this.characterNames[0] = c1;
		this.characterNames[1] = c2;
		this.aiNames[0] = name1;
		this.aiNames[1] = name2;
		this.num = num;

		this.end = new Object();

		LaunchSetting.deviceTypes[0] = InputManager.DEVICE_TYPE_AI;
		LaunchSetting.deviceTypes[1] = InputManager.DEVICE_TYPE_AI;
		LaunchSetting.characterNames[0] = c1;
		LaunchSetting.characterNames[1] = c2;
		LaunchSetting.aiNames[0] = name1;
		LaunchSetting.aiNames[1] = name2;
		LaunchSetting.repeatNumber = num;

		if (LaunchSetting.repeatNumber > 1) {
			FlagSetting.automationFlag = true;
		}
	}

	public String getCharacterName(boolean playerNumber) {
		return playerNumber ? this.characterNames[0] : this.characterNames[1];
	}

	public String getAIName(boolean playerNumber) {
		return playerNumber ? this.aiNames[0] : this.aiNames[1];
	}

	public int getRepeatCount() {
		return this.num;
	}

}
