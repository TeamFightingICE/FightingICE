package struct;

import static org.lwjgl.openal.AL10.alBufferData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.util.WaveData;

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
    
    private void registerSound(int bufferId, String filePath) {
        try {
            BufferedInputStream e = new BufferedInputStream(new FileInputStream(new File(filePath)));
            WaveData waveFile = WaveData.create(e);
            alBufferData(bufferId, waveFile.format, waveFile.data, waveFile.samplerate);
            e.close();
            waveFile.dispose();
        } catch (FileNotFoundException arg1) {
            arg1.printStackTrace();
        } catch (IOException arg2) {
            arg2.printStackTrace();
        }
    }
    
    public void registerSound(String filePath) {
    	for (int i = 0; i < buffers.length; i++) {
    		registerSound(buffers[i], filePath);
    	}
    }
    
}
