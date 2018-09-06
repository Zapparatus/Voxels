package com.voxels;

import com.sun.javafx.geom.Vec3f;

public class Player {
	private float height = Block.DEFAULT_SIZE * 2;
	private Vec3f position = null;
	private Vec3f rotation = null;
	
	Player() {
		position = new Vec3f();
		rotation = new Vec3f();
	}
	
	void addPosition(Vec3f displacement) {
		this.position.add(displacement);
	}
	
	void addRotation(Vec3f displacement) {
		this.rotation.add(displacement);
	}
	
	public float getHeight() {
		return height;
	}
	
	Vec3f getPosition() {
		return position;
	}
	
	Vec3f getRotation() {
		return rotation;
	}
	
	public void setPosition(Vec3f position) {
		this.position = position;
	}
	
	void setRotation(Vec3f rotation) {
		this.rotation = rotation;
	}
}
