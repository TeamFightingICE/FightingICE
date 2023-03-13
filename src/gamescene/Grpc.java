package gamescene;

import java.util.List;

import enumerate.GameSceneName;
import grpc.GrpcGame;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import setting.FlagSetting;
import setting.LaunchSetting;

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
		this.game = LaunchSetting.grpcServer.getGame();
	}

	@Override
	public void update() {
		if(FlagSetting.enableWindow){
			GraphicManager.getInstance().drawString("Waiting gRPC to launch a game", 300, 200);
		}
		if (this.game.getRunFlag()) {
			this.game.setRunFlag(false);

			List<String> allAiNames = ResourceLoader.getInstance().loadFileNames("./data/ai", ".jar");
			for (int i = 0; i < 2; i++) {
				String aiName = game.getAIName(i == 0);
				LaunchSetting.characterNames[i] = game.getCharacterName(i == 0);
				if (aiName != null) {
					LaunchSetting.deviceTypes[i] = allAiNames.contains(aiName) 
							? InputManager.DEVICE_TYPE_AI : InputManager.DEVICE_TYPE_GRPC;
					LaunchSetting.aiNames[i] = aiName;
				} else {
					LaunchSetting.deviceTypes[i] = InputManager.DEVICE_TYPE_KEYBOARD;
				}
			}
			
			LaunchSetting.repeatNumber = game.getGameNumber();
			if (LaunchSetting.repeatNumber > 1) {
				FlagSetting.automationFlag = true;
			}

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
