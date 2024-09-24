package struct;


import com.google.protobuf.ByteString;

import protoc.MessageProto.GrpcFftData;
import util.NumberConverter;

/**
 * The class representing Fast Fourier Transform data.<br>
 * For more details on the data structure, please see <a href="https://tinyurl.com/DareFightingICE/AI" target="blank">https://tinyurl.com/DareFightingICE/AI</a>.
 */
public class FFTData {
    /**
     * Real parts
     */
    private float[] real;

    private byte[] realAsBytes;
    /**
     * Imaginary parts
     */
    private float[] imag;
    private byte[] imagAsBytes;

    /**
     * Class constructor.
     * @param real real data.
     * @param imag imaginary data.
     */
    FFTData(float[] real, float[] imag) {
        this.real = real;
        this.imag = imag;
        this.realAsBytes = NumberConverter.getInstance().getByteArray(real);
        this.imagAsBytes = NumberConverter.getInstance().getByteArray(imag);
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

    /**
     * Byte sequence version of {@link #getReal()}.<br>
     * This method is recommended for Python-based AI
     * @return real part as byte sequence.
     */
    public byte[] getRealAsBytes() {
        return realAsBytes;
    }

    /**
     * Byte sequence version of {@link #getImag()}<br>
     * This method is recommended for Python-based AI
     * @return imaginary part as byte sequence.
     */
    public byte[] getImagAsBytes() {
        return imagAsBytes;
    }
    
    public GrpcFftData toProto() {
  		return GrpcFftData.newBuilder()
  				.setRealDataAsBytes(ByteString.copyFrom(this.realAsBytes))
  				.setImaginaryDataAsBytes(ByteString.copyFrom(this.imagAsBytes))
  				.build();
  	}
    
}