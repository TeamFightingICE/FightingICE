import java.util.ArrayList;
import java.util.LinkedList;

import aiinterface.AIInterface;
import aiinterface.CommandCenter;
import enumerate.Action;
import enumerate.State;
import simulator.Simulator;
import struct.CharacterData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.MotionData;

/**
 * MCTS(モンテカルロ木探索)により実装するAI
 *
 * @author Taichi
 */
public class MctsAi65 implements AIInterface {

	private Simulator simulator;
	private Key key;
	private CommandCenter commandCenter;
	private boolean playerNumber;
	private GameData gameData;

	/** 大本のFrameData */
	private FrameData frameData;

	/** 大本よりFRAME_AHEAD分遅れたFrameData */
	private FrameData simulatorAheadFrameData;

	/** 自分が行える行動全て */
	private LinkedList<Action> myActions;

	/** 相手が行える行動全て */
	private LinkedList<Action> oppActions;

	/** 自分の情報 */
	private CharacterData myCharacter;

	/** 相手の情報 */
	private CharacterData oppCharacter;

	/** フレームの調整用時間(JerryMizunoAIを参考) */
	private static final int FRAME_AHEAD = 14;

	private ArrayList<MotionData> myMotion;

	private ArrayList<MotionData> oppMotion;

	private Action[] actionAir;

	private Action[] actionGround;

	private Action spSkill;

	private Node rootNode;

	/** デバッグモードであるかどうか。trueの場合、様々なログが出力される */
	public static final boolean DEBUG_MODE = false;

	@Override
	public void close() {
	}

	@Override
	public int initialize(GameData gameData, boolean playerNumber) {
		this.playerNumber = playerNumber;
		this.gameData = gameData;

		this.key = new Key();
		this.frameData = new FrameData();
		this.commandCenter = new CommandCenter();

		this.myActions = new LinkedList<Action>();
		this.oppActions = new LinkedList<Action>();

		simulator = gameData.getSimulator();

		actionAir = new Action[] { Action.AIR_GUARD, Action.AIR_A, Action.AIR_B, Action.AIR_DA, Action.AIR_DB,
				Action.AIR_FA, Action.AIR_FB, Action.AIR_UA, Action.AIR_UB, Action.AIR_D_DF_FA, Action.AIR_D_DF_FB,
				Action.AIR_F_D_DFA, Action.AIR_F_D_DFB, Action.AIR_D_DB_BA, Action.AIR_D_DB_BB };
		actionGround = new Action[] { Action.STAND_D_DB_BA, Action.BACK_STEP, Action.FORWARD_WALK, Action.DASH,
				Action.JUMP, Action.FOR_JUMP, Action.BACK_JUMP, Action.STAND_GUARD, Action.CROUCH_GUARD, Action.THROW_A,
				Action.THROW_B, Action.STAND_A, Action.STAND_B, Action.CROUCH_A, Action.CROUCH_B, Action.STAND_FA,
				Action.STAND_FB, Action.CROUCH_FA, Action.CROUCH_FB, Action.STAND_D_DF_FA, Action.STAND_D_DF_FB,
				Action.STAND_F_D_DFA, Action.STAND_F_D_DFB, Action.STAND_D_DB_BB };
		spSkill = Action.STAND_D_DF_FC;

		myMotion = gameData.getMotionData(this.playerNumber);
		oppMotion = gameData.getMotionData(!this.playerNumber);

		return 0;
	}

	@Override
	public Key input() {
		return key;
	}

	@Override
	public void processing() {

		if (canProcessing()) {
			if (commandCenter.getSkillFlag()) {
				key = commandCenter.getSkillKey();
			} else {
				key.empty();
				commandCenter.skillCancel();

				mctsPrepare(); // MCTSの下準備を行う
				rootNode = new Node(simulatorAheadFrameData, null, myActions, oppActions, gameData, playerNumber,
						commandCenter);
				rootNode.createNode();

				Action bestAction = rootNode.mcts(); // MCTSの実行
				if (MctsAi65.DEBUG_MODE) {
					rootNode.printNode(rootNode);
				}

				commandCenter.commandCall(bestAction.name()); // MCTSで選択された行動を実行する
			}
		}
	}

	/**
	 * AIが行動できるかどうかを判別する
	 *
	 * @return AIが行動できるかどうか
	 */
	public boolean canProcessing() {
		return !frameData.getEmptyFlag() && frameData.getRemainingFramesNumber() > 0;
	}

	/**
	 * MCTSの下準備 <br>
	 * 14フレーム進ませたFrameDataの取得などを行う
	 */
	public void mctsPrepare() {
		simulatorAheadFrameData = simulator.simulate(frameData, playerNumber, null, null, FRAME_AHEAD);

		myCharacter = simulatorAheadFrameData.getCharacter(playerNumber);
		oppCharacter = simulatorAheadFrameData.getCharacter(!playerNumber);

		setMyAction();
		setOppAction();
	}

	public void setMyAction() {
		myActions.clear();

		int energy = myCharacter.getEnergy();

		if (myCharacter.getState() == State.AIR) {
			for (int i = 0; i < actionAir.length; i++) {
				if (Math.abs(myMotion.get(Action.valueOf(actionAir[i].name()).ordinal())
						.getAttackStartAddEnergy()) <= energy) {
					myActions.add(actionAir[i]);
				}
			}
		} else {
			if (Math.abs(
					myMotion.get(Action.valueOf(spSkill.name()).ordinal()).getAttackStartAddEnergy()) <= energy) {
				myActions.add(spSkill);
			}

			for (int i = 0; i < actionGround.length; i++) {
				if (Math.abs(myMotion.get(Action.valueOf(actionGround[i].name()).ordinal())
						.getAttackStartAddEnergy()) <= energy) {
					myActions.add(actionGround[i]);
				}
			}
		}

	}

	public void setOppAction() {
		oppActions.clear();

		int energy = oppCharacter.getEnergy();

		if (oppCharacter.getState() == State.AIR) {
			for (int i = 0; i < actionAir.length; i++) {
				if (Math.abs(oppMotion.get(Action.valueOf(actionAir[i].name()).ordinal())
						.getAttackStartAddEnergy()) <= energy) {
					oppActions.add(actionAir[i]);
				}
			}
		} else {
			if (Math.abs(oppMotion.get(Action.valueOf(spSkill.name()).ordinal())
					.getAttackStartAddEnergy()) <= energy) {
				oppActions.add(spSkill);
			}

			for (int i = 0; i < actionGround.length; i++) {
				if (Math.abs(oppMotion.get(Action.valueOf(actionGround[i].name()).ordinal())
						.getAttackStartAddEnergy()) <= energy) {
					oppActions.add(actionGround[i]);
				}
			}
		}
	}

	@Override
	public void roundEnd(int p1Hp, int p2Hp, int frames) {

	}

	@Override
	public void getInformation(FrameData frameData, boolean arg1) {
		// TODO Auto-generated method stub
		this.frameData = frameData;
		this.commandCenter.setFrameData(this.frameData, playerNumber);

		myCharacter = frameData.getCharacter(playerNumber);
		oppCharacter = frameData.getCharacter(!playerNumber);
	}
}
