import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Project1 {

	public static void main(String[] args) {
		
		File originalImage = new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\sample.png");	//detecting input sample image
		BufferedImage image = null; 	// define a buffered image to store the input image in buffered format. Buffered image used for performing operations
		try {			// try statement for including the exceptions that are caused by ImageIO.
			image = ImageIO.read(originalImage);		// read the input image into buffered image
			//define a new buffered image variable , width and height is kept same as input image and the type is RGB
			
			BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
			
			/*Conversion of image from RGB scale to greyscale. For each pixel left to right, top to bottom, the color component 
			 * is extracted in the Color variable(c). The luminance is calculated according to the formula provided in NYU Classes. 
			 * Another color variable c2 is created which contains the luminance value as Red,Green and Blue. The pixel colour is set
			 * in the buffer image grayIage defined earlier. This converts the RGB image to greyscale image with 0 being the darkest 
			 * and 255 being the brightest. */
			
			for(int i = 0 ; i< image.getWidth() ; i++) {
				for (int j = 0 ; j<image.getHeight() ; j++) {
					Color c = new Color(image.getRGB(i,j));
					int r = c.getRed();
					int g = c.getGreen();
					int b = c.getBlue();
					int luminance = (int) ((0.3*r)+(0.59*g)+(0.11*b));
					Color c2 = new Color(luminance,luminance,luminance);
					
					grayImage.setRGB(i,j, c2.getRGB());
					
				}
			}
			/*The buffered image grayscale variable is written out into a new file as a png image. The 2nd parameter of the following line
			 * defines the format of the output image. The third argument defines the location where the output will be saved.*/
			
			ImageIO.write(grayImage, "png", new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\TestImage1cgray.png"));
			
		} catch(IOException e) {
			e.printStackTrace();	// if there is an error in ImageIO
		}
		BufferedImage image1 = null;		//defining buffered image variables for further operations
		BufferedImage image2 = null;
		BufferedImage image3 = null;
		try {
			/* read the grayscale image into the first buffered image variable */
			
			image1 = ImageIO.read(new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\TestImage1cgray.png"));	
			
			/* define a buffered image variable to create the mean filter image. The width and height is same as the image1 variable
			 * which is the buffered image storing the grayscale image. The type of the mean filter is defined as RGB*/
			
			BufferedImage meanFilterImage = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
			
			int sum = 0;		//initialize a variable sum as 0
			
			/* Applying the mean filter on the image . This is done by traversing each pixel in the image but starting from the 2nd pixel
			 * and ending at the 2nd last pixel both in row and height. The sum is calculated for the neighbouring 3X3 pixel of the
			 * selected pixel. The value of the pixel is replaced by the sum of 3X3 neighbouring pixels divided by 9.
			 * In this way, the color component is modified to get the mean filter image.*/
			
			for(int i=1; i<image1.getWidth()-1;i++) {
				for(int j =1;j<image1.getHeight()-1 ; j++) {
					sum = (new Color(image1.getRGB(i-1, j-1))).getRed() +(new Color(image1.getRGB(i-1, j))).getRed()+(new Color(image1.getRGB(i-1, j+1))).getRed()+
							(new Color(image1.getRGB(i, j-1))).getRed()+(new Color(image1.getRGB(i, j))).getRed()+(new Color(image1.getRGB(i, j+1))).getRed()
							+(new Color(image1.getRGB(i+1, j-1))).getRed()+(new Color(image1.getRGB(i+1, j))).getRed()+(new Color(image1.getRGB(i+1, j+1))).getRed();
					sum = sum/9;
					Color c3 = new Color(sum,sum,sum);
					meanFilterImage.setRGB(i, j, c3.getRGB());		//set the new color value to each pixel.
				}
			}
			
			/* Write the mean filter image into new file. The second argument defines the type of the image format. The third argument
			 * defines the location where the output is stored. */
			
			ImageIO.write(meanFilterImage, "png", new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\TestImage1meanFilterImage.png"));
			//Store the mean filter image in 2nd buffered image variable
			image2 = ImageIO.read(new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\TestImage1meanFilterImage.png"));
			/* Create a new buffered image variable for creating the sobel edge detection image. The width and height is same as
			 * mean filter image and the type defined is RGB.*/
			BufferedImage edgeImage = new BufferedImage(image2.getWidth(), image2.getHeight(), BufferedImage.TYPE_INT_RGB);
			int kernel[][] = new int[3][3];			// initialize a matrix of size 3X3
			
			/*Traverse each pixel of the image. Store the neighbouring 3X3 pixels into the kernel matrix. These values are then multiplied by the sobel 
			 * gradient matrix. These gradient matrix are calculated for both x component and y component. The value of the pixel is obtained
			 * as the square root(sum of squares of gradient in x and y component).The calculated value is set as the new color component in 
			 * the buffered image for sobel. */
			
			for(int i =1 ; i<image2.getWidth()-1 ; i++) {
				for(int j = 1; j<image2.getHeight() -1; j++) {
					kernel[0][0] = (new Color(image2.getRGB(i-1, j-1))).getRed();
					kernel[0][1] = (new Color(image2.getRGB(i-1, j))).getRed();
					kernel[0][2] = (new Color(image2.getRGB(i-1, j+1))).getRed();
					kernel[1][0] = (new Color(image2.getRGB(i, j-1))).getRed();
					kernel[1][1] = (new Color(image2.getRGB(i, j))).getRed();
					kernel[1][2] = (new Color(image2.getRGB(i, j+1))).getRed();
					kernel[2][0] = (new Color(image2.getRGB(i+1, j-1))).getRed();
					kernel[2][1] = (new Color(image2.getRGB(i+1, j))).getRed();
					kernel[2][2] = (new Color(image2.getRGB(i+1, j+1))).getRed();
					
					int edgeValue = (int)(calculation(kernel));		//calculation is the function for calculating the gradient value from sobel operator
					
					edgeImage.setRGB(i, j, (edgeValue<<16 | edgeValue<<8 | edgeValue));		//the calculated value is set as the new color in the image. <<16 and <<8 are performed to perform byte shift in the value.
					
				}
			}
			/* write the sobel edge image into a particular solution as png image */
			ImageIO.write(edgeImage, "png", new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\TestImage1cedge.png"));
			// the sobel image is read into the third buffered image variable.
			image3 = ImageIO.read(new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\TestImage1cedge.png"));
			// a new buffered image variable is defined to obtain the binary image of the sobel thresholded image
			BufferedImage edgeThresholded = new BufferedImage(image3.getWidth(), image3.getHeight(), BufferedImage.TYPE_INT_RGB);
			int newColor = 0;		// variable defined for color component
			
			/*The image is traversed pixel by pixel and the value is set to either 0(black) or 255(white). A value of threshold is decided.
			 * If the value of pixel is before that, then the value is set to 0 ekse it is set to 255. */
			
			for(int i = 0 ; i<image3.getWidth() ; i++) {
				for(int j = 0 ;j<image3.getHeight();j++) {
					if((new Color(image3.getRGB(i, j))).getRed()<70)		//checking whether pixel lies below or above the threshold value
						newColor = 0;		//black pixel
					else
						newColor = 255;		//white pixel
					edgeThresholded.setRGB(i, j, (new Color(newColor,newColor,newColor)).getRGB());
				}
			}
			//write the binary image to a particular location as png image.
			ImageIO.write(edgeThresholded, "png", new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\TestImage1cedgethreshold.png"));
		}
		catch(IOException e) {
			e.printStackTrace();		// to catch any error caused by ImageIO
		}

	}

	public static double calculation(int[][] kernel) {		// function to calculate the gradient value by sobel operator.
		// TODO Auto-generated method stub
		int xComponent = (kernel[0][0]*(-1))+kernel[0][2]+(kernel[1][0]*(-2))+(kernel[1][2]*(2))+(kernel[2][0]*(-1))+kernel[2][2];	
		int yComponent = kernel[0][0]+(kernel[0][1]*(2))+kernel[0][2]+(kernel[2][0]*(-1))+(kernel[2][1]*(-2))+(kernel[2][2]*(-1));
		return Math.sqrt(Math.pow(xComponent, 2)+Math.pow(yComponent, 2));
	}

}
