package manager;

import static org.lwjgl.opengl.GL11.*;

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

	private LinkedList<CharacterActionImage> imageContainer;

	public GraphicManager() {
		this.renderTaskList = new LinkedList<RenderTask>();
		this.letterImage = new LetterImage();
		this.imageContainer = new LinkedList<CharacterActionImage>();
	}

	public LinkedList<CharacterActionImage> getImageContainer(){
		return this.imageContainer;
	}

	public void render() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		System.out.println(renderTaskList.size());
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
		StringTask task = new StringTask(letterImage, string, x, y);
		this.renderTaskList.add(task);
	}

	public void drawQuad(int x, int y, int sizeX, int sizeY, float red, float green, float blue, float alpha) {
		QuadTask task = new QuadTask(QuadTask.FILLED_QUAD, x, y, sizeX, sizeY, red, green, blue, alpha);
		this.renderTaskList.add(task);
	}

	public void drawLineQuad(int x, int y, int sizeX, int sizeY, float red, float green, float blue, float alpha) {
		QuadTask task = new QuadTask(QuadTask.LINE_QUAD, x, y, sizeX, sizeY, red, green, blue, alpha);
		this.renderTaskList.add(task);
	}

	public void setLetterFont(LetterImage lf){
		this.letterImage = lf;
	}

}
