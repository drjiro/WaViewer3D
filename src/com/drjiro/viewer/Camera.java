/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: Camera.java
 * 
 * Camera for a 3D scene.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 */

package com.drjiro.viewer;

/**
 * @author wada
 * @version $Revision: $ $Date: $
 */
public class Camera {
	/** Eye point */
	private Vector3f eyePoint;
	
	/**
	 * Default constructor.
	 */
	public Camera() {
		eyePoint = new Vector3f(0, 0, -1);
	}

	/**
	 * Get the eye point.
	 * 
	 * @return return the eye point
	 */
	public Vector3f getEyePoint() {
		return eyePoint;
	}

	/**
	 * Set the eye point.
	 * 
	 * @param eyePoint the eye point to set
	 */
	public void setEyePoint(Vector3f eyePoint) {
		this.eyePoint = eyePoint;
	}
}
