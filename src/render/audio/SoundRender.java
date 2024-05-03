package render.audio;

import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.AL_TRUE;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGetSourcef;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSource3f;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcei;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.SOFTLoopback;

import setting.GameSetting;

/**
 * The class dealing with sound rendering in the game using the default speaker or a virtual device.
 */
public class SoundRender {
    /**
     * Device's specifier in OpenAL.
     */
    long device;
    /**
     * Context's specifier in OpenAL.
     */
    long context;
    /**
     * The capabilities of the OpenAL Context API.
     */
    ALCCapabilities deviceCaps;

    public SoundRender(long device, long context, ALCCapabilities deviceCaps) {
        this.device = device;
        this.context = context;
        this.deviceCaps = deviceCaps;
    }
    /**
     * Return the OpenAL's default speaker device.
     * @return OpenAL's default speaker device.
     */
    public static SoundRender createDefaultRenderer() {
        long device, context;
        ALCCapabilities deviceCaps;
        String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        device = ALC10.alcOpenDevice(defaultDeviceName);
        context = ALC10.alcCreateContext(device, (IntBuffer) null);
        ALC10.alcMakeContextCurrent(context);
        deviceCaps = ALC.createCapabilities(device);
        AL.createCapabilities(deviceCaps);
        return new SoundRender(device, context, deviceCaps);
    }

    /**
     * Return the OpenAL's virtual speaker device, this virtual device allows saving sound to a buffer so that it can be
     * used for further processing
     * @return OpenAL's virtual speaker device
     */
    public static SoundRender createVirtualRenderer() {
        long device, context;
        device = SOFTLoopback.alcLoopbackOpenDeviceSOFT((ByteBuffer) null);
        context = ALC10.alcCreateContext(device, new int[]{
                SOFTLoopback.ALC_FORMAT_TYPE_SOFT, SOFTLoopback.ALC_FLOAT_SOFT,
                SOFTLoopback.ALC_FORMAT_CHANNELS_SOFT, SOFTLoopback.ALC_STEREO_SOFT,
                ALC10.ALC_FREQUENCY, GameSetting.SOUND_SAMPLING_RATE,
                0
        });
        ALC10.alcMakeContextCurrent(context);
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        AL.createCapabilities(deviceCaps);
        return new SoundRender(device, context, deviceCaps);
    }

    /**
     * Sets device as the current device.
     *
     * This function must be called before every OpenAL's processing.
     */
    public void set() {
        ALC10.alcMakeContextCurrent(this.context);
    }

    /**
     * Plays sound.
     * @param sourceId audio source
     * @param bufferId audio buffer
     */
    public void play(int sourceId, int bufferId) {
        set();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
        AL10.alSourcePlay(sourceId);
    }

    /**
     * Stops sound.
     * @param sourceId audio source
     */
    public void stop(int sourceId) {
        set();
        if(isPlaying(sourceId)) {
            AL10.alSourceStop(sourceId);
        }
    }

    /**
     * Plays sound with coordinates and looping.
     * @param sourceId sourceId
     * @param bufferId sourceId
     * @param x X position
     * @param y Y position
     * @param loop looping
     */
    public void play(int sourceId, int bufferId, int x, int y, boolean loop) {
        set();
        if(isPlaying(sourceId))
            stop(sourceId);
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSource3f(sourceId, AL_POSITION, x, 0, 4);
        alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
        AL10.alSourcePlay(sourceId);
    }

    /**
     * Sets default listener data.
     */
    public void setListenerData() {
        set();
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }
    
    public float getSourceGain(int sourceId) {
    	set();
    	return alGetSourcef(sourceId, AL10.AL_GAIN);
    }
    
    public void setSourceGain(int sourceId, float gain) {
    	set();
    	alSourcef(sourceId, AL10.AL_GAIN, gain);
    }

    public long getDevice() {
        return device;
    }

    /**
     * Sets a source property requiring three floating point values.<br>
     * This function is a wrapper of OpenAL's alSource3f
     *
     * @param source audio source
     * @param param the name of the attribute to set
     * @param x v1 value
     * @param y v2 value
     * @param z v3 value
     */
    public void setSource3f(int source, int param, float x, float y, float z) {
        set();
        alSource3f(source, param, x, y, z);
    }

    /**
     * Deletes one source.
     *
     * This function is a wrapper of OpenAL's alDeleteSources
     * @param source audio source
     */
    public void deleteSource(int source) {
        set();
        alDeleteSources(source);
    }

    /**
     * Deletes one buffer.
     *
     * This function is a wrapper of OpenAL's alDeleteBuffers
     * @param buffer audio buffer
     */
    public void deleteBuffer(int buffer) {
        set();
        alDeleteBuffers(buffer);
    }

    /**
     * Closes the device
     */
    public void close() {
        set();
        ALC10.alcDestroyContext(this.context);
        ALC10.alcCloseDevice(this.device);
    }

    /**
     * Checks if the current source is playing
     * @param source audio source
     * @return playing status
     */
    public boolean isPlaying(int source) {
        set();
        boolean ans;

        if (alGetSourcei(source, AL_SOURCE_STATE) == AL_PLAYING) {
            ans = true;
        } else {
            ans = false;
        }
        return ans;
    }

    /**
     * Sets a floating point-vector property of the listener.
     *
     * This function is a wrapper of OpenAL's alListenerfv.
     * @param param param
     * @param values values
     */
    public void alListenerfv(int param, float[] values) {
        set();
        AL10.alListenerfv(param, values);
    }

    /**
     * Render audio in virtual device in stereo
     * @return Audio data in 2-dimension array, the first dimension is data from left channel, the second dimension is
     * data from right channel
     */
    public float[][] sampleAudio() {
        set();
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

    /**
     * Gets device context.
     * @return device context
     */
    public long getContext() {
        return context;
    }
}
