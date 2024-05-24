package manager;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import aiinterface.AIController;
import aiinterface.AIInterface;
import aiinterface.SoundController;
import aiinterface.StreamController;
import aiinterface.ThreadController;
import enumerate.GameSceneName;
import informationcontainer.AIContainer;
import informationcontainer.RoundResult;
import input.KeyData;
import input.Keyboard;
import loader.ResourceLoader;
import service.SocketGenerativeSound;
import service.SocketServer;
import service.SocketStream;
import setting.FlagSetting;
import setting.LaunchSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.ScreenData;

/**
 * AIやキーボード等の入力関連のタスクを管理するマネージャークラス．
 */
public class InputManager {

	/**
	 * キー入力を格納するバッファ．
	 */
	private KeyData buffer;

	/**
	 * Keyboardクラスのインスタンス．
	 */
	private Keyboard keyboard;

	/**
	 * AIコントローラを格納する配列．
	 */
	private AIController[] ais;
	
	private SoundController sound;
	
	private List<StreamController> streams;

	/**
	 * ゲームのシーン名．
	 */
	private GameSceneName sceneName;

	/**
	 * Python側で定義されたAI名とAIInterfaceをセットで管理するマップ.
	 */
	private HashMap<String, AIInterface> predifinedAIs;

	/**
	 * Default number of devices.
	 */
	private final static int DEFAULT_DEVICE_NUMBER = 2;

	/**
	 * デバイスタイプとしてキーボードを指定する場合の定数．
	 */
	public final static char DEVICE_TYPE_KEYBOARD = 0;

	/**
	 * デバイスタイプとしてAIを指定する場合の定数．
	 */
	public final static char DEVICE_TYPE_AI = 1;
	
	public final static char DEVICE_TYPE_EXTERNAL = 2;

	/**
	 * 入力デバイスを指定する配列．
	 */
	private char[] deviceTypes;

	/**
	 * 1フレーム分のゲームの処理が終わったことを示すオブジェクト．
	 */
	private Object endFrame;

