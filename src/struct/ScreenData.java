package struct;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import setting.GameSetting;

/**
 * ゲーム画面のデータを扱うクラス
 */
public class ScreenData {

	/**
	 * Pixel data of the screen are saved in the form of ByteBuffer
	 */
	private ByteBuffer displayByteBuffer;

	/**
	 * Image of the screen
	 */
	private BufferedImage screenImage;

	/**
	 * ゲーム画面のデータを初期化するコンストラクタ
	 */
	public ScreenData() {
		this.displayByteBuffer = createDisplayByteBuffer();
		this.screenImage = new BufferedImage(GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT,
				BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * 指定された画像でゲーム画面を更新するコンストラクタ
	 *
	 * @param screenImage
	 *            ゲーム画面に使う画像
	 *
	 * @see BufferedImage
	 */
	public ScreenData(BufferedImage screenImage) {
		this.displayByteBuffer = createDisplayByteBuffer();
		this.screenImage = screenImage;
	}

	/**
	 * 指定されたデータでゲーム画面を更新するコンストラクタ
	 *
	 * @param screenData
	 *            ゲーム画面のデータ
	 *
	 * @see ScreenData
	 */
	public ScreenData(ScreenData screenData) {
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

	/**
	 * 現在のゲーム画面の画像を返すメソッド
	 *
	 * @return 現在のゲーム画面の画像
	 */
	public BufferedImage getScreenImage() {
		return this.screenImage;
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
