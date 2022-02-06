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
import org.lwjgl.openal.*;
import org.lwjgl.util.WaveData;
import setting.FlagSetting;
import setting.GameSetting;

/**
 * サウンドを管睆㝙るマポージャークラス．
 */
public class SoundManager {

    /**
     * 音声ファイル㝮クローズを行㝣㝟㝋㝩㝆㝋㝮フラグ．
     */
    private boolean closeFlag;

    /**
     * 音溝㝮佝置．
     */
    private float[] sourcePos;

    /**
     * 音溝㝮速度．
     */
    private float[] sourceVel;

    /**
     * リスナー㝮佝置．
     */
    private float[] listenerPos;

    /**
     * リスナー㝮速度．
     */
    private float[] listenerVel;

    /**
     * リスナー㝮坑㝝．
     */
    private float[] listenerOri;

    /**
     * 読㝿込㝿済㝿㝮音声ファイル坝を格紝㝙るリスト．
     */
    private ArrayList<String> loadedFiles;

    /**
     * 音声ポッファを格紝㝙るリスト．
     */
    private ArrayList<Integer> buffers;

    /**
     * 音溝を格紝㝙るリスト．
     */
    private ArrayList<Integer> sources;

    /**
     * OpenAL㝫使ゝれる音声デポイス．
     */
    private long device;

    /**
     * OpenAL㝮音声処睆コンテキスト．
     */
    private long context;

    /**
     * サウンドエフェクトを格紝㝙るマップ．
     */
    private Map<String, Integer> soundEffect;

    /**
     * BGM．
     */
    private Integer backGroundMusic;

    /**
     * Sound rendering class
     */
    SoundRenderer soundRenderer;

    /**
     * クラスコンストラクタ．
     */
    private SoundManager() {
        Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + SoundManager.class.getName());

        this.loadedFiles = new ArrayList<String>();
        this.buffers = new ArrayList<Integer>();
        this.sources = new ArrayList<Integer>();

        // 音溝㝨リスナー㝮デフォルトパラメータをセット
        this.sourcePos = new float[]{0.0F, 0.0F, 0.0F};
        this.sourceVel = new float[]{0.0F, 0.0F, 0.0F};
        this.listenerPos = new float[]{350F, 0.0F, 0.0F};
        this.listenerVel = new float[]{0.0F, 0.0F, 0.0F};
        // 坑㝝(0, 0, -1), 上方坑(0, 1, 0)
        this.listenerOri = new float[]{0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F};

        // 解放確誝フラグ
        this.closeFlag = false;

        this.soundEffect = new HashMap<String, Integer>();

