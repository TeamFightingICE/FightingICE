package aiinterface;

public class ThreadController {
	private static ThreadController threadController = new ThreadController();

	private Object AI1, AI2;

	private boolean processedAI1, processedAI2;
	private Object endFrame;

	private ThreadController() {
		this.AI1 = new Object();
		this.AI2 = new Object();
		this.endFrame = new Object();
		resetProcessedFlag();
	}

	public static ThreadController getInstance() {
		return threadController;
	}

	public void resetAllAIsObj() {
		synchronized (AI1) {
			this.AI1.notifyAll();
		}
		synchronized (AI2) {
			this.AI2.notifyAll();
		}
	}

	public Object getAIsObject(boolean playerNumber) {
		if (playerNumber)
			return this.AI1;
		else
			return this.AI2;
	}

	public Object getEndFrame() {
		return this.endFrame;
	}

	private void resetProcessedFlag() {
		this.processedAI1 = false;
		this.processedAI2 = false;
	}

	synchronized public void notifyEndProcess(boolean playerNumber){
		if (playerNumber) {
			processedAI1 = true;
		} else {
			processedAI2 = true;
		}
		this.checkEndFrame();
	}

	public void checkEndFrame() {
		if (processedAI1 && processedAI2) {
			synchronized (this.endFrame) {
				this.endFrame.notifyAll();
			}
			processedAI1 = false;
			processedAI2 = false;
		}
	}

}
