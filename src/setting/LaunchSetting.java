package setting;

import enumerate.BackgroundType;

/** キャラクターの体力や試合の繰り返し回数などといった、試合を行う際に必要な情報をまとめたクラス */
public final class LaunchSetting {

	/** P1,P2の最大HP */
	public static int[] maxHp = { 400, 400 };

	/** P1,P2の最大エネルギー */
	public static int[] maxEnergy = { 300, 300 };

	/** P1,P2のAI名 */
	public static String[] aiNames = { "KeyBoard", "KeyBoard" };

	/** P1,P2のキャラクター名 */
	public static String[] characterNames = { "ZEN", "ZEN" };

	/**
	 * 利用するデバイス
	 * 0: キーボード 1: AI
	 */
	public static char[] deviceTypes = { 0, 0 };

	/** Pythonを利用するときのポート番号 */
	public static int py4jPort = 4242;

	/**  */
	public static int repeatNumber = 1;

	/**  */
	public static int invertedPlayer = 0;

	/** 背景の種類 */
	public static BackgroundType backgroundType = BackgroundType.IMAGE;

	/** リプレイデータの名前 */
	public static String replayName = "None";

	/** 繰り返しの回数 */
	public static int repeatedCount = 0;

}
