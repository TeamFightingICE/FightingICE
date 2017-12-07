package util;

import java.io.File;

/** ログファイル削除用のシングルトンパターンクラス */
public class DeleteFiles {
	private String[] foldersName;

	/** コンストラクタ */
	private DeleteFiles() {
		System.out.println("delete files...");
		// 削除するフォルダ名
		this.foldersName = new String[] { "log/point", "log/replay" };
	}

	/**
	 * DeleteFilesクラスの唯一のインスタンスを取得するgetterメソッド．
	 *
	 * @return DeleteFilesクラスの唯一のインスタンス
	 */
	public static DeleteFiles getInstance() {
		return DeleteFilesHolder.instance;
	}

	/** getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス */
	private static class DeleteFilesHolder {
		private static final DeleteFiles instance = new DeleteFiles();
	}

	/** pointフォルダとreplayフォルダ内のファイルを削除する */
	public void deleteFiles() {
		for (String folderName : foldersName) {
			File[] fileList = (new File(folderName)).listFiles();
			if (fileList != null) {
				for (File file : fileList) {
					if (!file.delete())
						System.out.println("could not delete file: " + file.toString());
				}
			}
		}
		System.out.println("finish deleting");
	}
}
