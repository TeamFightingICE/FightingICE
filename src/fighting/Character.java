package fighting;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.javatuples.Triplet;

import enumerate.Action;
import enumerate.State;
import image.Image;
import loader.ResourceLoader;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
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

	private ArrayList<Action> currentCombo;

	private int lastCombo;

	private ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>> comboTable;

	private Deque<Key> inputCommands;

	private Deque<Key> processedCommands;

	private ArrayList<Motion> motionList;

	public Character() {
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
		this.currentCombo = new ArrayList<Action>();
		this.lastCombo = 0;
	}

	public Character(Character character) {
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
		this.currentCombo = character.getCurrentCombo();
		this.lastCombo = character.getLastCombo();
		this.motionList = character.getMotionList();
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
		this.comboTable = new ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>>();
		this.inputCommands = new LinkedList<Key>();
		this.processedCommands = new LinkedList<Key>();
		this.motionList = new ArrayList<Motion>();

		setMotionList(characterName);
		setComboTable(characterName);

	}

	/** 各ラウンドの開始時にキャラクター情報を初期化する */
	public void roundInit() {
		if (FlagSetting.limitHpFlag) {
			this.hp = LaunchSetting.maxHp[this.playerNumber ? 0 : 1];
		} else {
			this.hp = 0;
		}

		if (FlagSetting.trainingModeFlag) {
			this.hp = 9999;
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
		resetCombo();

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

		if (this.energy > LaunchSetting.maxEnergy[this.playerNumber ? 0 : 1]) {
			this.energy = LaunchSetting.maxEnergy[this.playerNumber ? 0 : 1];
		}

		if (getHitAreaBottom() >= GameSetting.STAGE_HEIGHT) {
			if (motionList.get(this.action.ordinal()).isLandingFlag()) {
				runAction(Action.LANDING, true);
				setSpeedY(0);

				// 着地音を鳴らす
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
			} else if(this.state == State.CROUCH){
				runAction(Action.CROUCH, true);
			}else{
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
	public void hitAttack(Character opponent, Attack attack) {

		int direction = opponent.getHitAreaCenterX() <= getHitAreaCenterX() ? 1 : -1;

		if (isGuard(attack)) {
			setHp(this.hp - attack.getGuardDamage() - opponent.getComboDamage());
			setEnergy(this.energy + attack.getGiveEnergy());
			setSpeedX(direction * attack.getImpactX() / 2); // 通常の半分のノックバック(旧より変更)
			setRemainingFrame(attack.getGiveGuardRecov());
			opponent.setEnergy(opponent.getEnergy() + attack.getGuardAddEnergy());

			// ガード時のサウンド鳴らす
		} else {
			// 投げ技のときの処理
			if (attack.getAttackType() == 4) {
				if (this.state != State.AIR && this.state != State.DOWN) {
					runAction(Action.THROW_SUFFER, false);

					if (opponent.getAction() != Action.THROW_SUFFER) {
						opponent.runAction(Action.THROW_HIT, false);
					}

					setHp(this.hp - attack.getHitDamage());
					setEnergy(this.energy + attack.getGiveEnergy());
					opponent.setEnergy(opponent.getEnergy() + attack.getHitAddEnergy());
				}

				// 投げ技以外
			} else {
				setHp(this.hp - attack.getHitDamage() - opponent.getComboDamage());
				setEnergy(this.energy + attack.getGiveEnergy());
				setSpeedX(direction * attack.getImpactX());
				setSpeedY(attack.getImpactY());
				opponent.setEnergy(opponent.getEnergy() + attack.getHitAddEnergy());

				if (attack.isDownProperty()) {
					runAction(Action.CHANGE_DOWN, false);
					setRemainingFrame(this.motionList.get(this.action.ordinal()).getFrameNumber());
					// ダウン時の音を鳴らす

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

					// 通常のヒット音を鳴らす
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

		if (isActive(motion)) {
			this.attack = new Attack(motion.getAttackHitArea(), motion.getAttackSpeedX(), motion.getAttackSpeedY(),
					motion.getAttackStartUp(), motion.getAttackActive(), motion.getAttackHitDamage(),
					motion.getAttackGuardDamage(), motion.getAttackStartAddEnergy(), motion.getAttackHitAddEnergy(),
					motion.getAttackGuardAddEnergy(), motion.getAttackGiveEnergy(), motion.getAttackImpactX(),
					motion.getAttackImpactY(), motion.getAttackGiveGuardRecov(), motion.getAttackType(),
					motion.isAttackDownProperty());

			this.attack.initialize(this.playerNumber, this.x, this.y, this.graphicSizeX, this.front);
		}
	}

	private boolean isActive(Motion motion) {
		int startActive = motion.getFrameNumber() - motion.getAttackStartUp();
		return (startActive < this.remainingFrame) && (startActive - motion.getAttackActive() <= this.remainingFrame);
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

	public ArrayList<Action> getCurrentCombo() {
		ArrayList<Action> temp = new ArrayList<Action>();
		for (Action action : this.currentCombo) {
			temp.add(action);
		}

		return temp;
	}

	public int getLastCombo() {
		return this.lastCombo;
	}

	public int getComboState() {
		return this.currentCombo.size();
	}

	public ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>> getComboTable() {
		return (ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>>) this.comboTable.clone();
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
	 * @return The current image handle.
	 */
	public Image getNowImage() {
		Motion motion = motionList.get(this.action.ordinal());

		return motion.getImage(Math.abs(this.remainingFrame) % motion.getFrameNumber());
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
	 * Sets the combo table.
	 *
	 * @param characterName
	 *            the name of the Character.
	 *
	 */
	private void setComboTable(String characterName) {
		try {
			BufferedReader br = ResourceLoader.getInstance()
					.openReadFile("./data/characters/" + characterName + "/ComboTable.csv");

			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(br);
			for (CSVRecord record : records) {
				ArrayList<Action> actions = new ArrayList<Action>();
				actions.add(Action.valueOf(record.get("1st attack")));
				actions.add(Action.valueOf(record.get("2nd attack")));
				actions.add(Action.valueOf(record.get("3rd attack")));
				actions.add(Action.valueOf(record.get("4th attack")));
				ArrayList<Action> breakers = new ArrayList<Action>();
				for (String breaker : record.get("combo breaker").split(",")) {
					breakers.add(Action.valueOf(breaker));
				}
				this.comboTable.add(Triplet.with(actions, breakers, Integer.parseInt(record.get("extra damage"))));
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				Motion motion = new Motion(st, characterName);
				this.motionList.add(motion);
			}

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets a list storing keys of the action that the character will be
	 * executing in the simulator
	 *
	 * @deprecated This method is used only for processing of the simulator. You
	 *             should not use this method for AI development.
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
	 * @deprecated This method is used only for processing of the simulator. You
	 *             should not use this method for AI development.
	 *
	 * @param inputCommands
	 *            A list storing up to 30 keys that the character executed in
	 *            the simulator
	 */
	public void setProcessedCommand(Deque<Key> inputCommands) {
		this.processedCommands = inputCommands;
	}

	/**
	 * Reset combo's information.
	 */
	public void resetCombo() {
		this.lastCombo = 0;
		this.currentCombo.clear();
	}

	/**
	 *
	 * Get all possible combo after the last attack.
	 *
	 * @return All possible combo after the last attack.
	 */
	private ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>> currentPossibleCombos() {
		if (this.currentCombo.isEmpty()) {
			return this.comboTable;
		}

		ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>> res = new ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>>(
				this.comboTable);
		for (int i = 0; i < res.size(); ++i) {
			Iterator<Action> it1 = res.get(i).getValue0().iterator();
			Iterator<Action> it2 = this.currentCombo.iterator();

			while (it1.hasNext() && it2.hasNext()) {
				Action comboAction = it1.next();
				Action action = it2.next();

				if (comboAction != action) {
					res.remove(i);
					--i;
					break;
				}
			}
		}
		return res;
	}

	/**
	 * Move to the next combo state using current action
	 *
	 * @param nowFrame
	 *            current frame number
	 */
	public void nextCombo(int nowFrame) {
		if (this.motionList.get(this.action.ordinal()).getAttackType() == 0) {
			return;
		}

		if (!this.isComboValid(nowFrame) || isComboCompleted()) {
			resetCombo();
		}

		this.lastCombo = nowFrame + this.remainingFrame;
		this.currentCombo.add(this.action);
		if (currentPossibleCombos().isEmpty()) {
			resetCombo();
		}
	}

	/**
	 *
	 * Checks if a combo is possible.
	 *
	 * @param nowFrame
	 *            the current frame.
	 */
	public void resetInvalidCombo(int nowFrame) {
		if (!this.isComboValid(nowFrame)) {
			this.resetCombo();
		}
	}

	/**
	 *
	 * Break the current combo.
	 *
	 */
	public void breakCombo() {
		if (!isComboBreakable()) {
			return;
		}

		ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>> combos = this.currentPossibleCombos();
		int damages = 0;
		for (Triplet<ArrayList<Action>, ArrayList<Action>, Integer> triplet : combos) {
			damages += triplet.getValue2();
		}

		damages /= combos.size();
		setHp(this.hp - damages);
		// this.resetCombo();
	}

	/**
	 * Get damages provides by the current combo.
	 *
	 * @return damages provides by the current combo.
	 */
	public int getComboDamage() {
		ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>> combos = this.currentPossibleCombos();

		if (this.isComboCompleted() && combos.size() > 0) {
			return combos.get(0).getValue2();
		}

		return 0;
	}

	/**
	 * Get combo breakers.
	 *
	 * @return combo breakers.
	 */
	public Set<Action> getComboBreakers() {
		Set<Action> res = new HashSet<Action>();
		ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>> combos = this.currentPossibleCombos();

		for (Triplet<ArrayList<Action>, ArrayList<Action>, Integer> triplet : combos) {
			res.addAll(triplet.getValue1());
		}

		return res;
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
		return (nowFrame - this.lastCombo) <= GameSetting.COMBO_LIMIT;
	}

	/**
	 * Get a boolean value whether the combo is breakable or not.
	 *
	 * @return <em>True</em> if a combo is breakable, <em>False</em> otherwise.
	 */
	public boolean isComboBreakable() {
		return this.currentCombo.size() >= 2 && this.currentCombo.size() <= 3;
	}

	/**
	 * Get a boolean value whether the combo is completed or not.
	 *
	 * @return <em>True</em> if a combo is done, <em>False</em> otherwise.
	 */
	public boolean isComboCompleted() {
		return this.currentCombo.size() == 4;
	}

}
