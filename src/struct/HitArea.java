package struct;

/**
 * 当たり判定の境界値などの、キャラクターや攻撃の当たり判定の領域に関する情報を扱うクラス
 */
public class HitArea {

	/**
	 * Value of arena`s left boundary
	 */
	private int left;

	/**
	 * Value of arena`s right boundary
	 */
	private int right;

	/**
	 * Value of arena`s top boundary
	 */
	private int top;

	/**
	 * Value of arena`s bottom boundary
	 */
	private int bottom;

	/**
	 * 当たり判定の領域を初期化するコンストラクタ
	 */
	public HitArea() {
		this.left = 0;
		this.right = 0;
		this.top = 0;
		this.bottom = 0;
	}

	/**
	 * 指定されたデータで当たり判定の領域を初期化するコンストラクタ
	 *
	 * @param hitArea
	 *            当たり判定の領域のデータ
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
	 * 指定された値で当たり判定の領域を初期化するコンストラクタ
	 *
	 * @param left
	 *            ヒットボックスの一番左のx座標
	 * @param right
	 *            ヒットボックスの一番右のx座標
	 * @param top
	 *            ヒットボックスの一番上のy座標
	 * @param bottom
	 *            ヒットボックスの一番下のy座標
	 */
	public HitArea(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	/**
	 * 指定された値で当たり判定の座標を更新するメソッド
	 *
	 * @param speedX
	 *            水平方向のスピード
	 * @param speedY
	 *            鉛直方向のスピード
	 */
	public void move(int speedX, int speedY) {
		this.left += speedX;
		this.right += speedX;
		this.top += speedY;
		this.bottom += speedY;
	}

	/**
	 * ヒットボックスの一番左のx座標を返すメソッド
	 *
	 * @return ヒットボックスの一番左のx座標
	 */
	public int getLeft() {
		return this.left;
	}

	/**
	 * ヒットボックスの一番右のx座標を返すメソッド
	 *
	 * @return ヒットボックスの一番右のx座標
	 */
	public int getRight() {
		return this.right;
	}

	/**
	 * ヒットボックスの一番上のy座標を返すメソッド
	 *
	 * @return ヒットボックスの一番上のy座標
	 */
	public int getTop() {
		return this.top;
	}

	/**
	 * ヒットボックスの一番下のy座標を返すメソッド
	 *
	 * @return ヒットボックスの一番下のy座標
	 */
	public int getBottom() {
		return this.bottom;
	}

}
