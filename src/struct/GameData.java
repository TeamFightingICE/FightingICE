package struct;

import java.util.ArrayList;

import fighting.Character;
import fighting.Motion;
import setting.LaunchSetting;
import simulator.Simulator;

public class GameData {
	/**
	 * delete private int
	 * "stageXMax,stageYMax,playerOneMaxEnergy,playerTwoMaxEnergy" ,GameData
	 * Function args"int stageX, int stageY" and public int getter
	 * "getMyMaxEnergy,getOpponentMaxEnergy"
	 */

	private ArrayList<ArrayList<MotionData>> characterMotions;

	private String[] characterNames = new String[2];

	private String[] aiNames = new String[2];

	private Simulator simulator;

	public GameData() {
		this.characterMotions = new ArrayList<ArrayList<MotionData>>(2);
		this.characterNames = new String[2];
		this.aiNames = new String[2];
	}

	public GameData(Character[] players) {
		this();

		for (int i = 0; i < 2; i++) {
			this.characterMotions.add(new ArrayList<MotionData>());
			this.characterNames[i] = LaunchSetting.characterNames[i];
			this.aiNames[i] = LaunchSetting.aiNames[i];

			// 各アクションのMotionDataを格納
			ArrayList<MotionData> motionDataList = new ArrayList<MotionData>();
			ArrayList<Motion> temp = players[i].getMotionList();
			for (Motion motion : temp) {
				motionDataList.add(new MotionData(motion));
			}
			this.characterMotions.add(motionDataList);
		}

		this.simulator = new Simulator(this);
	}

	/** Getter */

	public ArrayList<MotionData> getMotionData(boolean playerNumber) {
		ArrayList<MotionData> temp = new ArrayList<MotionData>();
		ArrayList<MotionData> copy = this.characterMotions.get(playerNumber ? 0 : 1);

		for (MotionData motionData : copy) {
			temp.add(motionData);
		}

		return temp;
	}

	public ArrayList<Motion> getMotion(boolean playerNumber) {
		ArrayList<Motion> temp = new ArrayList<Motion>();
		ArrayList<MotionData> copy = this.characterMotions.get(playerNumber ? 0 : 1);

		for (MotionData motionData : copy) {
			Motion motion = new Motion(motionData);
			temp.add(motion);
		}

		return temp;
	}

	public String getCharacterName(boolean playerNumber) {
		return playerNumber ? this.characterNames[0] : this.characterNames[1];
	}

	public String getAiName(boolean playerNumber) {
		return playerNumber ? this.aiNames[0] : this.aiNames[1];
	}

	public Simulator getSimulator() {
		return this.simulator;
	}
}
