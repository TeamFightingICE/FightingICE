package gamescene;

import static org.lwjgl.glfw.GLFW.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import enumerate.GameSceneName;
import fighting.Fighting;
import informationcontainer.RoundResult;
import input.KeyData;
import input.Keyboard;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import manager.SoundManager;
import setting.FlagSetting;
import setting.GameSetting;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;
import util.DebugActionData;
import util.LogWriter;
import util.ResourceDrawer;

public class Play extends GameScene {

	/** 対戦処理を行うクラスのインスタンス */
	private Fighting fighting;

	/** 現在のフレームナンバー */
	private int nowFrame;

	/** 各ラウンド前に行う初期化処理内における経過フレーム数 */
	private int elapsedBreakTime;

	/** 現在のラウンド */
	private int currentRound;

	/** 各ラウンドの開始時かどうかを表すフラグ */
	private boolean roundStartFlag;

	/** 対戦処理後のキャラクターデータといったゲーム情報を格納したフレームデータ */
	private FrameData frameData;

	/** 対戦処理後のゲーム画面の情報 */
	private ScreenData screenData;

	/** 対戦処理に用いるP1, P2の入力情報 */
	private KeyData keyData;

	/** 各ラウンド終了時のP1, P2の残り体力, 経過時間を格納するリスト */
	private ArrayList<RoundResult> roundResults;

	/** Replayファイルに出力するための出力ストリーム */
	private DataOutputStream dos;

	/** 現在の年月日, 時刻を表す文字列 */
	private String timeInfo;

