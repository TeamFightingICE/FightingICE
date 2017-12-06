package image;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import loader.ResourceLoader;

public class LetterImage {

	private Font font;

	private boolean antiAliasing;

	private HashMap<Character, Image> letterImageMap;

	/**
	 * LetterImageクラスのデフォルトコンストラクタ．
	 */
	public LetterImage() {

	}

	/**
	 * 文字画像インスタンスを生成するためのコンストラクタ．
	 * 文字フォントと，アンチエイリアシング処理を行うかどうかを引数としてインスタンスの初期化を行う．
	 * 引数に対応した処理が施された英数字及び記号の画像がハッシュマップに登録される．
	 *
	 * @param font 文字フォント
	 * @param antiAliasing アンチエイリアシング処理を行うかどうか(true or false)
	 */
	public LetterImage(Font font, boolean antiAliasing) {
		this.font = font;
		this.antiAliasing = antiAliasing;
		this.letterImageMap = new HashMap<Character, Image>();

		createLetterImage();
	}

	/**
	 * 文字を引数として，文字画像を取得するgetterメソッド．
	 *
	 * @param letter 画像として表示させる文字
	 * @return 検索された文字の画像
	 *         文字画像が存在しない場合はnullを返す．
	 */
	public Image getLetterImage(char letter) {
		return letterImageMap.get(letter);
	}

	/**
	 * Create a standard Java2D BufferedImage of the given character
	 *
	 * @param letter
	 *            The character to create a BufferedImage for
	 *
	 * @return A BufferedImage containing the character
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

		// Create another image holding the character we are creating
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
	 * 英数字及び記号文字のイメージを作成し，OpenGLにそのテクスチャを転送する． ASCIIコード表から必要な部分のみを抽出
	 */
	private void createLetterImage() {

		for (int i = 32; i <= 126; i++) {
			// get 32-126 characters and then custom letters
			BufferedImage bi = getLetterBufferedImage((char) i);

			this.letterImageMap.put((char) i, ResourceLoader.getInstance().loadTextureFromBufferedImage(bi));
		}

	}

}
