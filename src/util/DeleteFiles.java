package util;

import java.io.File;

/** ログファイル削除用のシングルトンパターンクラス */
public class DeleteFiles {

	/** このクラスの唯一のインスタンス */
	private static DeleteFiles instance = new DeleteFiles();

	/** コンストラクタ */
	private DeleteFiles() {
		System.out.println("delete files based on the dafault folders name...");
		//LogSystem.getInstance().logger.log(Level.INFO, "delete files based on the default folders name...");
	}

	/**
	 * このクラスの唯一のインスタンスを返すgetterメソッド
	 *
	 * @return このクラスの唯一のインスタンス
	 */
	public static DeleteFiles getInstance() {
		return instance;
	}

	/** 削除するフォルダ名 */
	private String[] foldersName = { "log/point", "log/replay" };

	/** logフォルダ内のpointファイルとreplayファイルを削除する */
	public void deleteFiles() {
		for (String folderName : foldersName) {
			File[] fileList = (new File(folderName)).listFiles();
			if (fileList != null) {
				for (File file : fileList) {
					if (!file.delete())
						System.out.println("could not delete file " + file.toString());
					//LogSystem.getInstance().logger.log(Level.WARNING, "Could not delete file " + file.toString());
				}
			}
		}
	}
}
