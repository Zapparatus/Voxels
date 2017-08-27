package com.voxels.graphics;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glMultMatrixf;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.voxels.Block;

public class GraphicsRoutines {
	private static final int BYTES_PER_PIXEL = 4;
	
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
		
		glMultMatrixf(matrix);
	}
	
	public static void loadTextures() throws IOException, ParserConfigurationException {
		// Load the block textures
		Block.BLOCK_TEXTURES = loadImage("/res/Blocks.png");
		
		// Create a XML document parser for the block data
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		try {
			// Parse the BlocksFormat.xml file
			Document blocksFormat = builder.parse(GraphicsRoutines.class.getResourceAsStream("/res/BlocksFormat.xml"));
			
			// Get the root element of the document
			Element root = blocksFormat.getDocumentElement();
			
			// Make sure the root element is <Blocks></Blocks>
			if (root.getNodeName().compareTo("Blocks") != 0) {
				throw new SAXException();
			}
			
			// Get the children (blocks)
			NodeList childNodes = root.getChildNodes();
			
			// Loop through the children blocks
			for (int index = 0; index < childNodes.getLength(); ++index) {
				// If the child node is not an element, continue
				if (childNodes.item(index).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				
				// Get the block type element node
				Element childNode = (Element)childNodes.item(index);
				
				// Create a BlockTexture to store the texture data for the type of block
				BlockTexture texture = new BlockTexture(childNode.getNodeName());
				
				// Get the children (faces)
				NodeList faceNodes = childNodes.item(index).getChildNodes();
				
				// Loop through the faces of the block type
				for (int faceIndex = 0; faceIndex < faceNodes.getLength(); ++faceIndex) {
					// If the child node is not an element, continue
					if (faceNodes.item(faceIndex).getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					
					// Get the face element node
					Element faceNode = (Element)faceNodes.item(faceIndex);
					
					// Store the texture location data
					texture.addFace(new BlockTextureData(faceNode.getNodeName(),
							faceNode.getAttribute("xLoc"),
							faceNode.getAttribute("yLoc"),
							faceNode.getAttribute("Size")));
				}
			}
		} catch (SAXException e) {
			System.err.println("Could not parse BlocksFormat.xml");
			System.exit(0);
		}
	}
	
	public static int loadImage(String path) throws IOException {
		// Load the image using the loadTexture method and reading from the path
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
		
		// Set the texture magnification behavior to nearest (prevents edge artifacts).
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		// Generate a mipmap
		GL30.glGenerateMipmap(GL_TEXTURE_2D);
		
		return textureId;
	}
}
