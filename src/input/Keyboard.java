package input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;

import org.lwjgl.glfw.GLFWKeyCallback;

import enumerate.GameSceneName;
import manager.InputManager;

/**
 * キー入力を扱うクラス
 */
public class Keyboard extends GLFWKeyCallback {

	/** 各キーが押されたかを格納 */
	public static boolean[] keys = new boolean[65536];

	/** 前のステップで各キーが押されていたかを格納 */
	private static boolean[] preKeys = new boolean[65536];


	public Keyboard() {
		Arrays.fill(keys, false);
		Arrays.fill(preKeys, false);
	}

	// The GLFWKeyCallback class is an abstract method that
	// can't be instantiated by itself and must instead be extended
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key >= 0 && key <= 65536) {
			keys[key] = action != GLFW_RELEASE;
			if (action == GLFW_RELEASE) {
				preKeys[key] = false;
			}
		}
	}



	/**
	 * 指定されたキーが押されている間に使われるメソッド
	 * @param keycode 指定されたキーコード
	 * @return {@code true} 指定されたキーコードが入力されたとき
	 */
	public static boolean getKey(int keycode) {
		return keys[keycode];
	}

	/**
	 * 指定されたキーが押されたときに使うメソッド
	 * @param keycode 指定されたキーコード
	 * @return ゲームシーン時<p>
	 * 			{@code true} 指定されたキーが存在するとき<p>
	 * 			{@code false} 指定されたキーが存在しないとき<p>
	 * 			ゲームシーン以外<p>
	 * 			{@code true} 指定されたキーが存在するとき<p>
	 * 			{@code false} 指定されたキーが存在しない,または指定されたキーが押されているとき
	 */
	public static boolean getKeyDown(int keycode) {
		if (InputManager.getInstance().getSceneName() == GameSceneName.PLAY) {
			if (!keys[keycode]) {
				return false;
			} else {
				preKeys[keycode] = true;
				return true;
			}
		} else {
			if (!keys[keycode] || preKeys[keycode]) {
				return false;
			} else {
				preKeys[keycode] = true;
				return true;
			}
		}

	}

	public void close() {

	}
}
