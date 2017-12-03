package struct;

import java.util.Deque;
import java.util.LinkedList;

import enumerate.Action;
import enumerate.State;
import fighting.Character;

public class CharacterData {

	/**
	 * The character's side.<br>
	 * True: the character is P1; false: the character is P2.
	 */
	private boolean playerNumber;

	/** The character's HP. */
	private int hp;

	/** The character's energy. */
	private int energy;

	/** The character graphic's most top-left x-coordinate. */
	private int x;

	/** The character graphic's most top-left y-coordinate. */
	private int y;

	/** The character's hit box's most-left x-coordinate. */
	private int left;

	/** The character's hit box's most-right x-coordinate. */
	private int right;

	/** The character's hit box's most-top y-coordinate. */
	private int top;

	/** The character's hit box's most-bottom y-coordinate. */
	private int bottom;

	/** The character's horizontal speed. */
	private int speedX;

	/** The character's vertical speed. */
	private int speedY;

	/** The character's state: STAND / CROUCH/ AIR / DOWN. */
	private State state;

	/** The character's action. */
	private Action action;

	/**
	 * The character's facing direction<br>
	 * true: facing right; false: facing left).
	 */
	private boolean front;

	/**
	 * The flag whether this character is able to control (true) or not (false).
	 */
	private boolean control;

	/** The attack data that the character is using. */
	private AttackData attackData;

	/**
	 * The number of frames that the character needs to resume to its normal
	 * status.
	 */
	private int remainingFrame;

	/** A boolean value whether the motion hits the opponent or not */
	private boolean hitConfirm;

	/** The character's graphic width. */
	private int graphicSizeX;

	/** The character's graphic height. */
	private int graphicSizeY;

	/** キャラクターの正面判定時に,x座標を調整するために用いる水平方向の移動量 */
	private int graphicAdjustX;

	/** 相手に攻撃が連続でhitしている回数 */
	private int hitCount;

	/** 最後の攻撃が当たった時のフレームナンバー */
	private int lastHitFrame;

	/**
	 * The list storing keys of the action that the character will be executing
	 * in the simulator.
	 */
	private Deque<Key> inputCommands;

	/**
	 * The list storing up to 30 keys that the character executed in the
	 * simulator.
	 */
	private Deque<Key> processedCommands;

	public CharacterData(Character character) {
		this.playerNumber = character.isPlayerNumber();
		this.hp = character.getHp();
		this.energy = character.getEnergy();
		this.x = character.getX();
		this.y = character.getY();
		this.graphicSizeX = character.getGraphicSizeX();
		this.graphicSizeY = character.getGraphicSizeY();
		this.graphicAdjustX = character.getGraphicAdjustX();
		this.left = character.getHitAreaLeft();
		this.right = character.getHitAreaRight();
		this.top = character.getHitAreaTop();
		this.bottom = character.getHitAreaBottom();
		this.speedX = character.getSpeedX();
		this.speedY = character.getSpeedY();
		this.state = character.getState();
		this.action = character.getAction();
		this.front = character.isFront();
		this.control = character.isControl();
		this.attackData = new AttackData(character.getAttack());
		this.remainingFrame = character.getRemainingFrame();
		this.hitConfirm = character.isHitConfirm();
		this.hitCount = character.getHitCount();
		this.lastHitFrame = character.getLastHitFrame();
	}

	// Copy constructor for the CharacterData class
	public CharacterData(CharacterData characterData) {
		this.playerNumber = characterData.isPlayerNumber();
		this.hp = characterData.getHp();
		this.energy = characterData.getEnergy();
		this.x = characterData.getX();
		this.y = characterData.getY();
		this.graphicSizeX = characterData.getGraphicSizeX();
		this.graphicSizeY = characterData.getGraphicSizeY();
		this.graphicAdjustX = characterData.getGraphicAdjustX();
		this.left = characterData.getLeft();
		this.right = characterData.getRight();
		this.top = characterData.getTop();
		this.bottom = characterData.getBottom();
		this.speedX = characterData.getSpeedX();
		this.speedY = characterData.getSpeedY();
		this.state = characterData.getState();
		this.action = characterData.getAction();
		this.front = characterData.isFront();
		this.control = characterData.isControl();
		this.attackData = new AttackData(characterData.getAttack());
		this.remainingFrame = characterData.getRemainingFrame();
		this.hitConfirm = characterData.isHitConfirm();
		this.hitCount = characterData.getHitCount();
		this.lastHitFrame = characterData.getLastHitFrame();
	}

	/**
	 * Returns the character's side.<br>
	 * True: the character is P1; false: the character is P2.
	 *
	 * @return the character's side
	 */
	public boolean isPlayerNumber() {
		return this.playerNumber;
	}

	/**
	 * Returns the character's facing direction.
	 *
	 * @return The character's facing direction (true for facing right; false
	 *         for facing left).
	 */
	public boolean isFront() {
		return this.front;
	}

	/**
	 * Returns the flag whether this character can run a new motion with the
	 * motion's command.
	 *
	 * @return The flag whether this character is able to control (true) or not
	 *         (false).
	 */
	public boolean isControl() {
		return this.control;
	}

