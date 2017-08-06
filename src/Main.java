import java.util.Arrays;

import core.Game;
import manager.DisplayManager;

public class Main {

	/**
	 *
	 * 使用ライブラリ
	 * LWJGL 3.1.2
	 * */
	public static void main(String[] options) {

		Game game = new Game();
		DisplayManager displayManager = new DisplayManager();

		if(Arrays.asList(options).contains("--disable-window")){
			displayManager.disableWindow();
		}

		displayManager.start(game);


	}

}
