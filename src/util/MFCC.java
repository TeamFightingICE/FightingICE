package util;/*
    Turn Librosa Mfcc feature into Java code.
	Parameters are set to the librosa default for the purpose of android demo.
	The FFT code is taken from org.ioe.tprsa.audio.feature.
 */


import setting.GameSetting;

/**
 * Mel-Frequency Cepstrum Coefficients.
 *
 *
 *
 */


public class MFCC {

    private final static int       n_mfcc       		= 20;
    private final static float    fMin                 = 0.0F;
    private final static int       n_fft                = 32;
    private final static int       hop_length           = 128;
    private final static int	   n_mels               = 128;

    private final static float    sampleRate           = GameSetting.SOUND_SAMPLING_RATE;
    private final static float    fMax                 = (float) (sampleRate/2.0);

    FFT fft = new FFT();


    public float[] process(float[] floatInputBuffer) {
        final float[][] mfccResult = dctMfcc(floatInputBuffer);
        return finalshape(mfccResult);
    }

    //MFCC into 1d
    private float[] finalshape(float[][] mfccSpecTro){
        float[] finalMfcc = new float[mfccSpecTro[0].length * mfccSpecTro.length];
        int k = 0;
        for (int i = 0; i < mfccSpecTro[0].length; i++){
            for (int j = 0; j < mfccSpecTro.length; j++){
                finalMfcc[k] = (float) mfccSpecTro[j][i];
                k = k+1;
            }
        }
        return finalMfcc;
    }

    //DCT to mfcc, librosa
    private float[][] dctMfcc(float[] y){
        final float[][] specTroGram = powerToDb(melSpectrogram(y));
        final float[][] dctBasis = dctFilter(n_mfcc, n_mels);
        float[][] mfccSpecTro = new float[n_mfcc][specTroGram[0].length];
        for (int i = 0; i < n_mfcc; i++){
            for (int j = 0; j < specTroGram[0].length; j++){
                for (int k = 0; k < specTroGram.length; k++){
                    mfccSpecTro[i][j] += dctBasis[i][k]*specTroGram[k][j];
                }
            }
        }
        return mfccSpecTro;
    }


    //mel spectrogram, librosa
    public float[][] melSpectrogram(float[] y){
        float[][] melBasis = melFilter();
        float[][] spectro = stftMagSpec(y);
        float[][] melS = new float[melBasis.length][spectro[0].length];
        for (int i = 0; i < melBasis.length; i++){
            for (int j = 0; j < spectro[0].length; j++){
                for (int k = 0; k < melBasis[0].length; k++){
                    melS[i][j] += melBasis[i][k]*spectro[k][j];
                }
            }
        }
        return melS;
    }


    //stft, librosa
    private float[][] stftMagSpec(float[] y){
        //Short-time Fourier transform (STFT)
        final float[] fftwin = getWindow();
        //pad y with reflect mode so it's centered. This reflect padding implementation is
        // not perfect but works for this demo.
        float[] ypad = new float[n_fft+y.length];
        for (int i = 0; i < n_fft/2; i++){
            ypad[(n_fft/2)-i-1] = y[i+1];
            ypad[(n_fft/2)+y.length+i] = y[y.length-2-i];
        }
        for (int j = 0; j < y.length; j++){
            ypad[(n_fft/2)+j] = y[j];
        }


        final float[][] frame = yFrame(ypad);
        float[][] fftmagSpec = new float[1+n_fft/2][frame[0].length];
        float[] fftFrame = new float[n_fft];
        for (int k = 0; k < frame[0].length; k++){
            for (int l =0; l < n_fft; l++){
                fftFrame[l] = fftwin[l]*frame[l][k];
            }
            float[] magSpec = magSpectrogram(fftFrame);
            for (int i =0; i < 1+n_fft/2; i++){
                fftmagSpec[i][k] = magSpec[i];
            }
        }
        return fftmagSpec;
    }

    private float[] magSpectrogram(float[] frame){
        float[] magSpec = new float[frame.length];
        fft.process(frame);
        for (int m = 0; m < frame.length; m++) {
            magSpec[m] = fft.real[m] * fft.real[m] + fft.imag[m] * fft.imag[m];
        }
        return magSpec;
    }


