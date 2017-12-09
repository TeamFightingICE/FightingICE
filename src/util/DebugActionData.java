package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import fighting.Character;
import loader.ResourceLoader;
import setting.LaunchSetting;

public class DebugActionData {

	/**
	 * P1, P2の行動回数をカウントする対象の行動名とその総フレーム数を管理するマップを格納したリスト<br>
	 * Index 0: P1; Index 1: P2
	 */
	private ArrayList<HashMap<String, Integer>> actionList;

	/**
	 * P1, P2の行動回数をカウントする対象の行動名と1ラウンド中のその行動回数を管理するマップを格納したリスト<br>
	 * Index 0: P1; Index 1: P2
	 */
	private ArrayList<HashMap<String, Integer>> countedActionContainer;

	/**
	 * ファイル読み込み用のReader<br>
	 * Index 0: P1; Index 1: P2
	 */
	private BufferedReader[] bReaders;

	/**
	 * ファイル書き込み用のWriter<br>
	 * Index 0: P1; Index 1: P2
	 */
	private PrintWriter[] pWriters;

	/**
	 * カウントする行動名を格納した配列
	 */
	private final String[] motionName = { "FORWARD_WALK", "DASH", "BACK_STEP", "JUMP", "FOR_JUMP", "BACK_JUMP",
			"STAND_GUARD", "CROUCH_GUARD", "AIR_GUARD", "THROW_A", "THROW_B", "STAND_A", "STAND_B", "CROUCH_A",
			"CROUCH_B", "AIR_A", "AIR_B", "AIR_DA", "AIR_DB", "STAND_FA", "STAND_FB", "CROUCH_FA", "CROUCH_FB",
			"AIR_FA", "AIR_FB", "AIR_UA", "AIR_UB", "STAND_D_DF_FA", "STAND_D_DF_FB", "STAND_F_D_DFA", "STAND_F_D_DFB",
			"STAND_D_DB_BA", "STAND_D_DB_BB", "AIR_D_DF_FA", "AIR_D_DF_FB", "AIR_F_D_DFA", "AIR_F_D_DFB", "AIR_D_DB_BA",
			"AIR_D_DB_BB", "STAND_D_DF_FC" };

	/**
	 * DebugActionDataクラス唯一のインスタンスを取得するgetterメソッド
	 *
	 * @return DebugActionDatクラスの唯一のインスタンス
	 */
	public static DebugActionData getInstance() {
		return DebugActionDataHolder.instance;
	}

	/** getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス */
	private static class DebugActionDataHolder {
		private static final DebugActionData instance = new DebugActionData();
	}

	/** コンストラクタ */
	private DebugActionData() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + DebugActionData.class.getName());
		Logger.getAnonymousLogger().log(Level.INFO, "start debug action mode...");
	}

	/**
	 * DebugActionDataクラスの初期化を行う.<br>
	 * 1. フィールド変数の初期化<br>
	 * 2. 出力用のファイルをオープンし, 行動名をヘッダ情報として出力<br>
	 * 3. カウントする対象の行動名とその総フレーム数をリストに格納<br>
	 */
	public void initialize() {
		this.actionList = new ArrayList<HashMap<String, Integer>>(2);
		this.countedActionContainer = new ArrayList<HashMap<String, Integer>>(2);
		this.pWriters = new PrintWriter[2];
		this.bReaders = new BufferedReader[2];

		for (int i = 0; i < 2; i++) {
			this.actionList.add(new HashMap<String, Integer>());
			this.countedActionContainer.add(new HashMap<String, Integer>());
		}

		String path = "./debugActionData";
		new File(path).mkdir();

		// 読み込み・書き込みファイルをオープンする
		for (int i = 0; i < 2; i++) {
			String fileName = "/" + (i == 0 ? "P1" : "P2") + "ActionFile.csv";
			this.pWriters[i] = ResourceLoader.getInstance().openWriteFile(path + fileName, true);
			this.bReaders[i] = ResourceLoader.getInstance().openReadFile(path + fileName);

			writeHeader(i);
			readMotionData(i);
		}
	}

	/**
	 * P1とP2の各行動の実行回数をカウントする.<br>
	 * 行動が実行される時点のみカウントする.<br>
	 * カウントする対象外の行動や, 実行途中の行動に関してはカウントしない.
	 *
	 * @param fd
	 *            キャラクターのデータといったゲーム情報を格納したフレームデータ
	 */
	public void countPlayerAction(Character[] characters) {
		String[] actionNames = new String[] { characters[0].getAction().name(), characters[1].getAction().name() };
		int[] remainingFrames = new int[] { characters[0].getRemainingFrame(), characters[1].getRemainingFrame() };

		for (int i = 0; i < 2; i++) {
			if (canCount(this.countedActionContainer.get(i), actionNames[i], remainingFrames[i])) {
				this.countedActionContainer.get(i).replace(actionNames[i],
						this.countedActionContainer.get(i).get(actionNames[i]) + 1);
			}
		}
	}

	/** P1とP2の各行動の実行回数をCSVに出力する */
	public void outputActionCount() {
		for (int i = 0; i < 2; i++) {
			for (String string : this.motionName) {
				this.pWriters[i].print(this.countedActionContainer.get(i).get(string) + ",");
				this.countedActionContainer.get(i).replace(string, 0);
			}

			this.pWriters[i].println();
			this.pWriters[i].flush();
		}
	}

	public void closeAllWriters() {
		for (int i = 0; i < 2; i++) {
			this.pWriters[i].close();
		}
		this.actionList.clear();
		this.countedActionContainer.clear();
	}

	private void writeHeader(int i) {
		try {
			if (this.bReaders[i].read() == -1) {
				for (String string : this.motionName) {
					this.pWriters[i].print(string + ",");
				}
				this.pWriters[i].println();
			} else {
				this.pWriters[i].println();
				this.pWriters[i].println();
			}

			this.bReaders[i].close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readMotionData(int i) {
		String fileName = "./data/characters/" + LaunchSetting.characterNames[i] + "/Motion.csv";
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String s;
			while ((s = br.readLine()) != null) {
				String array[] = s.split(","); // カンマで分割

				for (String string : this.motionName) {
					if (string.equals(array[0])) {
						this.actionList.get(i).put(string, Integer.parseInt(array[1]));
						this.countedActionContainer.get(i).put(string, 0);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean canCount(HashMap<String, Integer> temp, String actionName, int remainingFrame) {
		if (temp.containsKey(actionName)) {
			return temp.get(actionName) == remainingFrame - 1;
		} else {
			return false;
		}
	}
}
