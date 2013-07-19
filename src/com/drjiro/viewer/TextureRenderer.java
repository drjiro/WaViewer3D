/*
 * Project: WaViewer3D
 * Version: 1.0
 * File: TextureRenderer.java
 * 
 * A renderer using texture mappings. 
 * 
 * Copyright(C) 2006-2013 Takao WADA. All rights reserved.
 */
package com.drjiro.viewer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.Iterator;
import java.util.List;

/**
 * A renderer using texture mappings. 
 * 
 * @author wada
 */
public class TextureRenderer extends AbstractRenderer {
	/** 32bit pixel buffer */
	private int pbuf[];

	/** 32bit Z buffer */
	private int zbuf[];

	/** 32bit texture buffer */
	private int tbuf[];

	/** Width of a texture */
	private int textureWidth;

	/** Height of a texture */
	private int textureHeight;

	/** Mask for the width of a texture */
	private int textureWidthMask;

	/** Mask for the height of a texture */
	private int textureHeightMask;

	/** Shift for the texture */
	private int textureShift;

	/** Camera of a scene */
	private Camera camera;

	/** minimum index of the array */
	private int min[];

	/** maximum index of the array */
	private int max[];

	/** Z coordinate minimum value array */
	private int minz[];

	/** Z coordinate maximum value array */
	private int maxz[];

	/** Color red minimum value array */
	private int minr[];

	/** Color red maximum value array */
	private int maxr[];

	/** Color green minimum value array */
	private int ming[];

	/** Color green maximum value array */
	private int maxg[];

	/** Color blue minimum value array */
	private int minb[];

	/** Color blue maximum value array */
	private int maxb[];

	/** UV coordinate U minimum value array */
	private int minu[];

	/** UV coordinate U maximum value array */
	private int maxu[];

	/** UV coordinate V minimum value array */
	private int minv[];

	/** UV coordinate V maximum value array */
	private int maxv[];

	/** Polygon using primitives flag */
	public static final int PRIMITIVE_POLYGON = 1;
	
	/**
	 * Constructor by width and height of the screen.
	 * 
	 * @param screenWidth
	 *            Width of the screen
	 * @param screenHeight
	 *            Height of the screen
	 */
	public TextureRenderer(int screenWidth, int screenHeight) {
		super(screenWidth, screenHeight);

		pbuf = new int[screenWidth * screenHeight];
		zbuf = new int[screenWidth * screenHeight];
		min = new int[screenHeight];
		max = new int[screenHeight];
		minz = new int[screenHeight];
		maxz = new int[screenHeight];
		minr = new int[screenHeight];
		maxr = new int[screenHeight];
		ming = new int[screenHeight];
		maxg = new int[screenHeight];
		minb = new int[screenHeight];
		maxb = new int[screenHeight];
		minu = new int[screenHeight];
		maxu = new int[screenHeight];
		minv = new int[screenHeight];
		maxv = new int[screenHeight];
	}

