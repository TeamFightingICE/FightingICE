package core;

import java.awt.Font;

import gamescene.Menu;
import image.LetterImage;
import manager.GameManager;

public class Game extends GameManager {

	public Game(String[] options){
		super();
	}

	@Override
	public void initialize() {
		//各マネージャの初期化
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		this.graphicManager.setLetterFont(new LetterImage(awtFont, true));

		Menu menu = new Menu();
		this.startGame(menu);

	}

	@Override
	public void close() {


	}

}
