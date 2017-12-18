package image;

/**
 * キャラクターのアクション画像を扱うクラス．
 */
public class CharacterActionImage {

	/**
	 * キャラクターの名前．
	 */
	private String characterName;

	/**
	 * アクションの名前．
	 */
	private String actionName;

	/**
	 * アクションの総フレーム数．
	 */
	private int frameNumber;

	/**
	 * アクションの全画像を格納する配列．
	 */
	private Image[] actionImage;

	/**
	 * CharacterActionImageクラスの新たなインスタンスを生成するためのクラスコンストラクタ．
	 * キャラクター名，アクション名，そのアクションの総フレーム数と全画像を用いてインスタンスの初期化を行う．
	 *
	 * @param characterName
	 *            キャラクター名
	 * @param actionName
	 *            アクション名
	 * @param frameNumber
	 *            アクションの総フレーム数
	 * @param actionImage
	 *            アクションの全画像
	 */
	public CharacterActionImage(String characterName, String actionName, int frameNumber, Image[] actionImage) {
		this.characterName = characterName;
		this.actionName = actionName;
		this.frameNumber = frameNumber;
		this.actionImage = actionImage;
	}

	/**
	 * キャラクター名とアクション名のみを保持するCharacterActionImageインスタンスを生成するためのクラスコンストラクタ．
	 * CharacterActionImageクラスのインスタンスを検索する際に用いられる．
	 *
	 * @param characterName
	 *            キャラクター名
	 * @param actionName
	 *            アクション名
	 */
	public CharacterActionImage(String characterName, String actionName) {
		this.characterName = characterName;
		this.actionName = actionName;
	}

	/**
	 * 引数と呼び出し元のCharacterActionImageインスタンスのキャラクター名とアクション名が等しいかどうかを比較する．
	 *
	 * @return {@code true} キャラクター名とアクション名が等しい場合，{@code false} otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharacterActionImage) {
			CharacterActionImage temp = (CharacterActionImage) obj;

			return this.characterName.equals(temp.getCharacterName()) && this.actionName.equals(temp.getActionName());
		} else {
			return false;
		}
	}

	/**
	 * キャラクター名を返す．
	 *
	 * @return キャラクター名
	 */
	public String getCharacterName() {
		return this.characterName;
	}

	/**
	 * アクション名を返す．
	 *
	 * @return アクション名
	 */
	public String getActionName() {
		return this.actionName;
	}

	/**
	 * アクションの総フレーム数を返す．
	 *
	 * @return アクションの総フレーム数
	 */
	public int getFrameNumber() {
		return this.frameNumber;
	}

	/**
	 * アクションに対応する全画像を返す．
	 *
	 * @return アクションに対応する全画像
	 */
	public Image[] getActionImage() {
		return this.actionImage;
	}

}
