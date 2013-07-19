/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: WaViewer3D.java
 * 
 * WebFront OBJ file viewer.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 */
package com.drjiro.viewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 * WebFront OBJ file viewer.
 * 
 * @author wada
 */
public class WadViewer3D extends JApplet {
	/** Serial vertion UID */
	private static final long serialVersionUID = 1L;

	/** Pi value */
	public static final float PI = 3.141519f;
	
	/** file name */
	private String fileName = "dice";

	/** 3D object */
	private Object3D obj;

	/** Camera of a scene */
	private Camera camera;

	/** Renderer */
	private AbstractRenderer renderer;

	/** Texture image */
	private BufferedImage texture;

	/** Shader type */
	private int shader = TEXTURE;

	/** Offscreen buffer */
	private BufferedImage offImage;

	/** Graphics2D object */
	private Graphics2D offg;

	/** Color of a canvas */
	private Color bgColor = Color.black;

	/** Width of a screen */
	private int screenWidth;

	/** Height of a screen */
	private int screenHeight;

	/** pre-rotation angle around X axis in radian */
	private int pre_x;

	/** pre-rotation angle around Y axis  in radian */
	private int pre_y;

	/** rotation phi angle in radian */
	private float phi = PI;

	/** rotation theta angle in radian  */
	private float theta = 0;

	/** Sensitivity of a mouse */
	private float sense = 0.01f;

	/** Invert normals or not */
	private boolean nomal_inverse = false;

	/** FPS */
	private float fps = 0;

	/** Counter for computing a FPS */
	private int count = 59;

	/** Timer for computing a FPS */
	private long time = System.currentTimeMillis();

	/** Flat shading flag */
	private static final int FLAT = 1;

	/** Texture mapping flag */
	private static final int TEXTURE = 2;

