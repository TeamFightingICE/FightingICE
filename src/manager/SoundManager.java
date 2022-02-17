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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.WaveData;
import render.audio.SoundRender;
import setting.FlagSetting;
import struct.AudioBuffer;
import struct.AudioSource;

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
    private ArrayList<AudioBuffer> audioBuffers;

    /**
     * 音溝を格紝㝙るリスト．
     */
    private ArrayList<Integer> sources;
    private ArrayList<AudioSource> audioSources;

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
     * Sound redendering classes;
     */
    List<SoundRender> soundRenderers;
    SoundRender virtualRenderer;

    private Map<String, AudioBuffer> soundBuffer;
    private AudioBuffer backGroundMusicBuffer;

    /**
     * クラスコンストラクタ．
     */
    private SoundManager() {
        Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + SoundManager.class.getName());

        this.loadedFiles = new ArrayList<String>();
        this.buffers = new ArrayList<Integer>();
        this.sources = new ArrayList<Integer>();
        this.audioBuffers = new ArrayList<>();
        this.audioSources = new ArrayList<>();

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
        this.soundBuffer = new HashMap<>();

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
        // sound renderers
        this.soundRenderers = new ArrayList<>();
        if (!FlagSetting.fastModeFlag && !FlagSetting.muteFlag) {
            this.soundRenderers.add(SoundRender.createDefaultRenderer());
        }
        virtualRenderer = SoundRender.createVirtualRenderer();
        this.soundRenderers.add(virtualRenderer);
        this.setListenerValues();
    }

    /**
     * リスナー㝮パラメータ(Position, Velocity, Orientation)を設定㝙る．
     */
    private void setListenerValues(){
        for (SoundRender render: soundRenderers){
            render.alListenerfv(AL_POSITION, this.listenerPos);
            render.alListenerfv(AL_VELOCITY, this.listenerVel);
            render.alListenerfv(AL_ORIENTATION, this.listenerOri);
        }
    }

    /**
     * 音声㝮読㝿込㝿㝨パラメータ㝮設定を行㝄，冝生準備済㝿㝮音溝を返㝙．<br>
     * 音声ポッファを坖得㝗㝦，生戝㝗㝟音溝㝫セット㝗，ピッポ・ゲイン㝪㝩㝮パラメータを設定㝗㝟後，設定済㝿㝮音溝を返㝙．
     *
     * @param filePath 音声㝮ファイルパス
     * @param loop     ループ㝕㝛る㝋㝩㝆㝋(㝕㝛る場坈㝯true)
     * @return 設定済㝿㝮音溝
     */
    public AudioBuffer createAudioBuffer(String filePath, boolean loop){
        AudioBuffer audioBuffer = null;
        int[] bufferIds = new int[soundRenderers.size()];
        for (int i = 0; i < soundRenderers.size(); i++){
            soundRenderers.get(i).set();
            bufferIds[i] = this.registerSound(filePath);
        }
        audioBuffer = new AudioBuffer(bufferIds);
        this.audioBuffers.add(audioBuffer);
        return audioBuffer;
    }



    public int createSource() {
//        IntBuffer source1 = BufferUtils.createIntBuffer(1);

        // 音溝㝮生戝
        IntBuffer source1 = IntBuffer.wrap(new int[] {alGenSources()});

        alSourcef(source1.get(0), AL_ROLLOFF_FACTOR, 0.01F);
        this.sources.add(new Integer(source1.get(0)));

        return source1.get(0);
    }

    public AudioSource createAudioSource(){
        AudioSource audioSource = null;
        int[] sourceIds = new int[soundRenderers.size()];
        for (int i = 0; i < soundRenderers.size(); i++){
            soundRenderers.get(i).set();
            sourceIds[i] = createSource();
        }
        audioSource = new AudioSource(sourceIds);
        this.audioSources.add(audioSource);
        return audioSource;
    }

//    public void SourcePos(int source, int x, int y) {
//        alSource3f(source, AL_POSITION, x, 0, 4);
//    }

    public void SourcePos(AudioSource source, int x, int y){
        for(int i = 0; i < soundRenderers.size(); i++){
            soundRenderers.get(i).setSource3f(source.getSourceIds()[i], AL_POSITION, x, 0, 4);
        }
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

    public void play2(AudioSource source, AudioBuffer buffer, int x, int y, boolean loop){
        for (int i = 0; i < soundRenderers.size(); i++){
            int sourceId = source.getSourceIds()[i];
            int bufferId = buffer.getBuffers()[i];
            soundRenderers.get(i).play(sourceId, bufferId, x, y, loop);
        }
    }

    public boolean isPlaying(AudioSource source) {
        boolean ans = false;
        for(int i = 0; i < soundRenderers.size(); i++){
            ans = ans || soundRenderers.get(i).isPlaying(source.getSourceIds()[i]);
        }
        return ans;
    }

    /**
     * 引数㝧指定㝕れ㝟音溝を坜止㝙る．
     *
     * @param source 音溝
     */
//    public void stop(int source) {
//        alSourceStop(source);
//    }
    /**
     * 引数㝧指定㝕れ㝟音溝を坜止㝙る．
     *
     * @param audioSource 音溝
     */
    public void stop(AudioSource audioSource){
        for(int i = 0; i < soundRenderers.size(); i++){
            soundRenderers.get(i).stop(audioSource.getSourceIds()[i]);
        }
    }

    /**
     * 音声ファイルをクローズ㝙る．
     */
    public void close(){
        if(!this.closeFlag){
            for(AudioSource source: this.audioSources){
                this.stop(source);
                deleteSource(source);
            }
            for(AudioBuffer buffer: this.audioBuffers){
                deleteBuffer(buffer);
            }
            this.loadedFiles.clear();
            this.audioBuffers.clear();
            this.audioSources.clear();
            this.closeRenderers();
            this.closeFlag = true;
        }
    }

    public AudioBuffer getBackGroundMusicBuffer() {
        return backGroundMusicBuffer;
    }

    public void setBackGroundMusicBuffer(AudioBuffer buffer){
        this.backGroundMusicBuffer = buffer;
    }

    public List<SoundRender> getSoundRenderers() {
        return soundRenderers;
    }

    public Map<String, AudioBuffer> getSoundBuffers() {
        return soundBuffer;
    }

    public void deleteSource(AudioSource source){
        for(int i = 0; i < soundRenderers.size(); i++){
            int sourceId = source.getSourceIds()[i];
            soundRenderers.get(i).deleteSource(sourceId);
        }
    }

    public void deleteBuffer(AudioBuffer buffer){
        for(int i = 0; i < soundRenderers.size(); i++){
            int sourceId = buffer.getBuffers()[i];
            soundRenderers.get(i).deleteBuffer(sourceId);
        }
    }

    public void closeRenderers(){
        for(SoundRender render: soundRenderers){
            render.close();
        }
    }

    public SoundRender getVirtualRenderer() {
        return virtualRenderer;
    }

    public ArrayList<AudioSource> getAudioSources() {
        return audioSources;
    }
}
