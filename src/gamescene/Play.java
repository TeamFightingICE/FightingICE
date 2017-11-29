package gamescene;

import java.util.ArrayList;

import enumerate.GameSceneName;
import fighting.Fighting;
import informationcontainer.RoundResult;
import input.KeyData;
import manager.GraphicManager;
import manager.InputManager;
import setting.FlagSetting;
import setting.GameSetting;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;
import util.ResourceDrawer;

public class Play extends GameScene {

	private Fighting fighting;

	private int nowFrame;

	private int elapsedBreakTime;

	private int currentRound;

	private boolean roundStartFlag;

	private FrameData frameData;

	private ScreenData screenData;

	private KeyData keyData;

	private ArrayList<RoundResult> roundResults;

	public Play() {

	}

	@Override
	public void initialize() {
		InputManager.getInstance().setSceneName(GameSceneName.PLAY);
		this.fighting = new Fighting();
		this.fighting.initialize();

		this.nowFrame = 0;
		this.elapsedBreakTime = 0;
		this.currentRound = 0;
		this.roundStartFlag = true;

		this.frameData = new FrameData();
		this.screenData = new ScreenData();
		this.keyData = new KeyData();
		this.roundResults = new ArrayList<RoundResult>();

		GameData gameData = new GameData(fighting.getCharacters());

		InputManager.getInstance().createAIcontroller();
		InputManager.getInstance().startAI(gameData);

	}

	@Override
	public void update() {

		if (this.currentRound < GameSetting.ROUND_MAX) {
			// ラウンド開始時に初期化
			if (this.roundStartFlag) {
				initRound();
				System.out.println("Round: " + currentRound);

			} else if (this.elapsedBreakTime < GameSetting.BREAKTIME_FRAME_NUMBER) {
				// break time
				processingBreakTime();
				this.elapsedBreakTime++;

			} else {
				// processing
				processingGame();
				this.nowFrame++;
			}

		} else {
			System.out.println("Result遷移");
			// BGMを止める
			Result result = new Result(this.roundResults);
			this.setTransitionFlag(true);
			this.setNextGameScene(result);

		}

	}

	private void initRound() {
		this.fighting.initRound();
		this.nowFrame = 0;
		this.roundStartFlag = false;
		this.elapsedBreakTime = 0;

		// Input clear
	}

	private void processingBreakTime() {
		// ダミーフレームをAIにセット
		InputManager.getInstance().setFrameData(new FrameData());

		GraphicManager.getInstance().drawQuad(0, 0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, 0, 0, 0, 0);
		GraphicManager.getInstance().drawString("Waiting for Round Start", 350, 200);
	}

	private void processingGame() {
		this.keyData = new KeyData(InputManager.getInstance().getKeyData());

		this.fighting.processingFight(this.nowFrame, this.keyData);
		this.frameData = this.fighting.createFrameData(this.nowFrame, this.currentRound, this.keyData);
		this.screenData = new ScreenData();

		// AIにFrameDataをセット
		InputManager.getInstance().setFrameData(new FrameData(this.frameData));
		// 体力が0orタイムオーバーならラウンド終了処理
		if (isBeaten() || isTimeOver()) {
			processingRoundEnd();
		}

		// リプレイログ吐き出し
		// 画面をDrawerクラスで描画
		ResourceDrawer.getInstance().drawResource(this.fighting.getCharacters(), this.fighting.getProjectileDeque(),
				this.fighting.getHitEffectList(), this.screenData.getScreenImage(),
				this.frameData.getRemainingTimeMilliseconds(), this.currentRound);

	}

	private void processingRoundEnd() {
		RoundResult roundResult = new RoundResult(this.frameData);
		this.roundResults.add(roundResult);

		// AIに結果を渡す sendRoundResult(p1Hp, p2Hp, frames);
		InputManager.getInstance().sendRoundResult(roundResult);
		this.currentRound++;
		this.roundStartFlag = true;
	}

	private boolean isBeaten() {
		return FlagSetting.limitHpFlag && (this.frameData.getMyCharacter(true).getHp() <= 0
				|| this.frameData.getMyCharacter(false).getHp() <= 0);
	}

	private boolean isTimeOver() {
		return this.nowFrame == GameSetting.ROUND_FRAME_NUMBER - 1;
	}

	@Override
	public void close() {

	}

}
