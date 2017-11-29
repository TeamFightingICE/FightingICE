package manager;

import static org.lwjgl.glfw.GLFW.*;

import aiinterface.AIController;
import enumerate.GameSceneName;
import input.KeyData;
import input.Keyboard;
import struct.Key;

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
	public final static char DEVICE_TYPE_CONTROLLER = 1;
	public final static char DEVICE_TYPE_AI = 2;

	// to recognize the input devices
	private char[] deviceTypes;

	private InputManager() {
		System.out.println("Create instance: " + InputManager.class.getName());
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
		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();

		Key[] keys = new Key[this.deviceTypes.length];
		for (int i = 0; i < this.deviceTypes.length; i++) {
			switch (this.deviceTypes[i]) {
			case DEVICE_TYPE_KEYBOARD:
				keys[i] = getKeyFromKeyboard(i == 0);
				break;
			case DEVICE_TYPE_AI:
				// keys[i] = getKeyFromAI();
				break;
			default:
				break;
			}
		}

		// keys[0] = getKeyFromKeyboard(true);
		// keys[1] = getKeyFromKeyboard(false);

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

	// private synchronized Key getInputFromAI(AIController ai){
	private Key getInputFromAI(AIController ai) {
		if (ai == null)
			return new Key();
		return new Key(ai.getInput());
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
