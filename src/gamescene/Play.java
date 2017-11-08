package gamescene;

import java.util.LinkedList;

import fighting.Fighting;
import setting.GameSetting;
import struct.FrameData;
import struct.GameData;

public class Play extends GameScene {

	private Fighting fighting;

	private LinkedList<FrameData> framesData;

	private int nowFrame;

	private int elapsedBreakTime;

	private boolean roundStartFlag;

	public Play() {

	}

	@Override
	public void initialize() {
		this.fighting = new Fighting();
		this.fighting.initialize();

		this.framesData = new LinkedList<FrameData>();
		this.nowFrame = 0;
		this.elapsedBreakTime = 0;
		this.roundStartFlag = true;

		GameData gameData = new GameData(fighting.getCharacters());
		// ((Input) im).initialize(deviceTypes, aiNames);
		// ((Input) im).startAI(gameData);

	}

	@Override
	public void update() {

		if (fighting.getCurrentRound() < GameSetting.ROUND_MAX) {
			if (roundStartFlag) {
				// init
			} else if (elapsedBreakTime < GameSetting.BREAKTIME_FRAME_NUMBER) {
				// break
				elapsedBreakTime++;
			} else if (nowFrame < GameSetting.ROUND_FRAME_NUMBER) {
				// process
				nowFrame++;
			} else {
				// round end
			}
		} else {
			Result result = new Result();
			this.setTransitionFlag(true);
			this.setNextGameScene(result);
		}

	}

	@Override
	public void close() {

	}

}
