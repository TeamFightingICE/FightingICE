package gamescene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import enumerate.GameSceneName;
import fighting.Fighting;
import informationcontainer.RoundResult;
import input.KeyData;
import input.Keyboard;
import manager.GraphicManager;
import manager.InputManager;
import manager.SoundManager;
import service.SocketServer;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.AudioBuffer;
import struct.AudioData;
import struct.AudioSource;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.ScreenData;
import util.ResourceDrawer;

/**
 * リプレイの再生を行うクラス．
 */
public class Replay extends GameScene {

	/**
	 * 対戦処理を行うクラスのインスタンス．
	 */
	private Fighting fighting;

	/**
	 * Replayファイルからログを読み込むための入力ストリーム．
	 */
	protected DataInputStream dis;

	/**
	 * 現在のフレーム．
	 */
	private int nowFrame;

	private AudioSource audioSource;
	
	private AudioBuffer audioBuffer;

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
	
	private long roundStartTime;
	private long currentFrameTime;

	/**
	 * 対戦処理後のキャラクターデータなどのゲーム情報を格納したフレームデータ．
	 */
	protected FrameData frameData;

	/**
	 * 対戦処理後のゲーム画面の情報．
	 */
	private ScreenData screenData;
	
	private AudioData audioData;

	/**
	 * 対戦処理に用いるP1, P2の入力情報．
	 */
	private KeyData keyData;

	/**
	 * 再生速度を指定するインデックス．
	 */
	private int playSpeedIndex;

	/**
	 * 再生速度を管理する配列．
	 */
	private int[] playSpeedArray;

	/**
	 * ラウンドが終わったかどうかを表すフラグ．
	 */
	private boolean isFinished;

	/**
	 * クラスコンストラクタ．<br>
	 * 読み込むReplayファイルをopenする.
	 */
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

		this.fighting = new Fighting();
		this.fighting.initialize();

		this.nowFrame = 0;
		this.elapsedBreakTime = 0;
		this.currentRound = 1;
		this.roundStartFlag = true;
		this.roundStartTime = 0;
		this.currentFrameTime = 0;

		this.frameData = new FrameData();
		this.screenData = new ScreenData();
		this.audioData = new AudioData();
		this.keyData = new KeyData();
		this.playSpeedIndex = 1;
		this.playSpeedArray = new int[] { 0, 1, 2, 4 };
		this.isFinished = false;
		
		this.audioSource = SoundManager.getInstance().createAudioSource();
		this.audioBuffer = SoundManager.getInstance().createAudioBuffer();

		GameData gameData = new GameData(this.fighting.getCharacters());
		SocketServer.getInstance().initialize(gameData);
		
