/**
 * 
 */
package retinaPkg;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author lukas
 *
 */
public class RetinaProject {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedImage img = loadImg("/Users/lukas/Documents/im0001.gif");
		PixelManipulator manipulator = new PixelManipulator(img);
		manipulator.enhanceRetina();
		manipulator.saveFile("/Users/lukas/Documents/img.gif");

	}

	private static BufferedImage loadImg(String filepath){
		try {
		    BufferedImage img = ImageIO.read(new File(filepath));
		    return img;
		} catch (IOException e) {
			System.out.println("Wrong image file");
		}
		return null;
	}
}
