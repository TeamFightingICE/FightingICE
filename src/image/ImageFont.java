package image;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import loader.ResourceLoader;

public class ImageFont {

	private Font font;

	private boolean antiAliasing;

	private Image[] fontImage;


	public ImageFont(Font font, boolean antiAliasing){
		this.font = font;
		this.antiAliasing = antiAliasing;
		this.fontImage = new Image[256];
		
		createFont();
	}

	public Image getCharacter(char whatChar){
		return fontImage[whatChar];
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
	 * Create and store the font
	 *
	 * @param customCharsArray Characters that should be also added to the cache.
	 */
	private void createFont() {

		for (int i = 0; i < 256 ; i++) {
			// get 0-255 characters and then custom characters
			BufferedImage fontImage = getFontImage((char) i);

			this.fontImage[i] = ResourceLoader.getInstance().loadTextureFromBufferedImage(fontImage);
		}

	}

}
