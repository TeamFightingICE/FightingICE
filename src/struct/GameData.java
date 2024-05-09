package struct;

import java.util.ArrayList;

import fighting.Character;
import fighting.Motion;
import protoc.MessageProto.GrpcGameData;
import setting.GameSetting;
import setting.LaunchSetting;
import simulator.Simulator;

/**
 * The class dealing with invariable information in the game such as the screen
 * width of the stage and the chatacter's maximum HP.
 */
public class GameData {

	/**
	 * The horizontal size of the game stage.
	 */
	private int stageWidth;

	/**
	 * The vertical size of the game stage.
	 */
	private int stageHeight;

	/**
	 * The values of both characters' HP limits.<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private int[] maxHPs;

	/**
	 * The values of both characters' energy limits.<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private int[] maxEnergies;

	/**
	 * The list of MotionData of both characters.<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private ArrayList<ArrayList<MotionData>> characterMotions;

	/**
	 * The both characters' names.<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private String[] characterNames;

	/**
	 * The both AIs' names.<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private String[] aiNames;

	/**
	 * The simulator which simulates the progression of a fight starting from a
	 * given {@link FrameData} instance and executing specified actions for both
	 * players.
	 *
	 * @see Simulator
	 */
	private Simulator simulator;

	/**
	 * The class constructor.
	 */
	public GameData() {
		this.maxHPs = new int[2];
		this.maxEnergies = new int[2];
		this.characterMotions = new ArrayList<ArrayList<MotionData>>(2);
		this.characterNames = new String[2];
		this.aiNames = new String[2];
	}

	/**
	 * The class constructor that creates an instance of the GameData class by
	 * using character information.
	 *
	 * @param players
	 *            character information of P1 and P2
	 *
	 * @see Character
	 */
	public GameData(Character[] players) {
		this();

		for (int i = 0; i < 2; i++) {
			ArrayList<MotionData> motionDataList = new ArrayList<MotionData>();
			ArrayList<Motion> temp = players[i].getMotionList();
			for (Motion motion : temp) {
				motionDataList.add(new MotionData(motion));
			}

			this.characterMotions.add(motionDataList);
		}

		this.stageWidth = GameSetting.STAGE_WIDTH;
		this.stageHeight = GameSetting.STAGE_HEIGHT;
		this.maxHPs = LaunchSetting.maxHp.clone();
		this.maxEnergies = LaunchSetting.maxEnergy.clone();
		this.characterNames = LaunchSetting.characterNames.clone();
		this.aiNames = LaunchSetting.aiNames.clone();

		this.simulator = new Simulator(this);
	}

	/**
	 * Returns the motion data of the player specified by the argument.
	 *
	 * @param playerNumber
	 *            the player side's flag. {@code true} if the player is P1, or
	 *            {@code false} if P2.
	 * @return the motion data of the player specified by the argument
	 * @see MotionData
	 */
	public ArrayList<MotionData> getMotionData(boolean playerNumber) {
		ArrayList<MotionData> temp = new ArrayList<MotionData>();
		ArrayList<MotionData> copy = this.characterMotions.get(playerNumber ? 0 : 1);

		for (MotionData motionData : copy) {
			temp.add(motionData);
		}
		return temp;
	}

	/**
	 * Returns the motion of the player specified by the argument.<br>
	 *
	 * @deprecated This method is called in the simulator.<br>
	 *             AI developers can not use this method.
	 *
	 * @param playerNumber
	 *            the player side's flag. {@code true} if the player is P1, or
	 *            {@code false} if P2.
	 * @return the motion of the player specified by the argument.
	 * @see Motion
	 */
	public ArrayList<Motion> getMotion(boolean playerNumber) {
		ArrayList<Motion> temp = new ArrayList<Motion>();
		ArrayList<MotionData> copy = this.characterMotions.get(playerNumber ? 0 : 1);

		for (MotionData motionData : copy) {
			Motion motion = new Motion(motionData);
			temp.add(motion);
		}
		return temp;
	}

	/**
	 * Returns the width of the game stage.
	 *
	 * @return the width of the game stage
	 */
	public int getStageWidth() {
		return this.stageWidth;
	}

	/**
	 * Returns the height of the game stage.
	 *
	 * @return the height of the game stage
	 */
	public int getStageHeight() {
		return this.stageHeight;
	}

	/**
	 * Returns the maximum HP of the specified player.
	 *
	 * @param playerNumber
	 *            the player side's flag. {@code true} if the player is P1, or
	 *            {@code false} if P2.
	 * @return the maximum HP of the specified player
	 */
	public int getMaxHP(boolean playerNumber) {
		return playerNumber ? this.maxHPs[0] : this.maxHPs[1];
	}

	/**
	 * Returns the maximum energy of the specified player.
	 *
	 * @param playerNumber
	 *            the player side's flag. {@code true} if the player is P1, or
	 *            {@code false} if P2.
	 * @return the maximum energy of the specified player
	 */
	public int getMaxEnergy(boolean playerNumber) {
		return playerNumber ? this.maxEnergies[0] : this.maxEnergies[1];
	}

	/**
	 * Returns the character name of the specified player.
	 *
	 * @param playerNumber
	 *            the player side's flag. {@code true} if the player is P1, or
	 *            {@code false} if P2.
	 * @return the character name of the specified player
	 */
	public String getCharacterName(boolean playerNumber) {
		return playerNumber ? this.characterNames[0] : this.characterNames[1];
	}

	/**
	 * Returns the AI name of the specified player.
	 *
	 * @param playerNumber
	 *            the player side's flag. {@code true} if the player is P1, or
	 *            {@code false} if P2.
	 * @return the AI name of the specified player
	 */
	public String getAiName(boolean playerNumber) {
		return playerNumber ? this.aiNames[0] : this.aiNames[1];
	}

	/**
	 * Returns the simulator.
	 *
	 * @return the simulator
	 * @see Simulator
	 */
	public Simulator getSimulator() {
		return this.simulator;
	}
	
	public GrpcGameData toProto() {
		return GrpcGameData.newBuilder()
    			.addMaxHps(this.maxHPs[0])
    			.addMaxHps(this.maxHPs[1])
    			.addMaxEnergies(this.maxEnergies[0])
    			.addMaxEnergies(this.maxEnergies[1])
    			.addCharacterNames(this.characterNames[0])
    			.addCharacterNames(this.characterNames[1])
    			.addAiNames(this.aiNames[0])
    			.addAiNames(this.aiNames[1])
    			.build();
	}
	
}
