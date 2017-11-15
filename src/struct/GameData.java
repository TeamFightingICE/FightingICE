package struct;

import java.util.ArrayList;

import org.javatuples.Triplet;

import enumerate.Action;
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

	private ArrayList<ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>>> comboTable;

	public GameData() {
		this.characterMotions = new ArrayList<ArrayList<MotionData>>(2);
		this.characterNames = new String[2];
		this.aiNames = new String[2];
		this.comboTable = new ArrayList<ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>>>(2);
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

			// コンボテーブルを格納
			this.comboTable.add(players[i].getComboTable());
		}
		this.simulator = new Simulator(this);
	}

	/** Getter */

	public ArrayList<MotionData> getCharacterMotionData(boolean playerNumber) {
		ArrayList<MotionData> temp = new ArrayList<MotionData>();
		ArrayList<MotionData> copy = this.characterMotions.get(playerNumber ? 0 : 1);

		for (MotionData motionData : copy) {
			temp.add(motionData);
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
		return new Simulator(this.simulator);
	}

	public ArrayList<ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>>> getComboTable() {
		return (ArrayList<ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>>>) this.comboTable.clone();
	}

}
