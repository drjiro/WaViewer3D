/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: SimpleShape.java
 * 
 * Simple shape.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 * 
 */

package com.drjiro.viewer;

/**
 * Create a simple shapes for testing.
 * 
 * @author wada
 */
public class SimpleShape extends Object3D {
	/**
	 * Create doughnut.
	 * 
	 * @param n
	 */
	public void createDonuts(int n) {
		float a = 0;
		float c = 0;
		float addc = (float) Math.PI * 2 / n;

		for (int i = 0; i < n; i++) {
			float s = (float) Math.sin(c) + 2;
			float z = (float) Math.cos(c);
			for (int j = 0; j < n; j++) {
				float x = (float) (Math.cos(a));
				float y = (float) (Math.sin(a));
				Vector3f vertex = new Vector3f(x * s * 0.4f, y * s * 0.4f,
						z * 0.4f);
				vertices.add(vertex);
				a += Math.PI / n * 2;
			}
			c += addc;
		}

		int m = n * n;
		for (int j = 0; j < n; j++) {
			int off = j * n;
			for (int i = 0; i < n; i++) {
				Face face = new Face(new Vertex(
						(Vector3f) vertices.get((off + n) % m + (i + 1) % n)),
						new Vertex((Vector3f) vertices.get(off + (i + 1) % n)),
						new Vertex((Vector3f) vertices.get(off + i)));
				faces.add(face);

				face = new Face(new Vertex((Vector3f) vertices.get((off + n)
						% m + i)), new Vertex((Vector3f) vertices.get((off + n)
						% m + (i + 1) % n)), new Vertex(
						(Vector3f) vertices.get(off + i)));
				faces.add(face);
			}
		}

		// Average normal vectors for a Gouraud shading.
		for (int i = 0; i < faces.size(); i++) {
			Face face = (Face) faces.get(i);
			// Compute normal vector.
			Vector3f vn = calcNormal(face.vertices[0].v, face.vertices[1].v,
					face.vertices[2].v);
			if (face.vertices[0].n == null) {
				face.vertices[0].n = vn;
			} else {
				face.vertices[0].n.add(vn);
			}
			vn = calcNormal(face.vertices[0].v, face.vertices[1].v,
					face.vertices[2].v);
			if (face.vertices[1].n == null) {
				face.vertices[1].n = vn;
			} else {
				face.vertices[0].n.add(vn);
			}
			vn = calcNormal(face.vertices[0].v, face.vertices[1].v,
					face.vertices[2].v);
			if (face.vertices[2].n == null) {
				face.vertices[2].n = vn;
			} else {
				face.vertices[2].n.add(vn);
			}
			face.vertices[0].addUsed();
			face.vertices[1].addUsed();
			face.vertices[2].addUsed();
			for (int j = 0; j < face.vertices.length; j++) {
				face.vertices[j].uv.x = face.vertices[j].v.x / 2 + 0.5f;
				face.vertices[j].uv.y = face.vertices[j].v.y / 2 + 0.5f;
			}
		}
	}

	/**
	 * Compute normal vector.
	 * 
	 * @param v1
	 *            vertex
	 * @param v2
	 *            vertex
	 * @param v3
	 *            vertex
	 * @return normal vector
	 */
	private Vector3f calcNormal(Vector3f v1, Vector3f v2, Vector3f v3) {
		Vector3f n = new Vector3f();
		Vector3f a = new Vector3f();
		Vector3f b = new Vector3f();

		// Create a and b vectors from 3 coordinates.
		a.x = v1.x - v2.x;
		a.y = v1.y - v2.y;
		a.z = v1.z - v2.z;
		b.x = v1.x - v3.x;
		b.y = v1.y - v3.y;
		b.z = v1.z - v3.z;

		// Compute normal vector by a cross product.
		n.crossProduct(a, b);

		// Normalize the normal vector.
		n.normalize();

		return n;
	}
}
