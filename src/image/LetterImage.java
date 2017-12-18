package image;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import loader.ResourceLoader;

/**
 * 文字の画像を扱うクラス．
 */
public class LetterImage {

	/**
	 * 文字フォント．
	 */
	private Font font;

	/**
	 * アンチエイリアシングを行うかどうかのboolean value．
	 */
	private boolean antiAliasing;

	/**
	 * 文字と文字画像をセットで格納したハッシュマップ．
	 */
	private HashMap<Character, Image> letterImageMap;

	/**
	 * デフォルトコンストラクタ．
	 */
	public LetterImage() {

	}

	/**
	 * LetterImageクラスのインスタンスを生成するためのクラスコンストラクタ．<br>
	 * 文字フォントと，アンチエイリアシング処理を行うかどうかを引数としてインスタンスの初期化を行う．<br>
	 * 英数字及び記号の画像をハッシュマップに登録する．
	 *
	 * @param font
	 *            文字フォント
	 * @param antiAliasing
	 *            アンチエイリアシング処理を行うかどうかのboolean value
	 */
	public LetterImage(Font font, boolean antiAliasing) {
		this.font = font;
		this.antiAliasing = antiAliasing;
		this.letterImageMap = new HashMap<Character, Image>();

		createLetterImage();
	}

	/**
	 * 引数で指定された文字の画像を返す．
	 *
	 * @param letter
	 *            画像として表示させる文字
	 * @return 検索された文字画像
	 */
	public Image getLetterImage(char letter) {
		return letterImageMap.get(letter);
	}

	/**
	 * Creates a standard Java2D BufferedImage of the given letter.
	 *
	 * @param letter
	 *            the letter to create the BufferedImage for
	 *
	 * @return the BufferedImage containing the letter
	 */
	private BufferedImage getLetterBufferedImage(char letter) {
		// Create a temporary image to extract the letter's size
		BufferedImage tempLetterImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) tempLetterImage.createGraphics();

		if (antiAliasing) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		FontMetrics fontMetrics = g.getFontMetrics();

		int letterWidth = fontMetrics.charWidth(letter);
		int letterHeight = fontMetrics.getHeight();
		if (letterWidth <= 0) {
			letterWidth = 1;
		}
		if (letterHeight <= 0) {
			letterHeight = font.getSize();
		}
		tempLetterImage = null;

		// Creates another image holding the character we are creating
		BufferedImage letterImage = new BufferedImage(letterWidth, letterHeight, BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) letterImage.createGraphics();

		if (antiAliasing) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		g.setColor(Color.WHITE);

		g.drawString(String.valueOf(letter), 0, 0 + fontMetrics.getAscent());
		g.dispose();

		return letterImage;

	}

	/**
	 * 英数字及び記号文字のBufferedImageを作成し，OpenGLにそのテクスチャを転送する．<br>
	 * ASCIIコード表から必要な文字のみを抽出する．
	 */
	private void createLetterImage() {
		for (int i = 32; i <= 126; i++) {
			// get 32-126 characters and then custom letters
			BufferedImage bi = getLetterBufferedImage((char) i);

			this.letterImageMap.put((char) i, ResourceLoader.getInstance().loadTextureFromBufferedImage(bi));
		}
	}
}
