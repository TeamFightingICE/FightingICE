package manager;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import image.CharacterActionImage;
import image.Image;
import image.LetterImage;
import render.ImageTask;
import render.QuadTask;
import render.RenderTask;
import render.StringTask;
import setting.GameSetting;

/**
 * 画像の描画を管理するマネージャークラス．
 */
public class GraphicManager {

	/**
	 * 描画するタスクのリスト．
	 */
	private LinkedList<RenderTask> renderTaskList;

	/**
	 * 文字画像．
	 */
	private LetterImage letterImage;

	/**
	 * 各キャラクターの画像を格納するリスト．
	 */
	private ArrayList<CharacterActionImage> characterImageContainer;

	/**
	 * 波動拳の画像を格納するリスト．
	 */
	private ArrayList<Image> projectileImageContainer;

	/**
	 * 必殺技の画像を格納するリスト
	 */
	private ArrayList<Image> ultimateAttackImageContainer;

	/**
	 * "Hit"の画像を格納するリスト．
	 */
	private ArrayList<Image> hitTextImageContainer;

	/**
	 * 1～9までの画像を格納するリスト．
	 */
	private ArrayList<Image> counterTextImageContainer;

	/**
	 * アッパー画像を格納する2次元配列．
	 */
	private Image[][] upperImageContainer;

	/**
	 * 攻撃ヒット時に描画するエフェクトの画像を格納する2次元配列．
	 */
	private Image[][] hitEffectImageContainer;

	/**
	 * 背景画像を格納するリスト．
	 */
	private ArrayList<Image> backGroundImage;

	/**
	 * 描画情報
	 */
	private BufferedImage screen;

	private Graphics2D screenGraphic;


