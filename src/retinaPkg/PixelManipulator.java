package retinaPkg;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 */

/**
 * @author lukas
 *
 */
public class PixelManipulator {
	
	//private final static double
	// measurements of the image
	private final int WIDTH = 150;
	private final int HEIGHT = 150;
	
	// existing image
	private BufferedImage img;
	
	// new image
	private BufferedImage newImg = null;
	
	// factor & bias
	private int factor, bias;
	
	private double[][] currentMatrix;
	
	// boolean variable to check whether the permutation is done for the first time
	private boolean isFirstTime;
	
	public PixelManipulator(BufferedImage img) {
		this.img = img;//this.resizeImg(img);
		
		// create empty image
		this.newImg = new BufferedImage(this.img.getWidth(), this.img.getHeight(), img.getType());
		
		this.bias = 0;
		this.factor = 1;
		this.isFirstTime = true;
	}
	
	/**
	 * Resizes the original image, so the sizes of two are the same
	 * 
	 * @deprecated We don't really need to use resizing
	 */
	private BufferedImage resizeImg(BufferedImage img){
		BufferedImage resizedImage = new BufferedImage(this.WIDTH, this.HEIGHT, img.getType());
		Graphics2D g = resizedImage.createGraphics();
		
		g.drawImage(img, 0, 0, this.WIDTH, this.HEIGHT, null);
		g.dispose();
	 
		return resizedImage;
	}
	
	private void applyFilter(double[][] filter){
		int r,g,b; // rgb components
		int imageX, imageY;
		int pixel; //pixel int
		for(int x = 0; x < this.img.getWidth(); x++){
			for (int y = 0; y < this.img.getHeight(); y++){
				r = g = b = 0; //init rgb component

				
				for (int i = 0; i < filter.length; i++){
					for (int j = 0; j < filter[0].length; j++){
						imageX = (x - (filter[0].length - 1) / 2 + i + this.img.getWidth()) % this.img.getWidth();
						imageY = (y - (filter.length - 1) / 2 + j + this.img.getHeight()) % this.img.getHeight();
						
						// apply the filter
						
						/*
						 * First time around we perform the transform on the original image, but
						 * any time after that it has to be performed on already altered image
						 */
						pixel = this.isFirstTime ? this.img.getRGB(imageX, imageY) : this.newImg.getRGB(imageX, imageY);

						r += (int) (((pixel >> 16) & 0xFF) * filter[i][j]);
						g += (int) (((pixel >> 8) & 0xFF) * filter[i][j]);
						b += (int) ((pixel & 0xFF) * filter[i][j]);
						
					}
				}
				
				// normalize
				r = Math.min(this.factor * Math.max(r, 0) + this.bias, 255);
				g = Math.min(this.factor * Math.max(g, 0) + this.bias, 255);
				b = Math.min(this.factor * Math.max(b, 0) + this.bias, 255);
				
				this.newImg.setRGB(x, y, (r << 16) | (g << 8) | b);
			}
		}
		
		// first permutation is done. let it be false
		this.isFirstTime = false;
	}
	
	public void enhanceRetina(){
		for(int i = 0; i <= 180; i += 15){
			this.calculateGaussian(i);
			this.applyFilter(this.currentMatrix);
		}
	}
	
	/**
	 * 
	 * @param angle
	 * @return
	 * 
	 * @deprecated We don't need to use it - it was just to compute the sum initially
	 */
	public static double precomputeSum(int angle){
		double sum = 0.0, x, y;
		double rTheta = angle / 180 * Math.PI;
		
		for(int i = -7; i <= 7; i++){
			for(int j = -7; j <= 7; j++){
				x = i * Math.cos(rTheta) - j * Math.sin(rTheta);
				y = i * Math.sin(rTheta) + j * Math.cos(rTheta);
				sum += Math.exp(-(x * x/2 + y * y/3));
			}
		}
		System.out.println("Angle: " + Integer.toString(angle) + " sum: " + Double.toString(sum));
		return sum;
	}
	
	private void calculateGaussian(int angle){
		double x, y;
		double rTheta = angle / 180 * Math.PI;
		
		// reset the currentMatrix prior to manipulation
		this.currentMatrix = new double[14][14];
		
		for(int i = -7; i < 7; i++){
			for(int j = -7; j < 7; j++){
				x = i * Math.cos(rTheta) - j * Math.sin(rTheta);
				y = i * Math.sin(rTheta) + j * Math.cos(rTheta);
				
				// apparently the 7.6952990194143025 is same for all angles
				this.currentMatrix[7 + i][7 + j] = Math.exp(-(x * x/2 + y * y/3)) / 7.6952990194143025;
				
			}
		}
	}
	
	public void saveFile(String filename){
		try{
			File outputfile = new File(filename);
			ImageIO.write(this.newImg, "gif", outputfile);
		} catch(IOException e){
			System.out.println("Cannot write into the image");
		}
	}

}
