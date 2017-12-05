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

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.util.WaveData;

/** シングルトンパターン サウンドマネージャークラス */
public class SoundManager {

	private boolean closeFlag;

	private float[] sourcePos;

	private float[] sourceVel;

	private float[] listenerPos;

	private float[] listenerVel;

	private float[] listenerOri;

	private ArrayList<String> loadedFiles;

	private ArrayList<Integer> buffers;

	private ArrayList<Integer> sources;

	private long device;

	private long context;

	private Map<String, Integer> soundEffect;

	private Integer backGroundMusic;

	/** コンストラクタ */
	private SoundManager() {
		System.out.println("create instance: " + SoundManager.class.getName());

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
	 * このクラスの唯一のインスタンスを返すgetterメソッド．
	 *
	 * @return このクラスの唯一のインスタンス
	 */
	public static SoundManager getInstance() {
		return SoundManagerHolder.instance;
	}

	/** getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス． */
	private static class SoundManagerHolder {
		private static final SoundManager instance = new SoundManager();
	}

	/** OpenAL関連の準備を行うメソッド． */
	private void initialize() {
		// OpenALのデフォルトデバイスに接続する
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		this.device = alcOpenDevice(defaultDeviceName);

		// 必要な制御情報を作成
		this.context = alcCreateContext(device, (IntBuffer) null);
		alcMakeContextCurrent(context);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		AL.createCapabilities(deviceCaps);

		this.setListenerValues();
	}

	/** リスナーのパラメータ(Position, Velocity, Orientation)を設定するメソッド． */
	private void setListenerValues() {
		alListenerfv(AL_POSITION, this.listenerPos);
		alListenerfv(AL_VELOCITY, this.listenerVel);
		alListenerfv(AL_ORIENTATION, this.listenerOri);
	}

	/**
	 * 音声の読み込みとパラメータの設定を行い，再生準備済みの音源を返すメソッド．<br>
	 * 音声バッファを取得して，生成した音源にセットし，ピッチ・ゲインなどのパラメータを設定した後，設定済みの音源を返す．
	 *
	 * @param filePath 音声のファイルパス
	 * @param loop ループさせるかどうか(させる場合はtrue)
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
	 * 音声バッファを取得するgetterメソッド．<br>
	 * 新たに音声をバッファに取り込み、読み込み済みファイルのリストに登録した後に返す．
	 * 既に読み込んでいたファイルの場合新たに取り込まずに返す．
	 *
	 * @param filePath 音声のファイルパス
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
	 * Wav音声ファイルを読み込んでバッファに取り込み，音声バッファを返すメソッド．
	 *
	 * @param filePath 音声のファイルパス
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

	/** 引数で指定された音源を再生 */
	public void play(int source) {
		alSourcePlay(source);
	}

	/** 引数で指定された音源を停止 */
	public void stop(int source) {
		alSourceStop(source);
	}

	/** 音声ファイルクローズ 終了時は必ず行う */
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
			alcDestroyContext(context);
			alcCloseDevice(device);

			this.closeFlag = true;
		}
	}

	/**
	 * SEを格納したマップを取得するgetterメソッド．
	 *
	 * @return SEを格納したマップ
	 */
	public Map<String, Integer> getSoundEffect() {
		return this.soundEffect;
	}

	/**
	 * BGMを取得するgetterメソッド．
	 *
	 * @return BackGroundMusic
	 */
	public Integer getBackGroundMusic() {
		return this.backGroundMusic;
	}

	/**
	 * 引数の音源をBGMとしてセットするsetterメソッド．
	 *
	 * @param source
	 */
	public void setBackGroundMusic(int source) {
		this.backGroundMusic = source;
	}
}
