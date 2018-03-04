//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Feb  20 14:12 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20180220 [graham]:	Original file.
//
//******************************************************************************
// Notes: Imitates the image given in the homework 2 description.
//
//******************************************************************************

package edu.ou.cs.cg.homework;

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
 * @author  Austin Graham
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

	// Outline Bounds of the window
	private final float MAX_X = 2.0f;
	private final float MAX_Y = 1.0f;
	private final float MIN_X = -2.0f;
	private final float MIN_Y = -1.0f;

	// Outline background limits
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

		canvas.setPreferredSize(new Dimension(1100, 550));

		frame.setBounds(50, 50, 1050, 500);
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

		// Scale the GL coordinate system to the window
		setProjection(gl);

		// Draw the sky
		this.drawSky(gl);

		// Draw the galaxy
		this.drawGalaxy(gl);

		// Draw the road
		this.drawRoad(gl);

		// Draw the grass
		this.drawGrass(gl);

		// Draw the moon
		this.drawMoon(gl);

		// Draw the green house
		this.drawGreenHouse(gl);

		// Draw far left fence
		for(float x = MIN_X + 0.02f, count = 0; count < 4; x += 0.075f, count++)
		{
			final Point2D.Float start = new Point2D.Float(x, ROAD_LIM + 0.01f);
			this.drawFencePost(gl, start);
		}

		// Draw right side fence against the left side of the green house
		for(float x = MIN_X + 0.86f, count = 0; count < 4; x += 0.075f, count++)
		{
			final Point2D.Float start = new Point2D.Float(x, ROAD_LIM + 0.01f);
			this.drawFencePost(gl, start);
		}

		// Draw the right side fence on the other side of the right house
		for(float x = MIN_X + 1.18f, count = 0; count < 4; x += 0.075f, count++)
		{
			final Point2D.Float start = new Point2D.Float(x, ROAD_LIM + 0.01f);
			this.drawFencePost(gl, start);
		}

		// Draw far right fence
		final Point2D.Float first = new Point2D.Float(MAX_X - 2*0.075f - 0.02f, ROAD_LIM + 0.01f);
		this.drawFencePost(gl, first);
		final Point2D.Float reflected = new Point2D.Float(MAX_X - 0.075f - 0.02f, ROAD_LIM + 0.01f);
		this.drawReflectedFencePost(gl, reflected);

		// Draw right hand brown house
		this.drawRightBrownHouse(gl);

		// Draw the left brown house
		this.drawLeftBrownHouse(gl);

		// Draw the groups of reflected fence posts
		for(float x = MAX_X - 3*0.075f - 0.02f-0.5f; x > MAX_X - 10*0.075f - 0.02f-0.5f; x -= 2*0.075f)
		{
			final Point2D.Float first_left = new Point2D.Float(x - 0.075f, ROAD_LIM + 0.01f);
			this.drawFencePost(gl, first_left);
			final Point2D.Float reflected_left = new Point2D.Float(x, ROAD_LIM + 0.01f);
			this.drawReflectedFencePost(gl, reflected_left);
		}

		// Draw eight point starts in the sky
		final float[] YELLOW = new float[]{1.0f, 1.0f, 0f};
		final Point2D.Float star1 = new Point2D.Float(1.0f, MAX_Y - 0.2f);
		this.drawEightPointStar(gl, star1, 0.08f, YELLOW, 1f);
		final Point2D.Float star2 = new Point2D.Float(1.3f, MAX_Y - 0.3f);
		this.drawEightPointStar(gl, star2, 0.08f, YELLOW, 1f);
		final Point2D.Float star3 = new Point2D.Float(1.6f, MAX_Y - 0.25f);
		this.drawEightPointStar(gl, star3, 0.08f, YELLOW, 1f);
		final Point2D.Float star4 = new Point2D.Float(1.7f, MAX_Y - 0.5f);
		this.drawEightPointStar(gl, star4, 0.08f, YELLOW, 0.5f);
		final Point2D.Float star5 = new Point2D.Float(1.55f, MAX_Y - 0.7f);
		this.drawEightPointStar(gl, star5, 0.08f, YELLOW, 0.3f);

		// Draw the kite and string
		this.drawKite(gl);
	}

	//**********************************************************************
	// Private Methods (Coordinate System)
	//**********************************************************************

	private void	setProjection(GL2 gl)
	{
		GLU		glu = new GLU();

		gl.glMatrixMode(GL2.GL_PROJECTION);			// Prepare for matrix xform
		gl.glLoadIdentity();						// Set to identity matrix
		glu.gluOrtho2D(-2.0f, 2.0f, -1.0f, 1.0f);	// 2D translate and scale
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************

	/* Draw the road
	 * @param gl: The GL context
	 */
	private void drawRoad(GL2 gl)
	{
		// Constants for road dimensions
		final float DELTA = 0.2f;
		final float HEIGHT = 0.4f;
		final float OFFSET = 0.05f;
		final float GRAY_VAL = 0.61f;
		// Start for the road lines
		float rootX = MIN_X - OFFSET;
		float rootY = -1.0f;

		// Draw gray background to the road
		final Point2D.Float one = new Point2D.Float(rootX, rootY);
		final Point2D.Float two = new Point2D.Float(MAX_X, rootY);
		final Point2D.Float three = new Point2D.Float(MAX_X, MIN_Y + HEIGHT);
		final Point2D.Float four = new Point2D.Float(rootX, MIN_Y + HEIGHT);
		final float[] color = new float[]{GRAY_VAL, GRAY_VAL, GRAY_VAL};
		this.drawQuad(gl, one, two, three, four, color);

		// Draw hopscotch pattern
		this.drawHopscotch(gl);

		// Draw the lines on the road
		final float[] white = new float[]{1.0f, 1.0f, 1.0f};
		this.drawLine(gl, new Point2D.Float(rootX, -1.0f + HEIGHT), new Point2D.Float(MAX_X, -1.0f + HEIGHT), white);
		this.drawLine(gl, new Point2D.Float(rootX, -1.0f), new Point2D.Float(MAX_X, -1.0f), white);
		for(float x = rootX; x < MAX_X + 0.15f; x += DELTA)
		{
			this.drawLine(gl, new Point2D.Float(x, -1.0f), new Point2D.Float(x + OFFSET, -1.0f + HEIGHT), white);
		}
	}

	/* Draw the hopscotch pattern on the road
	 * @param gl: GL context
	 */
	private void drawHopscotch(GL2 gl)
	{
		// Dimensions for each individual square
		final float DELTA = 0.1f;
		final float OFFSET = 0.015f;
		final float HEIGHT = DELTA;

		// Define the color for the pattern
		final float[] outline = new float[]{1.0f, 1.0f, 1.0f};
		final float[] fill = new float[]{0.9f, 0.837f, 0.735f};

		// Draw the first rectangles
		final Point2D.Float startRect = new Point2D.Float(0f, ROAD_LIM - 0.3f);
		this.drawHopRect(gl, startRect, outline, fill);
		this.drawHopRect(gl, new Point2D.Float((float)startRect.getX() + DELTA, (float)startRect.getY()), outline, fill);
		final Point2D.Float endHalf = new Point2D.Float((float)startRect.getX() + DELTA*2, (float)startRect.getY());
		this.drawHopRect(gl, endHalf, outline, fill);

		// Draw the two in the middle - first time
		final Point2D.Float endHalfBot =  new Point2D.Float((float)endHalf.getX() + DELTA, (float)endHalf.getY());
		final Point2D.Float endHalfTop = new Point2D.Float((float)endHalf.getX() + DELTA + OFFSET,(float)endHalf.getY() + HEIGHT);
		final Point2D.Float startHalf = this.computeStartDiagRect(endHalfBot, endHalfTop);
		this.drawHopRect(gl, startHalf, outline, fill);
		this.drawHopRect(gl, new Point2D.Float((float)startHalf.getX() - OFFSET, (float)startHalf.getY() - HEIGHT), outline, fill);

		// Draw the rect in the middle
		final Point2D.Float endSecondHalf = new Point2D.Float((float)startRect.getX() + DELTA*4, (float)startRect.getY());
		this.drawHopRect(gl, endSecondHalf, outline, fill);

		// Draw the second two in the middle
		final Point2D.Float endSecHalfBot =  new Point2D.Float((float)endSecondHalf.getX() + DELTA, (float)endSecondHalf.getY());
		final Point2D.Float endSecHalfTop = new Point2D.Float((float)endSecondHalf.getX() + DELTA + OFFSET,(float)endSecondHalf.getY() + HEIGHT);
		final Point2D.Float startSecHalf = this.computeStartDiagRect(endSecHalfBot, endSecHalfTop);
		this.drawHopRect(gl, startSecHalf, outline, fill);
		this.drawHopRect(gl, new Point2D.Float((float)startSecHalf.getX() - OFFSET, (float)startSecHalf.getY() - HEIGHT), outline, fill);

		// Draw last rect
		this.drawHopRect(gl, new Point2D.Float((float)startRect.getX() + DELTA*6, (float)startRect.getY()), outline, fill);
	}

	/* Draw a single hopscotch rectangle
	 * @param gl: the GL context
	 * @param sourcePoint: Bottom left point in rect
	 * @param outlineColor: outline color
	 * @param fillColor: Fill color of rect
	 */
	private void drawHopRect(GL2 gl, Point2D sourcePoint, float[] outlineColor, float[] fillColor)
	{
		// Define dimensions
		final float DELTA = 0.1f;
		final float OFFSET = 0.015f;
		final float HEIGHT = DELTA;
		final float LINE_WIDTH = 3.0f;

		// Define vertices and outline
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

	/* Draw the grass background
	 * @param gl: GL context
	 */
	private void drawGrass(GL2 gl)
	{
		// Define points and draw with gradient
		final Point2D.Float one = new Point2D.Float(MIN_X, GRASS_LIM);
		final Point2D.Float two = new Point2D.Float(MAX_X, GRASS_LIM);
		final Point2D.Float three = new Point2D.Float(MAX_X, ROAD_LIM);
		final Point2D.Float four = new Point2D.Float(MIN_X, ROAD_LIM);
		final float[] purple = new float[]{81f/255f, 65f/255f, 63f/255f};
		final float[] green = new float[]{98f/255f, 142f/255f, 84f/255f};
		this.drawQuadGradient(gl, one, two, three, four, purple, green);
	}

	//*********************************************//
	// Three change equations for Lorenz equations //
	//*********************************************//
	private double dx(double x, double y, double z)
	{
		return -10*(x - y);
	}

	private double dy(double x, double y, double z)
	{
		return -x*z + 28*x - y;
	}

	private double dz(double x, double y, double z)
	{
		return x*y - 8*z/3;
	}

	/* Draw galazy using Lorenz equations
	 * @param gl: The GL context
	 */
	private void drawGalaxy(GL2 gl)
	{
		// Start point for galaxy
		float x = 0.5f;
		float y = 0.9f;
		float z = 1f;
		// Draw as points 
		gl.glBegin(gl.GL_POINTS);
		gl.glColor3f(0.8f, 0.8f, 0.8f);
		// Time lapse
		float dt = 0.01f;
		// Draw 20000 points
		for(int i=0;i<20000;i++)
		{
			// Define new point as change from 
			// previous point
			x += (float)dx(x,y,z)*dt;
			y += (float)dy(x,y,z)*dt;
			z += (float)dz(x,y,z)*dt;
			gl.glVertex2f(0.05f*x, 0.05f*y);
		}
		gl.glEnd();
	}

	/* Draw the kite and string
	 * @param gl: The GL context
	 */
	private void drawKite(GL2 gl)
	{
		final float[] BLUE = new float[]{70f/255f,130f/255f,180f/255f};
		final Point2D.Float center = new Point2D.Float(1.0f, 0.4f);
		// Draw both halves of the kite
		this.drawKitePiece(gl, center, 0.25f, 90, 180, BLUE);
		this.drawKitePiece(gl, center, 0.25f, 270, 360, BLUE);

		// Draw pieces of the string one at a time
		final float[] GRAY = new float[]{0.8f, 0.8f, 0.8f};
		Point2D.Float start = new Point2D.Float(MAX_X - 3*0.075f - 0.02f-0.5f, ROAD_LIM + 0.4f);
		Point2D.Float end = new Point2D.Float((float)start.getX() - 0.2f, (float)start.getY() + 0.15f);
		this.drawLine(gl, start, end, GRAY, 2.0f);
		start = end;
		end = new Point2D.Float((float)start.getX() - 0.12f, (float)start.getY() + 0.1f);
		this.drawLine(gl, start, end, GRAY, 2.0f);
		start = end;
		end = new Point2D.Float((float)start.getX() - 0.08f, (float)start.getY() + 0.11f);
		this.drawLine(gl, start, end, GRAY, 2.0f);
		start = end;
		end = new Point2D.Float((float)start.getX() +0.02f, (float)start.getY() + 0.03f);
		this.drawLine(gl, start, end, GRAY, 2.0f);
		start = end;
		end = new Point2D.Float((float)start.getX() - 0.04f, (float)start.getY() + 0.04f);
		this.drawLine(gl, start, end, GRAY, 2.0f);
		start = end;
		end = new Point2D.Float((float)start.getX() - 0.04f, (float)start.getY() + 0.02f);
		this.drawLine(gl, start, end, GRAY, 2.0f);
		start = end;
		this.drawLine(gl, start, center, GRAY, 2.0f);
	}

	/*
	 * Overload of the draw circle that will include individual 
	 * triangle outlines.
	 */
	private void drawKitePiece(GL2 gl, Point2D center, float radius, double start, double end, float[] color)
	{
		final float[] GRAY = new float[]{0.6f, 0.6f, 0.6f};
		int len = 6;
		double[] vert = new double[12];
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(center.getX(), center.getY());
		double centx = center.getX();
		double centy = center.getY();
		for(double angle = start, i = 0; angle <= end; angle+=18, i+= 2)
		{
			double x = centx + Math.cos(Math.toRadians(angle))*radius;
			double y = centy + Math.sin(Math.toRadians(angle))*radius;
			gl.glVertex2d(x,y);
			vert[(int)i] = x;
			vert[(int)i+1] = y;
		}
		gl.glEnd();
		for(int i = 0; i < vert.length - 2; i+=2)
		{
			final Point2D.Float from = new Point2D.Float((float)vert[i], (float)vert[i+1]);
			final Point2D.Float to = new Point2D.Float((float)vert[i+2], (float)vert[i+3]);
			this.drawLine(gl, from, to, GRAY, 5.0f, 0.3f);
			this.drawLine(gl, center, from, GRAY, 5.0f, 0.3f);
			this.drawLine(gl, center, to, GRAY, 5.0f, 0.3f);
		}
		// final Point2D.Float from = new Point2D.Float((float)vert[vert.length - 2], (float)vert[vert.length - 1]);
		// final Point2D.Float to = new Point2D.Float((float)vert[0], (float)vert[1]);
		// this.drawLine(gl, from, to, GRAY, 5.0f, 0.3f);
	}

	/* Draw the sky background
	 * @param gl: The GL context
	 */
	private void drawSky(GL2 gl)
	{
		final Point2D.Float one = new Point2D.Float(MIN_X, MAX_Y);
		final Point2D.Float two = new Point2D.Float(MAX_X, MAX_Y);
		final Point2D.Float three = new Point2D.Float(MAX_X, GRASS_LIM);
		final Point2D.Float four = new Point2D.Float(MIN_X, GRASS_LIM);
		final float[] purple = new float[]{12f/255f, 18f/255f, 30f/255f};
		final float[] green = new float[]{144f/255f, 129f/255f, 100f/255f};
		// Draw with gradient
		this.drawQuadGradient(gl, one, two, three, four, purple, green);
	}

	/* Draw the moon object
	 * @param gl: The GL context
	 */
	private void drawMoon(GL2 gl)
	{
		final float[] WHITE = new float[]{255f, 255f, 255f};
		final float[] DARK_GRAY = new float[]{81f/255f, 82f/255f, 98f/255f};
		final Point2D.Float center = new Point2D.Float(MIN_X + 0.3f, 0.7f);
		final Point2D.Float small_center = new Point2D.Float(MIN_X + 0.35f, 0.72f);
		// Draw a background white circle, then another dark gray one on top
		this.drawCircle(gl, center, 0.2f, 0.0, 360.0, WHITE, false);
		this.drawCircle(gl, small_center, 0.16f, 0.0, 360f, DARK_GRAY, false);
	}

	/* Draw the green house
	 * @param gl: The GL context
	 */
	private void drawGreenHouse(GL2 gl)
	{
		// Define the dimensions
		final float WIDTH = 0.5f;
		final float HEIGHT = 0.5f;

		// Define the colors
		final float[] DARK_GREEN = new float[]{85f/255f, 107f/255f, 47f/255f};
		final float[] DARK_GRAY = new float[]{0.3f, 0.3f, 0.3f};
		final float[] BLACK = new float[]{0f, 0f, 0f};

		// Draw outline and chimney
		final Point2D.Float houseStart = new Point2D.Float(MIN_X + 0.34f, ROAD_LIM + 0.01f);
		final Point2D.Float right_bot = new Point2D.Float((float)houseStart.getX() + WIDTH, (float)houseStart.getY());
		final Point2D.Float right_top = new Point2D.Float((float)houseStart.getX() + WIDTH, (float)houseStart.getY() + HEIGHT);
		final Point2D.Float left_top = new Point2D.Float((float)houseStart.getX(), (float)houseStart.getY() + HEIGHT);
		this.drawChimney(gl, new Point2D.Float((float)houseStart.getX() + 0.35f, (float)houseStart.getY()), true);
		this.drawQuad(gl, houseStart, right_bot, right_top, left_top, DARK_GREEN);
		this.drawLine(gl, houseStart, right_bot, BLACK);
		this.drawLine(gl, right_bot, right_top, BLACK);
		this.drawLine(gl, left_top, houseStart, BLACK);

		// Draw the window and the door
		final Point2D.Float windowStart = new Point2D.Float((float)houseStart.getX() + 0.3f, (float)houseStart.getY() + HEIGHT / 2.0f + 0.05f);
		final Point2D.Float doorStart = new Point2D.Float((float)houseStart.getX()+0.1f, ROAD_LIM + 0.01f);
		this.drawWindow(gl, windowStart);
		this.drawDoor(gl, doorStart);

		// Draw the roof with outline
		final Point2D.Float roofSource = new Point2D.Float((float)houseStart.getX() + WIDTH / 2.0f, 0.2f);
		this.drawTriangle(gl, roofSource, left_top, right_top, DARK_GRAY);
		this.drawLine(gl, roofSource, left_top, BLACK);
		this.drawLine(gl, roofSource, right_top, BLACK);
		this.drawLine(gl, right_top, left_top, BLACK);
		this.drawLine(gl, roofSource, new Point2D.Float((float)left_top.getX() + WIDTH / 3.0f, (float)left_top.getY()), BLACK);
		this.drawLine(gl, roofSource, new Point2D.Float((float)left_top.getX() + WIDTH / 3.0f * 2.0f, (float)left_top.getY()), BLACK);
		this.drawLine(gl, roofSource, new Point2D.Float((float)left_top.getX() + WIDTH / 6.0f, (float)left_top.getY()), BLACK);
		this.drawLine(gl, roofSource, new Point2D.Float((float)left_top.getX() + WIDTH / 6.0f * 5.0f, (float)left_top.getY()), BLACK);
	}

	/* Draw the brown house on the right
	 * @param gl: The GL context
	 */
	private void drawRightBrownHouse(GL2 gl)
	{
		// Define dimensions
		final float WIDTH = 0.5f;
		final float HEIGHT = 0.5f;

		// Define the colors
		final float[] BROWN = new float[]{153f/255f, 76f/255f, 0f/255f};
		final float[] BLACK = new float[]{0f, 0f, 0f};
		final float[] YELLOW = new float[]{1.0f, 1.0f, 0f};

		// Draw the outline andchimney
		final Point2D.Float houseStart = new Point2D.Float(MAX_X - 2*0.075f - 0.5f - 0.02f, ROAD_LIM + 0.01f);
		final Point2D.Float right_bot = new Point2D.Float((float)houseStart.getX() + WIDTH, (float)houseStart.getY());
		final Point2D.Float right_top = new Point2D.Float((float)houseStart.getX() + WIDTH, (float)houseStart.getY() + HEIGHT);
		final Point2D.Float left_top = new Point2D.Float((float)houseStart.getX(), (float)houseStart.getY() + HEIGHT);
		this.drawChimney(gl, new Point2D.Float((float)houseStart.getX() + 0.06f, ROAD_LIM + 0.01f), false);
		this.drawQuad(gl, houseStart, right_bot, right_top, left_top, BROWN);
		this.drawLine(gl, houseStart, right_bot, BLACK);
		this.drawLine(gl, right_bot, right_top, BLACK);
		this.drawLine(gl, left_top, houseStart, BLACK);

		// Draw the triangle
		final Point2D.Float roofPoint = new Point2D.Float((float)houseStart.getX() + WIDTH / 2.0f, 0.2f);
		this.drawTriangle(gl, left_top, roofPoint, right_top, BROWN);
		this.drawLine(gl, left_top, roofPoint, BLACK);
		this.drawLine(gl, roofPoint, right_top, BLACK);

		// Draw the door
		final Point2D.Float doorStart = new Point2D.Float((float)houseStart.getX() + 0.02f, ROAD_LIM + 0.01f);
		this.drawDoor(gl, doorStart);
		final Point2D.Float firstWindow = new Point2D.Float((float)houseStart.getX() + 0.22f, ROAD_LIM + 0.12f);
		final Point2D.Float secondWindow = new Point2D.Float((float)houseStart.getX() + 0.22f + 0.12f + 0.02f, ROAD_LIM + 0.12f);
		// Draw the windows
		this.drawWindow(gl, firstWindow);
		this.drawWindow(gl, secondWindow);

		// Draw the symbol on the door
		final Point2D.Float doorSymbol = new Point2D.Float((float)doorStart.getX()+0.06f, ROAD_LIM + 0.2f);
		this.drawCircle(gl, doorSymbol, 0.045f, 0.0f, 360f, 45, YELLOW, true);
	}

	/* The the brown house on the left
	 * @param gl: The GL context
	 */
	private void drawLeftBrownHouse(GL2 gl)
	{
		// Define the dimensions
		final float WIDTH = 0.5f;
		final float HEIGHT = 0.5f;

		// Define the colors
		final float[] BROWN = new float[]{153f/255f, 76f/255f, 0f/255f};
		final float[] BLACK = new float[]{0f, 0f, 0f};
		final float[] YELLOW = new float[]{1.0f, 1.0f, 0f};

		// Draw the outline and chimney
		final Point2D.Float houseStart = new Point2D.Float(0.1f, ROAD_LIM + 0.1f);
		final Point2D.Float right_bot = new Point2D.Float((float)houseStart.getX() + WIDTH, (float)houseStart.getY());
		final Point2D.Float right_top = new Point2D.Float((float)houseStart.getX() + WIDTH, (float)houseStart.getY() + HEIGHT);
		final Point2D.Float left_top = new Point2D.Float((float)houseStart.getX(), (float)houseStart.getY() + HEIGHT);
		this.drawChimney(gl, new Point2D.Float((float)houseStart.getX() + 0.06f, ROAD_LIM + 0.1f), false);
		this.drawQuad(gl, houseStart, right_bot, right_top, left_top, BROWN);
		this.drawLine(gl, houseStart, right_bot, BLACK);
		this.drawLine(gl, right_bot, right_top, BLACK);
		this.drawLine(gl, left_top, houseStart, BLACK);
		// Draw the rooftop
		final Point2D.Float roofPoint = new Point2D.Float((float)houseStart.getX() + WIDTH / 2.0f, 0.27f);
		this.drawTriangle(gl, left_top, roofPoint, right_top, BROWN);
		this.drawLine(gl, left_top, roofPoint, BLACK);
		this.drawLine(gl, roofPoint, right_top, BLACK);
		// Draw the star at the top
		final Point2D.Float startPos = new Point2D.Float((float)roofPoint.getX(), (float)roofPoint.getY() - 0.15f);
		this.drawFivePointStar(gl, startPos);
		// Draw the door and windows
		final Point2D.Float doorStart = new Point2D.Float((float)houseStart.getX() + 0.02f, ROAD_LIM + 0.1f);
		this.drawDoor(gl, doorStart);
		final Point2D.Float firstWindow = new Point2D.Float((float)houseStart.getX() + 0.22f, ROAD_LIM + 0.21f);
		final Point2D.Float secondWindow = new Point2D.Float((float)houseStart.getX() + 0.22f + 0.12f + 0.02f, ROAD_LIM + 0.21f);
		this.drawWindow(gl, firstWindow);
		this.drawWindow(gl, secondWindow);
	}

	/* Draw a chimney
	 * @param gl: The GL context
	 * @param po: Bottom left corner
	 * @param smoke: Draw with smoke
	 */
	private void drawChimney(GL2 gl, Point2D pos, boolean smoke)
	{
		// Define dimensions
		final float WIDTH = 0.1f;
		final float HEIGHT = 0.77f;

		// Define colors
		final float[] BROWN = new float[]{128f/255f, 0f/255f, 0f/255f};
		final float[] BLACK = new float[]{0f, 0f, 0f};

		// Define outline and draw smoke if necessary
		final Point2D.Float start = (Point2D.Float)pos;
		final Point2D.Float bot_right = new Point2D.Float((float)start.getX() + WIDTH, (float)start.getY());
		final Point2D.Float top_right = new Point2D.Float((float)start.getX() + WIDTH, (float)start.getY() + HEIGHT);
		final Point2D.Float top_left = new Point2D.Float((float)start.getX(), (float)start.getY() + HEIGHT);
		if(smoke)
		{
			this.drawSmoke(gl, new Point2D.Float((float)top_left.getX()+0.01f, (float)top_left.getY()), new Point2D.Float((float)top_right.getX()-0.01f, (float)top_right.getY()));
		}
		this.drawQuad(gl, start, bot_right, top_right, top_left, BROWN);
		this.drawLine(gl, start, bot_right, BLACK);
		this.drawLine(gl, bot_right, top_right, BLACK);
		this.drawLine(gl, top_right, top_left, BLACK);
		this.drawLine(gl, top_left, start, BLACK);
	}

	/* Draw a window object
	 * @param gl: The GL context
	 * @param pos: The bottom left corner
	 */
	private void drawWindow(GL2 gl, Point2D pos)
	{
		// Define the dimensions
		final float DIM = 0.12f;
		final float LINE_WIDTH = 3.0f;

		// Define the colors
		final float[] LIGHT_PURPLE = new float[]{230f/255f, 230f/255f, 250f/255f};
		final float[] YELLOW = new float[]{1f, 250f/255f, 205f/255f};
		final float[] BLACK = new float[]{0f, 0f, 0f};

		// Draw the outline of the window
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

	/* Draw a smoke column
	 * @param gl: The GL context
	 * @param left: The left start
	 * @param right: The right start
	 */
	private void drawSmoke(GL2 gl, Point2D left, Point2D right)
	{
		double startX_left = left.getX();
		double startY_left = left.getY();
		double startX_right = right.getX();
		double startY_right = right.getY();
		gl.glBegin(gl.GL_QUAD_STRIP);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glVertex2d(left.getX(), left.getY());
		gl.glVertex2d(right.getX(), right.getY());
		gl.glVertex2d(startX_left, startY_left + 0.05f);
		gl.glVertex2d(startX_right, startY_right + 0.05f);
		gl.glVertex2d(startX_left + 0.03f, startY_left + 0.07f);
		gl.glVertex2d(startX_right, startY_right + 0.07f);
		gl.glVertex2d(startX_left + 0.04f, startY_left + 0.1f);
		gl.glVertex2d(startX_right + 0.03f, startY_right + 0.1f);
		gl.glVertex2d(startX_left + 0.05f, startY_left + 0.13f);
		gl.glVertex2d(startX_right + 0.02f, startY_right + 0.13f);
		gl.glVertex2d(startX_left + 0.01f, startY_left + 0.17f);
		gl.glVertex2d(startX_right + 0.03f, startY_right + 0.17f);
		gl.glVertex2d(startX_left + 0.03f, startY_left + 0.2f);
		gl.glVertex2d(startX_right + 0.02f, startY_right + 0.2f);
		gl.glVertex2d(startX_left + 0.05f, startY_left + 0.24f);
		gl.glVertex2d(startX_right + 0.03f, startY_right + 0.24f);
		gl.glVertex2d(startX_left + 0.07f, startY_left + 0.26f);
		gl.glVertex2d(startX_right + 0.04f, startY_right + 0.26f);
		gl.glEnd();
	}

	/* Draw a five point star
	 * @param gl: The GL context
	 * @param start: The middle of the star
	 */
	private void drawFivePointStar(GL2 gl, Point2D start)
	{
		// Draw using triangle fan
		double centerX = start.getX();
		double centerY = start.getY();
		gl.glBegin(gl.GL_TRIANGLE_FAN);
		gl.glColor3f(1f, 1f, 0f);
		gl.glVertex2d(centerX, centerY);
		gl.glVertex2d(centerX, centerY + 0.05f);
		gl.glVertex2d(centerX - 0.02f, centerY + 0.005f);
		gl.glVertex2d(centerX - 0.06f, centerY + 0.005f);
		gl.glVertex2d(centerX - 0.02f, centerY - 0.02f);
		gl.glVertex2d(centerX - 0.04f, centerY - 0.06f);
		gl.glVertex2d(centerX, centerY - 0.03f);
		gl.glVertex2d(centerX + 0.04f, centerY - 0.06f);
		gl.glVertex2d(centerX + 0.02f, centerY - 0.02f);
		gl.glVertex2d(centerX + 0.06f, centerY + 0.005);
		gl.glVertex2d(centerX + 0.02f, centerY + 0.005f);
		gl.glVertex2d(centerX, centerY + 0.05f);
		gl.glEnd();
		// Outline the triangle
		gl.glBegin(gl.GL_LINES);
		gl.glColor3f(0f, 0f, 0f);
		gl.glVertex2d(centerX, centerY + 0.05f);
		gl.glVertex2d(centerX - 0.02f, centerY + 0.005f);
		gl.glVertex2d(centerX - 0.02f, centerY + 0.005f);
		gl.glVertex2d(centerX - 0.06f, centerY + 0.005f);
		gl.glVertex2d(centerX - 0.06f, centerY + 0.005f);
		gl.glVertex2d(centerX - 0.02f, centerY - 0.02f);
		gl.glVertex2d(centerX - 0.02f, centerY - 0.02f);
		gl.glVertex2d(centerX - 0.04f, centerY - 0.06f);
		gl.glVertex2d(centerX - 0.04f, centerY - 0.06f);
		gl.glVertex2d(centerX, centerY - 0.03f);
		gl.glVertex2d(centerX, centerY - 0.03f);
		gl.glVertex2d(centerX + 0.04f, centerY - 0.06f);
		gl.glVertex2d(centerX + 0.04f, centerY - 0.06f);
		gl.glVertex2d(centerX + 0.02f, centerY - 0.02f);
		gl.glVertex2d(centerX + 0.02f, centerY - 0.02f);
		gl.glVertex2d(centerX + 0.06f, centerY + 0.005f);
		gl.glVertex2d(centerX + 0.06f, centerY + 0.005f);
		gl.glVertex2d(centerX + 0.02f, centerY + 0.005f);
		gl.glVertex2d(centerX + 0.02f, centerY + 0.005f);
		gl.glVertex2d(centerX, centerY + 0.05f);
		gl.glEnd();
	}

	/* Draws the eight point starts at the top right.
	 * @param gl: the GL2 Context
	 * @param gl: the center of the star
	 * @param radius: Larger radius of star points
	 * @param color: Color of the star
	 * @param alpha: Fade of star color
	 */
	private void drawEightPointStar(GL2 gl, Point2D center, float radius, float[] color, float alpha)
	{
		final float[] BLACK = new float[]{0f, 0f, 0f};
		final float SCALE = 0.4f;
		int len = 16;
		double[] vert = new double[36];
		// Enable alpha blending
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glColor4f(color[0], color[1], color[2], alpha);
		// Get and draw center
		gl.glVertex2d(center.getX(), center.getY());
		double centx = center.getX();
		double centy = center.getY();
		// Draw outer points
		for(double angle = 0, i = 0; angle <= 360; angle+=45f, i+= 4)
		{
			double x = centx + Math.cos(Math.toRadians(angle))*radius;
			double y = centy + Math.sin(Math.toRadians(angle))*radius;
			// Scale in the smaller points in the star
			double in_x = centx + Math.cos(Math.toRadians(angle+22.5))*radius*SCALE;
			double in_y = centy + Math.sin(Math.toRadians(angle+22.5))*radius*SCALE;
			gl.glVertex2d(x,y);
			gl.glVertex2d(in_x, in_y);
			// DEPRACATED. Code is unused
			vert[(int)i] = x;
			vert[(int)i+1] = y;
			vert[(int)i+2] = in_x;
			vert[(int)i+3] = in_y;
		}
		gl.glEnd();
	}

	/* Draw a fence post
	 * @param gl: The GL context
	 * @param source: The bottom left corner
	 */
	private void drawFencePost(GL2 gl, Point2D source)
	{
		// Define the dimensions
		final float WIDTH = 0.075f;
		final float HEIGHT = 0.4f;

		// Define the colors
		final float[] BEIGE = new float[]{0.9f, 0.837f, 0.735f};
		final float[] BLACK = new float[]{0f, 0f, 0f};

		// Define and outline
		final Point2D.Float start = (Point2D.Float)source;
		final Point2D.Float right_bot = new Point2D.Float((float)start.getX() + WIDTH, (float)start.getY());
		final Point2D.Float right_top = new Point2D.Float((float)start.getX() + WIDTH, (float)start.getY() + HEIGHT);
		final Point2D.Float left_top = new Point2D.Float((float)start.getX(), (float)right_top.getY() - 0.05f);
		this.drawQuad(gl, start, right_bot, right_top, left_top, BEIGE);
		this.drawLine(gl, start, right_bot, BLACK);
		this.drawLine(gl, right_bot, right_top, BLACK);
		this.drawLine(gl, right_top, left_top, BLACK);
		this.drawLine(gl, left_top, start, BLACK);
	}

	/*
	 * Does the same thing as the above method, but
	 * reflects the fence post.
	 */
	private void drawReflectedFencePost(GL2 gl, Point2D source)
	{
		final float WIDTH = 0.075f;
		final float HEIGHT = 0.4f;

		final float[] BEIGE = new float[]{0.9f, 0.837f, 0.735f};
		final float[] BLACK = new float[]{0f, 0f, 0f};

		final Point2D.Float start = (Point2D.Float)source;
		final Point2D.Float right_bot = new Point2D.Float((float)start.getX() + WIDTH, (float)start.getY());
		final Point2D.Float right_top = new Point2D.Float((float)start.getX() + WIDTH, (float)start.getY() + HEIGHT - 0.05f);
		final Point2D.Float left_top = new Point2D.Float((float)start.getX(), (float)start.getY() + HEIGHT);
		this.drawQuad(gl, start, right_bot, right_top, left_top, BEIGE);
		this.drawLine(gl, start, right_bot, BLACK);
		this.drawLine(gl, right_bot, right_top, BLACK);
		this.drawLine(gl, right_top, left_top, BLACK);
		this.drawLine(gl, left_top, start, BLACK);
	}

	/* Draw a door
	 * @param gl: The GL context
	 * @param pos: The bottom left corner
	 */
	private void drawDoor(GL2 gl, Point2D pos)
	{
		// Define the dimensions
		final float HEIGHT = 0.25f;
		final float WIDTH = 0.13f;

		// Define the colors
		final float[] BLACK = new float[]{0f, 0f, 0f};
		final float[] LIGHT_BROWN = new float[]{205f/255f, 133f/255f, 63f/255f};
		final float[] LIGHT_GRAY = new float[]{0.75f, 0.75f, 0.75f};

		// Outline and draw the door
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
		this.drawCircle(gl, knob_center, 0.01f, 0.0f, 360f, LIGHT_GRAY, true);
	}

	/*
	 * Draw a quadralateral given four vertices and a fill color
	 */
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

	/*
	 * Draw a triangle given three vertices and a fill color
	 */
	private void drawTriangle(GL2 gl, Point2D one, Point2D two, Point2D three, float[] color)
	{
		gl.glBegin(gl.GL_TRIANGLES);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(one.getX(), one.getY());
		gl.glVertex2d(two.getX(), two.getY());
		gl.glVertex2d(three.getX(), three.getY());
		gl.glEnd();
	}

	/*
	 * Draw a quad given four vertices with two colors 
	 * forming a gradient
	 */
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

	/*
	 * Draw a line given two vertices and a color
	 */
	private void drawLine(GL2 gl, Point2D start, Point2D end, float[] color)
	{
		gl.glBegin(GL.GL_LINES);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(start.getX(), start.getY());
		gl.glVertex2d(end.getX(), end.getY());
		gl.glEnd();
	}

	/* 
	 * The same as above, but allowing for a width
	 */
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

	/* 
	 * The same as above, but allowing for an alpha as well
	 */
	private void drawLine(GL2 gl, Point2D start, Point2D end, float[] color, float width, float alpha)
	{
		gl.glLineWidth(width);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBegin(GL.GL_LINES);
		gl.glColor4f(color[0], color[1], color[2], alpha);
		gl.glVertex2d(start.getX(), start.getY());
		gl.glVertex2d(end.getX(), end.getY());
		gl.glEnd();
		gl.glLineWidth(1.0f);
	}

	/*
	 * Draws a circle with a center, radius, start and end degree marker, 
	 * fill color, and designate an outline if needed
	 */
	private void drawCircle(GL2 gl, Point2D center, float radius, double start, double end, float[] color, boolean outline)
	{
		final float[] BLACK = new float[]{0f, 0f, 0f};
		int len = 360;
		double[] vert = new double[(len+1)*2];
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(center.getX(), center.getY());
		double centx = center.getX();
		double centy = center.getY();
		for(double angle = start, i = 0; angle <= end; angle++, i+= 2)
		{
			double x = centx + Math.cos(Math.toRadians(angle))*radius;
			double y = centy + Math.sin(Math.toRadians(angle))*radius;
			gl.glVertex2d(x,y);
			vert[(int)i] = x;
			vert[(int)i+1] = y;
		}
		gl.glEnd();
		// If outline, do it
		if(outline)
		{
			for(int i = 0; i < vert.length - 2; i+=2)
			{
				final Point2D.Float from = new Point2D.Float((float)vert[i], (float)vert[i+1]);
				final Point2D.Float to = new Point2D.Float((float)vert[i+2], (float)vert[i+3]);
				this.drawLine(gl, from, to, BLACK);
			}
			final Point2D.Float from = new Point2D.Float((float)vert[vert.length - 2], (float)vert[vert.length - 1]);
			final Point2D.Float to = new Point2D.Float((float)vert[0], (float)vert[1]);
			this.drawLine(gl, from, to, BLACK);
		}
	}

	/*
	 * Overload of the above, allowing for an degree increment to draw polygons
	 */
	private void drawCircle(GL2 gl, Point2D center, float radius, double start, double end, int inc, float[] color, boolean outline)
	{
		final float[] BLACK = new float[]{0f, 0f, 0f};
		int len = 360 / inc;
		double[] vert = new double[(len+1)*2];
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(center.getX(), center.getY());
		double centx = center.getX();
		double centy = center.getY();
		for(double angle = start, i = 0; angle <= end; angle += inc, i+=2)
		{
			double x = centx + Math.cos(Math.toRadians(angle))*radius;
			double y = centy + Math.sin(Math.toRadians(angle))*radius;
			vert[(int)i] = x;
			vert[(int)i+1] = y;
			gl.glVertex2d(x,y);
		}
		gl.glEnd();
		if(outline)
		{
			for(int i = 0; i < vert.length - 2; i+=2)
			{
				final Point2D.Float from = new Point2D.Float((float)vert[i], (float)vert[i+1]);
				final Point2D.Float to = new Point2D.Float((float)vert[i+2], (float)vert[i+3]);
				this.drawLine(gl, from, to, BLACK);
			}
			final Point2D.Float from = new Point2D.Float((float)vert[vert.length - 2], (float)vert[vert.length - 1]);
			final Point2D.Float to = new Point2D.Float((float)vert[0], (float)vert[1]);
			this.drawLine(gl, from, to, BLACK);
		}
	}

	/*
	 * Compute the point to start the stacked hopscotch rect.
	 */
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
