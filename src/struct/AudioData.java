package struct;

import setting.GameSetting;
import util.FFT;
import util.MFCC;

public class AudioData {
    /**
     * Raw audio data
     */
    private float[][] rawData = null;
    /**
     * Fourier-transformed audio data
     */
    private FFTData[] fftData = null;
    /**
     * Spectrogram audio data
     */
    private float[][][] spectrogramData = null;

    private static FFT fft = new FFT();
//    private static AnotherFFT fft = new AnotherFFT();
    private static MFCC mfcc = new MFCC();

    public AudioData() {
    }

    private void init() {
        this.rawData = new float[2][GameSetting.SOUND_BUFFER_SIZE];
        this.fftData = new FFTData[2];
        this.spectrogramData = new float[2][][];
    }

    public AudioData(AudioData audioData) {
        int bufferSize = (audioData.getRawData() != null && audioData.getRawData()[0].length > 0) ? audioData.getRawData()[0].length : 0;
        init();
        if (bufferSize > 0) {
            this.rawData = audioData.getRawData();
            this.fftData = audioData.getFftData();
            this.spectrogramData = audioData.getSpectrogramData();
        }
    }

    public AudioData(float[][] rawData) {
        this.init();
//        long start = System.currentTimeMillis();
        this.rawData = rawData;

        // transform raw data
        fft.process(this.rawData[0]);
        this.fftData[0] = new FFTData(fft.getReal(), fft.getImag());
        fft.process(this.rawData[1]);
        this.fftData[1] = new FFTData(fft.getReal(), fft.getImag());
        this.spectrogramData[0] = mfcc.melSpectrogram(this.rawData[0]);
        this.spectrogramData[1] = mfcc.melSpectrogram(this.rawData[1]);
//        System.out.println(System.currentTimeMillis() - start);

    }

    public float[][] getRawData() {
        return rawData;
    }

    public FFTData[] getFftData() {
        return fftData;
    }

    public float[][][] getSpectrogramData() {
        return spectrogramData;
    }


    static class FFTData {
        private float[] real;
        private float[] imag;

        public float[] getReal() {
            return real;
        }

        public float[] getImag() {
            return imag;
        }

        FFTData(float[] real, float[] imag) {
            this.real = real;
            this.imag = imag;
        }
    }
}
