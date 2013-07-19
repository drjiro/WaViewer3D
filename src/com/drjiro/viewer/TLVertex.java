/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: TLVertex.java
 * 
 * Transform and lit vertex.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 */
package com.drjiro.viewer;

/**
 *Transform and lit vertex.
 * 
 * @author wada
 */
public class TLVertex {
	/** X coordinate */
	public int x;

	/** Y coordinate */
	public int y;

	/** Z coordinate */
	public int z;

	/** Texture coordinate U */
	public int u;

	/** Texture coordinate V */
	public int v;

	/** Vertex color R */
	public int r;

	/** Vertex color G */
	public int g;

	/** Vertex color B */
	public int b;

	/** Active or not */
	public int active;

	/**
	 * Dump contents.
	 */
	public String toString() {
		return "(" + (x >> 16) + "," + (y >> 16) + "," + (z >> 16)
		+ "," + (u >> 16) + "," + (v >> 16)
		+ "," + (r >> 16) + "," + (g >> 16) + "," + (b >> 16)
		+ ")";
	}
}
