import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;

public class Hough {

	/* This class is defined to perform the Hough Transform to obtain the straight lines of the edges in the image.*/
	
	public static void main(String[] args) throws Exception {
		// Buffered Image created to perform operations
		BufferedImage img1 = ImageIO.read(new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\nmsthreshold.png"));
		Hough houghObject = new Hough(img1.getWidth(),img1.getHeight());		//Hough Transform object created to perform hough transformation
		houghObject.insertPoints(img1);		//add the points in the accumulator array
		BufferedImage img2 = houghObject.HoughArrayImage();		// buffered image variable storing the accumulator array image created during Hough transform
		// Write the hough accumulator image to a particular location
		ImageIO.write(img2, "png", new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\HoughArrayImage.png"));
		 Vector<HoughLine> lines = houghObject.getLines(68); // vector lines are defined to draw lines on the edges of the image
		 
	        // draw the lines back onto the image 
	        for (int j = 0; j < lines.size(); j++) { 
	            HoughLine line = lines.elementAt(j); 
	            line.draw(img1, Color.RED.getRGB()); 
	        } 
	    ImageIO.write(img1, "png", new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\LineImage.png"));
	    houghObject.getParellelLines(houghObject.peakarray);	// function called to obtain the coordinates of the corners of the parellelograms
		

	}
	
	final int thetaMax = 180;		//initialize the maximum value of the angle
	double steptheta = Math.PI/thetaMax;		// define the step size of the angle axis in the accumulator
	double steptheta2 = Math.PI/(5*thetaMax);
	int numberOfPoints ;		// the count of number of points which have been added yet
	
	int width;		//width of the image
	int height;		//height of the image
	final int neighbour = 4;	//area where the maximum value is searched
	int[][] array ;		//accumulator array
	
	int[][] peakarray;
	
	float Xcenter;		//center of the image-x axis
	float Ycenter;		//center of the image - y axis
	int arrayHeight;		//height of the accumulator array
	int arrayWidth;		//width of the accumulator array
	double[] sin;		// the array for sin values
	double[] cos;		//the array for cos values
	public Hough(int width, int height) {		// constructor to create an object
		this.width = width;
		this.height = height;
		initializeValues();			// initializing all the values of the object
	}
	public void initializeValues() {
		 arrayHeight = 2*((int) (Math.sqrt(2) * Math.max(height, width)) / 2);		// the height of the array is calculated
		 array = new int[thetaMax][arrayHeight];	// array defined having angle as the columns and value calculated from the equation as rows
	
		 peakarray = new int[thetaMax][arrayHeight];	// array defined to store where the accumulator peaks
		
		 Xcenter = width/2;		// calculate the center x pixel
		 Ycenter = height/2;	// calculate the center y pixel
		 numberOfPoints = 0;	// initially no points are added so number of points 0
		
		 sin = new double[thetaMax];	// size of the matrix is same as the maximum value where the angle will go
		 cos = new double[thetaMax];	//size of the matrix is same as the maximum value where the angle will go
		 for(int i = 0 ;i<thetaMax ; i++) {		// traverse each angle degree and calculate the corresponding sin and cos values. This helps in saving time
			 double theta = i*steptheta;
			 sin[i] = Math.sin(theta);
			 cos[i] = Math.cos(theta);
		 }
		 
		 
	}
	/* a function is defined to insert points into the accumulator with a buffered image variable as the parameter */
	public void insertPoints(BufferedImage img1) {
		for(int i = 0; i<img1.getWidth() ; i++) {
			// Traversing left to right top to bottom each pixel
			for(int j = 0 ; j<img1.getHeight() ; j++) { 
				if(((img1.getRGB(i, j)>>16) & 0xff) == 0) {	
					//check if the point is edge point or not
					for(int x = 0 ; x<thetaMax ; x++) {		//when the pixel is edge point we calculate the value of p
						int p = (int)(((i-Xcenter)*cos[x])+((j-Ycenter)*sin[x]));
						p+=(arrayHeight/2);		//There might be cases when the value of p calculated is negative, so all p are incremented to make most solution positive and in range
						if(p<0 || p>=arrayHeight) continue;		//error checking for boundary cases
						array[x][p]++;		//incrementing the accumulator array to get the next value
						
					}
					numberOfPoints++;		//Since one point was inserted in the accumulator array, we increase the count of number of points inserted
				}			
			}
		}	
	}
	
	/* define a function to etract the maximum value from the accumulator array*/
	public int highestValue() {
		int maximum = 0;
		for(int t = 0;t<thetaMax ; t++) {
			for(int p = 0 ; p<arrayHeight ; p++) {
				
				if(array[t][p] >maximum) 
					maximum = array[t][p] ;
			}
		}
		return maximum;
	}
	
	/* define a function to write an image for the accumulator array. This gives a better understanding of the accumulator array*/  
	public  BufferedImage HoughArrayImage() {
		int maximum = highestValue();	//calculated from the function defined earlier
		BufferedImage houghImage = new BufferedImage(thetaMax, arrayHeight, BufferedImage.TYPE_INT_ARGB);
		for(int t = 0 ; t<thetaMax ;t++) {			// traversing each pixel
			for(int p = 0 ; p<arrayHeight ; p++ ) {
				double val = 255*((double)array[t][p])/maximum;		// convert the image pixel value in the range [0,255]
				int val1 = 255-(int)val;		// conversion applied to get a better look at the image
				int c = new Color(val1,val1,val1).getRGB();		// color variable calculated and color component set
				houghImage.setRGB(t, p,  c);
			}
		}
		return houghImage;
	}
	
	/* Function is defined to obtain the lines so that t can be seen what edges are detected using straight lines Hough transform.
	 * The return type is vector because lines need to be obtained. There might be gaps in the edges of our non maxima binary image, 
	 * but the use of vector makes sure that the gap is filled by the vector line.*/
	 public Vector<HoughLine> getLines(int threshold) { 
	        Vector<HoughLine> lines = new Vector<HoughLine>(20); 	// Initialise the vector of lines that we'll return    
	        if (numberOfPoints == 0) return lines; 		// Check if any point was inserted into the accumulator 
	        for (int t = 0; t < thetaMax; t++) { 	// Search for local peaks in the accumulator array 
	            loop: 
	            for (int r = neighbour; r < arrayHeight - neighbour; r++) { 
	                if (array[t][r] > threshold) { 
	                    int peak = array[t][r];                     
	                    for (int dx = -neighbour; dx <= neighbour; dx++) { // Find the local maxima
	                        for (int dy = -neighbour; dy <= neighbour; dy++) { 		// neighbour variable is used to traverse through the neighbouring pixels
	                            int dt = t + dx; 
	                            int dr = r + dy; 
	                            if (dt < 0) dt = dt + thetaMax; // to keep the values in the range
	                            else if (dt >= thetaMax) dt = dt - thetaMax; 		// to keep the values in range
	                            if (array[dt][dr] > peak) { 		   
	                                continue loop; 	// when a bigger point is found we will skip the current value.
	                            } 
	                        } 
	                    } 
	                    double theta = t * steptheta; 
	                    peakarray[t][r] = 1;  		// set the corresponding value of cellas 1 in the peakarray to perform further operations more smoothly
	                    lines.add(new HoughLine(theta, r)); // add function is called from HoughLine class  
	 
	                } 
	            } 
	        } 
	 
	        return lines; 
	    } 
	/* function defined to get the corners of the parellelograms detected in the image with the peakarray as the parameter. */
	 public void getParellelLines( int peakarray[][]) {
		 try {

		 BufferedImage im = ImageIO.read(new File("C:\\shrey college\\Semester 1\\Computer Vision\\Project 1\\nmsthreshold.png"));
		 line2 object = new line2();		// an object of line2 class is created.the line2 class is created to get the intersection points of the parellel ines and the corners
		 line2 parellelLine1 = new line2(0,0);		// an object of line2 class is defined with both angle and value of p initialised as 0.
		 line2 parellelLine2 = new line2(0,0);
		 line2 parellelLine3 = new line2(0,0);
		 line2 parellelLine4 = new line2(0,0);
		
		 for(int i = 0 ; i <thetaMax ; i++) {		// traverse each each cell of the peakarray
			 for(int j = 0 ; j <arrayHeight ; j++) {
				 if(peakarray[i][j] ==1 ) {			// this means that there exist edge pixel which satisfies the angle i and value j. Also this value is a local maxima
					  parellelLine1 = new line2(i,j);	// the object parellelLine1 is defined with the angle and value of p to get one parellel line
					 
				 
				 for(int k = j+1 ; k <arrayHeight ; k++) {		// traverse the matrix to get another line having the same angle i
					 if(peakarray[i][k] == 1 ) {				// found another line having the same angle which is local maxima
						 parellelLine2 = new line2(i,k);		// parellelLine1 and parellelLine2 is a pair of parellel line.
						
						 for(int l = i+1; l <thetaMax ; l++) {		// Traverse the angles after the selected angle for first pair of parellel lines to get another pair of parellel lines
							 for(int m = 0;m<arrayHeight ; m++) {	
								 if(peakarray[l][m] == 1) {		// found a line which is a local maxima but different angle than the first two parellel lines
									 parellelLine3 = new line2(l,m);
									
								 
								 for(int n = m+1; n<arrayHeight ; n++) {	// traverse to find another line which is local maxima having the same angle as 3rd parellel line
									 if(peakarray[l][n]==1) {		// 4th parellel line found which gives us a set of two parellel lines having speceficall two unique angles.
										  parellelLine4 = new line2(l,n);
								
										 object.cornerPoints(im, parellelLine1, parellelLine2, parellelLine3, parellelLine4);	// the object calls a function from line2 class to calculate the 4 corner points of the parellelogram
									 }
								 }}
							 }
						 }
					 }
				 }
			 }
			 }
		 }
		 }catch(IOException e) {
			 e.printStackTrace();		// to check for errors from ImageIO
		 }
		 
	 }

}

