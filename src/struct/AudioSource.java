package struct;

import manager.SoundManager;
import org.lwjgl.openal.AL10;
import render.audio.SoundRender;

import java.util.List;

public class AudioSource {
    int[] sourceIds;

    public AudioSource(int[] sourceIds){
        this.sourceIds = sourceIds;
    }

    public AudioSource(String file){
        List<SoundRender> renderers = SoundManager.getInstance().getSoundRenderers();
        sourceIds = new int[renderers.size()];
        for (int i = 0; i < renderers.size(); i++) {
            renderers.get(i).set();
            sourceIds[i] = AL10.alGenSources();
            AL10.alSource3f(sourceIds[i], AL10.AL_POSITION, 0, 0, 0);
            AL10.alSource3f(sourceIds[i], AL10.AL_VELOCITY, 0, 0, 0);
            AL10.alSourcei(sourceIds[i], AL10.AL_LOOPING, AL10.AL_TRUE);
        }
    }
    public int[] getSourceIds() {
        return sourceIds;
    }
}
