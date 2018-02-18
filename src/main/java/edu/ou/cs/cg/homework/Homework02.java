//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Feb  9 20:33:16 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160209 [weaver]:	Original file.
//
//******************************************************************************
// Notes:
//
//******************************************************************************

package edu.ou.cs.cg.homework;

//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

//******************************************************************************

/**
 * The <CODE>Homework02</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class Homework02
	implements GLEventListener
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	public static final GLU		GLU = new GLU();
	public static final GLUT	GLUT = new GLUT();
	public static final Random	RANDOM = new Random();

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private int				k = 0;		// Just an animation counter

	private int				w;			// Canvas width
	private int				h;			// Canvas height
	private TextRenderer	renderer;

	private final float MAX_X = 1.0f;
	private final float MAX_Y = 1.0f;

	private final float ROAD_LIM = -0.55f;

	//**********************************************************************
	// Main
	//**********************************************************************

	public static void main(String[] args)
	{
		GLProfile		profile = GLProfile.getDefault();
		GLCapabilities	capabilities = new GLCapabilities(profile);
		GLCanvas		canvas = new GLCanvas(capabilities);
		JFrame			frame = new JFrame("Homework02");

		canvas.setPreferredSize(new Dimension(1050, 550));

		frame.setBounds(50, 50, 1000, 500);
		frame.getContentPane().add(canvas);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

		canvas.addGLEventListener(new Homework02());

		FPSAnimator		animator = new FPSAnimator(canvas, 60);

		animator.start();
	}

	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	public void		init(GLAutoDrawable drawable)
	{
		w = drawable.getWidth();
		h = drawable.getHeight();

		renderer = new TextRenderer(new Font("Serif", Font.PLAIN, 18),
									true, true);
	}

	public void		dispose(GLAutoDrawable drawable)
	{
		renderer = null;
	}

	public void		display(GLAutoDrawable drawable)
	{
		update();
		render(drawable);
	}

	public void		reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		this.w = w;
		this.h = h;
	}

	//**********************************************************************
	// Private Methods (Rendering)
	//**********************************************************************

	private void	update()
	{
		k++;									// Counters are useful, right?
	}

	private void	render(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer

		// Draw the road
		this.drawRoad(gl);
	}

	//**********************************************************************
	// Private Methods (Coordinate System)
	//**********************************************************************

	private void	setProjection(GL2 gl)
	{
		GLU		glu = new GLU();

		gl.glMatrixMode(GL2.GL_PROJECTION);			// Prepare for matrix xform
		gl.glLoadIdentity();						// Set to identity matrix
		glu.gluOrtho2D(-1.0f, 1.0f, -1.0f, 1.0f);	// 2D translate and scale
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************

	private void drawRoad(GL2 gl)
	{
		final float DELTA = 0.2f;
		final float HEIGHT = 0.4f;
		final float OFFSET = 0.05f;
		final float GRAY_VAL = 0.61f;
		float rootX = -1.0f - OFFSET;
		float rootY = -1.0f;

		// Draw gray background to the road
		final Point2D.Float one = new Point2D.Float(rootX, rootY);
		final Point2D.Float two = new Point2D.Float(MAX_X, rootY);
		final Point2D.Float three = new Point2D.Float(MAX_X, -1.0f + HEIGHT);
		final Point2D.Float four = new Point2D.Float(rootX, -1.0f + HEIGHT);
		final float[] color = new float[]{GRAY_VAL, GRAY_VAL, GRAY_VAL};
		this.drawQuad(gl, one, two, three, four, color);

		// Draw hopscotch pattern
		this.drawHopscotch(gl);

		// Draw the lines on the road
		final float[] white = new float[]{1.0f, 1.0f, 1.0f};
		this.drawLine(gl, new Point2D.Float(rootX, -1.0f + HEIGHT), new Point2D.Float(MAX_X, -1.0f + HEIGHT), white);
		this.drawLine(gl, new Point2D.Float(rootX, -1.0f), new Point2D.Float(MAX_X, -1.0f), white);
		for(float x = rootX; x < 1.15; x += DELTA)
		{
			this.drawLine(gl, new Point2D.Float(x, -1.0f), new Point2D.Float(x + OFFSET, -1.0f + HEIGHT), white);
		}
	}

	private void drawHopscotch(GL2 gl)
	{
		final float DELTA = 0.1f;
		final float OFFSET = 0.015f;
		final float HEIGHT = DELTA;

		final float[] outline = new float[]{1.0f, 1.0f, 1.0f};
		final float[] fill = new float[]{0.9f, 0.837f, 0.735f};
		final Point2D.Float startRect = new Point2D.Float(0f, ROAD_LIM - 0.3f);
		this.drawHopRect(gl, startRect, outline, fill);
		this.drawHopRect(gl, new Point2D.Float((float)startRect.getX() + DELTA, (float)startRect.getY()), outline, fill);
		final Point2D.Float endHalf = new Point2D.Float((float)startRect.getX() + DELTA*2, (float)startRect.getY());
		this.drawHopRect(gl, endHalf, outline, fill);

		final Point2D.Float endHalfBot =  new Point2D.Float((float)endHalf.getX() + DELTA, (float)endHalf.getY());
		final Point2D.Float endHalfTop = new Point2D.Float((float)endHalf.getX() + DELTA + OFFSET,(float)endHalf.getY() + HEIGHT);
		final Point2D.Float startHalf = this.computeStartDiagRect(endHalfBot, endHalfTop);
		this.drawHopRect(gl, startHalf, outline, fill);
		this.drawHopRect(gl, new Point2D.Float((float)startHalf.getX() - OFFSET, (float)startHalf.getY() - HEIGHT), outline, fill);

		final Point2D.Float endSecondHalf = new Point2D.Float((float)startRect.getX() + DELTA*4, (float)startRect.getY());
		this.drawHopRect(gl, endSecondHalf, outline, fill);

		final Point2D.Float endSecHalfBot =  new Point2D.Float((float)endSecondHalf.getX() + DELTA, (float)endSecondHalf.getY());
		final Point2D.Float endSecHalfTop = new Point2D.Float((float)endSecondHalf.getX() + DELTA + OFFSET,(float)endSecondHalf.getY() + HEIGHT);
		final Point2D.Float startSecHalf = this.computeStartDiagRect(endSecHalfBot, endSecHalfTop);
		this.drawHopRect(gl, startSecHalf, outline, fill);
		this.drawHopRect(gl, new Point2D.Float((float)startSecHalf.getX() - OFFSET, (float)startSecHalf.getY() - HEIGHT), outline, fill);

		this.drawHopRect(gl, new Point2D.Float((float)startRect.getX() + DELTA*6, (float)startRect.getY()), outline, fill);
	}

	private void drawHopRect(GL2 gl, Point2D sourcePoint, float[] outlineColor, float[] fillColor)
	{
		final float DELTA = 0.1f;
		final float OFFSET = 0.015f;
		final float HEIGHT = DELTA;

		final Point2D.Float one = (Point2D.Float)sourcePoint;
		final Point2D.Float two =  new Point2D.Float((float)one.getX() + DELTA, (float)one.getY());
		final Point2D.Float three = new Point2D.Float((float)one.getX() + DELTA + OFFSET,(float)one.getY() + HEIGHT);
		final Point2D.Float four = new Point2D.Float((float)one.getX() + OFFSET, (float)one.getY() + HEIGHT);
		this.drawQuad(gl, one, two, three, four, fillColor);
		this.drawLine(gl, one, two, outlineColor);
		this.drawLine(gl, two, three, outlineColor);
		this.drawLine(gl, three, four, outlineColor);
		this.drawLine(gl, four, one, outlineColor);
	}

	private void drawQuad(GL2 gl, Point2D one, Point2D two, Point2D three, Point2D four, float[] color)
	{
		gl.glBegin(gl.GL_QUADS);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(one.getX(), one.getY());
		gl.glVertex2d(two.getX(), two.getY());
		gl.glVertex2d(three.getX(), three.getY());
		gl.glVertex2d(four.getX(), four.getY());
		gl.glEnd();
	}

	private void drawLine(GL2 gl, Point2D start, Point2D end, float[] color)
	{
		gl.glBegin(GL.GL_LINES);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(start.getX(), start.getY());
		gl.glVertex2d(end.getX(), end.getY());
		gl.glEnd();
	}

	private Point2D.Float computeStartDiagRect(Point2D start, Point2D end)
	{
		float rise = (float)(end.getY() - start.getY()) / 2.0f;
		float run = (float)(end.getX() - start.getX()) / 2.0f;
		return new Point2D.Float((float)start.getX() + run, (float)start.getY() + rise);
	}

	// This page is helpful (scroll down to "Drawing Lines and Polygons"):
	// http://www.linuxfocus.org/English/January1998/article17.html
	private void	drawSomething(GL2 gl)
	{
		gl.glBegin(GL.GL_POINTS);

		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glPointSize(2.0f);
		gl.glVertex2d(0.0, 0.0);

		gl.glEnd();
	}

	// This example on this page is long but helpful:
	// http://jogamp.org/jogl-demos/src/demos/j2d/FlyingText.java
	// Warning! Text is drawn in pixel coordinates, not projection coordinates.
	private void	drawSomeText(GLAutoDrawable drawable)
	{
		renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		renderer.setColor(1.0f, 1.0f, 0, 1.0f);
		renderer.draw("This is a point", w/2 + 8, h/2 - 5);
		renderer.endRendering();
	}
}

//******************************************************************************
