package manager;

import static org.lwjgl.glfw.GLFW.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import aiinterface.AIController;
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

	// static field
	/** Default number of devices **/
	private final static int DEFAULT_DEVICE_NUMBER = 2;

	/** Default device type is keyboard **/
	public final static char DEVICE_TYPE_KEYBOARD = 0;
	public final static char DEVICE_TYPE_AI = 1;

	// to recognize the input devices
	private char[] deviceTypes;

	private InputManager() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + InputManager.class.getName());

		keyboard = new Keyboard();
		deviceTypes = new char[DEFAULT_DEVICE_NUMBER];
		sceneName = GameSceneName.HOME_MENU;
		for (int i = 0; i < this.deviceTypes.length; i++) {
			this.deviceTypes[i] = DEVICE_TYPE_KEYBOARD;
		}
	}

	public static InputManager getInstance() {
		return InputManagerHolder.instance;
	}

	/** getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス */
	private static class InputManagerHolder {
		private static final InputManager instance = new InputManager();
	}

	public Keyboard getKeyboard() {
		return this.keyboard;
	}

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

	public void createAIcontroller() {
		String[] aiNames = LaunchSetting.aiNames.clone();

		if (FlagSetting.allCombinationFlag) {
			if(AIContainer.p1Index == AIContainer.p2Index){
				AIContainer.p1Index++;
			}
			aiNames[0] = AIContainer.allAINameList.get(AIContainer.p1Index);
			aiNames[1] = AIContainer.allAINameList.get(AIContainer.p2Index);
		}

		this.deviceTypes = LaunchSetting.deviceTypes.clone();
		this.ais = new AIController[aiNames.length];
		for (int i = 0; i < aiNames.length; i++) {
			if (deviceTypes[i] == DEVICE_TYPE_AI) {
				ais[i] = ResourceLoader.getInstance().loadAI(aiNames[i]);
			}
		}
	}

	public void startAI(GameData gameData) {
		int count = 0;
		for (int i = 0; i < this.deviceTypes.length; i++) {
			if (deviceTypes[i] == DEVICE_TYPE_AI) {
				// ais[count].initialize(waitFrame, gd,
				// !Transform.iTob(i));//Call the initialize function of the AI
				// of interest
				ais[count].initialize(ThreadController.getInstance().getAIsObject(i), gameData,
						Transform.convertPlayerNumberfromItoB(i));
				ais[count].start();// start the thread
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

	public void setFrameData(FrameData frameData, ScreenData screenData) {
		int count = 0;
		for (int i = 0; i < this.deviceTypes.length; i++) {
			if (deviceTypes[i] == DEVICE_TYPE_AI) {
				if (!frameData.getEmptyFlag()) {
					ais[count].setFrameData(new FrameData(frameData));
				} else {
					ais[count].setFrameData(new FrameData());
				}

				ais[count].setScreenData(new ScreenData(screenData));
				ThreadController.getInstance().resetFlag(i);
				count++;
			}
		}
	}

	public void sendRoundResult(RoundResult roundResult) {
		for (AIController ai : this.ais) {
			if (ai != null) {
				ai.informRoundResult(roundResult);
				ai.clear();
			}
		}
	}

	public KeyData getKeyData() {
		return this.buffer;
	}

	public void setKeyData(KeyData data) {
		this.buffer = data;
	}

	public GameSceneName getSceneName() {
		return this.sceneName;
	}

	public void setSceneName(GameSceneName sceneName) {
		this.sceneName = sceneName;
	}

}