	/**
	 * Returns the character's HP.
	 *
	 * @return The character's HP
	 */
	public int getHp() {
		return this.hp;
	}

	/**
	 * Returns the character's energy.
	 *
	 * @return The character's energy
	 */
	public int getEnergy() {
		return this.energy;
	}

	/**
	 * Returns the character graphic's most top-left x-coordinate.
	 *
	 * @return The character graphic's most top-left x-coordinate.
	 * @deprecated Use {@link #getLeft()} or {@link #getRight()} or
	 *             {@link #getCenterX()} instead
	 *
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Returns the character graphic's most top-left y-coordinate.
	 *
	 * @return The character graphic's most top-left y-coordinate.
	 * @deprecated Use {@link #getTop()} or {@link #getBottom()} or
	 *             {@link #getCenterY()} instead
	 *
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Returns the character's hit box's most-left x-coordinate.<br>
	 *
	 * @return The character's hit box's most-left x-coordinate
	 */
	public int getLeft() {
		return this.left;
	}

	/**
	 * Returns the character's hit box's most-right x-coordinate.<br>
	 *
	 * @return The character's hit box's most-right x-coordinate
	 */
	public int getRight() {
		return this.right;
	}

	/**
	 * Returns the character's hit box's most-top y-coordinate.<br>
	 *
	 * @return The character's hit box's most-top y-coordinate
	 */
	public int getTop() {
		return this.top;
	}

	/**
	 * Returns the character's hit box's most-bottom y-coordinate.<br>
	 *
	 * @return The character's hit box's most-bottom y-coordinate
	 */
	public int getBottom() {
		return this.bottom;
	}

	/**
	 * Returns the character's hit box's center x-coordinate.<br>
	 *
	 * @return The character's hit box's center x-coordinate
	 */
	public int getCenterX() {
		return (getLeft() + getRight()) / 2;
	}

	/**
	 * Returns the character's hit box's center y-coordinate.<br>
	 *
	 * @return The character's hit box's center y-coordinate
	 */
	public int getCenterY() {
		return (getTop() + getBottom()) / 2;
	}

	/**
	 * Returns the character's horizontal speed.
	 *
	 * @return The character's horizontal speed
	 */
	public int getSpeedX() {
		return this.speedX;
	}

	/**
	 * Returns the character's vertical speed.
	 *
	 * @return The character's vertical speed
	 */
	public int getSpeedY() {
		return this.speedY;
	}

	/**
	 * Returns the character's state: STAND / CROUCH/ AIR / DOWN.
	 *
	 * @return The character's state: STAND / CROUCH/ AIR / DOWN
	 */
	public State getState() {
		return this.state;
	}

	/**
	 * Returns the character's action.
	 *
	 * @return The character's action
	 */
	public Action getAction() {
		return this.action;
	}

	/**
	 * Returns the number of frames that the character needs to resume to its
	 * normal status.
	 *
	 * @return The number of frames that the character needs to resume to its
	 *         normal status
	 */
	public int getRemainingFrame() {
		return this.remainingFrame;
	}

	/**
	 * Returns the attack data that the character is using.
	 *
	 * @return The attack data that the character is using
	 */
	public AttackData getAttack() {
		return new AttackData(this.attackData);
	}

	/**
	 * Returns the character's graphic width.
	 *
	 * @return The character's graphic width.
	 */
	public int getGraphicSizeX() {
		return this.graphicSizeX;
	}

	/**
	 * Returns the character's graphic height.
	 *
	 * @return The character's graphic height.
	 */
	public int getGraphicSizeY() {
		return this.graphicSizeY;
	}

	/**
	 * x座標を調整するために用いる水平方向の移動量を返す.<br>
	 * キャラクターの正面判定時に用いられる.
	 *
	 * @return x座標を調整するために用いる水平方向の移動量
	 */
	public int getGraphicAdjustX() {
		return this.graphicAdjustX;
	}

	/**
	 * Returns a boolean value whether the motion hits the opponent or not
	 *
	 * @return A boolean value whether the motion hits the opponent (true) or
	 *         not (false)
	 */
	public boolean isHitConfirm() {
		return this.hitConfirm;
	}

	/**
	 * 相手に攻撃が何回連続でhitしているかを返す.
	 *
	 * @return 連続でヒットしている回数
	 */
	public int getHitCount() {
		return this.hitCount;
	}

	/**
	 * 最後の攻撃が当たった時のフレームナンバーを返す.
	 *
	 * @return 最後の攻撃が当たった時のフレームナンバー
	 */
	public int getLastHitFrame() {
		return this.lastHitFrame;
	}

	/**
	 * Returns a list storing keys of the action that the character will be
	 * executing in the simulator
	 *
	 * @deprecated This method is used only for processing of the simulator. You
	 *             should not use this method for AI development.
	 *
	 * @return A list storing keys of the action that the character will be
	 *         executing in the simulator
	 */
	public Deque<Key> getInputCommand() {
		LinkedList<Key> temp = new LinkedList<Key>();
		for (Key key : this.inputCommands) {
			temp.add(key);
		}

		return temp;
	}

