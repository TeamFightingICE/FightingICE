package manager;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.util.WaveData;

/**
 * サウンドを管理するマネージャークラス．
 */
public class SoundManager {

	/**
	 * 音声ファイルのクローズを行ったかどうかのフラグ．
	 */
	private boolean closeFlag;

	/**
	 * 音源の位置．
	 */
	private float[] sourcePos;

	/**
	 * 音源の速度．
	 */
	private float[] sourceVel;

	/**
	 * リスナーの位置．
	 */
	private float[] listenerPos;

	/**
	 * リスナーの速度．
	 */
	private float[] listenerVel;

	/**
	 * リスナーの向き．
	 */
	private float[] listenerOri;

	/**
	 * 読み込み済みの音声ファイル名を格納するリスト．
	 */
	private ArrayList<String> loadedFiles;

	/**
	 * 音声バッファを格納するリスト．
	 */
	private ArrayList<Integer> buffers;

	/**
	 * 音源を格納するリスト．
	 */
	private ArrayList<Integer> sources;

	/**
	 * OpenALに使われる音声デバイス．
	 */
	private long device;

	/**
	 * OpenALの音声処理コンテキスト．
	 */
	private long context;

	/**
	 * サウンドエフェクトを格納するマップ．
	 */
	private Map<String, Integer> soundEffect;

	/**
	 * BGM．
	 */
	private Integer backGroundMusic;

