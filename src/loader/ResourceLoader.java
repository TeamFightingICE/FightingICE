package loader;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import aiinterface.AIController;
import aiinterface.AIInterface;
import image.CharacterActionImage;
import image.Image;
import manager.GraphicManager;
import manager.SoundManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import setting.ResourceSetting;

/**
 * キャラクターの設定ファイルや画像等のリソースをロードするためのシングルトンパターンクラス．
 */
public class ResourceLoader {

	/**
	 * 読み込み済みのリソースのリスト．
	 */
	private ArrayList<String> loadedResources;

	/**
	 * クラスコンストラクタ．
	 */
	private ResourceLoader() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + ResourceLoader.class.getName());
		this.loadedResources = new ArrayList<String>();
	}

	/**
	 * ResourceLoaderクラスの唯一のインスタンスを取得する．
	 *
	 * @return ResourceLoaderクラスの唯一のインスタンス
	 */
	public static ResourceLoader getInstance() {
		return ResourceLoaderHolder.instance;
	}

	/**
	 * getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス．
	 */
	private static class ResourceLoaderHolder {
		private static final ResourceLoader instance = new ResourceLoader();
	}

	/**
	 * ゲームに必要な画像と音声をまとめて読み込むメソッド．
	 */
	public void loadResource() {
		if (!LaunchSetting.isExpectedProcessingMode(LaunchSetting.HEADLESS_MODE)) return;
		
		Logger.getAnonymousLogger().log(Level.INFO, "Loading the resources");
		String graphicPath = "./data/graphics/";
		String characterGraphicPath = "./data/characters/";

		// 波動拳読み込み
		if (!isLoaded("hadouken")) {
			loadImages(GraphicManager.getInstance().getProjectileImageContainer(),
					graphicPath + ResourceSetting.PROJECTILE_DIRECTORY);

			addLoadedResource("hadouken");
			Logger.getAnonymousLogger().log(Level.INFO, "Hadouken images have been loaded.");
		}

		// 必殺技読み込み
		if (!isLoaded("super")) {
			loadImages(GraphicManager.getInstance().getUltimateAttackImageContainer(),
					graphicPath + ResourceSetting.SUPER_DIRECTORY);

			addLoadedResource("super");
			Logger.getAnonymousLogger().log(Level.INFO, "Ultimate attack images have been loaded.");
		}

		// 0~9の文字カウンタ読み込み
		if (!isLoaded("hitCounter")) {
			loadImages(GraphicManager.getInstance().getCounterTextImageContainer(),
					graphicPath + ResourceSetting.COUNTER_DIRECTORY);

			addLoadedResource("hitCounter");
			Logger.getAnonymousLogger().log(Level.INFO, "Hit counter text images have been loaded.");
		}

		// "Hit"文字読み込み
		if (!isLoaded("hitText")) {
			loadImages(GraphicManager.getInstance().getHitTextImageContainer(),
					graphicPath + ResourceSetting.HIT_TEXT_DIRECTORY);

			addLoadedResource("hitText");
			Logger.getAnonymousLogger().log(Level.INFO, "Hit text image has been loaded.");
		}

		// ヒットエフェクト読み込み
		if (!isLoaded("hitEffect")) {
			loadHitEffectImage(graphicPath + ResourceSetting.HIT_DIRECTORY);

			addLoadedResource("hitEffect");
			Logger.getAnonymousLogger().log(Level.INFO, "Hit effect images have been loaded.");
		}

		// 背景画像読み込み
		if (!isLoaded("background")) {
			loadBackgroundImage(GraphicManager.getInstance().getBackgroundImage(),
					graphicPath + ResourceSetting.BACKGROUND_DIRECTORY);

			addLoadedResource("background");
			Logger.getAnonymousLogger().log(Level.INFO, "Background image has been loaded.");
		}

		// アッパー画像読み込み
		loadUpperImages(graphicPath + ResourceSetting.UPPER_DIRECTORY);
		Logger.getAnonymousLogger().log(Level.INFO, "Upper attack images have been loaded.");

		// キャラクター画像読み込み
		loadCharacterImages(characterGraphicPath);
		Logger.getAnonymousLogger().log(Level.INFO, "Character images have been loaded.");

		// サウンドエフェクト読み込み
		if (!isLoaded("soundEffect") && FlagSetting.enableBuiltinSound) {
			loadSoundEffect();

			addLoadedResource("soundEffect");
			Logger.getAnonymousLogger().log(Level.INFO, "Sound effects have been loaded.");
		}

		// BGM読み込み
		if (!isLoaded("BGM") && FlagSetting.enableBuiltinSound) {
			loadBackGroundMusic();

			addLoadedResource("BGM");
			Logger.getAnonymousLogger().log(Level.INFO, "BGM has been loaded.");
		}

	}

	/**
	 * 読み込みたいファイルを開き，そのBufferedReaderを返す．
	 *
	 * @param filePath
	 *            読み込みたいファイルまでのパス
	 *
	 *
	 * @return BufferedReader
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
	 * 読み込みたいファイルを開き，その出力ストリームを返す．
	 *
	 * @param filePath
	 *            読み込みたいファイルまでのパス
	 *
	 * @return 出力ストリーム
	 */
	public DataOutputStream openDataOutputStream(String filePath) {
		try {
			File file = new File(filePath);
			return new DataOutputStream(new FileOutputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 読み込みたいファイルを開き，そのBufferedReaderを返す．
	 *
	 * @param filePath
	 *            読み込みたいファイルまでのパス
	 * @param mode
	 *            バイトをファイルの最後に書き込むかどうか
	 *
	 * @return BufferedReader
	 */
	public PrintWriter openWriteFile(String filePath, boolean mode) {
		try {
			File file = new File(filePath);
			return new PrintWriter(new BufferedWriter(new FileWriter(file, mode)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * AIが入っているディレクトリからすべてのAI名を読み込み，AI名を登録したリストを返す．
	 *
	 * @return AI名を格納したリスト
	 */
	public ArrayList<String> loadAllAINames() {
		String[] files = new File("./data/ai").list();
		ArrayList<String> temp = new ArrayList<String>();

		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".jar")) {
				temp.add(files[i].substring(0, files[i].indexOf(".jar")));
			}
		}

		return temp;
	}

	/**
	 * 指定されたAI名のjarファイルを読み込み、AI情報を格納したコントローラを返す．
	 *
	 * @param aiName
	 *            読み込みたいAIの名前
	 *
	 * @return 読み込んだAIの情報を格納したコントローラ<br>
	 *         読み込んだAIが無ければnullを返す．
	 */
	public AIController loadAI(String aiName) {
		File file = new File("./data/ai/" + aiName + ".jar");

		try {
			ClassLoader cl = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() });
			Class<?> c = cl.loadClass(aiName);
			AIInterface ai = (AIInterface) c.getDeclaredConstructor().newInstance();
			return new AIController(ai);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 指定したディレクトリに格納されている、すべてのファイルの拡張子を除いた名前を返すメソッド．<br>
	 * 引数で読み込みたいファイルの拡張子を指定する．
	 *
	 * @param directoryPath
	 *            参照したいファイルが格納されているディレクトリまでのパス
	 * @param extension
	 *            読み込みたいファイルの拡張子
	 *
	 * @return 読み込んだすべてのファイルの，拡張子を除いた名前が格納されている配列<br>
	 *         読み込んだファイルが無ければnullを返す．
	 */
	public ArrayList<String> loadFileNames(String directoryPath, String extension) {
		File[] files = new File(directoryPath).listFiles();
		sortByFileName(files);
		ArrayList<String> fileNames = new ArrayList<String>();

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(extension)) {
				String fileName = files[i].getName();
				fileNames.add(fileName.substring(0, fileName.lastIndexOf(".")));
			}
		}

		return fileNames;
	}
	
	public ArrayList<String> loadSoundNames() {
		File[] files = new File(ResourceSetting.SOUND_DIRECTORY).listFiles();
		sortByFileName(files);
		ArrayList<String> soundNames = new ArrayList<String>();
		
		for (File file: files) {
			if (file.isDirectory()) {
				soundNames.add(file.getName());
			}
		}
		return soundNames;
	}

	/**
	 * キャラクターの画像を読み込み、リストに格納する．
	 *
	 * @param path
	 *            各キャラクターの画像が格納されているディレクトリまでのパス
	 */
	public void loadCharacterImages(String path) {
		for (int i = 0; i < 2; i++) {
			if (!isLoaded(LaunchSetting.characterNames[i] + "_Graphic") ) {
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
						sortByFileName(files);

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
			addLoadedResource(LaunchSetting.characterNames[i] + "_Graphic");
		}
	}

	/**
	 * 背景画像を読み込む．
	 *
	 * @param container
	 *            画像を格納するリスト
	 * @param path
	 *            背景画像ディレクトリのパス
	 */
	public void loadBackgroundImage(ArrayList<Image> container, String path) {
		switch (LaunchSetting.backgroundType) {
		case BLACK:
			container.add(loadTextureFromBufferedImage(createBackgroundImage(0, 0, 0)));
			break;
			
		case GREY:
			container.add(loadTextureFromBufferedImage(createBackgroundImage(128, 128, 128)));
			break;
			
		default:
			loadImages(container, path);
			break;
		}
	}
	
	private BufferedImage createBackgroundImage(int r, int g, int b) {
		Color color = new Color(r, g, b);
		BufferedImage bg = new BufferedImage(GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < bg.getWidth(); x++) {
			for (int y = 0; y < bg.getHeight(); y++) {
				bg.setRGB(x, y, color.getRGB());
			}
		}
		return bg;
	}

	/**
	 * 画像を読み込み，読み込んだ画像の情報を返す．
	 *
	 * @param filePath
	 *            読み込みたい画像までのパス
	 *
	 * @return 読み込んだ画像の情報<br>
	 *         画像を読み込めなかった場合はnullを返す．
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
	 * BufferedImageからテクスチャを読み込む．<br>
	 * 読み込み結果をOpenGLに転送し，割り当てられたテクスチャIDとBufferedImageの情報を含んだ新たな画像クラスのインスタンスを返す．
	 *
	 * @param bimg
	 *            テクスチャを読み込むBufferedImage
	 *
	 * @return 新たな画像クラスのインスタンス
	 */
	public Image loadTextureFromBufferedImage(BufferedImage bimg) {
		int textureId = -1;
		
		// Gather all the pixels
		int[] pixels = new int[bimg.getWidth() * bimg.getHeight()];
		bimg.getRGB(0, 0, bimg.getWidth(), bimg.getHeight(), pixels, 0, bimg.getWidth());
		
		if (LaunchSetting.isExpectedProcessingMode(LaunchSetting.STANDARD_MODE)) {
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
			textureId = glGenTextures();

			// Bind the ID to the context
			glBindTexture(GL_TEXTURE_2D, textureId);

			// Setup texture scaling filtering
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

			// Send texture data to OpenGL
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, bimg.getWidth(), bimg.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
					buffer);
			buffer = null;
		}

		return new Image(textureId, bimg);
	}

	/**
	 * 指定したディレクトリから画像を読み込み、リストに格納する．
	 *
	 * @param container
	 *            画像を格納するリスト
	 * @param path
	 *            読み込むディレクトリのパス
	 */
	private void loadImages(ArrayList<Image> container, String path) {
		File[] files = new File(path).listFiles();
		sortByFileName(files);

		for (File file : files) {
			container.add(loadImage(file.getPath()));
		}
	}

	/**
	 * アッパーの画像を読み込み、2次元配列に格納する．
	 *
	 * @param path
	 *            読み込む画像までのパス
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
			sortByFileName(files);

			for (int j = 0; j < files.length; j++) {
				GraphicManager.getInstance().getUpperImageContainer()[i][j] = loadImage(files[j].getPath());
			}
		}
	}

	/**
	 * 攻撃が当たったときに描画するエフェクトの画像を読み込み，2次元配列に格納する．
	 *
	 * @param path
	 *            読み込む画像までのパス
	 */
	private void loadHitEffectImage(String path) {
		File[] dir = new File(path).listFiles();
		sortByFileName(dir);

		for (int i = 0; i < dir.length; i++) {
			File[] files = new File(dir[i].getPath()).listFiles();
			sortByFileName(files);

			for (int j = 0; j < files.length; j++) {
				GraphicManager.getInstance().getHitEffectImageContaier()[i][j] = loadImage(files[j].getPath());
			}
		}
	}
	
	/**
	 * サウンドエフェクトを読み込んでマップに格納する．
	 */
	private void loadSoundEffect() {
		File[] files = new File(ResourceSetting.SOUND_DIRECTORY).listFiles();
		sortByFileName(files);

        for (File file : files) {
            if (!file.getName().equals(ResourceSetting.BGM_FILE) && !file.isDirectory()) {
                SoundManager.getInstance().getSoundBuffers().put(file.getName(),
                        SoundManager.getInstance().createAudioBuffer(file.getPath()));
            }
        }
    }

    /**
     * BGMを読み込む．
     */
    private void loadBackGroundMusic() {
        SoundManager.getInstance().setBackGroundMusicBuffer(SoundManager.getInstance()
                .createAudioBuffer(ResourceSetting.SOUND_DIRECTORY + ResourceSetting.BGM_FILE));
    }

	/**
	 * リソースが読み込み済みかどうかを返す．
	 *
	 * @param resourceName
	 *            リソース名
	 *
	 * @return {@code true} 読み込み済みの場合，{@code false} otherwise
	 */
	public boolean isLoaded(String resourceName) {
		return this.loadedResources.contains(resourceName);
	}

	/**
	 * 読み込んだリソース名をを読み込み済みリソースリストに追加する．
	 *
	 * @param resourceName
	 *            読み込んだリソース名
	 */
	public void addLoadedResource(String resourceName) {
		this.loadedResources.add(resourceName);
	}

	/**
	 * 複数のファイルを辞書式順序でソートする．
	 *
	 * @param files
	 *            ソートしたいファイルを格納した配列
	 */
	public void sortByFileName(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File file1, File file2) {
				return file1.getName().compareTo(file2.getName());
			}
		});
	}

}
