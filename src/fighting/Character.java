package fighting;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.javatuples.Triplet;

import enumerate.Action;
import enumerate.State;
import loader.ResourceLoader;
import setting.FlagSetting;
import setting.LaunchSetting;
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
		this.control = true;
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
	}

	public void initialize(String characterName, boolean playerNumber) {
		try {
			// gSetting.txtは名前や内容変える可能性大
			// graphicの情報も入れるか要検討
			BufferedReader br = ResourceLoader.getInstance()
					.openReadFile("./data/character/" + characterName + "/gSetting.txt");
			String[] size = br.readLine().split(",", 0);
			String[] center = br.readLine().split(",", 0);

			this.graphicSizeX = Integer.valueOf(size[0]);
			this.graphicSizeY = Integer.valueOf(size[1]);
			this.graphicCenterX = Integer.valueOf(center[0]);
			this.graphicCenterY = Integer.valueOf(center[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}

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

		if(this.playerNumber){
			this.front = true;
			//初期の立ち位置
			this.x = 100;
			this.y = 335;
		}else{
			this.front = false;
			//初期の立ち位置
			this.x = 460;
			this.y = 335;
		}
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
	public int getCharacterHitAreaRight() {

		return motionList.get(this.action.ordinal()).getCharacterHitArea().getRight() + x;
	}

	/**
	 * @return The character's hit box's most-left x-coordinate.
	 */
	public int getCharacterHitAreaLeft() {
		return motionList.get(this.action.ordinal()).getCharacterHitArea().getLeft() + x;
	}

	/**
	 * @return The character's hit box's most-top y-coordinate.
	 */
	public int getCharacterHitAreaTop() {
		return motionList.get(this.action.ordinal()).getCharacterHitArea().getTop() + y;
	}

	/**
	 * @return The character's hit box's most-bottom y-coordinate.
	 */
	public int getCharacterHitAreaBottom() {
		return motionList.get(this.action.ordinal()).getCharacterHitArea().getBottom() + y;

	}

	/**
	 * Returns a boolean value whether the motion hits the opponent or not
	 *
	 * @return hitConfirm A boolean value whether the motion hits the opponent
	 *         (true) or not (false)
	 */
	public boolean isHitConfirm() {
		return hitConfirm;
	}

	public int getRemainingFrame() {
		return this.remainingFrame;
	}

	public Attack getAttack() {
		return new Attack(this.attack);
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
					.openReadFile("./data/character/" + characterName + "/ComboTable.csv");

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
					.openReadFile("./data/character/" + characterName + "/Motion.csv");

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

}
