package aiinterface;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import informationcontainer.RoundResult;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.ScreenData;

public class AIController extends Thread {

	private AIInterface ai;

	private boolean playerNumber;

	private boolean isFighting;
	
	private Key key;

	private final static int DELAY = 14;

	private LinkedList<FrameData> framesData;

	private ScreenData screenData;

	private Object waitObj;

	public AIController(AIInterface ai) {
		this.ai = ai;
	}

	public void initialize(Object waitFrame, GameData gameData, boolean playerNumber) {
		this.playerNumber = playerNumber;
		this.waitObj = waitFrame;
		this.key = new Key();
		this.framesData = new LinkedList<FrameData>();
		this.clear();
		this.isFighting = true;
		this.ai.initialize(gameData, playerNumber);
	}

	@Override
	public void run() {
		Logger.getAnonymousLogger().log(Level.INFO, "Start to run");
		while (isFighting) {
			synchronized (this.waitObj) {
				try {
					// System.out.println("lock AI"+(this.playerNumber? 1:2));
					this.waitObj.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			this.ai.getInformation(this.framesData.removeFirst());
			this.ai.getScreenData(this.screenData);
			this.ai.processing();
			setInput(this.ai.input());
			// ThreadController.getInstance().resetFlag(this.Num);
			// System.out.println("AI" +
			// Transform.convertPlayerNumberfromBtoI(playerNumber) + "run");
		}
		//ai.close();
		

	}

	public synchronized Key getInput() {
		if (this.key != null) {
			return this.key;
		} else {
			return new Key();
		}
	}

	private synchronized void setInput(Key key) {
		this.key = new Key(key);
	}

	public synchronized void setFrameData(FrameData fd) {
		this.framesData.addLast(fd);
		while (this.framesData.size() > DELAY) {
			this.framesData.removeFirst();
		}
	}

	public void setScreenData(ScreenData screenData) {
		this.screenData = screenData;
	}

	public void clear() {
		this.framesData.clear();

		while (this.framesData.size() < DELAY) {
			this.framesData.add(new FrameData());
		}
	}

	public void informRoundResult(RoundResult roundResult) {
		this.ai.roundEnd(roundResult.getRemainingHPs()[0], roundResult.getRemainingHPs()[1],
				roundResult.getElapsedFrame());
	}
	
	public synchronized void gameEnd(){
		isFighting = false;
		synchronized(this.waitObj) {
			this.ai.close();
			this.waitObj.notifyAll();
		}
	}
}
