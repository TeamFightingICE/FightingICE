package struct;

public class AudioBuffer {
    private int[] buffers;

    public AudioBuffer(){

    }

    public AudioBuffer(int[] buffers){
        this.buffers = buffers;
    }
    public int[] getBuffers() {
        return buffers;
    }
}
