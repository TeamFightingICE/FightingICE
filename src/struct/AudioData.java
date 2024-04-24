package struct;

import java.nio.ByteBuffer;
import java.util.Arrays;

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
    private float[][] rawData = null;
    /**
     * Raw audio data as byte sequence.
     */
    private byte[] rawDataAsBytes = null;

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
        this.rawData = new float[2][GameSetting.SOUND_BUFFER_SIZE];
        this.fftData = new FFTData[2];
        this.spectrogramData = new float[2][][];
    }
    
    private void tranformRawData() {
        fft.process(Arrays.copyOf(this.rawData[0], this.rawData[0].length));
        this.fftData[0] = new FFTData(fft.getReal(), fft.getImag());
        fft.process(Arrays.copyOf(this.rawData[1], this.rawData[1].length));
        this.fftData[1] = new FFTData(fft.getReal(), fft.getImag());
        
        if (this.rawDataAsBytes == null)
        	this.rawDataAsBytes = NumberConverter.getInstance().getByteArray(this.rawData);
        
        this.spectrogramData[0] = mfcc.melSpectrogram(Arrays.copyOf(this.rawData[0], this.rawData[0].length));
        this.spectrogramData[1] = mfcc.melSpectrogram(Arrays.copyOf(this.rawData[1], this.rawData[1].length));
        
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
            this.rawData = audioData.getRawData();
            this.fftData = audioData.getFftData();
            this.spectrogramData = audioData.getSpectrogramData();
            this.rawDataAsBytes = audioData.getRawDataAsBytes();
            this.spectrogramDataAsBytes = audioData.getSpectrogramDataAsBytes();
        }
    }

    /**
     * Class constructor.
     * @param rawData raw audio data.
     */
    public AudioData(float[][] rawData) {
        this.init();
        this.rawData = rawData;

        this.tranformRawData();
    }
    
    public AudioData(byte[] rawDataAsBytes) {
    	this.init();
    	this.rawData = new float[2][1024];
    	this.rawDataAsBytes = rawDataAsBytes;
    	
        for (int i = 0; i < 8192; i += 4) {
            float value = ByteBuffer.wrap(rawDataAsBytes, i, 4).getFloat();

            int rowIndex = (i / 4) / 1024;
            int colIndex = (i / 4) % 1024;

            this.rawData[rowIndex][colIndex] = value;
        }
        
        this.tranformRawData();
    }

    /**
     * Gets raw audio data.
     * @return raw audio data.
     */
    public float[][] getRawData() {
        return rawData;
    }

    /**
     * Byte sequence version of {@link #getRawData()}.<br>
     * This method is recommended for Python-based AI
     * @return raw audio data as byte sequence.
     */
    public byte[] getRawDataAsBytes() {
        return rawDataAsBytes;
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
}
