package manager;

import gamescene.GameScene;

/**
 * 各ゲームシーンの初期化, 更新, 終了処理を管理するマネージャクラス．
 */
public abstract class GameManager {

	/**
	 * 現在のゲームシーン．<br>
	 * Menu, Launch, Play, Resultなどが入る．
	 *
	 * @see GameScene
	 */
	protected GameScene currentGameScene;

	/**
	 * ゲームの終了要求があったかどうかを表すフラグ．
	 */
	protected boolean isExitFlag;

	/**
	 * クラスコンストラクタ．<br>
	 * 現在のゲームシーンをnull，ゲームの終了要求がない状態(false)としてインスタンスの初期化を行う．
	 */
	public GameManager() {
		this.currentGameScene = null;
		this.isExitFlag = false;
	}

	/**
	 * 初期化処理の抽象メソッド．<br>
	 * GameManagerクラスを継承したクラスでオーバーライドして用いる．
	 */
	public abstract void initialize();

	/**
	 * 終了処理の抽象メソッド．<br>
	 * GameManagerクラスを継承したクラスでオーバーライドして用いる．
	 */
	public abstract void close();

	/**
	 * 現在のゲームシーンの更新を行う．<br>
	 * ゲーム終了判定が下されず，次のシーンへの遷移要求がない場合は，現在のゲームシーンの更新を行う．<br>
	 * ゲーム終了判定が下されず，次のシーンへの遷移要求がある場合は，現在のシーンの終了処理と遷移処理を行う．<br>
	 * ゲーム終了判定が下された場合は，ゲームの終了要求があったかどうかのフラグをtrueにする．
	 */
	public void update() {
		InputManager.getInstance().update();

		if (!currentGameScene.isGameEnd()) {
			if (currentGameScene.isTransition()) {

				// 現在のシーンの終了処理
				currentGameScene.close();

				// 遷移先のシーンを現在のシーンにセットし,初期化処理を行う
				currentGameScene = currentGameScene.getNextGameScene();
				currentGameScene.initialize();
			}

			// 現在のシーンの更新
			currentGameScene.update();
		} else {
			this.isExitFlag = true;
		}
	}

	/**
	 * ゲームをスタートする．<br>
	 * 引数に指定したゲームシーンを現在のゲームシーンとして設定し，ゲームの開始処理を行う．
	 *
	 * @param startGameScene
	 *            開始させるゲームシーン
	 */
	public void startGame(GameScene startGameScene) {
		this.currentGameScene = startGameScene;
		this.currentGameScene.initialize();
	}

	/**
	 * ゲームの終了要求があったかどうかのフラグを返す．<br>
	 * GameManagerの終了処理の判断に用いられる．
	 *
	 * @return ゲームの終了要求があったかどうかのフラグ
	 */
	public boolean isExit() {
		return this.isExitFlag;
	}

}