    //get hann window, librosa
    private float[] getWindow(){
        //Return a Hann window for even n_fft.
        //The Hann window is a taper formed by using a raised cosine or sine-squared
        //with ends that touch zero.
        float[] win = new float[n_fft];
        for (int i = 0; i < n_fft; i++){
            win[i] = (float) (0.5 - 0.5 * Math.cos(2.0*Math.PI*i/n_fft));
        }
        return win;
    }

    //frame, librosa
    private float[][] yFrame(float[] ypad){
        final int n_frames = 1 + (ypad.length - n_fft) / hop_length;
        float[][] winFrames = new float[n_fft][n_frames];
        for (int i = 0; i < n_fft; i++){
            for (int j = 0; j < n_frames; j++){
                winFrames[i][j] = ypad[j*hop_length+i];
            }
        }
        return winFrames;
    }

    //power to db, librosa
    private float[][] powerToDb(float[][] melS){
        //Convert a power spectrogram (amplitude squared) to decibel (dB) units
        //  This computes the scaling ``10 * log10(S / ref)`` in a numerically
        //  stable way.
        float[][] log_spec = new float[melS.length][melS[0].length];
        float maxValue = -100;
        for (int i = 0; i < melS.length; i++){
            for (int j = 0; j < melS[0].length; j++){
                float magnitude = Math.abs(melS[i][j]);
                if (magnitude > 1e-10){
                    log_spec[i][j]= (float) (10.0*log10(magnitude));
                }else{
                    log_spec[i][j]= (float) (10.0*(-10));
                }
                if (log_spec[i][j] > maxValue){
                    maxValue = log_spec[i][j];
                }
            }
        }

        //set top_db to 80.0
        for (int i = 0; i < melS.length; i++){
            for (int j = 0; j < melS[0].length; j++){
                if (log_spec[i][j] < maxValue - 80.0){
                    log_spec[i][j] = (float) (maxValue - 80.0);
                }
            }
        }
        //ref is disabled, maybe later.
        return log_spec;
    }

    //dct, librosa
    private float[][] dctFilter(int n_filters, int n_input){
        //Discrete cosine transform (DCT type-III) basis.
        float[][] basis = new float[n_filters][n_input];
        float[] samples = new float[n_input];
        for (int i = 0; i < n_input; i++){
            samples[i] = (float) ((1 + 2*i) * Math.PI/(2.0*(n_input)));
        }
        for (int j = 0; j < n_input; j++){
            basis[0][j] = (float) (1.0/Math.sqrt(n_input));
        }
        for (int i = 1; i < n_filters; i++){
            for (int j = 0; j < n_input; j++){
                basis[i][j] = (float) (Math.cos(i*samples[j]) * Math.sqrt(2.0/(n_input)));
            }
        }
        return basis;
    }


    //mel, librosa
    private float[][] melFilter(){
        //Create a Filterbank matrix to combine FFT bins into Mel-frequency bins.
        // Center freqs of each FFT bin
        final float[] fftFreqs = fftFreq();
        //'Center freqs' of mel bands - uniformly spaced between limits
        final float[] melF = melFreq(n_mels+2);

        float[] fdiff = new float[melF.length-1];
        for (int i = 0; i < melF.length-1; i++){
            fdiff[i] = melF[i+1]-melF[i];
        }

        float[][] ramps = new float[melF.length][fftFreqs.length];
        for (int i = 0; i < melF.length; i++){
            for (int j = 0; j < fftFreqs.length; j++){
                ramps[i][j] = melF[i]-fftFreqs[j];
            }
        }

        float[][] weights = new float[n_mels][1+n_fft/2];
        for (int i = 0; i < n_mels; i++){
            for (int j = 0; j < fftFreqs.length; j++){
                float lowerF = -ramps[i][j] / fdiff[i];
                float upperF = ramps[i+2][j] / fdiff[i+1];
                if (lowerF > upperF && upperF>0){
                    weights[i][j] = upperF;
                }else if (lowerF > upperF && upperF<0){
                    weights[i][j] = 0;
                }else if (lowerF < upperF && lowerF>0){
                    weights[i][j] =lowerF;
                }else if (lowerF < upperF && lowerF<0){
                    weights[i][j] = 0;
                }else {}
            }
        }

        float enorm[] = new float[n_mels];
        for (int i = 0; i < n_mels; i++){
            enorm[i] = (float) (2.0 / (melF[i+2]-melF[i]));
            for (int j = 0; j < fftFreqs.length; j++){
                weights[i][j] *= enorm[i];
            }
        }
        return weights;

        //need to check if there's an empty channel somewhere
    }

