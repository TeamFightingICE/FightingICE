package gamescene;

import java.util.logging.Level;
import java.util.logging.Logger;

import enumerate.GameSceneName;
import grpc.GrpcGame;
import manager.GraphicManager;
import service.SocketServer;
import setting.FlagSetting;

/**
 * Python側で起動したゲームの実行処理を行うクラス．
 */
public class Grpc extends GameScene {
	
	private GrpcGame game;

	/**
	 * クラスコンストラクタ．
	 */
	public Grpc() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.GRPC;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize() {
		this.game = SocketServer.getInstance().getGame();
		FlagSetting.isPyftgReady = true;
		
		Logger.getAnonymousLogger().log(Level.INFO, "Waiting to launch a game");
	}
	
	@Override
	public void update() {
		GraphicManager.getInstance().drawString("Waiting to launch a game", 330, 200);
		
		if (this.game.isReady()) {
			this.game.setRunFlag(false);
			FlagSetting.isPyftgReady = false;

			Launcher launcher = new Launcher(GameSceneName.PLAY);
			this.setTransitionFlag(true);
			this.setNextGameScene(launcher);
		}
	}

	@Override
	public void close() {
		this.game = null;
	}

}
