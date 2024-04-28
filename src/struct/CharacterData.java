package struct;

import java.util.Deque;
import java.util.LinkedList;

import enumerate.Action;
import enumerate.State;
import fighting.Character;
import protoc.MessageProto.GrpcCharacterData;
import setting.LaunchSetting;

/**
 * The class dealing with data on the character such as the character's HP,
 * energy, coordinates.
 */
public class CharacterData {

	/**
	 * The character's side flag.<br>
	 * {@code true} if the character is P1, or {@code false} if P2.
	 */
	private boolean playerNumber;

	/**
	 * The character's HP.
	 */
	private int hp;

	/**
	 * The character's energy.
	 */
	private int energy;

	/**
	 * The character graphic's most top-left x-coordinate.
	 */
	private int x;

	/**
	 * The character graphic's most top-left y-coordinate.
	 */
	private int y;

	/**
	 * The character's hit box's most-left x-coordinate.
	 */
	private int left;

	/**
	 * The character's hit box's most-right x-coordinate.
	 */
	private int right;

	/**
	 * The character's hit box's most-top y-coordinate.
	 */
	private int top;

	/**
	 * The character's hit box's most-bottom y-coordinate.
	 */
	private int bottom;

	/**
	 * The character's horizontal speed.
	 */
	private int speedX;

	/**
	 * The character's vertical speed.
	 */
	private int speedY;

	/**
	 * The character's state: STAND / CROUCH / AIR / DOWN.
	 */
	private State state;

	/**
	 * The character's action.
	 */
	private Action action;

	/**
	 * The character's facing direction.<br>
	 * {@code true} if the character is facing right, {@code false} otherwise.
	 */
	private boolean front;

	/**
	 * The flag whether this character can run a new motion with the motion's
	 * command.<br>
	 * {@code true} if the character can run, {@code false} otherwise.
	 */
	private boolean control;

	/**
	 * The attack data that the character is using.
	 */
	private AttackData attackData;

	/**
	 * The number of frames that the character needs to resume to its normal
	 * status.
	 */
	private int remainingFrame;

	/**
	 * The flag whether the motion hits the opponent or not. {@code true} if the
	 * motion hits the opponent, {@code false} otherwise.
	 */
	private boolean hitConfirm;

	/**
	 * The character's graphic width.
	 */
	private int graphicSizeX;

	/**
	 * The character's graphic height.
	 */
	private int graphicSizeY;

	/**
	 * The amount of movement in the horizontal direction used to adjust the x
	 * coordinate when determining the direction of the character.
	 */
	private int graphicAdjustX;

	/**
	 * The continuous hit count of attacks used by this character.
	 */
	private int hitCount;

	/**
	 * The frame number of the last frame that an attack used by this character
	 * hit the opponent.
	 */
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
	
	private AttackData[] projectileAttack;
	
	private void initializeArray() {
		this.projectileAttack = new AttackData[3];
	}

	/**
	 * The class constructor that acquires information on the character from an
	 * instance of the Character class passed as the argument and newly creates
	 * an instance of the CharacterData class.
	 *
	 * @param character
	 *            an instance of Character class
	 */
	public CharacterData(Character character) {
		initializeArray();
		
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
		this.inputCommands = character.getInputCommand();
		this.processedCommands = character.getProcessedCommand();
		
		for (int i = 0; i < 3; i++) {
			this.projectileAttack[i] = new AttackData(character.getProjectileAttack(i));
		}
	}

	/**
	 * The copy constructor that creates a copy of an instance of the
	 * CharacterData class passed as the argument.
	 *
	 * @param characterData
	 *            an instance of CharacterData class
	 */
	public CharacterData(CharacterData characterData) {
		initializeArray();
		
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
		this.inputCommands = characterData.getInputCommand();
		this.processedCommands = characterData.getProcessedCommand();
		
		for (int i = 0; i < 3; i++) {
			this.projectileAttack[i] = new AttackData(characterData.getProjectileAttack(i));
		}
	}