	/**
	 * Set a texture to a object.
	 * 
	 * @param texture a texture to set
	 */
	public final void setTexture(Image texture) {
		textureWidth = texture.getWidth(null);
		textureHeight = texture.getHeight(null);
		textureWidthMask = textureWidth - 1;
		textureHeightMask = textureHeight - 1;

		// Compute shift value
		textureShift = 0;
		for (int i = 0; i < 32; i++) {
			if (((textureWidthMask >> i) & 1) > 0) {
				textureShift++;
			}
		}

		// Create a texture buffer.
		tbuf = new int[textureWidth * textureHeight];

		// Create a pixel grabber.
		PixelGrabber pg = new PixelGrabber(texture, 0, 0, textureWidth, textureHeight, tbuf, 0,
				textureWidth);

		try {
			// Get a pixel from a texture.
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clear buffers.
	 */
	public final void clearBuffer() {
		for (int i = 0; i < screenWidth * screenHeight; i++) {
			pbuf[i] = 0xFF000000;		// •
			zbuf[i] = Integer.MAX_VALUE;
		}
	}

	/**
	 * Draw primitive.
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
	public final void render(Object3D obj, Camera camera, Graphics2D offg, boolean nomal_inverse) {
		this.camera = camera;
		render(PRIMITIVE_POLYGON, offg, obj);
	}
	
	/**
	 * Draw primitive.
	 * 
	 * @param type The type of a polygon
	 * @param offg
	 *            Offscreen buffer
	 * @param obj
	 *            3D object
	 */
	public final void render(int type, Graphics2D offg, Object3D obj) {
		// Get face list.
		List faces = obj.getFaces();
		
		// Clear buffers.
		clearBuffer();

		switch (type) {
		case PRIMITIVE_POLYGON:
			Iterator iter = faces.iterator();
			while (iter.hasNext()) {
				Face face = (Face)iter.next();
				// Transform and lighting.
				TLVertex[] tlvertices = transformAndLighting(face);
				drawPolygon(tlvertices[0], tlvertices[1], tlvertices[2]);
			}
			break;
		}
		// Create image from a buffer.
		Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(screenWidth, screenHeight, pbuf, 0, screenWidth));
		// Draw an image to the offscreen buffer.
		offg.drawImage(image, 0, 0, null);
	}

	/**
	 * Transform and lighting.
	 * 
	 * @param faces face list
	 * @return transform and lit vertices
	 */
	private TLVertex[] transformAndLighting(Face face) {
		// transform and lit vertices
		TLVertex[] tlvertices = new TLVertex[face.vertices.length];

		// Lights.
		Vector3f ambient = new Vector3f(0.5f, 0.5f, 0.5f);
		Vector3f diffuse = new Vector3f(0.9f, 0.9f, 0.9f);

		// Create a transposed matrix instead of a inverse matrix.
		Matrix4f im = new Matrix4f();
		im.invert(worldMatrix);

		// Get the direction of the light.
		Vector3f light = im.transform(new Vector3f(-0.2f, 0.2f, -1.0f));
		light.normalize();

		// Get eye vector from a camera.
		Vector3f eye = im.transform(camera.getEyePoint());

		int r = 0, g = 0, b = 0;

		Vertex[] vertices = face.vertices;
		for (int i = 0; i < vertices.length; i++) {
			Vertex vertex = vertices[i];
			// Do transform
			Vector3f v = worldMatrix.transform(vertex.v);
//			System.out.println("vertex=" + vertex + " v=" + v);
			// Compute a shade using Lambert's law
			float w = light.dotProduct(vertex.n);

			// Compute colors of vertex.
			r = (int) ((ambient.x + w * diffuse.x) * 255);
			g = (int) ((ambient.y + w * diffuse.y) * 255);
			b = (int) ((ambient.z + w * diffuse.z) * 255);
			if (r < 0) {
				r = 0;
			}
			if (g < 0) {
				g = 0;
			}
			if (b < 0) {
				b = 0;
			}

			// Set information of a vertex
			TLVertex tlvertex = new TLVertex();
			tlvertex.x = (int) (v.x * 0x10000);
			tlvertex.y = (int) (v.y * 0x10000);
			tlvertex.z = (int) (v.z * 0x10000);
			tlvertex.r = r * 0x10000;
			tlvertex.g = g * 0x10000;
			tlvertex.b = b * 0x10000;

			// Set a texture coordinate.
			tlvertex.u = (int) (vertex.uv.x * textureWidth * 0x10000);
			tlvertex.v = (int) (vertex.uv.y * textureHeight * 0x10000);
			float e = eye.dotProduct(vertex.n);
//			System.out.println("u=" + vertex.uv + " TL=" + tlvertex + " e=" + e);
			if (e < 0) {
				tlvertex.active = 0;
			}
			else {
				tlvertex.active = 1;
			}
			tlvertices[i] = tlvertex;
		}

		return tlvertices;
	}

	/**
	 * Draw a polygon.
	 * 
	 * @param v1 transformed and lit vertex
	 * @param v2 transformed and lit vertex
	 * @param v3 transformed and lit vertex
	 */
	private final void drawPolygon(TLVertex v1, TLVertex v2, TLVertex v3) {
//		System.out.println("drawPolygon v1.active=" + v1.active + " v2.active=" + v2.active+ " v3.active=" + v3.active);
		if ((v1.active + v2.active + v3.active) == 0) {
			return;
		}
//		System.out.println("v1" + v1+ " v2=" + v2 +" v3=" + v3);

		// Set ranges of buffer to compute efficiently.
		int top = Integer.MAX_VALUE;
		int bottom = Integer.MIN_VALUE;
		if (top > v1.y) {
			top = v1.y;
		}
		if (top > v2.y) {
			top = v2.y;
		}
		if (top > v3.y) {
			top = v3.y;
		}
		if (bottom < v1.y) {
			bottom = v1.y;
		}
		if (bottom < v2.y) {
			bottom = v2.y;
		}
		if (bottom < v3.y) {
			bottom = v3.y;
		}
		top >>= 16;
		bottom >>= 16;
		if (top < 0) {
			top = 0;
		}
		if (bottom > screenHeight) {
			bottom = screenHeight;
		}

		// Initialize minimum and maximum buffers.
		for (int i = top; i < bottom; i++) {
			min[i] = Integer.MAX_VALUE;
			max[i] = Integer.MIN_VALUE;
		}

		// Scan edges.
		scanEdge(v1, v2);
		scanEdge(v2, v3);
		scanEdge(v3, v1);

		// Draw using minimum and maximum buffers.
//		System.out.println("drawPolygon top=" + top +  " bottom=" + bottom);
		for (int y = top; y < bottom; y++) {
			// Skip if the buffer is not updated.
			if (min[y] == Integer.MAX_VALUE) {
				continue;
			}

			int offset = y * screenWidth;

			// Compute an increasing value.
			int len = (max[y] - min[y]) + 1;
			int addz = (maxz[y] - minz[y]) / len;
			int addr = (maxr[y] - minr[y]) / len;
			int addg = (maxg[y] - ming[y]) / len;
			int addb = (maxb[y] - minb[y]) / len;
			int addu = (maxu[y] - minu[y]) / len;
			int addv = (maxv[y] - minv[y]) / len;

			// Initialize.
			int z = minz[y];
			int r = minr[y];
			int g = ming[y];
			int b = minb[y];
			int u = minu[y];
			int v = minv[y];

//			System.out.println("drawPolygon min[y]=" + min[y] +  " max[y]=" + max[y]);
			for (int x = min[y]; x <= max[y]; x++, z += addz, r += addr, g += addg, b += addb, u += addu, v += addv) {
				if (x < 0 || x >= screenWidth) {
					continue;
				}
				int p = offset + x;

				// Skip using comparing to the Z buffer.
				if (zbuf[p] > z) {
					// Get a texel.
//					System.out.println("u=" + u + " v=" + v);
					int texel = tbuf[((((textureHeight - v - 1) >> 16) & textureHeightMask) << textureShift)
							+ ((u >> 16) & textureWidthMask)];

					// Interpolate a RGB value from a RGB value of texel and vertex color.
					int tr = ((r >> 16) * ((texel & 0xff0000) >> 16)) >> 8;
					int tg = ((g >> 16) * ((texel & 0x00ff00) >> 8)) >> 8;
					int tb = ((b >> 16) * ((texel & 0x0000ff))) >> 8;
					if (tr > 255) {
						tr = 255;
					}
					if (tg > 255) {
						tg = 255;
					}
					if (tb > 255) {
						tb = 255;
					}

					pbuf[screenHeight * screenWidth - p - 1] = (tr << 16) | (tg << 8) | tb | 0xFF000000;
//					System.out.printf("texel=%8x pbuf[%d]=%6x\n", texel, p, pbuf[p]);
					zbuf[p] = z;
				}
			}
		}
	}

	/**
	 * Scan edges.
	 * 
	 * @param v1 staring point
	 * @param v2 end point
	 */
	private final void scanEdge(TLVertex v1, TLVertex v2) {
		int len = Math.abs((int) ((v2.y >> 16) - (v1.y >> 16))) + 1;

		// Compute an increasing value.
		int addx = (v2.x - v1.x) / len;
		int addy = (v2.y - v1.y) / len;
		int addz = (v2.z - v1.z) / len;
		int addr = (v2.r - v1.r) / len;
		int addg = (v2.g - v1.g) / len;
		int addb = (v2.b - v1.b) / len;
		int addu = (v2.u - v1.u) / len;
		int addv = (v2.v - v1.v) / len;

		// Initialize.
		int x = v1.x;
		int y = v1.y;
		int z = v1.z;
		int r = v1.r;
		int g = v1.g;
		int b = v1.b;
		int u = v1.u;
		int v = v1.v;

		// Scanning.
		for (int i = 0; i < len; i++, x += addx, y += addy, z += addz, r += addr, g += addg, b += addb, u += addu, v += addv) {
			int py = y >> 16;
			int px = x >> 16;

			if (py < 0 || py >= screenHeight) {
				continue;
			}

			if (min[py] > px) {
				min[py] = px;
				minz[py] = z;
				minr[py] = r;
				ming[py] = g;
				minb[py] = b;
				minu[py] = u;
				minv[py] = v;
//				System.out.println("u=" + u + " v=" + v);
			}

			if (max[py] < px) {
				max[py] = px;
				maxz[py] = z;
				maxr[py] = r;
				maxg[py] = g;
				maxb[py] = b;
				maxu[py] = u;
				maxv[py] = v;
			}
		}
	}
}
