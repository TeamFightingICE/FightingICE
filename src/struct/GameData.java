package struct;

import java.util.ArrayList;

import fighting.Character;
import fighting.Motion;
import setting.GameSetting;
import setting.LaunchSetting;
import simulator.Simulator;

/**
 * ゲームデータを扱うクラス
 */
public class GameData {

	private int stageWidth;

	private int stageHeight;

	private int[] maxHPs;

	private int[] maxEnergies;

	private ArrayList<ArrayList<MotionData>> characterMotions;

	private String[] characterNames;

	private String[] aiNames;

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
	 * プレイヤーデータを用いてゲームデータを更新するコンストラクタ
	 *
	 * @param players
	 *            プレイヤーデータ
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

	/** Getter */

	/**
	 * 指定されたプレイヤーのモーションデータを返すメソッド
	 *
	 * @param playerNumber
	 *            プレイヤー番号(P1 or P2)
	 * @return モーションデータ
	 */
	public ArrayList<MotionData> getMotionData(boolean playerNumber) {
		ArrayList<MotionData> temp = new ArrayList<MotionData>();
		ArrayList<MotionData> copy = this.characterMotions.get(playerNumber ? 0 : 1);

		for (MotionData motionData : copy) {
			temp.add(motionData);
		}

		return temp;
	}

	// シミュレータ用
	/**
	 * 指定されたプレイヤーのモーションを返すメソッド
	 *
	 * @param playerNumber
	 *            playerNumber プレイヤー番号(P1 or P2)
	 * @return モーション
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
	 *            プレイヤー番号(P1 or P2)
	 * @return 最大HP
	 */
	public int getMaxHP(boolean playerNumber) {
		return playerNumber ? this.maxHPs[0] : this.maxHPs[1];
	}

	/**
	 * 指定されたプレイヤーの最大エネルギー量を返すメソッド
	 *
	 * @param playerNumber
	 *            プレイヤー番号(P1 or P2)
	 * @return 最大エネルギー量
	 */
	public int getMaxEnergy(boolean playerNumber) {
		return playerNumber ? this.maxEnergies[0] : this.maxEnergies[1];
	}

	/**
	 * 指定されたプレイヤーのキャラクターの名前を返すメソッド
	 *
	 * @param playerNumber
	 *            playerNumber プレイヤー番号(P1 or P2)
	 * @return キャラクターの名前
	 */
	public String getCharacterName(boolean playerNumber) {
		return playerNumber ? this.characterNames[0] : this.characterNames[1];
	}

	/**
	 * 指定されたプレイヤーのAIの名前を返すメソッド
	 *
	 * @param playerNumber
	 *            playerNumber プレイヤー番号(P1 or P2)
	 * @return AIの名前
	 */
	public String getAiName(boolean playerNumber) {
		return playerNumber ? this.aiNames[0] : this.aiNames[1];
	}

	/**
	 * シミュレータを返すメソッド
	 *
	 * @return シミュレータ
	 */
	public Simulator getSimulator() {
		return this.simulator;
	}
}
