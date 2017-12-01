package image;

public class CharacterActionImage {

	private String characterName;

	private String actionName;

	private int frameNumber;

	private Image[] actionImage;

	/**
	 * キャラクターアクション画像クラスの新たなインスタンスを生成するためのコンストラクタ．
	 * キャラクター名,アクション名，そのアクションの総フレーム数と全画像を用いてインスタンスの初期化を行う．
	 *
	 * @param characterName キャラクター名
	 * @param actionName アクション名
	 * @param frameNumber アクションの総フレーム数
	 * @param actionImage アクションの全画像
	 */
	public CharacterActionImage(String characterName, String actionName, int frameNumber, Image[] actionImage) {
		this.characterName = characterName;
		this.actionName = actionName;
		this.frameNumber = frameNumber;
		this.actionImage = actionImage;
	}

	/**
	 * キャラクターアクション画像クラスのインスタンスを検索する際に用いられる，
	 * キャラクター名とアクション名のみを保持するキャラクターアクション画像インスタンスを生成するためのコンストラクタ．
	 * キャラクター名とアクション名を用いてインスタンスの初期化を行う．
	 *
	 * @param characterName キャラクター名
	 * @param actionName アクション名
	 */
	public CharacterActionImage(String characterName, String actionName) {
		this.characterName = characterName;
		this.actionName = actionName;
	}

	/**
	 * 二つのキャラクターアクション画像インスタンスのキャラクター名とアクション名が等しいかどうか比較するメソッド．
	 *
	 * @return 引数のインスタンスがキャラクターアクション画像クラスのインスタンスであれば，呼び出し元と引数のインスタンスを比較し，
	 * キャラクター名とアクション名が等しければtrueを返す．
	 * それ以外の場合はfalseを返す．
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
	 * キャラクターアクション画像インスタンスのキャラクター名を取得するgetterメソッド．
	 *
	 * @return アクション画像オブジェクトのキャラクター名
	 */
	public String getCharacterName() {
		return this.characterName;
	}

	/**
	 * キャラクターアクション画像インスタンスのアクション名を取得するgetterメソッド．
	 *
	 * @return アクション画像オブジェクトのアクション名
	 */
	public String getActionName() {
		return this.actionName;
	}

	/**
	 * キャラクターアクション画像インスタンスの総フレーム数を取得するgetterメソッド．
	 *
	 * @return アクション画像オブジェクトの総フレーム数
	 */
	public int getFrameNumber() {
		return this.frameNumber;
	}

	/**
	 * キャラクターアクション画像インスタンスのアクションに対応する全画像を取得するgetterメソッド．
	 *
	 * @return アクション画像オブジェクトのアクションに対応する全画像
	 */
	public Image[] getActionImage() {
		return this.actionImage;
	}

}