	/**
	 * Constructor.
	 * 
	 * @param fileName file name of the .OBJ file
	 */
	public WadViewer3D(String fileName) {
		this.fileName = fileName;

		camera = new Camera();
		
		this.addMouseListener(new MouseAdapter() {
			/**
			 * Mouse pressed. 
			 * 
			 * @param e mouse event
			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e) {
				pre_x = e.getX();
				pre_y = e.getY();
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			/**
			 * Mouse dragged.
			 * 
			 * @param e mouse event
			 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
			 */
			public void mouseDragged(MouseEvent e) {
				int dx = e.getX() - pre_x;
				int dy = e.getY() - pre_y;

				phi += dx * sense;
				theta += dy * sense;

				obj.setRot(new Vector3f(theta, phi, 0.0f));
				repaint();

				pre_x = e.getX();
				pre_y = e.getY();
			}
		});

		// Keyboard event.
		this.addKeyListener(new KeyAdapter() {
			/**
			 * Specify the invertion of the normals when using a flat shading.
			 * 
			 * @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
			 */
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == 'n' || key == 'N') {
					if (nomal_inverse) {
						nomal_inverse = false;
					} else {
						nomal_inverse = true;
					}
				}
			}
		});
	}

	/**
	 * <p>
	 * Initialize an applet.
	 * </p>
	 * Get the screen size when invoking the applet.
	 * 
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		// Get the screen width and height.
		screenWidth = getWidth();
		screenHeight = getHeight();
		pre_x = screenWidth / 2;
		pre_y = screenHeight / 2;

		try {
			if (getParameter("file") != null) {
				fileName = getParameter("file");
			}
		} catch (Exception e) {
			// Nothing to do.
		}

		count = 0;
		fps = 0;
		time = 0;

		if (shader == FLAT) {
			renderer = new FlatRenderer(screenWidth, screenHeight);
		} else {
			renderer = new TextureRenderer(screenWidth, screenHeight);
		}

		// Create offscreen buffer.
		offImage = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_BGR);

		offg = offImage.createGraphics();

		clearImage();

		// Create a 3D object.
		obj = new Object3D();
		obj.setRot(new Vector3f(theta, phi, 0.0f));

		try {
			// Load a .OBJ object.
			obj.load(new URL(getCodeBase(), fileName + ".obj"));
		} catch (MalformedURLException e) {
			// e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			try {
				obj.load(new FileInputStream(fileName + ".obj"));
			} catch (FileNotFoundException e1) {
				System.exit(1);
			} catch (IOException e1) {
				System.exit(1);
			}
		}

		// Load a texture.
		try {
			texture = ImageIO.read(new URL(getCodeBase(), fileName + ".jpg"));
			// Set a texture to the renderer.
			renderer.setTexture(texture);
		} catch (Exception e) {
			try {
				texture = ImageIO.read(new FileInputStream(fileName + ".jpg"));
				// Set a texture to the renderer.
				renderer.setTexture(texture);
			} catch (FileNotFoundException e1) {
				System.exit(1);
			} catch (IOException e1) {
				System.exit(1);
			}
		}
	}

	/**
	 * @see java.awt.Component#update(java.awt.Graphics)
	 */
	public void update(Graphics g) {
		paint(g);
	}

	/**
	 * Paint handler.
	 * 
	 * @param g2 Graphics2D object
	 */
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		drawModel(offg);
		// Draw the offscreen image to the real screen.
		g2.drawImage(offImage, 0, 0, this);
	}

	/**
	 * <p>
	 * Clear back buffer image.
	 * </p>
	 * Using only a flat shading.
	 */
	public void clearImage() {
		offg.setColor(bgColor);
		offg.fillRect(0, 0, screenWidth, screenHeight);
	}

	/**
	 * Draw the model.
	 * 
	 * @param g2 Graphics2D object
	 */
	public void drawModel(Graphics2D g2) {
		// Create a matrix
		Matrix4f m = new Matrix4f();
		// Identity the matrix
		m.identify();
		float scale = 1.0f / (obj.getRadius() * 2);
		// Scale the matrix
		m.scale(screenWidth * scale, screenWidth * scale, screenWidth * scale);
		if (obj.rot != null) {
			m.rotateX(obj.rot.x); // Rotate around the X axis
			m.rotateY(obj.rot.y); // Rotate around the Y axis
			m.rotateZ(obj.rot.z); // Rotate around the Z axis
		}
		int dx = (int) (screenWidth / 2 - (obj.getCenter().x * screenWidth * scale));
		int dy = (int) (screenHeight / 2 - (obj.getCenter().y * screenWidth * scale));
		m.translate(dx, dy, 0); // Transform to the center of the screen

		renderer.setTransform(m);

		renderer.render(obj, camera, offg, nomal_inverse);
	}

	/**
	 * Draw a frame rate.
	 * 
	 * @param gg2 Graphics2D object
	 */
	public void frameRate(Graphics2D g2) {
		if (++count == 60) {
			fps = 1000 / (float) ((System.currentTimeMillis() - time) / 60);
			time = System.currentTimeMillis();
			count = 0;
		}
		g2.setColor(Color.black);
		g2.drawString("FPS " + fps, 0, 16);
		g2.drawString("VTX " + obj.getVertexCount(), 0, 32);
	}

	/**
	 * Set a shader.
	 * 
	 * @param shader shader type
	 */
	public void setShader(int shader) {
		this.shader = shader;
	}

	/**
	 * Entry point of this application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1 && args.length != 2) {
			System.err.println("Usage: com.drjiro.viewer.WaViewer3D filename");
			System.exit(1);
		}
		final JFrame frame = new JFrame("WaViewer3D");

		final WadViewer3D applet = new WadViewer3D(args[0]);

		if (args.length == 2) {
			if (args[1].equals("FLAT")) {
				applet.setShader(WadViewer3D.FLAT);
			}
		}
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});

		// Event of a component.
		frame.addComponentListener(new ComponentAdapter() {
			/**
			 * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
			 */
			public void componentResized(ComponentEvent e) {
				frame.setSize(400, 400);
			}
		});
		applet.setSize(400, 400);
		frame.getContentPane().add("Center", applet);
		applet.init();
		applet.start();
		frame.pack();
		frame.setSize(400, 400);
		frame.setVisible(true);
	}
}
