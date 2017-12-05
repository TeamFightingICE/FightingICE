package gamescene;

import enumerate.GameSceneName;

/** LauncherやPlayといった,ゲームシーンの共通部分をまとめた親クラス */
public class GameScene {

	/** 現在のゲームシーンの名前 */
	protected GameSceneName gameSceneName;

	/** ゲームが終わったかどうかを表すフラグ */
	protected boolean isGameEndFlag;

	/** 次のゲームシーンへの遷移要求があったかどうかを表すフラグ */
	protected boolean isTransitionFlag;

	/** 次の遷移先のゲームシーン */
	protected GameScene nextGameScene;

	/** ゲームシーンを初期化するするコンストラクタ */
	public GameScene() {
		this.gameSceneName = GameSceneName.HOME_MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;

	}

	/**
	 * 指定されたデータでゲームシーンを作成するコンストラクタ
	 *
	 * @param gameScene
	 *            指定されたゲームシーン
	 */
	public GameScene(GameScene gameScene) {
		this.gameSceneName = gameScene.getCurrentSceneName();
		this.isGameEndFlag = gameScene.isGameEndFlag;
		this.isTransitionFlag = gameScene.isTransitionFlag;
		this.nextGameScene = gameScene.getNextGameScene();

	}

	/**
	 * 指定されたデータでゲームシーンを作成するコンストラクタ
	 *
	 * @param gameSceneName
	 *            指定されたゲームシーン名
	 * @param isEndingFlag
	 *            現在のシーンが終わったかどうかを表すフラグ
	 * @param isTransitionFlag
	 *            遷移要求があったかどうかを表すフラグ
	 * @param nextGameScene
	 *            次に遷移するゲームシーン
	 *
	 */
	public GameScene(GameSceneName gameSceneName, boolean isEndFlag, boolean isTransitionFlag,
			GameScene nextGameScene) {
		this.gameSceneName = gameSceneName;
		this.isGameEndFlag = isEndFlag;
		this.isTransitionFlag = isTransitionFlag;
		this.nextGameScene = nextGameScene;
	}

	/** 現在のゲームシーンの初期化を行う */
	public void initialize() {
	}

	/** 現在のゲームシーンの更新を行う */
	public void update() {
	}

	/** 現在のゲームシーンの終了処理を行う */
	public void close() {
	}

	/**
	 * 次の遷移先のゲームシーンを取得する
	 *
	 * @return 次の遷移先のゲームシーン
	 */
	public GameScene getNextGameScene() {
		return nextGameScene;
	}

	/**
	 * 現在のゲームシーン名を取得する
	 *
	 * @return 現在のゲームシーン名
	 */
	public GameSceneName getCurrentSceneName() {
		return this.gameSceneName;
	}

	/**
	 * 次の遷移先のゲームシーンをセットする
	 *
	 * @param next
	 *            次の遷移先のゲームシーン
	 */
	public void setNextGameScene(GameScene next) {
		this.nextGameScene = next;
	}

	/**
	 * 現在のゲームシーンのシーン名をセットする
	 *
	 * @param gameSceneName
	 *            現在のゲームシーン名
	 */
	public void setCurrentSceneName(GameSceneName gameSceneName) {
		this.gameSceneName = gameSceneName;
	}

	/**
	 * ゲームの終了要求があったかどうかを返す
	 *
	 * @return true: ゲームの終了要求があった; false: otherwise
	 */
	public boolean isGameEnd() {
		return this.isGameEndFlag;
	}

	/**
	 * 次のゲームシーンへの遷移要求があったかどうかを返す
	 *
	 * @return true: 次のゲームシーンへの遷移要求があった; false: otherwise
	 */
	public boolean isTransition() {
		return this.isTransitionFlag;
	}

	/**
	 * ゲームの終了要求を表すフラグをセットする<br>
	 * {@code true} 終了要求があった; {@code false} 終了要求が無かった
	 *
	 * @param isEnd
	 *            終了要求があったかを表すフラグ
	 */
	public void setGameEndFlag(boolean isEnd) {
		this.isGameEndFlag = isEnd;
	}

	/**
	 * 次のゲームシーンへの遷移要求を表すフラグをセットする<br>
	 * {@code true} 遷移要求があった; {@code false} 遷移要求が無かった
	 *
	 * @param isTransition
	 *            遷移要求があったかを表すフラグ
	 */
	public void setTransitionFlag(boolean isTransition) {
		this.isTransitionFlag = isTransition;
	}

}
