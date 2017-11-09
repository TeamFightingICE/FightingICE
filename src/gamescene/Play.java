package gamescene;

import fighting.Fighting;
import manager.GraphicManager;
import setting.GameSetting;
import struct.FrameData;
import struct.GameData;

public class Play extends GameScene {

	private Fighting fighting;

	private int nowFrame;

	private int elapsedBreakTime;

	private int currentRound;

	private boolean roundStartFlag;

	public Play() {

	}

	@Override
	public void initialize() {
		this.fighting = new Fighting();
		this.fighting.initialize();

		this.nowFrame = 0;
		this.elapsedBreakTime = 0;
		this.currentRound = 0;
		this.roundStartFlag = true;

		GameData gameData = new GameData(fighting.getCharacters());
		// ((Input) im).initialize(deviceTypes, aiNames);
		// ((Input) im).startAI(gameData);

	}

	@Override
	public void update() {

		if (currentRound < GameSetting.ROUND_MAX) {
			// ラウンド開始時に初期化
			if (roundStartFlag) {
				initRound();

			} else if (elapsedBreakTime < GameSetting.BREAKTIME_FRAME_NUMBER) {
				// break time
				processingBreakTime();
				elapsedBreakTime++;

			} else if (nowFrame < GameSetting.ROUND_FRAME_NUMBER) {
				// processing
				processingGame();
				nowFrame++;

			} else {
				// round end
				processingRoundEnd();
			}

		} else {
			Result result = new Result();
			this.setTransitionFlag(true);
			this.setNextGameScene(result);
		}

	}

	private void initRound() {
		fighting.initRound();
		nowFrame = 0;
		roundStartFlag = true;
		elapsedBreakTime = 0;

		// Input clear
	}

	private void processingBreakTime() {
		// ダミーフレームをAIにセット

		GraphicManager.getInstance().drawQuad(0, 0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, 0, 0, 0, 0);
		GraphicManager.getInstance().drawString("Waiting for Round Start", 350, 200);
	}

	private void processingGame() {

		fighting.processingFight(nowFrame);
		FrameData frameData = fighting.createFrameData(nowFrame);
		//AIにFrameDataをセット
		//limithpモードで体力が尽きていたら、ラウンド終了処理
//		if(/*体力尽きた*/){
//			processingRoundEnd();
//		}

		//リプレイログ吐き出し
		//画面をDrawerクラスで描画

	}

	private void processingRoundEnd() {


	}

	@Override
	public void close() {

	}

}
