package com.voxels;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

import com.sun.javafx.geom.Vec3f;
import com.voxels.graphics.BlockTexture;
import com.voxels.graphics.BlockTextureData;
import com.voxels.graphics.BlockTextureData.BlockFace;

public class Block {
	public static final float DEFAULT_SIZE = 5;
	public static enum BlockType {
		Air,
		Dirt,
		Grass,
		Sand,
	}
	
	public static int BLOCK_TEXTURES = 0;
	
	private Block frontBlock = null;
	private Block backBlock = null;
	private Block leftBlock = null;
	private Block rightBlock = null;
	private Block topBlock = null;
	private Block bottomBlock = null;
	private BlockType blockType = BlockType.Air;
	private boolean needsVisibilityUpdate = true;
	private boolean visible = true;
	private Chunk parent = null;
	private float depth = DEFAULT_SIZE;
	private float height = DEFAULT_SIZE;
	private float width = DEFAULT_SIZE;
	private Vec3f position = null;
	
	public Block(Chunk c, Vec3f location, BlockType blockType) {
		parent = c;
		position = location;
		this.blockType = blockType;
	}
	
	public float getDepth() {
		return depth;
	}
	
	public float getHeight() {
		return height;
	}
	
	public float getWidth() {
		return width;
	}

	public BlockType getBlockType() {
		return blockType;
	}
	
	public Block getFrontBlock() {
		return frontBlock;
	}

	public Block getBackBlock() {
		return backBlock;
	}

	public Block getLeftBlock() {
		return leftBlock;
	}

	public Block getRightBlock() {
		return rightBlock;
	}

	public Block getTopBlock() {
		return topBlock;
	}

	public Block getBottomBlock() {
		return bottomBlock;
	}

	public Chunk getParentChunk() {
		return parent;
	}

	public Vec3f getPosition() {
		return position;
	}

	public boolean isVisible() {
		// If the Block needs to check its visibility again
		if (needsVisibilityUpdate) {
			if (getBlockType() == BlockType.Air) {
				// Don't render an invisible block
				visible = false;
			} else if (frontBlock == null || backBlock == null || leftBlock == null
					|| rightBlock == null || topBlock == null || bottomBlock == null) {
				// Render a block without a defined side (end chunk)
				visible = true;
			} else if (frontBlock.getBlockType() == BlockType.Air
					|| backBlock.getBlockType() == BlockType.Air
					|| leftBlock.getBlockType() == BlockType.Air
					|| rightBlock.getBlockType() == BlockType.Air
					|| topBlock.getBlockType() == BlockType.Air
					|| bottomBlock.getBlockType() == BlockType.Air) {
				// Render a block exposed to air
				visible = true;
			} else {
				// Otherwise, do not render the block
				visible = false;
			}
			
			// Don't check visibility again
			needsVisibilityUpdate = false;
		}
		
		return visible;
	}
	