	/**
	 * Returns the character's side flag.
	 *
	 * @return {@code true} if the character is P1, or {@code false} if P2
	 */
	public boolean isPlayerNumber() {
		return this.playerNumber;
	}

	/**
	 * Returns the character's facing direction.
	 *
	 * @return {@code true} if the character is facing right, {@code false}
	 *         otherwise
	 */
	public boolean isFront() {
		return this.front;
	}

	/**
	 * Returns the flag whether this character can run a new motion with the
	 * motion's command.
	 *
	 * @return {@code true} if the character can run, {@code false} otherwise
	 */
	public boolean isControl() {
		return this.control;
	}

	/**
	 * Returns the character's HP.
	 *
	 * @return the character's HP
	 */
	public int getHp() {
		return this.hp;
	}

    /**
     * Returns the character's HP percentage.
     *
     * @return the character's HP percentage
     */
    public float getHpPercentage() {
    	return (float) this.hp / LaunchSetting.maxHp[playerNumber ? 0 : 1];
    }

	/**
	 * Returns the character's energy.
	 *
	 * @return the character's energy
	 */
	public int getEnergy() {
		return this.energy;
	}
    
    /**
     * Returns the character's energy percentage.
     *
     * @return the character's energy percentage
     */
    public float getEnergyPercentage() {
    	return (float) this.energy / LaunchSetting.maxEnergy[playerNumber ? 0 : 1];
    }

	/**
	 * Returns the character graphic's most top-left x-coordinate.
	 *
	 * @return the character graphic's most top-left x-coordinate
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
	 * @return the character graphic's most top-left y-coordinate
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
	 * @return the character's hit box's most-left x-coordinate
	 */
	public int getLeft() {
		return this.left;
	}

	/**
	 * Returns the character's hit box's most-right x-coordinate.<br>
	 *
	 * @return the character's hit box's most-right x-coordinate
	 */
	public int getRight() {
		return this.right;
	}

	/**
	 * Returns the character's hit box's most-top y-coordinate.<br>
	 *
	 * @return the character's hit box's most-top y-coordinate
	 */
	public int getTop() {
		return this.top;
	}

	/**
	 * Returns the character's hit box's most-bottom y-coordinate.<br>
	 *
	 * @return the character's hit box's most-bottom y-coordinate
	 */
	public int getBottom() {
		return this.bottom;
	}

	/**
	 * Returns the character's hit box's center x-coordinate.<br>
	 *
	 * @return the character's hit box's center x-coordinate
	 */
	public int getCenterX() {
		return (getLeft() + getRight()) / 2;
	}

	/**
	 * Returns the character's hit box's center y-coordinate.<br>
	 *
	 * @return the character's hit box's center y-coordinate
	 */
	public int getCenterY() {
		return (getTop() + getBottom()) / 2;
	}

	/**
	 * Returns the character's horizontal speed.
	 *
	 * @return the character's horizontal speed
	 */
	public int getSpeedX() {
		return this.speedX;
	}

	/**
	 * Returns the character's vertical speed.
	 *
	 * @return the character's vertical speed
	 */
	public int getSpeedY() {
		return this.speedY;
	}

	/**
	 * Returns the character's state: STAND / CROUCH/ AIR / DOWN.
	 *
	 * @return the character's state: STAND / CROUCH/ AIR / DOWN
	 *
	 * @see State
	 */
	public State getState() {
		return this.state;
	}

	/**
	 * Returns the character's action.
	 *
	 * @return the character's action
	 *
	 * @see Action
	 */
	public Action getAction() {
		return this.action;
	}

	/**
	 * Returns the number of frames that the character needs to resume to its
	 * normal status.
	 *
	 * @return the number of frames that the character needs to resume to its
	 *         normal status
	 */
	public int getRemainingFrame() {
		return this.remainingFrame;
	}

	/**
	 * Returns the attack data that the character is using.
	 *
	 * @return the attack data that the character is using
	 *
	 * @see AttackData
	 */
	public AttackData getAttack() {
		return new AttackData(this.attackData);
	}

	/**
	 * Returns the character's graphic width.
	 *
	 * @return the character's graphic width
	 */
	public int getGraphicSizeX() {
		return this.graphicSizeX;
	}

