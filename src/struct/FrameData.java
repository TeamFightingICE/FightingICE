package struct;

import java.util.Deque;
import java.util.LinkedList;

import fighting.Attack;
import input.KeyData;
import setting.GameSetting;

public class FrameData {

	private CharacterData[] characterData;

	private int currentFrameNumber;
	/**
	 * The current round number
	 */
	private int currentRound;
	/**
	 * The projectile data of both characters
	 */
	private Deque<Attack> projectileData;
	/**
	 * The value of input information
	 */
	private KeyData keyData;

	/**
	 * If this value is true, no data are available or they are dummy data
	 */
	private boolean emptyFlag;



	/**
	 * This is the default constructor.
	 */
	public FrameData() {
		this.characterData = new CharacterData[2];
		this.currentFrameNumber = -1;
		this.currentRound = -1;
		this.projectileData = new LinkedList<Attack>();
		this.keyData = null;
		this.emptyFlag = true;

	}

	public FrameData(CharacterData[] characterData, int currentFrame, int currentRound, Deque<Attack> projectileData,
			KeyData keyData) {
		this.characterData = new CharacterData[] { characterData[0], characterData[1] };
		this.currentFrameNumber = currentFrame;
		this.currentRound = currentRound;

		// make deep copy of the attacks list
		this.projectileData = new LinkedList<Attack>();
		for (Attack attack : projectileData) {
			 this.projectileData.add(new Attack(attack));
		}

		this.keyData = keyData;
		this.emptyFlag = false;
	}

	// Copy constructor for the FrameData class
	public FrameData(FrameData frameData) {
		this.characterData[0] = frameData.getMyCharacter(true);
		this.characterData[1] = frameData.getMyCharacter(false);
		this.currentFrameNumber = frameData.getCurrentFrameNumber();
		this.currentRound = frameData.getCurrentRound();

		// make deep copy of the attacks list
		this.projectileData = new LinkedList<Attack>();
		Deque<Attack> temp = frameData.getProjectiles();
		for (Attack attack : temp) {
			 this.projectileData.add(new Attack(attack));
		}

		this.keyData = new KeyData(frameData.getKeyData());
		this.emptyFlag = frameData.getEmptyFlag();

	}

	public CharacterData getMyCharacter(boolean playerNumber) {
		return playerNumber ? new CharacterData(this.characterData[0]) : new CharacterData(this.characterData[1]);
	}

	public CharacterData getOpponentCharacter(boolean playerNumber) {
		return playerNumber ? new CharacterData(this.characterData[1]) : new CharacterData(this.characterData[0]);
	}

	/**
	 * Returns the expected remaining time in milliseconds of the current round.
	 *
	 * @return The expected remaining time in milliseconds of the current round
	 */
	public int getRemainingTimeMilliseconds() {
		// Calculate the expected remaining time in milliseconds (based on the
		// current frame)
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
	public int getRemainingFrameNumber() {
		return (GameSetting.ROUND_FRAME_NUMBER - currentFrameNumber);
	}

	/**
	 * Returns the number of frames since the beginning of the round.
	 *
	 * @return The number of frames since the beginning of the round.
	 */
	public int getCurrentFrameNumber() {
		return this.currentFrameNumber;
	}

	/**
	 * Returns the current round number.
	 *
	 * @return The current round number
	 */
	public int getCurrentRound() {
		return this.currentRound;
	}

	/**
	 * Returns the projectile data of both characters.
	 *
	 * @return The projectile data of both characters
	 */
	public Deque<Attack> getProjectiles() {
		// create a deep copy of the attacks list
		LinkedList<Attack> attackList = new LinkedList<Attack>();
		for (Attack attack : this.projectileData) {
			 attackList.add(new Attack(attack));
		}

		return attackList;
	}

	/**
	 * Returns the projectile data of player 1.
	 *
	 * @return The projectile data of player 1
	 */
	public Deque<Attack> getProjectilesByP1() {
		LinkedList<Attack> attackList = new LinkedList<Attack>();
		for (Attack attack : this.projectileData) {
			if (attack.isPlayerNumber()) {
				// attackList.add(new Attack(attack));
			}
		}
		return attackList;
	}

	/**
	 * Returns the projectile data of player 2.
	 *
	 * @return The projectile data of player 2
	 */
	public Deque<Attack> getProjectilesByP2() {
		LinkedList<Attack> attackList = new LinkedList<Attack>();
		for (Attack attack : this.projectileData) {
			if (!attack.isPlayerNumber()) {
				// attackList.add(new Attack(attack));
			}
		}
		return attackList;
	}

	/**
	 * Returns the value of input information.
	 *
	 * @return The value of input information
	 */
	public KeyData getKeyData() {
		return new KeyData(keyData);
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

}
