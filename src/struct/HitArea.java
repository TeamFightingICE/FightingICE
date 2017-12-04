package struct;

/**
 * キャラクターの当たり判定の領域を扱うクラス
 */
public class HitArea {

	private int left;

	private int right;

	private int top;

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
	 *
	 * @see HitArea
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
	 *            左側の境界値
	 * @param right
	 *            右側の境界値
	 * @param top
	 *            上側の境界値
	 * @param bottom
	 *            下側の境界値
	 */
	public HitArea(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	/**
	 * 指定された値で当たり判定を動かすメソッド
	 *
	 * @param speedX
	 *            横方向のスピード
	 * @param speedY
	 *            縦方向のスピード
	 */
	public void move(int speedX, int speedY) {
		this.left += speedX;
		this.right += speedX;
		this.top += speedY;
		this.bottom += speedY;
	}

	/**
	 * 左側の境界値を返すメソッド
	 *
	 * @return 左側の境界値
	 */
	public int getLeft() {
		return this.left;
	}

	/**
	 * 右側の境界値を返すメソッド
	 *
	 * @return 右側の境界値
	 */
	public int getRight() {
		return this.right;
	}

	/**
	 * 上側の境界値を返すメソッド
	 *
	 * @return 上側の境界値
	 */
	public int getTop() {
		return this.top;
	}

	/**
	 * 下側の境界値を返すメソッド
	 *
	 * @return 下側の境界値
	 */
	public int getBottom() {
		return this.bottom;
	}

}
