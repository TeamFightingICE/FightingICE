package render;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import image.Image;

public class ImageTask extends RenderTask {

	private int textureId;
	private int width;
	private int height;
	private int posX;
	private int posY;
	private boolean direction;

	public ImageTask() {
		this.textureId = -1;
		this.posX = -1;
		this.posY = -1;
		this.width = -1;
		this.height = -1;
		this.direction = true;
	}

	public ImageTask(Image img, int x, int y, boolean dir) {
		this.textureId = img.getTextureId();
		this.posX = x;
		this.posY = y;
		this.width = img.getBufferedImage().getWidth();
		this.height = img.getBufferedImage().getHeight();
		this.direction = dir;
	}

	public ImageTask(int id, int x, int y, int width, int height, boolean dir) {
		this.textureId = id;
		this.posX = x;
		this.posY = y;
		this.width = width;
		this.height = height;
		this.direction = dir;
	}

	@Override
	public void render() {
		GL11.glEnable(GL_TEXTURE_2D);

		// Bind the texture
		glBindTexture(GL_TEXTURE_2D, textureId);

		// Draw a textured quad.
		glBegin(GL_QUADS);
		{
			if (direction) {
				// Top left corner of the texture
				glTexCoord2f(0, 0);
				glVertex2f(posX, posY);

				// Top right corner of the texture
				glTexCoord2f(1, 0);
				glVertex2f(posX + width, posY);

				// Bottom right corner of the texture
				glTexCoord2f(1, 1);
				glVertex2f(posX + width, posY + height);

				// Bottom left corner of the texture
				glTexCoord2f(0, 1);
				glVertex2f(posX, posY + height);
			} else {
				// Top right corner of the texture
				glTexCoord2f(0, 0);
				glVertex2f(posX + width, posY);

				// Top left corner of the texture
				glTexCoord2f(1, 0);
				glVertex2f(posX, posY);

				// Bottom left corner of the texture
				glTexCoord2f(1, 1);
				glVertex2f(posX, posY + height);

				// Bottom right corner of the texture
				glTexCoord2f(0, 1);
				glVertex2f(posX + width, posY + height);
			}
		}
		glEnd();

		GL11.glDisable(GL_TEXTURE_2D);

	}

}
