/*

  Example program for CS325

  Author: Michael Eckmann

  Updated to work with JOGL 2.3.2 (from October 2015 build)

  This class contains the main method and draws a window and creates an object of 
  DrawAndHandleInput which in a GLEventListener as well as a KeyListener and MouseListener.


 */
import java.awt.Frame;
import java.awt.event.*;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

public class ExampleMain {


	private static DrawAndHandleInput dahi;
	public static Frame testFrame;
	public static void main(String[] args) 
	{
		/*
		 * Getting user input for row and column size
		 */
		boolean acceptInput = false;
		int numRowCol = 0;
		while(!acceptInput)
		{
			System.out.println("Please enter a number for row and column: \n(We suggest > 10)");
			Scanner userInput = new Scanner(System.in);
			try{
				numRowCol = userInput.nextInt();
				if(numRowCol > 0)
				{
					System.out.println("You have input: " + numRowCol);
					acceptInput = true;
				}
				else
				{
					System.out.println("You have entered a negative number or 0. Please enter a positive number instead");
				}
			}
			catch( InputMismatchException inputError)
			{
				System.out.println("Please enter a number for the number of row and column.");
			}
		}

		float r = 0;
		float g = 0;
		float b = 0;
		acceptInput = false;
		while(!acceptInput)
		{
			System.out.println("Please enter a number for rgb between 0 and 1");
			Scanner userInput = new Scanner(System.in);
			try{
				System.out.println("Value for Red (r): ");
				r = userInput.nextFloat();
				if(r < 0 || r > 1)
				{
					System.out.println("Please enter a value between 0 and 1 for r");
				}
				else
				{
					System.out.println("Value for Green (g): ");
					g = userInput.nextFloat();
					if(g < 0 || g > 1)
					{
						System.out.println("Please enter a value between 0 and 1 for g");
					}
					else
					{
						System.out.println("Value for Blue (b): ");
						b = userInput.nextFloat();
						if(b < 0 || b > 1)
						{
							System.out.println("Please enter a value between 0 and 1 for b");
						}
						else
						{
							acceptInput = true;
						}
					}
				}
			}
			catch( InputMismatchException inputError)
			{
				System.out.println("Please enter a number for the RGB value.");
			}

		}
		/* Create the Frame */
		testFrame = new Frame("TestFrame");


		/* set the coordinates on the screen of the
	       upper left corner of the window 

	       So the window will start off at 10,10 
	       (near the upper left corner of the whole screen)
		 */
		testFrame.setLocation(10, 10);

		/* set the window to be 400x500 pixels 
               higher b/c of borders
		 */
		testFrame.setSize( 510, 428 );


		// This allows us to define some attributes
		// about the capabilities of GL for this program
		// such as color depth, and whether double buffering is
		// used.
		//GLCapabilities glCapabilities = new GLCapabilities();

		GLCapabilities glCapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));

		glCapabilities.setRedBits(8);
		glCapabilities.setGreenBits(8);
		glCapabilities.setBlueBits(8);
		glCapabilities.setAlphaBits(8);

		/*
		 * this will turn on double buffering
		 * ignore for now
		 * glCapabilities.setDoubleBuffered(true);
		 */
		glCapabilities.setDoubleBuffered(false);
		// create the GLCanvas that is to be added to our Frame
		GLCanvas canvas = new GLCanvas(glCapabilities);
		testFrame.add( canvas );

		// create the Animator and attach the GLCanvas to it
		Animator a = new Animator(canvas);

		// create an instance of the Class that listens to all events
		// (GLEvents, Keyboard, and Mouse events)
		// add this object as all these listeners to the canvas 
		dahi = new DrawAndHandleInput(canvas, numRowCol, r, g, b);
		canvas.addGLEventListener(dahi);
		canvas.addKeyListener(dahi);
		canvas.addMouseListener(dahi);

		// this will swap the buffers (when double buffering)
		// ignore for now
		// canvas.swapBuffers();

		// if user closes the window by clicking on the X in 
		// upper right corner
		testFrame.addWindowListener( new WindowListener() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			public void windowClosed(WindowEvent e) {

			}
			public void windowDeiconified(WindowEvent e) {

			}
			public void windowIconified(WindowEvent e) {

			}
			public void windowOpened(WindowEvent e) {

			}
			public void windowDeactivated(WindowEvent e) {

			}
			public void windowActivated(WindowEvent e) {

			}
		});
		/*		
		.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		      System.exit(0);
		    }
		  });
		 */	
		testFrame.setVisible(true);
		a.start(); // start the Animator, which periodically calls display() on the GLCanvas

	}
}