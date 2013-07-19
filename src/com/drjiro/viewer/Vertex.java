/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: Vertex.java
 * 
 * Vertex.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 */
package com.drjiro.viewer;

/**
 * Vertex.
 * 
 * @author wada
 */
public class Vertex {
	/** Vertex coordinate */
	public Vector3f v;

	/** Normal vector */
	public Vector3f n;

	/** Texture coordinate */
	public Vector3f uv;

	/** Used time */
	private int used;

	/**
	 * Default constructor.
	 */
	public Vertex() {
		n.x = 0;
		n.y = 0;
		n.z = 0;
		used = 0;
	}

	/**
	 * A constructor using a vertex, a texture coordinate and a normal vector.
	 * 
	 * @param v vertex
	 * @param uv texture coordinate
	 * @param n normal vector
	 */
	public Vertex(Vector3f v, Vector3f uv, Vector3f n) {
		this.v = v;
		this.uv = uv;
		this.n = n;
	}
	
	/**
	 * A constructor using a vertex, a texture coordinate.
	 * 
	 * @param v vertex
	 * @param uv texture coordinate
	 */
	public Vertex(Vector3f v, Vector3f uv) {
		this(v, uv, new Vector3f());
	}

	/**
	 * A constructor using a vertex.
	 * 
	 * @param v vertex
	 */
	public Vertex(Vector3f v) {
		this(v, new Vector3f(), new Vector3f());
	}

	/**
	 * A constructor using 3 coordinates.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 */
	public Vertex(float x, float y, float z) {
		v.x = x;
		v.y = y;
		v.z = z;
	}

	/**
	 * A constructor using 3 coordinates.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param nx normal vector X value
	 * @param ny normal vector Y value
	 * @param nz normal vector Z value
	 */
	public Vertex(float x, float y, float z, float nx, float ny, float nz) {
		v.x = x;
		v.y = y;
		v.z = z;
		n.x = nx;
		n.y = ny;
		n.z = nz;
		used = 0;
	}
	
	/**
	 * Increments the used time.
	 */
	public void addUsed() {
		used++;
	}

	/**
	 * Get the used time.
	 * 
	 * @return used time
	 */
	public int getUsed() {
		return used;
	}

	/**
	 * Dump contents.
	 */
	public String toString() {
		return "(v=" + v + ",uv=" + uv + ",n=" + n + ")";
	}
}
