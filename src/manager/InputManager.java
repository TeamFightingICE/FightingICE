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
import setting.FlagSetting;
import setting.LaunchSetting;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.ScreenData;
import util.Transform;

/** AIやキーボード等の入力関連のタスクを管理するマネージャー */
public class InputManager<Data> {

	private KeyData buffer;

	/* KeyCallBackクラス */
	private Keyboard keyboard;

	private AIController[] ais;

	private GameSceneName sceneName;

	private HashMap<String, AIInterface> predifinedAIs;

	// static field
	/** Default number of devices **/
	private final static int DEFAULT_DEVICE_NUMBER = 2;

	/** デバイスタイプでキーボードを指定する場合の定数 */
	public final static char DEVICE_TYPE_KEYBOARD = 0;

	/** デバイスタイプでAIを指定する場合の定数 */
	public final static char DEVICE_TYPE_AI = 1;

	// to recognize the input devices
	private char[] deviceTypes;

	/**
	 * InputManagerクラスのコンストラクタ．<br>
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
	}

	/**
	 * InputManagerクラスの唯一のインスタンスを取得するgetterメソッド．
	 *
	 * @return InputManagerクラスの唯一のインスタンス
	 */
	public static InputManager getInstance() {
		return InputManagerHolder.instance;
	}

	/** getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス */
	private static class InputManagerHolder {
		private static final InputManager instance = new InputManager();
	}

	/** Keyboardクラスのインスタンスを取得するgetterメソッド． */
	public Keyboard getKeyboard() {
		return this.keyboard;
	}

	public void registerAI(String name, AIInterface ai) {
		this.predifinedAIs.put(name, ai);
	}

	/** 毎フレーム実行され，キーボード入力及びAIの入力を受け付けるメソッド． */
	public void update() {
		int aiCount = 0;

		Key[] keys = new Key[this.deviceTypes.length];
		for (int i = 0; i < this.deviceTypes.length; i++) {
			switch (this.deviceTypes[i]) {
			case DEVICE_TYPE_KEYBOARD:
				keys[i] = getKeyFromKeyboard(i == 0);
				break;
			case DEVICE_TYPE_AI:
				keys[i] = getKeyFromAI(ais[aiCount]);
				aiCount++;
				break;
			default:
				break;
			}
		}

		this.setKeyData(new KeyData(keys));
	}

	/**
	 * キーボードで押されたキーを取得するgetterメソッド． 引数でプレイヤーがP1(true)かP2(false)かを指定する．
	 *
	 * @param playerNumber
	 *            trueはP1，falseはP2
	 * @return 押されたキー
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

	/** AIの情報を格納したコントローラをInputManagerクラスに取り込むメソッド． */
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
		this.ais = new AIController[aiNames.length];
		for (int i = 0; i < aiNames.length; i++) {
			if (deviceTypes[i] == DEVICE_TYPE_AI) {
				if (this.predifinedAIs.containsKey(aiNames[i])) {
					this.ais[i] = new AIController(this.predifinedAIs.get(aiNames[i]));
				} else {
					this.ais[i] = ResourceLoader.getInstance().loadAI(aiNames[i]);
				}

			}
		}
	}

	/**
	 * AIコントローラの動作を開始させるメソッド．<br>
	 * 引数のGameDataクラスのインスタンスを用いてAIコントローラを初期化し，動作を開始する．
	 *
	 * @param gameData
	 *            GameDataクラスのインスタンス
	 */
	public void startAI(GameData gameData) {
		int count = 0;
		for (int i = 0; i < this.deviceTypes.length; i++) {
			if (deviceTypes[i] == DEVICE_TYPE_AI) {
				// ais[count].initialize(waitFrame, gd,
				// !Transform.iTob(i));//Call the initialize function of the AI
				// of interest
				this.ais[count].initialize(ThreadController.getInstance().getAIsObject(i), gameData,
						Transform.convertPlayerNumberfromItoB(i));
				this.ais[count].start();// start the thread
				count++;
			}
		}
	}

	// private synchronized Key getInputFromAI(AIController ai){
	private Key getKeyFromAI(AIController ai) {
		if (ai == null)
			return new Key();
		return new Key(ai.getInput());
	}

	/**
	 * 引数のフレームデータをAIコントローラにセットするsetterメソッド．
	 *
	 * @param frameData
	 *            フレームデータ
	 */
	public void setFrameData(FrameData frameData, ScreenData screenData) {
		int count = 0;
		for (int i = 0; i < this.deviceTypes.length; i++) {
			if (deviceTypes[i] == DEVICE_TYPE_AI) {
				if (!frameData.getEmptyFlag()) {
					this.ais[count].setFrameData(new FrameData(frameData));
				} else {
					this.ais[count].setFrameData(new FrameData());
				}

				ais[count].setScreenData(new ScreenData(screenData));
				ThreadController.getInstance().resetFlag(i);
				count++;
			}
		}
	}

	/**
	 * AIコントローラにラウンド結果を送信するメソッド．
	 *
	 * @param roundResult
	 *            ラウンド結果
	 */
	public void sendRoundResult(RoundResult roundResult) {
		for (AIController ai : this.ais) {
			if (ai != null) {
				ai.informRoundResult(roundResult);
				ai.clear();
			}
		}
	}

	/** 各AIコントローラ内に保持されているフレームデータをクリアする. */
	public void clear() {
		for (AIController ai : this.ais) {
			if (ai != null) {
				ai.clear();
			}
		}
	}

	/**
	 * 入力されたキーを格納しているキーバッファを取得するgetterメソッド．
	 *
	 * @return キーバッファ
	 */

	public KeyData getKeyData() {
		return this.buffer;
	}

	/**
	 * 入力されたキーをバッファにセットするsetterメソッド．
	 *
	 * @param data
	 *            入力キーデータ
	 */
	public void setKeyData(KeyData data) {
		this.buffer = data;
	}

	/**
	 * シーン名を取得するgetterメソッド．
	 *
	 * @return 現在のシーン名
	 */
	public GameSceneName getSceneName() {
		return this.sceneName;
	}

	/** 引数のシーン名をフィールド変数にセットするsetterメソッド． */
	public void setSceneName(GameSceneName sceneName) {
		this.sceneName = sceneName;
	}

}