	/**
	 * Returns the character's graphic height.
	 *
	 * @return the character's graphic height
	 */
	public int getGraphicSizeY() {
		return this.graphicSizeY;
	}

	/**
	 * Returns the amount of movement in the horizontal direction used to adjust
	 * the x coordinate when determining the direction of the character.
	 *
	 * @return the amount of movement in the horizontal direction used to adjust
	 *         the x coordinate
	 */
	public int getGraphicAdjustX() {
		return this.graphicAdjustX;
	}

	/**
	 * Returns the flag whether the motion hits the opponent or not.
	 *
	 * @return {@code true} if the motion hits the opponent, {@code false}
	 *         otherwise
	 */
	public boolean isHitConfirm() {
		return this.hitConfirm;
	}

	/**
	 * Returns the continuous hit count of attacks used by this character.
	 *
	 * @return the continuous hit count of attacks used by this character
	 *
	 */
	public int getHitCount() {
		return this.hitCount;
	}

	/**
	 * Returns the frame number of the last frame that an attack used by this
	 * character hit the opponent.
	 *
	 * @return the frame number of the last frame that an attack used by this
	 *         character hit the opponent
	 */
	public int getLastHitFrame() {
		return this.lastHitFrame;
	}

	/**
	 * Returns a list storing keys of the action that the character will be
	 * executing in the simulator.
	 *
	 * @deprecated This method is used only for processing of the simulator. You
	 *             should not use this method for AI development.
	 *
	 * @return a list storing keys of the action that the character will be
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
	 * simulator.
	 *
	 * @deprecated This method is used only for processing of the simulator. You
	 *             should not use this method for AI development.
	 *
	 * @return a list storing up to 30 keys that the character executed in the
	 *         simulator
	 */
	public Deque<Key> getProcessedCommand() {
		LinkedList<Key> temp = new LinkedList<Key>();
		for (Key key : this.processedCommands) {
			temp.add(key);
		}

		return temp;
	}
	
	public AttackData getProjectileAttack(int index) {
		return this.projectileAttack[index];
	}

	/**
	 * Sets the character's HP.
	 *
	 * @param hp
	 *            the amount of HP
	 */
	public void setHp(int hp) {
		this.hp = hp;
	}

	/**
	 * Sets the character's energy.
	 *
	 * @param energy
	 *            the amount of energy
	 */
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	/**
	 * Sets the character's horizontal position.
	 *
	 * @param x
	 *            the character's horizontal position
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the character's vertical position.
	 *
	 * @param y
	 *            the character's vertical position
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Sets the character's horizontal speed.
	 *
	 * @param speedX
	 *            the character's horizontal speed
	 */
	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	/**
	 * Sets the character's vertical speed.
	 *
	 * @param speedY
	 *            the character's vertical speed
	 */
	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	/**
	 * Sets the character's state.
	 *
	 * @param state
	 *            a given state
	 * @see State
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Sets the character's action.
	 *
	 * @param action
	 *            a given action
	 * @see Action
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * Sets the character's facing direction.
	 *
	 * @param front
	 *            the character's facing direction. {@code true} if the
	 *            character is facing right, {@code false} otherwise.
	 */
	public void setFront(boolean front) {
		this.front = front;
	}

	/**
	 * Sets the flag whether this character can run a new motion with the
	 * motion's command.
	 *
	 * @param control
	 *            the flag whether this character can run a new motion with the
	 *            motion's command. {@code true} if the character can run,
	 *            {@code false} otherwise.
	 */
	public void setControl(boolean control) {
		this.control = control;
	}

	/**
	 * Sets the number of frames that the character needs to resume to its
	 * normal status.
	 *
	 * @param remainingFrame
	 *            the number of frames that the character needs to resume to its
	 *            normal status you want to set
	 */
	public void setRemainingFrame(int remainingFrame) {
		this.remainingFrame = remainingFrame;
	}

	/**
	 * Sets the character's hit box's most-top y-coordinate
	 *
	 * @param top
	 *            the integer value you want to set
	 */
	public void setTop(int top) {
		this.top = top;
	}

