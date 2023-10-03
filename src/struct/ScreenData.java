package struct;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;

import org.lwjgl.BufferUtils;

import manager.GraphicManager;
import setting.GameSetting;

/**
 * The class dealing with the screen information such as the game screen's image
 * and the background color.
 */
public class ScreenData {

	/**
	 * The pixel data of the screen are saved in the form of bytes.
	 */
	private byte[] displayBytes;

	/**
	 *
	 */
	private BufferedImage displayBufferedImage;
	
	public ScreenData() {
		this.displayBytes = createDisplayBytes();
		this.displayBufferedImage =  GraphicManager.getInstance().getScreenImage();
	}

	/**
	 * The class constructor that creates pixel information of the game screen
	 * by using the specified data.
	 *
	 * @param screenData
	 *            an instance of ScreenData class
	 */
	public ScreenData(ScreenData screenData) {
		this.displayBytes = screenData.getDisplayBytes();
		this.displayBufferedImage = screenData.getDisplayBufferedImage();
	}

	/**
	 * Obtains RGB data of the screen in the form of ByteBuffer.<br>
	 * Warning: If the window is disabled, will just return a black buffer.
	 *
	 * @return the RGB data of the screen in the form of ByteBuffer
	 */
	public ByteBuffer getDisplayByteBuffer() {
		return ByteBuffer.wrap(displayBytes);
	}

	public BufferedImage getDisplayBufferedImage() {
		return this.displayBufferedImage;
	}

	/**
	 * Obtains RGB data of the screen in the form of byte[].<br>
	 * Warning: If the window is disabled, will just return a black buffer.
	 *
	 * @return the RGB data of the screen in the form of byte[]
	 */
	public byte[] getDisplayBytes() {
		return this.displayBytes;
	}
	
	public byte[] getCompressedDisplayBytes() {
		return this.compressByteData(this.displayBytes);
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
		    // Create a ByteArrayOutputStream to store the compressed data
		    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		    // Create a GZIPOutputStream to compress the data
		    try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
		        // Write the original data to the compressed stream
		        gzipOutputStream.write(original);
		    }

		    // Get the compressed data as a byte array
		    byte[] compressedData = byteArrayOutputStream.toByteArray();

		    // Now you can send the compressedData over gRPC
		    return compressedData;
		} catch (IOException e) {
		    // Handle compression or I/O errors
			return new byte[1];
		}
	}

	/**
	 * Obtains RGB data of the screen in the form of ByteBuffer<br>
	 * Warning: If the window is disabled, will just returns a black buffer.
	 *
	 * @return RGB data of the screen in the form of ByteBuffer
	 */
	private byte[] createDisplayBytes() {
		// Allocate memory for the RGB data of the screen
		ByteBuffer pixels = BufferUtils.createByteBuffer(3 * GameSetting.STAGE_WIDTH * GameSetting.STAGE_HEIGHT);
		pixels.clear();

		// Assign the RGB data of the screen to pixels, a ByteBuffer
		// variable
		glReadPixels(0, 0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, pixels);
		pixels.rewind();
		
		byte[] buffer = new byte[pixels.remaining()];
		pixels.get(buffer);

		return buffer;
	}

//	private BufferedImage createDisplayBufferedImage(){
//		int width = GameSetting.STAGE_WIDTH;
//		int height = GameSetting.STAGE_HEIGHT;
//		int bpp = 3;
//		BufferedImage src = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				int i = (x + width * y) * bpp;
//				int r = this.displayByteBuffer.get(i) & 0xFF;
//				int g = this.displayByteBuffer.get(i + 1) & 0xFF;
//				int b = this.displayByteBuffer.get(i + 2) & 0xFF;
//				src.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
//			}
//		}
//		boolean result = false;
//
//		try {
//		  result = ImageIO.write(src, "jpeg", new File("sample.jpeg"));
//		} catch (Exception e) {
//		  e.printStackTrace();
//		  result = false;
//		}
//		return src;
//	}

}
