package box2dlite;

import box2dlite.arbiters.Arbiter;

import static box2dlite.mathutils.JMath.*;

import box2dlite.mathutils.JMath;
import box2dlite.mathutils.Vec2;

import java.awt.*;
import java.util.Vector;

public class World {

    Vector<Body> bodies = new Vector<>();
    Vector<Arbiter> arbiters = new Vector<>();
    Vec2 gravity;
    int iterations;

    public static int ID = 0;

    public World(Vec2 gravity, int iterations) {
        this.gravity = gravity;
        this.iterations = iterations;
    }

    public void add(Body body) {
        body.id = ID++;
        bodies.add(body);
    }

    public void clear() {
        bodies.clear();
        arbiters.clear();
    }

    public void render(Graphics g) {
        for (Body body : bodies) {
            body.draw(g);
        }
        for (int a = 0; a < arbiters.size(); a++) {
            Arbiter arbiter = arbiters.get(a);
            for (int i = 0; i < arbiter.numContacts; i++) {
                arbiter.contacts[i].draw(g);
            }
        }
    }

    public void step(float dt) {
        float inv_dt = dt > 0.0f ? 1.0f / dt : 0.0f;

        // Determine overlapping bodies and update contact points.
        broadPhase();

        // Integrate forces.
        for (int i = 0; i < bodies.size(); ++i)
        {
            Body b = bodies.get(i);

            if (b.invMass == 0.0f)
                continue;

            b.velocity.add(multiply(dt , JMath.add(gravity, multiply(b.invMass , b.force))));
            b.angularVelocity += dt * b.invI * b.torque;
        }

        // Perform pre-steps.
        for (Arbiter arbiter : arbiters) {
            arbiter.preStep(inv_dt);
        }

        // Perform iterations
        for (int i = 0; i < iterations; ++i)
        {
            for (Arbiter arbiter : arbiters) {
                arbiter.applyImpulse();
            }
        }

        // Integrate Velocities
        for (int i = 0; i < bodies.size(); ++i)
        {
            Body b = bodies.get(i);

            b.position.add(multiply(dt, b.velocity));
            b.rotation += dt * b.angularVelocity;

            b.force.set(0.0f, 0.0f);
            b.torque = 0.0f;
        }
    }

    public void broadPhase() {
        for (int i = 0; i < bodies.size(); i++) {
            Body bi = bodies.get(i);

            for (int j = i + 1; j < bodies.size(); j++)
            {
                Body bj = bodies.get(j);

                if (bi.invMass == 0.0f && bj.invMass == 0.0f)
                    continue;

                Arbiter newArb = new Arbiter(bi, bj);

                int iter = -1;
                for (int k = 0; k < arbiters.size(); k++) {
                    if (this.arbiters.get(k).value.equals(newArb.value)) {
                        iter = k;
                        break;
                    }
                }

                if (newArb.numContacts > 0) {
                    if (iter == -1) {
                        this.arbiters.add(newArb);
                    } else {
                        this.arbiters.get(iter).update(newArb.contacts, newArb.numContacts);
                    }
                } else if (newArb.numContacts == 0 && iter > -1) {
                    this.arbiters.remove(iter);
                }
            }
        }
    }
}
