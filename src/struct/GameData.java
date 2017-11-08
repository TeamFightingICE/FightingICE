package struct;

import java.util.ArrayList;
import java.util.Vector;

import org.javatuples.Triplet;

import enumerate.Action;
import fighting.Character;
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

	private ArrayList<Vector<Triplet<Vector<Action>, Vector<Action>, Integer>>> comboTable;

	public GameData() {
		this.characterMotions = new ArrayList<ArrayList<MotionData>>(2);
		this.characterNames = new String[2];
		this.aiNames = new String[2];
		this.comboTable = new ArrayList<Vector<Triplet<Vector<Action>, Vector<Action>, Integer>>>(2);
	}

	public GameData(Character[] players) {
		this();

		for (int i = 0; i < 2; i++) {
			this.characterMotions.add(new ArrayList<MotionData>());
			this.characterNames[i] = LaunchSetting.characterNames[i];
			this.aiNames[i] = LaunchSetting.aiNames[i];

			// 各アクションのMotionDataを格納
			for (Action act : Action.values()) {
				// MotionData motionData = new
				// MotionData(players[i].getMotion.get(act.ordinal()));
				// characterMotions.get(i).add(motionData);
			}

			// コンボテーブルを格納
			// this.comboTable.add(players[i].getComboTable());
		}
		this.simulator = new Simulator(this);
	}

	/** Getter */

	public ArrayList<MotionData> getCharacterMotionData(boolean playerNumber) {
		return playerNumber ? new ArrayList<MotionData>(characterMotions.get(0))
				: new ArrayList<MotionData>(characterMotions.get(1));
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

	public ArrayList<Vector<Triplet<Vector<Action>, Vector<Action>, Integer>>> getComboTable() {
		return (ArrayList<Vector<Triplet<Vector<Action>, Vector<Action>, Integer>>>) this.comboTable.clone();
	}

}
