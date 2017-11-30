package aiinterface;

public class ThreadController {
	private static ThreadController threadController = new ThreadController();

	private Object AI1, AI2;


	private ThreadController() {
		this.AI1 = new Object();
		this.AI2 = new Object();
	}

	public static ThreadController getInstance() {
		return threadController;
	}

	public void resetFlag(int i) {
		if (i == 0) {
			synchronized (AI1) {
				//System.out.println("reset AI1");
				this.AI1.notifyAll();
			}
		} else if (i == 1) {
			synchronized (AI2) {
				//System.out.println("reset AI2");
				this.AI2.notifyAll();
			}
		}
	}

	public Object getAIsObject(int i) {
		if (i == 0)
			return this.AI1;
		else if (i == 1)
			return this.AI2;
		return null;
	}
}
