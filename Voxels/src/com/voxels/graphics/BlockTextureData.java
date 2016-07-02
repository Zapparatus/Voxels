package com.voxels.graphics;

import com.sun.javafx.geom.Vec2f;

public class BlockTextureData {
	public static enum BlockFace {
		Top,
		Side,
		Bottom,
	}
	
	private BlockFace face = null;
	private float xLoc = 0;
	private float yLoc = 0;
	private float size = 0;
	
	public BlockTextureData(String face, String xLoc, String yLoc, String size) {
		this.face = BlockFace.valueOf(face);
		this.xLoc = Float.parseFloat(xLoc);
		this.yLoc = Float.parseFloat(yLoc);
		this.size = Float.parseFloat(size);
	}
	
	public BlockFace getFace() {
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