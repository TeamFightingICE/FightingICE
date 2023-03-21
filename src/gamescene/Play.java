package gamescene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import enumerate.GameSceneName;
import fighting.Fighting;
import grpc.ObserverAgent;
import informationcontainer.RoundResult;
import input.KeyData;
import input.Keyboard;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import manager.SoundManager;
import py4j.Py4JException;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;
import util.BGMUtil;
import util.DebugActionData;
import util.LogWriter;
import util.ResourceDrawer;

/**
 * 対戦中のシーンを扱うクラス．
 */
public class Play extends GameScene {

	/**
	 * 対戦処理を行うクラスのインスタンス．
	 */
	private Fighting fighting;

	/**
	 * 現在のフレーム．
	 */
	private int nowFrame;

	/**
	 * 各ラウンド前に行う初期化処理内における経過フレーム数．
	 */
	private int elapsedBreakTime;

	/**
	 * 現在のラウンド．
	 */
	private int currentRound;

	/**
	 * 各ラウンドの開始時かどうかを表すフラグ．
	 */
	private boolean roundStartFlag;

	/**
	 * 対戦処理後のキャラクターデータなどのゲーム情報を格納したフレームデータ．
	 */
	private FrameData frameData;

	/**
	 * 対戦処理後のゲーム画面の情報．
	 */
	private ScreenData screenData;

	/**
	 * 対戦処理に用いるP1, P2の入力情報．
	 */
	private KeyData keyData;

	/**
	 * 各ラウンド終了時のP1, P2の残り体力, 経過時間を格納するリスト．
	 */
	private ArrayList<RoundResult> roundResults;

	/**
	 * Replayファイルに出力するための出力ストリーム．
	 */
	private DataOutputStream dos;

	/**
	 * 現在の年月日, 時刻を表す文字列．
	 */
	private String timeInfo;

	private int endFrame;

	private AudioData audioData;
	
	private FileWriter writer;
	
	/**
	 * クラスコンストラクタ．
	 */
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
		this.endFrame = -1;
		this.frameData = new FrameData();
		this.screenData = new ScreenData();
		this.audioData = new AudioData();
		this.keyData = new KeyData();
		this.roundResults = new ArrayList<RoundResult>();

		this.timeInfo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss", Locale.ENGLISH));

		if (!FlagSetting.trainingModeFlag) {
			openReplayFile();
		}

		if (FlagSetting.debugActionFlag) {
			DebugActionData.getInstance().initialize();
		}

		if (FlagSetting.jsonFlag) {
			String jsonName = LogWriter.getInstance().createOutputFileName("./log/replay/", this.timeInfo);
			LogWriter.getInstance().initJson(jsonName + ".json");
		}

		GameData gameData = new GameData(this.fighting.getCharacters());
		if (FlagSetting.grpc) {
			LaunchSetting.grpcServer.getObserver().initialize(gameData);
		}

		try {
			InputManager.getInstance().createAIcontroller();
			InputManager.getInstance().startAI(gameData);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        Logger.getAnonymousLogger().log(Level.INFO, "AI controller is ready");
		} catch (Py4JException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Fail to Initialize AI");
			Launcher launch = new Launcher(GameSceneName.PLAY);
			this.setTransitionFlag(true);
			this.setNextGameScene(launch);
		}
		
