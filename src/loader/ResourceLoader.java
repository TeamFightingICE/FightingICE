package loader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ResourceLoader {

	public void loadImage(String filePath) {
		BufferedImage bimg = null;

		try {
			bimg = ImageIO.read(new FileInputStream(new File(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void loadTextureFromImage(BufferedImage image){

	}
}
