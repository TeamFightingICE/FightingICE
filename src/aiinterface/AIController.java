package aiinterface;

import java.util.LinkedList;

import informationcontainer.RoundResult;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import util.Transform;

public class AIController extends Thread {

	private AIInterface ai;

	private boolean playerNumber;

	private Key key;

	private final static int DELAY = 14;

	private LinkedList<FrameData> framesData;

	private Object waitObj;

	public AIController(AIInterface ai) {
		this.ai = ai;
	}

	public void initialize(Object waitFrame, GameData gameData, boolean playerNumber){
		this.playerNumber = playerNumber;
		this.waitObj = waitFrame;
		this.key = new Key();
		this.framesData = new LinkedList<FrameData>();
		this.clear();
		this.ai.initialize(gameData,playerNumber);
	}


	@Override
	public void run() {
		System.out.println("Start run");
		while (true) {
			synchronized (this.waitObj) {
				try {
					//System.out.println("lock AI"+(this.playerNumber? 1:2));
					this.waitObj.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			this.ai.getInformation(this.framesData.removeFirst());
			this.ai.processing();
//			ThreadController.getInstance().resetFlag(this.Num);
			System.out.println("AI" + Transform.convertPlayerNumberfromBtoI(playerNumber) + "run");
		}

	}

	public Key getInput() {
		return new Key();
	}

	public void setFrameData(FrameData fd){
		this.framesData.addLast(fd);
		while(this.framesData.size()>DELAY){
			this.framesData.removeFirst();
		}
	}

	public void clear(){
		System.out.println("init AI");
		this.framesData.clear();
		while(framesData.size()<DELAY){
			this.framesData.add(new FrameData());
		}
	}

	public void informRoundResult(RoundResult roundResult){
		this.ai.roundEnd(roundResult.getRemainingHPs()[0],roundResult.getRemainingHPs()[1],roundResult.getElapsedFrame());
	}
}
