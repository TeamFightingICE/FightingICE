package gamescene;

import enumerate.GameSceneName;
import manager.GraphicManager;
import python.PyGame;
import python.PyGatewayServer;
import python.StateInhibitor;
import setting.LaunchSetting;

/**
 * Python側で起動したゲームの実行処理を行うクラス．
 */
public class Python extends GameScene {

	/**
	 * リプレイ再生時の処理を管理するインタフェース．
	 */
	private StateInhibitor stateInhibitor;

	/**
	 * 起動したゲームを実行するかどうかを表すフラグ．
	 */
	private boolean needRun = false;

	/**
	 * 現在のゲーム情報．
	 *
	 * @see PyGame
	 */
	private PyGame currentGame;

	/**
	 * クラスコンストラクタ．
	 */
	public Python() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.PYTHON;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize() {
		this.stateInhibitor = null;
		this.needRun = false;

		// ゲートウェイを開く
		LaunchSetting.pyGatewayServer = new PyGatewayServer(this);
	}

	@Override
	public void update() {
		if (this.stateInhibitor != null) {
			this.stateInhibitor.replayUpdate();
			return;
		}

		GraphicManager.getInstance().drawString("Waiting python to launch a game", 300, 200);

		if (this.needRun) {
			this.needRun = false;

			Launcher launcher = new Launcher(GameSceneName.PLAY);
			this.setTransitionFlag(true);
			this.setNextGameScene(launcher);

		} else if (this.currentGame != null) {
			synchronized (this.currentGame.end) {
				this.currentGame.end.notifyAll();
			}
		}
	}

	/**
	 * Pythonでの処理のために作成したゲーム情報をセットし, ゲームを実行させるフラグをtrueにする.
	 */
	public void runGame(PyGame game) {
		this.currentGame = game;
		this.needRun = true;
	}

	/**
	 * 現在のゲーム情報を取得する.
	 *
	 * @return 現在のゲーム情報
	 */
	public PyGame getCurrentGame() {
		return this.currentGame;
	}

	/**
	 * リプレイ再生を管理するインタフェースをセットする．
	 *
	 * @param stateInhibitor
	 *            リプレイ再生を管理するインタフェース
	 */
	public void setStateInhibitor(StateInhibitor stateInhibitor) {
		this.stateInhibitor = stateInhibitor;
	}

}