		SoundManager.getInstance().initializeBGM();
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
				if (this.endFrame == -1) {
					this.nowFrame++;
				} else if (this.endFrame % 30 == 0) {
					this.nowFrame++;
				}
			}

		} else {
			processingGameEnd();
			Logger.getAnonymousLogger().log(Level.INFO, "Game over");

			Result result = new Result(this.roundResults, this.timeInfo);
			this.setTransitionFlag(true);
			this.setNextGameScene(result);
		}

		if (Keyboard.getKeyDown(GLFW_KEY_SPACE)) {
			System.out.println("P1 x:" + this.frameData.getCharacter(true).getCenterX() + "\n" + "P2 x:"
					+ this.frameData.getCharacter(false).getCenterX() + "\n" + "P1 Left:"
					+ this.frameData.getCharacter(true).getLeft() + "\n" + "P1 Right:"
					+ this.frameData.getCharacter(true).getRight() + "\n" + "P2 Left:"
					+ this.frameData.getCharacter(false).getLeft() + "\n" + "P2 Right:"
					+ this.frameData.getCharacter(false).getRight() + "\n");
		}

		if (Keyboard.getKeyDown(GLFW_KEY_ESCAPE)) {
			HomeMenu homeMenu = new HomeMenu();
			this.setTransitionFlag(true);
			this.setNextGameScene(homeMenu);
		}

	}

	/**
	 * 各ラウンド開始時に, 対戦情報や現在のフレームなどの初期化を行う．
	 */
	private void initRound() {
		this.fighting.initRound();
		this.nowFrame = 0;
		this.roundStartFlag = false;
		this.elapsedBreakTime = 0;
		this.keyData = new KeyData();

		InputManager.getInstance().clear();
		SoundManager.getInstance().playBGM();
		
		//TODO to be remove
		String timeInfo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss", Locale.ENGLISH));
    	String fileName = String.format("log/bgm_%s.csv", timeInfo);
    	try {
			this.writer = new FileWriter(new File(fileName));
			this.writer.write("frame,violin,piano,flute,ukulele,cello\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 各ラウンド開始時における, インターバル処理を行う．
	 */
	private void processingBreakTime() {
		// ダミーフレームをAIにセット
		InputManager.getInstance().setFrameData(new FrameData(), new ScreenData(), new AudioData());

		if (FlagSetting.enableWindow) {
			GraphicManager.getInstance().drawQuad(0, 0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, 0, 0, 0, 0);
			GraphicManager.getInstance().drawString("Waiting for Round Start", 350, 200);
		}
		this.fighting.initRound();
	}

	/**
	 * 対戦処理を行う.<br>
	 *
	 * 1. P1, P2の入力を受け取る.<br>
	 * 2. 対戦処理を行う.<br>
	 * 3. 対戦後のFrameDataを取得する.<br>
	 * 4. リプレイファイルにログを出力する.<br>
	 * 5. ゲーム画面を描画する.<br>
	 * 6. 対戦後の画面情報(ScreenData)を取得する．<br>
	 * 7. AIにFrameData及びScreenDataを渡す．<br>
	 * 8. ラウンドが終了しているか判定する.<br>
	 */
	private void processingGame() {
		if (this.endFrame != -1) {
			this.keyData = new KeyData();
			if (this.endFrame % 30 == 0) {
				this.fighting.processingFight(this.nowFrame, this.keyData);
			}
		} else {
			this.keyData = new KeyData(InputManager.getInstance().getKeyData());
			this.fighting.processingFight(this.nowFrame, this.keyData);
		}

		this.frameData = this.fighting.createFrameData(this.nowFrame, this.currentRound);
		
		// for Adaptive Sound Design -> volume [ 0.1, 0.75 ]
		if (!this.frameData.getEmptyFlag()) {
			float[] audioGains = BGMUtil.getAudioGains(this.frameData);
			try {
				this.writer.write(String.format("%d,%f,%f,%f,%f,%f\n", this.frameData.getFramesNumber(), audioGains[0], 
						audioGains[1], audioGains[2], audioGains[3], audioGains[4]));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SoundManager.getInstance().setBGMAudioGains(audioGains);
		}

		// リプレイログ吐き出し
		if (!FlagSetting.trainingModeFlag) {
			LogWriter.getInstance().outputLog(this.dos, this.keyData, this.fighting.getCharacters());
		}

		if (FlagSetting.jsonFlag) {
			LogWriter.getInstance().updateJson(this.frameData, this.keyData);
		}

		if (FlagSetting.enableWindow) {
			// 画面をDrawerクラスで描画
			ResourceDrawer.getInstance().drawResource(this.fighting.getCharacters(), this.fighting.getProjectileDeque(),
					this.fighting.getHitEffectList(), this.frameData.getRemainingTimeMilliseconds(), this.currentRound);
		}

		// P1とP2の行った各アクションの数を数える
		if (FlagSetting.debugActionFlag) {
			DebugActionData.getInstance().countPlayerAction(this.fighting.getCharacters());
		}

		this.screenData = new ScreenData();
		if (this.nowFrame == 0) {
			this.audioData = new AudioData();
		} else {
            this.audioData = new AudioData(SoundManager.getInstance().getVirtualRenderer().sampleAudio());
        }
		// AIにFrameDataをセット
		InputManager.getInstance().setFrameData(this.frameData, this.screenData, this.audioData);
		
		if (FlagSetting.grpc) {
			ObserverAgent observer = LaunchSetting.grpcServer.getObserver();
			observer.setInformation(this.frameData, this.audioData, this.screenData);
			observer.onGameUpdate();
		}

		// 体力が0orタイムオーバーならラウンド終了処理
		if (isBeaten() || isTimeOver()) {
			processingRoundEnd();
		}
	}

	/**
	 * ラウンド終了時の処理を行う.
	 */
	private void processingRoundEnd() {
		SoundManager.getInstance().stopAll();
		try {
			this.writer.close();
			this.writer = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (FlagSetting.slowmotion) {
			if (this.endFrame > GameSetting.ROUND_EXTRAFRAME_NUMBER) {
				this.fighting.processingRoundEnd();
				RoundResult roundResult = new RoundResult(this.frameData);
				this.roundResults.add(roundResult);

				// AIに結果を渡す
				InputManager.getInstance().sendRoundResult(roundResult);
				this.currentRound++;
				this.roundStartFlag = true;
				this.endFrame = -1;

				// P1とP2の行った各アクションの数のデータをCSVに出力する
				if (FlagSetting.debugActionFlag) {
					DebugActionData.getInstance().outputActionCount();
				}
			} else {
				this.endFrame++;
			}

		} else {
			this.endFrame = 0;
			this.fighting.processingRoundEnd();
			RoundResult roundResult = new RoundResult(this.frameData);
			this.roundResults.add(roundResult);

			// AIに結果を渡す
			InputManager.getInstance().sendRoundResult(roundResult);
			this.currentRound++;
			this.roundStartFlag = true;
			this.endFrame = -1;

			// P1とP2の行った各アクションの数のデータをCSVに出力する
			if (FlagSetting.debugActionFlag) {
				DebugActionData.getInstance().outputActionCount();
			}
		}
	}
	
	private void processingGameEnd() {
		InputManager.getInstance().gameEnd();
		SoundManager.getInstance().closeBGM();
	}

	/**
	 * キャラクターが倒されたかどうかを判定する.
	 *
	 * @return {@code true}: P1 or P2が倒された，{@code false}: otherwise
	 */
	private boolean isBeaten() {
		return FlagSetting.limitHpFlag
				&& (this.frameData.getCharacter(true).getHp() <= 0 || this.frameData.getCharacter(false).getHp() <= 0);
	}

	/**
	 * 1ラウンドの制限時間が経過したかどうかを判定する.<br>
	 * Training modeのときは, Integerの最大との比較を行う.
	 *
	 * @return {@code true}: 1ラウンドの制限時間が経過した， {@code false}: otherwise
	 */
	private boolean isTimeOver() {
		if (FlagSetting.trainingModeFlag) {
			return this.nowFrame == Integer.MAX_VALUE;
		} else {
			return this.nowFrame >= GameSetting.ROUND_FRAME_NUMBER - 1;
		}

	}

	/**
	 * リプレイファイルを作成し, 使用キャラクターを表すインデックスなどのヘッダ情報を記述する.
	 */
	private void openReplayFile() {
		String fileName = LogWriter.getInstance().createOutputFileName("./log/replay/", this.timeInfo);
		this.dos = ResourceLoader.getInstance().openDataOutputStream(fileName + ".dat");

		LogWriter.getInstance().writeHeader(this.dos);
	}

	@Override
	public void close() {
		this.fighting.close();
		this.fighting = null;
		this.frameData = null;
		this.screenData = null;
		this.keyData = null;
		this.roundResults.clear();
		
		InputManager.getInstance().closeAI();

		if (FlagSetting.debugActionFlag) {
			DebugActionData.getInstance().closeAllWriters();
		}

		try {
			if (this.dos != null) {
				this.dos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (FlagSetting.jsonFlag) {
			LogWriter.getInstance().finalizeJson();
		}
	}
}
