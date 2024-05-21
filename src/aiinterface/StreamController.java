package aiinterface;

import java.util.LinkedList;

import informationcontainer.RoundResult;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;

public class StreamController extends Thread {
	
    private boolean isFighting;
    private StreamInterface stream;
	
    private final static int DELAY = 3;
    private int queueSize;
    private LinkedList<FrameData> framesData;
    private LinkedList<AudioData> audiosData;
    private LinkedList<ScreenData> screensData;
    private Object waitObj;
    
    private boolean roundEndFlag;
    private RoundResult roundResult;
    
    public StreamController(StreamInterface stream) {
    	this.stream = stream;
    }
    
    public void initialize(Object waitFrame, GameData gameData) {
        this.waitObj = waitFrame;
    	this.queueSize = 0;
        this.framesData = new LinkedList<FrameData>();
        this.audiosData = new LinkedList<AudioData>();
        this.screensData = new LinkedList<ScreenData>();
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
            
            if (this.roundEndFlag) {
            	this.stream.roundEnd(roundResult);
            	this.roundEndFlag = false;
            	this.roundResult = null;
            } else {
            	FrameData frameData = !this.framesData.isEmpty() ? this.framesData.removeFirst() : new FrameData();
                AudioData audioData = !this.audiosData.isEmpty() ? this.audiosData.removeFirst() : new AudioData();
                ScreenData screenData = !this.screensData.isEmpty() ? this.screensData.removeFirst() : new ScreenData();
                this.queueSize--;

            	this.stream.getInformation(frameData);
    	        this.stream.getAudioData(audioData);
    	        this.stream.getScreenData(screenData);
    	        this.stream.processing();
            }
        }
    }
    
    public synchronized void setFrameData(FrameData fd, AudioData ad, ScreenData sd) {
        this.framesData.addLast(fd);
        this.audiosData.addLast(ad);
        this.screensData.addLast(sd);
        this.queueSize++;

        while (this.queueSize > DELAY) {
            this.framesData.removeFirst();
            this.audiosData.removeFirst();
            this.screensData.removeFirst();
            this.queueSize--;
        }
    }
    
    public synchronized void clear() {
    	this.queueSize = 0;
    	this.framesData.clear();
    	this.audiosData.clear();
    	this.screensData.clear();

        while (this.queueSize < DELAY) {
            this.framesData.add(new FrameData());
            this.audiosData.add(new AudioData());
            this.screensData.add(new ScreenData());
            this.queueSize++;
        }
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