	/** Playシーンを初期化するコンストラクタ */
	public Play() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.PLAY;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////

	}

	@Override
	public void initialize() {
		InputManager.getInstance().setSceneName(GameSceneName.PLAY);

		this.fighting = new Fighting();
		this.fighting.initialize();

		this.nowFrame = 0;
		this.elapsedBreakTime = 0;
		this.currentRound = 1;
		this.roundStartFlag = true;

		this.frameData = new FrameData();
		this.screenData = new ScreenData();
		this.keyData = new KeyData();
		this.roundResults = new ArrayList<RoundResult>();

		if (!FlagSetting.trainingModeFlag) {
			openReplayFile();
		}

		if(FlagSetting.debugActionFlag){
			DebugActionData.getInstance().initialize();
		}

		GameData gameData = new GameData(this.fighting.getCharacters());

		InputManager.getInstance().createAIcontroller();
		InputManager.getInstance().startAI(gameData);

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
			Logger.getAnonymousLogger().log(Level.INFO, "Game over");
			// BGMを止める
			SoundManager.getInstance().stop(SoundManager.getInstance().getBackGroundMusic());

			Result result = new Result(this.roundResults, this.timeInfo);
			this.setTransitionFlag(true);
			this.setNextGameScene(result);
		}

		if (Keyboard.getKeyDown(GLFW_KEY_ESCAPE)) {
			// BGMを止める
			SoundManager.getInstance().stop(SoundManager.getInstance().getBackGroundMusic());

			HomeMenu homeMenu = new HomeMenu();
			this.setTransitionFlag(true); // 現在のシーンからの遷移要求をtrueに
			this.setNextGameScene(homeMenu); // 次のシーンをセットする
		}

	}

	/** 各ラウンド開始時に, 対戦情報や現在のフレームなどの初期化を行う */
	private void initRound() {
		this.fighting.initRound();
		this.nowFrame = 0;
		this.roundStartFlag = false;
		this.elapsedBreakTime = 0;

		InputManager.getInstance().clear();
	}

	/** 各ラウンド開始時における, インターバル処理を行う */
	private void processingBreakTime() {
		// ダミーフレームをAIにセット
		InputManager.getInstance().setFrameData(new FrameData(), new ScreenData());

		GraphicManager.getInstance().drawQuad(0, 0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, 0, 0, 0, 0);
		GraphicManager.getInstance().drawString("Waiting for Round Start", 350, 200);
	}

	/**
	 * 対戦処理を行う.<br>
	 *
	 * 1. P1, P2の入力を受け取る.<br>
	 * 2. 対戦処理を行う.<br>
	 * 3. 対戦後のFrameData, 及びScreenDataを取得する.<br>
	 * 4. AIにFrameDataとScreenDataを渡す.<br>
	 * 5. ラウンドが終了しているか判定する.<br>
	 * 6. リプレイファイルにログを出力する.<br>
	 * 7. ゲーム画面を描画する.
	 */
	private void processingGame() {
		this.keyData = new KeyData(InputManager.getInstance().getKeyData());

		this.fighting.processingFight(this.nowFrame, this.keyData);
		this.frameData = this.fighting.createFrameData(this.nowFrame, this.currentRound, this.keyData);
		this.screenData = new ScreenData();

		// AIにFrameDataをセット
		InputManager.getInstance().setFrameData(this.frameData, this.screenData);
		// 体力が0orタイムオーバーならラウンド終了処理
		if (isBeaten() || isTimeOver()) {
			processingRoundEnd();
		}

		// リプレイログ吐き出し
		if (!FlagSetting.trainingModeFlag) {
			LogWriter.getInstance().outputLog(this.dos, this.keyData, this.fighting.getCharacters());
		}
		// 画面をDrawerクラスで描画
		ResourceDrawer.getInstance().drawResource(this.fighting.getCharacters(), this.fighting.getProjectileDeque(),
				this.fighting.getHitEffectList(), this.screenData.getScreenImage(),
				this.frameData.getRemainingTimeMilliseconds(), this.currentRound);

		// P1とP2の行った各アクションの数を数える
		if (FlagSetting.debugActionFlag) {
			DebugActionData.getInstance().countPlayerAction(this.frameData);
		}

	}

	/** 各ラウンド終了時の処理を行う. */
	private void processingRoundEnd() {
		RoundResult roundResult = new RoundResult(this.frameData);
		this.roundResults.add(roundResult);

		// AIに結果を渡す sendRoundResult(p1Hp, p2Hp, frames);
		InputManager.getInstance().sendRoundResult(roundResult);
		this.currentRound++;
		this.roundStartFlag = true;

		 // P1とP2の行った各アクションの数のデータをCSVに出力する
		if (FlagSetting.debugActionFlag) {
			DebugActionData.getInstance().outputActionCount();
		}
	}

	/**
	 * キャラクターが倒されたかどうかを判定する.
	 *
	 * @return true: P1 or P2が倒された; false: otherwise
	 */
	private boolean isBeaten() {
		return FlagSetting.limitHpFlag
				&& (this.frameData.getCharacter(true).getHp() <= 0 || this.frameData.getCharacter(false).getHp() <= 0);
	}

	/**
	 * 1ラウンドの制限時間が経過したかどうかを判定する.<br>
	 * Training modeのときは, Integerの最大との比較を行う.
	 *
	 * @return true: 1ラウンドの制限時間が経過した; false: otherwise
	 */
	private boolean isTimeOver() {
		if (FlagSetting.trainingModeFlag) {
			return this.nowFrame == Integer.MAX_VALUE;
		} else {
			return this.nowFrame == GameSetting.ROUND_FRAME_NUMBER - 1;
		}

	}

	/** リプレイファイルを作成し, 使用キャラクターを表すインデックスといったヘッダ情報を記述する. */
	private void openReplayFile() {
		this.timeInfo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss", Locale.ENGLISH));

		String fileName = LogWriter.getInstance().createOutputFileName("./log/replay/", this.timeInfo);
		this.dos = ResourceLoader.getInstance().openDataOutputStream(fileName + ".dat");

		LogWriter.getInstance().writeHeader(this.dos);
	}

	@Override
	public void close() {
		this.fighting = null;
		this.frameData = null;
		this.screenData = null;
		this.keyData = null;
		this.roundResults.clear();

		if (FlagSetting.debugActionFlag) {
			DebugActionData.getInstance().closeAllWriters();
		}

		try {
			this.dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
