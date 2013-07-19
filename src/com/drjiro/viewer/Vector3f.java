/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: Vertex3f.java
 * 
 * 3 dimension vector.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 */
package com.drjiro.viewer;

/**
 * 3 dimension vector.
 * 
 * @author wada
 */
public class Vector3f {
	/** X coordinate */
	public float x;

	/** Y coordinate */
	public float y;

	/** Z coordinate */
	public float z;

	/**
	 * Default constructor.
	 */
	public Vector3f() {
	}

	/**
	 * A constructor using 3 coordinates.
	 * 
	 * @param vx X coordinate
	 * @param vy Y coordinate
	 * @param vz Z coordinate
	 */
	public Vector3f(float vx, float vy, float vz) {
		x = vx;
		y = vy;
		z = vz;
	}

	/**
	 * Initialize this vector by 3 coordinates.
	 * 
	 * @param v 3 coordinates
	 */
	public void initialize(Vector3f v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	/**
	 * Add a vector to this vector.
	 * 
	 * @param v
	 */
	public void add(Vector3f v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	/**
	 * Normalize this vector.
	 */
	public void normalize() {
		// 
		float _l = (float) (1 / Math.sqrt(x * x + y * y + z * z));
		x *= _l;
		y *= _l;
		z *= _l;
	}

	/**
	 * Dot product a vector to this vector.
	 * 
	 * @param v vector
	 * @return dot product value
	 */
	public float dotProduct(Vector3f v) {
		return (x * v.x + y * v.y + z * v.z);
	}

	/**
	 * Cross product a vector to this vector.
	 * 
	 * @param a vector
	 * @param b vector
	 */
	public void crossProduct(Vector3f a, Vector3f b) {
		x = a.y * b.z - a.z * b.y;
		y = a.z * b.x - a.x * b.z;
		z = a.x * b.y - a.y * b.x;
	}

	/**
	 * Invert this vector.
	 */
	public void invert() {
		x = -x;
		y = -y;
		z = -z;
	}

	/**
	 * Dump contents.
	 */
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}
}
