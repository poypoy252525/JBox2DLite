package box2dlite.mathutils;

public class Vec2 {
    public float x, y;

    public Vec2() {
        x = 0;
        y = 0;
    }

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 negate() {
        return new Vec2(-x, -y);
    }

    public void add(Vec2 v) {
        this.x += v.x;
        this.y += v.y;
    }

    public void subtract(Vec2 v) {
        this.x -= v.x;
        this.y -= v.y;
    }

    public void multiply(float a) {
        this.x *= a;
        this.y *= a;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
