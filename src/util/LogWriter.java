package util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import fighting.Character;
import informationcontainer.RoundResult;
import input.KeyData;
import loader.ResourceLoader;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;

public class LogWriter {

	/** 試合結果の出力ファイル拡張子を.csvに指定する定数 */
	public static final int CSV = 0;

	/** 試合結果の出力ファイル拡張子を.txtに指定する定数 */
	public static final int TXT = 1;

	/** 試合結果の出力ファイル拡張子を.PLOGに指定する定数 */
	public static final int PLOG = 2;

	/** コンストラクタ */
	private LogWriter() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + LogWriter.class.getName());
	}

	/** getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス */
	private static class LogWriterHolder {
		private static final LogWriter instance = new LogWriter();
	}

	/**
	 * LogWriterクラスの唯一のインスタンスを取得するgetterメソッド．
	 *
	 * @return LogWriterクラスの唯一のインスタンス
	 */
	public static LogWriter getInstance() {
		return LogWriterHolder.instance;
	}

	/**
	 * 試合結果を指定した拡張子のファイルへ出力する．<br>
	 * 引数によって拡張子を指定し，また引数の現在の時間情報は出力ファイル名に用いられる．
	 *
	 * @param roundResults
	 *            各ラウンドの結果を格納しているリスト
	 * @param extension
	 *            指定した拡張子
	 * @param timeInfo
	 *            現在の時間情報
	 */
	public void outputResult(ArrayList<RoundResult> roundResults, int extension, String timeInfo) {
		String path = "./log/point/";
		String fileName = createOutputFileName(path, timeInfo);

		PrintWriter pw;
		switch (extension) {
		case CSV:
			pw = ResourceLoader.getInstance().openWriteFile(fileName + ".csv");
			break;
		case TXT:
			pw = ResourceLoader.getInstance().openWriteFile(fileName + ".txt");
			break;
		default:
			pw = ResourceLoader.getInstance().openWriteFile(fileName + ".PLOG");
			break;
		}

		for (RoundResult roundResult : roundResults) {
			int[] score = roundResult.getRemainingHPs();

			pw.println(roundResult.getRound() + "," + score[0] + "," + score[1] + "," + roundResult.getElapsedFrame());
		}

		pw.close();
	}

	/**
	 * リプレイファイルのログを出力するメソッド． 現在フレームのキャラクター情報とキー入力のデータが書き込まれる．
	 *
	 * @param dos
	 *            リプレイファイルに書き込みを行うためのデータ出力ストリーム
	 * @param keyData
	 *            KeyDataクラスのインスタンス
	 * @param playerCharacters
	 *            P1とP2のキャラクターを格納した配列
	 */
	public void outputLog(DataOutputStream dos, KeyData keyData, Character[] playerCharacters) {
		// output log file for replay
		try {
			for (int i = 0; i < 2; ++i) {
				dos.writeBoolean(playerCharacters[i].isFront());
				dos.writeByte((byte) playerCharacters[i].getRemainingFrame());
				dos.writeByte((byte) playerCharacters[i].getAction().ordinal());
				dos.writeInt(playerCharacters[i].getHp());
				dos.writeInt(playerCharacters[i].getEnergy());
				dos.writeInt(playerCharacters[i].getX());
				dos.writeInt(playerCharacters[i].getY());

				byte input = (byte) (convertBtoI(keyData.getKeys()[i].A) + convertBtoI(keyData.getKeys()[i].B) * 2
						+ convertBtoI(keyData.getKeys()[i].C) * 4 + convertBtoI(keyData.getKeys()[i].D) * 8
						+ convertBtoI(keyData.getKeys()[i].L) * 16 + convertBtoI(keyData.getKeys()[i].R) * 32
						+ convertBtoI(keyData.getKeys()[i].U) * 64);

				dos.writeByte(input);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * リプレイファイルに起動モード(HP mode or Time mode)や使用キャラといったヘッダ情報を記述するメソッド．
	 *
	 * @param dos
	 *            リプレイファイルに書き込みを行うためのデータ出力ストリーム
	 */
	public void writeHeader(DataOutputStream dos) {
		try {
			for (int i = 0; i < 2; i++) {
				if (FlagSetting.limitHpFlag) {
					dos.writeInt(-1);
					dos.writeInt(LaunchSetting.maxHp[i]);
				}

				dos.writeInt(Arrays.asList(GameSetting.CHARACTERS).indexOf(LaunchSetting.characterNames[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 出力ファイルの名前を生成するメソッド．<br>
	 * "ファイル出力場所のパス+ゲームモード+P1のAI名+P2のAI名+現在時間"をファイル名として返す．
	 *
	 * @param path
	 *            ファイルを出力する場所のパス
	 * @param timeInfo
	 *            現在の時間情報
	 *
	 * @return 出力ファイル名
	 */
	public String createOutputFileName(String path, String timeInfo) {
		String mode = FlagSetting.limitHpFlag ? "HPMode" : "TimeMode";

		return path + mode + "_" + LaunchSetting.aiNames[0] + "_" + LaunchSetting.aiNames[1] + "_" + timeInfo;
	}

	/**
	 * boolean型変数をint型に変換する
	 *
	 * @param b
	 *            変換したいboolean型の変数
	 *
	 * @return 1 : 引数がtrueのとき, 0: 引数がfalseのとき
	 */
	private int convertBtoI(boolean b) {
		return b ? 1 : 0;
	}
}
