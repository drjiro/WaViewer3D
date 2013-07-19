/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: Object3D.java
 * 
 * A 3D object using WebFront .OBJ file.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 */

package com.drjiro.viewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A 3D object using WebFront .OBJ file.
 * 
 * @author wada
 */
public class Object3D {
	/** Vertex list */
	protected List vertices;

	/** UV coordinate list */
	protected List uvs;

	/** Normal vector list */
	protected List normals;

	/** Face index list */
	protected List faces;

	/** Rotation vector list */
	protected Vector3f rot;

	/** File name to load */
	protected String fileName;

	/** URL */
	protected URL url;

	/** Center of the object */
	protected Vector3f center = new Vector3f();
	
	/** Scaling factor to view */
	protected float scale = 10.0f;

	float radius = 0.000001f; // temporary minimum value
	
	/**
	 * Deafult constructor.
	 */
	public Object3D() {
		vertices = new ArrayList();
		uvs = new ArrayList();
		normals = new ArrayList();
		faces = new ArrayList();
	}

	/**
	 * Load an 3D object from a .OBJ file specified by a URL.
	 * 
	 * @param url URL to load a object
	 */
	public void load(URL url) throws IOException {
		load(url.openStream());
	}

	/**
	 * Load an 3D object from a stream.
	 * 
	 * @param is input stream
	 * @throws IOException
	 */
	public void load(InputStream is) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));

			float totalx = 0.0f;
			float totaly = 0.0f;
			float totalz = 0.0f;
			int totaln = 0;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0 || line.startsWith("#") || line.startsWith(" ")) {
					// Skip blank line or start width '#'.
					continue;
				}
				StringTokenizer st = new StringTokenizer(line);
				// Get a next token.
				String token = st.nextToken();
				if (token.equals("v")) {
					// Vertex coordinates
					// ex. v -0.238906 0.610127 0.538036
					float x = Float.parseFloat(st.nextToken());
					float y = Float.parseFloat(st.nextToken());
					float z = Float.parseFloat(st.nextToken());
					// Add a vertex to the list.
					vertices.add(new Vector3f(x, y, z));
					if (Math.abs(x) > radius)
						radius = Math.abs(x);
					if (Math.abs(y) > radius)
						radius = Math.abs(y);
					if (Math.abs(z) > radius)
						radius = Math.abs(z);
					totalx += x;
					totaly += y;
					totalz += z;
					totaln++;

//					System.out.println("vertex x=" + x + " y=" + y + " z=" + z);
				} else if (token.equals("vt")) {
					// UV coordinate.
					// ex. vt 0.213384 0.643933 0.000000
					float u = Float.parseFloat(st.nextToken());
					float v = Float.parseFloat(st.nextToken());
					float w = Float.parseFloat(st.nextToken());
					// Add UV coordinate to the list.
					uvs.add(new Vector3f(u, v, w));
//					System.out.println("uv u=" + u + " v=" + v + " w=" + w);
				} else if (token.equals("vn")) {
					// Normal vector
					// ex. vn -0.237367 -0.151809 0.945690
					float x = Float.parseFloat(st.nextToken());
					float y = Float.parseFloat(st.nextToken());
					float z = Float.parseFloat(st.nextToken());
					// Add a normal vector to the list.
					normals.add(new Vector3f(x, y, z));
//					System.out.println("in v=" + x + " t=" + y + " n=" + z);
				} else if (token.equals("f")) {
					// Face indices vertex/UV/normal
					// ex. f 149/149/149 155/155/155 2/2/2
					token = st.nextToken();
					if (token.indexOf("/") >= 0) {
						// First vertex
						String[] args = token.split("/");
						int iv0 = Integer.parseInt(args[0]) - 1;
						int it0 = Integer.parseInt(args[1]) - 1;
						int in0 = 0;
						if (args.length > 2) {
							in0 = Integer.parseInt(args[2]) - 1;
						}
						// 2nd vertex
						args = st.nextToken().split("/");
						int iv1 = Integer.parseInt(args[0]) - 1;
						int it1 = Integer.parseInt(args[1]) - 1;
						int in1 = 0;
						if (args.length > 2) {
							in1 = Integer.parseInt(args[2]) - 1;
						}
						// 3rd vertex
						args = st.nextToken().split("/");
						int iv2 = Integer.parseInt(args[0]) - 1;
						int it2 = Integer.parseInt(args[1]) - 1;
						int in2 = 0;
						if (args.length > 2) {
							in2 = Integer.parseInt(args[2]) - 1;
						}
						// Add face to the list.
						Face face = null;
						if (args.length > 2) {
							face = new Face(
									new Vertex((Vector3f)vertices.get(iv0), (Vector3f)uvs.get(it0), (Vector3f)normals.get(in0)),
									new Vertex((Vector3f)vertices.get(iv1), (Vector3f)uvs.get(it1), (Vector3f)normals.get(in1)),
									new Vertex((Vector3f)vertices.get(iv2), (Vector3f)uvs.get(it2), (Vector3f)normals.get(in2)));
						}
						else {
							face = new Face(
									new Vertex((Vector3f)vertices.get(iv0), (Vector3f)uvs.get(it0)),
									new Vertex((Vector3f)vertices.get(iv1), (Vector3f)uvs.get(it1)),
									new Vertex((Vector3f)vertices.get(iv2), (Vector3f)uvs.get(it2)));
						}
						/*
						Face face = new Face(new Index3(iv0, it0, in0), new Index3(iv1, it1, in1),
								new Index3(iv2, it2, in2));
						*/
						faces.add(face);
//						System.out.println("face=" + face);
					} else {
						int iv0 = Integer.parseInt(token) - 1;
						int iv1 = Integer.parseInt(st.nextToken()) - 1;
						int iv2 = Integer.parseInt(st.nextToken()) - 1;
						Face face = new Face(
								new Vertex((Vector3f)vertices.get(iv0)),
								new Vertex((Vector3f)vertices.get(iv1)),
								new Vertex((Vector3f)vertices.get(iv2)));
						faces.add(face);
					}
				}
			}
			if (totaln > 0) {
				center.x = totalx / totaln;
				center.y = totaly / totaln;
				center.z = totalz / totaln;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get the face list.
	 * 
	 * @return return the face list
	 */
	public List getFaces() {
		return faces;
	}

	/**
	 * Get the vertex list.
	 * 
	 * @return return the vertex list
	 */
	public List getVertices() {
		return vertices;
	}

	/**
	 * Get the UV list.
	 * 
	 * @return return the UV list
	 */
	public List getUvs() {
		return uvs;
	}

	/**
	 * Get the normal vector list.
	 * 
	 * @return return the normal vector list
	 */
	public List getNormals() {
		return normals;
	}

	/**
	 * Get the vertex count.
	 * 
	 * @return vertex count
	 */
	public int getVertexCount() {
		return vertices.size();
	}

	/**
	 * Get the rotation data
	 * 
	 * @return return rotation data
	 */
	public Vector3f getRot() {
		return rot;
	}

	/**
	 * Set the rotation data
	 * 
	 * @param rot rotation data
	 */
	public void setRot(Vector3f rot) {
		this.rot = rot;
	}

	/**
	 * Get the bounding radius of this object.
	 * 
	 * @return the bounding radius of this object
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * Get the center of this object.
	 * 
	 * @return return the center of this object
	 */
	public Vector3f getCenter() {
		return center;
	}

}
