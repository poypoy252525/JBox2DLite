package box2dlite.arbiters;

import box2dlite.mathutils.Vec2;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Contact {

    public Vec2 position;
    public Vec2 normal;
    public Vec2 r1, r2;
    public float separation;
    public float pn;
    public float pt;
    public float pnb;
    public float massNormal, massTangent;
    public float bias;
    public FeaturePair feature;

    public Contact() {
        pn = 0.0f;
        pt = 0.0f;
        pnb = 0.0f;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.RED);
        g2.translate(position.x, position.y);
        g2.fill(new Rectangle2D.Float(-2, -2, 4, 4));
        g2.dispose();
    }
}
