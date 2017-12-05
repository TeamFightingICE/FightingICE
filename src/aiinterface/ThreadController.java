package aiinterface;

import setting.FlagSetting;

public class ThreadController {
	private static ThreadController threadController = new ThreadController();

	private Object AI1, AI2;

	private boolean processedAI1,processedAI2;
	private Object endFrame;

	private ThreadController() {
		this.AI1 = new Object();
		this.AI2 = new Object();
		this.endFrame = new Object();
		this.processedAI1 = false;
		this.processedAI2 = false;
	}

	public static ThreadController getInstance() {
		return threadController;
	}

	public void resetFlag(int i) {
		if(FlagSetting.fastModeFlag){
			if(canProcessing()){
				synchronized (AI1) {
					this.AI1.notifyAll();
				}
				synchronized (AI2) {
					this.AI2.notifyAll();
				}
			}
		}else{
			if (i == 0) {
				synchronized (AI1) {
					// System.out.println("reset AI1");
					this.AI1.notifyAll();
				}
			} else if (i == 1) {
				synchronized (AI2) {
					// System.out.println("reset AI2");
					this.AI2.notifyAll();
				}
			}
		}
	}

	public Object getAIsObject(boolean playerNumber) {
		if (playerNumber)
			return this.AI1;
		else
			return this.AI2;
	}
	
	public Object getEndFrame(){
		return this.endFrame;
	}
	
	public void setProcessedFlag(boolean playerNumber){
		if(playerNumber)
			processedAI1 = true;
		else
			processedAI2 = true;
		if(FlagSetting.fastModeFlag && processedAI1 && processedAI2){
			this.endFrame.notifyAll();
			processedAI1 = false;
			processedAI2 = false;
		}
	}
	
	public boolean canProcessing(){
		return !processedAI1 && !processedAI2;
	}
}
