package informationcontainer;

public class MenuItem {

	private String string;

	private int coordinateX;

	private int coordinateY;

	private int cursorPosition;

	/**
	 * 新たなメニュー項目クラスのインスタンスを生成するためのコンストラクタ．
	 * メニュー項目として表示させる文字，メニュー項目を表示させる場所のX座標とY座標，
	 * 選択カーソルを表示させる場所を指定するためのカーソル位置番号を用いてインスタンスの初期化を行う．
	 *
	 * @param string メニュー項目の文字
	 * @param coordinateX メニュー項目のX座標
	 * @param coordinateY メニュー項目のY座標
	 * @param cursorPosition カーソル位置番号
	 */
	public MenuItem(String string, int coordinateX, int coordinateY, int cursorPosition) {
		this.string = string;
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
		this.cursorPosition = cursorPosition;
	}

	/**
	 * メニュー項目の文字を取得するgetterメソッド．
	 *
	 * @return メニュー項目の文字
	 */
	public String getString() {
		return this.string;
	}

	/**
	 * メニュー項目のX座標を取得するgetterメソッド．
	 *
	 * @return メニュー項目のX座標
	 */
	public int getCoordinateX() {
		return this.coordinateX;
	}

	/**
	 * メニュー項目のY座標を取得するgetterメソッド．
	 *
	 * @return メニュー項目のY座標
	 */
	public int getCoordinateY() {
		return this.coordinateY;
	}

	/**
	 * メニュー項目のカーソル位置番号を取得するgetterメソッド．
	 *
	 * @return メニュー項目のカーソル位置番号
	 */
	public int getCursorPosition() {
		return this.cursorPosition;
	}
}
