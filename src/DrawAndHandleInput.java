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

	// height and width of the bigpixel area in world coordinate measurements
	public static final int BIGAREA_HEIGHT = 200;
	public static final int BIGAREA_WIDTH = 200;

	// number of rows and columns of big pixels to appear in the grid
	// eventually these will be set by user input or via a command line
	// parameter.  That is functionality you need to add for program 02.
	public static int BIGPIXEL_ROWS;
	public static int BIGPIXEL_COLS;

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
	public int activeMode = 0;

	// globals to know whether we're dealing with first or second point
	public boolean firstPoint = true;

	// globals for color of the chosen big pixel
	private float r = 0;
	private float g = 0;
	private float b = 0;
	
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
		drawBigPixel((int)bigpixelxFirst,(int)bigpixelyFirst);
		
		if(!firstPoint)
		{
			drawBigPixel((int)bigpixelxSecond,(int)bigpixelySecond);
			
			// draw based on active mode
			if(activeMode == 1) 
			{
				// circular mode
				drawCircularMode(bigpixelxFirst, bigpixelyFirst, bigpixelxSecond, bigpixelySecond);
			}
			else if(activeMode == 2)
			{
				// regular line mode
				drawRegularLine(bigpixelxFirst, bigpixelyFirst, bigpixelxSecond, bigpixelySecond);
			}
			else if(activeMode == 3)
			{
				// antialiased mode
				drawAntialiasedLine(bigpixelxFirst, bigpixelyFirst, bigpixelxSecond, bigpixelySecond);
			}
			else
			{
				//System.out.println("No mode of drawing specified.");
			}
		}

		/* force any buffered calls to actually be executed */
		gl.glFlush();


		// this will swap the buffers (when double buffering)
		// canvas.swapBuffers() should do the same thing
		drawable.swapBuffers();

	} // end display

	/*
	 * Method to draw antialised line. 
	 * The two points passed as parameters are the two ending points
	 * of the line.
	 */
	private void drawAntialiasedLine(double bigpixelxFirst2,
			double bigpixelyFirst2, double bigpixelxSecond2,
			double bigpixelySecond2) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * Method to implement Bresenham line with the two 
	 * points given in the parameters
	 */
	private void drawRegularLine(double bigpixelxFirst2,
			double bigpixelyFirst2, double bigpixelxSecond2,
			double bigpixelySecond2) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * Method to draw circle based on two points.
	 * Takes in first point as the centre of the circle and draw
	 * the circle such that the distance between the two points is the
	 * radius.
	 */
	private void drawCircularMode(double bigpixelxFirst2,
			double bigpixelyFirst2, double bigpixelxSecond2,
			double bigpixelySecond2) {
		// TODO Auto-generated method stub
		
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

	 */
	public void drawBigPixel(int x, int y)
	{
		// because the y screen coordinates increase as we go down 
		// and the y world coordinates increase as we go up
		// we need to compute flip_y which will be the y coordinate
		// of the big pixel if the big pixel coordinates' y values
		// increased as we go up
		int flip_y = Math.abs((BIGPIXEL_ROWS-1) - y);
		gl.glColor3d(r, g, b);
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
			firstPoint = true;
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
			firstPoint = false;
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
			
			System.out.println("Please press r on your keyboard to reset the points.");
		}
		//System.out.println("bigpixel x, y  = " + bigpixelx + ", " + bigpixely);
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