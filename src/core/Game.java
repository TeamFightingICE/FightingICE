package core;

import java.awt.Font;

import gamescene.Launcher;
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

		//Menu menu = new Menu();
		Launcher launcher = new Launcher();
		this.startGame(launcher);

	}

	@Override
	public void close() {


	}

}
