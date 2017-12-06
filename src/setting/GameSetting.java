package setting;

/**
 * ゲームの画面幅、FPS、使うことができるキャラクターなどの基本情報を設定しているクラス。
 */
public class GameSetting {

	/** ゲーム画面の横幅 */
	public static final int STAGE_WIDTH = 960;

	/** ゲーム画面の縦幅 */
	public static final int STAGE_HEIGHT = 640;

	/** ゲームのFPS */
	public static final int FPS = 60;

	/** ゲームで使うことができるキャラクター */
	public static final String[] CHARACTERS = { "ZEN", "GARNET", "LUD" };

	/** 繰り返しの回数 */
	public static final int[] REPEAT_NUMBERS = { 1, 2, 3, 5, 10, 30, 50, 100 };

	/** 1ラウンドのフレーム数 */
	public static final int ROUND_FRAME_NUMBER = 120;

	/** ラウンド間の休憩時間 */
	public static final int BREAKTIME_FRAME_NUMBER = 70;

	/** 重力の影響を与えるパラメータ*/
	public static final int GRAVITY = 1;

	/** 摩擦の影響を与えるパラメータ */
	public static final int FRICTION = 1;

	/** 入力キーの保持数上限 */
	public static final int INPUT_LIMIT = 30;

	/** 1ラウンドの制限時間(ミリ秒) */
	public static final int ROUND_TIME = 60000;

	/** ラウンド数の上限 */
	public static final int ROUND_MAX = 3;

	/** コンボを継続させるための次アクションまでの入力受け付け時間 */
	public static final int COMBO_LIMIT = 30;

}
