package com.voxels;

import com.sun.javafx.geom.Vec3f;

public class Player {
	private Vec3f position = null;
	private Vec3f rotation = null;
	
	public Player() {
		position = new Vec3f();
		rotation = new Vec3f();
	}
	
	public void addPosition(Vec3f displacement) {
		this.position.add(displacement);
	}
	
	public void addRotation(Vec3f displacement) {
		this.rotation.add(displacement);
	}
	
	public Vec3f getPosition() {
		return position;
	}
	
	public Vec3f getRotation() {
		return rotation;
	}
	
	public void setPosition(Vec3f position) {
		this.position = position;
	}
	
	public void setRotation(Vec3f rotation) {
		this.rotation = rotation;
	}
}