	public void render() {
		// Draw only if the block is visible 
		if (!isVisible()) {
			return;
		}
		
		// Bind the sprite sheet
		glBindTexture(GL_TEXTURE_2D, BLOCK_TEXTURES);
		
		// Clamp the texture to the edge
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		// Begin drawing GL_QUADS
		glBegin(GL_QUADS);
		
		// Get the face textures
		BlockTextureData topFace = BlockTexture.getBlockTexture(blockType).getFaceData(BlockFace.Top);
		BlockTextureData sideFace = BlockTexture.getBlockTexture(blockType).getFaceData(BlockFace.Side);
		BlockTextureData bottomFace = BlockTexture.getBlockTexture(blockType).getFaceData(BlockFace.Bottom);
		
		if (frontBlock == null || frontBlock.getBlockType() == BlockType.Air) {
			// Top left vertex
			glTexCoord2f(sideFace.getTopLeft().x, sideFace.getTopLeft().y);
			glVertex3f(-width / 2, height / 2, depth / 2);
			
			// Top right vertex
			glTexCoord2f(sideFace.getTopRight().x, sideFace.getTopRight().y);
			glVertex3f(width / 2, height / 2, depth / 2);
			
			// Bottom right vertex
			glTexCoord2f(sideFace.getBottomRight().x, sideFace.getBottomRight().y);
			glVertex3f(width / 2, -height / 2, depth / 2);
			
			// Bottom left vertex
			glTexCoord2f(sideFace.getBottomLeft().x, sideFace.getBottomLeft().y);
			glVertex3f(-width / 2, -height / 2, depth / 2);
		}
		
		if (backBlock == null || backBlock.getBlockType() == BlockType.Air) {
			// Top left vertex
			glTexCoord2f(sideFace.getTopLeft().x, sideFace.getTopLeft().y);
			glVertex3f(width / 2, height / 2, -depth / 2);
			
			// Top right vertex
			glTexCoord2f(sideFace.getTopRight().x, sideFace.getTopRight().y);
			glVertex3f(-width / 2, height / 2, -depth / 2);
			
			// Bottom right vertex
			glTexCoord2f(sideFace.getBottomRight().x, sideFace.getBottomRight().y);
			glVertex3f(-width / 2, -height / 2, -depth / 2);
			
			// Bottom left vertex
			glTexCoord2f(sideFace.getBottomLeft().x, sideFace.getBottomLeft().y);
			glVertex3f(width / 2, -height / 2, -depth / 2);
		}
		
		if (leftBlock == null || leftBlock.getBlockType() == BlockType.Air) {
			// Top left vertex
			glTexCoord2f(sideFace.getTopLeft().x, sideFace.getTopLeft().y);
			glVertex3f(-width / 2, height / 2, -depth / 2);
			
			// Top right vertex
			glTexCoord2f(sideFace.getTopRight().x, sideFace.getTopRight().y);
			glVertex3f(-width / 2, height / 2, depth / 2);
			
			// Bottom right vertex
			glTexCoord2f(sideFace.getBottomRight().x, sideFace.getBottomRight().y);
			glVertex3f(-width / 2, -height / 2, depth / 2);
			
			// Bottom left vertex
			glTexCoord2f(sideFace.getBottomLeft().x, sideFace.getBottomLeft().y);
			glVertex3f(-width / 2, -height / 2, -depth / 2);
		}
		
		if (rightBlock == null || rightBlock.getBlockType() == BlockType.Air) {
			// Top left vertex
			glTexCoord2f(sideFace.getTopLeft().x, sideFace.getTopLeft().y);
			glVertex3f(width / 2, height / 2, depth / 2);
			
			// Top right vertex
			glTexCoord2f(sideFace.getTopRight().x, sideFace.getTopRight().y);
			glVertex3f(width / 2, height / 2, -depth / 2);
			
			// Bottom right vertex
			glTexCoord2f(sideFace.getBottomRight().x, sideFace.getBottomRight().y);
			glVertex3f(width / 2, -height / 2, -depth / 2);
			
			// Bottom left vertex
			glTexCoord2f(sideFace.getBottomLeft().x, sideFace.getBottomLeft().y);
			glVertex3f(width / 2, -height / 2, depth / 2);
		}
		
		if (topBlock == null || topBlock.getBlockType() == BlockType.Air) {
			// Top left vertex
			glTexCoord2f(topFace.getTopLeft().x, topFace.getTopLeft().y);
			glVertex3f(-width / 2, height / 2, -depth / 2);
			
			// Top right vertex
			glTexCoord2f(topFace.getTopRight().x, topFace.getTopRight().y);
			glVertex3f(width / 2, height / 2, -depth / 2);
			
			// Bottom right vertex
			glTexCoord2f(topFace.getBottomRight().x, topFace.getBottomRight().y);
			glVertex3f(width / 2, height / 2, depth / 2);
			
			// Bottom left vertex
			glTexCoord2f(topFace.getBottomLeft().x, topFace.getBottomLeft().y);
			glVertex3f(-width / 2, height / 2, depth / 2);
		}
		
		if (bottomBlock == null || bottomBlock.getBlockType() == BlockType.Air) {
			// Top left vertex
			glTexCoord2f(bottomFace.getTopLeft().x, bottomFace.getTopLeft().y);
			glVertex3f(-width / 2, -height / 2, depth / 2);
			
			// Top right vertex
			glTexCoord2f(bottomFace.getTopRight().x, bottomFace.getTopRight().y);
			glVertex3f(width / 2, -height / 2, depth / 2);
			
			// Bottom right vertex
			glTexCoord2f(bottomFace.getBottomRight().x, bottomFace.getBottomRight().y);
			glVertex3f(width / 2, -height / 2, -depth / 2);
			
			// Bottom left vertex
			glTexCoord2f(bottomFace.getBottomLeft().x, bottomFace.getBottomLeft().y);
			glVertex3f(-width / 2, -height / 2, -depth / 2);
		}
		
		// Stop drawing GL_QUADS
		glEnd();
		
		// Stop using the sprite sheet texture
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void setFrontBlock(Block frontBlock) {
		this.frontBlock = frontBlock;
		needsVisibilityUpdate = true;
	}

	public void setBackBlock(Block backBlock) {
		this.backBlock = backBlock;
		needsVisibilityUpdate = true;
	}

	public void setLeftBlock(Block leftBlock) {
		this.leftBlock = leftBlock;
		needsVisibilityUpdate = true;
	}

	public void setRightBlock(Block rightBlock) {
		this.rightBlock = rightBlock;
		needsVisibilityUpdate = true;
	}

	public void setTopBlock(Block topBlock) {
		this.topBlock = topBlock;
		needsVisibilityUpdate = true;
	}

	public void setBottomBlock(Block bottomBlock) {
		this.bottomBlock = bottomBlock;
		needsVisibilityUpdate = true;
	}
}
