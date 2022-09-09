package box2dlite;

import box2dlite.mathutils.Vec2;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Body {

    public Vec2 position = new Vec2();
    public float rotation = 0.0f;

    public Vec2 velocity = new Vec2();
    public float angularVelocity = 0.0f;

    public Vec2 force = new Vec2();
    public float torque = 0.0f;

    public Vec2 width;

    public float friction;
    public float mass, invMass;
    public float I, invI;

    public int id;

    public Body() {
        friction = 0.2f;
        width = new Vec2(1.0f, 1.0f);
        mass = Float.MAX_VALUE;
        invMass = 0.0f;
        I = Float.MAX_VALUE;
        invI = 0.0f;
    }

    public void set(Vec2 w, float m) {
        friction = 0.2f;

        width = w;
        mass = m;
        if (mass < Float.MAX_VALUE) {
            invMass = 1.0f / mass;
            I = mass * (width.x * width.x + width.y * width.y) / 12.0f;
            invI = 1.0f / I;
        } else {
            invMass = 0.0f;
            I = Float.MAX_VALUE;
            invI = 0.0f;
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        g2.translate(this.position.x, this.position.y);
        g2.rotate(this.rotation);
        g2.draw(new Rectangle2D.Float(-width.x / 2, -width.y / 2, width.x, width.y));
        g2.dispose();
    }

    public void addForce(Vec2 f) {
        force.add(f);
    }
}
