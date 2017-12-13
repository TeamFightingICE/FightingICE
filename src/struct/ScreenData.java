package struct;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import setting.GameSetting;

/**
 * ゲーム画面の画像や背景色などの画面情報を扱うクラス
 */
public class ScreenData {

	/**
	 * Pixel data of the screen are saved in the form of ByteBuffer
	 */
	private ByteBuffer displayByteBuffer;

	/**
	 * ゲーム画面のデータを初期化するコンストラクタ
	 */
	public ScreenData() {
		this.displayByteBuffer = createDisplayByteBuffer();
	}

	/**
	 * 指定されたデータでゲーム画面の画素情報を作成するコンストラクタ
	 *
	 * @param screenData
	 *            ゲーム画面のデータ
	 */
	public ScreenData(ScreenData screenData) {
		this.displayByteBuffer = screenData.getDisplayByteBuffer();
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
		int width = GameSetting.STAGE_WIDTH;
		int height = GameSetting.STAGE_HEIGHT;
		int bpp = 3;
		BufferedImage src = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		if (this.displayByteBuffer != null) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int i = (x + width * y) * bpp;
					int r = this.displayByteBuffer.get(i) & 0xFF;
					int g = this.displayByteBuffer.get(i + 1) & 0xFF;
					int b = this.displayByteBuffer.get(i + 2) & 0xFF;
					src.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
				}
			}

			// 画像のリサイズ
			AffineTransformOp xform = new AffineTransformOp(AffineTransform
					.getScaleInstance((double) newWidth / src.getWidth(), (double) newHeight / src.getHeight()),
					AffineTransformOp.TYPE_BILINEAR);
			BufferedImage resize = new BufferedImage(newWidth, newHeight, src.getType());
			xform.filter(src, resize);

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
		} else {
			return null;
		}
	}

	/**
	 * Obtain RGB data of the screen in the form of ByteBuffer Warning: If the
	 * window is disabled, will just returns a black buffer
	 *
	 * @return RGB data of the screen in the form of ByteBuffer
	 */
	private ByteBuffer createDisplayByteBuffer() {
		// Allocate memory for the RGB data of the screen
		ByteBuffer pixels = BufferUtils.createByteBuffer(3 * GameSetting.STAGE_WIDTH * GameSetting.STAGE_HEIGHT);
		pixels.clear();

		// Assign the RGB data of the screen to pixels, a ByteBuffer
		// variable
		glReadPixels(0, 0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, pixels);
		pixels.rewind();

		return pixels;
	}

}
