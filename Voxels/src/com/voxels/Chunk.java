package com.voxels;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

import com.sun.javafx.geom.Vec3f;

public class Chunk {
	public static final int CHUNK_SIZE = 16;
	
	private Block[][][] blocks = null;
	private int displayList = 0;
	private Vec3f position;
	
	public Chunk() {
		blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
	}
	
	public static float getSize() {
		return Block.DEFAULT_SIZE * CHUNK_SIZE;
	}
	
	public Block getBlock(Vec3f location) {
		// Get the difference locations
		float deltaX = location.x - position.x;
		float deltaY = location.y - position.y;
		float deltaZ = location.z - position.z;
		
		// If the block is outside the chunk, return null
		if (Math.abs(deltaX) > getSize() / 2) {
			return null;
		} else if (Math.abs(deltaY) > getSize() / 2) {
			return null;
		} else if (Math.abs(deltaZ) > getSize() / 2) {
			return null;
		}
		
		// Compute the index based on location
		Vec3f index = new Vec3f(location);
		
		index.x -= position.x - getSize() / 2;
		index.y -= position.y - getSize() / 2;
		index.z -= position.z - getSize() / 2;
		
		index.x = index.x % Block.DEFAULT_SIZE;
		index.y = index.y % Block.DEFAULT_SIZE;
		index.z = index.z % Block.DEFAULT_SIZE;
		
		return blocks[(int)index.x][(int)index.y][(int)index.z];
	}
	
	public Block[][][] getBlocks() {
		return blocks;
	}
	
	public void render() {
		// Render the current Chunk by calling the display list
		glCallList(displayList);
	}
	
	public void setBlock(int x, int y, int z, Block block) {
		// Set the block at location to the given block
		blocks[x][y][z] = block;
		
		// If the block is not on the boundaries,
		// set the adjacent blocks and corresponding
		// adjacent blocks' adjacent block
		if (x > 0) {
			if (block != null) {
				block.setLeftBlock(blocks[x - 1][y][z]);
			}
			
			if (blocks[x - 1][y][z] != null) {
				blocks[x - 1][y][z].setRightBlock(block);
			}
		}
		
		if (x < CHUNK_SIZE - 1) {
			if (block != null) {
				block.setRightBlock(blocks[x + 1][y][z]);
			}
			
			if (blocks[x + 1][y][z] != null) {
				blocks[x + 1][y][z].setLeftBlock(block);
			}
		}
		
		if (y > 0) {
			if (block != null) {
				block.setTopBlock(blocks[x][y - 1][z]);
			}
			
			if (blocks[x][y - 1][z] != null) {
				blocks[x][y - 1][z].setBottomBlock(block);
			}
		}
		
		if (y < CHUNK_SIZE - 1) {
			if (block != null) {
				block.setBottomBlock(blocks[x][y + 1][z]);
			}
			
			if (blocks[x][y + 1][z] != null) {
				blocks[x][y + 1][z].setTopBlock(block);
			}
		}
		
		if (z > 0) {
			if (block != null) {
				block.setBackBlock(blocks[x][y][z - 1]);
			}
			
			if (blocks[x][y][z - 1] != null) {
				blocks[x][y][z - 1].setFrontBlock(block);
			}
		}
		
		if (z < CHUNK_SIZE - 1) {
			if (block != null) {
				block.setFrontBlock(blocks[x][y][z + 1]);
			}
			
			if (blocks[x][y][z + 1] != null) {
				blocks[x][y][z + 1].setBackBlock(block);
			}
		}
	}
	
	public void update() {
		// If the display list exists, delete it
		if (displayList != 0) {
			glDeleteLists(displayList, 1);
		}
		
		// Create a new display list
		displayList = glGenLists(1);
		
		// Start populating the display list
		glNewList(displayList, GL_COMPILE);
		
		// Render each Block
		for (int x = 0; x < CHUNK_SIZE; ++x) {
			for (int y = 0; y < CHUNK_SIZE; ++y) {
				for (int z = 0; z < CHUNK_SIZE; ++z) {
					if (blocks[x][y][z] != null) {
						// Push the current location
						glPushMatrix();
						
						// Calculate the Block location in the Chunk
						float xPos = (x - CHUNK_SIZE / 2) * Block.DEFAULT_SIZE;
						float yPos = -(y - CHUNK_SIZE / 2) * Block.DEFAULT_SIZE;
						float zPos = (z - CHUNK_SIZE / 2) * Block.DEFAULT_SIZE;
						
						// Translate to the Block location in the Chunk
						glTranslatef(xPos, yPos, zPos);
						
						// Render the Block (if possible)
						blocks[x][y][z].render();
						
						// Get back the pushed location
						glPopMatrix();
					}
				}
			}
		}
		
		// Stop populating the display list
		glEndList();
	}
}
