package python;

import gamescene.Replay;
import loader.ResourceLoader;
import struct.FrameData;

/**
 * Pythonでリプレイの再生を行えるようにするクラス．
 */
public class PyReplay implements StateInhibitor {

	/**
	 * リプレイの状態の種類を管理する列挙型．<br>
	 * NONE: 何もしていない<br>
	 * INIT: 初期化<br>
	 * UPDATE: リプレイファイルを再生<br>
	 * CLOSE: 再生終了
	 */
	enum State {
		NONE, INIT, UPDATE, CLOSE
	}

	/** リプレイの再生を行うクラス */
	private Replay replay;

	/**
	 * 現在のリプレイの状態．<br>
	 * NONE: 何もしていない<br>
	 * INIT: 初期化<br>
	 * UPDATE: リプレイファイルを再生<br>
	 * CLOSE: 再生終了
	 */
	private State state;

	/**
	 * Python側と同期を取るためのオブジェクト．
	 */
	private Object waiter;

	/**
	 * クラスコンストラクタ．
	 */
	public PyReplay() {
		this.state = State.NONE;
		this.waiter = new Object();
	}

	/**
	 * Initializes the replay.
	 *
	 * @throws InterruptedException
	 *             If the thread is interrupted while waiting
	 */
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

	/**
	 * Closes the replay.<br>
	 * Should be called at the end when you don't need the replay anymore.
	 *
	 * @throws InterruptedException
	 *             If the thread is interrupted while waiting
	 */
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

	/**
	 * Simulates one frame of the replay.
	 *
	 * @throws InterruptedException
	 *             If the thread is interrupted while waiting
	 */
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

	/**
	 * Gets the frame data of the current frame.
	 *
	 * @return the frame data of the current frame
	 */
	public FrameData getFrameData() {
		return this.replay.getFrameData();
	}

	/**
	 * Gets the state of the replay.<br>
	 * NONE/INIT/UPDATE/CLOSE
	 *
	 * @return the the state of the replay
	 */
	public State getState() {
		return this.state;
	}

}
