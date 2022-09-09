package box2dlite.mathutils;

import java.util.Random;

public class JMath {

    public static float dot(Vec2 a, Vec2 b) {
        return a.x * b.x + a.y * b.y;
    }

    public static float cross(Vec2 a, Vec2 b) {
        return a.x * b.y - a.y * b.x;
    }

    public static Vec2 cross(Vec2 a, float s) {
        return new Vec2(s * a.y, -s * a.x);
    }

    public static Vec2 cross(float s, Vec2 a) {
        return new Vec2(-s * a.y, s * a.x);
    }

    public static Vec2 multiply(Mat22 A, Vec2 v) {
        return new Vec2(A.col1.x * v.x + A.col2.x * v.y, A.col1.y * v.x + A.col2.y * v.y);
    }

    public static Vec2 add(Vec2 a, Vec2 b) {
        return new Vec2(a.x + b.x, a.y + b.y);
    }

    public static Vec2 subtract(Vec2 a, Vec2 b) {
        return new Vec2(a.x - b.x, a.y - b.y);
    }

    public static Vec2 multiply(float s, Vec2 v) {
        return new Vec2(s * v.x, s * v.y);
    }

    public static Mat22 add(Mat22 A, Mat22 B) {
        return new Mat22(add(A.col1, B.col1), add(A.col2, B.col2));
    }

    public static Mat22 multiply(Mat22 A, Mat22 B) {
        return new Mat22(multiply(A, B.col1), multiply(A, B.col2));
    }

    public static float abs(float a) {
        return a > 0.0f ? a : -a;
    }

    public static Vec2 abs(Vec2 a) {
        return new Vec2(abs(a.x), abs(a.y));
    }

    public static Mat22 abs(Mat22 A) {
        return new Mat22(abs(A.col1), abs(A.col2));
    }

    public static float sign(float x) {
        return x < 0.0f ? -1.0f : 1.0f;
    }

    public static float min(float a, float b) {
        return a < b ? a : b;
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    public static float clamp(float a, float low, float high) {
        return max(low, min(a, high));
    }

    public static  <T> void swap(T a, T b) {
        T tmp = a;
        a = b;
        b = tmp;
    }

    public static float random() {
        Random r = new Random();
        return r.nextFloat(-1, 1);
    }

    public static float random(float lo, float hi) {
        Random r = new Random();
        return r.nextFloat(lo, hi);
    }
}

