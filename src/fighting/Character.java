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
import struct.Key;

public class Character {
	private boolean playerNumber;

	private int hp;

	private int energy;

	private int x;

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

	private Attack attack;

	private int remainingFrame;

	private int graphicSizeX;

	private int graphicSizeY;

	private int graphicCenterX;

	private int graphicCenterY;

	private ArrayList<Action> currentCombo;

	private int lastCombo;

	/**
	 * Combo table of this character.
	 * May be NULL if the corresponding file is not found
	 */
	private ArrayList<Triplet<ArrayList<Action>, ArrayList<Action>, Integer>> comboTable;

	private Deque<Key> inputCommands;

	private Deque<Key> processedCommands;

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
		this.left = 0;
		this.right = 0;
		this.top = 0;
		this.bottom = 0;
		this.speedX = 0;
		this.speedY = 0;
		this.state = State.STAND;
		this.action = Action.NEUTRAL;
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
		this.left = character.getLeft();
		this.right = character.getRight();
		this.top = character.getTop();
		this.bottom = character.getBottom();
		this.speedX = character.getSpeedX();
		this.speedY = character.getSpeedY();
		this.state = character.getState();
		this.action = character.getAction();
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

		setComboTable(characterName);

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
	 * @param characterName the name of the Character.
	 *
	 */
	private void setComboTable(String characterName) {
		try{
			BufferedReader br = ResourceLoader.getInstance().openReadFile("./data/character/"+characterName+"/ComboTable.csv");

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
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Sets motions.
	 *
	 * @param characterName the character's name.
	 */
	/*private void setMotionVector(String characterName) {
		try{
			File csv = new File("./data/character/"+characterName+"/Motion.csv");

			BufferedReader br = new BufferedReader(new FileReader(csv));

			Vector<MotionData> motionData = new Vector<MotionData>();

			String line;
			br.readLine(); // ignore header

			while((line = br.readLine()) != null)
			{
				String[] st = line.split(",",0);

				MotionData tmp = new MotionData();

				tmp.motionName = st[0];
				tmp.frameNumber = Integer.valueOf(st[1]);
				tmp.speedX = Integer.valueOf(st[2]);
				tmp.speedY = Integer.valueOf(st[3]);
				tmp.hit = new HitArea(Integer.valueOf(st[4]),Integer.valueOf(st[5]),Integer.valueOf(st[6]),Integer.valueOf(st[7]));
				tmp.state= State.valueOf(st[8]);
				tmp.attackHit = new HitArea(Integer.valueOf(st[9]),Integer.valueOf(st[10]),Integer.valueOf(st[11]),Integer.valueOf(st[12]));
				tmp.attackSpeedX = Integer.valueOf(st[13]);
				tmp.attackSpeedY = Integer.valueOf(st[14]);
				tmp.attackStartUp = Integer.valueOf(st[15]);
				tmp.attackInterval = Integer.valueOf(st[16]);
				tmp.attackRepeat = Integer.valueOf(st[17]);
				tmp.attackActive = Integer.valueOf(st[18]);
				tmp.attackHitDamage = Integer.valueOf(st[19]);
				tmp.attackGuardDamage = Integer.valueOf(st[20]);
				tmp.attackStartAddEnergy = Integer.valueOf(st[21]);
				tmp.attackHitAddEnergy = Integer.valueOf(st[22]);
				tmp.attackGuardAddEnergy = Integer.valueOf(st[23]);
				tmp.attackGiveEnergy = Integer.valueOf(st[24]);
				tmp.attackImpactX = Integer.valueOf(st[25]);
				tmp.attackImpactY = Integer.valueOf(st[26]);
				tmp.attackGiveGuardRecov = Integer.valueOf(st[27]);
				tmp.attackKnockBack = Integer.valueOf(st[28]);
				tmp.attackHitStop = Integer.valueOf(st[29]);
				tmp.attackType = Integer.valueOf(st[30]);
				tmp.attackDownProperty =  Boolean.valueOf(st[31]);
				tmp.attackImage = st[32];
				tmp.cancelAbleFrame = Integer.valueOf(st[33]);
				tmp.cancelAbleMotionLevel = Integer.valueOf(st[34]);
				tmp.motionLevel = Integer.valueOf(st[35]);
				tmp.control = Boolean.valueOf(st[36]);
				tmp.landingFlag = Boolean.valueOf(st[37]);

				motionData.addElement(tmp);
			}

			this.motionVector = new Vector<Motion>();

			for(Action act : Action.values()){
				Motion motionObject = new Motion();
				motionObject.setParameters(motionData.elementAt(act.ordinal()),characterName,ic);
				motionVector.addElement(motionObject);
			}

			br.close();

		} catch(IOException e){
			e.printStackTrace();
		}
	}*/

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

}
