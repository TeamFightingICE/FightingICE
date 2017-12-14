package struct;

import java.util.Deque;
import java.util.LinkedList;

import input.KeyData;
import setting.GameSetting;

/**
 * 現在のフレーム番号やラウンド数, キャラクター情報など, ゲーム内の可変情報を扱うクラス．
 */
public class FrameData {

	/**
	 * The character's data of both characters<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private CharacterData[] characterData;

	/**
	 * The current frame of the round.
	 */
	private int currentFrameNumber;

	/**
	 * The current round number.
	 */
	private int currentRound;

	/**
	 * The projectile data of both characters.
	 */
	private Deque<AttackData> projectileData;

	/**
	 * The input information of both characters.
	 */
	private KeyData keyData;

	/**
	 * If this value is true, no data are available or they are dummy data.
	 */
	private boolean emptyFlag;

	/**
	 * クラスコンストラクタ．
	 */
	public FrameData() {
		this.characterData = new CharacterData[] { null, null };
		this.currentFrameNumber = -1;
		this.currentRound = -1;
		this.projectileData = new LinkedList<AttackData>();
		this.keyData = null;
		this.emptyFlag = true;

	}

	/**
	 * 引数として渡されたデータを用いてFrameDataクラスの新たなインスタンスを生成するクラスコンストラクタ．
	 *
	 * @param characterData
	 *            現在のキャラクター情報を持つCharacterDataクラスのインスタンス
	 * @param currentFrame
	 *            現在のフレーム数
	 * @param currentRound
	 *            現在のラウンド数
	 * @param projectileData
	 *            P1とP2の波動拳の情報を格納するキュー
	 * @param keyData
	 *            P1とP2のキー入力
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
	 * 引数で渡されたFrameDataクラスのインスタンスのコピーを生成するコピーコンストラクタ．
	 *
	 * @param frameData
	 *            指定されたFrameDataクラスのインスタンス
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
	 * 引数で指定したプレイヤーのCharacterDataクラスのインスタンスを返す．
	 *
	 * @param playerNumber
	 *            プレイヤー番号．
	 *            {@code true} if the player is P1, or {@code false} if P2.
	 * @return 指定したプレイヤーのCharacterDataクラスのインスタンス
	 */
	public CharacterData getCharacter(boolean playerNumber) {
		CharacterData temp = this.characterData[playerNumber ? 0 : 1];

		return temp == null ? null : new CharacterData(temp);
	}

	/**
	 * Returns the expected remaining time in milliseconds of the current round.
	 *
	 * @return the expected remaining time in milliseconds of the current round
	 */
	public int getRemainingTimeMilliseconds() {
		return GameSetting.ROUND_TIME - (int) (((float) this.currentFrameNumber / GameSetting.FPS) * 1000);
	}

	/**
	 * Returns the expected remaining time in seconds of the current round.
	 *
	 * @return the expected remaining time in seconds of the current round
	 * @deprecated Use {@link #getRemainingTimeMilliseconds()} instead. This
	 *             method has been renamed to more clearly reflect its purpose.
	 */
	public int getRemainingTime() {
		return (int) Math.ceil((float) getRemainingTimeMilliseconds() / 1000);
	}

	/**
	 * Returns the number of remaining frames of the round.
	 *
	 * @return the number of remaining frames of the round
	 */
	public int getRemainingFramesNumber() {
		return (GameSetting.ROUND_FRAME_NUMBER - currentFrameNumber);
	}

	/**
	 * Returns the number of frames since the beginning of the round.
	 *
	 * @return the number of frames since the beginning of the round
	 */
	public int getFramesNumber() {
		return this.currentFrameNumber;
	}

	/**
	 * Returns the current round number.
	 *
	 * @return the current round number
	 */
	public int getRound() {
		return this.currentRound;
	}

	/**
	 * Returns the projectile data of both characters.
	 *
	 * @return the projectile data of both characters
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
	 * @return the projectile data of player 1
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
	 * @return the projectile data of player 2
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
	 * @return the input information of both characters
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
	 * @return {@code true} if this instance is empty, or {@code false} if it contains
	 *         meaningful data
	 */
	public boolean getEmptyFlag() {
		return this.emptyFlag;
	}

	/**
	 * P1,P2間の水平方向の距離を返す．
	 *
	 * @return P1,P2間の水平方向の距離
	 */
	public int getDistanceX() {
		return Math.abs((this.characterData[0].getCenterX() - this.characterData[1].getCenterX()));
	}

	/**
	 * P1,P2間の鉛直方向の距離を返す．
	 *
	 * @return P1,P2間の鉛直方向の距離
	 */
	public int getDistanceY() {
		return Math.abs((this.characterData[0].getCenterY() - this.characterData[1].getCenterY()));
	}

}
