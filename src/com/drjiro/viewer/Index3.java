/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: Index3.java
 * 
 * 3 indices.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 */
package com.drjiro.viewer;

/**
 * 3 indices.
 * 
 * @author wada
 */
public class Index3 {
	/** The index for a vertex */
	int v;

	/** The index for a texture */
	int t;

	/** The index for a normal */
	int n;

	/**
	 * Constructor.
	 */
	Index3() {
	}

	/**
	 * Constructor by 3 indices.
	 * 
	 * @param v
	 *            index for a vertex
	 * @param t
	 *            index for a texture
	 * @param n
	 *            index for a normal
	 */
	Index3(int v, int t, int n) {
		this.v = v;
		this.t = t;
		this.n = n;
	}

	/**
	 * Dump contents.
	 */
	public String toString() {
		return "(" + v + "," + t + "," + n + ")";
	}
}
