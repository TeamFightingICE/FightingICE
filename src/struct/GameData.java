package struct;

import java.util.ArrayList;

import fighting.Character;
import fighting.Motion;
import setting.GameSetting;
import setting.LaunchSetting;
import simulator.Simulator;

/**
 * ステージの画面幅や最大HPなどの、ゲーム内で不変の情報を扱うクラス
 */
public class GameData {

	/**
	 * The horizontal size of the game stage
	 */
	private int stageWidth;

	/**
	 * The vertical size of the game stage
	 */
	private int stageHeight;

	/**
	 * The values of both characters' HP limits.<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private int[] maxHPs;

	/**
	 * The values of both characters' energy limits.<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private int[] maxEnergies;

	/**
	 * The list of MotionData of both characters<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private ArrayList<ArrayList<MotionData>> characterMotions;

	/**
	 * The both characters' names<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private String[] characterNames;

	/**
	 * The both AIs' names<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private String[] aiNames;

	/**
	 * The simulator which simulates the progression of a fight starting from a
	 * given {@link FrameData} instance and executing specified actions for both
	 * players.
	 *
	 * @see Simulator
	 */
	private Simulator simulator;

	/**
	 * ゲームデータを初期化するコンストラクタ
	 */
	public GameData() {
		this.maxHPs = new int[2];
		this.maxEnergies = new int[2];
		this.characterMotions = new ArrayList<ArrayList<MotionData>>(2);
		this.characterNames = new String[2];
		this.aiNames = new String[2];
	}

	/**
	 * キャラクターのデータを用いてゲームデータのインスタンスを作成するコンストラクタ
	 *
	 * @param players
	 *            P1, P2のキャラクターのデータ
	 *
	 * @see Character
	 */
	public GameData(Character[] players) {
		this();

		for (int i = 0; i < 2; i++) {
			// 各アクションのMotionDataを格納
			ArrayList<MotionData> motionDataList = new ArrayList<MotionData>();
			ArrayList<Motion> temp = players[i].getMotionList();
			for (Motion motion : temp) {
				motionDataList.add(new MotionData(motion));
			}

			this.characterMotions.add(motionDataList);
		}

		this.stageWidth = GameSetting.STAGE_WIDTH;
		this.stageHeight = GameSetting.STAGE_HEIGHT;
		this.maxHPs = LaunchSetting.maxHp.clone();
		this.maxEnergies = LaunchSetting.maxEnergy.clone();
		this.characterNames = LaunchSetting.characterNames.clone();
		this.aiNames = LaunchSetting.aiNames.clone();

		this.simulator = new Simulator(this);
	}

	/**
	 * 指定されたプレイヤーのモーションデータを返すメソッド
	 *
	 * @param playerNumber
	 *            プレイヤー番号(true: P1; false: P2)
	 * @return 指定されたプレイヤーのモーションデータ
	 * @see MotionData
	 */
	public ArrayList<MotionData> getMotionData(boolean playerNumber) {
		ArrayList<MotionData> temp = new ArrayList<MotionData>();
		ArrayList<MotionData> copy = this.characterMotions.get(playerNumber ? 0 : 1);

		for (MotionData motionData : copy) {
			temp.add(motionData);
		}
		return temp;
	}

	/**
	 * 指定されたプレイヤーのモーションを返すメソッド<br>
	 *
	 * @deprecated このメソッドはシミュレータの処理で呼び出される.<br>
	 *             AI developerは使用することができない.
	 *
	 * @param playerNumber
	 *            playerNumber プレイヤー番号(true: P1; false: P2)
	 * @return 指定されたプレイヤーのモーション
	 * @see Motion
	 */
	public ArrayList<Motion> getMotion(boolean playerNumber) {
		ArrayList<Motion> temp = new ArrayList<Motion>();
		ArrayList<MotionData> copy = this.characterMotions.get(playerNumber ? 0 : 1);

		for (MotionData motionData : copy) {
			Motion motion = new Motion(motionData);
			temp.add(motion);
		}
		return temp;
	}

	/**
	 * ゲームステージの横幅を返すメソッド
	 *
	 * @return ゲームステージの横幅
	 */
	public int getStageWidth() {
		return this.stageWidth;
	}

	/**
	 * ゲームステージの縦幅を返すメソッド
	 *
	 * @return ゲームステージの縦幅
	 */
	public int getStageHeight() {
		return this.stageHeight;
	}

	/**
	 * 指定されたプレイヤーの最大HPを返すメソッド
	 *
	 * @param playerNumber
	 *            プレイヤー番号(true: P1; false: P2)
	 * @return 指定されたプレイヤーの最大HP
	 */
	public int getMaxHP(boolean playerNumber) {
		return playerNumber ? this.maxHPs[0] : this.maxHPs[1];
	}

	/**
	 * 指定されたプレイヤーの最大エネルギー量を返すメソッド
	 *
	 * @param playerNumber
	 *            プレイヤー番号(true: P1; false: P2)
	 * @return 指定されたプレイヤーの最大エネルギー量
	 */
	public int getMaxEnergy(boolean playerNumber) {
		return playerNumber ? this.maxEnergies[0] : this.maxEnergies[1];
	}

	/**
	 * 指定されたプレイヤーのキャラクターの名前を返すメソッド
	 *
	 * @param playerNumber
	 *            playerNumber プレイヤー番号(true: P1; false: P2)
	 * @return 指定されたプレイヤーのキャラクターの名前
	 */
	public String getCharacterName(boolean playerNumber) {
		return playerNumber ? this.characterNames[0] : this.characterNames[1];
	}

	/**
	 * 指定されたプレイヤーのAIの名前を返すメソッド
	 *
	 * @param playerNumber
	 *            playerNumber プレイヤー番号(true: P1; false: P2)
	 * @return 指定されたプレイヤーのAIの名前
	 */
	public String getAiName(boolean playerNumber) {
		return playerNumber ? this.aiNames[0] : this.aiNames[1];
	}

	/**
	 * シミュレータを返すメソッド
	 *
	 * @return シミュレータ
	 * @see Simulator
	 */
	public Simulator getSimulator() {
		return this.simulator;
	}
}
