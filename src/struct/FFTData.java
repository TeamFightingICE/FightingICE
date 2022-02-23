package struct;


/**
 * The class representing Fast Fourier Transform data.
 */
public class FFTData {
    /**
     * Real parts
     */
    private float[] real;
    /**
     * Imaginary parts
     */
    private float[] imag;

    /**
     * Class constructor.
     * @param real real data.
     * @param imag imaginary data.
     */
    FFTData(float[] real, float[] imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     * Gets real part.
     * @return real part.
     */
    public float[] getReal() {
        return real;
    }

    /**
     * Gets imaginary part.
     * @return imaginary part
     */
    public float[] getImag() {
        return imag;
    }
}