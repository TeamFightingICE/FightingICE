package struct;

import manager.SoundManager;

/**
 * The class representing OpenAL audio source in multiple devices.
 */
public class AudioSource {
    /**
     * Source ids.
     */
    int[] sourceIds;

    /**
     * Class constructor.
     * @param sourceIds sourceids.
     */
    public AudioSource(int[] sourceIds){
        this.sourceIds = sourceIds;
    }

    /**
     * Gets sources.
     * @return source ids.
     */
    public int[] getSourceIds() {
        return sourceIds;
    }

    /**
     * Close the source to release OpenAL source ids
     */
    public void close(){
        SoundManager.getInstance().deleteSource(this);
    }
}
