package aiinterface;

import informationcontainer.RoundResult;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;

public class SoundController extends Thread {
	
	private SoundDesignAIInterface ai;
	
	private boolean isFighting;
	
    private Object waitObj;

    /**
     * フレームデータを格納するリスト．
     */
    private FrameData frameData;

    private AudioData audioData;
    
    private boolean isRoundEnd;
    private RoundResult roundResult;
    
    public SoundController(SoundDesignAIInterface ai) {
    	this.ai = ai;
    	this.frameData = new FrameData();
    	this.audioData = new AudioData();
        this.clear();
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
            } else {
            	this.ai.getInformation(this.frameData);
            	this.ai.processing();
            	this.setInput(this.ai.input());
            }
            
	        ThreadController.getInstance().notifyEndSoundProcess();
		}
	}
	
	public synchronized void setFrameData(FrameData frameData) {
		System.out.println("Frame Number: " + frameData.getFramesNumber());
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
        this.audioData = new AudioData(input);
    }
	
	public synchronized void clear() {
        
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
