package python;

import aiinterface.AIInterface;
import gamescene.Python;
import manager.InputManager;
import setting.LaunchSetting;

/**
 * PythonでFightingICEを操作し, ゲームの作成やリプレイのロードといった処理を管理するマネージャクラス.
 */
public class PyManager {

	/**
	 * Pythonで実行処理を行うゲームシーン.
	 */
	public static Python python;

	/**
	 * 引数で渡されたゲームシーンでPyManagerの初期化を行うクラスコンストラクタ.
	 *
	 * @param python
	 *            Python用のゲームシーン
	 */
	public PyManager(Python python) {
		PyManager.python = python;
	}

	/**
	 * Registers one python AI with a given name.<br>
	 * Should be called before createGame (and for each call to createGame)
	 *
	 * @param name
	 *            the given name of the AI
	 * @param ai
	 *            the instance of a class which inherits from the interface
	 *            "gameInterface.AIInterface"
	 */
	public void registerAI(String name, AIInterface ai) {
		InputManager.getInstance().registerAI(name, ai);
	}

	/**
	 * Creates one game.
	 *
	 * @param c1
	 *            the name of the character for the first AI (P1)
	 * @param c2
	 *            the name of the character for the second AI (P2)
	 * @param name1
	 *            the name of the first AI (could be the name of a previously
	 *            registered AI with registerAI or the name of a Java AI)
	 * @param name2
	 *            the name of the second AI (could be the name of a previously
	 *            registered AI with registerAI or the name of a Java AI)
	 * @param num
	 *            the number of repeat count of the game
	 * @return the created game
	 */
	public PyGame createGame(String c1, String c2, String name1, String name2, int num) {
		return new PyGame(this, c1, c2, name1, name2, num);
	}

	/**
	 * Runs the given game until the end of this one
	 *
	 * @param game
	 *            the given game to run
	 */
	public void runGame(PyGame game) {
		PyManager.python.runGame(game);

		/*
		 * TODO this synchronization is on a non-final field -
		 * http://stackoverflow.com/a/6910838/1326534 If someone calls changes
		 * Game.end, the synchronized sections could be accessed at the same
		 * time on the same instance. Unlikely, but better not to forget it.
		 */
		synchronized (game.end) {
			try {
				game.end.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 引数で指定されたリプレイファイル名をセットし, リプレイを再生するための処理を行うクラスのインスタンスを返す.
	 *
	 * @param fileName
	 *            読み込むファイル名
	 *
	 * @return リプレイを再生するための処理を行うクラスのインスタンス
	 */
	public PyReplay loadReplay(String fileName) {
		LaunchSetting.replayName = fileName;
		PyReplay pyReplay = new PyReplay();

		return pyReplay;

	}

}
