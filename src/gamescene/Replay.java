package gamescene;

import enumerate.GameSceneName;
import manager.InputManager;

public class Replay extends GameScene {

	@Override
	public void initialize() {
		InputManager.getInstance().setSceneName(GameSceneName.REPLAY);


	}

	@Override
	public void update() {


	}

	@Override
	public void close() {


	}

}
