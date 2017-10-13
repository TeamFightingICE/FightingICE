package image;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import loader.ResourceLoader;

public class LetterImage {

	private Font font;

	private boolean antiAliasing;

	private Image[] letterImage;

	public LetterImage() {
		
	}

	public LetterImage(Font font, boolean antiAliasing){
		this.font = font;
		this.antiAliasing = antiAliasing;
		this.letterImage = new Image[256];

		createFont();
	}

	public Image getCharacter(char whatChar){
		return letterImage[whatChar];
	}

	/**
	 * Create a standard Java2D BufferedImage of the given character
	 *
	 * @param ch
	 *            The character to create a BufferedImage for
	 *
	 * @return A BufferedImage containing the character
	 */
	private BufferedImage getFontImage(char ch) {
		// Create a temporary image to extract the character's size
		BufferedImage tempFontImage = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) tempFontImage.createGraphics();

		if (antiAliasing) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		FontMetrics tempFontMetrics = g.getFontMetrics();

		int charwidth = tempFontMetrics.charWidth(ch);
		int charheight = tempFontMetrics.getHeight();
		if (charwidth <= 0) {
			charwidth = 1;
		}
		if (charheight <= 0) {
			charheight = font.getSize();
		}
		tempFontImage = null;

		// Create another image holding the character we are creating
		BufferedImage charImage;
		charImage = new BufferedImage(charwidth, charheight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D) charImage.createGraphics();

		if (antiAliasing == true) {
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		gt.setFont(font);
		FontMetrics fontMetrics = g.getFontMetrics();
		gt.setColor(Color.WHITE);

		gt.drawString(String.valueOf(ch), 0, 0
				+ fontMetrics.getAscent());

		return charImage;

	}

	/**
	 * 英数字及び記号文字のイメージを作成し，OpenGLにそのテクスチャを転送する．
	 * ASCIIコード表から必要な部分のみを抽出
	 */
	private void createFont() {

		for (int i = 32; i <= 126 ; i++) {
			// get 32-126 characters and then custom letters
			BufferedImage fontImage = getFontImage((char) i);

			this.letterImage[i] = ResourceLoader.getInstance().loadTextureFromBufferedImage(fontImage);
		}

	}

}
