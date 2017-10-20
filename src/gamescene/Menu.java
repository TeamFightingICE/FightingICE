package gamescene;

import enumerate.GameSceneName;

public class Menu extends GameScene {

	/*int x = 100;
	int y = 100;
	int count = 0;*/

	public Menu(){
		//以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize() {
		System.out.println("Menu initialize");

		//gm.drawString("Hello World", 100, 100);
	}

	@Override
	public void update() {

		//処理...
		//次シーンに遷移するとき
		Launcher launch = new Launcher();  //次のシーンのコンストラクタ作成
	//	this.setTransitioFlag(true);    //現在のシーンからの遷移要求をtrueに
	//	this.setNextGameScene(launch);       //次のシーンをセットする
		//System.out.println(this.gameSceneName);
		//gm.drawQuad(100, 100, 250, 260, 0.0f, 0.0f, 1.0f, 0.0f);
		//System.out.println("Menu update");
		/*if(count < 500){
			gm.drawQuad(++x, y, 10, 10, 1.0f, 0.0f, 0.0f, 0.0f);
		} else if(count < 1000){
			gm.drawQuad(x, ++y, 10, 10, 1.0f, 0.0f, 0.0f, 0.0f);
		} else if(count < 1500){
			gm.drawQuad(--x, y, 10, 10, 1.0f, 0.0f, 0.0f, 0.0f);
		}else if(count < 2000){
			gm.drawQuad(x, --y, 10, 10, 1.0f, 0.0f, 0.0f, 0.0f);
		} else {
			count = 0;
		}
		count++;
		*/


	}

	@Override
	public void close() {
		System.out.println("Menu close");

	}

}
