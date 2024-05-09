package struct;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.google.protobuf.ByteString;

import protoc.MessageProto.GrpcScreenData;
import setting.GameSetting;

/**
 * The class dealing with the screen information such as the game screen's image
 * and the background color.
 */
public class ScreenData {
	/**
	 *
	 */
	private BufferedImage displayBufferedImage;
	
	public ScreenData() {
		
	}
	
	public ScreenData(BufferedImage bimg) {
		this.displayBufferedImage = bimg;
	}

	/**
	 * The class constructor that creates pixel information of the game screen
	 * by using the specified data.
	 *
	 * @param screenData
	 *            an instance of ScreenData class
	 */
	public ScreenData(ScreenData screenData) {
		this.displayBufferedImage = screenData.getDisplayBufferedImage();
	}

	public BufferedImage getDisplayBufferedImage() {
		return this.displayBufferedImage;
	}

	/**
	 * Obtains RGB data or the grayScale data of the screen in the form of
	 * byte[].<br>
	 * Warning: This method doesn't return exactly the same buffer as
	 * getDisplayByteBufferAsBytes().
	 *
	 * @param newWidth
	 *            the width in pixel for the scaled image
	 * @param newHeight
	 *            the height in pixel for the scaled image
	 * @param grayScale
	 *            true to use grayScale for the scaled image (1 byte per pixel
	 *            instead of 3 bytes per pixel with RGB)
	 * @return the RGB data or the grayScale data of the screen in the form of
	 *         byte[]
	 */
	public byte[] getDisplayByteBufferAsBytes(int newWidth, int newHeight, boolean grayScale) {
		// Resizes the image
		AffineTransformOp xform = new AffineTransformOp(AffineTransform
				.getScaleInstance((double) newWidth / displayBufferedImage.getWidth(), (double) newHeight / displayBufferedImage.getHeight()),
				AffineTransformOp.TYPE_BILINEAR);
		BufferedImage resize = new BufferedImage(newWidth, newHeight, displayBufferedImage.getType());
		xform.filter(displayBufferedImage, resize);

		// Converts it back to array of bytes
		byte[] dst;

		if (grayScale) {
			BufferedImage temp = new BufferedImage(resize.getWidth(), resize.getHeight(),
					BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g = temp.createGraphics();
			g.drawImage(resize, 0, 0, newWidth, newHeight, null);
			g.dispose();

			dst = ((DataBufferByte) temp.getData().getDataBuffer()).getData();
		} else {
			dst = new byte[newWidth * newHeight * 3];

			int[] array = ((DataBufferInt) resize.getRaster().getDataBuffer()).getData();
			for (int x = 0; x < newWidth; x++) {
				for (int y = 0; y < newHeight; y++) {
					int idx = x + y * newWidth;
					dst[idx * 3] = (byte) ((array[idx] >> 16) & 0xFF); // R
					dst[idx * 3 + 1] = (byte) ((array[idx] >> 8) & 0xFF); // G
					dst[idx * 3 + 2] = (byte) ((array[idx]) & 0xFF); // B
				}
			}
		}

		return dst;
	}
	
	public byte[] getCompressedDisplayByteBufferAsBytes(int newWidth, int newHeight, boolean grayScale) {
		byte[] displayBytes = this.getDisplayByteBufferAsBytes(newWidth, newHeight, grayScale);
		return this.compressByteData(displayBytes);
	}
	
	private byte[] compressByteData(byte[] original) {
		try {
		    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		    try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
		        gzipOutputStream.write(original);
		    }
		    
		    byte[] compressedData = byteArrayOutputStream.toByteArray();
		    return compressedData;
		} catch (IOException e) {
			return new byte[1];
		}
	}
	
	public GrpcScreenData toProto() {
  		GrpcScreenData.Builder builder = GrpcScreenData.newBuilder();
  		if (this.displayBufferedImage != null) {
  			builder.setDisplayBytes(ByteString.copyFrom(this.getCompressedDisplayByteBufferAsBytes(
  					GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, false)));
  		}
  		return builder.build();
  	}

}
