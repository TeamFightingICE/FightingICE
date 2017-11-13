package struct;

public class HitArea {

	private int left;

	private int right;

	private int top;

	private int bottom;

	public HitArea() {
		this.left = 0;
		this.right = 0;
		this.top = 0;
		this.bottom = 0;
	}

	public HitArea(HitArea hitArea) {
		if (!(hitArea == null)) {
			this.left = hitArea.getLeft();
			this.right = hitArea.getRight();
			this.top = hitArea.getTop();
			this.bottom = hitArea.getBottom();
		} else {
			this.left = 0;
			this.right = 0;
			this.top = 0;
			this.bottom = 0;
		}
	}

	public HitArea(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
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
