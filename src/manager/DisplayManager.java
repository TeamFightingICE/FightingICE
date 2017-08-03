package manager;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class DisplayManager {

	private boolean enableWindow;
	private long window;

	public DisplayManager() {
		enableWindow = true;
	}

	/**
	 * ゲームをスタートさせる 1. OpenGL及びwindowの初期化 2. ゲームのメインループ 3. windowをクローズする
	 */
	public void start(GameManager game, short width, short height, byte fps) {

		if (enableWindow) {
			// Window, OpenGLの初期化
			initialize(width, height);
		}

		// メインループ
		gameLoop(game, fps);

		// ゲームの終了処理
		close();

	}

	private void initialize(short width, short height) {
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

		// windowの作成
		this.window = glfwCreateWindow(width, height, "FightingICE", NULL, NULL);
		if (this.window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		glfwSetKeyCallback(this.window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true); // We will detect this
														// in the rendering loop
			}
		});

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
	}

	/**
	 * ゲームのメインループの処理を行う
	 */
	private void gameLoop(GameManager gm, byte fps) {

		double currentTime = 0;
		double lastTime = 0;
		double elapsedTime = 0;
		glfwSetTime(0.0);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

		// 初期化
		gm.initialize();

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(this.window)) {

			// ゲーム終了の場合,リソースを解放してループを抜ける
			/*
			 * if(gm.isExit){
			 * gm.close();
			 * break; }
			 */
			// ゲーム状態の更新
			gm.update();

			if (this.enableWindow) {
				currentTime = glfwGetTime();
				elapsedTime = currentTime - lastTime;

				// FPSに従って描画
				if (elapsedTime >= 1.0 / fps) {
					glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

					// バックバッファに描画する
					// gm.render();

					glfwSwapBuffers(this.window); // バックバッファとフレームバッファを入れ替える
					lastTime = glfwGetTime();
				}

				// Poll for window events. The key callback above will only be
				// invoked during this call.
				glfwPollEvents();
			}
		}
	}

	private void close() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(this.window);
		glfwDestroyWindow(this.window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public void disableWindow() {
		this.enableWindow = false;
	}

}
