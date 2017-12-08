package gamescene;

import enumerate.GameSceneName;
import manager.GraphicManager;
import python.PyGame;
import python.PyGatewayServer;
import python.StateInhibitor;
import setting.LaunchSetting;

public class Python extends GameScene {

	private StateInhibitor stateInhibitor;
	boolean needRun = false;

	private PyGame currentGame;

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

		LaunchSetting.pyGatewayServer = new PyGatewayServer(this);
	}

	@Override
	public void update() {
		if (this.stateInhibitor != null) {
			this.stateInhibitor.replayUpdate();
			return;
		}

		GraphicManager.getInstance().drawString("Waiting python to launch a game", 300, 200);

		if (needRun) {
			needRun = false;

			Launcher launcher = new Launcher(GameSceneName.PLAY);
			this.setTransitionFlag(true);
			this.setNextGameScene(launcher);

		} else if (currentGame != null) {
			synchronized (currentGame.end) {
				currentGame.end.notifyAll();
			}
		}
	}

	public void runGame(PyGame game) {
		currentGame = game;
		needRun = true;
	}

	public PyGame getCurrentGame() {
		return this.currentGame;
	}

	public void setStateInhibitor(StateInhibitor stateInhibitor) {
		this.stateInhibitor = stateInhibitor;
	}

}
