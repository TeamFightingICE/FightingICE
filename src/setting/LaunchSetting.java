package setting;

import enumerate.BackgroundType;

/** キャラクターの体力や試合の繰り返し回数などといった、試合を行う際に必要な情報をまとめたクラス */
public final class LaunchSetting {

	public static int[] maxHp = { 400, 400 };

	public static int[] maxEnergy = { 300, 300 };

	public static String[] aiNames = { "KeyBoard", "KeyBoard" };

	public static String[] characterNames = { "ZEN", "ZEN" };

	/**
	 * 0: キーボード 1: AI
	 */
	public static int[] deviceTypes = { 0, 0 };

	public static int py4jPort = 4242;

	public static int repeatNumber = 1;

	public static int invertedPlayer = 0;

	public static BackgroundType backgroundType = BackgroundType.Image;

}
