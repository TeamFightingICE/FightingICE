package aiinterface;

import informationcontainer.RoundResult;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;

public class SoundController extends Thread {
	
	private SoundDesignAIInterface ai;
	
	private boolean isFighting;
    private Object waitObj;

    private FrameData frameData;
    private AudioData audioData;
    
    private boolean roundEndFlag;
    private RoundResult roundResult;
    
    public SoundController(SoundDesignAIInterface ai) {
    	this.ai = ai;
        this.clear();
    }
    
    public void initialize(Object waitFrame, GameData gameData) {
    	this.waitObj = waitFrame;
        this.isFighting = true;
        this.roundEndFlag = false;
        
        this.ai.initialize(gameData);
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
            
            if (!isFighting) break;
            
            if (this.roundEndFlag) {
            	this.ai.roundEnd(this.roundResult);
            	this.roundEndFlag = false;
            	this.roundResult = null;
            } else {
            	this.ai.getInformation(this.frameData);
            	this.ai.processing();
            	this.setInput(this.ai.input());
            }
            
	        ThreadController.getInstance().notifyEndSoundProcess();
		}
	}
	
	public synchronized void setFrameData(FrameData frameData) {
        this.frameData = frameData;
    }
	
	public synchronized AudioData getAudioData() {
		if (this.audioData != null) {
			return this.audioData;
		} else {
			return new AudioData();
		}
	}
	
	public synchronized void setInput(AudioData input) {
        this.audioData = input;
    }
	
	public synchronized void clear() {
        this.frameData = new FrameData();
        this.audioData = new AudioData();
    }
	
	public synchronized void informRoundResult(RoundResult roundResult) {
    	this.roundEndFlag = true;
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
