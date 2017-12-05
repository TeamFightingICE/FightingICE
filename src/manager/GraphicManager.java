package manager;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.LinkedList;

import image.CharacterActionImage;
import image.Image;
import image.LetterImage;
import render.ImageTask;
import render.QuadTask;
import render.RenderTask;
import render.StringTask;

public class GraphicManager {

	private LinkedList<RenderTask> renderTaskList;

	private LetterImage letterImage;

	/** 各キャラクターの画像を格納するリスト */
	private ArrayList<CharacterActionImage> characterImageContainer;

	/** 波動拳の画像を格納するリスト */
	private ArrayList<Image> projectileImageContainer;

	/** 必殺技の画像を格納するリスト */
	private ArrayList<Image> ultimateAttackImageContainer;

	/** "Hit"の画像を格納するリスト */
	private ArrayList<Image> hitTextImageContainer;

	/** 1～9までの画像を格納するリスト */
	private ArrayList<Image> counterTextImageContainer;

	/** アッパー画像を格納する2次元配列 */
	private Image[][] upperImageContainer;

	/** 攻撃ヒット時に描画するエフェクトの画像を格納する2次元配列 */
	private Image[][] hitEffectImageContainer;

	/** 背景画像を格納するリスト */
	private ArrayList<Image> backGroundImage;

	/** コンストラクタ */
	private GraphicManager() {
		System.out.println("Create instance: " + GraphicManager.class.getName());

		this.renderTaskList = new LinkedList<RenderTask>();
		this.letterImage = new LetterImage();

		this.characterImageContainer = new ArrayList<CharacterActionImage>();

		this.projectileImageContainer = new ArrayList<Image>();
		this.ultimateAttackImageContainer = new ArrayList<Image>();

		this.counterTextImageContainer = new ArrayList<Image>();
		this.hitTextImageContainer = new ArrayList<Image>();

		this.upperImageContainer = new Image[2][3];
		this.hitEffectImageContainer = new Image[4][4];
		this.backGroundImage = new ArrayList<Image>();

	}

	/** GraphicManagerクラスの唯一のインスタンスを取得するメソッド． */
	public static GraphicManager getInstance() {
		return GraphicManagerHolder.instance;
	}

	/** getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス */
	private static class GraphicManagerHolder {
		private static final GraphicManager instance = new GraphicManager();
	}

	/**
	 * 各キャラクターの画像を格納するリストを取得するgetterメソッド．
	 *
	 * @return 各キャラクターの画像を格納するリスト
	 */
	public ArrayList<CharacterActionImage> getCharacterImageContainer() {
		return this.characterImageContainer;
	}

	/**
	 * 波動拳の画像を格納するリストを取得するgetterメソッド．
	 *
	 * @return 波動拳の画像を格納するリスト
	 */
	public ArrayList<Image> getProjectileImageContainer() {
		return this.projectileImageContainer;
	}

	/**
	 * 1～9までの画像を格納するリストを取得するgetterメソッド．
	 *
	 * @return 1～9までの画像を格納するリスト
	 */
	public ArrayList<Image> getCounterTextImageContainer() {
		return this.counterTextImageContainer;
	}

	/**
	 * 必殺技の画像を格納するリストを取得するgetterメソッド．
	 *
	 * @return 必殺技の画像を格納するリスト
	 */
	public ArrayList<Image> getUltimateAttackImageContainer() {
		return this.ultimateAttackImageContainer;
	}

	/**
	 * "Hit"の画像を格納するリストを取得するgetterメソッド．
	 *
	 * @return "Hit"の画像を格納するリスト
	 */
	public ArrayList<Image> getHitTextImageContainer() {
		return this.hitTextImageContainer;
	}

	/**
	 * アッパーの画像を格納する二次元配列を取得するgetterメソッド．
	 *
	 * @return アッパーの画像を格納する二次元配列
	 */
	public Image[][] getUpperImageContainer() {
		return this.upperImageContainer;
	}

	/**
	 * 攻撃ヒット時に描画するエフェクトの画像を格納する二次元配列を取得するgetterメソッド．
	 *
	 * @return 攻撃ヒット時に描画するエフェクトの画像を格納する二次元配列
	 */
	public Image[][] getHitEffectImageContaier() {
		return this.hitEffectImageContainer;
	}

	/**
	 * 背景画像を格納するリストを取得するgetterメソッド．
	 *
	 * @return 背景画像を格納するリスト
	 */
	public ArrayList<Image> getBackgroundImage() {
		return this.backGroundImage;
	}

