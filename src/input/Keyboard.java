package input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;

import org.lwjgl.glfw.GLFWKeyCallback;

import enumerate.GameSceneName;
import manager.InputManager;

public class Keyboard extends GLFWKeyCallback {
	public static boolean[] keys = new boolean[65536];

	private static boolean[] preKeys = new boolean[65536];

	public Keyboard() {
		Arrays.fill(keys, false);
		Arrays.fill(preKeys, false);
	}

	// The GLFWKeyCallback class is an abstract method that
	// can't be instantiated by itself and must instead be extended
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		keys[key] = action != GLFW_RELEASE;
		if (action == GLFW_RELEASE) {
			preKeys[key] = false;
		}
	}

	// boolean method that returns true if a given key is pressed.
	public static boolean getKey(int keycode) {
		if (keys[keycode])
			;// System.out.println("pless");
		return keys[keycode];
	}

	// boolean method that returns true if キーを押したとき.
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