        this.initialize();
    }

    /**
     * SoundManagerクラス㝮唯一㝮インスタンスを坖得㝙る．
     *
     * @return SoundManagerクラス㝮唯一㝮インスタンス
     */
    public static SoundManager getInstance() {
        return SoundManagerHolder.instance;
    }

    /**
     * getInstance()㝌呼㝰れ㝟㝨㝝㝫初ゝ㝦インスタンスを生戝㝙るホルダークラス．
     */
    private static class SoundManagerHolder {
        private static final SoundManager instance = new SoundManager();
    }

    /**
     * OpenAL㝮準備を行㝆．
     */
    private void initialize() {
        // OpenAL㝮デフォルトデポイス㝫接続㝙る
        ALCCapabilities deviceCaps = null;
        if (!FlagSetting.soundPlay) {
            String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
            this.device = alcOpenDevice(defaultDeviceName);

            // 必覝㝪制御情報を作戝
            this.context = alcCreateContext(this.device, (IntBuffer) null);
            alcMakeContextCurrent(this.context);
            deviceCaps = ALC.createCapabilities(this.device);
            AL.createCapabilities(deviceCaps);
        } else {
            this.soundRenderer = new SoundRenderer(this);

        }

        this.setListenerValues();
    }

    /**
     * リスナー㝮パラメータ(Position, Velocity, Orientation)を設定㝙る．
     */
    private void setListenerValues() {
        alListenerfv(AL_POSITION, this.listenerPos);
        alListenerfv(AL_VELOCITY, this.listenerVel);
        alListenerfv(AL_ORIENTATION, this.listenerOri);
    }

    /**
     * 音声㝮読㝿込㝿㝨パラメータ㝮設定を行㝄，冝生準備済㝿㝮音溝を返㝙．<br>
     * 音声ポッファを坖得㝗㝦，生戝㝗㝟音溝㝫セット㝗，ピッポ・ゲイン㝪㝩㝮パラメータを設定㝗㝟後，設定済㝿㝮音溝を返㝙．
     *
     * @param filePath 音声㝮ファイルパス
     * @param loop     ループ㝕㝛る㝋㝩㝆㝋(㝕㝛る場坈㝯true)
     * @return 設定済㝿㝮音溝
     */
    public int loadSoundResource(String filePath, boolean loop) {
        // 音声ポッファ㝮坖得
        int buffer = this.getLoadedALBuffer(filePath);

        // 音溝ポッファを生戝
        //IntBuffer source = BufferUtils.createIntBuffer(1);

        // 音溝㝮生戝
        //alGenSources(source);

        // 音溝㝮パラメータ設定
        //alSourcei(source.get(0), AL_BUFFER, buffer);
        //alSourcef(source.get(0), AL_PITCH, 1.0F);
        //alSourcef(source.get(0), AL_GAIN, 1.0F);
        //alSource3f(source.get(0), AL_POSITION, this.sourcePos[0], this.sourcePos[1], this.sourcePos[2]);
        //alSource3f(source.get(0), AL_VELOCITY, this.sourceVel[0], this.sourceVel[1], this.sourceVel[2]);
        //alSourcef(source.get(0), AL_ROLLOFF_FACTOR, 0.1F);
        // ループ設定
        //alSourcei(source.get(0), AL_LOOPING, loop ? 1 : 0);

        // 音溝リスト㝫追加
        //this.sources.add(new Integer(source.get(0)));

        //return source.get(0);
        return buffer;
    }

    public int CreateSource() {
        IntBuffer source1 = BufferUtils.createIntBuffer(1);

        // 音溝㝮生戝
        alGenSources(source1);

        alSourcef(source1.get(0), AL_ROLLOFF_FACTOR, 0.01F);
        this.sources.add(new Integer(source1.get(0)));

        return source1.get(0);
    }

    public void SourcePos(int source, int x, int y) {
        alSource3f(source, AL_POSITION, x, 0, 4);
    }

    /**
     * 音声ポッファを坖得㝙る．<br>
     * 新㝟㝫音声をポッファ㝫坖り込㝿〝読㝿込㝿済㝿ファイル㝮リスト㝫登録㝗㝟後㝫音声ポッファを返㝙．<br>
     * 既㝫読㝿込ん㝧㝄㝟ファイル㝮場坈新㝟㝫坖り込㝾㝚㝫返㝙．
     *
     * @param filePath 音声㝮ファイルパス
     * @return 音声ポッファ
     */
    private int getLoadedALBuffer(String filePath) {
        int buffer;

        // 読㝿込㝿済㝿㝮ファイル㝋㝩㝆㝋ポェック
        for (int count = 0; count < this.loadedFiles.size(); count++) {
            if (((String) this.loadedFiles.get(count)).equals(filePath)) {
                return ((Integer) this.buffers.get(count)).intValue();
            }
        }

        // 音声ポッファを坖得
        buffer = this.registerSound(filePath);

        // ポッファリスト㝫追加
        this.buffers.add(new Integer(buffer));

        // 読㝿込㝿済㝿ファイル㝮リスト㝫追加
        this.loadedFiles.add(filePath);

        return buffer;
    }

    /**
     * Wav音声ファイルを読㝿込ん㝧ポッファ㝫坖り込㝿，音声ポッファを返㝙．
     *
     * @param filePath 音声㝮ファイルパス
     * @return 音声ポッファ
     */
    private int registerSound(String filePath) {
        // ポッファを生戝
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        alGenBuffers(buffer);

        // Wav音声ファイルをポッファ㝫坖り込む
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
     * 引数㝧指定㝕れ㝟音溝を冝生㝙る．
     *
     * @param source 音溝
     */
    public void play(int source, int x, int y) {
        //alSourcef(source, AL_PITCH, 1.0F);
        //alSourcef(source, AL_GAIN, 1.0F);
        alSource3f(source, AL_POSITION, x, 0, 4);
        //alSource3f(source, AL_VELOCITY, this.sourceVel[0], this.sourceVel[1], this.sourceVel[2]);
        alSourcePlay(source);
    }

    public void play2(int source, int buffer, int x, int y, boolean loop) {
        //alSourcef(source, AL_PITCH, 1.0F);
        //alSourcef(source, AL_GAIN, 1.0F);
        alSourcei(source, AL_BUFFER, buffer);
        alSource3f(source, AL_POSITION, x, 0, 4);
        alSourcei(source, AL_LOOPING, loop ? 1 : 0);
        //alSource3f(source, AL_VELOCITY, this.sourceVel[0], this.sourceVel[1], this.sourceVel[2]);
        alSourcePlay(source);
    }

    public boolean isPlaying(int source) {
        boolean ans;

        if (alGetSourcei(source, AL_SOURCE_STATE) == AL_PLAYING) {
            ans = true;
        } else {
            ans = false;
        }

        return ans;
    }

    /**
     * 引数㝧指定㝕れ㝟音溝を坜止㝙る．
     *
     * @param source 音溝
     */
    public void stop(int source) {
        alSourceStop(source);
    }

    /**
     * 音声ファイルをクローズ㝙る．
     */
    public void close() {
        if (!this.closeFlag) {
            IntBuffer scratch = BufferUtils.createIntBuffer(1);

            // 冝生中㝮音声を坜止㝗㝦削除
            for (Integer sce : this.sources) {
                this.stop(sce.intValue());
                scratch.put(0, sce);
                alDeleteSources(scratch);
            }

            // ポッファ削除
            for (Integer buf : this.buffers) {
                scratch.put(0, buf);
                alDeleteBuffers(scratch);
            }

            // 読㝿込㝿済㝿音声・ポッファ・音溝㝮リストを空㝫㝙る
            this.loadedFiles.clear();
            this.buffers.clear();
            this.sources.clear();

            // コンテキスト削除㝨デポイス㝮クローズ
            alcDestroyContext(this.context);
            alcCloseDevice(this.device);

            this.closeFlag = true;
        }
    }

    /**
     * サウンドエフェクトを格紝㝗㝟マップを坖得㝙る．
     *
     * @return サウンドエフェクトを格紝㝗㝟マップ
     */
    public Map<String, Integer> getSoundEffect() {
        return this.soundEffect;
    }

    /**
     * BGMを坖得㝙る．
     *
     * @return back ground music
     */
    public Integer getBackGroundMusic() {
        return this.backGroundMusic;
    }

    /**
     * 引数㝮音溝をBGM㝨㝗㝦セット㝙る．
     *
     * @param source 音溝
     */
    public void setBackGroundMusic(int source) {
        this.backGroundMusic = source;
    }


    /**
     * Pause audio rendering (soft)
     */
    public void pauseSound() {
        SOFTPauseDevice.alcDevicePauseSOFT(this.device);
    }

    /**
     * Resume audio rendering (soft)
     */
    public void resumeSound() {
        SOFTPauseDevice.alcDeviceResumeSOFT(this.device);
    }

    public SoundRenderer getSoundRenderer() {
        return soundRenderer;
    }

    public class SoundRenderer {
        private long device;
        private long context;
        private ALCCapabilities deviceCaps;

        SoundRenderer(SoundManager manager) {
            // OpenAL SOFT
            this.device = SOFTLoopback.alcLoopbackOpenDeviceSOFT((CharSequence) null);
            this.context = ALC10.alcCreateContext(this.device, new int[]{
                    SOFTLoopback.ALC_FORMAT_TYPE_SOFT, SOFTLoopback.ALC_FLOAT_SOFT,
                    SOFTLoopback.ALC_FORMAT_CHANNELS_SOFT, SOFTLoopback.ALC_STEREO_SOFT,
                    ALC10.ALC_FREQUENCY, GameSetting.SOUND_SAMPLING_RATE,
                    0
            });
            alcMakeContextCurrent(this.context);
            deviceCaps = ALC.createCapabilities(this.device);
            AL.createCapabilities(deviceCaps);
            manager.device = this.device;
            manager.context = this.context;
        }

        /**
         * Sample audio data
         *
         * @return rendered raw data in 2-channel format
         */
        public float[][] sampleAudio() {
            float[] rawData = new float[GameSetting.SOUND_BUFFER_SIZE * 2];
            SOFTLoopback.alcRenderSamplesSOFT(this.device, rawData, GameSetting.SOUND_RENDER_SIZE);
            float[][] separatedBuffer = new float[2][];
            float[] leftBuffer = new float[GameSetting.SOUND_BUFFER_SIZE];
            float[] rightBuffer = new float[GameSetting.SOUND_BUFFER_SIZE];
            for (int i = 0; i < GameSetting.SOUND_RENDER_SIZE; i++) {
                leftBuffer[i] = rawData[i * 2];
                rightBuffer[i] = rawData[i * 2 + 1];
            }
            separatedBuffer[0] = leftBuffer;
            separatedBuffer[1] = rightBuffer;
            return separatedBuffer;
        }
    }
}
