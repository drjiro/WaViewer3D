/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: AbstractRenderer.java
 * 
 * Abstract renderer for 3D objects.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 * 
 */

package com.drjiro.viewer;

import java.awt.Graphics2D;
import java.awt.Image;

/**
 * Abstract renderer.
 * 
 * @author wada
 */
public abstract class AbstractRenderer {
	/** World matrix */
	protected Matrix4f worldMatrix = new Matrix4f();

	/** Width of the screen */
	protected int screenWidth;

	/** Height of the screen */
	protected int screenHeight;

	/**
	 * Constructor by width and height of the screen.
	 * 
	 * @param screenWidth
	 *            Width of the screen
	 * @param screenHeight
	 *            Height of the screen
	 */
	public AbstractRenderer(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	/**
	 * Set transform matrix.
	 * 
	 * @param m
	 *            transform matrix
	 */
	public final void setTransform(Matrix4f m) {
		worldMatrix = m;
	}

	/**
	 * Draw primitive.
	 * 
	 * @param obj
	 *            3D object
	 * @param camera
	 *            Camera object
	 * @param offg
	 *            Offscreen buffer
	 * @param nomal_inverse
	 *            true if inverting normals
	 */
	public abstract void render(Object3D obj, Camera camera, Graphics2D offg,
			boolean nomal_inverse);

	/**
	 * Set texture.
	 * 
	 * @param texture
	 *            Texture image object
	 */
	public abstract void setTexture(Image texture);

}
