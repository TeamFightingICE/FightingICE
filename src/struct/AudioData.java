package struct;

import setting.GameSetting;
import util.FFT;
import util.MFCC;

/**
 * The class dealing with the audio information in game such as raw audio data, FFT and Mel-Spectrogram transformation
 */
public class AudioData {
    /**
     * Raw audio data.
     */
    private float[][] rawData = null;
    /**
     * Fourier-transformed audio data.
     */
    private FFTData[] fftData = null;
    /**
     * Mel-Spectrogram audio data.
     */
    private float[][][] spectrogramData = null;
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
    }

    /**
     * Initialize data.
     */
    private void init() {
        this.rawData = new float[2][GameSetting.SOUND_BUFFER_SIZE];
        this.fftData = new FFTData[2];
        this.spectrogramData = new float[2][][];
    }

    /**
     * Class constructor.
     * @param audioData audio data.
     */
    public AudioData(AudioData audioData) {
        int bufferSize = (audioData.getRawData() != null && audioData.getRawData()[0].length > 0) ? audioData.getRawData()[0].length : 0;
        init();
        if (bufferSize > 0) {
            this.rawData = audioData.getRawData();
            this.fftData = audioData.getFftData();
            this.spectrogramData = audioData.getSpectrogramData();
        }
    }

    /**
     * Class constructor.
     * @param rawData raw audio data.
     */
    public AudioData(float[][] rawData) {
        this.init();
        this.rawData = rawData;

        // transform raw data
        fft.process(this.rawData[0]);
        this.fftData[0] = new FFTData(fft.getReal(), fft.getImag());
        fft.process(this.rawData[1]);
        this.fftData[1] = new FFTData(fft.getReal(), fft.getImag());
        this.spectrogramData[0] = mfcc.melSpectrogram(this.rawData[0]);
        this.spectrogramData[1] = mfcc.melSpectrogram(this.rawData[1]);
    }

    /**
     * Gets raw audio data.
     * @return raw audio data.
     */
    public float[][] getRawData() {
        return rawData;
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
}
