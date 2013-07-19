/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: Face.java
 * 
 *A face which is only adopted for triangles.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 */

package com.drjiro.viewer;

/**
 * A face which is only adopted for triangles.
 * 
 * @author wada
 */
public class Face {
	/** Vertices */
	public Vertex[] vertices;

	/** Depth */
	public float depth;

	/**
	 * Constructor by 3 vertices.
	 * 
	 * @param v1 a vertex
	 * @param v2 a vertex
	 * @param v3 a vertex
	 */
	public Face(Vertex v1, Vertex v2, Vertex v3) {
		vertices = new Vertex[3];
		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;
	}

	/**
	 * Retrieve the mean depth for Z-sorting.
	 * 
	 * @return depth
	 */
	public float getDepth() {
		return (vertices[0].v.z + vertices[1].v.z + vertices[2].v.z) / 3;
	}

	/**
	 * Dump contents.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + vertices[0] + "," + vertices[1] + "," + vertices[2] + ")";
	}
}
