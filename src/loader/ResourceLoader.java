package loader;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import image.CharacterActionImage;
import image.Image;

/** キャラクターの設定ファイルや画像等のリソースをロードするためのシングルトンなクラス */
public class ResourceLoader {

	private static ResourceLoader resourceLoader = new ResourceLoader();

	private ResourceLoader() {
		System.out.println("Create instance: " + ResourceLoader.class.getName());
	}

	public static ResourceLoader getInstance() {
		return resourceLoader;
	}

	/**
	 * 読み込みたいファイルを開き，そのBufferedReaderを返す
	 *
	 * @param filePath
	 *            読み込みたいファイルまでのパス
	 */
	public BufferedReader openReadFile(String filePath) {
		try {
			File file = new File(filePath);
			return new BufferedReader(new FileReader(file));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void loadCharacterFile(LinkedList<CharacterActionImage> ic, String characterName){
		BufferedReader br = openReadFile("./data/character/"+characterName+"/Motion.csv");

		try{
			String line;
			br.readLine(); // ignore header

			while((line = br.readLine()) != null){
				String[] st = line.split(",",0);

//ここで必要なデータをMotion.csvから読み込み

			}

			br.close();

		}catch(IOException e){
			e.printStackTrace();
		}

	}



	/**
	 * 画像を読み込み，読み込んだ画像の情報を返す
	 *
	 * @param filePath
	 *            読み込みたい画像までのパス
	 */
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

	/**
	 * BufferedImageからテクスチャを読み込み，その結果をOpenGLに転送し，
	 * 割り当てられたテクスチャIDとBufferedImageを返す．<br>
	 *
	 * @param bimg
	 *            テクスチャを読み込むBufferedImage
	 */
	public Image loadTextureFromBufferedImage(BufferedImage bimg) {
		// Gather all the pixels
		int[] pixels = new int[bimg.getWidth() * bimg.getHeight()];
		bimg.getRGB(0, 0, bimg.getWidth(), bimg.getHeight(), pixels, 0, bimg.getWidth());

		// Create a ByteBuffer
		ByteBuffer buffer = ByteBuffer.allocateDirect(bimg.getWidth() * bimg.getHeight() * 4)
				.order(ByteOrder.nativeOrder());

		// Iterate through all the pixels and add them to the ByteBuffer
		for (int y = 0; y < bimg.getHeight(); y++) {
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
		buffer.flip();

		// Generate a texture ID
		int textureId = glGenTextures();

		// Bind the ID to the context
		glBindTexture(GL_TEXTURE_2D, textureId);

		// Setup texture scaling filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Send texture data to OpenGL
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, bimg.getWidth(), bimg.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
				buffer);
		buffer = null;

		return new Image(textureId, bimg);
	}

}
