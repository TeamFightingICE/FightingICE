package fighting;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import enumerate.Action;
import enumerate.State;
import image.Image;
import loader.ResourceLoader;
import manager.SoundManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.CharacterData;
import struct.HitArea;
import struct.Key;

public class Character {
	private boolean playerNumber;

	private int hp;

	private int energy;

	/** キャラクター画像の最も左端のx座標 */
	private int x;

	/** キャラクター画像の最も上端のy座標 */
	private int y;

	private int speedX;

	private int speedY;

	private State state;

	private Action action;

	private boolean front;

	private boolean control;

	private Attack attack;

	private int remainingFrame;

	/**
	 * Attack hit confirm.<br>
	 */
	private boolean hitConfirm;

	private int graphicSizeX;

	private int graphicSizeY;

	private int graphicCenterX;

	private int graphicCenterY;

	private int lastHitFrame;

	private Deque<Key> inputCommands;

	private Deque<Key> processedCommands;

	private ArrayList<Motion> motionList;

	/** 攻撃の連続ヒット数 */
	private int hitCount;

	public Character() {
		initializeList();

		this.playerNumber = true;
		this.hp = 0;
		this.energy = 0;
		this.x = 0;
		this.y = 0;
		this.graphicSizeX = 0;
		this.graphicSizeY = 0;
		this.graphicCenterX = 0;
		this.graphicCenterY = 0;
		this.speedX = 0;
		this.speedY = 0;
		this.state = State.STAND;
		this.action = Action.NEUTRAL;
		this.hitConfirm = false;
		this.front = true;
		this.control = false;
		this.attack = null;
		this.remainingFrame = 0;
		this.lastHitFrame = 0;
		this.hitCount = 0;
	}

	public Character(Character character) {
		initializeList();

		this.playerNumber = character.isPlayerNumber();
		this.hp = character.getHp();
		this.energy = character.getEnergy();
		this.x = character.getX();
		this.y = character.getY();
		this.graphicSizeX = character.getGraphicSizeX();
		this.graphicSizeY = character.getGraphicSizeY();
		this.graphicCenterX = character.getGraphicCenterX();
		this.graphicCenterY = character.getGraphicCenterY();
		this.speedX = character.getSpeedX();
		this.speedY = character.getSpeedY();
		this.state = character.getState();
		this.action = character.getAction();
		this.hitConfirm = character.isHitConfirm();
		this.front = character.isFront();
		this.control = character.isControl();
		this.attack = character.getAttack();
		this.remainingFrame = character.getRemainingFrame();
		this.inputCommands = character.getInputCommand();
		this.processedCommands = character.getProcessedCommand();
		this.motionList = character.getMotionList();
		this.lastHitFrame = character.getLastHitFrame();
		this.hitCount = character.getHitCount();
	}

	//シミュレータで呼び出す用
	public Character(CharacterData characterData, ArrayList<Motion> motionList) {
		initializeList();

		this.playerNumber = characterData.isPlayerNumber();
		this.hp = characterData.getHp();
		this.energy = characterData.getEnergy();
		this.x = characterData.getX();
		this.y = characterData.getY();
		this.graphicSizeX = characterData.getGraphicSizeX();
		this.graphicSizeY = characterData.getGraphicSizeY();
		this.graphicCenterX = characterData.getGraphicCenterX();
		this.graphicCenterY = characterData.getGraphicCenterY();
		this.speedX = characterData.getSpeedX();
		this.speedY = characterData.getSpeedY();
		this.state = characterData.getState();
		this.action = characterData.getAction();
		this.hitConfirm = characterData.isHitConfirm();
		this.front = characterData.isFront();
		this.control = characterData.isControl();
		this.attack = new Attack(characterData.getAttack()) ;
		this.remainingFrame = characterData.getRemainingFrame();
		this.inputCommands = characterData.getInputCommand();
		this.processedCommands = characterData.getProcessedCommand();
		this.motionList = motionList;
		this.lastHitFrame = characterData.getLastHitFrame();
		this.hitCount = characterData.getHitCount();
	}

