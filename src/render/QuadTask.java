package render;

import static org.lwjgl.opengl.GL11.*;

public class QuadTask extends RenderTask {

	/** 四角形を指定色で塗りつぶす場合を指定する定数 */
	public static final byte FILLED_QUAD = 0;

	/** 四角形を枠線で描画する場合を指定する定数 */
	public static final byte LINE_QUAD = 1;

	private byte mode;
	private float red;
	private float green;
	private float blue;
	private float alpha;
	private int posX;
	private int posY;
	private int sizeX;
	private int sizeY;

	/** コンストラクタ */
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

	/** インスタンスの初期値を設定する場合のコンストラクタ．
	 *
	 * @param mode 描画モード(FILLED_QUAD or LINE_QUAD)
	 * @param posX 描画するX座標
	 * @param posY 描画するY座標
	 * @param sizeX 四角形のX軸サイズ
	 * @param sizeY 四角形のY軸サイズ
	 * @param r 赤み
	 * @param g 緑み
	 * @param b 青み
	 * @param alpha 不透明度
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
	 * 四角形をレンダリングするメソッド．<br>
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
