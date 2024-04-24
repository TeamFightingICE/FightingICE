package struct;

import protoc.MessageProto.GrpcHitArea;

/**
 * The class dealing with information on the area of the character and the
 * attack hit box.
 */
public class HitArea {

	/**
	 * The most left x coordinate of the hit box.
	 */
	private int left;

	/**
	 * The most right x coordinate of the hit box.
	 */
	private int right;

	/**
	 * The most top y coordinate of the hit box.
	 */
	private int top;

	/**
	 * The most bottom y coordinate of the hit box.
	 */
	private int bottom;

	/**
	 * The class constructor.
	 */
	public HitArea() {
		this.left = 0;
		this.right = 0;
		this.top = 0;
		this.bottom = 0;
	}

	/**
	 * The class constructor that initializes the hit determination area using
	 * the specified data.
	 *
	 * @param hitArea
	 *            an instance of the HitArea class
	 */
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

	/**
	 * The class constructor that initializes the hit determination area using
	 * the specified values.
	 *
	 * @param left
	 *            the most left x coordinate of the hit box
	 * @param right
	 *            the most right x coordinate of the hit box
	 * @param top
	 *            the most top y coordinate of the hit box
	 * @param bottom
	 *            the most bottom y coordinate of the hit box
	 */
	public HitArea(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	/**
	 * Updates the hit determination area's coordinates using the specified
	 * speed.
	 *
	 * @param speedX
	 *            the horizontal speed
	 * @param speedY
	 *            the vertical speed
	 */
	public void move(int speedX, int speedY) {
		this.left += speedX;
		this.right += speedX;
		this.top += speedY;
		this.bottom += speedY;
	}

	/**
	 * Returns the most left x coordinate of the hit box.
	 *
	 * @return the most left x coordinate of the hit box
	 */
	public int getLeft() {
		return this.left;
	}

	/**
	 * Returns the most right x coordinate of the hit box.
	 *
	 * @return the most right x coordinate of the hit box
	 */
	public int getRight() {
		return this.right;
	}

	/**
	 * Returns the most top y coordinate of the hit box.
	 *
	 * @return the most top y coordinate of the hit box
	 */
	public int getTop() {
		return this.top;
	}

	/**
	 * Returns the most bottom y coordinate of the hit box.
	 *
	 * @return the most bottom y coordinate of the hit box
	 */
	public int getBottom() {
		return this.bottom;
	}
	
	public GrpcHitArea toProto() {
		return GrpcHitArea.newBuilder()
				.setLeft(this.getLeft())
				.setRight(this.getRight())
				.setTop(this.getTop())
				.setBottom(this.getBottom())
				.build();
	}
	
	public String toString() {
		return String.format("HitArea(left=%d, right=%d, top=%d, bottom=%d)", left, right, top, bottom);
	}

}
