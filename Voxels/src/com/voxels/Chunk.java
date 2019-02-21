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

import com.voxels.math.Vec3f;

public class Chunk {
	static final int CHUNK_SIZE = 16;
	
	private Block[][][] blocks = null;
	private int displayList = 0;
	private Vec3f position = null;
	
	Chunk(Vec3f location) {
		blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
		position = location;
	}
	
	Block getBlock(Vec3f location) {
	    // Get the difference locations
		float deltaX = position.x - location.x;
		float deltaY = location.y - position.y;
		float deltaZ = position.z - location.z;

        deltaX = (int) ((deltaX + (Math.signum(deltaX) > 0 ? 0 : -1) * Block.DEFAULT_SIZE) / Block.DEFAULT_SIZE);
        deltaY = (int) ((deltaY + (Math.signum(deltaY) > 0 ? 0 : -1) * Block.DEFAULT_SIZE) / Block.DEFAULT_SIZE);
        deltaZ = (int) ((deltaZ + (Math.signum(deltaZ) > 0 ? 0 : -1) * Block.DEFAULT_SIZE) / Block.DEFAULT_SIZE);

        Vec3f index = new Vec3f(deltaX, deltaY, deltaZ);

        if (index.x >= 0 && index.x < CHUNK_SIZE && index.y >= 0 && index.y < CHUNK_SIZE && index.z >= 0 && index.z < CHUNK_SIZE) {
            return blocks[(int) index.x][(int) index.y][(int) index.z];
        } else {
            return null;
        }
	}

	public Block[][][] getBlocks() {
		return blocks;
	}
	
	Vec3f getPosition() {
		return position;
	}
	
	void render() {
		// Render the current Chunk by calling the display list
		glCallList(displayList);
	}
	
	void setBlock(int x, int y, int z, Block block) {
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
				block.setBottomBlock(blocks[x][y - 1][z]);
			}
			
			if (blocks[x][y - 1][z] != null) {
				blocks[x][y - 1][z].setTopBlock(block);
			}
		}
		
		if (y < CHUNK_SIZE - 1) {
			if (block != null) {
				block.setTopBlock(blocks[x][y + 1][z]);
			}
			
			if (blocks[x][y + 1][z] != null) {
				blocks[x][y + 1][z].setBottomBlock(block);
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
	
	void update() {
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
                        float xPos = x * Block.DEFAULT_SIZE;
                        float yPos = y * Block.DEFAULT_SIZE;
                        float zPos = z * Block.DEFAULT_SIZE;
						
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
