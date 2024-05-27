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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import aiinterface.AIController;
import aiinterface.AIInterface;
import aiinterface.SoundController;
import aiinterface.ThreadController;
import enumerate.GameSceneName;
import informationcontainer.AIContainer;
import input.KeyData;
import input.Keyboard;
import loader.ResourceLoader;
import service.SocketServer;
import setting.FlagSetting;
import setting.LaunchSetting;
import struct.AudioData;
import struct.Key;

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
	 * ゲームのシーン名．
	 */
	private GameSceneName sceneName;

	/**
	 * Default number of devices.
	 */
	public final static int DEFAULT_DEVICE_NUMBER = 2;

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

		for (int i = 0; i < this.deviceTypes.length; i++) {
			this.deviceTypes[i] = DEVICE_TYPE_KEYBOARD;
		}
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
	
	public void initialize() {
		this.deviceTypes = LaunchSetting.deviceTypes.clone();
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
				AIController ai = ThreadController.getInstance().getAIController(i == 0);
				keys[i] = getKeyFromAI(ai);
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
	 * AIの動作を停止させる．
	 */
	public void close() {
		this.buffer = new KeyData();
		
		this.deviceTypes = new char[DEFAULT_DEVICE_NUMBER];
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
		SoundController sound = ThreadController.getInstance().getSoundController();
		if (sound == null)
			return new AudioData();
		return new AudioData(sound.getAudioData());
	}
	
	public void setInput(boolean playerNumber, Key input) {
		AIController ai = ThreadController.getInstance().getAIController(playerNumber);
		if (ai != null) {
			ai.setInput(input);
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
