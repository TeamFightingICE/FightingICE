package gamescene;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import enumerate.GameSceneName;
import fighting.Fighting;
import input.KeyData;
import manager.GraphicManager;
import manager.InputManager;
import manager.SoundManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.FrameData;
import struct.Key;
import struct.ScreenData;
import util.ResourceDrawer;

public class Replay extends GameScene {

	private Fighting fighting;

	private DataInputStream dis;

	private int nowFrame;

	private int elapsedBreakTime;

	private int currentRound;

	private boolean roundStartFlag;

	private FrameData frameData;

	private ScreenData screenData;

	private KeyData keyData;

	public Replay() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.REPLAY;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize() {
		InputManager.getInstance().setSceneName(GameSceneName.REPLAY);

		try {
			String path = "./log/replay/" + LaunchSetting.replayName + ".dat";
			this.dis = new DataInputStream(new FileInputStream(new File(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 2; i++) {
			int maxHp = 0;

			try {
				int checkMode = dis.readInt();

				// Check whether fighting mode is limited HP mode or not
				// If it is HP mode, checkMode is less than 0 (e.g. -1)
				if (checkMode < 0) {
					maxHp = dis.readInt();

					LaunchSetting.characterNames[i] = GameSetting.CHARACTERS[dis.readInt()];
					FlagSetting.limitHpFlag = true;
				} else {
					LaunchSetting.characterNames[i] = GameSetting.CHARACTERS[checkMode];
					FlagSetting.limitHpFlag = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.fighting = new Fighting();
		this.fighting.initialize();

		this.nowFrame = 1;
		this.elapsedBreakTime = 0;
		this.currentRound = 1;
		this.roundStartFlag = true;

		this.frameData = new FrameData();
		this.screenData = new ScreenData();
		this.keyData = new KeyData();

		SoundManager.getInstance().play(SoundManager.getInstance().getBackGroundMusic());
	}

	@Override
	public void update() {
		if (this.currentRound <= GameSetting.ROUND_MAX) {
			// ラウンド開始時に初期化
			if (this.roundStartFlag) {
				initRound();

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
			// BGMを止める
			SoundManager.getInstance().stop(SoundManager.getInstance().getBackGroundMusic());

			HomeMenu homeMenu = new HomeMenu();
			this.setTransitionFlag(true);
			this.setNextGameScene(homeMenu);
		}

	}

	@Override
	public void close() {
		this.fighting = null;
		this.frameData = null;
		this.screenData = null;
		this.keyData = null;

		try {
			this.dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void processingBreakTime() {
		// ダミーフレームをAIにセット

		GraphicManager.getInstance().drawQuad(0, 0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, 0, 0, 0, 0);
		GraphicManager.getInstance().drawString("Waiting for Round Start", 350, 200);
	}

	private void processingGame() {
		this.keyData = createKeyData();

		this.fighting.processingFight(this.nowFrame, this.keyData);
		this.frameData = this.fighting.createFrameData(this.nowFrame, this.currentRound, this.keyData);
		this.screenData = new ScreenData();

		// 体力が0orタイムオーバーならラウンド終了処理
		if (isBeaten() || isTimeOver()) {
			processingRoundEnd();
		}

		// 画面をDrawerクラスで描画
		ResourceDrawer.getInstance().drawResource(this.fighting.getCharacters(), this.fighting.getProjectileDeque(),
				this.fighting.getHitEffectList(), this.screenData.getScreenImage(),
				this.frameData.getRemainingTimeMilliseconds(), this.currentRound);

	}

	private void processingRoundEnd() {
		this.currentRound++;
		this.roundStartFlag = true;
	}

	private boolean isBeaten() {
		return FlagSetting.limitHpFlag && (this.frameData.getMyCharacter(true).getHp() <= 0
				|| this.frameData.getMyCharacter(false).getHp() <= 0);
	}

	private boolean isTimeOver() {
		return this.nowFrame == GameSetting.ROUND_FRAME_NUMBER;
	}

	private void initRound() {
		this.fighting.initRound();
		this.nowFrame = 1;
		this.roundStartFlag = false;
		this.elapsedBreakTime = 0;
	}

	private KeyData createKeyData() {
		Key[] temp = new Key[2];

		for (int i = 0; i < 2; i++) {
			temp[i] = new Key();
			byte keyByte = 0;

			try {
				dis.readBoolean(); //front
				dis.readByte();  //remaingFrame
				dis.readByte(); // actionOrdinal
				dis.readInt(); //hp
				dis.readInt(); //energy
				dis.readInt(); //x
				dis.readInt(); //y
				keyByte = dis.readByte();
			} catch (IOException e) {
				e.printStackTrace();
			}

			temp[i].U = convertItoB(keyByte / 64);
			keyByte %= 64;
			temp[i].R = convertItoB(keyByte / 32);
			keyByte %= 32;
			temp[i].L = convertItoB(keyByte / 16);
			keyByte %= 16;
			temp[i].D = convertItoB(keyByte / 8);
			keyByte %= 8;
			temp[i].C = convertItoB(keyByte / 4);
			keyByte %= 4;
			temp[i].B = convertItoB(keyByte / 2);
			keyByte %= 2;
			temp[i].A = convertItoB(keyByte / 1);
			keyByte %= 1;
		}

		return new KeyData(temp);
	}

	/**
	 * boolean型変数をint型に変換する
	 *
	 * @param b
	 *            変換したいboolean型の変数
	 *
	 * @return 1 : 引数がtrueのとき, 0: 引数がfalseのとき
	 */
	private boolean convertItoB(int i) {
		return i == 1 ? true : false;
	}

}
