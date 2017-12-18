package image;

import java.awt.image.BufferedImage;

/**
 * 画像に関するデータを扱うクラス．
 */
public class Image {

	/**
	 * キャラクターの向きの右方向(true)．
	 */
	static final public boolean DIRECTION_RIGHT = true;

	/**
	 * キャラクターの向きの左方向(false)．
	 */
	static final public boolean DIRECTION_LEFT = false;

	/**
	 * 画像テクスチャのID．
	 */
	private int textureId;

	/**
	 * バッファーに保存された画像．
	 */
	private BufferedImage bimg;

	/**
	 * Imageクラスのインスタンスを生成するためのクラスコンストラクタ．<br>
	 * 引数が指定されていない場合，画像テクスチャのIDを-1，BufferedImageをnullとして初期化を行う．
	 */
	public Image() {
		this.textureId = -1;
		this.bimg = null;
	}

	/**
	 * Imageクラスのインスタンスを生成するためのクラスコンストラクタ．<br>
	 * 引数として渡されたImageインスタンスのテクスチャIDとBufferedImageを取得し，それを用いて新たなインスタンスの初期化を行う．
	 *
	 * @param image
	 *            Imageインスタンス
	 */
	public Image(Image image) {
		this.textureId = image.getTextureId();
		this.bimg = image.getBufferedImage();
	}

	/**
	 * 新たな画像クラスのインスタンスを生成するためのコンストラクタ．<br>
	 * 引数のテクスチャIDとBufferedImageを用いて，新たなインスタンスの初期化を行う．
	 *
	 * @param id
	 *            画像のテクスチャID
	 * @param bimg
	 *            BufferedImage
	 */
	public Image(int id, BufferedImage bimg) {
		this.textureId = id;
		this.bimg = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
	}

	/**
	 * 画像のテクスチャIDを返す．
	 *
	 * @return 画像のテクスチャID
	 */
	public int getTextureId() {
		return this.textureId;
	}

	/**
	 * 画像のBufferedImageを返す．
	 *
	 * @return 画像のBufferedImage
	 */
	public BufferedImage getBufferedImage() {
		return new BufferedImage(this.bimg.getWidth(), this.bimg.getHeight(), this.bimg.getType());
	}

	/**
	 * 画像のテクスチャIDをセットする．
	 *
	 * @param textureId
	 *            画像のテクスチャID
	 */
	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}

	/**
	 * 画像のBufferedImageをセットする．
	 *
	 * @param bimg
	 *            画像のBufferedImage
	 */
	public void setBufferedImage(BufferedImage bimg) {
		this.bimg = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
	}

	/**
	 * 画像の幅を返す．
	 *
	 * @return BufferedImageがnullでなければ画像の幅を返す． nullであれば0を返す．
	 */
	public int getWidth() {
		if (this.bimg != null) {
			return this.bimg.getWidth();
		} else {
			return 0;
		}
	}

	/**
	 * 画像の高さを返す．
	 *
	 * @return 画像のBufferedImageがnullでなければ画像の高さを返す． nullであれば0を返す．
	 */
	public int getHeight() {
		if (this.bimg != null) {
			return this.bimg.getHeight();
		} else {
			return 0;
		}

	}
}
