package struct;

import java.util.ArrayList;
import java.util.Vector;

import org.javatuples.Triplet;

import enumerate.Action;
import fighting.Character;
import simulator.Simulator;

public class GameData {
    /**
     * delete  private int "stageXMax,stageYMax,playerOneMaxEnergy,playerTwoMaxEnergy"
     * ,GameData Function args"int stageX, int stageY"
     * and public int getter "getMyMaxEnergy,getOpponentMaxEnergy"
     */

    private Vector<MotionData> playerOneMotion;

    private Vector<MotionData> playerTwoMotion;

    private String[] characterNames = new String[2];

    private String[] aiNames = new String[2];

    private Simulator simulator;


    private ArrayList<Vector<Triplet<Vector<Action>, Vector<Action>, Integer>>> comboTable;

    public GameData(Character playerOne, Character playerTwo){

        this.characterNames[0] = playerOne.getName();
        this.characterNames[1] = playerTwo.getName();

        this.aiNames[0] = "";
        this.aiNames[1] = "";

        this.playerOneMotion = new Vector<MotionData>();
        this.playerTwoMotion = new Vector<MotionData>();

        for(Action act : Action.values()){
            MotionData motionObjectOne = new MotionData(playerOne.getMotionVector().elementAt(act.ordinal()));
            MotionData motionObjectTwo = new MotionData(playerTwo.getMotionVector().elementAt(act.ordinal()));
            playerOneMotion.addElement(motionObjectOne);
            playerTwoMotion.addElement(motionObjectTwo);
        }

        this.comboTable = new ArrayList<Vector<Triplet<Vector<Action>, Vector<Action>, Integer>>>(2);
        this.comboTable.add(playerOne.getComboTable());
        this.comboTable.add(playerTwo.getComboTable());

        simulator = new Simulator(this);

    }

    public GameData(Character playerOne, Character playerTwo, String playerOneName, String playerTwoName){//

        this.characterNames[0] = playerOne.getName();
        this.characterNames[1] = playerTwo.getName();

        this.aiNames[0] = playerOneName;
        this.aiNames[1] = playerTwoName;

        this.playerOneMotion = new Vector<MotionData>();
        this.playerTwoMotion = new Vector<MotionData>();

        for(Action act : Action.values()){
            MotionData motionObjectOne = new MotionData(playerOne.getMotionVector().elementAt(act.ordinal()));
            MotionData motionObjectTwo = new MotionData(playerTwo.getMotionVector().elementAt(act.ordinal()));
            playerOneMotion.addElement(motionObjectOne);
            playerTwoMotion.addElement(motionObjectTwo);
        }

        this.comboTable = new ArrayList<Vector<Triplet<Vector<Action>, Vector<Action>, Integer>>>(2);
        this.comboTable.add(playerOne.getComboTable());
        this.comboTable.add(playerTwo.getComboTable());

        simulator = new Simulator(this);

    }
    /**Getter*/
    public Vector<MotionData> getPlayerOneMotion() {
        return playerOneMotion;
    }


    public Vector<MotionData> getPlayerTwoMotion() {
        return playerTwoMotion;
    }

    public String getPlayerOneCharacterName(){
        return characterNames[0];
    }

    public String getPlayerTwoCharacterName(){
        return characterNames[1];
    }


    public String getPlayerOneAiName(){
        return aiNames[0];
    }


    public String getPlayerTwoAiName(){
        return aiNames[1];
    }


    public Vector<MotionData> getMyMotion(boolean playerNumber){
        return playerNumber ? getPlayerOneMotion() : getPlayerTwoMotion();
    }


    public Vector<MotionData> getOpponentMotion(boolean playerNumber){
        return playerNumber ? getPlayerTwoMotion() : getPlayerOneMotion();
    }


    public String getMyName(boolean playerNumber){
        return playerNumber ? getPlayerOneCharacterName() : getPlayerTwoCharacterName();
    }


    public String getOpponentName(boolean playerNumber){
        return playerNumber ? getPlayerTwoCharacterName() : getPlayerOneCharacterName();
    }


    public Simulator getSimulator(){
        return this.simulator;
    }


    public ArrayList<Vector<Triplet<Vector<Action>, Vector<Action>, Integer>>> getComboTable(){
        return this.comboTable;
    }

}


