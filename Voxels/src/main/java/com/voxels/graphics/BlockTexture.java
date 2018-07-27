package com.voxels.graphics;

import java.util.HashMap;

import com.voxels.Block.BlockType;
import com.voxels.graphics.BlockTextureData.BlockFace;

public class BlockTexture {
	public static HashMap<BlockType, BlockTexture> blockTextures = new HashMap<BlockType, BlockTexture>();
	
	private HashMap<BlockFace, BlockTextureData> blockFaceData = null;
	
	public BlockTexture(String blockType) {
		// Initialize the face data HashMap
		blockFaceData = new HashMap<BlockFace, BlockTextureData>();
		
		// Place the current BlockTexture into the HashMap
		blockTextures.put(BlockType.valueOf(blockType), this);
	}
	
	public static BlockTexture getBlockTexture(BlockType type) {
		return blockTextures.get(type);
	}
	
	public void addFace(BlockTextureData faceData) {
		// Add face data to the current face
		blockFaceData.put(faceData.getFace(), faceData);
	}
	
	public BlockTextureData getFaceData(BlockFace face) {
		return blockFaceData.get(face);
	}
}
