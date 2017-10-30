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

import image.CharacterActionImage;
import image.Image;
import manager.GraphicManager;

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
		String characterGraphicPath = "./data/characters/";

		// 波動拳読み込み
		/*loadImages(GraphicManager.getInstance().getProjectileImageContainer(),
				graphicPath + ResourceSetting.PROJECTILE_DIRECTORY);
		System.out.println("波動拳読み込み完了");
		// 必殺技読み込み
		loadImages(GraphicManager.getInstance().getUltimateAttackImageContainer(),
				graphicPath + ResourceSetting.SUPER_DIRECTORY);
		System.out.println("必殺技読み込み完了");
		// 1~4の文字カウンタ読み込み
		loadImages(GraphicManager.getInstance().getCounterTextImageContainer(),
				graphicPath + ResourceSetting.COUNTER_DIRECTORY);

		// "Hit"文字読み込み
		loadImages(GraphicManager.getInstance().getHitTextImageContainer(),
				graphicPath + ResourceSetting.HIT_TEXT_DIRECTORY);
		// 背景画像読み込み
		loadImages(GraphicManager.getInstance().getBackgroundImage(),
				graphicPath + ResourceSetting.BACKGROUND_DIRECTORY);
		// アッパー画像読み込み
		loadUpperImages(graphicPath + ResourceSetting.UPPER_DIRECTORY, characterName);
		// ヒットエフェクト読み込み
		loadHitEffectImage(graphicPath + ResourceSetting.HIT_DIRECTORY);*/

		loadCharacterImages(characterGraphicPath, characterName);

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

	public void loadCharacterImages(String path, String[] characterName) {
		for (int i = 0; i < 2; i++) {
			try {
				BufferedReader br = openReadFile("./data/characters/" + characterName[i] + "/Motion.csv");

				String line;
				br.readLine(); // ignore header

				while ((line = br.readLine()) != null) {
					String[] data = line.split(",", 0);

					String actionName = data[0];
					int frameNumber = Integer.valueOf(data[1]);
					String imageName = data[33];
					Image[] actionImage = new Image[frameNumber];
					String dirPath = path + characterName[i] + "/graphics/" + imageName;
					System.out.println(actionName);

					// 指定キャラクターのグラフィックが格納されているディレクトリを取得
					File[] files = new File(dirPath).listFiles();
					System.out.println(dirPath);
					int num = 0;
					for (int j = 0; j < actionImage.length; j++) {
						actionImage[j] = loadImage(files[j].getPath());
						num++;
					}

					// 画像数がMotion.csvで定められているフレーム数よりも少ない場合、不足分を補う
					if (num < frameNumber) {
						for (int j = num; j < frameNumber; j++) {
							actionImage[j] = actionImage[0];
						}
					}
					CharacterActionImage temp = new CharacterActionImage(characterName[i], actionName, frameNumber,
							actionImage);
					GraphicManager.getInstance().getCharacterImageContainer().add(temp);
				}

				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
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
	private void loadImages(ArrayList<Image> container, String path) {
		File[] files = new File(path).listFiles();
		for (File file : files) {
			container.add(loadImage(file.getPath()));
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
		for (int i = 0; i < 2; i++) {
			String tempPath = path;

			switch (characterName[i]) {
			case "ZEN":
				tempPath += "ZEN/";
				break;
			case "GARNET":
				tempPath += "GARNET/";
				break;
			default:
				tempPath += "LUD/";
			}

			File[] files = new File(tempPath).listFiles();
			for (int j = 0; j < files.length; j++) {
				System.out.println(files[j].getPath());
				GraphicManager.getInstance().getUpperImageContainer()[i][j] = loadImage(files[j].getPath());
			}
		}
	}

	/**
	 * 攻撃が当たったときに描画するエフェクトの画像を読み込み、2次元配列に格納する
	 *
	 * @param path
	 *            読み込む画像までのパス
	 */
	private void loadHitEffectImage(String path) {
		File[] dir = new File(path).listFiles();
		for (int i = 0; i < dir.length; i++) {
			File[] files = new File(dir[i].getPath()).listFiles();

			for (int j = 0; j < files.length; j++) {
				GraphicManager.getInstance().getHitEffectImageContaier()[i][j] = loadImage(files[j].getPath());
			}
		}
	}

}
