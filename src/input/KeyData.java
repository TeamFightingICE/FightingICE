package input;

import struct.Key;

/**
 * 同一フレーム内で各プレイヤーが入力したKeyを一つにまとめるためのクラス．
 */
public class KeyData {

	private Key[] keys;

	/**
	 * 引数で渡された使用するDeviceの数に対応する数のKeyをまとめるKeyDataを生成するクラスコンストラクタ．
	 *
	 * @param keyDataLength
	 *            使用するDeviceの数
	 */
	public KeyData(int keyDataLength) {
		keys = new Key[keyDataLength];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = new Key();
		}
	}

	/**
	 * 各プレイヤーのKey入力からKeyDataを作成するクラスコンストラクタ．
	 *
	 * @param keys
	 *            各プレイヤーからのキー入力 基本的には、keys[]のlengthは2
	 */
	public KeyData(Key[] keys) {
		this.keys = new Key[keys.length];
		for (int i = 0; i < keys.length; i++) {
			this.keys[i] = new Key(keys[i]);
		}
	}

	/**
	 * 引数で渡されたKeyDataのインスタンスのコピーを生成するクラスコンストラクタ．<br>
	 * 引数がnullならキー配列を初期化して新たなインスタンスを生成する．
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

	/**
	 * クラスコンストラクタ．
	 */
	public KeyData() {
		// TODO 自動生成されたコンストラクター・スタブ
		keys = new Key[2];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = new Key();
		}
	}

	/**
	 * 各プレイヤーのキー配列を返す．
	 *
	 * @return 各プレイヤーのキー配列
	 */
	public Key[] getKeys() {
		return keys.clone();
	}
}
