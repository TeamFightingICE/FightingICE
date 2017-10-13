package loader;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import image.Image;

/** キャラクターの設定ファイルや画像等のリソースをロードするためのシングルトンなクラス */
public class ResourceLoader {

	private static ResourceLoader resourceLoader = new ResourceLoader();

	private ResourceLoader(){
		System.out.println("Create instance: " + ResourceLoader.class.getName());
	}

	public static ResourceLoader getInstance() {
		return resourceLoader;
	}

	/** 画像を読み込み，読み込んだ画像の情報を返す */
	public Image loadImage(String filePath) {
		BufferedImage bimg = null;

		try {
			bimg = ImageIO.read(new FileInputStream(new File(filePath)));
			return loadTextureFromBufferedImage(bimg);

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Image loadTextureFromBufferedImage(BufferedImage bimg){
		// Gather all the pixels
		int[] pixels = new int[bimg.getWidth() * bimg.getHeight()];
		bimg.getRGB(0, 0, bimg.getWidth(), bimg.getHeight(), pixels, 0, bimg.getWidth());

		// Create a ByteBuffer
		ByteBuffer buffer = ByteBuffer.allocateDirect(bimg.getWidth() * bimg.getHeight() * 4)
				.order(ByteOrder.nativeOrder());

		// Iterate through all the pixels and add them to the ByteBuffer
	/*	for (int y = 0; y < bimg.getHeight(); y++) {
			for (int x = 0; x < bimg.getWidth(); x++) {
				// Select the pixel
				int pixel = pixels[y * bimg.getWidth() + x];

				// Add the RED component
				buffer.put(((byte) ((pixel >> 16) & 0xFF)));
				// Add the GREEN component
				buffer.put(((byte) ((pixel >> 8) & 0xFF)));
				// Add the BLUE component
				buffer.put((byte) (pixel & 0xFF));
				// Add the ALPHA component
				buffer.put(((byte) ((pixel >> 24) & 0xFF)));

			}
		}
		buffer.flip();*/

		// Generate a texture ID
		int textureId = glGenTextures();

		// Bind the ID to the context
		glBindTexture(GL_TEXTURE_2D, textureId);

		// Setup texture scaling filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Send texture data to OpenGL
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 256, 256/*bimg.getWidth(), bimg.getHeight()*/, 0, GL_RGBA, GL_UNSIGNED_BYTE,
				buffer);
		buffer = null;

		return new Image(textureId, bimg);
	}

}