	/**
	 * クラスコンストラクタ．
	 */
	private GraphicManager() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + GraphicManager.class.getName());

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

		screen = new BufferedImage(GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		screenGraphic = screen.createGraphics();

	}

	/**
	 * GraphicManagerクラスの唯一のインスタンスを取得する．
	 *
	 * @return GameManagerクラスの唯一のインスタンス
	 */
	public static GraphicManager getInstance() {
		return GraphicManagerHolder.instance;
	}

	/**
	 * getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス．
	 */
	private static class GraphicManagerHolder {
		private static final GraphicManager instance = new GraphicManager();
	}

	/**
	 * 各キャラクターの画像を格納するリストを取得する．
	 *
	 * @return 各キャラクターの画像を格納するリスト
	 */
	public ArrayList<CharacterActionImage> getCharacterImageContainer() {
		return this.characterImageContainer;
	}

	/**
	 * 波動拳の画像を格納するリストを取得する．
	 *
	 * @return 波動拳の画像を格納するリスト
	 */
	public ArrayList<Image> getProjectileImageContainer() {
		return this.projectileImageContainer;
	}

	/**
	 * 1～9までの画像を格納するリストを取得する．
	 *
	 * @return 1～9までの画像を格納するリスト
	 */
	public ArrayList<Image> getCounterTextImageContainer() {
		return this.counterTextImageContainer;
	}

	/**
	 * 必殺技の画像を格納するリストを取得する．
	 *
	 * @return 必殺技の画像を格納するリスト
	 */
	public ArrayList<Image> getUltimateAttackImageContainer() {
		return this.ultimateAttackImageContainer;
	}

	/**
	 * "Hit"の画像を格納するリストを取得する．
	 *
	 * @return "Hit"の画像を格納するリスト
	 */
	public ArrayList<Image> getHitTextImageContainer() {
		return this.hitTextImageContainer;
	}

	/**
	 * アッパーの画像を格納する二次元配列を取得する．
	 *
	 * @return アッパーの画像を格納する二次元配列
	 */
	public Image[][] getUpperImageContainer() {
		return this.upperImageContainer;
	}

	/**
	 * 攻撃ヒット時に描画するエフェクトの画像を格納する二次元配列を取得する．
	 *
	 * @return 攻撃ヒット時に描画するエフェクトの画像を格納する二次元配列
	 */
	public Image[][] getHitEffectImageContaier() {
		return this.hitEffectImageContainer;
	}

	/**
	 * 背景画像を格納するリストを取得する．
	 *
	 * @return 背景画像を格納するリスト
	 */
	public ArrayList<Image> getBackgroundImage() {
		return this.backGroundImage;
	}

	/**
	 * レンダリングタスクリストに登録されているタスクを実行し，画像をレンダリングする．<br>
	 *
	 * @see DisplayManager#gameLoop(GameManager)
	 */
	public void render() {

		// 黒で塗りつぶすように指定
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// 指定した色でバッファを塗りつぶすことでバッファクリアを行う
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// レンダリングタスクリストに残っているタスクを実行し，画像をバッファにセット
		while (!this.renderTaskList.isEmpty()) {
			this.renderTaskList.removeFirst().render();
		}
		// バッファの中身を画面にレンダリング
		glFlush();
	}

	/**
	 * 画像をレンダリングするタスクを新たに生成し，タスクリストに追加する．
	 *
	 * @param img
	 *            画像
	 * @param x
	 *            画像をレンダリングするx座標
	 * @param y
	 *            画像をレンダリングするy座標
	 * @param direction
	 *            画像の左右の向き(右がtrue)
	 */
	public void drawImage(Image img, int x, int y, boolean direction) {
		this.drawImage(img, x, y, img.getWidth(), img.getHeight(), direction, -img.getWidth(), 0);
	}

	/**
	 * 画像をレンダリングするタスクを新たに生成し，タスクリストに追加する．
	 *
	 * @param img
	 *            描画する画像
	 * @param x
	 *            画像をレンダリングするx座標
	 * @param y
	 *            画像をレンダリングするy座標
	 * @param sizeX
	 *            画像のx軸サイズ
	 * @param sizeY
	 *            画像のy軸サイズ
	 * @param direction
	 *            画像の左右の向き(右がtrue)
	 */
	public void drawImage(Image img, int x, int y, int sizeX, int sizeY, boolean direction, double tx, double ty) {
		ImageTask task = new ImageTask(img.getTextureId(), x, y, sizeX, sizeY, direction);
		this.renderTaskList.add(task);
		
		this.drawImageinScreenData(img, x, y, sizeX, sizeY, direction, tx, ty);
	}

	public void drawImageinScreenData(Image img, int x, int y, int sizeX, int sizeY, boolean direction, double tx, double ty) {
		if (direction) {
			screenGraphic.drawImage(img.getBufferedImage(), x, y, sizeX, sizeY, null);
		} else {
			AffineTransform transform = AffineTransform.getScaleInstance(-1d, 1d);
			transform.translate(tx, ty);
			AffineTransformOp flip = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			screenGraphic.drawImage(flip.filter(img.getBufferedImage(), null), x, y, sizeX, sizeY, null);
		}
	}

	/**
	 * 文字画像をレンダリングするタスクを新たに生成し，タスクリストに追加する．
	 *
	 * @param string
	 *            描画する文字
	 * @param x
	 *            文字画像をレンダリングするx座標
	 * @param y
	 *            文字画像をレンダリングするy座標
	 */
	public void drawString(String string, int x, int y) {
		StringTask task = new StringTask(letterImage, string, x, y);
		this.renderTaskList.add(task);
		
		this.drawStringInScreenData(string, x, y);
	}
	
	public void drawStringInScreenData(String string, int x, int y) {
		int nowPositionX = x;

		for (int i = 0; i < string.length(); i++) {
			Image img = letterImage.getLetterImage(string.charAt(i));
			screenGraphic.drawImage(img.getBufferedImage(), nowPositionX, y, img.getWidth(), img.getHeight(), null);

			nowPositionX += img.getBufferedImage().getWidth();
		}
	}

	/**
	 * 指定色で塗りつぶされた四角形をレンダリングするタスクを新たに生成し，タスクリストに追加する．<br>
	 * 塗りつぶし色は引数で指定することができる．
	 *
	 * @param x
	 *            四角形をレンダリングするX座標
	 * @param y
	 *            四角形をレンダリングするY座標
	 * @param sizeX
	 *            四角形のX軸サイズ
	 * @param sizeY
	 *            四角形のY軸サイズ
	 * @param red
	 *            塗りつぶし色の赤み
	 * @param green
	 *            塗りつぶし色の緑み
	 * @param blue
	 *            塗りつぶし色の青み
	 * @param alpha
	 *            塗りつぶし色の不透明度
	 */
	public void drawQuad(int x, int y, int sizeX, int sizeY, float red, float green, float blue, float alpha) {
		QuadTask task = new QuadTask(QuadTask.FILLED_QUAD, x, y, sizeX, sizeY, red, green, blue, alpha);
		this.renderTaskList.add(task);
		
		this.drawQuadInScreenData(x, y, sizeX, sizeY, red, green, blue, alpha);
	}
	
	public void drawQuadInScreenData(int x, int y, int sizeX, int sizeY, float red, float green, float blue, float alpha) {
		screenGraphic.setColor(new Color(red, green, blue));
		
		if (sizeX < 0) {
			x += sizeX;
			sizeX *= -1;
		}
		
		if (sizeY < 0) {
			y += sizeY;
			sizeY *= -1;
		}
		
		screenGraphic.fillRect(x, y, sizeX, sizeY);
	}

	/**
	 * 枠線で四角形をレンダリングするタスクを新たに生成し，タスクリストに追加する．<br>
	 * 枠線の色は引数で指定することができる．
	 *
	 * @param x
	 *            四角形をレンダリングするX座標
	 * @param y
	 *            四角形をレンダリングするY座標
	 * @param sizeX
	 *            四角形のX軸サイズ
	 * @param sizeY
	 *            四角形のY軸サイズ
	 * @param red
	 *            線の赤み
	 * @param green
	 *            線の緑み
	 * @param blue
	 *            線の青み
	 * @param alpha
	 *            線の色の不透明度
	 */
	public void drawLineQuad(int x, int y, int sizeX, int sizeY, float red, float green, float blue, float alpha) {
		QuadTask task = new QuadTask(QuadTask.LINE_QUAD, x, y, sizeX, sizeY, red, green, blue, alpha);
		this.renderTaskList.add(task);
		
		this.drawLineQuadinScreenData(x, y, sizeX, sizeY, red, green, blue, alpha);
	}

	public void drawLineQuadinScreenData(int x, int y, int sizeX, int sizeY, float red, float green, float blue, float alpha){
		screenGraphic.setColor(new Color(red, green, blue));
		screenGraphic.drawRect(x, y, sizeX, sizeY);
	}

	/**
	 * 引数で指定された文字フォントを設定する．
	 *
	 * @param lf
	 *            文字フォント
	 */
	public void setLetterFont(LetterImage lf) {
		this.letterImage = lf;
	}

	public void resetScreen(){
		screen = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_RGB);
		screenGraphic = screen.createGraphics();
		screenGraphic.setColor(new Color (128, 128, 128));
	}

	public void disposeScreenGraphic(){
		screenGraphic.dispose();
	}

	public BufferedImage getScreenImage(){
		return screen;
	}

	/**
	 * GraphicManagerのフィールド変数をクリアする．
	 */
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
