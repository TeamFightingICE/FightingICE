package manager;

import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_ROLLOFF_FACTOR;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alSource3f;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourcef;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;

import render.audio.SoundRender;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
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
    // private float[] sourcePos;

    /**
     * 音溝㝮速度．
     */
    // private float[] sourceVel;
    
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
    private ArrayList<AudioBuffer> audioBuffers;

    /**
     * 音溝を格紝㝙るリスト．
     */
    private ArrayList<AudioSource> audioSources;

    /**
     * Sound rendering devices.
     */
    List<SoundRender> soundRenderers;
    /**
     *
     */
    SoundRender virtualRenderer;
    
    /**
     * Sound buffers.
     */
    private Map<String, AudioBuffer> soundBuffer;
    /**
     * Background music buffer.
     */
    private AudioBuffer backGroundMusicBuffer;

    /**
     * クラスコンストラクタ．
     */
    private SoundManager() {
        Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + SoundManager.class.getName());

        this.loadedFiles = new ArrayList<>();
        this.audioBuffers = new ArrayList<>();
        this.audioSources = new ArrayList<>();

        // 音溝㝨リスナー㝮デフォルトパラメータをセット
        // this.sourcePos = new float[]{0.0F, 0.0F, 0.0F};
        // this.sourceVel = new float[]{0.0F, 0.0F, 0.0F};
        this.listenerPos = new float[]{GameSetting.STAGE_WIDTH / 2.0F, 0.0F, GameSetting.STAGE_HEIGHT / 2.0F};
        this.listenerVel = new float[]{0.0F, 0.0F, 0.0F};
        // 坑㝝(0, 0, -1), 上方坑(0, 1, 0)
        this.listenerOri = new float[]{0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F};

        // 解放確誝フラグ
        this.closeFlag = false;

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
        
        if (LaunchSetting.isExpectedProcessingMode(LaunchSetting.HEADLESS_MODE)) {
        	virtualRenderer = SoundRender.createVirtualRenderer();
            this.soundRenderers.add(virtualRenderer);
            
            if (!FlagSetting.muteFlag) {
            	SoundRender defaultRenderer = SoundRender.createDefaultRenderer();
                this.soundRenderers.add(defaultRenderer);
            }

            this.setListenerValues();
        }
    }

    /**
     * リスナー㝮パラメータ(Position, Velocity, Orientation)を設定㝙る．
     */
    private void setListenerValues() {
        for (SoundRender render : soundRenderers) {
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
    public AudioBuffer createAudioBuffer(String filePath) {
        AudioBuffer audioBuffer = null;
        int[] bufferIds = new int[soundRenderers.size()];
        for (int i = 0; i < soundRenderers.size(); i++) {
            soundRenderers.get(i).set();
            bufferIds[i] = this.createBuffer();
        }
        audioBuffer = new AudioBuffer(bufferIds);
        audioBuffer.registerSound(filePath);
        this.audioBuffers.add(audioBuffer);
        return audioBuffer;
    }

    public AudioBuffer createAudioBuffer() {
    	AudioBuffer audioBuffer = null;
        int[] bufferIds = new int[soundRenderers.size()];
        for (int i = 0; i < soundRenderers.size(); i++) {
            soundRenderers.get(i).set();
            bufferIds[i] = this.createBuffer();
        }
        audioBuffer = new AudioBuffer(bufferIds);
        return audioBuffer;
    }

    /**
     * Create audio source at the current OpenAL context.
     *
     * @return a new source id.
     */
    private int createSource() {
        // 音溝㝮生戝
        IntBuffer source = BufferUtils.createIntBuffer(1);
        alGenSources(source);

        alSourcef(source.get(0), AL_ROLLOFF_FACTOR, 0.01F);

        return source.get(0);
    }

    /**
     * Creates a new audio source.
     *
     * @return a new audio source.
     */
    public AudioSource createAudioSource() {
        AudioSource audioSource = null;
        int[] sourceIds = new int[soundRenderers.size()];
        for (int i = 0; i < soundRenderers.size(); i++) {
            soundRenderers.get(i).set();
            sourceIds[i] = this.createSource();
        }
        audioSource = new AudioSource(sourceIds);
        this.audioSources.add(audioSource);
        return audioSource;
    }

    /**
     * Sets position for audio source.
     *
     * @param source audio source
     * @param x      X position.
     * @param y      Y position.
     */
    public void setSourcePos(AudioSource source, int x, int y) {
        for (int i = 0; i < soundRenderers.size(); i++) {
            soundRenderers.get(i).setSource3f(source.getSourceIds()[i], AL_POSITION, x, 0, y);
        }
    }
    
    private int createBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        alGenBuffers(buffer);

        return buffer.get(0);
    }
    
    public void playback(AudioSource source, byte[] audioSample) {
    	if (!FlagSetting.enableAudioPlayback) return;
    	
        for (int i = 0; i < soundRenderers.size(); i++) {
        	if (soundRenderers.get(i) == virtualRenderer) continue;
        	
            int sourceId = source.getSourceIds()[i];
            soundRenderers.get(i).playback(sourceId, audioSample);
        }
    }
    
    public void stopPlayback(AudioSource source) {
    	if (!FlagSetting.enableAudioPlayback) return;
    	
        for (int i = 0; i < soundRenderers.size(); i++) {
        	if (soundRenderers.get(i) == virtualRenderer) continue;
        	
            int sourceId = source.getSourceIds()[i];
            soundRenderers.get(i).stopPlayback(sourceId);
        }
    }

    /**
     * 引数㝧指定㝕れ㝟音溝を冝生㝙る．
     *
     * @param source 音溝
     * @deprecated
     */
    public void play(int source, int x, int y) {
        alSource3f(source, AL_POSITION, x, 0, 4);
        alSourcePlay(source);
    }
    
    public void play(AudioSource source, AudioBuffer buffer) {
    	for (int i = 0; i < soundRenderers.size(); i++) {
            int sourceId = source.getSourceIds()[i];
            int bufferId = buffer.getBuffers()[i];
            soundRenderers.get(i).play(sourceId, bufferId);
        }
    }

    /**
     * Play audio in a source.
     *
     * @param source audio source.
     * @param buffer audio buffer.
     * @param x      X position.
     * @param y      Y position.
     * @param loop   looping.
     */
    public void play2(AudioSource source, AudioBuffer buffer, int x, int y, boolean loop) {
    	if (!FlagSetting.enableBuiltinSound) return;
    	
        for (int i = 0; i < soundRenderers.size(); i++) {
            int sourceId = source.getSourceIds()[i];
            int bufferId = buffer.getBuffers()[i];
            soundRenderers.get(i).play(sourceId, bufferId, x, y, loop);
        }
    }

    /**
     * Checks if the current audio source is playing.
     *
     * @param source the current source.
     * @return playing status.
     */
    public boolean isPlaying(AudioSource source) {
        boolean ans = false;
        for (int i = 0; i < soundRenderers.size(); i++) {
            ans = ans || soundRenderers.get(i).isPlaying(source.getSourceIds()[i]);
        }
        return ans;
    }

    /**
     * 引数㝧指定㝕れ㝟音溝を坜止㝙る．
     *
     * @param audioSource 音溝
     */
    public void stop(AudioSource audioSource) {
        for (int i = 0; i < soundRenderers.size(); i++) {
            soundRenderers.get(i).stop(audioSource.getSourceIds()[i]);
        }
    }
    
    public void stopAll() {
    	for (AudioSource audioSource: getAudioSources())
			stop(audioSource);
    }

    public AudioBuffer getBackGroundMusicBuffer() {
        return backGroundMusicBuffer;
    }
    
    /**
     * Set the gain of a source. For changing the volume of the source
     * @param source
     * @param val
     */
    public void setSourceGain(AudioSource source, float gain) {
		for (int i = 0; i < soundRenderers.size(); i++) {
	        int sourceId = source.getSourceIds()[i];
	        soundRenderers.get(i).setSourceGain(sourceId, Math.min(1.0f, Math.max(0.0f, gain)));
	    }
    }

    /**
     * Sets background music buffer.
     *
     * @param buffer background music buffer.
     */
    public void setBackGroundMusicBuffer(AudioBuffer buffer) {
        this.backGroundMusicBuffer = buffer;
    }

    /**
     * Gets all sound renderers
     *
     * @return all sound renderers
     */
    public List<SoundRender> getSoundRenderers() {
        return soundRenderers;
    }

    /**
     * Gets sound buffers.
     *
     * @return sound buffers.
     */
    public Map<String, AudioBuffer> getSoundBuffers() {
        return soundBuffer;
    }
    
    public AudioBuffer getSoundBuffer(String soundName) {
    	return soundBuffer.getOrDefault(soundName, null);
    }

    /**
     * Delete an audio source.
     *
     * @param source audio source.
     */
    public void deleteSource(AudioSource source) {
        for (int i = 0; i < soundRenderers.size(); i++) {
            int sourceId = source.getSourceIds()[i];
            soundRenderers.get(i).deleteSource(sourceId);
        }
    }

    /**
     * Deletes an audio buffer.
     *
     * @param buffer audio buffer.
     */
    public void deleteBuffer(AudioBuffer buffer) {
        for (int i = 0; i < soundRenderers.size(); i++) {
            int sourceId = buffer.getBuffers()[i];
            soundRenderers.get(i).deleteBuffer(sourceId);
        }
    }

    /**
     * Closes all audio renderers.
     */
    public void closeRenderers() {
        for (SoundRender render : soundRenderers) {
            render.close();
        }
    }

    /**
     * Gets the virtual sound renderer.
     *
     * @return virtual sound render.
     */
    public SoundRender getVirtualRenderer() {
        return virtualRenderer;
    }

    /**
     * Get audio sources.
     *
     * @return audio sources.
     */
    public ArrayList<AudioSource> getAudioSources() {
        return audioSources;
    }
    
    public void closeSources() {
    	for (AudioSource source: getAudioSources()) {
    		source.close();
    	}
    }

    /**
     * 音声ファイルをクローズ㝙る．
     */
    public void close() {
        if (!this.closeFlag) {
            for (AudioSource source : this.audioSources) {
                this.stop(source);
                deleteSource(source);
            }
            for (AudioBuffer buffer : this.audioBuffers) {
                deleteBuffer(buffer);
            }
            this.loadedFiles.clear();
            this.audioBuffers.clear();
            this.audioSources.clear();
            this.closeRenderers();
            this.closeSources();
            this.closeFlag = true;
        }
    }
}