	/**
	 * Sets the character's hit box's most-bottom y-coordinate
	 *
	 * @param bottom
	 *            the integer value you want to set
	 */
	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	/**
	 * Sets the character's hit box's most-left x-coordinate
	 *
	 * @param left
	 *            the character's hit box's most-left x-coordinate you want to
	 *            set
	 */
	public void setLeft(int left) {
		this.left = left;
	}

	/**
	 * Sets the character's hit box's most-right x-coordinate.
	 *
	 * @param right
	 *            the character's hit box's most-right x-coordinate you want to
	 *            set
	 */
	public void setRight(int right) {
		this.right = right;
	}

	/**
	 * Sets the character's attack.
	 *
	 * @param attack
	 *            the attack you want to set
	 *
	 * @see AttackData
	 */
	public void setAttack(AttackData attack) {
		this.attackData = attack;
	}

	/**
	 * Sets the continuous hit count of attacks used by this character.
	 *
	 * @param hitCount
	 *            the continuous hit count of attacks used by this character
	 */
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	/**
	 * Sets the frame number of the last frame that an attack used by this
	 * character hit the opponent.
	 *
	 * @param lastHitFrame
	 *            the frame number of the last frame that an attack used by this
	 *            character hit the opponent
	 */
	public void setLastHitFrame(int lastHitFrame) {
		this.lastHitFrame = lastHitFrame;
	}

	/**
	 * Sets a boolean value whether the motion hits the opponent or not.
	 *
	 * @param hitConfirm
	 *            a boolean value whether the motion hits the opponent or not.
	 *            {@code true} if the motion hits the opponent, {@code false}
	 *            otherwise.
	 */
	public void setHitConfirm(boolean hitConfirm) {
		this.hitConfirm = hitConfirm;
	}

	/**
	 * Sets a list storing keys of the action that the character will be
	 * executing in the simulator.
	 *
	 * @deprecated This method is used only for processing of the simulator. You
	 *             should not use this method for AI development.
	 *
	 * @param inputCommand
	 *            a list storing keys of the action that the character will be
	 *            executing in the simulator
	 */
	public void setInputCommand(Deque<Key> inputCommand) {
		this.inputCommands = new LinkedList<Key>(inputCommand);
	}

	/**
	 * Sets a list storing up to 30 keys that the character executed in the
	 * simulator.
	 *
	 * @deprecated This method is used only for processing of the simulator. You
	 *             should not use this method for AI development.
	 *
	 * @param inputCommand
	 *            a list storing up to 30 keys that the character executed in
	 *            the simulator
	 */
	public void setProcessedCommand(Deque<Key> inputCommand) {
		this.processedCommands = new LinkedList<Key>(inputCommand);
	}
	
	public GrpcCharacterData toProto() {
		GrpcCharacterData.Builder builder = GrpcCharacterData.newBuilder();
		builder = builder.setPlayerNumber(this.playerNumber)
				.setHp(this.hp)
  				.setEnergy(this.energy)
  				.setX(this.getCenterX())
  				.setY(this.getCenterY())
  				.setLeft(this.left)
  				.setRight(this.right)
  				.setTop(this.top)
  				.setBottom(this.bottom)
  				.setSpeedX(this.speedX)
  				.setSpeedY(this.speedY)
  				.setStateValue(this.state.ordinal())
  				.setActionValue(this.action.ordinal())
  				.setFront(this.front)
  				.setControl(this.control)
  				.setAttackData(this.attackData.toProto())
  				.setRemainingFrame(this.remainingFrame)
  				.setHitConfirm(this.hitConfirm)
  				.setGraphicSizeX(this.graphicSizeX)
  				.setGraphicSizeY(this.graphicSizeY)
  				.setGraphicAdjustX(this.graphicAdjustX)
  				.setHitCount(this.hitCount)
  				.setLastHitFrame(this.lastHitFrame);
		
		for (int i = 0; i < 3; i++) {
			builder.addProjectileAttack(projectileAttack[i].toProto());
		}
		
		return builder.build();
  	}

}