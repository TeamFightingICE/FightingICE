package util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import setting.GameSetting;

public class WaveFileWriter {
	
	private int nChannels;
	private int sampleRate;
	private int bitsPerSample;
	
	private int dataSize;
	private String filePath;
	private ByteArrayOutputStream byteOutput;
	
	public WaveFileWriter(int nChannels, int sampleRate, int bitsPerSample) {
		this.nChannels = nChannels;
		this.sampleRate = sampleRate;
		this.bitsPerSample = bitsPerSample;
		this.dataSize = 0;
	}
	
	private static WaveFileWriter instance = new WaveFileWriter(2, GameSetting.SOUND_SAMPLING_RATE, 16);

    private WaveFileWriter(){

    }

    public static WaveFileWriter getInstance(){
        if(instance == null){
            instance = new WaveFileWriter();
        }
        return instance;
    }
	
	public int getNChannels() {
		return this.nChannels;
	}
	
	public int getSampleRate() {
		return this.sampleRate;
	}
	
	public int getDataSize() {
		return this.dataSize * nChannels * (sampleRate / 60) * (bitsPerSample / 8);
	}
	
	public void initializeWaveFile(String filePath) {
		this.filePath = filePath;
		this.byteOutput = new ByteArrayOutputStream();
	}
	
	public void addSample(byte[] sample) {
		if (sample.length != nChannels * (sampleRate / 60) * (bitsPerSample / 8)) {
	        Logger.getAnonymousLogger().log(Level.SEVERE, "Failed to write new audio sample. Invalid format");
			return;
		}
		
		try {
			this.byteOutput.write(sample);
			this.dataSize++;
		} catch (IOException e) {
	        Logger.getAnonymousLogger().log(Level.SEVERE, "Failed to write new audio sample. IOException");
		}
	}
	
	public void writeToFile() {
		if (dataSize < 1) return;
		
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			int dataSize = getDataSize();
			
			// Calculate the total file size minus 8 bytes for the RIFF header and 16 bytes for the format chunk
			int chunkSize = dataSize + 36;
	        int subChunk2Size = dataSize;
	        
	        // Write RIFF header
	        fos.write(new byte[] {'R', 'I', 'F', 'F'}); // Chunk ID
	        fos.write(intToByteArray(chunkSize)); // Chunk Size
	        fos.write(new byte[] {'W', 'A', 'V', 'E'}); // Format
	        
	        // Write fmt subchunk
	        fos.write(new byte[] {'f', 'm', 't', ' '}); // Subchunk 1 ID
	        fos.write(intToByteArray(16)); // Subchunk 1 Size
	        fos.write(shortToByteArray((short) 1)); // Audio Format (PCM = 1)
	        fos.write(shortToByteArray((short) nChannels)); // Num Channels
	        fos.write(intToByteArray(sampleRate)); // Sample Rate
	        fos.write(intToByteArray(sampleRate * nChannels * (bitsPerSample / 8)));  //Byte Rate
	        fos.write(shortToByteArray((short) (nChannels * bitsPerSample / 8))); // Block Align
	        fos.write(shortToByteArray((short) bitsPerSample)); // Bits Per Sample
	        
	        // Write data subchunk
	        fos.write(new byte[] {'d', 'a', 't', 'a'}); // Subchunk 2 ID
	        fos.write(intToByteArray(subChunk2Size)); // Subchunk 2 Size
	        fos.write(byteOutput.toByteArray());  // Data
	        
	        fos.close();
	        this.byteOutput.close();
	        
	        this.dataSize = 0;
	        this.byteOutput = null;
	        
	        Logger.getAnonymousLogger().log(Level.INFO, "WAV file created successfully.");
		} catch (IOException e) {
	        Logger.getAnonymousLogger().log(Level.SEVERE, "Failed to create WAV file.");
		}
		
	}
	
	public void close() {
		if (this.dataSize > 0) {
			writeToFile();
		}
		
		this.byteOutput = null;
	}
	
	private static byte[] intToByteArray(int value) {
		ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(value);
		return buffer.array();
	}
	
	private static byte[] shortToByteArray(short value) {
		ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(value);
        return buffer.array();
    }

}
