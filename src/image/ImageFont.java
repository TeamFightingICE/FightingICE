package image;

import java.awt.Font;

public class ImageFont {

	private Font font;

	private boolean antiAliasing;


	public ImageFont(Font font, boolean antiAliasing){
		this.font = font;
		this.antiAliasing = antiAliasing;
	}

}
