package render;

import static org.lwjgl.opengl.GL11.*;

/**
 * 四角形を描画するタスクを扱うメソッド．
 */
public class QuadTask extends RenderTask {

	/**
	 * 四角形を指定色で塗りつぶす場合を指定する定数．
	 */
	public static final byte FILLED_QUAD = 0;

	/**
	 * 四角形を枠線で描画する場合を指定する定数．
	 */
	public static final byte LINE_QUAD = 1;

	/**
	 * 四角形の描画モード．
	 */
	private byte mode;

	/**
	 * 塗りつぶし色あるいは枠線の色の赤み．
	 */
	private float red;

	/**
	 * 塗りつぶし色あるいは枠線の色の緑み．
	 */
	private float green;

	/**
	 * 塗りつぶし色あるいは枠線の色の青み．
	 */
	private float blue;

	/**
	 * 不透明度．
	 */
	private float alpha;

	/**
	 * 四角形を描画するx座標．
	 */
	private int posX;

	/**
	 * 四角形を描画するy座標．
	 */
	private int posY;

	/**
	 * 四角形のx軸方向のサイズ．
	 */
	private int sizeX;

	/**
	 * 四角形のy軸方向のサイズ．
	 */
	private int sizeY;

	/**
	 * クラスコンストラクタ．
	 */
	public QuadTask() {
		this.mode = FILLED_QUAD;
		this.posX = -1;
		this.posY = -1;
		this.sizeX = -1;
		this.sizeY = -1;
		this.red = 0;
		this.green = 0;
		this.blue = 0;
		this.alpha = 0;
	}

	/**
	 * QuadTaskインスタンスの初期値を設定する場合のクラスコンストラクタ．
	 *
	 * @param mode
	 *            描画モード(FILLED_QUAD or LINE_QUAD)
	 * @param posX
	 *            描画するx座標
	 * @param posY
	 *            描画するy座標
	 * @param sizeX
	 *            四角形のx軸サイズ
	 * @param sizeY
	 *            四角形のy軸サイズ
	 * @param r
	 *            赤み
	 * @param g
	 *            緑み
	 * @param b
	 *            青み
	 * @param alpha
	 *            不透明度
	 */
	public QuadTask(byte mode, int posX, int posY, int sizeX, int sizeY, float r, float g, float b, float alpha) {
		this.mode = mode;
		this.posX = posX;
		this.posY = posY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = alpha;
	}

	/**
	 * 指定されたモードと色で四角形をレンダリングする．<br>
	 */
	@Override
	public void render() {
		// 色設定
		glColor3f(red, green, blue);

		switch (mode) {
		case FILLED_QUAD:
			// 色で塗りつぶされた四角形を描画
			glBegin(GL_QUADS);
			glVertex2i(posX, posY);
			glVertex2i(posX + sizeX, posY);
			glVertex2i(posX + sizeX, posY + sizeY);
			glVertex2i(posX, posY + sizeY);

			glEnd();
			break;

		default:
			// 枠線で四角形を描画
			glBegin(GL_LINE_LOOP);
			glVertex2i(posX, posY);
			glVertex2i(posX + sizeX, posY);
			glVertex2i(posX + sizeX, posY + sizeY);
			glVertex2i(posX, posY + sizeY);
			glEnd();
			break;
		}

		// 色設定をデフォルト値(白)にする
		glColor3d(1, 1, 1);
	}

}
