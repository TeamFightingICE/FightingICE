package aiinterface;

import informationcontainer.RoundResult;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;

public class StreamController extends Thread {

    private StreamInterface stream;
    
    private boolean isFighting;
    private Object waitObj;
	
    private FrameData frameData;
    private AudioData audioData;
    private ScreenData screenData;
    
    private boolean roundEndFlag;
    private RoundResult roundResult;
    
    public StreamController(StreamInterface stream) {
    	this.stream = stream;
    	this.clear();
    }
    
    public void initialize(Object waitFrame, GameData gameData) {
        this.waitObj = waitFrame;
        this.clear();
        this.isFighting = true;
        this.roundEndFlag = false;
        
    	this.stream.initialize(gameData);
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
            	this.stream.roundEnd(this.roundResult);
            	this.roundEndFlag = false;
            	this.roundResult = null;
            } else {
            	this.stream.getInformation(this.frameData);
    	        this.stream.getAudioData(this.audioData);
    	        this.stream.getScreenData(this.screenData);
    	        this.stream.processing();
            }
        }
    }
    
    public synchronized void setFrameData(FrameData fd, AudioData ad, ScreenData sd) {
        this.frameData = fd;
        this.audioData = ad;
        this.screenData = sd;
    }
    
    public synchronized void clear() {
    	this.frameData = new FrameData();
    	this.audioData = new AudioData();
    	this.screenData = new ScreenData();
    }
    
    public synchronized void informRoundResult(RoundResult roundResult) {
    	this.roundEndFlag = true;
    	this.roundResult = roundResult;
    }

    public synchronized void gameEnd() {
        this.isFighting = false;
        this.stream.close();
        synchronized (this.waitObj) {
            this.waitObj.notifyAll();
        }
    }
	
}
