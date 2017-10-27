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
import java.util.ArrayList;

import javax.imageio.ImageIO;

import image.Image;
import manager.GraphicManager;
import setting.ResourceSetting;

/** キャラクターの設定ファイルや画像等のリソースをロードするためのシングルトンなクラス */
public class ResourceLoader {

	private static ResourceLoader resourceLoader = new ResourceLoader();

	private ResourceLoader() {
		System.out.println("Create instance: " + ResourceLoader.class.getName());
	}

	public static ResourceLoader getInstance() {
		return resourceLoader;
	}

	public void loadResource(String[] characterName) {
		// String motionFilePath = "./data/character/" + characterName +
		// "/Motion.csv";
		String graphicPath = "./data/graphics/";

		// 波動拳読み込み
		loadImages(GraphicManager.getInstance().getProjectileImageContainer(),
				graphicPath + ResourceSetting.PROJECTILE_DIRECTORY, ResourceSetting.PROJECTILE_FILES);
		// 必殺技読み込み
		loadImages(GraphicManager.getInstance().getUltimateAttackImageContainer(),
				graphicPath + ResourceSetting.SUPER_DIRECTORY, ResourceSetting.SUPER_FILES);
		// 1~4の文字カウンタ読み込み
		loadImages(GraphicManager.getInstance().getCounterTextImageContainer(),
				graphicPath + ResourceSetting.COUNTER_DIRECTORY, ResourceSetting.COUNTER_TEXT_FILES);
		// "Hit"文字読み込み
		loadImages(GraphicManager.getInstance().getHitTextAttackImageContainer(),
				graphicPath + ResourceSetting.HIT_TEXT_DIRECTORY, ResourceSetting.HIT_TEXT_FILE);
		// アッパー画像読み込み
		loadUpperImages(graphicPath + ResourceSetting.UPPER_DIRECTORY, characterName);
		//ヒットエフェクト読み込み
		loadHitEffectImage(graphicPath + ResourceSetting.HIT_DIRECTORIES);

	}

	/**
	 * ディレクトリから画像を読み込み、リストに格納する
	 *
	 * @param container
	 *            画像を格納するリスト
	 * @param path
	 *            読み込む画像までのパス
	 * @param resourceName
	 *            読み込む画像のファイル名が格納されている配列
	 */
	private void loadImages(ArrayList<Image> container, String path, String[] resourceName) {
		for (int i = 0; i < resourceName.length; i++) {
			container.add(loadImage(path + resourceName[i]));
		}
	}

	/**
	 * アッパーの画像を読み込み、2次元配列に格納する
	 *
	 * @param path
	 *            読み込む画像までのパス
	 * @param characterName
	 *            P1, P2の使用キャラクタ名が格納された配列
	 */
	private void loadUpperImages(String path, String[] characterName) {
		String[][] upper = { ResourceSetting.UPPER_FILES.clone(), ResourceSetting.UPPER_FILES.clone() };

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < upper[i].length; j++) {
				upper[i][j] += characterName[i].equals("ZEN") ? ".png" : "_" + characterName[i] + ".png";
				GraphicManager.getInstance().getUpperImageContainer()[i][j] = loadImage(path + upper[i][j]);
			}
		}
	}

	/**
	 * 攻撃が当たったときに描画するエフェクトの画像を読み込み、2次元配列に格納する
	 */
	private void loadHitEffectImage(String path) {
		for (int i = 0; i < ResourceSetting.HIT_DIRECTORIES.length; i++) {
			for (int j = 0; j < ResourceSetting.HIT_FILES.length; j++) {
				GraphicManager.getInstance().getHitEffectImageContaier()[i][j] = loadImage(
						path + ResourceSetting.HIT_FILES[j]);
			}
		}
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

	public void loadCharacterFile(String path, String characterName) {
		BufferedReader br = openReadFile("./data/character/" + characterName + "/Motion.csv");

		try {
			String line;
			br.readLine(); // ignore header

			while ((line = br.readLine()) != null) {
				String[] st = line.split(",", 0);

				// ここで必要なデータをMotion.csvから読み込み

			}

			br.close();

		} catch (IOException e) {
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
