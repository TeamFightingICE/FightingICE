package image;

import java.awt.image.BufferedImage;

public class Image {

	/** キャラクターの向きの右方向をtrueとする． */
	static final public boolean DIRECTION_RIGHT = true;

	/** キャラクターの向きの左方向をfalseとする． */
	static final public boolean DIRECTION_LEFT = false;

	private int textureId;
	private BufferedImage bimg;

	/**
	 * 新たな画像クラスのインスタンスを生成するためのコンストラクタ．
	 * 引数が指定されていない場合，画像テクスチャのIDを-1，BufferedImageをnullとして初期化を行う．
	 */
	public Image() {
		this.textureId = -1;
		this.bimg = null;
	}

	/**
	 * 新たな画像クラスのインスタンスを生成するためのコンストラクタ．
	 * 引数の画像インスタンスのテクスチャIDとBufferedImageを取得し，それを用いて新たなインスタンスの初期化を行う．
	 *
	 * @param image 画像インスタンス
	 */
	public Image(Image image) {
		this.textureId = image.getTextureId();
		this.bimg = image.getBufferedImage();
	}

	/**
	 * 新たな画像クラスのインスタンスを生成するためのコンストラクタ．
	 * 引数のテクスチャIDとBufferedImageを用いて，新たなインスタンスの初期化を行う．
	 *
	 * @param id テクスチャID
	 * @param bimg BufferedImage
	 */
	public Image(int id, BufferedImage bimg) {
		this.textureId = id;
		this.bimg = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
	}

	/**
	 * 画像インスタンスのテクスチャIDを取得するgetterメソッド．
	 *
	 * @return 画像インスタンスのテクスチャID
	 */
	public int getTextureId() {
		return this.textureId;
	}

	/**
	 * 画像インスタンスのBufferedImageを取得するgetterメソッド．
	 *
	 * @return 画像インスタンスのBufferedImage
	 */
	public BufferedImage getBufferedImage() {
		return new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
	}

	/** 画像インスタンスのテクスチャIDに引数のテクスチャIDを設定するsetterメソッド．*/
	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}

	/** 画像インスタンスのBufferedImageに引数のBufferedImageを設定するsetterメソッド．*/
	public void setBufferedImage(BufferedImage bimg) {
		this.bimg = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
	}

	/**
	 * 画像の幅を取得するgetterメソッド．
	 *
	 * @return 画像インスタンスのBufferedImageがnullでなければ画像の幅を返す．
	 *         nullであれば999を返す．
	 */
	public int getWidth() {
		if (this.bimg != null) {
			return this.bimg.getWidth();
		} else {
			return 999;
		}
	}

	/**
	 * 画像の高さを取得するgetterメソッド．
	 *
	 * @return 画像インスタンスのBufferedImageがnullでなければ画像の高さを返す．
	 *         nullであれば999を返す．
	 */
	public int getHeight() {
		if (this.bimg != null) {
			return this.bimg.getHeight();
		} else {
			return 999;
		}

	}
}
