package struct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;

import protoc.MessageProto.GrpcAudioData;
import setting.GameSetting;
import util.FFT;
import util.MFCC;
import util.NumberConverter;

/**
 * The class dealing with the audio information in game such as raw audio data, FFT and Mel-Spectrogram transformation.<br>
 * For more details on the data structure, please see <a href="https://tinyurl.com/DareFightingICE/AI" target="blank">https://tinyurl.com/DareFightingICE/AI</a>.
 */
public class AudioData {
    /**
     * Raw audio data.
     */
	private short[][] rawShortData = null;
    private float[][] rawFloatData = null;
    /**
     * Raw audio data as byte sequence.
     */
    private byte[] rawShortDataAsBytes = null;
    private byte[] rawFloatDataAsBytes = null;

    /**
     * Fourier-transformed audio data.
     */
    private FFTData[] fftData = null;
    /**
     * Mel-Spectrogram audio data.
     */
    private float[][][] spectrogramData = null;
    /**
     * Mel-Spectrogram audio data as byte sequence.
     */
    private byte[] spectrogramDataAsBytes = null;

    /**
     * Fast-Fourier transformer.
     */
    private static FFT fft = new FFT();
    /**
     * Mel-Spectrogram transformer.
     */
    private static MFCC mfcc = new MFCC();

    /**
     * Class constructor.
     */
    public AudioData() {
    	this.init();
    	this.tranformRawData();
    }

    /**
     * Initialize data.
     */
    private void init() {
        this.rawShortData = new short[2][GameSetting.SOUND_RENDER_SIZE];
        this.rawFloatData = new float[2][GameSetting.SOUND_BUFFER_SIZE];
        this.fftData = new FFTData[2];
        this.spectrogramData = new float[2][][];
    }
    
    private void tranformRawData() {
        fft.process(Arrays.copyOf(this.rawFloatData[0], this.rawFloatData[0].length));
        this.fftData[0] = new FFTData(fft.getReal(), fft.getImag());
        fft.process(Arrays.copyOf(this.rawFloatData[1], this.rawFloatData[1].length));
        this.fftData[1] = new FFTData(fft.getReal(), fft.getImag());
        
        if (this.rawShortDataAsBytes == null)
        	this.rawShortDataAsBytes = NumberConverter.getInstance().getByteArray(this.rawShortData);
        
        if (this.rawFloatDataAsBytes == null)
        	this.rawFloatDataAsBytes = NumberConverter.getInstance().getByteArray(this.rawFloatData);
        
        this.spectrogramData[0] = mfcc.melSpectrogram(Arrays.copyOf(this.rawFloatData[0], this.rawFloatData[0].length));
        this.spectrogramData[1] = mfcc.melSpectrogram(Arrays.copyOf(this.rawFloatData[1], this.rawFloatData[1].length));
        
        this.spectrogramDataAsBytes = NumberConverter.getInstance().getByteArray(this.spectrogramData);
    }

    /**
     * Class constructor.
     * @param audioData audio data.
     */
    public AudioData(AudioData audioData) {
        int bufferSize = (audioData.getRawData() != null && audioData.getRawData()[0].length > 0) ? audioData.getRawData()[0].length : 0;
        this.init();
        if (bufferSize > 0) {
            this.rawFloatData = audioData.getRawData();
            this.fftData = audioData.getFftData();
            this.spectrogramData = audioData.getSpectrogramData();
            this.rawFloatDataAsBytes = audioData.getRawDataAsBytes();
            this.spectrogramDataAsBytes = audioData.getSpectrogramDataAsBytes();
        }
    }

    /**
     * Class constructor.
     * @param rawData raw audio data.
     */
    public AudioData(float[][] rawData) {
        this.init();
        this.rawFloatData = rawData;

        this.tranformRawData();
    }
    
    public AudioData(byte[] rawDataAsBytes) {
    	this.init();
    	
    	if (rawDataAsBytes.length != 3200 && rawDataAsBytes.length != 6400) {
    		rawDataAsBytes = new byte[6400];
			Logger.getAnonymousLogger().log(Level.WARNING, "Audio data format mismatch");
		}
    	
    	if (rawDataAsBytes.length == 3200) {
    		this.rawShortDataAsBytes = rawDataAsBytes;
    		
    		for (int i = 0; i < 3200; i += 2) {
    			short value = ByteBuffer.wrap(rawDataAsBytes, i, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
    			
    			int rowIndex = (i / 2) / GameSetting.SOUND_RENDER_SIZE;
    			int colIndex = (i / 2) % GameSetting.SOUND_RENDER_SIZE;
    			
    			this.rawShortData[rowIndex][colIndex] = value;
    			this.rawFloatData[rowIndex][colIndex] = (float) (value / 32767.0);
    		}
    	} else if (rawDataAsBytes.length == 6400) {
        	for (int i = 0; i < 6400; i += 4) {
                float value = ByteBuffer.wrap(rawDataAsBytes, i, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();

                int rowIndex = (i / 4) / GameSetting.SOUND_RENDER_SIZE;
                int colIndex = (i / 4) % GameSetting.SOUND_RENDER_SIZE;

                this.rawFloatData[rowIndex][colIndex] = value;
            	this.rawShortData[rowIndex][colIndex] = (short) (value * 32767);
            }
    	}
        
        this.tranformRawData();
    }

    /**
     * Gets raw audio data.
     * @return raw audio data.
     */
    public float[][] getRawData() {
        return rawFloatData;
    }

    /**
     * Byte sequence version of {@link #getRawData()}.<br>
     * This method is recommended for Python-based AI
     * @return raw audio data as byte sequence.
     */
    public byte[] getRawDataAsBytes() {
        return rawFloatDataAsBytes;
    }

    /**
     * Gets Fast Fourier Transform data.
     * @return Fast Fourier Transform data.
     */
    public FFTData[] getFftData() {
        return fftData;
    }

    /**
     * Gets Mel-Spectrogram data.
     * @return Mel-Spectrogram data.
     */
    public float[][][] getSpectrogramData() {
        return spectrogramData;
    }

    /**
     * Byte sequence version of {@link #getSpectrogramData()}.<br>
     * This method is recommended for Python-based AI
     * @return Mel-Spectrogram data as byte sequence.
     */
    public byte[] getSpectrogramDataAsBytes() {
        return spectrogramDataAsBytes;
    }
    
    public byte[] getRawShortDataAsBytes() {
    	return rawShortDataAsBytes;
    }
    
    public GrpcAudioData toProto() {
  		return GrpcAudioData.newBuilder()
  				.setRawDataAsBytes(ByteString.copyFrom(this.rawFloatDataAsBytes))
  				.addAllFftData(Arrays.stream(this.fftData).map(x -> x.toProto()).toList())
  				.setSpectrogramDataAsBytes(ByteString.copyFrom(this.spectrogramDataAsBytes))
  				.build();
  	}
    
}
