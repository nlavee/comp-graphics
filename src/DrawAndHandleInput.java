/*

  Example program for CS325

  Author: Michael Eckmann

  Updated to work with JOGL 2.3.2 (from October 2015 build)

  This class DrawAndHandleInput which in a GLEventListener as well as a KeyListener and MouseListener
  displays a grid of "big" pixels for student use to add code to draw Bresenham Lines, Circles, and
  do antialiasing.

 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Dimension2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
//import com.jogamp.nativewindow.util.*;

public class DrawAndHandleInput implements GLEventListener, KeyListener, MouseListener
{

	/* this object will give us access to the gl functions */
	private GL2            gl;
	/* this object will give us access to the glu functions */
	private GLU            glu;

	/* define the world coordinate limits */
	public static  final int MIN_X =0;
	public static  final int MIN_Y =0;
	public static  final int MAX_X =250;
	public static  final int MAX_Y =200;

	/* define the intensity limits */
	public static final double MAX_INTENSITY = 1.0;
	public static final double MIN_INTENSITY = 0.0;

	// height and width of the bigpixel area in world coordinate measurements
	public static final int BIGAREA_HEIGHT = 200;
	public static final int BIGAREA_WIDTH = 200;

	// number of rows and columns of big pixels to appear in the grid
	// eventually these will be set by user input or via a command line
	// parameter.  That is functionality you need to add for program 02.
	public static int BIGPIXEL_ROWS = 1;
	public static int BIGPIXEL_COLS = 1;

	// globals that hold the coordinates of the big pixel that is to be
	// "turned on"
	// you may want to change the way this is done.
	public double bigpixelyFirst=0;
	public double bigpixelxFirst=0;
	public double bigpixelySecond=0;
	public double bigpixelxSecond=0;


	public final int COLORRED = 1;
	public int newcolor = 2;
	private GLCanvas canvas;

	// globals on what mode we're in
	public int activeMode = 2;

	// globals for color of the chosen big pixel
	private float r = 0;
	private float g = 0;
	private float b = 0;

	// globals for offset
	private double offSet = 0.0001;

	public DrawAndHandleInput(GLCanvas c, int count, float red, float green, float blue)
	{
		this.canvas = c;
		this.BIGPIXEL_ROWS = count;
		this.BIGPIXEL_COLS = count;
		this.r = red;
		this.g = green;
		this.b = blue;
	}
	// ====================================================================================
	//
	// Start of the methods in GLEventListener
	//

	/**
	   =============================================================
	   This method is called by the drawable to do initialization. 
  	   =============================================================

  	   @param drawable The GLCanvas that will be drawn to

	 */
	public void init(GLAutoDrawable drawable)
	{
		this.gl = drawable.getGL().getGL2();
		this.glu = new GLU(); // from demo for new version

		/* Set the clear color to black */
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		/* sets up the projection matrix from world to window coordinates */
		gl.glMatrixMode(GL2.GL_PROJECTION);

		gl.glLoadIdentity();
		/* show the whole world within the window */
		glu.gluOrtho2D(MIN_X, MAX_X, MIN_Y, MAX_Y);

		/* sets up the modelview matrix */
		/* ignore this for now
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, 0.0f);
		 */

		// wraps the GL to provide error checking and so
		// it will throw a GLException at the point of failure
		drawable.setGL( new DebugGL2(drawable.getGL().getGL2() ));

	} // end init

	/**
	   =============================================================
	   This method is called when the screen needs to be drawn.
	   =============================================================

	   @param drawable The GLCanvas that will be drawn to

	 */
	public void display(GLAutoDrawable drawable)
	{
		float r1, g1, b1; /* red, green and blue values */

		/* clear the color buffer */
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

		r1 = 0.5f;
		g1 = 0.5f;
		b1 = 0.0f;



		/* sets up the current color for drawing 
	       for polygons, it's the "fill color" */
		gl.glColor3f(1,1,1);

		// draw the "big pixel area" 
		gl.glBegin(GL2.GL_POLYGON);
		// These are the vertices of the polygon using world coordinates 
		gl.glVertex2i( 0, 0);  // vertex 1 
		gl.glVertex2i( BIGAREA_WIDTH, 0);  
		gl.glVertex2i( BIGAREA_WIDTH, BIGAREA_HEIGHT);
		gl.glVertex2i( 0, BIGAREA_HEIGHT);  
		gl.glEnd();

		// draw the "small pixel area" to the right of the "big pixel area" 
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2d( BIGAREA_WIDTH, 
				BIGAREA_HEIGHT - 5.0*(MAX_Y-MIN_Y)/8);
		gl.glVertex2d( BIGAREA_WIDTH + 0.2*(MAX_X-MIN_X), 
				BIGAREA_HEIGHT - 5.0*(MAX_Y-MIN_Y)/8);
		gl.glVertex2d( BIGAREA_WIDTH + 0.2*(MAX_X-MIN_X), 
				BIGAREA_HEIGHT - 3.0*(MAX_Y-MIN_Y)/8);
		gl.glVertex2d( BIGAREA_WIDTH, 
				BIGAREA_HEIGHT - 3.0*(MAX_Y-MIN_Y)/8);
		gl.glEnd();

		// change color to black for grid lines
		gl.glColor3f(0,0,0);

		// draw the vertical lines for the grid
		for (int col=0; col <= BIGPIXEL_COLS; col++)
		{

			gl.glBegin(GL2.GL_LINES);
			gl.glVertex2d((double)col*BIGAREA_WIDTH/BIGPIXEL_COLS, 0);
			gl.glVertex2d((double)col*BIGAREA_WIDTH/BIGPIXEL_COLS, BIGAREA_HEIGHT);
			gl.glEnd();

		}

		// draw the horizontal lines for the grid
		for (int row=0; row <= BIGPIXEL_ROWS; row++)
		{
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex2d(0, (double)row*BIGAREA_HEIGHT/BIGPIXEL_ROWS);
			gl.glVertex2d(BIGAREA_WIDTH,(double)row*BIGAREA_HEIGHT/BIGPIXEL_ROWS);
			gl.glEnd();
		}

		// uses the global double variables that were set when user
		// clicked as the coordinates of the bigpixel to be drawn.
		drawBigPixel((int)bigpixelxFirst,(int)bigpixelyFirst, MAX_INTENSITY);

		drawBigPixel((int)bigpixelxSecond,(int)bigpixelySecond, MAX_INTENSITY);

		// draw based on active mode
		if(activeMode == 1) 
		{
			// circular mode
			drawCircularMode(bigpixelxFirst, bigpixelyFirst, bigpixelxSecond, bigpixelySecond);
		}
		else if(activeMode == 2)
		{
			// regular line mode
			//				drawRegularLine(bigpixelxFirst, bigpixelyFirst, bigpixelxSecond, bigpixelySecond);
			drawLine(bigpixelxFirst, bigpixelyFirst, bigpixelxSecond, bigpixelySecond, false);
		}
		else if(activeMode == 3)
		{
			// antialiased mode
			drawLine(bigpixelxFirst, bigpixelyFirst, bigpixelxSecond, bigpixelySecond, true);
		}
		else if(activeMode == 4)
		{
			drawPixelArt(1);
		}
		else if(activeMode == 5)
		{
			drawPixelArt(2);
		}
		else if(activeMode == 6)
		{
			drawPixelArt(3);
		}
		else
		{
			//System.out.println("No mode of drawing specified.");
		}

		/* force any buffered calls to actually be executed */
		gl.glFlush();


		// this will swap the buffers (when double buffering)
		// canvas.swapBuffers() should do the same thing
		drawable.swapBuffers();

	} // end display

	/*
	 * 		Method to draw line, both aliased and antialiased.
	 * 		
	 * 		The core calculations are the same, we just did a bit more calculation for dUpper and dLower if 
	 * 		we want to draw antialiased line. 
	 * 
	 * 		The two points passed as parameters are the two ending points
	 * 		of the line.
	 */
	private void drawLine(double x0,
			double y0, double xEnd,
			double yEnd, boolean antialiased) {

		System.out.println("Two points: (" + x0 + ", " + y0 + ") - (" + xEnd + ", " + yEnd + ")");

		/*
		 * These are values for actual change in x and y
		 * So we would know whether we should increment or decrement as we increase in step.
		 */
		int delX = (int) (Math.floor(xEnd) - Math.floor(x0));
		int delY = (int) (Math.floor(yEnd) - Math.floor(y0));

		/*
		 * Calculate the absolute value for calculation of p
		 */
		int dx = (int) Math.abs(delX);
		int dy = (int) Math.abs(delY);

		System.out.println("dx: " + dx);
		System.out.println("dy: " + dy);

		/*
		 *  Case where 0.0 < |m| < 1.0.
		 *  Essentially, cases where we go along x-axis and decide whether to turn on 
		 *  the pixel to the right or the pixel to the right and below/above.
		 */
		if(dy < dx)
		{
			/* 
			 * Calculate the necessary components
			 */
			int p = 2 * dy - dx;
			int twoDy = 2 * dy;
			int twoDyMinusDx = 2 * (dy - dx);
			int x,y;

			// determine which end point to use as start position
			if( x0 > xEnd )
			{
				x = (int) xEnd;
				y = (int) yEnd;
				xEnd = x0;
				yEnd = y0;
				delY = -1 * delY;
			}
			else
			{
				x = (int) x0;
				y = (int) y0;
			}

			/*
			 * if antialiased line, we will only allow black color line for now.
			 * Else, we draw with color.
			 */
			if(antialiased)
			{
				drawBigPixel(x,y, 0,0,0);
				drawBigPixel( (int) Math.floor(xEnd), (int) Math.floor(yEnd), 0, 0, 0);
			}
			else
			{
				drawBigPixel(x,y, MAX_INTENSITY);
				drawBigPixel( (int) Math.floor(xEnd), (int) Math.floor(yEnd), MAX_INTENSITY);
			}

			while( x < Math.floor(xEnd)-1 )
			{
				/*
				 * We know that p = dx * (dLower - dUpper) & dLower + dUpper = 1
				 * Therefore, 
				 * 			p / dx = dLower - dUpper
				 * 			(p / dx) + 1 = dLower - dUpper + dLower + dUpper
				 * 			(p / dx) + 1 = 2 * dLower
				 * 			( (p / dx) + 1 ) /2 = dLower
				 *  ==> dLower = ( (p / dx) + 1 ) /2
				 *  ==> dUpper = 1 - dLower
				 *  
				 *  We only calculate this if we want to draw antialiased line.
				 *  Else, we don't need to waste our computation here.
				 */
				double dLower = -1;
				double dUpper = -1;
				if(antialiased)
				{
					dLower = ( (double) p / dx + 1) / 2;
					dUpper = 1 - dLower;
				}

				System.out.println(dLower + " - " + dUpper);

				x++;

				boolean move = false;
				if( p < 0 ) 
				{
					p += twoDy;
				}
				else
				{
					/**
					 * Cases of negative and positive slope.
					 * For positive slope, we go increment.
					 * For negative slope, we go decrement.
					 */
					if( delY > 0 ) y++;
					else y--;
					move = true;
					p += twoDyMinusDx;
				}

				/*
				 * If the mode is antialiased, we do different operations.
				 * Else, we just going to draw the point.
				 */
				if(antialiased)
				{
					/*
					 * For positive slope
					 */
					if( delY > 0 )
					{
						/*
						 * the line passes through unequal amount for two pixels.
						 * Whichever pixel has higher percentage (dUpper vs. dLower),
						 * we give it more.
						 */
						if(move && dLower != dUpper)
						{
							System.out.println("(x,y-1) : " + x + ", " + (y-1) + " - " + dUpper);
							System.out.println("(x,y) : " + x + ", " + y + " - " + dLower);
							System.out.println();
							drawBigPixel(x,y-1, dUpper, dUpper, dUpper); 
							drawBigPixel(x,y, dLower, dLower, dLower);
						}
						/*
						 * If the line passes through the center, we are just 
						 * going to draw the chosen point
						 */
						else if(!move && dLower != dUpper)
						{
							System.out.println("(x,y+1) : " + x + ", " + (y+1) + " - " + dUpper);
							System.out.println("(x,y) : " + x + ", " + y + " - " + dLower);
							System.out.println();
							drawBigPixel(x,y+1, dUpper, dUpper, dUpper); 
							drawBigPixel(x,y, dLower, dLower, dLower);
						}
						else
						{
							System.out.println("Center (x,y) : " + x + ", " + y);
							System.out.println();
							drawBigPixel(x,y,0d,0d,0d);
						}
					}
					/*
					 * For negative slope, we consider between pixel to the right 
					 * or pixel right & below.
					 * Instead of y + 1 as above, we draw y - 1 
					 */
					else 
					{
						if(move && dUpper != dLower)
						{
							drawBigPixel(x,y+1, dUpper, dUpper, dUpper); 
							drawBigPixel(x,y, dLower, dLower, dLower);
						}
						else if(!move && dUpper != dLower)
						{
							drawBigPixel(x,y-1, dUpper, dUpper, dUpper); 
							drawBigPixel(x,y, dLower, dLower, dLower);
						}
						else
						{
							drawBigPixel(x,y, 0d, 0d, 0d);
						}
					}
				}
				/*
				 * Drawing aliased line
				 */
				else
				{
					drawBigPixel(x,y, MAX_INTENSITY);
				}
			}
		}
		/* Otherwise, |m| > 1.0
		 * We go along y-axis and choose which pixel of x to turn on.
		 * This is essentially same as the above,
		 * just switching y and x.
		 */
		else
		{
			int p = 2 * dx - dy;
			int twoDx = 2 * dx;
			int twoDxMinusDy = 2 * (dx - dy);
			int x,y;

			if( y0 > yEnd )
			{
				x = (int) xEnd;
				y = (int) yEnd;
				yEnd = y0;
				xEnd = x0;
				delX = -1 * delX;
			}
			else
			{
				x = (int) x0;
				y = (int) y0;
			}

			/*
			 * if antialiased line, we will only allow black color line for now.
			 * Else, we draw with color.
			 */
			if(antialiased)
			{
				drawBigPixel(x,y, 0,0,0);
				drawBigPixel( (int) Math.floor(xEnd), (int) Math.floor(yEnd), 0,0,0);
			}
			else
			{
				drawBigPixel(x,y, MAX_INTENSITY);
				drawBigPixel( (int) Math.floor(xEnd), (int) Math.floor(yEnd), MAX_INTENSITY);
			}

			while( y < Math.floor(yEnd)-1 )
			{
				/*
				 * Similar to above, only calculate these two values if we're drawing antialiased line.
				 * Else, we don't need to waste computation time here.
				 */
				double dLower = -1;
				double dUpper = -1;
				if(antialiased)
				{
					dLower = ( (double) p / dy + 1) / 2;
					dUpper = 1 - dLower;
				}

				y++;
				boolean move = false;
				if( p < 0 ) p += twoDx;
				else
				{
					if( delX > 0 ) 
					{
						x++;
					}
					else x--;
					move = true;
					p += twoDxMinusDy;
				}

				/*
				 * Similar to above, drawing antialiased line has some special computation
				 */
				if(antialiased)
				{
					if( delX > 0 ) 
					{
						/*
						 * We consider between the pixel up or pixel up & right
						 */
						if(move && dUpper != dLower)
						{
							drawBigPixel(x+1,y, dUpper, dUpper, dUpper);
							drawBigPixel(x,y, dLower, dLower, dLower);
						}
						else if(!move && dUpper != dLower)
						{
							drawBigPixel(x-1,y, dUpper, dUpper, dUpper);
							drawBigPixel(x,y, dLower, dLower, dLower);
						}
						else
						{
							drawBigPixel(x,y, 0d, 0d , 0d);
						}
					}
					else 
					{
						/*
						 * We consider between the pixel up or pixel up & left
						 */
						if(move && dUpper != dLower)
						{
							drawBigPixel(x-1,y, dUpper, dUpper, dUpper);
							drawBigPixel(x,y, dLower, dLower, dLower);
						}
						else if(!move && dUpper != dLower)
						{
							drawBigPixel(x+1,y, dUpper, dUpper, dUpper);
							drawBigPixel(x,y, dLower, dLower, dLower);
						}
						else
						{
							drawBigPixel(x,y, 0d, 0d , 0d);
						}
					}
					//				System.out.println(dLower + " - " + dUpper);
				}
				/*
				 * Drawing aliased line
				 */
				else
				{
					drawBigPixel(x,y, MAX_INTENSITY);
				}
			}

		}

	}

	/*
	 * Method to draw circle based on two points.
	 * Takes in first point as the centre of the circle and draw
	 * the circle such that the distance between the two points is the
	 * radius.
	 */
	private void drawCircularMode(double x0,
			double y0, double xEnd,
			double yEnd) {

		/*
		 * Calculate radius
		 */
		int radius = calculateDistance(x0,y0,xEnd,yEnd);


		/* 
		 * draw circle at origin
		 * only need to draw a quarter of the circle
		 * The eight points are: 
		 * (-x,-y), (-x,y), (x,-y), (x,y), (y,x), (y,-x), (-y,x), (-y,-x)
		 */
		int pK = (int) (1 - radius);

		int x = -1;
		int y = radius;

		while(x <= y )		
		{
			x = x + 1;
			if( pK < 0 )
			{
				pK = pK + 2 * (x) + 1;
			}
			else
			{
				pK = pK + 2 * (x) +1 - 2 * (y + 1);
				y = y - 1;
			}

			// draw 8 points
			int negX = -1 * x;
			int negY = -1 * y;

			/*
			 * Bound for width: 0 --> BIGPIXEL_COLS
			 * Bound for height: 0 --> BIGPIXEL_ROWS
			 */
			int widthLimit = BIGPIXEL_COLS;
			int heightLimit = BIGPIXEL_ROWS;

			/*
			 * Translate back to point we need to draw by adding with the coords of (x0, y0).
			 * Check to see it's not out of the bounded area that we need.
			 */
			if( Math.abs(x + x0) < widthLimit && x + x0 > 0  && y + y0 > 0 && Math.abs(y + y0) < heightLimit) 
			{
				drawBigPixel( (int) (x + x0), (int) (y + y0), MAX_INTENSITY );
			}
			if( Math.abs(negX + x0) < widthLimit && negX + x0 > 0  && negY + y0 > 0 && Math.abs(negY + y0) < heightLimit) 
			{
				drawBigPixel( (int) (negX + x0), (int) (negY + y0), MAX_INTENSITY );
			}
			if( Math.abs(negX + x0) < widthLimit && negX + x0 > 0  && y + y0 > 0 && Math.abs(y + y0) < heightLimit) 
			{
				drawBigPixel( (int) (negX + x0), (int) (y + y0), MAX_INTENSITY );
			}
			if( Math.abs(x + x0) < widthLimit && x + x0 > 0  && negY + y0 > 0 && Math.abs(negY + y0) < heightLimit) 
			{
				drawBigPixel( (int) (x + x0), (int) (negY + y0), MAX_INTENSITY );
			}
			if( Math.abs(y + x0) < widthLimit && y + x0 > 0  && x + y0 > 0 && Math.abs(x + y0) < heightLimit) 
			{
				drawBigPixel( (int) (y + x0), (int) (x + y0), MAX_INTENSITY );
			}
			if( Math.abs(y + x0) < widthLimit && y + x0 > 0  && negX + y0 > 0 && Math.abs(negX + y0) < heightLimit) 
			{
				drawBigPixel( (int) (y + x0), (int) (negX + y0), MAX_INTENSITY );
			}
			if( Math.abs(negY + x0) < widthLimit && negY + x0 > 0  && x + y0 > 0 && Math.abs(x + y0) < heightLimit) 
			{
				drawBigPixel( (int) (negY + x0), (int) (x + y0), MAX_INTENSITY );
			}
			if( Math.abs(negY + x0) < widthLimit && negY + x0 > 0  && negX + y0 > 0 && Math.abs(negX + y0) < heightLimit) 
			{
				drawBigPixel( (int) (negY + x0), (int) (negX + y0), MAX_INTENSITY );
			}
		}

	}

	/*
	 * Method to calculate the distance between two points 
	 * using Pythagoras' theorem.
	 */
	private int calculateDistance(double x0, double y0, double xEnd,
			double yEnd) {
		return (int) Math.sqrt( Math.pow( Math.floor(yEnd) - Math.floor(y0), 2 ) 
				+ Math.pow( Math.floor(xEnd) - Math.floor(x0), 2 ) );
	}

	/* 

	 method name: drawPixelArt

	    takes in an int which represent an option and displays the "big pixel" (polygon)
	    arts from the precalculated pixels read from external text file
	    Note: Due to IO operation, this can take a while to run. Also, it looks best with smaller
	    row and column number ( < 200 )

	 parameters:
	    pixelOption: 1 for Pokemon related art, 2 for Mario related art and 3 for Link (Zelda) related art

	 */
	private void drawPixelArt(int pixelOption)
	{
		/*
		 * Specify what file we want
		 */
		String file = "";
		if(pixelOption == 1) file = "pikachu";
		else if(pixelOption == 2) file = "mario";
		else file = "link";


		try(BufferedReader br = new BufferedReader(new FileReader( "txt/" + file + ".txt" ))) {
			/*
			 * Read through save file to get pixel information
			 */
			String line = br.readLine();

			// take out width and height
			String[] wh = line.split(", ");
			int width = Integer.parseInt(wh[0]);
			int height = Integer.parseInt(wh[1]);
			//			System.out.println("WH = " + width + ", " + height);

			// get coords & rgb
			line = br.readLine();
			while (line != null) {

				/*
				 * Split the string into an array of values.
				 */
				String[] elements = line.split(", ");

				/*
				 * I do not bother to do try catch here since
				 * I have gone through and made sure that the saved file
				 * only has numbers.
				 */
				double xOri = Double.parseDouble(elements[0]);
				double yOri = Double.parseDouble(elements[1]);
				double r = Integer.parseInt(elements[2]);
				double g = Integer.parseInt(elements[3]);
				double b = Integer.parseInt(elements[4]);

				/*
				 * Translate X and Y to the screen that we have.
				 */
				int translatedX = (int) ((xOri / width) * BIGPIXEL_COLS);
				int translatedY = (int) ((yOri / height) * BIGPIXEL_ROWS);

				drawBigPixel(translatedX, translatedY, r/255,g/255,b/255);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println(e);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	/* 

	 method name: drawBigPixel

	    takes in "big pixel" coordinates and displays the "big pixel" (polygon)
	    associated with those coordinates.
	    Note: the "big pixel" area is situated so that
	       0, 0 is the highest-leftmost big pixel and
	       BIXPIXEL_COLS - 1, BIGPIXELROWS - 1 is the lowest-rightmost big pixel

	 parameters:
	    x - the x coordinate of the big pixel
	    y - the y coordinate of the big pixel
	    i - the value to times rgb value by

	 */
	public void drawBigPixel(int x, int y, double i)
	{
		// because the y screen coordinates increase as we go down 
		// and the y world coordinates increase as we go up
		// we need to compute flip_y which will be the y coordinate
		// of the big pixel if the big pixel coordinates' y values
		// increased as we go up
		int flip_y = Math.abs((BIGPIXEL_ROWS-1) - y);
		gl.glColor3d(r * i, g * i, b * i);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2d((double)x*BIGAREA_HEIGHT/BIGPIXEL_ROWS, 
				(double)flip_y*BIGAREA_WIDTH/BIGPIXEL_COLS);
		gl.glVertex2d((double)(x+1)*BIGAREA_HEIGHT/BIGPIXEL_ROWS, 
				(double)flip_y*BIGAREA_WIDTH/BIGPIXEL_COLS);
		gl.glVertex2d((double)(x+1)*BIGAREA_HEIGHT/BIGPIXEL_ROWS, 
				(double)(flip_y+1)*BIGAREA_WIDTH/BIGPIXEL_COLS);
		gl.glVertex2d((double)x*BIGAREA_HEIGHT/BIGPIXEL_ROWS, 
				(double)(flip_y+1)*BIGAREA_WIDTH/BIGPIXEL_COLS);
		gl.glEnd();

	}

	/* 

	 method name: drawBigPixel

	    takes in "big pixel" coordinates and displays the "big pixel" (polygon)
	    associated with those coordinates.
	    Note: the "big pixel" area is situated so that
	       0, 0 is the highest-leftmost big pixel and
	       BIXPIXEL_COLS - 1, BIGPIXELROWS - 1 is the lowest-rightmost big pixel

	 parameters:
	    x - the x coordinate of the big pixel
	    y - the y coordinate of the big pixel
	    red - value for red in RGB
	    green - value for green in RGB
	    blue - value for blue in RGB

	 */
	public void drawBigPixel(int x, int y, double red, double green, double blue)
	{
		// because the y screen coordinates increase as we go down 
		// and the y world coordinates increase as we go up
		// we need to compute flip_y which will be the y coordinate
		// of the big pixel if the big pixel coordinates' y values
		// increased as we go up
		int flip_y = Math.abs((BIGPIXEL_ROWS-1) - y);
		//System.out.println("Color: " + red + " - " + green + " - " + blue);
		gl.glColor3d(red,green,blue);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2d((double)x*BIGAREA_HEIGHT/BIGPIXEL_ROWS, 
				(double)flip_y*BIGAREA_WIDTH/BIGPIXEL_COLS);
		gl.glVertex2d((double)(x+1)*BIGAREA_HEIGHT/BIGPIXEL_ROWS, 
				(double)flip_y*BIGAREA_WIDTH/BIGPIXEL_COLS);
		gl.glVertex2d((double)(x+1)*BIGAREA_HEIGHT/BIGPIXEL_ROWS, 
				(double)(flip_y+1)*BIGAREA_WIDTH/BIGPIXEL_COLS);
		gl.glVertex2d((double)x*BIGAREA_HEIGHT/BIGPIXEL_ROWS, 
				(double)(flip_y+1)*BIGAREA_WIDTH/BIGPIXEL_COLS);
		gl.glEnd();

	}
	/**
	   =============================================================
	   This method is called when the window is resized.
	   =============================================================

	   @param drawable The GLCanvas that will be drawn to

	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{  
		// System.out.println("In reshape");
	}

	/**
	   =============================================================
	   Called by the drawable when the display mode or the display 
	   device associated with the GLDrawable has changed.
	   =============================================================

	   @param drawable The GLCanvas that will be drawn to
	   @param modeChanged  not implemented
	   @param deviceChanged  not implemented

	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{
		// System.out.println("In displayChanged");
	}

	//
	// End of methods in GLEventListener
	//
	// ====================================================================================



	// ====================================================================================
	// Deal with the keyboard events
	// KeyListener require the following methods to be 
	// specified:
	//
	//  keyReleased
	//  keyPressed
	//  keyTyped

	public void keyReleased(KeyEvent ke) {
	}
	public void keyPressed(KeyEvent ke) {
	}
	public void keyTyped(KeyEvent ke) {
		char ch = ke.getKeyChar();
		switch (ch) {
		case 'q': 
			System.exit(0);
			break;
		case 'r': 
			newcolor = COLORRED;
			bigpixelxFirst = 0;
			bigpixelyFirst = 0;
			bigpixelxSecond = 0;
			bigpixelySecond = 0;
			activeMode = 0;
			break;
		case 'c':
			System.out.println("Entering circular mode.");
			activeMode = 1;
			break;
		case 'l':
			System.out.println("Entering regular line mode.");
			activeMode = 2;
			break;
		case 'a':
			System.out.println("Entering antialiased line mode.");
			activeMode = 3;
			break;
		case 'p':
			System.out.println("Drawing Pikachu pixels art.");
			activeMode = 4;
			break;
		case 'm':
			System.out.println("Drawing Mario pixels art.");
			activeMode = 5;
			break;
		case 'z':
			System.out.println("Drawing Link (Zelda) pixels art.");
			activeMode = 6;
			break;
		}
	} // end keyTyped


	//
	// End of dealing with Keyboard Events
	//
	// ====================================================================================



	// ====================================================================================
	//
	// Deal with the mouse events
	// MouseListener require the following methods to be 
	// specified:
	//
	//  mouseReleased
	//  mouseEntered
	//  mouseExited
	//  mouseClicked
	//  mousePressed

	public void mouseReleased(MouseEvent me) { }
	public void mouseEntered(MouseEvent me) { }
	public void mouseExited(MouseEvent me) { }
	public void mouseClicked(MouseEvent me) { 

		// to get the coordinates of the event 
		int x, y;
		// to store which button was clicked, left, right or middle
		int button;

		x = me.getX();
		y = me.getY();

		System.out.println("x = " + x + " y = " + y);
		button = me.getButton();

		// example code for how to check which button was clicked
		if (button == MouseEvent.BUTTON1)
		{
			System.out.println("LEFT click");
		}
		else
			if (button == MouseEvent.BUTTON2)
			{
				System.out.println("MIDDLE click");
			}
			else
				if (button == MouseEvent.BUTTON3)
				{
					System.out.println("RIGHT click");
				}

		/*
		   getButton() - returns an int which should be compared against
		      BUTTON1, BUTTON2, or BUTTON3
		   the left button is BUTTON1
		   the middle button is BUTTON2
		   the right button is BUTTON3

		   these are the correct designations for linux,
		   they should also be correct for windows, but
		   I recall some kind of difference for mac os.
		   try it and see

		 */

		// get the current size of the canvas
		Dimension2D d = canvas.getSize();

		//System.out.println("height of canvas = " + d.height + " and width of canvas = " + d.width);
		//System.out.println("x = " + x + " and y = " + y);

		// using the x,y screen coordinates of the point that was clicked and
		// using the current size of the canvas and the number of 
		// bigpixel rows and columns and the min and max of the 
		// world coordinates, compute the coordinates of the bigpixel
		// where bigpixelx increases from left to right starting at 0 and going to
		// BIGPIXEL_COLS - 1
		// and
		// bigpixely increases from top to bottom starting at 0 and going to
		// BIGPIXEL_ROWS - 1
		//
		if(button == MouseEvent.BUTTON1)
		{
			System.out.println("Setting coordinates for the first point");
			bigpixelyFirst = y / (d.getHeight() / BIGPIXEL_ROWS);
			bigpixelxFirst = x / ((d.getWidth()  / ((double)(MAX_X - MIN_X) / (MAX_Y - MIN_Y))) / BIGPIXEL_COLS);

			//
			// if either the x or y coordinate of the big pixel is "out of bounds"
			// place it at the nearest big pixel "in bounds"
			//
			if (bigpixelxFirst >= BIGPIXEL_COLS)
				bigpixelxFirst = BIGPIXEL_COLS - 1;
			if (bigpixelyFirst >= BIGPIXEL_ROWS)
				bigpixelyFirst = BIGPIXEL_ROWS - 1;
			if (bigpixelxFirst < 0)
				bigpixelxFirst = 0;
			if (bigpixelyFirst < 0)
				bigpixelyFirst = 0;
		}
		else if(button == MouseEvent.BUTTON3)
		{
			System.out.println("Setting coordinates for the second point");
			bigpixelySecond = y / (d.getHeight() / BIGPIXEL_ROWS);
			bigpixelxSecond = x / ((d.getWidth()  / ((double)(MAX_X - MIN_X) / (MAX_Y - MIN_Y))) / BIGPIXEL_COLS);

			//
			// if either the x or y coordinate of the big pixel is "out of bounds"
			// place it at the nearest big pixel "in bounds"
			//
			if (bigpixelxSecond >= BIGPIXEL_COLS)
				bigpixelxSecond = BIGPIXEL_COLS - 1;
			if (bigpixelySecond >= BIGPIXEL_ROWS)
				bigpixelySecond = BIGPIXEL_ROWS - 1;
			if (bigpixelxSecond < 0)
				bigpixelxSecond = 0;
			if (bigpixelySecond < 0)
				bigpixelySecond = 0;

			System.out.println("Please press r on your keyboard to reset the two points.");
		}
		//		System.out.println("bigpixel xSecond, ySecond  = " + bigpixelxSecond + ", " + bigpixelySecond);
	}

	public void mousePressed(MouseEvent me) { }

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	//
	// End of dealing with Mouse Events
	//
	// ====================================================================================

} // end class