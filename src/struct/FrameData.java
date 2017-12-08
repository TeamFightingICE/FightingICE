package struct;

import java.util.Deque;
import java.util.LinkedList;

import input.KeyData;
import setting.GameSetting;

/**
 * 現在のフレーム番号やラウンド数, キャラクター情報など, ゲーム内の可変情報を扱うクラス
 */
public class FrameData {

	/**
	 * The character's data of both characters<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private CharacterData[] characterData;

	/**
	 * The current frame of the round
	 */
	private int currentFrameNumber;

	/**
	 * The current round number
	 */
	private int currentRound;

	/**
	 * The projectile data of both characters
	 */
	private Deque<AttackData> projectileData;

	/**
	 * The input information of both characters
	 */
	private KeyData keyData;

	/**
	 * If this value is true, no data are available or they are dummy data
	 */
	private boolean emptyFlag;

	/**
	 * フレームデータを初期化するするコンストラクタ
	 */
	public FrameData() {
		this.characterData = new CharacterData[2];
		this.currentFrameNumber = -1;
		this.currentRound = -1;
		this.projectileData = new LinkedList<AttackData>();
		this.keyData = null;
		this.emptyFlag = true;

	}

	/**
	 * This method receives the data from an instance of the Fighting class. It
	 * sets all the received data to an instance of the FrameData class. This
	 * method must be called in each frame by Play.
	 *
	 * @param characterData
	 *            is player's data
	 * @param currentFrame
	 *            is the current frame in the round.
	 * @param currentRound
	 *            is the current round.
	 * @param projectileData
	 *            is a queue which stores all attacks.
	 * @param keyData
	 *            contains informations of all players.
	 *
	 * @see CharacterData
	 * @see KeyData
	 */
	public FrameData(CharacterData[] characterData, int currentFrame, int currentRound,
			Deque<AttackData> projectileData, KeyData keyData) {
		this.characterData = new CharacterData[] { characterData[0], characterData[1] };
		this.currentFrameNumber = currentFrame;
		this.currentRound = currentRound;

		// make deep copy of the attacks list
		this.projectileData = new LinkedList<AttackData>();
		for (AttackData attack : projectileData) {
			this.projectileData.add(new AttackData(attack));
		}

		this.keyData = keyData;
		this.emptyFlag = false;
	}

	/**
	 * 指定されたデータでフレームデータのインスタンスを作成するコンストラクタ
	 *
	 * @param frameData
	 *            指定されたフレームデータ
	 */
	public FrameData(FrameData frameData) {
		this.characterData = new CharacterData[2];
		this.characterData[0] = frameData.getCharacter(true);
		this.characterData[1] = frameData.getCharacter(false);
		this.currentFrameNumber = frameData.getFramesNumber();
		this.currentRound = frameData.getRound();

		// make deep copy of the attacks list
		this.projectileData = new LinkedList<AttackData>();
		Deque<AttackData> temp = frameData.getProjectiles();
		for (AttackData attack : temp) {
			this.projectileData.add(new AttackData(attack));
		}

		this.keyData = new KeyData(frameData.getKeyData());
		this.emptyFlag = frameData.getEmptyFlag();

	}

	/**
	 * 指定したプレイヤーのキャラクターデータを返すメソッド
	 *
	 * @param playerNumber
	 *            プレイヤー番号(true: P1; false: P2)
	 * @return 自分のキャラクターデータ
	 */
	public CharacterData getCharacter(boolean playerNumber) {
		return playerNumber ? new CharacterData(this.characterData[0]) : new CharacterData(this.characterData[1]);
	}

	/**
	 * Returns the expected remaining time in milliseconds of the current round.
	 *
	 * @return The expected remaining time in milliseconds of the current round
	 */
	public int getRemainingTimeMilliseconds() {
		return GameSetting.ROUND_TIME - (int) (((float) this.currentFrameNumber / GameSetting.FPS) * 1000);
	}

	/**
	 * Returns the expected remaining time in seconds of the current round.
	 *
	 * @return The expected remaining time in seconds of the current round
	 * @deprecated Use {@link #getRemainingTimeMilliseconds()} instead. This
	 *             method has been renamed to more clearly reflect its purpose.
	 */
	public int getRemainingTime() {
		return (int) Math.ceil((float) getRemainingTimeMilliseconds() / 1000);
	}

	/**
	 * Returns the number of remaining frames of the round.
	 *
	 * @return The number of remaining frames of the round.
	 */
	public int getRemainingFramesNumber() {
		return (GameSetting.ROUND_FRAME_NUMBER - currentFrameNumber);
	}

	/**
	 * Returns the number of frames since the beginning of the round.
	 *
	 * @return The number of frames since the beginning of the round.
	 */
	public int getFramesNumber() {
		return this.currentFrameNumber;
	}

	/**
	 * Returns the current round number.
	 *
	 * @return The current round number
	 */
	public int getRound() {
		return this.currentRound;
	}

	/**
	 * Returns the projectile data of both characters.
	 *
	 * @return The projectile data of both characters
	 */
	public Deque<AttackData> getProjectiles() {
		// create a deep copy of the attacks list
		LinkedList<AttackData> attackList = new LinkedList<AttackData>();
		for (AttackData attack : this.projectileData) {
			attackList.add(new AttackData(attack));
		}
		return attackList;
	}

	/**
	 * Returns the projectile data of player 1.
	 *
	 * @return The projectile data of player 1
	 */
	public Deque<AttackData> getProjectilesByP1() {
		LinkedList<AttackData> attackList = new LinkedList<AttackData>();
		for (AttackData attack : this.projectileData) {
			if (attack.isPlayerNumber()) {
				attackList.add(new AttackData(attack));
			}
		}
		return attackList;
	}

	/**
	 * Returns the projectile data of player 2.
	 *
	 * @return The projectile data of player 2
	 */
	public Deque<AttackData> getProjectilesByP2() {
		LinkedList<AttackData> attackList = new LinkedList<AttackData>();
		for (AttackData attack : this.projectileData) {
			if (!attack.isPlayerNumber()) {
				attackList.add(new AttackData(attack));
			}
		}
		return attackList;
	}

	/**
	 * Returns the input information of both characters.
	 *
	 * @return The input information of both characters
	 *
	 * @see KeyData
	 */
	public KeyData getKeyData() {
		return new KeyData(this.keyData);
	}

	/**
	 * Returns true if this instance is empty, false if it contains meaningful
	 * data.
	 *
	 * @return emptyFlag true if this instance is empty, false if it contains
	 *         meaningful data.
	 */
	public boolean getEmptyFlag() {
		return this.emptyFlag;
	}

	/**
	 * P1,P2間の水平方向の距離を返すメソッド
	 *
	 * @return P1,P2間の水平方向の距離
	 */
	public int getDistanceX() {
		return Math.abs((this.characterData[0].getCenterX() - this.characterData[1].getCenterX()));
	}

	/**
	 * P1,P2間の鉛直方向の距離を返すメソッド
	 *
	 * @return P1,P2間の鉛直方向の距離
	 */
	public int getDistanceY() {
		return Math.abs((this.characterData[0].getCenterY() - this.characterData[1].getCenterY()));
	}

}
