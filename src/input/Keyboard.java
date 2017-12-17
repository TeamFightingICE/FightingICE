package input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;

import org.lwjgl.glfw.GLFWKeyCallback;

import enumerate.GameSceneName;
import manager.InputManager;

/**
 * キー入力を扱うクラス．
 */
public class Keyboard extends GLFWKeyCallback {

	/**
	 * 各キーが押されたかどうかのboolean valueを格納する配列．
	 */
	public static boolean[] keys = new boolean[65536];

	/**
	 * 前のステップで各キーが押されていたかどうかのboolean valueを格納する配列．
	 */
	private static boolean[] preKeys = new boolean[65536];

	/**
	 * クラスコンストラクタ．
	 */
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
	 * 指定されたキーが入力されているかどうかを返す．
	 *
	 * @param keycode
	 *            指定するキー
	 * @return {@code true} 指定されたキーが入力されたとき，{@code false} otherwise
	 */
	public static boolean getKey(int keycode) {
		return keys[keycode];
	}

	/**
	 * 指定されたキーが押されているかどうかを返す．
	 *
	 * @param keycode
	 *            指定するキー
	 * @return ゲームシーン内
	 *         <p>
	 *         {@code true} 指定されたキーが入力されているとき，<br>
	 *         {@code false} 指定されたキーが入力されていないとき．
	 *         <p>
	 *         ゲームシーン以外
	 *         <p>
	 *         {@code true} 指定されたキーが入力されているとき，<br>
	 *         {@code false} 指定されたキーが入力されていない,または前ステップで入力されていたとき．
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

	/**
	 * クローズ処理．
	 */
	public void close() {

	}
}