	public void initialize(String characterName, boolean playerNumber) {
		try {
			// gSetting.txtは名前や内容変える可能性大
			// graphicの情報も入れるか要検討
			BufferedReader br = ResourceLoader.getInstance()
					.openReadFile("./data/characters/" + characterName + "/gSetting.txt");
			String[] size = br.readLine().split(",", 0);
			String[] center = br.readLine().split(",", 0);

			this.graphicSizeX = Integer.valueOf(size[0]);
			this.graphicSizeY = Integer.valueOf(size[1]);
			this.graphicCenterX = Integer.valueOf(center[0]);
			this.graphicCenterY = Integer.valueOf(center[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.playerNumber = playerNumber;

		setMotionList(characterName);
	}

	public void initializeList() {
		this.inputCommands = new LinkedList<Key>();
		this.processedCommands = new LinkedList<Key>();
		this.motionList = new ArrayList<Motion>();
	}

	/** 各ラウンドの開始時にキャラクター情報を初期化する */
	public void roundInit() {
		if (FlagSetting.limitHpFlag) {
			this.hp = LaunchSetting.maxHp[this.playerNumber ? 0 : 1];
		} else {
			this.hp = 0;
		}

		if (FlagSetting.trainingModeFlag) {
			this.hp = LaunchSetting.maxHp[this.playerNumber ? 0 : 1];
			this.energy = LaunchSetting.maxEnergy[this.playerNumber ? 0 : 1];
		} else {
			this.energy = 0;
		}

		this.speedX = 0;
		this.speedY = 0;
		this.state = State.STAND;
		this.action = Action.NEUTRAL;
		this.attack = null;
		this.remainingFrame = 1;
		this.control = false;
		this.hitConfirm = false;
		this.hitCount = 0;
		this.lastHitFrame = 0;

		if (this.playerNumber) {
			this.front = true;
			// 初期の立ち位置
			this.x = 100;
			this.y = 335;

		} else {
			this.front = false;
			// 初期の立ち位置
			this.x = 460;
			this.y = 335;
		}
	}

	/** 引数のアクションの情報(アクションによって変化するキャラクターの座標やエネルギーなど)をキャラクターにセットする */
	public void runAction(Action executeAction, boolean resetFlag) {
		Motion exeMotion = this.motionList.get(executeAction.ordinal());

		if (this.action != executeAction) {
			if (resetFlag) {
				destroyAttackInstance();
			}

			this.remainingFrame = exeMotion.getFrameNumber();
			this.hitConfirm = false;
			this.energy += exeMotion.getAttackStartAddEnergy();
		}

		this.action = executeAction;
		this.state = exeMotion.getState();

		if (exeMotion.getSpeedX() != 0) {
			this.speedX = this.front ? exeMotion.getSpeedX() : -exeMotion.getSpeedX();
		}
		this.speedY += exeMotion.getSpeedY();
		this.control = exeMotion.isControl();

		// createAttackInstance();
	}

	/**
	 *
	 * Updates character's information.
	 *
	 */
	public void update() {
		moveX(this.speedX);
		moveY(this.speedY);

		frictionEffect();
		gravityEffect();

		if (FlagSetting.trainingModeFlag) {
			this.energy = LaunchSetting.maxEnergy[this.playerNumber ? 0 : 1];
			this.hp = LaunchSetting.maxHp[this.playerNumber ? 0 : 1];
		}

		if (this.energy > LaunchSetting.maxEnergy[this.playerNumber ? 0 : 1]) {
			this.energy = LaunchSetting.maxEnergy[this.playerNumber ? 0 : 1];
		}

		if (getHitAreaBottom() >= GameSetting.STAGE_HEIGHT) {
			if (motionList.get(this.action.ordinal()).isLandingFlag()) {
				runAction(Action.LANDING, true);
				setSpeedY(0);

				SoundManager.getInstance().play(SoundManager.getInstance().getSoundEffect().get("Landing.wav"));
			}

			moveY(GameSetting.STAGE_HEIGHT - this.getHitAreaBottom());
		}

		this.remainingFrame = getRemainingFrame() - 1;

		if (this.remainingFrame <= 0) {
			if (this.action == Action.CHANGE_DOWN) {
				runAction(Action.DOWN, true);
			} else if (this.action == Action.DOWN) {
				runAction(Action.RISE, true);
			} else if (this.state == State.AIR || getHitAreaBottom() < GameSetting.STAGE_HEIGHT) {
				runAction(Action.AIR, true);
			} else if (this.state == State.CROUCH) {
				runAction(Action.CROUCH, true);
			} else {
				runAction(Action.STAND, true);
			}
		}

		createAttackInstance();

		if (!this.inputCommands.isEmpty()) {
			this.processedCommands.addLast(new Key(this.inputCommands.pop()));
		} else {
			this.processedCommands.addLast(new Key());
		}

		if (this.processedCommands.size() > GameSetting.INPUT_LIMIT)
			this.processedCommands.removeFirst();
	}

	/** 攻撃がヒットしたときに,自身のパラメータや状態を更新する */
	public void hitAttack(Character opponent, Attack attack, int currentFrame) {

		int direction = opponent.getHitAreaCenterX() <= getHitAreaCenterX() ? 1 : -1;
		opponent.setHitCount(opponent.getHitCount() + 1);
		opponent.setLastHitFrame(currentFrame);

		if (isGuard(attack)) {
			setHp(this.hp - attack.getGuardDamage() - opponent.getExtraDamage());
			setEnergy(this.energy + attack.getGiveEnergy());
			setSpeedX(direction * attack.getImpactX() / 2); // 通常の半分のノックバック(旧より変更)
			setRemainingFrame(attack.getGiveGuardRecov());
			opponent.setEnergy(opponent.getEnergy() + attack.getGuardAddEnergy());

			SoundManager.getInstance().play(SoundManager.getInstance().getSoundEffect().get("WeakGuard.wav"));
		} else {
			// 投げ技のときの処理
			if (attack.getAttackType() == 4) {
				if (this.state != State.AIR && this.state != State.DOWN) {
					runAction(Action.THROW_SUFFER, false);

					if (opponent.getAction() != Action.THROW_SUFFER) {
						opponent.runAction(Action.THROW_HIT, false);
					}

					setHp(this.hp - attack.getHitDamage() - opponent.getExtraDamage());
					setEnergy(this.energy + attack.getGiveEnergy());
					opponent.setEnergy(opponent.getEnergy() + attack.getHitAddEnergy());
				}

				// 投げ技以外
			} else {
				setHp(this.hp - attack.getHitDamage() - opponent.getExtraDamage());
				setEnergy(this.energy + attack.getGiveEnergy());
				setSpeedX(direction * attack.getImpactX());
				setSpeedY(attack.getImpactY());
				opponent.setEnergy(opponent.getEnergy() + attack.getHitAddEnergy());

				if (attack.isDownProperty()) {
					runAction(Action.CHANGE_DOWN, false);
					setRemainingFrame(this.motionList.get(this.action.ordinal()).getFrameNumber());

					SoundManager.getInstance().play(SoundManager.getInstance().getSoundEffect().get("StrongHit.wav"));

				} else {
					switch (this.state) {
					case STAND:
						runAction(Action.STAND_RECOV, false);
						break;

					case CROUCH:
						runAction(Action.CROUCH_RECOV, false);
						break;

					case AIR:
						runAction(Action.AIR_RECOV, false);
						break;

					default:
						break;
					}

					SoundManager.getInstance().play(SoundManager.getInstance().getSoundEffect().get("WeakHit.wav"));
				}
			}
		}
	}

	/**
	 * 攻撃が当たったときに自身がガードしていたかどうかを返す<br>
	 * また, 自身をガードの種類に対応したリカバリー状態に変化させる
	 */
	private boolean isGuard(Attack attack) {
		boolean isGuard = false;

		switch (this.action) {
		case STAND_GUARD:
			if (attack.getAttackType() == 1 || attack.getAttackType() == 2) {
				runAction(Action.STAND_GUARD_RECOV, false);
				isGuard = true;
			}
			break;

		case CROUCH_GUARD:
			if (attack.getAttackType() == 1 || attack.getAttackType() == 3) {
				runAction(Action.CROUCH_GUARD_RECOV, false);
				isGuard = true;
			}
			break;

		case AIR_GUARD:
			if (attack.getAttackType() == 1 || attack.getAttackType() == 2) {
				runAction(Action.STAND_GUARD_RECOV, false);
				isGuard = true;
			}
			break;

		case STAND_GUARD_RECOV:
			runAction(Action.STAND_GUARD_RECOV, false);
			isGuard = true;
			break;

		case CROUCH_GUARD_RECOV:
			runAction(Action.CROUCH_GUARD_RECOV, false);
			isGuard = true;
			break;

		case AIR_GUARD_RECOV:
			runAction(Action.AIR_GUARD_RECOV, false);
			isGuard = true;
			break;

		default:
			isGuard = false;
			break;
		}

		return isGuard;
	}

	/** アクションのアタックオブジェクト(当たり判定を伴ったヒットボックス)を作成する */
	private void createAttackInstance() {
		Motion motion = this.motionList.get(this.action.ordinal());

		if (startActive(motion)) {
			this.attack = new Attack(motion.getAttackHitArea(), motion.getAttackSpeedX(), motion.getAttackSpeedY(),
					motion.getAttackStartUp(), motion.getAttackActive(), motion.getAttackHitDamage(),
					motion.getAttackGuardDamage(), motion.getAttackStartAddEnergy(), motion.getAttackHitAddEnergy(),
					motion.getAttackGuardAddEnergy(), motion.getAttackGiveEnergy(), motion.getAttackImpactX(),
					motion.getAttackImpactY(), motion.getAttackGiveGuardRecov(), motion.getAttackType(),
					motion.isAttackDownProperty());

			this.attack.initialize(this.playerNumber, this.x, this.y, this.graphicSizeX, this.front);
		}
	}

	public boolean startActive(Motion motion) {
		int startActive = motion.getFrameNumber() - motion.getAttackStartUp();
		return startActive == this.remainingFrame;
	}

	/**
	 *
	 * Move the character on the X-axis.
	 *
	 * @param relativePosition
	 *            value in pixels.
	 */
	public void moveX(int relativePosition) {
		setX(getX() + relativePosition);
	}

	/**
	 *
	 * Move the character on the Y-axis.
	 *
	 * @param relativePosition
	 *            value in pixels.
	 */
	public void moveY(int relativePosition) {
		setY(getY() + relativePosition);
	}

	/**
	 *
	 * キャラクターが床に接しているときに,摩擦の影響を与える
	 *
	 */
	public void frictionEffect() {
		if (getHitAreaBottom() >= GameSetting.STAGE_HEIGHT) {
			if (this.speedX > 0) {
				setSpeedX(this.speedX - GameSetting.FRICTION);
			} else if (this.speedX < 0) {
				setSpeedX(this.speedX + GameSetting.FRICTION);
			}
		}
	}

	/**
	 *
	 * キャラクターが空中にいるときに, 重力の影響を与える
	 *
	 */
	public void gravityEffect() {
		if (getHitAreaBottom() >= GameSetting.STAGE_HEIGHT) {
			setSpeedY(0);
		} else if (getHitAreaTop() <= 0) {
			setSpeedY(GameSetting.GRAVITY);
		} else {
			setSpeedY(this.speedY + GameSetting.GRAVITY);
		}
	}

	/**
	 *
	 * Defines character's orientation.
	 *
	 */
	public void frontDecision(int opponentCenterX) {
		if (this.front) {
			if (getHitAreaCenterX() < opponentCenterX) {
				this.front = true;
			} else {
				this.x = this.x - this.graphicSizeX + graphicCenterX * 2;
				this.front = false;
			}

		} else {
			if (getHitAreaCenterX() < opponentCenterX) {
				this.x = this.x + this.graphicSizeX - graphicCenterX * 2;
				this.front = true;
			} else {
				this.front = false;
			}
		}
	}

	public void destroyAttackInstance() {
		this.attack = null;
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

	/**
	 * @return The character's hit box's most-right x-coordinate.
	 */
	public int getHitAreaRight() {
		HitArea area = this.motionList.get(this.action.ordinal()).getCharacterHitArea();

		return this.front ? area.getRight() + x : this.graphicSizeX - area.getLeft() + x;
	}

	/**
	 * @return The character's hit box's most-left x-coordinate.
	 */
	public int getHitAreaLeft() {
		HitArea area = this.motionList.get(this.action.ordinal()).getCharacterHitArea();

		return this.front ? area.getLeft() + x : this.graphicSizeX - area.getRight() + x;
	}

	/**
	 * @return The character's hit box's most-top y-coordinate.
	 */
	public int getHitAreaTop() {
		return this.motionList.get(this.action.ordinal()).getCharacterHitArea().getTop() + y;
	}

	/**
	 * @return The character's hit box's most-bottom y-coordinate.
	 */
	public int getHitAreaBottom() {
		return this.motionList.get(this.action.ordinal()).getCharacterHitArea().getBottom() + y;

	}

	public int getHitAreaCenterX() {
		return (getHitAreaRight() + getHitAreaLeft()) / 2;
	}

	public int getHitAreaCenterY() {
		return (getHitAreaTop() + getHitAreaBottom()) / 2;
	}

	/**
	 *
	 * Reverse horizontal speed.
	 *
	 */
	public void reversalSpeedX() {
		this.speedX = -(this.speedX / 2);
	}

	/**
	 * Returns a boolean value whether the motion hits the opponent or not
	 *
	 * @return hitConfirm A boolean value whether the motion hits the opponent
	 *         (true) or not (false)
	 */
	public boolean isHitConfirm() {
		return this.hitConfirm;
	}

	public int getRemainingFrame() {
		return this.remainingFrame;
	}

	public Attack getAttack() {
		return this.attack;
	}

	public int getGraphicCenterX() {
		return this.graphicCenterX;
	}

	public int getGraphicCenterY() {
		return this.graphicCenterY;
	}

	public int getGraphicSizeX() {
		return this.graphicSizeX;
	}

	public int getGraphicSizeY() {
		return this.graphicSizeY;
	}

	public ArrayList<Motion> getMotionList() {
		ArrayList<Motion> temp = new ArrayList<Motion>();
		for (Motion motion : this.motionList) {
			temp.add(motion);
		}

		return temp;
	}

	/**
	 * Returns a list storing keys of the action that the character will be
	 * executing in the simulator
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
	 * @return The current image handle.
	 */
	public Image getNowImage() {
		Motion motion = motionList.get(this.action.ordinal());

		return motion.getImage(Math.abs(this.remainingFrame) % motion.getFrameNumber());
	}

	/** 現時点での攻撃の連続ヒット回数を取得する */
	public int getHitCount() {
		return this.hitCount;
	}

	public int getLastHitFrame() {
		return this.lastHitFrame;
	}

	public int getExtraDamage() {
		int requireHit = 4; // ボーナスダメージに必要な最小限のヒット数
		int damage = 5; // ボーナスダメージ

		return this.hitCount < requireHit ? 0 : damage * requireHit / this.hitCount;
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

	/**
	 * Sets hitConfirm whether the motion hits the opponent or not
	 *
	 * @param hitConfirm
	 *            A boolean value whether the motion hits the opponent or not
	 */
	public void setHitConfirm(boolean hitConfirm) {
		this.hitConfirm = hitConfirm;
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

	public void setAttack(Attack attack) {
		this.attack = attack;
	}

	public void setGraphicCenterX(int graphicCenterX) {
		this.graphicCenterX = graphicCenterX;
	}

	public void setGraphicCenterY(int graphicCenterY) {
		this.graphicCenterY = graphicCenterY;
	}

	public void setGraphicSizeX(int graphicSizeX) {
		this.graphicSizeX = graphicSizeX;
	}

	public void setGraphicSizeY(int graphicSizeY) {
		this.graphicSizeY = graphicSizeY;
	}

	/**
	 *
	 * Sets motions.
	 *
	 * @param characterName
	 *            the character's name.
	 */
	private void setMotionList(String characterName) {
		try {
			BufferedReader br = ResourceLoader.getInstance()
					.openReadFile("./data/characters/" + characterName + "/Motion.csv");

			String line;
			br.readLine(); // ignore header

			while ((line = br.readLine()) != null) {
				String[] st = line.split(",", 0);
				Motion motion = new Motion(st, characterName, this.playerNumber ? 0 : 1);
				this.motionList.add(motion);
			}

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public void setLastHitFrame(int currentFrame) {
		this.lastHitFrame = currentFrame;
	}

	/**
	 * Sets a list storing keys of the action that the character will be
	 * executing in the simulator
	 *
	 * @param inputCommands
	 *            A list storing keys of the action that the character will be
	 *            executing in the simulator
	 */
	public void setInputCommand(Deque<Key> inputCommands) {
		this.inputCommands = inputCommands;
	}

	/**
	 * Sets a list storing up to 30 keys that the character executed in the
	 * simulator
	 *
	 * @param inputCommands
	 *            A list storing up to 30 keys that the character executed in
	 *            the simulator
	 */
	public void setProcessedCommand(Deque<Key> inputCommands) {
		this.processedCommands = inputCommands;
	}

	/**
	 * Get a boolean value whether the combo is still valid or not.
	 *
	 * @param nowFrame
	 *            the current frame.
	 *
	 * @return <em>True</em> if the combo is still valid, <em>False</em>
	 *         otherwise.
	 */
	public boolean isComboValid(int nowFrame) {
		return (nowFrame - this.lastHitFrame) <= GameSetting.COMBO_LIMIT;
	}
}
