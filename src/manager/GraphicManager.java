package manager;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;
import java.util.LinkedList;

import image.Image;
import image.ImageFont;
import render.ImageTask;
import render.QuadTask;
import render.RenderTask;
import render.StringTask;

public class GraphicManager {

	private LinkedList<RenderTask> renderTaskList;

	private ImageFont imageFont;

	public GraphicManager() {
		renderTaskList = new LinkedList<RenderTask>();
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		imageFont = new ImageFont(awtFont, true);

	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		while (!renderTaskList.isEmpty()) {
			renderTaskList.removeFirst().render();
		}
		glFlush();

	}

	public void drawImage(Image img, int x, int y, boolean direction) {
		ImageTask task = new ImageTask(img, x, y, direction);
		this.renderTaskList.add(task);
	}


	public void drawImage(Image img, int x, int y, int sizeX, int sizeY, boolean direction) {
		ImageTask task = new ImageTask(img.getTextureId(), x, y, sizeX, sizeY, direction);
		this.renderTaskList.add(task);
	}

	public void drawString(String string, int x, int y) {
		StringTask task = new StringTask(imageFont, string, x, y);
		this.renderTaskList.add(task);
	}

	public void drawQuad(int x, int y, int sizeX, int sizeY, double red, double green, double blue) {
		QuadTask task = new QuadTask(QuadTask.MODE_FILLED_QUAD, x, y, sizeX, sizeY, red, green, blue);
		this.renderTaskList.add(task);
	}

	public void drawLineQuad(int x, int y, int sizeX, int sizeY, double red, double green, double blue) {
		QuadTask task = new QuadTask(QuadTask.MODE_LINE_QUAD, x, y, sizeX, sizeY, red, green, blue);
		this.renderTaskList.add(task);
	}

}
