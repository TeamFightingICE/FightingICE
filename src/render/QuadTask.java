package render;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

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
	}

	@Override
	public void render() {
		GL11.glColor3f(red, green, blue);

		switch (mode) {
		case FILLED_QUAD:
			// draw quad
			glBegin(GL11.GL_QUADS); {
			GL11.glVertex2i(posX, posY);
			GL11.glVertex2i(posX + sizeX, posY);
			GL11.glVertex2i(posX + sizeX, posY + sizeY);
			GL11.glVertex2i(posX, posY + sizeY);
		}
			glEnd();
			break;

		default:
			glBegin(GL11.GL_LINE_LOOP); {
			GL11.glVertex2i(posX, posY);
			GL11.glVertex2i(posX + sizeX, posY);
			GL11.glVertex2i(posX + sizeX, posY + sizeY);
			GL11.glVertex2i(posX, posY + sizeY);
		}
			glEnd();
			break;
		}

		GL11.glColor3d(1, 1, 1);
	}

}
