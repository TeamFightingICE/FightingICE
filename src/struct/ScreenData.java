package struct;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

public class ScreenData {

	/**
	 * Pixel data of the screen are saved in the form of ByteBuffer
	 */
	private ByteBuffer displayByteBuffer;

	/**
	 * Image of the screen
	 */
	private BufferedImage screenImage;

	public ScreenData() {
		this.displayByteBuffer = null;
		this.screenImage = null;
	}

	public ScreenData( ByteBuffer displayByteBuffer, BufferedImage screenImage){
		this.displayByteBuffer = displayByteBuffer;
		this.screenImage = screenImage;
	}

	public ScreenData(ScreenData screenData){
		this.displayByteBuffer = screenData.getDisplayByteBuffer();
		this.screenImage = screenData.getScreenImage();
	}


	/**
	 * Obtains RGB data of the screen in the form of ByteBuffer<br>
	 * Warning: If the window is disabled, will just return a black buffer
	 *
	 * @return The RGB data of the screen in the form of ByteBuffer
	 */
	public ByteBuffer getDisplayByteBuffer() {
		return this.displayByteBuffer;
	}

	/**
	 * Obtains RGB data of the screen in the form of byte[]<br>
	 * Warning: If the window is disabled, will just return a black buffer
	 *
	 * @return The RGB data of the screen in the form of byte[]
	 */
	public byte[] getDisplayByteBufferAsBytes() {
		byte[] buffer = new byte[this.displayByteBuffer.remaining()];
		this.displayByteBuffer.get(buffer);
		return buffer;
	}

	/**
	 * Obtains RGB data or the grayScale data of the screen in the form of
	 * byte[]<br>
	 * Warning: This method doesn't return exactly the same buffer as
	 * getDisplayByteBufferAsBytes()
	 *
	 * @param newWidth
	 *            The width in pixel for the scaled image
	 * @param newHeight
	 *            The height in pixel for the scaled image
	 * @param grayScale
	 *            True to use grayScale for the scaled image (1 byte per pixel
	 *            instead of 3 bytes per pixel with RGB)
	 * @return The RGB data or the grayScale data of the screen in the form of
	 *         byte[]
	 */
	public byte[] getDisplayByteBufferAsBytes(int newWidth, int newHeight, boolean grayScale) {
		// Scale the image (and grayScale too)
		BufferedImage newImage = new BufferedImage(newWidth, newHeight,
				grayScale ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_INT_RGB);
		{
			Graphics2D g = newImage.createGraphics();
			g.drawImage(this.screenImage, 0, 0, newWidth, newHeight, null);
			g.dispose();
		}

		// Convert it back to array of bytes
		byte[] dst;

		if (grayScale) {
			dst = ((DataBufferByte) newImage.getData().getDataBuffer()).getData();
		} else {
			dst = new byte[newWidth * newHeight * 3];

			int[] array = ((DataBufferInt) newImage.getRaster().getDataBuffer()).getData();
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

	public BufferedImage getScreenImage() {
		return this.screenImage;
	}


}
