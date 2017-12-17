package simulator;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import enumerate.Action;
import fighting.Motion;
import struct.FrameData;
import struct.GameData;

/**
 * シミュレータークラス．
 */
public class Simulator {

	/**
	 * ゲーム内で不変の情報を保持する変数．
	 */
	private GameData gameData;

	/**
	 * GameDataクラスのインスタンスを用いてインスタンスを生成するクラスコンストラクタ．
	 *
	 * @param gameData
	 *            GameDataクラスのインスタンス
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
	 * considered invalid by the simulator and all GROUND actions are considered
	 * invalid if the character is in air as well).<br>
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
	 *            If `simulationLimit` is less than the total number of actions
	 *            passed for the players, the simulator will simulate only
	 *            `simulationLimit` frames, and will not simulate frames
	 *            exceeding `simulationLimit`.<br>
	 *            If `simulationLimit` is more than the total number of actions
	 *            passed for the players, the simulator will simulate all the
	 *            given actions and then continue the simulation up to
	 *            `simulationLimit` frames assuming no actions are performed by
	 *            the characters.
	 *
	 * @return the frame data after the simulation
	 */
	public FrameData simulate(FrameData frameData, boolean playerNumber, Deque<Action> myAct, Deque<Action> oppAct,
			int simulationLimit) {

		// Create deep copy of each action's list
		ArrayList<Deque<Action>> tempActionList = new ArrayList<Deque<Action>>(2);
		Deque<Action> tempMyAct = (myAct == null) ? null : new LinkedList<Action>(myAct);
		Deque<Action> tempOppAct = (oppAct == null) ? null : new LinkedList<Action>(oppAct);
		tempActionList.add(tempMyAct);
		tempActionList.add(tempOppAct);

		ArrayList<ArrayList<Motion>> tempMotionList = new ArrayList<ArrayList<Motion>>(2);
		ArrayList<Motion> myMotionData = this.gameData.getMotion(playerNumber);
		ArrayList<Motion> oppMotionData = this.gameData.getMotion(!playerNumber);
		tempMotionList.add(myMotionData);
		tempMotionList.add(oppMotionData);

		int nowFrame = frameData.getFramesNumber();

		SimFighting simFighting = new SimFighting();
		simFighting.initialize(tempMotionList, tempActionList, new FrameData(frameData), playerNumber);

		for (int i = 0; i < simulationLimit; i++) {
			simFighting.processingFight(nowFrame);
			nowFrame++;
		}

		return simFighting.createFrameData(nowFrame, frameData.getRound(), null);
	}

}
