package struct;

import java.util.Deque;
import java.util.LinkedList;

import enumerate.Action;
import enumerate.State;
import fighting.Character;

public class CharacterData {


	private boolean playerNumber;

	private int hp;

	private int energy;

	/** キャラクター画像の一番左上のx座標*/
	private int x;

    /** キャラクター画像の一番左上のy座標*/
	private int y;

	private int left;

	private int right;

	private int top;

	private int bottom;

	private int speedX;

	private int speedY;

	private State state;

	private Action action;

	private boolean front;

	private boolean control;

	private AttackData attackData;

	private int remainingFrame;

	private boolean hitConfirm;

	private int graphicSizeX;

	private int graphicSizeY;

	private int graphicCenterX;

	private int graphicCenterY;

	private int hitCount;

	private int lastHitFrame;

	private Deque<Key> inputCommands;

	private Deque<Key> processedCommands;

	public CharacterData(Character character) {
		this.playerNumber = character.isPlayerNumber();
		this.hp = character.getHp();
		this.energy = character.getEnergy();
		this.x = character.getX();
		this.y = character.getY();
		this.graphicSizeX = character.getGraphicSizeX();
		this.graphicSizeY = character.getGraphicSizeY();
		this.graphicCenterX = character.getGraphicCenterX();
		this.graphicCenterY = character.getGraphicCenterY();
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
		this.graphicCenterX = characterData.getGraphicCenterX();
		this.graphicCenterY = characterData.getGraphicCenterY();
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
		this.attackData =new AttackData(characterData.getAttack());
		this.remainingFrame = characterData.getRemainingFrame();
		this.hitConfirm = characterData.isHitConfirm();
		this.hitCount = characterData.getHitCount();
		this.lastHitFrame = characterData.getLastHitFrame();
	}

	public boolean isPlayerNumber() {
		return this.playerNumber;
	}

	public boolean isFront() {
		return this.front;
	}

	public boolean isControl() {
		return this.control;
	}

	////// Getter//////
	public int getHp() {
		return this.hp;
	}

	public int getEnergy() {
		return this.energy;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getLeft() {
		return this.left;
	}

	public int getRight() {
		return this.right;
	}

	public int getTop() {
		return this.top;
	}

	public int getBottom() {
		return this.bottom;
	}

	public int getSpeedX() {
		return this.speedX;
	}

	public int getSpeedY() {
		return this.speedY;
	}

	public State getState() {
		return this.state;
	}

	public Action getAction() {
		return this.action;
	}

	public int getRemainingFrame() {
		return this.remainingFrame;
	}

	public AttackData getAttack() {
		return new AttackData(this.attackData);
	}

	public int getGraphicSizeX() {
		return this.graphicSizeX;
	}

	public int getGraphicSizeY() {
		return this.graphicSizeY;
	}


	public int getHitCount() {
		return this.hitCount;
	}

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

	////// Setter//////
	public void setHp(int hp) {
		this.hp = hp;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void setFront(boolean front) {
		this.front = front;
	}

	public void setControl(boolean control) {
		this.control = control;
	}

	public void setRemainingFrame(int remainingFrame) {
		this.remainingFrame = remainingFrame;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public void setAttack(AttackData attack) {
		this.attackData = attack;
	}

	public void setGraphicSizeX(int graphicSizeX) {
		this.graphicSizeX = graphicSizeX;
	}

	public void setGraphicSizeY(int graphicSizeY) {
		this.graphicSizeY = graphicSizeY;
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public void setLastHitFrame(int lastHitFrame) {
		this.lastHitFrame = lastHitFrame;
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

	public int getGraphicCenterX() {
		return 0;
	}

	public int getGraphicCenterY() {
		return 0;
	}

	public boolean isHitConfirm() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}