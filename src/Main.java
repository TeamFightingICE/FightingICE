import core.Game;
import manager.DisplayManager;

/** FightingICEのメインクラス */
public class Main {

	/**
	 * FightingICEのメインメソッド<br>
	 * 起動時の引数に応じて起動情報を設定し, それを基にゲームを開始する.
	 *
	 * @param options
	 *            起動時のすべての引数を格納した配列
	 */
	public static void main(String[] options) {
		Game game = new Game();
		game.setOptions(options);
		DisplayManager displayManager = new DisplayManager();

		// ゲームの開始
		displayManager.start(game);
	}
}
