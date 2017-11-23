package image;

import java.awt.image.BufferedImage;

public class Image {
	static final public boolean DIRECTION_RIGHT = true;
	static final public boolean DIRECTION_LEFT = false;

	private int textureId;
	private BufferedImage bimg;

	public Image() {
		this.textureId = -1;
		this.bimg = null;
	}

	public Image(Image image) {
		this.textureId = image.getTextureId();
		this.bimg = image.getBufferedImage();
	}

	public Image(int id, BufferedImage bimg) {
		this.textureId = id;
		this.bimg = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
	}

	public int getTextureId() {
		return this.textureId;
	}

	public BufferedImage getBufferedImage() {
		return new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
	}

	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}

	public void setBufferedImage(BufferedImage bimg) {
		this.bimg = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
	}

	public int getWidth() {
		if (this.bimg != null) {
			return this.bimg.getWidth();
		} else {
			return 999;
		}
	}

	public int getHeight() {
		if (this.bimg != null) {
			return this.bimg.getHeight();
		} else {
			return 999;
		}

	}
}
