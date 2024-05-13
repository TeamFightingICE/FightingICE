package aiinterface;

import informationcontainer.RoundResult;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;

public class SoundController extends Thread {
	
	private SoundDesignAIInterface ai;
	
	private boolean isFighting;
	private AudioData input;
	
    private Object waitObj;
    
    private FrameData frameData;
    
    private boolean isRoundEnd;
    private RoundResult roundResult;
    
    public SoundController(SoundDesignAIInterface ai) {
    	this.ai = ai;
    }
    
    public void initialize(Object waitFrame, GameData gameData) {
    	this.waitObj = waitFrame;
        this.isFighting = true;
        this.isRoundEnd = false;
    }
	
	@Override
    public void run() {
		while (isFighting) {
            synchronized (this.waitObj) {
                try {
                    this.waitObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            if (isRoundEnd) {
            	this.ai.roundEnd(roundResult);
            	this.isRoundEnd = false;
            	this.roundResult = null;
            } else if (!frameData.getEmptyFlag()) {
            	this.ai.getInformation(frameData);
            	this.ai.processing();
            	this.setInput(this.ai.input());
            }
            
	        ThreadController.getInstance().notifyEndSoundProcess();
		}
	}
	
	public synchronized void setFrameData(FrameData frameData) {
		this.frameData = frameData;
	}
	
	public synchronized AudioData getInput() {
		if (this.input != null) {
			return this.input;
		} else {
			return new AudioData();
		}
	}
	
	public synchronized void setInput(byte[] input) {
        this.input = new AudioData(input);
    }
	
	public synchronized void informRoundResult(RoundResult roundResult) {
    	this.isRoundEnd = true;
    	this.roundResult = roundResult;
    }
	
	public synchronized void gameEnd() {
        this.isFighting = false;
        this.ai.close();
        synchronized (this.waitObj) {
            this.waitObj.notifyAll();
        }
    }
	
}
