import enumerate.Action;
import enumerate.State;

import aiinterface.AIInterface;
import aiinterface.CommandCenter;

import java.util.LinkedList;

import simulator.Simulator;
import struct.CharacterData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.MotionData;

import java.util.ArrayList;

/**
 * AI implementing MCTS
 *
 * @author Taichi Miyazaki
 */
public class OurMctsAi implements AIInterface {

  private Simulator simulator;
  private Key key;
  private CommandCenter commandCenter;
  private boolean playerNumber;
  private GameData gameData;

  /** Main FrameData */
  private FrameData frameData;

  /** Data with FRAME_AHEAD frames ahead of FrameData */
  private FrameData simulatorAheadFrameData;

  /** All actions that could be performed by self character */
  private LinkedList<Action> myActions;

  /** All actions that could be performed by the opponent character */
  private LinkedList<Action> oppActions;

  /** self information */
  private CharacterData myCharacter;

  /** opponent information */
  private CharacterData oppCharacter;

  /** Number of adjusted frames (following the same recipe in JerryMizunoAI) */
  private static final int FRAME_AHEAD = 14;

  private ArrayList<MotionData> myMotion;

  private ArrayList<MotionData> oppMotion;

  private Action[] actionAir;

  private Action[] actionGround;

  private Action spSkill;

  private Node rootNode;

  /** True if in debug mode, which will output related log */
  public static final boolean DEBUG_MODE = false;

  @Override
  public void close() {}

  @Override
  public void roundEnd(int p1Hp, int p2Hp, int frames) {}

  // @Override
  // public String getCharacter() {
  //   return CHARACTER_ZEN;
  // }

  @Override
  public void getInformation(FrameData frameData) {
    this.frameData = frameData;
    this.commandCenter.setFrameData(this.frameData, playerNumber);

    if (playerNumber) {
      myCharacter = frameData.getCharacter(true);
      oppCharacter = frameData.getCharacter(false);
    } else {
      myCharacter = frameData.getCharacter(false);
      oppCharacter = frameData.getCharacter(true);
    }
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

    actionAir =
        new Action[] {Action.AIR_GUARD, Action.AIR_A, Action.AIR_B, Action.AIR_DA, Action.AIR_DB,
            Action.AIR_FA, Action.AIR_FB, Action.AIR_UA, Action.AIR_UB, Action.AIR_D_DF_FA,
            Action.AIR_D_DF_FB, Action.AIR_F_D_DFA, Action.AIR_F_D_DFB, Action.AIR_D_DB_BA,
            Action.AIR_D_DB_BB};
    actionGround =
        new Action[] {Action.STAND_D_DB_BA, Action.BACK_STEP, Action.FORWARD_WALK, Action.DASH,
            Action.JUMP, Action.FOR_JUMP, Action.BACK_JUMP, Action.STAND_GUARD,
            Action.CROUCH_GUARD, Action.THROW_A, Action.THROW_B, Action.STAND_A, Action.STAND_B,
            Action.CROUCH_A, Action.CROUCH_B, Action.STAND_FA, Action.STAND_FB, Action.CROUCH_FA,
            Action.CROUCH_FB, Action.STAND_D_DF_FA, Action.STAND_D_DF_FB, Action.STAND_F_D_DFA,
            Action.STAND_F_D_DFB, Action.STAND_D_DB_BB};
    spSkill = Action.STAND_D_DF_FC;

    myMotion = this.playerNumber ? gameData.getMotionData(true) : gameData.getMotionData(false);
    oppMotion = this.playerNumber ? gameData.getMotionData(false) : gameData.getMotionData(true);

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

        mctsPrepare(); // Some preparation for MCTS
        rootNode =
            new Node(simulatorAheadFrameData, null, myActions, oppActions, gameData, playerNumber,
                commandCenter);
        rootNode.createNode();

        Action bestAction = rootNode.mcts(); // Perform MCTS
        if (OurMctsAi.DEBUG_MODE) {
          rootNode.printNode(rootNode);
        }

        commandCenter.commandCall(bestAction.name()); // Perform an action selected by MCTS
      }
    }
  }

  /**
   * Determine whether or not the AI can perform an action
   *
   * @return whether or not the AI can perform an action
   */
  public boolean canProcessing() {
    return !frameData.getEmptyFlag() && frameData.getRemainingTimeMilliseconds() > 0;
  }

  /**
   * Some preparation for MCTS
   * Perform the process for obtaining FrameData with 14 frames ahead
   */
  public void mctsPrepare() {
    simulatorAheadFrameData = simulator.simulate(frameData, playerNumber, null, null, FRAME_AHEAD);

    myCharacter = playerNumber ? simulatorAheadFrameData.getCharacter(true) : simulatorAheadFrameData.getCharacter(false);
    oppCharacter = playerNumber ? simulatorAheadFrameData.getCharacter(false) : simulatorAheadFrameData.getCharacter(true);

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
      if (Math.abs(myMotion.get(Action.valueOf(spSkill.name()).ordinal())
          .getAttackStartAddEnergy()) <= energy) {
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
}