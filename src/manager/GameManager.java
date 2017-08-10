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

	/** ゲームの終了要求があったかを表すフラグ */
	private boolean isExitFlag;

	/** フィールドの初期化を行うコンストラクタ */
	public GameManager() {
		this.graphicManager = new GraphicManager();
		this.inputManager = new Input();
		this.soundManager = new SoundManager();
		this.currentGameScene = null;
		this.isExitFlag = false;
	}

	public abstract void initialize();

	public abstract void close();

	/** 現在のゲームシーンの更新を行う */
	public void update() {
		inputManager.update();

		if (!currentGameScene.isGameEnd()) {
			if (currentGameScene.isTransition()) {

				// 現在のシーンの終了処理
				currentGameScene.close(graphicManager, soundManager, inputManager);

				//遷移先のシーンを現在のシーンにセットし,初期化処理を行う
				currentGameScene = currentGameScene.getNextGameScene();
				currentGameScene.initialize(graphicManager, soundManager, inputManager);
			}

			//現在のシーンの更新
			currentGameScene.update(graphicManager, soundManager, inputManager);
		} else {
			this.isExitFlag = true;
		}
	}

	/**
	 * ゲームをスタートする<br>
	 * 引数に指定したゲームのシーンからゲームが始まる
	 *
	 * @param startGameScene
	 *            始めのゲームシーン
	 */
	public void startGame(GameScene startGameScene) {
		this.currentGameScene = startGameScene;
		this.currentGameScene.initialize(graphicManager, soundManager, inputManager);
	}

	/**
	 * ゲームの終了要求があったかどうかを返す
	 *
	 * @return ゲームの終了要求があったかどうか
	 */
	public boolean isExit() {
		return this.isExitFlag;
	}

	public void render(){
		graphicManager.render();
	}

}
