package com.voxels.graphics;

import com.voxels.math.Vec2f;

public class BlockTextureData {
	public enum BlockFace {
		Top,
		Side,
		Bottom,
	}
	
	private BlockFace face;
	private float xLoc;
	private float yLoc;
	private float size;
	
	BlockTextureData(String face, String xLoc, String yLoc, String size) {
		this.face = BlockFace.valueOf(face);
		this.xLoc = Float.parseFloat(xLoc);
		this.yLoc = Float.parseFloat(yLoc);
		this.size = Float.parseFloat(size);
	}
	
	BlockFace getFace() {
		return face;
	}
	
	public Vec2f getTopLeft() {
		return new Vec2f(xLoc, yLoc);
	}
	
	public Vec2f getTopRight() {
		return new Vec2f(xLoc + size, yLoc);
	}
	
	public Vec2f getBottomLeft() {
		return new Vec2f(xLoc, yLoc + size);
	}
	
	public Vec2f getBottomRight() {
		return new Vec2f(xLoc + size, yLoc + size);
	}
}