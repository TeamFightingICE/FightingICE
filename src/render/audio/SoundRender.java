package render.audio;

import org.lwjgl.openal.*;
import setting.GameSetting;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.AL_LOOPING;

public class SoundRender {
    long device;
    long context;
    ALCCapabilities deviceCaps;


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

    public void set() {
        ALC10.alcMakeContextCurrent(this.context);
    }

    public SoundRender(long device, long context, ALCCapabilities deviceCaps) {
        this.device = device;
        this.context = context;
        this.deviceCaps = deviceCaps;
    }

    public void play(int sourceId, int bufferId) {
        set();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
        AL10.alSourcePlay(sourceId);
    }

    public void stop(int sourceId) {
        set();
        if(isPlaying(sourceId)) {
            AL10.alSourceStop(sourceId);
        }
    }

    public void play(int sourceId, int bufferId, int x, int y, boolean loop) {
        set();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSource3f(sourceId, AL_POSITION, x, 0, 4);
        alSourcei(sourceId, AL_LOOPING, loop ? 1 : 0);
        AL10.alSourcePlay(sourceId);
        int error = alGetError();
    }

    public void setListenerData() {
        set();
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }

    public long getDevice() {
        return device;
    }

    public void setSource3f(int source, int param, float x, float y, float z) {
        set();
        alSource3f(source, param, x, y, z);
    }

    public void deleteSource(int source) {
        set();
        alDeleteSources(source);
    }

    public void deleteBuffer(int buffer) {
        set();
        alDeleteBuffers(buffer);
    }

    public void close() {
        set();
        ALC10.alcDestroyContext(this.context);
        ALC10.alcCloseDevice(this.device);
    }

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

    public void alListenerfv(int param, float[] values) {
        set();
        AL10.alListenerfv(param, values);
    }

    public float[][] sampleAudio() {
        float[] rawData = new float[GameSetting.SOUND_BUFFER_SIZE * 2];
        set();
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

    public long getContext() {
        return context;
    }
}
