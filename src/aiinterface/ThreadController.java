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
	
	private boolean isFighting;
	
	/**
	 * AIコントローラを格納する配列．
	 */
	private AIController[] ais;
	
	private SoundController sound;
	
	private List<StreamController> streams;
	
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
		this.isFighting = false;
		this.endFrame = new Object();
		
		this.ais = new AIController[InputManager.DEFAULT_DEVICE_NUMBER];
		this.streams = new ArrayList<>();
		
		this.waitObjs = new ArrayList<>();

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
	
	public AIController getAIController(boolean playerNumber) {
		return this.ais[playerNumber ? 0 : 1];
	}
	
	public SoundController getSoundController() {
		return this.sound;
	}
	
	public void closeAI() {
		this.ais = new AIController[InputManager.DEFAULT_DEVICE_NUMBER];
		this.sound = null;
		this.streams.clear();
	}
	
	public void setAIController(int i, AIController ai) {
		this.ais[i] = ai;
	}
	
	public void createSoundController() {
		SocketGenerativeSound generativeSound = SocketServer.getInstance().getGenerativeSound();
		if (!generativeSound.isCancelled()) {
			this.sound = new SoundController(generativeSound);
		}
	}
	
	public void createStreamControllers() {
		for (SocketStream socketStream : SocketServer.getInstance().getStreams()) {
			if (!socketStream.isCancelled()) {
				StreamController stream = new StreamController(socketStream);
				this.streams.add(stream);
			}
		}
	}
	
	public Object createWaitObject() {
		Object waitObj = new Object();
		addWaitObject(waitObj);
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
	
	public void setFrameData(FrameData frameData, ScreenData screenData, AudioData audioData) {
		// Game Playing AI
		for (AIController ai : this.ais) {
			if (ai != null) {
				if (!frameData.getEmptyFlag()) {
					ai.setFrameData(new FrameData(frameData));
				} else {
					ai.setFrameData(new FrameData());
				}
				ai.setScreenData(new ScreenData(screenData));
				ai.setAudioData(new AudioData(audioData));
			}
		}

		// Sound Design AI
		if (this.sound != null) {
			this.sound.setFrameData(frameData);
		}
		
		for (StreamController stream : this.streams) {
			stream.setFrameData(frameData, audioData, screenData);
		}
	}
	
	public void sendRoundResult(RoundResult roundResult) {
		// Game Playing AI
		for (AIController ai : this.ais) {
			if (ai != null) {
				ai.informRoundResult(roundResult);
			}
		}
		
		// Sound Design AI
		if (this.sound != null) {
			this.sound.informRoundResult(roundResult);
		}
		
		for (StreamController stream : this.streams) {
			stream.informRoundResult(roundResult);
		}
	}
	
	public void gameEnd() {
		// Game Playing AI
		for (AIController ai : this.ais) {
			if (ai != null) {
				ai.gameEnd();
			}
		}
		
		// Sound Design AI
		if (this.sound != null) {
			this.sound.gameEnd();
		}
		
		for (StreamController stream : this.streams) {
			stream.gameEnd();
		}
		
		this.isFighting = false;
	}
	
	/**
	 * 各AIコントローラ内に保持されているフレームデータをクリアする.
	 */
	public void clear() {
		for (AIController ai : this.ais) {
			if (ai != null) {
				ai.clear();
			}
		}
		
		if (this.sound != null) {
			this.sound.clear();
		}
		
		for (StreamController stream : this.streams) {
			stream.clear();
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
	public void notifyEndAIProcess(boolean playerNumber) {
		if (playerNumber) {
			this.processedAI1 = true;
		} else {
			this.processedAI2 = true;
		}
	}
	
	public void notifyEndSoundProcess() {
		this.processedSound = true;
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
	
	private boolean isThreadWaiting() {
		boolean ans = true;
		for (int i = 0; i < 2; i++) {
			if (this.ais[i] != null) {
				ans = ans && this.ais[i].getState() == Thread.State.WAITING;
			}
		}
		
		if (this.sound != null) {
			ans = ans && this.sound.getState() == Thread.State.WAITING;
		}
		
		return ans;
	}
	
	public void initialize() {
		this.isFighting = true;
	}
	
	public void start() {
		new Thread(() -> {
			while (this.isFighting) {
				try {
	    			Thread.sleep(1);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
				
	            if (isAIProcessed() && isSoundProcessed() && isThreadWaiting()) {
	    			resetProcessedFlag();

	    			synchronized (this.endFrame) {
	    				this.endFrame.notifyAll();
	    			}
	    		}
			}
		}).start();
	}

}
