package manager;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import setting.GameSetting;

public class DisplayManager {

	/** ウィンドウの表示が有効かどうか */
	private boolean enableWindow;

	/** GLFWで使用されるwindow作成用の変数 */
	private long window;

	public DisplayManager() {
		enableWindow = true;
	}

	/**
	 * ゲームをスタートさせる 1. OpenGL及びwindowの初期化 2. ゲームのメインループ 3. windowをクローズする
	 */
	public void start(GameManager game) {
		if (enableWindow) {
			// Window, OpenGLの初期化
			initialize();
		}

		// メインループ
		gameLoop(game);

		// ゲームの終了処理
		close();

	}

	/** ウィンドウを作成する際の初期化処理を行う */
	private void initialize() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// GLFWの設定
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		System.setProperty("java.awt.headless", "true");

		// windowの作成
		short width = GameSetting.STAGE_WIDTH;
		short height = GameSetting.STAGE_HEIGHT;
		this.window = glfwCreateWindow(width, height, "FightingICE", NULL, NULL);
		if (this.window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		// glfwSetKeyCallback(this.window, (window, key, scancode, action, mods)
		// -> {
		// if (windowCloseRequest(key, action)) {
		// glfwSetWindowShouldClose(window, true); // We will detect this
		// // in the rendering loop
		// }
		// });
		glfwSetKeyCallback(this.window, InputManager.getInstance().getKeyboard());

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(this.window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(this.window, (vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(this.window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(this.window);

		Logger.getAnonymousLogger().log(Level.INFO, "Create Window " + width + "x" + height);
	}

	/**
	 * ゲームのメインループの処理を行う
	 *
	 * @param gm
	 *            ゲームマネージャー
	 */
	private void gameLoop(GameManager gm) {
		glfwSetTime(0.0);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		initGL();

		// ゲームマネージャ初期化
		gm.initialize();

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the DELETE key.
		while (!glfwWindowShouldClose(this.window)) {
			// ゲーム終了の場合,リソースを解放してループを抜ける
			if (gm.isExit()) {
				gm.close();
				break;
			}

			// ゲーム状態の更新
			gm.update();

			if (this.enableWindow) {
				// バックバッファに描画する
				GraphicManager.getInstance().render();

				glfwSwapBuffers(this.window); // バックバッファとフレームバッファを入れ替える

				// Poll for window events. The key callback above will only be
				// invoked during this call.
				glfwPollEvents();
			}
		}
	}

	/** ゲームの終了処理を行い,ウィンドウを閉じる. */
	private void close() {
		GraphicManager.getInstance().close();
		SoundManager.getInstance().close();
		// InputManager.getInstance().close();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(this.window);
		glfwDestroyWindow(this.window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		Logger.getAnonymousLogger().log(Level.INFO, "Close FightingICE");
	}

	public void disableWindow() {
		this.enableWindow = false;
	}

	/**
	 * ウィンドウを閉じる要求を出す.<br>
	 * return文内に指定されたキーと操作の組み合わせが満たされたとき,windowを閉じる要求を出す.
	 *
	 * @param key
	 *            指定キー
	 * @param action
	 *            keyに対する操作 (e.g. Press, Release)
	 * @return windowを閉じる要求が出されたかどうか
	 */
	private boolean windowCloseRequest(int key, int action) {
		return key == GLFW_KEY_DELETE && action == GLFW_RELEASE;
	}

	private void initGL() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		glMatrixMode(GL_MODELVIEW);
		glOrtho(0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, 0, 1, -1);

		// Enable Blending for transparent textures
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
}
