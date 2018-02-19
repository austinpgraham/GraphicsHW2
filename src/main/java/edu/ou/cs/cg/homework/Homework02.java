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

	private final float ROAD_LIM = -0.6f;
	private final float GRASS_LIM = -0.05f;

	//**********************************************************************
	// Main
	//**********************************************************************

	public static void main(String[] args)
	{
		GLProfile		profile = GLProfile.getDefault();
		GLCapabilities	capabilities = new GLCapabilities(profile);
		GLCanvas		canvas = new GLCanvas(capabilities);
		JFrame			frame = new JFrame("Homework02");

		canvas.setPreferredSize(new Dimension(550, 550));

		frame.setBounds(50, 50, 500, 500);
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

		this.drawGrass(gl);

		this.drawSky(gl);

		this.drawMoon(gl);

		this.drawGreenHouse(gl);
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
		final float LINE_WIDTH = 3.0f;

		final Point2D.Float one = (Point2D.Float)sourcePoint;
		final Point2D.Float two =  new Point2D.Float((float)one.getX() + DELTA, (float)one.getY());
		final Point2D.Float three = new Point2D.Float((float)one.getX() + DELTA + OFFSET,(float)one.getY() + HEIGHT);
		final Point2D.Float four = new Point2D.Float((float)one.getX() + OFFSET, (float)one.getY() + HEIGHT);
		this.drawQuad(gl, one, two, three, four, fillColor);
		this.drawLine(gl, one, two, outlineColor, LINE_WIDTH);
		this.drawLine(gl, two, three, outlineColor, LINE_WIDTH);
		this.drawLine(gl, three, four, outlineColor, LINE_WIDTH);
		this.drawLine(gl, four, one, outlineColor, LINE_WIDTH);
	}

	private void drawGrass(GL2 gl)
	{
		final Point2D.Float one = new Point2D.Float(-1.0f, GRASS_LIM);
		final Point2D.Float two = new Point2D.Float(MAX_X, GRASS_LIM);
		final Point2D.Float three = new Point2D.Float(MAX_X, ROAD_LIM);
		final Point2D.Float four = new Point2D.Float(-1.0f, ROAD_LIM);
		final float[] purple = new float[]{25f/255f, 0f, 30f/255f};
		final float[] green = new float[]{40f/255f, 145f/255f, 40f/255f};
		this.drawQuadGradient(gl, one, two, three, four, purple, green);
	}

	private void drawSky(GL2 gl)
	{
		final Point2D.Float one = new Point2D.Float(-1.0f, 1.0f);
		final Point2D.Float two = new Point2D.Float(MAX_X, 1.0f);
		final Point2D.Float three = new Point2D.Float(MAX_X, GRASS_LIM);
		final Point2D.Float four = new Point2D.Float(-1.0f, GRASS_LIM);
		final float[] purple = new float[]{0f, 0f, 0f};
		final float[] green = new float[]{83f/255f, 83f/255f, 73f/255f};
		this.drawQuadGradient(gl, one, two, three, four, purple, green);
	}

	private void drawMoon(GL2 gl)
	{
		final float[] gray = new float[]{50f/255f, 50f/255f, 50f/255f};
		final Point2D.Float center = new Point2D.Float(-0.85f, 0.75f);
		this.drawCircle(gl, center, 0.15f, 0.0, 360.0, gray);
	}

	private void drawGreenHouse(GL2 gl)
	{
		final float[] DARK_GREEN = new float[]{};

		final Point2D.Float houseStart = new Point2D.Float(-0.85, ROAD_LIM);
		
		final Point2D.Float windowStart = new Point2D.Float(0.0f, 0.0f);
		final Point2D.Float doorStart = new Point2D.Float(-0.8f, ROAD_LIM);
		this.drawWindow(gl, windowStart);
		this.drawDoor(gl, doorStart);
	}

	private void drawWindow(GL2 gl, Point2D pos)
	{
		final float DIM = 0.1f;
		final float LINE_WIDTH = 3.0f;

		final float[] LIGHT_PURPLE = new float[]{230f/255f, 230f/255f, 250f/255f};
		final float[] YELLOW = new float[]{1f, 250f/255f, 205f/255f};
		final float[] BLACK = new float[]{0f, 0f, 0f};

		final Point2D.Float start = (Point2D.Float)pos;
		final Point2D.Float right_bot = new Point2D.Float((float)start.getX() + DIM, (float)start.getY());
		final Point2D.Float right_top = new Point2D.Float((float)start.getX() + DIM, (float)start.getY() + DIM);
		final Point2D.Float left_top = new Point2D.Float((float)start.getX(), (float)start.getY()+DIM);
		final Point2D.Float top_middle = new Point2D.Float((float)left_top.getX() + DIM / 2.0f, (float)left_top.getY());
		final Point2D.Float bot_middle = new Point2D.Float((float)start.getX() + DIM / 2.0f, (float)start.getY());
		final Point2D.Float left_middle = new Point2D.Float((float)left_top.getX(), (float)left_top.getY() - DIM / 2.0f);
		final Point2D.Float right_middle = new Point2D.Float((float)right_top.getX(), (float)right_top.getY() - DIM / 2.0f);
		this.drawQuad(gl, start, right_bot, right_top, left_top, LIGHT_PURPLE);
		this.drawTriangle(gl, start, right_bot, top_middle, YELLOW);
		this.drawLine(gl, start, top_middle, BLACK);
		this.drawLine(gl, top_middle, right_bot, BLACK);
		this.drawLine(gl, start, right_bot, BLACK, LINE_WIDTH);
		this.drawLine(gl, right_bot, right_top, BLACK, LINE_WIDTH);
		this.drawLine(gl, right_top, left_top, BLACK, LINE_WIDTH);
		this.drawLine(gl, left_top, start, BLACK, LINE_WIDTH);
		this.drawLine(gl, left_middle, right_middle, BLACK, LINE_WIDTH);
		this.drawLine(gl, top_middle, bot_middle, BLACK, LINE_WIDTH);
	}

	private void drawDoor(GL2 gl, Point2D pos)
	{
		final float HEIGHT = 0.2f;
		final float WIDTH = 0.1f;

		final float[] BLACK = new float[]{0f, 0f, 0f};
		final float[] LIGHT_BROWN = new float[]{205f/255f, 133f/255f, 63f/255f};
		final float[] LIGHT_GRAY = new float[]{0.75f, 0.75f, 0.75f};

		final Point2D.Float start = (Point2D.Float)pos;
		final Point2D.Float right_bot = new Point2D.Float((float)start.getX() + WIDTH, (float)start.getY());
		final Point2D.Float top_right = new Point2D.Float((float)start.getX() + WIDTH, (float)start.getY() + HEIGHT);
		final Point2D.Float top_left = new Point2D.Float((float)start.getX(), (float)start.getY() + HEIGHT);
		final Point2D.Float knob_center = new Point2D.Float((float)start.getX() + 0.015f, (float)start.getY() + HEIGHT / 2.0f);
		this.drawQuad(gl, start, right_bot, top_right, top_left, LIGHT_BROWN);
		this.drawLine(gl, start, right_bot, BLACK);
		this.drawLine(gl, right_bot, top_right, BLACK);
		this.drawLine(gl, top_right, top_left, BLACK);
		this.drawLine(gl, top_left, start, BLACK);
		this.drawCircle(gl, knob_center, 0.01f, 0.0f, 360f, LIGHT_GRAY);
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

	private void drawTriangle(GL2 gl, Point2D one, Point2D two, Point2D three, float[] color)
	{
		gl.glBegin(gl.GL_TRIANGLES);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(one.getX(), one.getY());
		gl.glVertex2d(two.getX(), two.getY());
		gl.glVertex2d(three.getX(), three.getY());
		gl.glEnd();
	}

	private void drawQuadGradient(GL2 gl, Point2D one, Point2D two, Point2D three, Point2D four, float[] start, float[] end)
	{
		gl.glBegin(gl.GL_QUADS);
		gl.glColor3f(start[0], start[1], start[2]);
		gl.glVertex2d(one.getX(), one.getY());
		gl.glVertex2d(two.getX(), two.getY());
		gl.glColor3f(end[0], end[1], end[2]);
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

	private void drawLine(GL2 gl, Point2D start, Point2D end, float[] color, float width)
	{
		gl.glLineWidth(width);
		gl.glBegin(GL.GL_LINES);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(start.getX(), start.getY());
		gl.glVertex2d(end.getX(), end.getY());
		gl.glEnd();
		gl.glLineWidth(1.0f);
	}

	private void drawCircle(GL2 gl, Point2D center, float radius, double start, double end, float[] color)
	{
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(center.getX(), center.getY());
		double centx = center.getX();
		double centy = center.getY();
		for(double angle = start; angle <= end; angle++)
		{
			double x = centx + Math.cos(Math.toRadians(angle))*radius;
			double y = centy + Math.sin(Math.toRadians(angle))*radius;
			gl.glVertex2d(x,y);
		}
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
