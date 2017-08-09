package core;

import gamescene.Menu;
import manager.GameManager;

public class Game extends GameManager {

	public Game(String[] options){
		super();


	}

	@Override
	public void initialize() {
		//各マネージャの初期化

		Menu menu = new Menu();
		this.startGame(menu);

	}

	@Override
	public void close() {


	}

}