		try {
			String replayPath = "./log/replay/" + LaunchSetting.replayName + ".dat";
			this.dis = new DataInputStream(new FileInputStream(new File(replayPath)));
			
			readHeader();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				// 再生速度を更新する
				updatePlaySpeed();

				// processing
				for (int i = 0; i < this.playSpeedArray[this.playSpeedIndex] && !this.isFinished; i++) {
					processingGame();
					// 体力が0orタイムオーバーならラウンド終了処理
					if (isBeaten() || isTimeOver()) {
						processingRoundEnd();
						this.nowFrame++;
						break;
					}
					this.nowFrame++;
				}
				
				if (!this.isFinished) {
					// 画面をDrawerクラスで描画
					ResourceDrawer.getInstance().drawResource(this.fighting.getCharacters(),
							this.fighting.getProjectileDeque(), this.fighting.getHitEffectList(),
							this.frameData.getRemainingTimeMilliseconds(), this.currentRound);

					GraphicManager.getInstance().drawString("PlaySpeed:" + this.playSpeedArray[this.playSpeedIndex], 50,
							550);

					this.screenData = new ScreenData();
				}
			}

		} else {
			this.processingGameEnd();
			transitionProcess();
		}

		if (Keyboard.getKeyDown(GLFW_KEY_ESCAPE)) {
			// BGMを止める
			SoundManager.getInstance().stop(audioSource);
			transitionProcess();
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

	/**
	 * 各ラウンド開始時における, インターバル処理を行う．
	 */
	private void processingBreakTime() {
		GraphicManager.getInstance().drawQuad(0, 0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, 0, 0, 0, 0);
		GraphicManager.getInstance().drawString("Waiting for Round Start", 350, 200);
		
		this.roundStartTime = System.currentTimeMillis();
	}

	/**
	 * 対戦処理を行う.<br>
	 *
	 * 1. P1, P2の入力を受け取る.<br>
	 * 2. 対戦処理を行う.<br>
	 * 3. 対戦後のFrameDataを取得する.<br>
	 */
	private void processingGame() {
		this.currentFrameTime = System.currentTimeMillis();
		this.keyData = createKeyData();
		
		if (this.isFinished) return;

		this.fighting.processingFight(this.nowFrame, this.keyData);
		this.frameData = this.fighting.createFrameData(this.nowFrame, this.currentRound);
		
		if (this.nowFrame == 0) {
			this.audioData = new AudioData();
			
			SoundManager.getInstance().play2(this.audioSource, SoundManager.getInstance().getBackGroundMusicBuffer(), 350, 0, true);
			if (FlagSetting.enableReplaySound) {
				SoundManager.getInstance().play(this.audioSource, this.audioBuffer);
			}
		} else {
			this.audioData = InputManager.getInstance().getAudioData();
		}
		
		SocketServer.getInstance().processingGame(frameData, null, null);
		SoundManager.getInstance().playback(audioSource, audioData.getRawShortDataAsBytes());
	}

	/**
	 * 各ラウンド終了時の処理を行う.
	 */
	private void processingRoundEnd() {
		this.isFinished = true;
		this.currentRound++;
		this.roundStartFlag = true;
		
		Logger.getAnonymousLogger().log(Level.INFO, String.format("Round Duration: %.3f seconds (Expected %.3f)", 
				(double) (currentFrameTime - roundStartTime) / 1e3, (double) (this.frameData.getFramesNumber() + 1) / 60));
		
		RoundResult roundResult = new RoundResult(this.frameData);
		SocketServer.getInstance().roundEnd(roundResult);

		SoundManager.getInstance().stopAll();
		SoundManager.getInstance().stopPlayback(audioSource);
		
		if (FlagSetting.enableReplaySound) {
			this.audioSource.clearBuffer();
		}
	}
	
	private void processingGameEnd() {
		SocketServer.getInstance().gameEnd();
	}

	/**
	 * キャラクターが倒されたかどうかを判定する.
	 *
	 * @return {@code true} P1 or P2が倒された, {@code false} otherwise
	 */
	private boolean isBeaten() {
		return FlagSetting.limitHpFlag
				&& (this.frameData.getCharacter(true).getHp() <= 0 || this.frameData.getCharacter(false).getHp() <= 0);
	}

	/**
	 * 1ラウンドの制限時間が経過したかどうかを判定する.<br>
	 *
	 * @return {@code true} 1ラウンドの制限時間が経過した, {@code false} otherwise
	 */
	private boolean isTimeOver() {
		return this.nowFrame == GameSetting.ROUND_FRAME_NUMBER - 1;
	}
	
	/**
	 * 各ラウンド開始時に, 対戦情報や現在のフレームなどの初期化を行う．
	 */
	private void initRound() {
		this.fighting.initRound();
		this.nowFrame = 0;
		this.roundStartFlag = false;
		this.elapsedBreakTime = 0;
		this.isFinished = false;
		
		SocketServer.getInstance().initRound();
		
		if (FlagSetting.enableReplaySound) {
			String soundPath = "./log/sound/" + LaunchSetting.replayName + "_" + this.currentRound + ".wav";
			this.audioBuffer.registerSound(soundPath);
		}
	}

	/**
	 * 対戦処理に用いるP1, P2のキー入力データを作成する.<br>
	 *
	 * @return P1, P2のキー入力データ
	 */
	private KeyData createKeyData() {
		Key[] temp = new Key[2];

		for (int i = 0; i < 2; i++) {
			temp[i] = new Key();
			byte keyByte = 0;

			try {
				this.dis.readBoolean(); // front
				this.dis.readByte(); // remaingFrame
				this.dis.readByte(); // actionOrdinal
				this.dis.readInt(); // hp
				this.dis.readInt(); // energy
				this.dis.readInt(); // x
				this.dis.readInt(); // y
				keyByte = this.dis.readByte();
			} catch (EOFException e) {
				Logger.getAnonymousLogger().log(Level.INFO, "The replay file was finished in the middle");
				try {
					this.dis.close();
					this.isFinished = true;
				} catch (IOException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
				this.processingRoundEnd();
				this.processingGameEnd();
				transitionProcess();

				break;
			} catch (IOException e1) {
				e1.printStackTrace();
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
	 * int型変数をboolean型に変換する．
	 *
	 * @param i
	 *            変換したいint型の変数
	 *
	 * @return {@code true} 引数が1のとき, {@code false} otherwise
	 */
	private boolean convertItoB(int i) {
		return i == 1 ? true : false;
	}

	/**
	 * 使用キャラクターや最大HPといったヘッダ情報を読み込む．
	 */
	protected void readHeader() {
		for (int i = 0; i < 2; i++) {
			try {
				int checkMode = dis.readInt();

				// Checks whether fighting mode is limited HP mode or not
				// If it is HP mode, checkMode is less than 0 (e.g. -1)
				if (checkMode < 0) {
					LaunchSetting.maxHp[i] = dis.readInt();
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
	}

	/**
	 * Replayの再生速度を更新する．
	 */
	private void updatePlaySpeed() {
		Key key = InputManager.getInstance().getKeyData().getKeys()[0];

		if (key.U) {
			this.playSpeedIndex = ++this.playSpeedIndex % this.playSpeedArray.length;
		}
		if (key.D) {
			this.playSpeedIndex = (--this.playSpeedIndex + this.playSpeedArray.length) % this.playSpeedArray.length;
		}
	}

	/**
	 * 遷移先のシーンへの遷移処理を行う．
	 */
	private void transitionProcess() {
		HomeMenu homeMenu = new HomeMenu();
		this.setTransitionFlag(true);
		this.setNextGameScene(homeMenu);
	}

	/**
	 * フレームデータを取得する．
	 *
	 * @return フレームデータ
	 * @see FrameData
	 */
	public FrameData getFrameData() {
		return new FrameData(this.frameData);
	}

	/**
	 * ScreenDataを取得する．
	 *
	 * @return screen data
	 * @see ScreenData
	 */
	public ScreenData getScreenData() {
		return new ScreenData(this.screenData);
	}

}
