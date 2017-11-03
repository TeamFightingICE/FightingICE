package struct;

public class HitArea {

	private int left;

	private int right;

	private int top;

	private int bottom;

	public HitArea(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	public HitArea() {
		this.left = -1;
		this.right = -1;
		this.top = -1;
		this.bottom = -1;
	}

	public void move(int speedX, int speedY) {
		this.left += speedX;
		this.right += speedX;
		this.top += speedY;
		this.bottom += speedY;
	}

	public int getLeft() {
		return this.left;
	}

	public int getRight() {
		return this.right;
	}

	public int getTop() {
		return this.top;
	}

	public int getBottom() {
		return this.bottom;
	}

}
