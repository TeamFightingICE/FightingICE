package input;

import struct.Key;

/**
 * 同一フレーム内で各プレイヤーが入力したKeyを管理するクラス．
 */
public class KeyData {

	/**
	 * P1, P2の入力キーを格納した配列<br>
	 * Index 0 is P1, index 1 is P2.
	 */
	private Key[] keys;

	/**
	 * クラスコンストラクタ．
	 */
	public KeyData() {
		this.keys = new Key[2];
		for (int i = 0; i < this.keys.length; i++) {
			this.keys[i] = new Key();
		}
	}

	/**
	 * 各プレイヤーのKey入力からKeyDataを作成するクラスコンストラクタ．
	 *
	 * @param keys
	 *            P1, P2の入力キーを格納した配列.<br>
	 *            Index 0 is P1, index 1 is P2.
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
	 *            P1, P2の入力キー情報.<br>
	 *            Index 0 is P1, index 1 is P2.
	 */
	public KeyData(KeyData keyData) {
		if (keyData != null) {
			this.keys = new Key[keyData.getKeys().length];
			for (int i = 0; i < keyData.getKeys().length; i++) {
				this.keys[i] = new Key(keyData.getKeys()[i]);
			}
		} else {
			this.keys = new Key[2];
			for (int i = 0; i < this.keys.length; i++) {
				this.keys[i] = new Key();
			}
		}
	}

	/**
	 * P1, P2の入力キーを格納した配列を返す．
	 *
	 * @return P1, P2の入力キーを格納した配列.<br>
	 *         Index 0 is P1, index 1 is P2.
	 */
	public Key[] getKeys() {
		return this.keys.clone();
	}
}
