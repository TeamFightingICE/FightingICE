package setting;

/**
 * ゲーム内で用いられるフラグを設定しているクラス。
 */
public class FlagSetting {

	/**  */
	public static boolean automationFlag = false;

	/**  */
	public static boolean allCombinationFlag = false;

	/** 背景を利用するかどうか */
	public static boolean enableBackground = true;

	/** Pythonを利用するかどうか */
	public static boolean py4j = false;

	/**  */
	public static boolean debugActionFlag = false;

	/**  */
	public static boolean debugFrameDataFlag = false;

	/** 練習モードを利用するかどうか */
	public static boolean trainingModeFlag = false;

	/** P1,P2に最大HPを設定するかどうか */
	public static boolean limitHpFlag = false;

	/** 音をミュートにするかどうか */
	public static boolean muteFlag = false;

	/** jsonファイルを利用するかどうか */
	public static boolean jsonFlag = false;

	/** エラーログを出力するかどうか */
	public static boolean outputErrorAndLogFlag = false;

}
