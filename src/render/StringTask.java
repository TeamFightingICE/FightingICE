package render;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import image.Image;
import image.ImageFont;

public class StringTask extends RenderTask {

	private ImageFont imageFont;
	private String str;
	private int posX;
	private int posY;

	public StringTask() {
		this.imageFont = null;
		this.str = "";
		this.posX = -1;
		this.posY = -1;
	}

	public StringTask(ImageFont imageFont, String str, int x, int y) {
		this.imageFont = imageFont;
		this.str = str;
		this.posX = x;
		this.posY = y;
	}

	@Override
	public void render() {
		int nowPositionX = posX;

		for (int i = 0; i < str.length(); i++) {
			Image img = imageFont.getCharacter(str.charAt(i));
			draw(img, nowPositionX, posY);

			nowPositionX += img.getBufferedImage().getWidth();
		}
	}

	private void draw(Image img, int posX, int posY) {
		GL11.glEnable(GL_TEXTURE_2D);

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
