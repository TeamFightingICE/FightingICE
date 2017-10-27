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

	/**各キャラクターの画像を格納するリスト*/
	private ArrayList<CharacterActionImage> characterImageContainer;

	/**波動拳の画像を格納するリスト*/
	private ArrayList<Image> projectileImageContainer;

	/**必殺技の画像を格納するリスト*/
	private ArrayList<Image> ultimateAttackImageContainer;

	/**"Hit"の画像を格納するリスト*/
	private ArrayList<Image> hitTextImageContainer;

	/**1～9までの画像を格納するリスト*/
	private ArrayList<Image> counterTextImageContainer;

	/** アッパー画像を格納する2次元配列*/
	private Image[][] upperImageContainer;

	/** 攻撃ヒット時に描画するエフェクトの画像を格納する2次元配列*/
	private Image[][] hitEffectImageContainer;

	private Image backGroundImage;





	private static  GraphicManager  graphicManager = new  GraphicManager();

	private  GraphicManager() {
		System.out.println("Create instance: " + GraphicManager.class.getName());

		this.renderTaskList = new LinkedList<RenderTask>();
		this.letterImage = new LetterImage();

		this.characterImageContainer = new ArrayList<CharacterActionImage>();

		this.projectileImageContainer = new ArrayList<Image>();
		this.ultimateAttackImageContainer = new ArrayList<Image>();

		this.counterTextImageContainer = new ArrayList<Image>();
		this.hitTextImageContainer = new ArrayList<Image>();

		this.upperImageContainer = new Image[2][];
		this.hitEffectImageContainer = new Image[4][];

	}

	public static  GraphicManager getInstance() {
		return  graphicManager;
	}

	public ArrayList<CharacterActionImage> getImageContainer(){
		return this.characterImageContainer;
	}

	public ArrayList<Image> getProjectileImageContainer(){
		return this.projectileImageContainer;
	}

	public ArrayList<Image> getCounterTextImageContainer(){
		return this.counterTextImageContainer;
	}

	public ArrayList<Image> getUltimateAttackImageContainer(){
		return this.ultimateAttackImageContainer;
	}

	public ArrayList<Image> getHitTextAttackImageContainer(){
		return this.hitTextImageContainer;
	}

	public Image[][] getUpperImageContainer(){
		return this.upperImageContainer;
	}

	public Image[][] getHitEffectImageContaier(){
		return this.hitEffectImageContainer;
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
