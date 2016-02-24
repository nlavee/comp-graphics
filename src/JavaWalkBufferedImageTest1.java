/**
 * This code is adapted from: 
 * http://alvinalexander.com/blog/post/java/getting-rgb-values-for-each-pixel-in-image-using-java-bufferedi
 * in order to get pixel out of image.
 * 
 * Summary: 
 * 		The code walks through the image, walking through the pixels
 * 		and for each pixel, record the rgb values down.
 * 		It saves information into a text file where the first line is the width and height
 * 		of the image. Each other line is a pixel with the first two values being 
 * 		x, y values, followed by rgb. I reduced the amount of info we save by not saving data for those 
 * 		pixel with rgb values of (0, 0, 0). 
 * @author AnhVuNguyen
 *
 */

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class JavaWalkBufferedImageTest1 extends Component {
	
	public static void main(String[] foo) {
		String file = "";
		new JavaWalkBufferedImageTest1(file);
	}

	/**
	 * This is called first to start reading the file given through param fileName
	 * @param fileName
	 */
	public JavaWalkBufferedImageTest1(String fileName) {
		try {
			URL url = this.getClass().getResource( fileName + ".png");
			System.out.println(url);
			// get the BufferedImage, using the ImageIO class
			BufferedImage image = 
					ImageIO.read(url);
			marchThroughImage(image, fileName);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Go through image, and output into txt file as a save method
	 * @param image
	 * @param fileName
	 */
	private void marchThroughImage(BufferedImage image, String fileName) {
		int w = image.getWidth();
		int h = image.getHeight();
		String size = (w + ", " + h + "\n");

		try
		{
			File file = new File( fileName + ".txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(size);

			for (int i = 0; i < h; i++) 
			{
				for (int j = 0; j < w; j++) 
				{
					String xy = (j + ", " + i+ ", ");
					int pixel = image.getRGB(j, i);
					String rgb = printPixelARGB(pixel);
					if(!rgb.equals("0, 0, 0"))
					{
						String toWrite = xy + rgb + "\n";
						bw.write(toWrite);
					}
				}
			}
		}
		catch(Exception e)
		{

		}
	}
	
	/**
	 * return rgb from java way of representing rgb
	 * @param pixel
	 * @return
	 */
	public String printPixelARGB(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return (""+ red + ", " + green + ", " + blue+ "");
	}
	
}