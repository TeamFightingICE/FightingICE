package manager;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetTime;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import service.SocketServer;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import util.FrameRateSync;
import util.WaveFileWriter;

/**
 * ゲームの進行管理を行うマネージャクラス．
 */
public class DisplayManager {

	/**
	 * GLFWで使用されるwindow作成用の変数．
	 */
	private long window;

	/**
	 * クラスコンストラクタ．
	 */
	public DisplayManager() {

	}

	/**
	 * ゲームをスタートさせる．<br>
	 * 1. OpenGL及びウィンドウの初期化を行う．<br>
	 * 2. ゲームの終了処理命令が来るまで，ゲーム状態の更新，描画処理などのメインループ処理を行う．<br>
	 * 3. ゲームの終了処理を行ってウィンドウを閉じる．<br>
	 *
	 * @param game
	 *            GameManagerクラスのインスタンス
	 * @see GameManager
	 */
	public void start(GameManager game) {
		// Window, OpenGLの初期化
		initialize();

		// メインループ
		gameLoop(game);

		// ゲームの終了処理
		close();
	}

	/**
	 * ウィンドウを作成する際の初期化及びOpenGLの初期化処理を行う．
	 */
	private void initialize() {
		if (LaunchSetting.isExpectedProcessingMode(LaunchSetting.STANDARD_MODE)) {
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
			String title = GameSetting.TITLE_NAME;
			this.window = glfwCreateWindow(width, height, title, NULL, NULL);
			if (this.window == NULL) {
				throw new RuntimeException("Failed to create the GLFW window");
			}

			// Setup a key callback. It will be called every time a key is pressed,
			// repeated or released.
			glfwSetKeyCallback(this.window, InputManager.getInstance().getKeyboard());

			// Gets the thread stack and push a new frame
			try (MemoryStack stack = stackPush()) {
				IntBuffer pWidth = stack.mallocInt(1); // int*
				IntBuffer pHeight = stack.mallocInt(1); // int*

				// Gets the window size passed to glfwCreateWindow
				glfwGetWindowSize(this.window, pWidth, pHeight);

				// Gets the resolution of the primary monitor
				GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

				// Center the window
				glfwSetWindowPos(this.window, (vidmode.width() - pWidth.get(0)) / 2,
						(vidmode.height() - pHeight.get(0)) / 2);
			} // the stack frame is popped automatically

			// Makes the OpenGL context current
			glfwMakeContextCurrent(this.window);

			// Makes the window visible
			glfwShowWindow(this.window);
			Logger.getAnonymousLogger().log(Level.INFO, "Create Window " + width + "x" + height);
			
			glfwSetTime(0.0);

			// This line is critical for LWJGL's interoperation with GLFW's
			// OpenGL context, or any context that is managed externally.
			// LWJGL detects the context that is current in the current thread,
			// creates the GLCapabilities instance and makes the OpenGL
			// bindings available for use.
			GL.createCapabilities();

			// Sets the clear color
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			initGL();
		}
	}
	
	private boolean shouldClose() {
		if (LaunchSetting.isExpectedProcessingMode(LaunchSetting.STANDARD_MODE)) {
			return glfwWindowShouldClose(this.window);
		} else {
			return false;
		}
	}

	/**
	 * ゲームのメインループの処理を行う．
	 *
	 * @param gm
	 *            GameManagerクラスのインスタンス
	 */
	private void gameLoop(GameManager gm) {
		// ゲームマネージャ初期化
		gm.initialize();
		
		// Runs the rendering loop until the user has attempted to close the window.
		while (!shouldClose()) {
			// ゲーム終了の場合,リソースを解放してループを抜ける
			if (gm.isExit()) {
				gm.close();
				break;
			}

			// ゲーム状態の更新
			gm.update();
			
			// バックバッファに描画する
			GraphicManager.getInstance().render();
			
			if (LaunchSetting.isExpectedProcessingMode(LaunchSetting.STANDARD_MODE)) {
				// バックバッファとフレームバッファを入れ替える
				glfwSwapBuffers(this.window);
			   	
				// Poll for window events. The key callback above will only be
				// invoked during this call.
				glfwPollEvents();
			}
			
			if (!FlagSetting.inputSyncFlag) {
				// Sync frame rate
				FrameRateSync.sync(GameSetting.FPS);
			}
		}
	}

	/**
	 * ゲームの終了処理を行い，ウィンドウを閉じる.
	 */
	private void close() {
		GraphicManager.getInstance().close();
		SoundManager.getInstance().close();
		WaveFileWriter.getInstance().close();
		
		try {
			SocketServer.getInstance().stopServer();
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.INFO, "Fail to stop socket server");
		}

		if (LaunchSetting.isExpectedProcessingMode(LaunchSetting.STANDARD_MODE)) {
			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(this.window);
			glfwDestroyWindow(this.window);

			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "Close FightingICE");
		System.exit(0);
	}

	/**
	 * OpenGLの初期化処理を行う．
	 */
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
