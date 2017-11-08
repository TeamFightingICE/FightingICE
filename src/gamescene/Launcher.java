package gamescene;

import enumerate.GameSceneName;
import loader.ResourceLoader;

public class Launcher extends GameScene {

	private GameSceneName nextGameSceneName;

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
		this.gameSceneName = GameSceneName.LAUNCH;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;

		this.nextGameSceneName = nextGameSceneName;
	}

	@Override
	public void initialize() {
		System.out.println("Launcher initialize");
	}

	@Override
	public void update() {

		switch (this.nextGameSceneName.name()) {
		case "PLAY":
			System.out.println("Play遷移");
			Play play = new Play();
			this.setTransitioFlag(true);
			this.setNextGameScene(play);
			break;

		case "REPLAY":
			System.out.println("Replay遷移");
			Replay replay = new Replay();  //このコンストラクタでリプレイ再生時に使用するキャラをセットしないとつらい(
			this.setTransitioFlag(true);
			this.setNextGameScene(replay);
			break;
		default:
			System.out.println("存在しないシーン名です");
			this.setGameEndFlag(true);
		}

		ResourceLoader.getInstance().loadResource();

	}

	@Override
	public void close() {

	}

}
