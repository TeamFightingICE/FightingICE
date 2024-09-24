package gamescene;

import java.util.logging.Level;
import java.util.logging.Logger;

import enumerate.GameSceneName;
import manager.GraphicManager;
import service.GameService;
import setting.FlagSetting;

/**
 * Python側で起動したゲームの実行処理を行うクラス．
 */
public class Socket extends GameScene {

	/**
	 * クラスコンストラクタ．
	 */
	public Socket() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.SOCKET;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize() {
		FlagSetting.isPyftgReady = true;
		
		Logger.getAnonymousLogger().log(Level.INFO, "Waiting to launch a game");
	}
	
	@Override
	public void update() {
		GraphicManager.getInstance().drawString("Waiting to launch a game", 330, 200);
		
		if (GameService.getInstance().getCloseFlag()) {
			this.setGameEndFlag(true);
			
		} else if (GameService.getInstance().getRunFlag()) {
			GameService.getInstance().setRunFlag(false);
			FlagSetting.isPyftgReady = false;

			Launcher launcher = new Launcher(GameSceneName.PLAY);
			this.setTransitionFlag(true);
			this.setNextGameScene(launcher);
			
		}
	}

	@Override
	public void close() {
		
	}

}
