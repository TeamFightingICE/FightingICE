package gamescene;

import enumerate.GameSceneName;
import loader.ResourceLoader;

public class Launcher extends GameScene {

	public Launcher() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.LAUNCH;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize() {
		System.out.println("Launcher initialize");

	}

	@Override
	public void update() {
		System.out.println("Launcher update");
		//this.isGameEndFlag = true;
		String[] characterName = {"ZEN", "GARNET"};
		ResourceLoader.getInstance().loadResource(characterName);

	}

	@Override
	public void close() {

	}

}
