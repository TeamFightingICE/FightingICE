package render;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import image.Image;
import image.LetterImage;

public class StringTask extends RenderTask {

	private LetterImage imageFont;
	private String str;
	private int posX;
	private int posY;

	/** コンストラクタ */
	public StringTask() {
		this.imageFont = null;
		this.str = "";
		this.posX = -1;
		this.posY = -1;
	}

	/**
	 * インスタンスの初期値をセットする場合のコンストラクタ．
	 *
	 * @param imageFont 文字フォント
	 * @param str 文字
	 * @param x 文字画像を描画するX座標
	 * @param y 文字画像を描画するY座標
	 */
	public StringTask(LetterImage imageFont, String str, int x, int y) {
		this.imageFont = imageFont;
		this.str = str;
		this.posX = x;
		this.posY = y;
	}

	/**
	 * 文字画像をレンダリングするメソッド．
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
