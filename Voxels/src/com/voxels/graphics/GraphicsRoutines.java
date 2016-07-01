package com.voxels.graphics;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GraphicsRoutines {
	private static final int BYTES_PER_PIXEL = 4;
	
	public static int loadImage(String path) throws IOException {
		return loadTexture(ImageIO.read(GraphicsRoutines.class.getResource(path)));
	}
	
	private static int loadTexture(BufferedImage image) {
		// Set aside an array for the pixels from the image
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		
		// Copy the pixels from the image into the array
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		
		// Create a ByteBuffer for OpenGL to use for the texture
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);
		
		// Loop through each pixel of the image
		for (int y = 0; y < image.getHeight(); ++y) {
			for (int x = 0; x < image.getWidth(); ++x) {
				// Get the pixel data
				int pixel = pixels[y * image.getWidth() + x];
				
				// Place the different channels as different bytes
				buffer.put((byte)((pixel >> 16) & 0xFF));
				buffer.put((byte)((pixel >> 8) & 0xFF));
				buffer.put((byte)(pixel & 0xFF));
				buffer.put((byte)((pixel >> 24) & 0xFF));
			}
		}
		
		// Don't forget to flip the buffer
		buffer.flip();
		
		// Obtain a texture id
		int textureId = glGenTextures();
		
		// Set the current texture
		glBindTexture(GL_TEXTURE_2D, textureId);
		
		// Give the texture to OpenGL
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		
		// Generate a mipmap
		GL30.glGenerateMipmap(GL_TEXTURE_2D);
		
		return textureId;
	}
	
	// Set up a perspective matrix
	public static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
		float sine, cotangent, deltaZ;
		float radians = fovy / 2 * 3.14f / 180;
		
		deltaZ = zFar - zNear;
		sine = (float)Math.sin(radians);
		
		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			return;
		}
		
		cotangent = (float)Math.cos(radians) / sine;
		
		FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
		
		matrix.put(0 * 4 + 0, cotangent / aspect);
		matrix.put(1 * 4 + 1, cotangent);
		matrix.put(2 * 4 + 2, -(zFar + zNear) / deltaZ);
		matrix.put(2 * 4 + 3, -1);
		matrix.put(3 * 4 + 2, -2 * zNear * zFar / deltaZ);
		matrix.put(3 * 4 + 3, 0);
		
		GL11.glMultMatrixf(matrix);
	}
}
