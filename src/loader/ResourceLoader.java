package loader;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import aiinterface.AIController;
import aiinterface.AIInterface;
import image.CharacterActionImage;
import image.Image;
import manager.GraphicManager;
import manager.SoundManager;
import setting.GameSetting;
import setting.LaunchSetting;
import setting.ResourceSetting;

/** キャラクターの設定ファイルや画像等のリソースをロードするためのシングルトンなクラス */
public class ResourceLoader {

	private static ResourceLoader resourceLoader = new ResourceLoader();

	private ArrayList<String> loadedGraphics;

	private ResourceLoader() {
		System.out.println("Create instance: " + ResourceLoader.class.getName());
		this.loadedGraphics = new ArrayList<String>();
	}

	public static ResourceLoader getInstance() {
		return resourceLoader;
	}

	public void loadResource() {
		String graphicPath = "./data/graphics/";
		String characterGraphicPath = "./data/characters/";

		// 波動拳読み込み
		if (!isLoaded("hadouken")) {
			loadImages(GraphicManager.getInstance().getProjectileImageContainer(),
					graphicPath + ResourceSetting.PROJECTILE_DIRECTORY);

			addLoadedGraphic("hadouken");
			System.out.println("波動拳読み込み完了");
		}

		// 必殺技読み込み
		if (!isLoaded("super")) {
			loadImages(GraphicManager.getInstance().getUltimateAttackImageContainer(),
					graphicPath + ResourceSetting.SUPER_DIRECTORY);

			addLoadedGraphic("super");
			System.out.println("必殺技読み込み完了");
		}

		// 0~9の文字カウンタ読み込み
		if (!isLoaded("hitCounter")) {
			loadImages(GraphicManager.getInstance().getCounterTextImageContainer(),
					graphicPath + ResourceSetting.COUNTER_DIRECTORY);

			addLoadedGraphic("hitCounter");
			System.out.println("文字カウンタ読み込み完了");
		}

		// "Hit"文字読み込み
		if (!isLoaded("hitText")) {
			loadImages(GraphicManager.getInstance().getHitTextImageContainer(),
					graphicPath + ResourceSetting.HIT_TEXT_DIRECTORY);

			addLoadedGraphic("hitText");
			System.out.println("Hit文字読み込み完了");
		}

		// ヒットエフェクト読み込み
		if (!isLoaded("hitEffect")) {
			loadHitEffectImage(graphicPath + ResourceSetting.HIT_DIRECTORY);

			addLoadedGraphic("hitEffect");
			System.out.println("ヒットエフェクト読み込み完了");
		}

		// 背景画像読み込み
		if (!isLoaded("background")) {
			loadBackgroundImage(GraphicManager.getInstance().getBackgroundImage(),
					graphicPath + ResourceSetting.BACKGROUND_DIRECTORY);

			addLoadedGraphic("background");
			System.out.println("背景読み込み完了");
		}

		// アッパー画像読み込み
		loadUpperImages(graphicPath + ResourceSetting.UPPER_DIRECTORY);
		System.out.println("アッパー読み込み完了");

		// キャラクター画像読み込み
		loadCharacterImages(characterGraphicPath);
		System.out.println("キャラクター画像読み込み完了");

		// サウンドエフェクト読み込み
		if (!isLoaded("soundEffect")) {
			loadSoundEffect();

			addLoadedGraphic("soundEffect");
			System.out.println("サウンドエフェクト読み込み完了");
		}

		// BGM読み込み
		if (!isLoaded("BGM")) {
			loadBackGroundMusic();

			addLoadedGraphic("BGM");
			System.out.println("BGM読み込み完了");
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

	/**
	 * 指定されたAI名のjarファイルを読み込み、その情報を格納したコントローラを返す
	 *
	 * @param aiName
	 *            読み込みたいAIの名前
	 *
	 * @return 読み込んだAIの情報を格納したコントローラ<br>
	 *         読み込んだAIが無ければnullを返す
	 */
	public AIController loadAI(String aiName) {
		File file = new File("./data/ai/" + aiName + ".jar");

		try {
			ClassLoader cl = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() });
			Class<?> c = cl.loadClass(aiName);
			AIInterface ai = (AIInterface) c.newInstance();
			return new AIController(ai);
		} catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 指定したディレクトリに格納されている、すべてのファイルの拡張子を除いた名前を返す
	 *
	 * @param directoryPath
	 *            参照したいファイルが格納されているディレクトリまでのパス
	 * @param extension
	 *            読み込みたいファイルの拡張子
	 *
	 * @return 読み込んだすべてのファイルの、拡張子を除いた名前が格納されている配列<br>
	 *         読み込んだファイルが無ければnullを返す
	 */
	public ArrayList<String> loadFileNames(String directoryPath, String extension) {
		File[] files = new File(directoryPath).listFiles();
		ArrayList<String> fileNames = new ArrayList<String>();

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(extension)) {
				String fileName = files[i].getName();
				fileNames.add(fileName.substring(0, fileName.lastIndexOf(".")));
			}
		}
		return fileNames;

	}

	/**
	 * キャラクターの画像を読み込み、リストに格納する
	 *
	 * @param path
	 *            各キャラクターの画像が格納されているディレクトリまでのパス
	 * @param characterNames
	 *            P1, P2の使用キャラクターの名前が格納されている配列
	 */
	public void loadCharacterImages(String path) {
		for (int i = 0; i < 2; i++) {
			if (!isLoaded(LaunchSetting.characterNames[i] + "_Graphic")) {
				try {
					BufferedReader br = openReadFile(path + LaunchSetting.characterNames[i] + "/Motion.csv");

					String line;
					br.readLine(); // ignore header

					while ((line = br.readLine()) != null) {
						String[] data = line.split(",", 0);
						String actionName = data[0];
						int frameNumber = Integer.valueOf(data[1]);
						String imageName = data[33];

						Image[] actionImage = new Image[frameNumber];
						String dirPath = path + LaunchSetting.characterNames[i] + "/graphics/" + imageName;

						// 指定キャラクターのグラフィックが格納されているディレクトリを取得
						File[] files = new File(dirPath).listFiles();
						int num = 0;
						for (int j = 0; j < files.length; j++) {
							if (j >= frameNumber) {
								break;
							}

							actionImage[j] = loadImage(files[j].getPath());
							num++;
						}

						// 画像数がMotion.csvで定められているフレーム数よりも少ない場合、不足分を補う
						if (num < frameNumber) {
							for (int j = num; j < frameNumber; j++) {
								actionImage[j] = actionImage[0];
							}
						}
						CharacterActionImage temp = new CharacterActionImage(LaunchSetting.characterNames[i],
								actionName, frameNumber, actionImage);
						GraphicManager.getInstance().getCharacterImageContainer().add(temp);
					}

					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			addLoadedGraphic(LaunchSetting.characterNames[i] + "_Graphic");
		}
	}

	public void loadBackgroundImage(ArrayList<Image> container, String path) {
		BufferedImage bg = null;

		switch (LaunchSetting.backgroundType) {
		case BLACK:
			bg = new BufferedImage(GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, BufferedImage.TYPE_BYTE_BINARY,
					new IndexColorModel(1, 1, new byte[] { 0 }, new byte[] { 0 }, new byte[] { 0 }));
			container.add(loadTextureFromBufferedImage(bg));
			break;

		case GREY:
			bg = new BufferedImage(GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, BufferedImage.TYPE_BYTE_BINARY,
					new IndexColorModel(1, 1, new byte[] { (byte) 128 }, new byte[] { (byte) 128 },
							new byte[] { (byte) 128 }));
			container.add(loadTextureFromBufferedImage(bg));

		default:
			loadImages(container, path);
			break;
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
	private void loadUpperImages(String path) {
		for (int i = 0; i < 2; i++) {
			String tempPath = path;

			switch (LaunchSetting.characterNames[i]) {
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

	private void loadSoundEffect() {
		File[] files = new File(ResourceSetting.SOUND_DIRECTORY).listFiles();

		for (File file : files) {
			if (!file.getName().equals(ResourceSetting.BGM_FILE)) {
				SoundManager.getInstance().getSoundEffect().put(file.getName(),
						SoundManager.getInstance().loadSoundResource(file.getPath(), false));
			}
		}
	}

	private void loadBackGroundMusic() {
		SoundManager.getInstance().setBackGroundMusic(SoundManager.getInstance()
				.loadSoundResource(ResourceSetting.SOUND_DIRECTORY + ResourceSetting.BGM_FILE, true));
	}

	public boolean isLoaded(String graphicName) {
		return this.loadedGraphics.contains(graphicName);
	}

	public void addLoadedGraphic(String graphicName) {
		this.loadedGraphics.add(graphicName);
	}

}
