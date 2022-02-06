package manager;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import aiinterface.AIController;
import aiinterface.AIInterface;
import aiinterface.ThreadController;
import enumerate.GameSceneName;
import informationcontainer.AIContainer;
import informationcontainer.RoundResult;
import input.KeyData;
import input.Keyboard;
import loader.ResourceLoader;
import py4j.Py4JException;
import setting.FlagSetting;
import setting.LaunchSetting;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.ScreenData;

/**
 * AIやキーボード等の入力関連のタスクを管理するマネージャークラス．
 */
public class InputManager<Data> {

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

		keyboard = new Keyboard();
		deviceTypes = new char[DEFAULT_DEVICE_NUMBER];
		sceneName = GameSceneName.HOME_MENU;
		this.predifinedAIs = new HashMap<String, AIInterface>();

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

		if (playerNumber) {
			key.A = keyboard.getKeyDown(GLFW_KEY_Z);
			key.B = keyboard.getKeyDown(GLFW_KEY_X);
			key.C = keyboard.getKeyDown(GLFW_KEY_C);
			key.U = keyboard.getKeyDown(GLFW_KEY_UP);
			key.D = keyboard.getKeyDown(GLFW_KEY_DOWN);
			key.R = keyboard.getKeyDown(GLFW_KEY_RIGHT);
			key.L = keyboard.getKeyDown(GLFW_KEY_LEFT);
		} else {
			key.A = keyboard.getKeyDown(GLFW_KEY_T);
			key.B = keyboard.getKeyDown(GLFW_KEY_Y);
			key.C = keyboard.getKeyDown(GLFW_KEY_U);
			key.U = keyboard.getKeyDown(GLFW_KEY_I);
			key.D = keyboard.getKeyDown(GLFW_KEY_K);
			key.R = keyboard.getKeyDown(GLFW_KEY_L);
			key.L = keyboard.getKeyDown(GLFW_KEY_J);
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
		this.ais = new AIController[DEFAULT_DEVICE_NUMBER];
		for (int i = 0; i < this.deviceTypes.length; i++) {
			if (this.deviceTypes[i] == DEVICE_TYPE_AI) {
				if (this.predifinedAIs.containsKey(aiNames[i])) {
					this.ais[i] = new AIController(this.predifinedAIs.get(aiNames[i]));
				} else {
					this.ais[i] = ResourceLoader.getInstance().loadAI(aiNames[i]);
				}
			} else {
				this.ais[i] = null;
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
	public void startAI(GameData gameData) throws Py4JException{
		for (int i = 0; i < this.deviceTypes.length; i++) {
			if (this.ais[i] != null) {
				this.ais[i].initialize(ThreadController.getInstance().getAIsObject(i == 0), gameData, i == 0);
				this.ais[i].start();// start the thread
			}
		}
	}

	/**
	 * AIの動作を停止させる．
	 */
	public void closeAI() {
		this.buffer = new KeyData();

		for (AIController ai : this.ais) {
			if (ai != null)
				ai.gameEnd();
		}
		this.deviceTypes = new char[DEFAULT_DEVICE_NUMBER];
		this.ais = null;
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

	/**
	 * 引数のフレームデータ及びScreenDataを各AIコントローラにセットする．
	 *
	 * @param frameData
	 *            フレームデータ
	 * @param screenData
	 *            スクリーンデータ
	 *
	 * @see FrameData
	 * @see ScreenData
	 */
	public void setFrameData(FrameData frameData, ScreenData screenData) {
		for (int i = 0; i < this.ais.length; i++) {
			if (this.ais[i] != null) {
				if (!frameData.getEmptyFlag()) {
					this.ais[i].setFrameData(new FrameData(frameData));
				} else {
					this.ais[i].setFrameData(new FrameData());
				}
				this.ais[i].setScreenData(new ScreenData(screenData));
			}
		}

		synchronized (this.endFrame) {
			try {
				ThreadController.getInstance().resetAllAIsObj();
				if (FlagSetting.fastModeFlag) {
//					SoundManager.getInstance().pauseSound();
					this.endFrame.wait();
//					SoundManager.getInstance().resumeSound();
				} else {

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		for (AIController ai : this.ais) {
			if (ai != null) {
				ai.informRoundResult(roundResult);
			}
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