	/**
	 * クラスコンストラクタ．
	 */
	private SoundManager() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + SoundManager.class.getName());

		this.loadedFiles = new ArrayList<String>();
		this.buffers = new ArrayList<Integer>();
		this.sources = new ArrayList<Integer>();

		// 音源とリスナーのデフォルトパラメータをセット
		this.sourcePos = new float[] { 0.0F, 0.0F, 0.0F };
		this.sourceVel = new float[] { 0.0F, 0.0F, 0.0F };
		this.listenerPos = new float[] { 0.0F, 0.0F, 0.0F };
		this.listenerVel = new float[] { 0.0F, 0.0F, 0.0F };
		// 向き(0, 0, -1), 上方向(0, 1, 0)
		this.listenerOri = new float[] { 0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F };

		// 解放確認フラグ
		this.closeFlag = false;

		this.soundEffect = new HashMap<String, Integer>();

		this.initialize();
	}

	/**
	 * SoundManagerクラスの唯一のインスタンスを取得する．
	 *
	 * @return SoundManagerクラスの唯一のインスタンス
	 */
	public static SoundManager getInstance() {
		return SoundManagerHolder.instance;
	}

	/**
	 * getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス．
	 */
	private static class SoundManagerHolder {
		private static final SoundManager instance = new SoundManager();
	}

	/**
	 * OpenALの準備を行う．
	 */
	private void initialize() {
		// OpenALのデフォルトデバイスに接続する
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		this.device = alcOpenDevice(defaultDeviceName);

		// 必要な制御情報を作成
		this.context = alcCreateContext(this.device, (IntBuffer) null);
		alcMakeContextCurrent(this.context);
		ALCCapabilities deviceCaps = ALC.createCapabilities(this.device);
		AL.createCapabilities(deviceCaps);

		this.setListenerValues();
	}

	/**
	 * リスナーのパラメータ(Position, Velocity, Orientation)を設定する．
	 */
	private void setListenerValues() {
		alListenerfv(AL_POSITION, this.listenerPos);
		alListenerfv(AL_VELOCITY, this.listenerVel);
		alListenerfv(AL_ORIENTATION, this.listenerOri);
	}

	/**
	 * 音声の読み込みとパラメータの設定を行い，再生準備済みの音源を返す．<br>
	 * 音声バッファを取得して，生成した音源にセットし，ピッチ・ゲインなどのパラメータを設定した後，設定済みの音源を返す．
	 *
	 * @param filePath
	 *            音声のファイルパス
	 * @param loop
	 *            ループさせるかどうか(させる場合はtrue)
	 *
	 * @return 設定済みの音源
	 */
	public int loadSoundResource(String filePath, boolean loop) {
		// 音声バッファの取得
		int buffer = this.getLoadedALBuffer(filePath);

		// 音源バッファを生成
		IntBuffer source = BufferUtils.createIntBuffer(1);

		// 音源の生成
		alGenSources(source);

		// 音源のパラメータ設定
		alSourcei(source.get(0), AL_BUFFER, buffer);
		alSourcef(source.get(0), AL_PITCH, 1.0F);
		alSourcef(source.get(0), AL_GAIN, 1.0F);
		alSource3f(source.get(0), AL_POSITION, this.sourcePos[0], this.sourcePos[1], this.sourcePos[2]);
		alSource3f(source.get(0), AL_VELOCITY, this.sourceVel[0], this.sourceVel[1], this.sourceVel[2]);

		// ループ設定
		alSourcei(source.get(0), AL_LOOPING, loop ? 1 : 0);

		// 音源リストに追加
		this.sources.add(new Integer(source.get(0)));

		return source.get(0);
	}

	/**
	 * 音声バッファを取得する．<br>
	 * 新たに音声をバッファに取り込み、読み込み済みファイルのリストに登録した後に音声バッファを返す．<br>
	 * 既に読み込んでいたファイルの場合新たに取り込まずに返す．
	 *
	 * @param filePath
	 *            音声のファイルパス
	 *
	 * @return 音声バッファ
	 */
	private int getLoadedALBuffer(String filePath) {
		int buffer;

		// 読み込み済みのファイルかどうかチェック
		for (int count = 0; count < this.loadedFiles.size(); count++) {
			if (((String) this.loadedFiles.get(count)).equals(filePath)) {
				return ((Integer) this.buffers.get(count)).intValue();
			}
		}

		// 音声バッファを取得
		buffer = this.registerSound(filePath);

		// バッファリストに追加
		this.buffers.add(new Integer(buffer));

		// 読み込み済みファイルのリストに追加
		this.loadedFiles.add(filePath);

		return buffer;
	}

	/**
	 * Wav音声ファイルを読み込んでバッファに取り込み，音声バッファを返す．
	 *
	 * @param filePath
	 *            音声のファイルパス
	 *
	 * @return 音声バッファ
	 */
	private int registerSound(String filePath) {
		// バッファを生成
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		alGenBuffers(buffer);

		// Wav音声ファイルをバッファに取り込む
		try {
			BufferedInputStream e = new BufferedInputStream(new FileInputStream(new File(filePath)));
			WaveData waveFile = WaveData.create(e);
			alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
			e.close();
			waveFile.dispose();
		} catch (FileNotFoundException arg1) {
			arg1.printStackTrace();
		} catch (IOException arg2) {
			arg2.printStackTrace();
		}

		return buffer.get(0);
	}

	/**
	 * 引数で指定された音源を再生する．
	 *
	 * @param source
	 *            音源
	 */
	public void play(int source) {
		alSourcePlay(source);
	}

	/**
	 * 引数で指定された音源を停止する．
	 *
	 * @param source
	 *            音源
	 */
	public void stop(int source) {
		alSourceStop(source);
	}

	/**
	 * 音声ファイルをクローズする．
	 */
	public void close() {
		if (!this.closeFlag) {
			IntBuffer scratch = BufferUtils.createIntBuffer(1);

			// 再生中の音声を停止して削除
			for (Integer sce : this.sources) {
				this.stop(sce.intValue());
				scratch.put(0, sce);
				alDeleteSources(scratch);
			}

			// バッファ削除
			for (Integer buf : this.buffers) {
				scratch.put(0, buf);
				alDeleteBuffers(scratch);
			}

			// 読み込み済み音声・バッファ・音源のリストを空にする
			this.loadedFiles.clear();
			this.buffers.clear();
			this.sources.clear();

			// コンテキスト削除とデバイスのクローズ
			alcDestroyContext(this.context);
			alcCloseDevice(this.device);

			this.closeFlag = true;
		}
	}

	/**
	 * サウンドエフェクトを格納したマップを取得する．
	 *
	 * @return サウンドエフェクトを格納したマップ
	 */
	public Map<String, Integer> getSoundEffect() {
		return this.soundEffect;
	}

	/**
	 * BGMを取得する．
	 *
	 * @return back ground music
	 */
	public Integer getBackGroundMusic() {
		return this.backGroundMusic;
	}

	/**
	 * 引数の音源をBGMとしてセットする．
	 *
	 * @param source
	 *            音源
	 */
	public void setBackGroundMusic(int source) {
		this.backGroundMusic = source;
	}
}
