package struct;

import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_NONE;
import static org.lwjgl.openal.AL10.alSourcei;

import manager.SoundManager;

/**
 * The class representing OpenAL audio source in multiple devices.
 */
public class AudioSource {
	
    /**
     * Source ids.
     */
    private int[] sourceIds;

    /**
     * Class constructor.
     * @param sourceIds sourceids.
     */
    public AudioSource(int[] sourceIds) {
        this.sourceIds = sourceIds;
    }

    /**
     * Gets sources.
     * @return source ids.
     */
    public int[] getSourceIds() {
        return sourceIds;
    }
    
    public void clearBuffer() {
    	for (int i = 0; i < sourceIds.length; i++) {
    		SoundManager.getInstance().getSoundRenderers().get(i).set();
    		int sourceId = sourceIds[i];
    		
    		alSourcei(sourceId, AL_BUFFER, AL_NONE);
    	}
    
    }

    /**
     * Close the source to release OpenAL source ids
     */
    public void close(){
        SoundManager.getInstance().deleteSource(this);
    }
}
