package aiinterface;

/**
 * AIの実行のタイミングなどのスレッド関連の処理を扱うクラス．
 */
public class ThreadController {

	/**
	 * ThreadController唯一のインスタンス
	 */
	private static ThreadController threadController = new ThreadController();

	/**
	 * P1のAIの処理の開始のタイミングを管理するオブジェクト．
	 */
	private Object AI1;

	/**
	 * P2のAIの処理の開始のタイミングを管理するオブジェクト．
	 */
	private Object AI2;

	/**
	 * P1のAIの処理が終わったかどうかを表すフラグ．<br>
	 * Fastmodeのときのみ使用される．
	 */
	private boolean processedAI1;

	/**
	 * P2のAIの処理が終わったかどうかを表すフラグ．<br>
	 * Fastmodeのときのみ使用される．
	 */
	private boolean processedAI2;

	/**
	 * 各AIの処理を同時に始めるための同期用オブジェクト
	 */
	private Object endFrame;

	/**
	 * フィールド変数を初期化するクラスコンストラクタ
	 */
	private ThreadController() {
		this.AI1 = new Object();
		this.AI2 = new Object();
		this.endFrame = new Object();

		resetProcessedFlag();
	}

	/**
	 * ThreadControllerクラスの唯一のインスタンスを取得する．
	 *
	 * @return ThreadControllerクラスの唯一のインスタンス
	 */
	public static ThreadController getInstance() {
		return threadController;
	}

	/**
	 * 各AIの処理を再開させる．
	 */
	public void resetAllAIsObj() {
		synchronized (this.AI1) {
			this.AI1.notifyAll();
		}
		synchronized (this.AI2) {
			this.AI2.notifyAll();
		}
	}

	/**
	 * 引数に指定したキャラクターの同期用オブジェクトを返す．
	 *
	 * @param playerNumber
	 *            The character's side flag.<br>
	 *            {@code true} if the character is P1, or {@code false} if P2.
	 *
	 * @return 引数に指定したキャラクターの同期用オブジェクト
	 */
	public Object getAIsObject(boolean playerNumber) {
		if (playerNumber)
			return this.AI1;
		else
			return this.AI2;
	}

	/**
	 * 1フレーム分のゲームの処理が終わったことを示すオブジェクトを返す．
	 *
	 * @return 1フレーム分のゲームの処理が終わったことを示すオブジェクト．
	 */
	public Object getEndFrame() {
		return this.endFrame;
	}

	/**
	 * 各AIの処理が終わったかどうかを表すフラグを{@code false}にする．<br>
	 * Fastmodeのときのみ使用される．
	 */
	private void resetProcessedFlag() {
		this.processedAI1 = false;
		this.processedAI2 = false;
	}

	/**
	 * 引数に指定したキャラクターの1フレーム分の処理が終わったことをセットする．<br>
	 * セット後に，両方のAIが処理を終えているかどうかをチェックする．<br>
	 * Fastmodeのときのみ使用される．
	 *
	 * @param playerNumber
	 *            The character's side flag.<br>
	 *            {@code true} if the character is P1, or {@code false} if P2.
	 */
	synchronized public void notifyEndProcess(boolean playerNumber) {
		if (playerNumber) {
			this.processedAI1 = true;
		} else {
			this.processedAI2 = true;
		}
		this.checkEndFrame();
	}

	/**
	 * 現在のフレームにおいて，両AIが処理を終えているかどうかをチェックする．<br>
	 * 終えている場合は，次のフレームの処理を開始させる．<br>
	 * Fastmodeのときのみ使用される．
	 */
	private void checkEndFrame() {
		if (this.processedAI1 && this.processedAI2) {
			synchronized (this.endFrame) {
				this.endFrame.notifyAll();
			}
			this.processedAI1 = false;
			this.processedAI2 = false;
		}
	}

}
