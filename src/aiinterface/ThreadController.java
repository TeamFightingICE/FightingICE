package aiinterface;

import java.util.ArrayList;
import java.util.List;

import manager.InputManager;
import service.SocketGenerativeSound;
import service.SocketServer;
import setting.LaunchSetting;

/**
 * AIの実行のタイミングなどのスレッド関連の処理を扱うクラス．
 */
public class ThreadController {

	/**
	 * ThreadController唯一のインスタンス
	 */
	private static ThreadController threadController = new ThreadController();

	/**
	 * P1のAIの処理の開始のタイミングを管理するオブジェクト．
	 */
	private Object AI1;

	/**
	 * P2のAIの処理の開始のタイミングを管理するオブジェクト．
	 */
	private Object AI2;
	
	private Object sound;
	
	private List<Object> waitObjs;

	/**
	 * P1のAIの処理が終わったかどうかを表すフラグ．<br>
	 * Fastmodeのときのみ使用される．
	 */
	private boolean processedAI1;

	/**
	 * P2のAIの処理が終わったかどうかを表すフラグ．<br>
	 * Fastmodeのときのみ使用される．
	 */
	private boolean processedAI2;
	
	private boolean processedSound;

	/**
	 * 各AIの処理を同時に始めるための同期用オブジェクト
	 */
	private Object endFrame;

	/**
	 * フィールド変数を初期化するクラスコンストラクタ
	 */
	private ThreadController() {
		this.AI1 = new Object();
		this.AI2 = new Object();
		this.sound = new Object();
		this.endFrame = new Object();
		
		this.waitObjs = new ArrayList<>();
		this.addWaitObject(this.AI1);
		this.addWaitObject(this.AI2);
		this.addWaitObject(this.sound);

		resetProcessedFlag();
	}
	
	public void addWaitObject(Object waitObj) {
		this.waitObjs.add(waitObj);
	}

	/**
	 * ThreadControllerクラスの唯一のインスタンスを取得する．
	 *
	 * @return ThreadControllerクラスの唯一のインスタンス
	 */
	public static ThreadController getInstance() {
		return threadController;
	}

	/**
	 * 各AIの処理を再開させる．
	 */
	public void resetAllObjects() {
		for (Object waitObj : this.waitObjs) {
			synchronized (waitObj) {
				waitObj.notifyAll();
			}
		}
	}

	/**
	 * 引数に指定したキャラクターの同期用オブジェクトを返す．
	 *
	 * @param playerNumber
	 *            The character's side flag.<br>
	 *            {@code true} if the character is P1, or {@code false} if P2.
	 *
	 * @return 引数に指定したキャラクターの同期用オブジェクト
	 */
	public Object getAIsObject(boolean playerNumber) {
		if (playerNumber)
			return this.AI1;
		else
			return this.AI2;
	}
	
	public Object getSoundObject() {
		return this.sound;
	}

	/**
	 * 1フレーム分のゲームの処理が終わったことを示すオブジェクトを返す．
	 *
	 * @return 1フレーム分のゲームの処理が終わったことを示すオブジェクト．
	 */
	public Object getEndFrame() {
		return this.endFrame;
	}

	/**
	 * 各AIの処理が終わったかどうかを表すフラグを{@code false}にする．<br>
	 * Fastmodeのときのみ使用される．
	 */
	private void resetProcessedFlag() {
		this.processedAI1 = false;
		this.processedAI2 = false;
		this.processedSound = false;
	}

	/**
	 * 引数に指定したキャラクターの1フレーム分の処理が終わったことをセットする．<br>
	 * セット後に，両方のAIが処理を終えているかどうかをチェックする．<br>
	 * Fastmodeのときのみ使用される．
	 *
	 * @param playerNumber
	 *            The character's side flag.<br>
	 *            {@code true} if the character is P1, or {@code false} if P2.
	 */
	synchronized public void notifyEndAIProcess(boolean playerNumber) {
		if (playerNumber) {
			this.processedAI1 = true;
		} else {
			this.processedAI2 = true;
		}
		this.checkEndFrame();
	}
	
	synchronized public void notifyEndSoundProcess() {
		this.processedSound = true;
		this.checkEndFrame();
	}
	
	private boolean isAIProcessed() {
		boolean ans = true;
		for (int i = 0; i < 2; i++) {
			char deviceType = LaunchSetting.deviceTypes[i];
			boolean processedAI = i == 0 ? this.processedAI1 : this.processedAI2;
			ans = ans && (deviceType == InputManager.DEVICE_TYPE_KEYBOARD || processedAI);
		}
		return ans;
	}
	
	private boolean isSoundProcessed() {
		SocketGenerativeSound generativeSound = SocketServer.getInstance().getGenerativeSound();
		return generativeSound.isCancelled() || this.processedSound;
	}

	/**
	 * 現在のフレームにおいて，両AIが処理を終えているかどうかをチェックする．<br>
	 * 終えている場合は，次のフレームの処理を開始させる．<br>
	 * Fastmodeのときのみ使用される．
	 */
	private void checkEndFrame() {
		if (isAIProcessed() && isSoundProcessed()) {
			synchronized (this.endFrame) {
				this.endFrame.notifyAll();
			}
			resetProcessedFlag();
		}
	}

}