	/**
	 * InputManagerクラスのクラスコンストラクタ．<br>
	 * デバイスタイプはデフォルトでキーボードを指定する．
	 */
	private InputManager() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + InputManager.class.getName());

		if (LaunchSetting.isExpectedProcessingMode(LaunchSetting.STANDARD_MODE)) {
			keyboard = new Keyboard();
		}
		
		deviceTypes = new char[DEFAULT_DEVICE_NUMBER];
		sceneName = GameSceneName.HOME_MENU;
		this.predifinedAIs = new HashMap<String, AIInterface>();
		this.ais = new AIController[DEFAULT_DEVICE_NUMBER];
		this.streams = new ArrayList<>();

		for (int i = 0; i < this.deviceTypes.length; i++) {
			this.deviceTypes[i] = DEVICE_TYPE_KEYBOARD;
		}

		this.endFrame = ThreadController.getInstance().getEndFrame();
	}

	/**
	 * InputManagerクラスの唯一のインスタンスを取得する．
	 *
	 * @return InputManagerクラスの唯一のインスタンス
	 */
	public static InputManager getInstance() {
		return InputManagerHolder.instance;
	}

	/**
	 * getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス．
	 */
	private static class InputManagerHolder {
		private static final InputManager instance = new InputManager();
	}

	/**
	 * InputManagerが持つKeyboardクラスのインスタンスを取得する．
	 *
	 * @return Keyboardクラスのインスタンス
	 */
	public Keyboard getKeyboard() {
		return this.keyboard;
	}

	/**
	 * Pythonでの処理のために用意されたAI名とAIインタフェースをマップに追加する．
	 *
	 * @param name
	 *            AI名
	 * @param ai
	 *            AIインタフェース
	 */
	public void registerAI(String name, AIInterface ai) {
		this.predifinedAIs.put(name, ai);
	}

	/**
	 * 毎フレーム実行され，キーボード入力及びAIの入力情報を取得する．
	 */
	public void update() {
		Key[] keys = new Key[this.deviceTypes.length];
		for (int i = 0; i < this.deviceTypes.length; i++) {
			switch (this.deviceTypes[i]) {
			case DEVICE_TYPE_KEYBOARD:
				keys[i] = getKeyFromKeyboard(i == 0);
				break;
			case DEVICE_TYPE_AI:
			case DEVICE_TYPE_EXTERNAL:
				keys[i] = getKeyFromAI(this.ais[i]);
				break;
			default:
				break;
			}
		}

		this.setKeyData(new KeyData(keys));
	}

	/**
	 * キーボードで押されたキーを取得する．<br>
	 * 引数でプレイヤーがP1(true)かP2(false)かを指定する．
	 *
	 * @param playerNumber
	 *            プレイヤー番号
	 * @return 押されたキーの情報
	 * @see Key
	 */
	private Key getKeyFromKeyboard(boolean playerNumber) {
		Key key = new Key();
		
		if (LaunchSetting.isExpectedProcessingMode(LaunchSetting.STANDARD_MODE)) {
			if (playerNumber) {
				key.A = Keyboard.getKeyDown(GLFW_KEY_Z);
				key.B = Keyboard.getKeyDown(GLFW_KEY_X);
				key.C = Keyboard.getKeyDown(GLFW_KEY_C);
				key.U = Keyboard.getKeyDown(GLFW_KEY_UP);
				key.D = Keyboard.getKeyDown(GLFW_KEY_DOWN);
				key.R = Keyboard.getKeyDown(GLFW_KEY_RIGHT);
				key.L = Keyboard.getKeyDown(GLFW_KEY_LEFT);
			} else {
				key.A = Keyboard.getKeyDown(GLFW_KEY_T);
				key.B = Keyboard.getKeyDown(GLFW_KEY_Y);
				key.C = Keyboard.getKeyDown(GLFW_KEY_U);
				key.U = Keyboard.getKeyDown(GLFW_KEY_I);
				key.D = Keyboard.getKeyDown(GLFW_KEY_K);
				key.R = Keyboard.getKeyDown(GLFW_KEY_L);
				key.L = Keyboard.getKeyDown(GLFW_KEY_J);
			}
		}

		return key;
	}

	/**
	 * AIの情報を格納したコントローラをInputManagerクラスに取り込む．
	 */
	public void createAIcontroller() {
		String[] aiNames = LaunchSetting.aiNames.clone();

		if (FlagSetting.allCombinationFlag) {
			if (AIContainer.p1Index == AIContainer.p2Index) {
				AIContainer.p1Index++;
			}
			aiNames[0] = AIContainer.allAINameList.get(AIContainer.p1Index);
			aiNames[1] = AIContainer.allAINameList.get(AIContainer.p2Index);
		}

		this.deviceTypes = LaunchSetting.deviceTypes.clone();
		for (int i = 0; i < this.deviceTypes.length; i++) {
			if (this.deviceTypes[i] == DEVICE_TYPE_AI) {
				if (this.predifinedAIs.containsKey(aiNames[i])) {
					this.ais[i] = new AIController(this.predifinedAIs.get(aiNames[i]));
				} else {
					this.ais[i] = ResourceLoader.getInstance().loadAI(aiNames[i]);
				}
			} else if (this.deviceTypes[i] == DEVICE_TYPE_EXTERNAL) {
				this.ais[i] = new AIController(SocketServer.getInstance().getPlayer(i));
			} else {
				this.ais[i] = null;
			}
		}
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

	/**
	 * AIコントローラの動作を開始させる．<br>
	 * 引数のGameDataクラスのインスタンスを用いてAIコントローラを初期化し，AIの動作を開始する．
	 *
	 * @param gameData
	 *            GameDataクラスのインスタンス
	 * @see GameData
	 */
	public void startAI(GameData gameData) {
		for (int i = 0; i < this.deviceTypes.length; i++) {
			if (this.ais[i] != null) {
				this.ais[i].initialize(ThreadController.getInstance().getAIsObject(i == 0), gameData, i == 0);
				this.ais[i].start();// start the thread
		        Logger.getAnonymousLogger().log(Level.INFO, String.format("Start P%s AI controller thread", i == 0 ? "1" : "2"));
			}
		}
	}
	
	public void startSound(GameData gameData) {
        if (this.sound != null) {
            this.sound.initialize(ThreadController.getInstance().getSoundObject(), gameData);
            this.sound.start();
        	Logger.getAnonymousLogger().log(Level.INFO, "Start Sound controller thread");
        }
	}
	
	public void startStreams(GameData gameData) {
		for (int i = 0; i < this.streams.size(); i++) {
			StreamController stream = this.streams.get(i);
			Object waitObj = new Object();
			ThreadController.getInstance().addWaitObject(waitObj);
			stream.initialize(waitObj, gameData);
            stream.start();
        	Logger.getAnonymousLogger().log(Level.INFO, String.format("Start Stream controller thread #%d", i + 1));
		}
	}

	/**
	 * AIの動作を停止させる．
	 */
	public void closeAI() {
		this.buffer = new KeyData();
		
		this.deviceTypes = new char[DEFAULT_DEVICE_NUMBER];
		this.ais = new AIController[DEFAULT_DEVICE_NUMBER];
		this.sound = null;
		this.streams.clear();
	}

	/**
	 * AIのキー入力を取得する．
	 *
	 * @param ai
	 *            AIの情報を格納したコントローラ
	 *
	 * @return AIのキー入力．
	 * @see AIController
	 * @see Key
	 */
	private Key getKeyFromAI(AIController ai) {
		if (ai == null)
			return new Key();
		return new Key(ai.getInput());
	}
	
	public AudioData getAudioData() {
		if (sound == null)
			return new AudioData();
		return new AudioData(sound.getAudioData());
	}
	
	public void resetAllObjects() {
		ThreadController.getInstance().resetAllObjects();
		
		if (FlagSetting.inputSyncFlag) {
			synchronized (this.endFrame) {
				try {
					this.endFrame.wait();
					
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 引数のフレームデータ及びScreenDataを各AIコントローラにセットする．
	 *
	 * @param frameData
	 *            フレームデータ
	 * @param screenData
	 *            スクリーンデータ
	 * @param audioData
	 *
	 * @see FrameData
	 * @see ScreenData
	 * @see AudioData
	 */
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
		
		this.resetAllObjects();
	}
	
	public void setInput(boolean playerNumber, Key input) {
		AIController ai = this.ais[playerNumber ? 0 : 1];
		if (ai != null) {
			ai.setInput(input);
		}
	}

	/**
	 * AIコントローラに現在のラウンドの結果を送信する．
	 *
	 * @param roundResult
	 *            現在のラウンドの結果
	 * @see RoundResult
	 */
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
		
		this.resetAllObjects();
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
	 * 入力されたキーを格納しているキーバッファを取得する．
	 *
	 * @return キーバッファ
	 */
	public KeyData getKeyData() {
		return this.buffer;
	}

	/**
	 * 入力されたキーをバッファにセットする．
	 *
	 * @param data
	 *            入力キーデータ
	 */
	public void setKeyData(KeyData data) {
		this.buffer = data;
	}

	/**
	 * シーン名を取得する．
	 *
	 * @return 現在のシーン名
	 */
	public GameSceneName getSceneName() {
		return this.sceneName;
	}

	/**
	 * 引数のシーン名をフィールド変数にセットする．
	 *
	 * @param sceneName
	 *            シーン名
	 */
	public void setSceneName(GameSceneName sceneName) {
		this.sceneName = sceneName;
	}

}
