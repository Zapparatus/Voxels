package com.voxels.math;

public class Vec3f {
    public float x;
    public float y;
    public float z;

    public Vec3f() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(Vec3f other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    public void mul(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
    }

    public float length() {
        return (float)Math.sqrt(x*x + y*y + z*z);
    }

    public void normalize() {
        float l = length();
        if (l != 0) {
            mul(1/l);
        }
    }
}
