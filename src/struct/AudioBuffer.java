package struct;

/**
 * The class representing audio buffer in multiple devices.
 */
public class AudioBuffer {
    /**
     * Buffer ids.
     */
    private int[] buffers;

    /**
     * Class constructor.
     */
    public AudioBuffer() {

    }

    /**
     * Class constructor.
     *
     * @param buffers audio buffers.
     */
    public AudioBuffer(int[] buffers) {
        this.buffers = buffers;
    }

    /**
     * Get buffers.
     * @return buffer ids.
     */
    public int[] getBuffers() {
        return buffers;
    }
}