	/**
	 * Returns a list storing up to 30 keys that the character executed in the
	 * simulator
	 *
	 * @deprecated This method is used only for processing of the simulator. You
	 *             should not use this method for AI development.
	 *
	 * @return A list storing up to 30 keys that the character executed in the
	 *         simulator
	 */
	public Deque<Key> getProcessedCommand() {
		LinkedList<Key> temp = new LinkedList<Key>();
		for (Key key : this.processedCommands) {
			temp.add(key);
		}

		return temp;
	}

	/**
	 * Sets the character's HP.
	 *
	 * @param hp
	 *            Amount of HP.
	 */
	public void setHp(int hp) {
		this.hp = hp;
	}

	/**
	 * Sets the character's energy.
	 *
	 * @param energy
	 *            Amount of energy.
	 */
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	/**
	 * Sets the character's horizontal position.
	 *
	 * @param x
	 *            Horizontal value.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the character's vertical position.
	 *
	 * @param y
	 *            Vertical value.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Sets the character's horizontal speed.
	 *
	 * @param speedX
	 *            Horizontal speed.
	 */
	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	/**
	 * Sets the character's vertical speed.
	 *
	 * @param speedY
	 *            Vertical speed.
	 */
	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	/**
	 * Sets the character's state.
	 *
	 * @param state
	 *            A given state.
	 * @see State
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Sets the character's action.
	 *
	 * @param action
	 *            A given action.
	 * @see Action
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * Sets the character's facing direction.
	 *
	 * @param front
	 *            The character's facing direction (true for facing right; false
	 *            for facing left).
	 */
	public void setFront(boolean front) {
		this.front = front;
	}

	/**
	 * Sets the flag whether this character can run a new motion with the
	 * motion's command.
	 *
	 * @param control
	 *            The boolean value to set (true if the character can run a
	 *            motion, false otherwise).
	 */
	public void setControl(boolean control) {
		this.control = control;
	}

	/**
	 * Sets the number of frames that the character needs to resume to its
	 * normal status.
	 *
	 * @param remainingFrame
	 *            The number of frames that the character needs to resume to its
	 *            normal status you want to set.
	 */
	public void setRemainingFrame(int remainingFrame) {
		this.remainingFrame = remainingFrame;
	}

	/**
	 * Sets the character's hit box's most-top y-coordinate
	 *
	 * @param top
	 *            The integer value you want to set.
	 */
	public void setTop(int top) {
		this.top = top;
	}

	/**
	 * Sets the character's hit box's most-bottom y-coordinate
	 *
	 * @param bottom
	 *            The integer value you want to set.
	 */
	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	/**
	 * Sets the character's hit box's most-left x-coordinate
	 *
	 * @param left
	 *            The character's hit box's most-left x-coordinate you want to
	 *            set.
	 */
	public void setLeft(int left) {
		this.left = left;
	}

	/**
	 * Sets the character's hit box's most-right x-coordinate
	 *
	 * @param right
	 *            The character's hit box's most-right x-coordinate you want to
	 *            set.
	 */
	public void setRight(int right) {
		this.right = right;
	}

	/**
	 * Sets the character's attack.
	 *
	 * @param attack
	 *            The attack you want to set.
	 */
	public void setAttack(AttackData attack) {
		this.attackData = attack;
	}

	/**
	 * 相手に攻撃が連続でhitしている回数をセットする.
	 *
	 * @param hitCount
	 *            連続でヒットしている回数
	 */
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	/**
	 * 最後の攻撃が当たった時のフレームナンバーをセットする.
	 *
	 * @return 最後の攻撃が当たった時のフレームナンバー
	 */
	public void setLastHitFrame(int lastHitFrame) {
		this.lastHitFrame = lastHitFrame;
	}

	/**
	 * Sets a boolean value whether the motion hits the opponent or not
	 *
	 * @param hitConfirm
	 *            A boolean value whether the motion hits the opponent or not
	 */
	public void setHitConfirm(boolean hitConfirm) {
		this.hitConfirm = hitConfirm;
	}

	/**
	 * Sets a list storing keys of the action that the character will be
	 * executing in the simulator
	 *
	 * @deprecated This method is used only for processing of the simulator. You
	 *             should not use this method for AI development.
	 *
	 * @param inputCommand
	 *            A list storing keys of the action that the character will be
	 *            executing in the simulator
	 */
	public void setInputCommand(Deque<Key> inputCommand) {
		this.inputCommands = new LinkedList<Key>(inputCommand);
	}

	/**
	 * Sets a list storing up to 30 keys that the character executed in the
	 * simulator
	 *
	 * @deprecated This method is used only for processing of the simulator. You
	 *             should not use this method for AI development.
	 *
	 * @param inputCommand
	 *            A list storing up to 30 keys that the character executed in
	 *            the simulator
	 */
	public void setProcessedCommand(Deque<Key> inputCommand) {
		this.processedCommands = new LinkedList<Key>(inputCommand);
	}

}