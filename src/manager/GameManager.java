package manager;

import core.Input;
import gamescene.GameScene;

public abstract class GameManager {

	/**
	 * 現在のゲームシーン <br>
	 * Menu, Launch, Play, Resultなどが入る
	 */
	private GameScene currentGameScene;

	/** AIやキーボード等の入力関連のタスクを管理するマネージャー
	 * inputManagerはシングルトンなクラスで宣言するため、ここにインスタンスを作成するべきではない
	 * 伊藤君と話し合って修正する*/
	protected InputManager<?> inputManager;

	/** ゲームの終了要求があったかを表すフラグ */
	private boolean isExitFlag;

	/** フィールドの初期化を行うコンストラクタ */
	public GameManager() {
		this.inputManager = new Input();//伊藤君と話し合って修正する
		this.currentGameScene = null;
		this.isExitFlag = false;
	}

	public abstract void initialize();

	public abstract void close();

	/** 現在のゲームシーンの更新を行う */
	public void update() {
		inputManager.update();  //伊藤君と話し合って修正する

		if (!currentGameScene.isGameEnd()) {
			if (currentGameScene.isTransition()) {

				// 現在のシーンの終了処理
				currentGameScene.close();

				//遷移先のシーンを現在のシーンにセットし,初期化処理を行う
				currentGameScene = currentGameScene.getNextGameScene();
				currentGameScene.initialize();
			}

			//現在のシーンの更新
			currentGameScene.update();
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
		this.currentGameScene.initialize();
	}

	/**
	 * ゲームの終了要求があったかどうかを返す
	 *
	 * @return ゲームの終了要求があったかどうか
	 */
	public boolean isExit() {
		return this.isExitFlag;
	}

}