	/**
	 * レンダリングタスクリストに登録されているタスクを実行し，画像をレンダリングするメソッド．<br>
	 * タスクリストが空の場合，画面を黒く塗りつぶす．
	 *
	 * @see DisplayManager#gameLoop(GameManager)
	 */
	public void render() {
		// 黒で塗りつぶすように指定
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// 指定した色でバッファを塗りつぶすことでバッファクリアを行う
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// レンダリングタスクリストに残っているタスクを実行し，画像をバッファにセット
		while (!renderTaskList.isEmpty()) {
			renderTaskList.removeFirst().render();
		}
		// バッファの中身を画面にレンダリング
		glFlush();
	}

	/**
	 * 画像をレンダリングするタスクを新たに生成し，タスクリストに追加するメソッド．<br>
	 * 画像，画像をレンダリングするX座標とY座標(この座標で画像の左上端からレンダリングされる)，画像の左右の向きを引数で指定する．
	 *
	 * @param img 画像
	 * @param x 画像をレンダリングするX座標
	 * @param y 画像をレンダリングするY座標
	 * @param direction 画像の左右の向き(右がtrue)
	 */
	public void drawImage(Image img, int x, int y, boolean direction) {
		ImageTask task = new ImageTask(img, x, y, direction);
		this.renderTaskList.add(task);
	}

	/**
	 * 画像をレンダリングするタスクを新たに生成し，タスクリストに追加するメソッド．<br>
	 * 画像，画像をレンダリングするX座標とY座標(この座標で画像の左上端からレンダリングされる)，
	 * 画像のX軸サイズとY軸サイズ，画像の左右の向きを引数で指定する．
	 *
	 * @param img 画像
	 * @param x 画像をレンダリングするX座標
	 * @param y 画像をレンダリングするY座標
	 * @param sizeX 画像のX軸サイズ
	 * @param sizeY 画像のY軸サイズ
	 * @param direction 画像の左右の向き(右がtrue)
	 */
	public void drawImage(Image img, int x, int y, int sizeX, int sizeY, boolean direction) {
		ImageTask task = new ImageTask(img.getTextureId(), x, y, sizeX, sizeY, direction);
		this.renderTaskList.add(task);
	}

	/**
	 * 文字画像をレンダリングするタスクを新たに生成し，タスクリストに追加するメソッド．
	 *
	 * @param string 文字
	 * @param x 文字画像をレンダリングするX座標
	 * @param y 文字画像をレンダリングするY座標
	 */
	public void drawString(String string, int x, int y) {
		StringTask task = new StringTask(letterImage, string, x, y);
		this.renderTaskList.add(task);
	}

	/**
	 * 指定色で塗りつぶされた四角形をレンダリングするタスクを新たに生成し，タスクリストに追加するメソッド．<br>
	 * 塗りつぶし色は引数で指定することができる．
	 *
	 * @param x 四角形をレンダリングするX座標
	 * @param y 四角形をレンダリングするY座標
	 * @param sizeX 四角形のX軸サイズ
	 * @param sizeY 四角形のY軸サイズ
	 * @param red 塗りつぶし色の赤み
	 * @param green 塗りつぶし色の緑み
	 * @param blue 塗りつぶし色の青み
	 * @param alpha 塗りつぶし色の不透明度
	 */
	public void drawQuad(int x, int y, int sizeX, int sizeY, float red, float green, float blue, float alpha) {
		QuadTask task = new QuadTask(QuadTask.FILLED_QUAD, x, y, sizeX, sizeY, red, green, blue, alpha);
		this.renderTaskList.add(task);
	}

	/**
	 * 線で四角形をレンダリングするタスクを新たに生成し，タスクリストに追加するメソッド．<br>
	 * 線の色は引数で指定することができる．
	 *
	 * @param x 四角形をレンダリングするX座標
	 * @param y 四角形をレンダリングするY座標
	 * @param sizeX 四角形のX軸サイズ
	 * @param sizeY 四角形のY軸サイズ
	 * @param red 線の赤み
	 * @param green 線の緑み
	 * @param blue 線の青み
	 * @param alpha 線の色の不透明度
	 */
	public void drawLineQuad(int x, int y, int sizeX, int sizeY, float red, float green, float blue, float alpha) {
		QuadTask task = new QuadTask(QuadTask.LINE_QUAD, x, y, sizeX, sizeY, red, green, blue, alpha);
		this.renderTaskList.add(task);
	}

	/**
	 * 引数で指定された文字フォントを設定するsetterメソッド．
	 *
	 * @param lf 文字フォント
	 */
	public void setLetterFont(LetterImage lf) {
		this.letterImage = lf;
	}

	/** GraphicManagerのフィールド変数をクリアする */
	public void close() {
		this.renderTaskList.clear();
		this.letterImage = null;
		this.characterImageContainer.clear();
		this.projectileImageContainer.clear();
		this.ultimateAttackImageContainer.clear();
		this.counterTextImageContainer.clear();
		this.hitTextImageContainer.clear();
		this.upperImageContainer = null;
		this.hitEffectImageContainer = null;
		this.backGroundImage.clear();
	}

}
