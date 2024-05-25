package aiinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import informationcontainer.RoundResult;
import manager.InputManager;
import service.SocketGenerativeSound;
import service.SocketServer;
import service.SocketStream;
import setting.FlagSetting;
import setting.LaunchSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;

/**
 * AIの実行のタイミングなどのスレッド関連の処理を扱うクラス．
 */
public class ThreadController {

	/**
	 * ThreadController唯一のインスタンス
	 */
	private static ThreadController threadController = new ThreadController();
	
	private AIController[] ais;
	private SoundController sound;
	private List<StreamController> streams;
	private List<ControllerInterface> controllerList;
	
	private List<Object> waitObjs;

	private boolean[] processedAIs;
	private boolean processedSound;

	/**
	 * 各AIの処理を同時に始めるための同期用オブジェクト
	 */
	private Object endFrame;

	/**
	 * フィールド変数を初期化するクラスコンストラクタ
	 */
	private ThreadController() {
		this.ais = new AIController[2];
		this.streams = new ArrayList<>();
		
		this.waitObjs = new ArrayList<>();
		this.controllerList = new ArrayList<>();
		
		this.processedAIs = new boolean[2];
		resetProcessedFlag();
	}

	/**
	 * ThreadControllerクラスの唯一のインスタンスを取得する．
	 *
	 * @return ThreadControllerクラスの唯一のインスタンス
	 */
	public static ThreadController getInstance() {
		return threadController;
	}
	
	public AIController getAIController(boolean playerNumber) {
		return this.ais[playerNumber ? 0 : 1];
	}
	
	public SoundController getSoundController() {
		return this.sound;
	}
	
	public void setAIController(int i, AIController ai) {
		this.ais[i] = ai;
		
		if (ai != null) {
			this.controllerList.add(ai);
		}
	}
	
	public void createSoundController() {
		SocketGenerativeSound generativeSound = SocketServer.getInstance().getGenerativeSound();
		if (!generativeSound.isCancelled()) {
			this.sound = new SoundController(generativeSound);
			this.controllerList.add(this.sound);
		}
	}
	
	public void createStreamControllers() {
		for (SocketStream socketStream : SocketServer.getInstance().getStreams()) {
			if (!socketStream.isCancelled()) {
				StreamController stream = new StreamController(socketStream);
				this.streams.add(stream);
				this.controllerList.add(stream);
			}
		}
	}
	
	public Object createWaitObject() {
		Object waitObj = new Object();
		this.waitObjs.add(waitObj);
		return waitObj;
	}
	
	/**
	 * AIコントローラの動作を開始させる．<br>
	 * 引数のGameDataクラスのインスタンスを用いてAIコントローラを初期化し，AIの動作を開始する．
	 *
	 * @param gameData
	 *            GameDataクラスのインスタンス
	 * @see GameData
	 */
	public void startAI(GameData gameData) {
		for (int i = 0; i < 2; i++) {
			if (this.ais[i] != null) {
				this.ais[i].initialize(createWaitObject(), gameData, i == 0);
				this.ais[i].start();// start the thread
		        Logger.getAnonymousLogger().log(Level.INFO, String.format("Start P%s AI controller thread", i == 0 ? "1" : "2"));
			}
		}
	}
	
	public void startSound(GameData gameData) {
        if (this.sound != null) {
            this.sound.initialize(createWaitObject(), gameData);
            this.sound.start();
        	Logger.getAnonymousLogger().log(Level.INFO, "Start Sound controller thread");
        }
	}
	
	public void startStreams(GameData gameData) {
		for (int i = 0; i < this.streams.size(); i++) {
			StreamController stream = this.streams.get(i);
			stream.initialize(createWaitObject(), gameData);
            stream.start();
        	Logger.getAnonymousLogger().log(Level.INFO, String.format("Start Stream controller thread #%d", i + 1));
		}
	}
	
	public void resetAllObjects() {
		for (Object waitObj : this.waitObjs) {
			synchronized (waitObj) {
				waitObj.notifyAll();
			}
		}
		
		if (FlagSetting.inputSyncFlag) {
			while (!(isAIProcessed() && isSoundProcessed() && checkThreadState(Thread.State.WAITING))) {
				try {
	    			Thread.sleep(1);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
			}
			
			resetProcessedFlag();
		}
	}
	
	public void setFrameData(FrameData frameData, ScreenData screenData, AudioData audioData) {
		for (ControllerInterface controller : this.controllerList) {
			controller.setFrameData(frameData, screenData, audioData);
		}
		
		resetAllObjects();
	}
	
	public void sendRoundResult(RoundResult roundResult) {
		for (ControllerInterface controller : this.controllerList) {
			controller.informRoundResult(roundResult);
		}
		
		resetAllObjects();
	}
	
	public void gameEnd() {
		for (ControllerInterface controller : this.controllerList) {
			controller.gameEnd();
		}
		
		resetProcessedFlag();
	}
	
	/**
	 * 各AIコントローラ内に保持されているフレームデータをクリアする.
	 */
	public void clear() {
		for (ControllerInterface controller : this.controllerList) {
			controller.clear();
		}
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
		this.processedAIs[0] = false;
		this.processedAIs[1] = false;
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
	public void notifyEndAIProcess(boolean playerNumber) {
		this.processedAIs[playerNumber ? 0 : 1] = true;
	}
	
	public void notifyEndSoundProcess() {
		this.processedSound = true;
	}
	
	private boolean isAIProcessed() {
		boolean ans = true;
		for (int i = 0; i < 2; i++) {
			ans = ans && (LaunchSetting.deviceTypes[i] == InputManager.DEVICE_TYPE_KEYBOARD || this.processedAIs[i]);
		}
		return ans;
	}
	
	private boolean isSoundProcessed() {
		SocketGenerativeSound generativeSound = SocketServer.getInstance().getGenerativeSound();
		return generativeSound.isCancelled() || this.processedSound;
	}
	
	private boolean checkThreadState(Thread.State threadState) {
		boolean ans = true;
		for (int i = 0; i < 2; i++) {
			if (this.ais[i] != null) {
				ans = ans && this.ais[i].getState() == threadState;
			}
		}
		
		if (this.sound != null) {
			ans = ans && this.sound.getState() == threadState;
		}
		
		return ans;
	}
	
	public void close() {
		this.ais = new AIController[2];
		this.sound = null;
		this.streams.clear();
		this.waitObjs.clear();
		this.controllerList.clear();
	}

}
