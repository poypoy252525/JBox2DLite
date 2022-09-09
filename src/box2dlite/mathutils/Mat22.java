package box2dlite.mathutils;

import javax.swing.*;

public class Mat22 {

    public Vec2 col1;
    public Vec2 col2;

    public Mat22() {
        col1 = new Vec2(1, 0);
        col2 = new Vec2(0, 1);
    }

    public Mat22(float angle) {
        float c = (float) Math.cos(angle), s = (float) Math.sin(angle);
        col1 = new Vec2(c, s);
        col2 = new Vec2(-s, c);
    }

    public Mat22(Vec2 col1, Vec2 col2) {
        this.col1 = col1;
        this.col2 = col2;
    }

    public Mat22 transpose() {
        return new Mat22(new Vec2(col1.x, col2.x), new Vec2(col1.y, col2.y));
    }

    public Mat22 invert() {
        float a = col1.x, b = col2.x, c = col1.y, d = col2.y;
        Mat22 B = new Mat22();
        float det = a * d - b * c;
        if (det == 0.0f) {
            JOptionPane.showMessageDialog(null, "det is 0");
            return null;
        }
        det = 1.0f / det;
        B.col1.x =  det * d;    B.col2.x = -det * b;
        B.col1.y = -det * c;    B.col2.y =  det * a;
        return B;
    }

    @Override
    public String toString() {
        return "(" + this.col1.x + ", " + this.col2.x + ")\n" +
                "(" + this.col1.y + ", " + this.col2.y + ")";
    }
}
