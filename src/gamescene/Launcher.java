package gamescene;

import java.util.logging.Level;
import java.util.logging.Logger;

import enumerate.GameSceneName;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import setting.FlagSetting;
import setting.GameSetting;

public class Launcher extends GameScene {

	/** 次の遷移先のゲームシーン */
	private GameSceneName nextGameSceneName;

	/** 最初のアップデートかどうかを表すフラグ */
	private boolean isFirstUpdate;

	/** Launcherシーンを初期化するコンストラクタ */
	public Launcher() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.LAUNCH;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////

		this.nextGameSceneName = null;

	}

	/**
	 * Launcherシーンを初期化し, 次の遷移先のゲームシーンを設定するコンストラクタ
	 *
	 * @param nextGameScene
	 *            指定された次の遷移先のゲームシーン
	 */
	public Launcher(GameSceneName nextGameSceneName) {
		super();

		this.nextGameSceneName = nextGameSceneName;
		this.isFirstUpdate = true;
	}

	@Override
	public void initialize() {
		InputManager.getInstance().setSceneName(GameSceneName.LAUNCH);
	}

	@Override
	public void update() {
		if (this.isFirstUpdate && !FlagSetting.enableWindow) {
			GraphicManager.getInstance().drawString("Now loading ...", GameSetting.STAGE_WIDTH / 2 - 80, 200);
			this.isFirstUpdate = false;

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

			if (FlagSetting.enableWindow) {
				// リソースのロード
				ResourceLoader.getInstance().loadResource();
			}
		}
	}

	@Override
	public void close() {

	}

}
