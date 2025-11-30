import core.Game;
import manager.DisplayManager;
import twitch.socket;

/**
 * FightingICEのメインメソッドを持つクラス．
 */
public class Main {

	/**
	 * 起動時に入力した引数に応じて起動情報を設定し, それを基にゲームを開始する．<br>
	 * このメソッドはFightingICEのメインメソッドである．
	 *
	 * @param options
	 *            起動時に入力した全ての引数を格納した配列
	 */
	public static void main(String[] options) {
		Game game = new Game();
		game.setOptions(options);
		DisplayManager displayManager = new DisplayManager();
        new socket();

		// ゲームの開始
		displayManager.start(game);
	}
}
