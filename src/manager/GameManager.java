package manager;

import core.Input;
import gamescene.GameScene;

public abstract class GameManager {

	/**
	 * 現在のゲームシーン <br>
	 * Menu, Launch, Play, Resultなどが入る
	 */
	private GameScene currentGameScene;

	/** グラフィック関連のタスクを管理するマネージャー */
	protected GraphicManager graphicManager;

	/** AIやキーボード等の入力関連のタスクを管理するマネージャー */
	protected InputManager<?> inputManager;

	/** BGMやSE等の音楽関連のタスクを管理するマネージャー */
	protected SoundManager soundManager;

	private boolean isExitFlag;

	public GameManager() {
		this.graphicManager = new GraphicManager();
		this.inputManager = new Input();
		this.soundManager = new SoundManager();
		this.currentGameScene = null;
		this.isExitFlag = false;
	}


	public abstract void initialize();
	public abstract void close();

	public void update() {

		inputManager.update();

		if (!currentGameScene.isGameEnd()) {
			if (currentGameScene.isTransition()) {
				// 現在のシーンの終了処理
				currentGameScene.close(graphicManager, soundManager, inputManager);
				currentGameScene = currentGameScene.getNextGameScene();
				currentGameScene.initialize(graphicManager, soundManager, inputManager);
			}

			currentGameScene.update(graphicManager, soundManager, inputManager);
		} else {
			this.isExitFlag = true;
		}
	}

	public void startGame(GameScene startGameScene){
		this.currentGameScene = startGameScene;
		this.currentGameScene.initialize(graphicManager, soundManager, inputManager);
	}



	public boolean isExit() {
		return this.isExitFlag;
	}

}
