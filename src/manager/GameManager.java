package manager;

import java.util.LinkedList;

import core.Input;
import gamescene.GameScene;

public abstract class GameManager {

	/** 現在のゲームシーン <br>
	 * Menu, Launch, Play, Resultなどが入る*/
	private GameScene currentGameScene;

	/** ゲームシーンを管理するリスト */
	protected LinkedList<GameScene> gameSceneList;

	/** グラフィック関連のタスクを管理するマネージャー */
	protected GraphicManager graphicManager;

	/** AIやキーボード等の入力関連のタスクを管理するマネージャー */
	protected InputManager<?> inputManager;

	/** BGMやSE等の音楽関連のタスクを管理するマネージャー*/
	protected SoundManager soundManager;


	public GameManager(){
		graphicManager = new GraphicManager();
		inputManager = new Input();
		soundManager = new SoundManager();
		gameSceneList = new LinkedList<GameScene>();
	}

	public abstract void initialize();

	public abstract void update();

	public abstract void close();

}
