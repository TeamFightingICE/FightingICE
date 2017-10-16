package render;

import static org.lwjgl.opengl.GL11.*;

public class QuadTask extends RenderTask {

	public static final byte FILLED_QUAD = 0;
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
		System.out.println(r + " " + g + " " + b);
	}

	@Override
	public void render() {
		glColor3f(red, green, blue);

		switch (mode) {
		case FILLED_QUAD:
			// draw quad
			glBegin(GL_QUADS);
			glVertex2i(posX, posY);
			glVertex2i(posX + sizeX, posY);
			glVertex2i(posX + sizeX, posY + sizeY);
			glVertex2i(posX, posY + sizeY);

			System.out.println("描画");
			glEnd();
			break;

		default:
			glBegin(GL_LINE_LOOP);
			glVertex2i(posX, posY);
			glVertex2i(posX + sizeX, posY);
			glVertex2i(posX + sizeX, posY + sizeY);
			glVertex2i(posX, posY + sizeY);
			glEnd();
			break;
		}

		//glColor3d(1, 1, 1);
	}

}
