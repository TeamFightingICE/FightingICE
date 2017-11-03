package core;

import java.awt.Font;

import gamescene.HomeMenu;
import image.LetterImage;
import manager.GameManager;
import manager.GraphicManager;

public class Game extends GameManager {

	public Game(String[] options){
		super();
	}

	@Override
	public void initialize() {
		//各マネージャの初期化
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		GraphicManager.getInstance().setLetterFont(new LetterImage(awtFont, true));

		HomeMenu homeMenu = new HomeMenu();
		this.startGame(homeMenu);

	}

	@Override
	public void close() {


	}

}