    //fft frequencies, librosa
    private float[] fftFreq() {
        //Alternative implementation of np.fft.fftfreqs
        float[] freqs = new float[1+n_fft/2];
        for (int i = 0; i < 1+n_fft/2; i++){
            freqs[i] = 0 + (sampleRate/2)/(n_fft/2) * i;
        }
        return freqs;
    }

    //mel frequencies, librosa
    private float[] melFreq(int numMels) {
        //'Center freqs' of mel bands - uniformly spaced between limits
        float[] LowFFreq = new float[1];
        float[] HighFFreq = new float[1];
        LowFFreq[0] = fMin;
        HighFFreq[0] = fMax;
        final float[] melFLow    = freqToMel(LowFFreq);
        final float[] melFHigh   = freqToMel(HighFFreq);
        float[] mels = new float[numMels];
        for (int i = 0; i < numMels; i++) {
            mels[i] = melFLow[0] + (melFHigh[0] - melFLow[0]) / (numMels-1) * i;
        }
        return melToFreq(mels);
    }


    //mel to hz, htk, librosa
    private float[] melToFreqS(float[] mels) {
        float[] freqs = new float[mels.length];
        for (int i = 0; i < mels.length; i++) {
            freqs[i] = (float) (700.0 * (Math.pow(10, mels[i]/2595.0) - 1.0));
        }
        return freqs;
    }


    // hz to mel, htk, librosa
    protected float[] freqToMelS(float[] freqs) {
        float[] mels = new float[freqs.length];
        for (int i = 0; i < freqs.length; i++){
            mels[i] = (float) (2595.0 * log10((float) (1.0 + freqs[i]/700.0)));
        }
        return mels;
    }

    //mel to hz, Slaney, librosa
    private float[] melToFreq(float[] mels) {
        // Fill in the linear scale
        final float f_min = 0.0F;
        final float f_sp = (float) (200.0 / 3);
        float[] freqs = new float[mels.length];

        // And now the nonlinear scale
        final float min_log_hz = 1000.0F;                         // beginning of log region (Hz)
        final float min_log_mel = (min_log_hz - f_min) / f_sp;  // same (Mels)
        final float logstep = (float) (Math.log(6.4) / 27.0);

        for (int i = 0; i < mels.length; i++) {
            if (mels[i] < min_log_mel){
                freqs[i] =  f_min + f_sp * mels[i];
            }else{
                freqs[i] = (float) (min_log_hz * Math.exp(logstep * (mels[i] - min_log_mel)));
            }
        }
        return freqs;
    }


    // hz to mel, Slaney, librosa
    protected float[] freqToMel(float[] freqs) {
        final float f_min = 0.0f;
        final float f_sp = 200.0f / 3;
        float[] mels = new float[freqs.length];

        // Fill in the log-scale part

        final float min_log_hz = 1000.0f;                         // beginning of log region (Hz)
        final float min_log_mel = (min_log_hz - f_min) / f_sp ;  // # same (Mels)
        final float logstep = (float) (Math.log(6.4) / 27.0);              // step size for log region

        for (int i = 0; i < freqs.length; i++) {
            if (freqs[i] < min_log_hz){
                mels[i] = (freqs[i] - f_min) / f_sp;
            }else{
                mels[i] = (float) (min_log_mel + Math.log(freqs[i]/min_log_hz) / logstep);
            }
        }
        return mels;
    }

    // log10
    private float log10(float value) {
        return (float) (Math.log(value) / Math.log(10));
    }
}