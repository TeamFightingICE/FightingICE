package gamescene;

import java.util.logging.Level;
import java.util.logging.Logger;

import enumerate.GameSceneName;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import setting.GameSetting;

public class Launcher extends GameScene {

	private GameSceneName nextGameSceneName;

	private int count;

	public Launcher() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.LAUNCH;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////

		this.nextGameSceneName = null;

	}

	public Launcher(GameSceneName nextGameSceneName) {
		super();

		this.nextGameSceneName = nextGameSceneName;
		this.count = 0;
	}

	@Override
	public void initialize() {
		InputManager.getInstance().setSceneName(GameSceneName.LAUNCH);
	}

	@Override
	public void update() {
		if (this.count++ == 0) {
			GraphicManager.getInstance().drawString("Now loading ...", GameSetting.STAGE_WIDTH / 2 - 80, 200);

		} else {
			switch (this.nextGameSceneName.name()) {
			case "PLAY":
				Logger.getAnonymousLogger().log(Level.INFO, "Transition to PLAY");
				Play play = new Play();
				this.setTransitionFlag(true);
				this.setNextGameScene(play);
				break;

			case "REPLAY":
				Logger.getAnonymousLogger().log(Level.INFO, "Transition to REPLAY");
				Replay replay = new Replay();
				this.setTransitionFlag(true);
				this.setNextGameScene(replay);
				break;
			default:
				Logger.getAnonymousLogger().log(Level.INFO, "This scene does not exist");
				this.setGameEndFlag(true);
			}

			// リソースのロード
			ResourceLoader.getInstance().loadResource();
		}
	}

	@Override
	public void close() {

	}

}
