package gamescene;

import enumerate.GameSceneName;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import manager.SoundManager;

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
	public void initialize(GraphicManager gm, SoundManager sm, InputManager<?> im) {
		System.out.println("Launcher initialize");

	}

	@Override
	public void update(GraphicManager gm, SoundManager sm, InputManager<?> im) {
		System.out.println("Launcher update");
		//this.isGameEndFlag = true;

		ResourceLoader.getInstance().loadCharacterFile(gm.getImageContainer(), "ZEN");

	}

	@Override
	public void close(GraphicManager gm, SoundManager sm, InputManager<?> im) {

	}

}
