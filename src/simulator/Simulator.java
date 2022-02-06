package simulator;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import enumerate.Action;
import fighting.Motion;
import struct.FrameData;
import struct.GameData;

/**
 * The class of the simulator.
 */
public class Simulator {

	/**
	 * The variable that holds invariant information in the game.
	 */
	private GameData gameData;

	/**
	 * The class constructor that creates an instance of the Simulator class by
	 * using an instance of the GameData class.
	 *
	 * @param gameData
	 *            an instance of the GameData class
	 */
	public Simulator(GameData gameData) {
		this.gameData = gameData;
	}

	/**
	 * Simulates the progression of a fight starting from a given
	 * {@link FrameData} instance and executing specified actions for both
	 * players.<br>
	 * The resulting {@link FrameData} is returned, and can be used to assess
	 * the outcome of the simulation.<br>
	 *
	 * Note that when the character is on ground, all AIR actions will be
	 * considered invalid by the simulator. Likewise, all GROUND actions are
	 * considered invalid if the character is in air. <br>
	 * To simulate AIR actions when the character is initially on ground, add
	 * Action.JUMP to the action list before the AIR action.<br>
	 * For example, myAct.add(Action.JUMP); myAct.add(Action.AIR_A);
	 *
	 * @param frameData
	 *            frame data at the start of simulation
	 * @param playerNumber
	 *            boolean value which identifies P1/P2. {@code true} if the
	 *            player is P1, or {@code false} if P2.
	 * @param myAct
	 *            actions to be performed by the player identified by the
	 *            parameter `player`
	 * @param oppAct
	 *            actions to be performed by the opponent of the player
	 *            identified by the parameter `player`
	 * @param simulationLimit
	 *            the number of frames to be simulated.<br>
	 *            If `simulationLimit` is less than the number of frames
	 *            required for executing the actions of a given player, the
	 *            simulator will simulate the player's actions up to
	 *            `simulationLimit` frames; otherwise, the simulator will
	 *            simulate the player's all actions and then continue the
	 *            simulation until the `simulationLimit`-th frame assuming no
	 *            actions are performed by the player.
	 *
	 * @return the frame data after the simulation
	 */
	public FrameData simulate(FrameData frameData, boolean playerNumber, Deque<Action> myAct, Deque<Action> oppAct,
			int simulationLimit) {

		// Creates deep copy of each action's list
		ArrayList<Deque<Action>> tempActionList = new ArrayList<Deque<Action>>(2);
		Deque<Action> tempP1Act = ((playerNumber ? myAct : oppAct) == null) ? null
				: new LinkedList<Action>(playerNumber ? myAct : oppAct);
		Deque<Action> tempP2Act = ((!playerNumber ? myAct : oppAct) == null) ? null
				: new LinkedList<Action>(!playerNumber ? myAct : oppAct);
		tempActionList.add(tempP1Act);
		tempActionList.add(tempP2Act);

		ArrayList<ArrayList<Motion>> tempMotionList = new ArrayList<ArrayList<Motion>>(2);
//		ArrayList<Motion> p1MotionData = this.gameData.getMotion(playerNumber ? true : false);
//		ArrayList<Motion> p2MotionData = this.gameData.getMotion(!playerNumber ? true : false);
		ArrayList<Motion> p1MotionData = this.gameData.getMotion(true);
		ArrayList<Motion> p2MotionData = this.gameData.getMotion(false);
		tempMotionList.add(p1MotionData);
		tempMotionList.add(p2MotionData);

		int nowFrame = frameData.getFramesNumber();

		SimFighting simFighting = new SimFighting();
		simFighting.initialize(tempMotionList, tempActionList, new FrameData(frameData), playerNumber);

		for (int i = 0; i < simulationLimit; i++) {
			simFighting.processingFight(nowFrame);
			nowFrame++;
		}

		return simFighting.createFrameData(nowFrame, frameData.getRound(), false);
	}

}
