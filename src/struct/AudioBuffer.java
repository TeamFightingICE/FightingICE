package struct;

import static org.lwjgl.openal.AL10.alBufferData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.WaveData;

import manager.SoundManager;

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
    
    public void registerSound(String filePath) {
    	int format, samplerate;
    	ByteBuffer data;
    	
    	try {
	        BufferedInputStream e = new BufferedInputStream(new FileInputStream(new File(filePath)));
	        WaveData waveFile = WaveData.create(e);
	        
	        format = waveFile.format;
	        samplerate = waveFile.samplerate;
	        
	        data = BufferUtils.createByteBuffer(waveFile.data.limit());
	        data.order(ByteOrder.LITTLE_ENDIAN);
	        data.put(waveFile.data);
	        
	        e.close();
	        waveFile.dispose();
    	} catch (FileNotFoundException e1) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Sound file not found at " + filePath);
            return;
        } catch (IOException e2) {
            e2.printStackTrace();
            return;
        }
    	
		for (int i = 0; i < buffers.length; i++) {
			SoundManager.getInstance().getSoundRenderers().get(i).set();
    		int bufferId = buffers[i];
    		
    		data.flip();
    		alBufferData(bufferId, format, data, samplerate);
		}
    }
    
}
