package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A helper class that converts numeric array to byte array
 */
public class NumberConverter {
    private static NumberConverter instance = new NumberConverter();

    private NumberConverter(){

    }

    public static NumberConverter getInstance(){
        if(instance == null){
            instance = new NumberConverter();
        }
        return instance;
    }

    public byte[] getByteArray(int[][] intArray){
        // Set up a ByteBuffer called intBuffer
        int iMax = intArray.length;
        int jMax = intArray[0].length;
        ByteBuffer intBuffer = ByteBuffer.allocate(4*iMax*jMax); // 4 bytes in an int
        intBuffer.order(ByteOrder.LITTLE_ENDIAN); // Java's default is big-endian

        // Copy ints from intArray into intBuffer as bytes
        for (int i = 0; i < iMax; i++) {
            for (int j = 0; j < jMax; j++){
                intBuffer.putInt(intArray[i][j]);
            }
        }

        // Convert the ByteBuffer to a byte array and return it
        byte[] byteArray = intBuffer.array();
        return byteArray;
    }

    public byte[] getByteArray(float[][] floatArray){
        // Set up a ByteBuffer called floatBuffer
        int iMax = floatArray.length;
        int jMax = floatArray[0].length;
        ByteBuffer floatBuffer = ByteBuffer.allocate(4*iMax*jMax); // 4 bytes in an int
        floatBuffer.order(ByteOrder.LITTLE_ENDIAN); // Java's default is big-endian

        // Copy ints from floatArray into floatBuffer as bytes
        for (int i = 0; i < iMax; i++) {
            for (int j = 0; j < jMax; j++){
                floatBuffer.putFloat(floatArray[i][j]);
            }
        }

        // Convert the ByteBuffer to a byte array and return it
        byte[] byteArray = floatBuffer.array();
        return byteArray;
    }

    public byte[] getByteArray(float[] floatArray){
        // Set up a ByteBuffer called floatBuffer
        int iMax = floatArray.length;
        ByteBuffer intBuffer = ByteBuffer.allocate(4*iMax); // 4 bytes in an int
        intBuffer.order(ByteOrder.LITTLE_ENDIAN); // Java's default is big-endian

        // Copy ints from floatArray into floatBuffer as bytes
        for (int i = 0; i < iMax; i++) {
            intBuffer.putFloat(floatArray[i]);
        }

        // Convert the ByteBuffer to a byte array and return it
        byte[] byteArray = intBuffer.array();
        return byteArray;
    }

    public byte[] getByteArray(int[] intArray){
        // Set up a ByteBuffer called intBuffer
        int iMax = intArray.length;
        ByteBuffer intBuffer = ByteBuffer.allocate(4*iMax); // 4 bytes in an int
        intBuffer.order(ByteOrder.LITTLE_ENDIAN); // Java's default is big-endian

        // Copy ints from intArray into intBuffer as bytes
        for (int i = 0; i < iMax; i++) {
            intBuffer.putInt(intArray[i]);
        }

        // Convert the ByteBuffer to a byte array and return it
        byte[] byteArray = intBuffer.array();
        return byteArray;
    }

    public byte[] getByteArray(float[][][] floatArray){
        // Set up a ByteBuffer called intBuffer
        int iMax = floatArray.length;
        int jMax = floatArray[0].length;
        int kMax = floatArray[0][0].length;
        ByteBuffer intBuffer = ByteBuffer.allocate(4*iMax*jMax*kMax); // 4 bytes in an int
        intBuffer.order(ByteOrder.LITTLE_ENDIAN); // Java's default is big-endian

        // Copy ints from floatArray into intBuffer as bytes
        for (int i = 0; i < iMax; i++) {
            for (int j = 0; j < jMax; j++){
                for(int k = 0; k < kMax; k++)
                intBuffer.putFloat(floatArray[i][j][k]);
            }
        }

        // Convert the ByteBuffer to a byte array and return it
        byte[] byteArray = intBuffer.array();
        return byteArray;
    }
    
}
