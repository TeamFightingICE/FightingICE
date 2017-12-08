package python;

import gamescene.Replay;
import loader.ResourceLoader;
import struct.FrameData;

public class PyReplay implements StateInhibitor {

	enum State {
		NONE, INIT, UPDATE, CLOSE
	}

	private Replay replay;

	private State state;

	private Object waiter;

	public PyReplay() {
		this.state = State.NONE;
		this.waiter = new Object();
	}

	public void init() {
		PyManager.python.setStateInhibitor(this);
		this.state = State.INIT;
		this.replay = new Replay();

		synchronized (waiter) {
			try {
				waiter.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void replayUpdate() {
		switch (this.state) {
		case INIT:
			ResourceLoader.getInstance().loadResource();
			this.replay.initialize();
			break;
		case UPDATE:
			this.replay.update();
			break;
		case CLOSE:
			this.replay.close();
			PyManager.python.setStateInhibitor(null);
			break;
		default:
			break;
		}

		this.state = State.NONE;
		synchronized (this.waiter) {
			this.waiter.notifyAll();
		}
	}

	public void close() {
		this.state = State.CLOSE;
		
		synchronized (waiter) {
			try {
				waiter.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateState() {
		this.state = State.UPDATE;

		synchronized (waiter) {
			try {
				waiter.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public FrameData getFrameData() {
		return this.replay.getFrameData();
	}
	
	public State getState() {
		return this.state;
	}

}
