package render;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import image.Image;
import image.LetterImage;

/**
 * 文字描画タスクを扱うクラス．
 */
public class StringTask extends RenderTask {

	/**
	 * 文字画像のフォント．
	 */
	private LetterImage imageFont;

	/**
	 * 文字．
	 */
	private String str;

	/**
	 * 文字画像を描画するx座標．
	 */
	private int posX;

	/**
	 * 文字画像を描画するy座標．
	 */
	private int posY;

	/**
	 * クラスコンストラクタ．
	 */
	public StringTask() {
		this.imageFont = null;
		this.str = "";
		this.posX = -1;
		this.posY = -1;
	}

	/**
	 * StringTaskインスタンスの初期値をセットする場合のクラスコンストラクタ．
	 *
	 * @param imageFont
	 *            文字画像のフォント
	 * @param str
	 *            文字
	 * @param x
	 *            文字画像を描画するx座標
	 * @param y
	 *            文字画像を描画するy座標
	 */
	public StringTask(LetterImage imageFont, String str, int x, int y) {
		this.imageFont = imageFont;
		this.str = str;
		this.posX = x;
		this.posY = y;
	}

	/**
	 * 文字画像をレンダリングする．<br>
	 * 文字列の各文字を取り出していき，順番に描画する．
	 */
	@Override
	public void render() {
		int nowPositionX = posX;

		// 文字列の各文字を取り出し，指定位置に描画する
		for (int i = 0; i < str.length(); i++) {
			Image img = imageFont.getLetterImage(str.charAt(i));
			draw(img, nowPositionX, posY);

			// 次の文字は文字の大きさ分X軸方向に動かして描画する
			nowPositionX += img.getBufferedImage().getWidth();
		}
	}

	/**
	 * OpenGLの機能を用いて文字画像を描画する．
	 *
	 * @param img
	 *            文字画像
	 * @param posX
	 *            文字画像を描画するx座標
	 * @param posY
	 *            文字画像を描画するy座標
	 */
	private void draw(Image img, int posX, int posY) {
		GL11.glEnable(GL_TEXTURE_2D);
		glColor3f(1.0f, 1.0f, 1.0f);

		// Bind the texture
		glBindTexture(GL_TEXTURE_2D, img.getTextureId());

		// Draw a textured quad.
		glBegin(GL_QUADS);
		{
			// Top left corner of the texture
			glTexCoord2f(0, 0);
			glVertex2f(posX, posY);

			// Top right corner of the texture
			glTexCoord2f(1, 0);
			glVertex2f(posX + img.getBufferedImage().getWidth(), posY);

			// Bottom right corner of the texture
			glTexCoord2f(1, 1);
			glVertex2f(posX + img.getBufferedImage().getWidth(), posY + img.getBufferedImage().getHeight());

			// Bottom left corner of the texture
			glTexCoord2f(0, 1);
			glVertex2f(posX, posY + img.getBufferedImage().getHeight());

		}
		glEnd();

		GL11.glDisable(GL_TEXTURE_2D);
	}

}
