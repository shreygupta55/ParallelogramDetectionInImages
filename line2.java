import java.awt.image.BufferedImage;

public class line2 {
	
	/* this class is defined to calculate the corner points of the parellelograms detected in the image. The logic is such that 
	 * the two pairs of parellel lines are computed here to get 4 intersection points. Next it is checked as to how many edge points
	 * lie on each side of the candidate parellelogram. If the density of edge points is high enough, the candidate parellelogram can be 
	 * considered as one of the parellelogram which we want to detect in the image.  */
	
	final int thetamax = 180;	// define the maximum angle as 180 degrees
	protected double theta;		// variable defined for angle of the line
	protected double r;			// variable defined for the calculation of the value
		
	public line2() {		// constructor of the line
		
	}
	public line2(double theta, double r) {		// constructor with values to define
		this.theta = theta*(Math.PI/thetamax);
		this.r = r;
		
	}
	/* function to calculate the corner points of the parellelogram in the image */
	public void cornerPoints(BufferedImage img, line2 p1, line2 p2, line2 p3, line2 p4) {
		int arrayHeight = 2*((int) (Math.sqrt(2) * Math.max(img.getHeight(), img.getWidth())) / 2);		// height of the accumulator array
		int Xcenter = img.getWidth()/2;		// center of the image in x axis
		int Ycenter = img.getHeight()/2;	// center of the image in y axis
		int x1=0,x2=0,x3=0,x4=0,y1=0,y2=0,y3=0,y4=0;	// initialize 8 variable to store the coordinates
		int numPoints = 0;		// initialize a variable to store the number 
		for(int i = 0 ; i < img.getWidth() ; i++) {
			for(int j = 0 ; j < img.getHeight() ; j++) {
				/*Calculation of intersection point of 1st line and 3rd line */
				if((p1.r ==((int)((i-Xcenter)*Math.cos(p1.theta) + (j-Ycenter)*Math.sin(p1.theta)))+(arrayHeight/2)) && (p3.r ==((int)(i-Xcenter)*Math.cos(p3.theta) + (j-Ycenter)*Math.sin(p3.theta))+(arrayHeight/2))) {
					x1 = i;
					y1 = j;
				}
				/*Calculation of intersection point of 1st line and 4th line */
				if((p1.r ==((int)((i-Xcenter)*Math.cos(p1.theta) + (j-Ycenter)*Math.sin(p1.theta)))+(arrayHeight/2)) && (p4.r ==((int)(i-Xcenter)*Math.cos(p4.theta) + (j-Ycenter)*Math.sin(p4.theta))+(arrayHeight/2))) {
					x2 = i;
					y2 = j;
				}
				/*Calculation of intersection point of 2nd line and 3rd line */
				if((p2.r ==((int)((i-Xcenter)*Math.cos(p2.theta) + (j-Ycenter)*Math.sin(p2.theta)))+(arrayHeight/2)) && (p3.r ==((int)(i-Xcenter)*Math.cos(p3.theta) + (j-Ycenter)*Math.sin(p3.theta))+(arrayHeight/2))) {
					x3 = i;
					y3 = j;
				}
				/*Calculation of intersection point of 2nd line and 4th line */
				if((p2.r ==((int)((i-Xcenter)*Math.cos(p2.theta) + (j-Ycenter)*Math.sin(p2.theta)))+(arrayHeight/2)) && (p4.r ==((int)(i-Xcenter)*Math.cos(p4.theta) + (j-Ycenter)*Math.sin(p4.theta))+(arrayHeight/2))) {
					x4 = i;
					y4 = j;
				}
				if(((img.getRGB(i, j)>>16) & 0xff) == 255) {	// if the edge point lie on one of the lines we will increment the count of numPoints
					if((p1.r ==((int)((i-Xcenter)*Math.cos(p1.theta) + (j-Ycenter)*Math.sin(p1.theta)))+(arrayHeight/2)) || (p2.r ==((int)((i-Xcenter)*Math.cos(p2.theta) + (j-Ycenter)*Math.sin(p2.theta)))+(arrayHeight/2)) ||
							(p3.r ==((int)((i-Xcenter)*Math.cos(p3.theta) + (j-Ycenter)*Math.sin(p3.theta)))+(arrayHeight/2)) || (p4.r ==((int)((i-Xcenter)*Math.cos(p4.theta) + (j-Ycenter)*Math.sin(p4.theta)))+(arrayHeight/2))) {
						numPoints = numPoints +1;
					}
				}
			}
		}
		//if(numPoints>00 && x1!=0 && y1!=0 && x2!=0 && y2!=0 && x3!=0 && y3!=0 && x4!=0 && y4!=0 ) {		// checking the density of edge points and eliminating the points calculated by noise in the image
		System.out.println(x1+" , " + y1+" | "+x2+" , " + y2+" | "+x3+" , " + y3+" | "+x4+" , " + y4 );
		//System.out.println(numPoints);
		}
	}
	//}
