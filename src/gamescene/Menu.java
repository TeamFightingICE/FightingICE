package gamescene;

import enumerate.GameSceneName;
import manager.GraphicManager;
import manager.InputManager;
import manager.SoundManager;

public class Menu extends GameScene {

	public Menu(){
		//以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize(GraphicManager gm, SoundManager sm, InputManager<?> im) {
		System.out.println("Menu initialize");


	}

	@Override
	public void update(GraphicManager gm, SoundManager sm, InputManager<?> im) {

		//処理...
		//次シーンに遷移するとき
		Launcher launch = new Launcher();  //次のシーンのコンストラクタ作成
	//	this.setTransitioFlag(true);    //現在のシーンからの遷移要求をtrueに
	//	this.setNextGameScene(launch);       //次のシーンをセットする
		//System.out.println(this.gameSceneName);
		//gm.drawQuad(100, 100, 250, 260, 0.0f, 0.0f, 1.0f, 0.0f);
		gm.drawString("Hello, World", 200, 200);
		//System.out.println("Menu update");


	}

	@Override
	public void close(GraphicManager gm, SoundManager sm, InputManager<?> im) {
		System.out.println("Menu close");

	}

}
