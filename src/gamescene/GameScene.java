package gamescene;

import enumerate.GameSceneName;
import manager.GraphicManager;
import manager.InputManager;
import manager.SoundManager;

public class GameScene {

	/** 現在のゲームシーンの名前 */
	protected GameSceneName gameSceneName;

	/** ゲームが終わったかどうかを表すフラグ */
	protected boolean isGameEndFlag;

	/** 次のゲームシーンへの遷移要求があったかどうかを表すフラグ */
	protected boolean isTransitionFlag;

	protected GameScene nextGameScene;

	public GameScene() {
		this.gameSceneName = GameSceneName.MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;

	}

	public GameScene(GameScene gameScene) {
		this.gameSceneName = gameScene.getCurrentSceneName();
		this.isGameEndFlag = gameScene.isGameEndFlag;
		this.isTransitionFlag = gameScene.isTransitionFlag;
		this.nextGameScene = gameScene.getNextGameScene();

	}

	public GameScene(GameSceneName gameSceneName, boolean isEndFlag, boolean isTransitionFlag,
			GameScene nextGameScene) {
		this.gameSceneName = gameSceneName;
		this.isGameEndFlag = isEndFlag;
		this.isTransitionFlag = isTransitionFlag;
		this.nextGameScene = nextGameScene;
	}

	public void initialize(GraphicManager gm, SoundManager sm, InputManager<?> im) {
	}

	public void update(GraphicManager gm, SoundManager sm, InputManager<?> im) {
	}

	public void close(GraphicManager gm, SoundManager sm, InputManager<?> im) {
	}

	/**
	 * 遷移先のゲームシーンを取得する
	 *
	 * @return 遷移先のゲームシーン
	 */
	public GameScene getNextGameScene() {
		return nextGameScene;
	}

	/**
	 * 現在のゲームシーン名を取得する
	 *
	 * @return 現在のゲームシーン
	 */
	public GameSceneName getCurrentSceneName() {
		return this.gameSceneName;
	}

	/**
	 * 遷移先のゲームシーンをセットする
	 *	 * @param next
	 *            遷移先のゲームシーン
	 */
	public void setNextGameScene(GameScene next) {
		this.nextGameScene = next;
	}

	/**
	 * 現在のゲームシーンのシーン名をセットする
	 *
	 * @param 現在のゲームシーン名
	 */
	public void setCurrentSceneName(GameSceneName gameSceneName) {
		this.gameSceneName = gameSceneName;
	}

	/**
	 * ゲームの終了要求があったかどうかを返す
	 *
	 * @return ゲームの終了要求があったかどうか
	 */
	public boolean isGameEnd() {
		return this.isGameEndFlag;
	}

	/**
	 * 次のゲームシーンへの遷移要求があったかどうかを返す
	 *
	 * @return 次のゲームシーンへの遷移要求があったかどうか
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
	public void setTransitioFlag(boolean isTransition) {
		this.isTransitionFlag = isTransition;
	}

}
