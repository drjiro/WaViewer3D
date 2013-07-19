/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: FlatRenderer.java
 * 
 * A renderer by a flat shading.
 * Not implemented texture mapping, sorry.
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 */
package com.drjiro.viewer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

/**
 * A renderer by a flat shading.
 * 
 * @author wada
 */
public class FlatRenderer extends AbstractRenderer {
	/** X coordinate of the center */
	private int centerX;

	/** Y coordinate of the center */
	private int centerY;

	/**
	 * Constructor by width and height of the screen.
	 * 
	 * @param screenWidth
	 *            Width of the screen
	 * @param screenHeight
	 *            Height of the screen
	 */
	public FlatRenderer(int screenWidth, int screenHeight) {
		super(screenWidth, screenHeight);
		centerX = screenWidth / 2;
		centerY = screenHeight / 2;
	}

	/**
	 * ÉÇÉfÉãÇï`âÊÇ∑ÇÈÅB
	 * 
	 * @param obj
	 *            3D object
	 * @param camera
	 *            Camera for the scene
	 * @param offg
	 *            Offscreen buffer
	 * @param nomal_inverse
	 *            true if inverting normals
	 */
	public void render(Object3D obj, Camera camera, Graphics2D offg,
			boolean nomal_inverse) {
		int px[] = new int[3];
		int py[] = new int[3];
		int count = 0;

		List faces = obj.getFaces();

		int tmp[] = new int[faces.size()];

		float tmp_depth[] = new float[faces.size()];

		offg.setColor(Color.black);

		// Create a transposed matrix as an inverse matrix.
		Matrix4f im = new Matrix4f();
		im.invert(worldMatrix);

		// Get the direction of a light.
		Vector3f light = im.transform(new Vector3f(-0.2f, 0.2f, -1.0f));
		light.normalize();

		for (int i = 0; i < faces.size(); i++) {
			Face face = (Face) faces.get(i);

			// Vertices transform by matrices.
			Vector3f v1 = worldMatrix.transform(face.vertices[0].v);
			Vector3f v2 = worldMatrix.transform(face.vertices[1].v);
			Vector3f v3 = worldMatrix.transform(face.vertices[2].v);

			if (face.vertices[0].n == null) {
				float a1 = v2.x - v1.x;
				float a2 = v2.y - v1.y;
				float a3 = v2.z - v1.z;
				float b1 = v3.x - v2.x;
				float b2 = v3.x - v2.x;
				float b3 = v3.x - v2.x;

				// Compute normals.
				float nx = a2 * b3 - a3 * b2;
				float ny = a3 * b1 - a1 * b3;
				float nz = a1 * b2 - a2 * b1;

				if (nomal_inverse) {
					nx = -nx;
					ny = -ny;
					nz = -nz;
				}

				face.vertices[0].n.x = nx;
				face.vertices[0].n.y = ny;
				face.vertices[0].n.z = nz;
			}
			if (face.vertices[0].n.z < 0) {
				tmp[count] = i;
				tmp_depth[count] = face.getDepth();
				count++;
			}
		}

		// Z-sorting.
		float t;
		int ti;
		int lim = count - 1;

		do {
			int m = 0;
			for (int n = 0; n <= lim - 1; n++) {
				if (tmp_depth[n] < tmp_depth[n + 1]) {
					t = tmp_depth[n];
					tmp_depth[n] = tmp_depth[n + 1];
					tmp_depth[n + 1] = t;
					ti = tmp[n];
					tmp[n] = tmp[n + 1];
					tmp[n + 1] = ti;
					m = n;
				}
			}
			lim = m;
		} while (lim != 0);

		// Compute a color using HSB color system.
		int B;
		float len;

		for (int m = 0; m < count; m++) {
			int i = tmp[m];

			Face face = (Face) faces.get(i);
			len = (float) Math.sqrt(face.vertices[0].n.x * face.vertices[0].n.x
					+ face.vertices[0].n.y * face.vertices[0].n.y
					+ face.vertices[0].n.z * face.vertices[0].n.z);

			B = (int) (75 - 180 * face.vertices[0].n.z / len);

			if (B < 0)
				B = 0;
			if (B > 255)
				B = 255;

			Color cc;
			cc = Color.getHSBColor((float) 0.2, (float) 0.5, (float) B / 255);

			offg.setColor(cc);

			for (int j = 0; j < 3; j++) {
				px[j] = (int) (face.vertices[j].v.x + centerX);
				py[j] = (int) (-face.vertices[j].v.y + centerY);
			}

			offg.fillPolygon(px, py, 3);
		}
	}

	/**
	 * Set texture.
	 * 
	 * @param texture
	 *            Texture image object.
	 */
	public final void setTexture(Image texture) {
	}
}
