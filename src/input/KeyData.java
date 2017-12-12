package input;

import struct.Key;

/**
 *　同一フレーム内で各プレイヤー(AI,Keyboard)が入力したKeyを一つにまとめるためのクラス
 */
public class KeyData {

	private Key[] keys;

	/**
	 * 指定されたキー番号のキーを生成するコンストラクタ
	 *
	 * @param keyNumber
	 *            キー番号
	 */
	public KeyData(int keyNumber) {
		keys = new Key[keyNumber];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = new Key();
		}
	}

	/**
	 * 2つのKey入力から1つのKeyDataを作成するコンストラクタ
	 *
	 * @param keys
	 *            各プレイヤーからのキー入力
	 */
	public KeyData(Key[] keys) {
		this.keys = new Key[keys.length];
		for (int i = 0; i < keys.length; i++) {
			this.keys[i] = new Key(keys[i]);
		}
	}

	/**
	 *
	 * @param keyData
	 *            Source data
	 */
	public KeyData(KeyData keyData) {
		if (keyData != null) {
			this.keys = new Key[keyData.getKeys().length];
			for (int i = 0; i < keyData.getKeys().length; i++) {
				keys[i] = new Key(keyData.getKeys()[i]);
			}
		} else {
			keys = new Key[2];
			for (int i = 0; i < keys.length; i++) {
				keys[i] = new Key();
			}
		}
	}

	public KeyData() {
		// TODO 自動生成されたコンストラクター・スタブ
		keys = null;
	}

	/**
	 * 2つのプレイヤーのキー配列を返すメソッド
	 *
	 * @return 2つのプレイヤーのキー配列
	 */
	public Key[] getKeys() {
		return keys.clone();
	}
}
