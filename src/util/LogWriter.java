package util;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import informationcontainer.RoundResult;
import loader.ResourceLoader;
import setting.FlagSetting;
import setting.LaunchSetting;

public class LogWriter {

	public static final int CSV = 0;

	public static final int TXT = 1;

	public static final int PLOG = 2;

	/** コンストラクタ */
	private LogWriter() {
		System.out.println("create instance: " + LogWriter.class.getName());
	}

	/** getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス */
	private static class LogWriterHolder {
		private static final LogWriter instance = new LogWriter();
	}

	/**
	 * このクラスの唯一のインスタンスを返すgetterメソッド
	 *
	 * @return このクラスの唯一のインスタンス
	 */
	public static LogWriter getInstance() {
		return LogWriterHolder.instance;
	}

	/**
	 * 試合結果を指定した拡張子のファイルへ出力する
	 *
	 * @param roundResults
	 *            各ラウンドの結果を格納しているリスト
	 * @param extension
	 *            指定した拡張子
	 */
	public void outputResult(ArrayList<RoundResult> roundResults, int extension) {
		String path = "./log/point/";
		String mode = FlagSetting.limitHpFlag ? "HPMode" : "TimeMode";
		String timeInfo = LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss", Locale.ENGLISH));
		String fileName = mode + "_" + LaunchSetting.aiNames[0] + "_" + LaunchSetting.aiNames[1] + "_" + timeInfo;

		PrintWriter pw;
		switch (extension) {
		case CSV:
			pw = ResourceLoader.getInstance().openWriteFile(path + fileName + ".csv");
			break;
		case TXT:
			pw = ResourceLoader.getInstance().openWriteFile(path + fileName + ".txt");
			break;
		default:
			pw = ResourceLoader.getInstance().openWriteFile(path + fileName + ".PLOG");
			break;
		}

		for (RoundResult roundResult : roundResults) {
			int[] score = roundResult.getRemainingHPs();

			pw.println(roundResult.getRound() + "," + score[0] + "," + score[1] + "," + roundResult.getElapsedFrame());
		}

		pw.close();
	}

	public void outputLog() {

	}
}
