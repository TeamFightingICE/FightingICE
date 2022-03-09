package struct;

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
}
