import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class nonmaxima {
	
	/*  This class is defined to perform the nonmaxima suppresion on the binary threshold image of the input image. The binary image obtained
	 * earlier contains thick edges. This will lead to multiple hough lines and will not give a good final output. Moreover, it will refine
	 * the final output and will show only one hough straight line for each edge.*/
	
	public static void main(String[] args) {
		
		// Locate the image obtained by smoothing the image. Instead of using a Gaussian filter, mean filtering is done which is present in Project1 class
		File thresholdImage = new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\TestImage1meanFilterImage.png");
							
		BufferedImage image1 = null;		// define a buffered image variable 
	try {

		image1 = ImageIO.read(thresholdImage);		// store the smoothed image by mean filter into the buffered image variable defined earlier.
		// create a buffered image variable having the same width and height as mean smoothed image. Define the type as RGB
		BufferedImage testImage = new BufferedImage(image1.getWidth(), image1.getHeight(), image1.TYPE_INT_RGB); 
		
		int[][] gradientMatrix = new int[image1.getWidth()][image1.getHeight()];		// create a matrix to store the gradient values for each pixel
		double[][] angleMatrix = new double[image1.getWidth()][image1.getHeight()];		// create a matrix to store the angle value for each pixel
		int[][] sectorMatrix = new int[image1.getWidth()][image1.getHeight()];		// create a matrix to store the sector where each pixel value lies in after performing non maxima suppression
		int[][] operator = new int[3][3];		// define a 3X3 matrix to calculate the gradient magnitude and angles
		
		/* traverse each pixel in the image left to right, top to bottom, and calculate the gradient magnitude and angle. The gradient magnitude
		 * is calculated by the same way  in which sobel operator was implemented in Project1 class. The angle is calculated as the tan inverse of
		 * yComponent/xComponent */
		
		for(int i=1;i<image1.getWidth()-1;i++) {
			for(int j = 1 ; j<image1.getHeight()-1; j++) {
				operator[0][0] = (new Color(image1.getRGB(i-1, j-1))).getRed();
				operator[0][1] = (new Color(image1.getRGB(i-1, j))).getRed();
				operator[0][2] = (new Color(image1.getRGB(i-1, j+1))).getRed();
				operator[1][0] = (new Color(image1.getRGB(i, j-1))).getRed();
				operator[1][1] = (new Color(image1.getRGB(i, j))).getRed();
				operator[1][2] = (new Color(image1.getRGB(i, j+1))).getRed();
				operator[2][0] = (new Color(image1.getRGB(i+1, j-1))).getRed();
				operator[2][1] = (new Color(image1.getRGB(i+1, j))).getRed();
				operator[2][2] = (new Color(image1.getRGB(i+1, j+1))).getRed();
				
				int val = (int)(calculation2(operator));		// call to function to calculate gradient magnitude
				gradientMatrix[i][j] = val;				// Store the gradient magnitude of pixel at i,j in the matrix
				double theta = angleCalculation(operator);		// call to function to calculate the angle value
				angleMatrix[i][j] = theta;			// Store the gradient angle of pixel at i,j in the matrix
				sectorMatrix[i][j] = sector(theta);		// The sector in which the particular pixel will lie is calculated by sector function and stored in the matrix
				
			}
		}
		// define a buffered image variable having the same height and width as smoothed image. The type is RGB 
		BufferedImage beforeThresholdImage = new BufferedImage(image1.getWidth(), image1.getHeight(), image1.TYPE_INT_RGB);	
		
		/* Traverse each pixel of the image and check the value of sector on the pixel. When sector value is found, it is checked whether the gradient magnitude of
		 * the pixel at that position is the local maxima of the neighbouring pixels along the sector line. If it is the local maxima the value
		 * remains same, else it is set to 0(black).*/		
		
		for(int i = 1; i <image1.getWidth()-1 ; i++) {
			for(int j = 1 ; j<image1.getHeight()-1 ; j++) {
				if(sectorMatrix[i][j] ==0) {
					if(gradientMatrix[i][j] == Math.max(Math.max(gradientMatrix[i][j-1], gradientMatrix[i][j]), gradientMatrix[i][j+1])) {
						beforeThresholdImage.setRGB(i, j, (gradientMatrix[i][j]<<16 | gradientMatrix[i][j]<<8 | gradientMatrix[i][j]));	//<<16 and <<8 are done to perform byte shift
					}
					else {
						beforeThresholdImage.setRGB(i, j, 0);
					}
					
				}
				else if(sectorMatrix[i][j] == 1) {
					if(gradientMatrix[i][j] == Math.max(Math.max(gradientMatrix[i-1][j+1], gradientMatrix[i][j]), gradientMatrix[i+1][j-1])) {
						beforeThresholdImage.setRGB(i, j, (gradientMatrix[i][j]<<16 | gradientMatrix[i][j]<<8 | gradientMatrix[i][j]));
					}
					else {
						beforeThresholdImage.setRGB(i, j, 0);
					}
				}
				else if(sectorMatrix[i][j] == 2) {
					if(gradientMatrix[i][j] == Math.max(Math.max(gradientMatrix[i-1][j], gradientMatrix[i][j]), gradientMatrix[i+1][j])) {
						beforeThresholdImage.setRGB(i, j, (gradientMatrix[i][j]<<16 | gradientMatrix[i][j]<<8 | gradientMatrix[i][j]));
					}
					else {
						beforeThresholdImage.setRGB(i, j, 0);
					}
				}
				else if(sectorMatrix[i][j] == 3) {
					if(gradientMatrix[i][j] == Math.max(Math.max(gradientMatrix[i-1][j-1], gradientMatrix[i][j]), gradientMatrix[i+1][j+1])) {
						beforeThresholdImage.setRGB(i, j, (gradientMatrix[i][j]<<16 | gradientMatrix[i][j]<<8 | gradientMatrix[i][j]));
					}
					else {
						beforeThresholdImage.setRGB(i, j, 0);
					}
				}
			}
		}
		// write the non maxima image to a particular location
		ImageIO.write(beforeThresholdImage, "png", new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\nmsimage.png"));
		// thresholding is performed on the non maxima image to obtain a binary image for improved operations.
		
		// define a buffered image variable to read the non maxima image
		BufferedImage image3 = ImageIO.read(new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\nmsimage.png"));
		
		// define a buffered image variable where we will get the binary image of non maxima suppression
		BufferedImage nmsThresholded = new BufferedImage(image3.getWidth(), image3.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		
		int newColor = 0;
		for(int i = 0 ; i<image3.getWidth() ; i++) {
			for(int j = 0 ;j<image3.getHeight();j++) {
				if((new Color(image3.getRGB(i, j))).getRed()<30)		// if the value of pixel is below 30 set to black else set it to white
					newColor = 255;
				else
					newColor = 0;
				nmsThresholded.setRGB(i, j, (new Color(newColor,newColor,newColor)).getRGB());
			}
		}
		// write the binary image to a particular location.
		ImageIO.write(nmsThresholded, "png", new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\nmsthreshold.png"));
	}
	catch(IOException e) {
		e.printStackTrace();		// to catch any error by ImageIO
	}
	
	}
	public static double calculation2(int[][] kernel) {		// function to calculate gradient magnitude used in non maxima
		
		int xComponent = (kernel[0][0]*(-1))+kernel[0][2]+(kernel[1][0]*(-2))+(kernel[1][2]*(2))+(kernel[2][0]*(-1))+kernel[2][2];
		int yComponent = kernel[0][0]+(kernel[0][1]*2)+kernel[0][2]+(kernel[2][0]*(-1))+(kernel[2][1]*(-2))+(kernel[2][2]*(-1));
		return Math.sqrt(Math.pow(xComponent, 2)+Math.pow(yComponent, 2));
	}
	public static double angleCalculation(int[][] kernel) {		// function to define gradient angle used in non maxima
			
		int xComponent = (kernel[0][0]*(-1))+kernel[0][2]+(kernel[1][0]*(-2))+(kernel[1][2]*(2))+(kernel[2][0]*(-1))+kernel[2][2];
		int yComponent = kernel[0][0]+(kernel[0][1]*2)+kernel[0][2]+(kernel[2][0]*(-1))+(kernel[2][1]*(-2))+(kernel[2][2]*(-1));
		double theta= Math.toDegrees(Math.atan2(yComponent, xComponent));
		if(theta<0) {				// to keep the angle positive and in the range of 0 to 360, all negative angles are added with 360.
			theta = theta +360;
		}
		return theta;
	}
	public static int sector(double theta) {		// function to calculate the sector value of each pixel.
		int sectorvalue = 0;	
		while (theta>360) {				//the angle must be in the range of 0 to 360
			theta = theta-360;
		}
		if(theta>=0 && theta<=22.5 && theta>=157.5 && theta<=202.5 && theta>=337.5 && theta<=360) {		// specefic ranges are defined for the angle to keep the sector in
			sectorvalue = 0;
		}
		else if(theta>22.5 && theta<=67.5 && theta>202.5 && theta<=247.5 ) {
			sectorvalue = 1;
		}
		else if(theta>67.5 && theta<=112.5 && theta>247.5 && theta<=292.5 ) {
			sectorvalue = 2;
		}
		else {
			sectorvalue = 3;
		}
		return sectorvalue;
	}
}
