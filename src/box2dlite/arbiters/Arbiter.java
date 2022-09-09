package box2dlite.arbiters;

import box2dlite.Body;
import box2dlite.collide.Collide;
import box2dlite.mathutils.Vec2;

import static box2dlite.mathutils.JMath.*;

public class Arbiter {

    public static int MAX_POINTS = 2;
    public Contact[] contacts = new Contact[MAX_POINTS];
    public int numContacts;
    public Body body1;
    public Body body2;

    public String value;

    public float friction;

    public Arbiter(Body body1, Body body2) {
        this.body1 = body1;
        this.body2 = body2;
        numContacts = Collide.collide(contacts, this.body1, this.body2);
        value = body1.id + ":" + body2.id;
        friction = 0.5f;
    }

    public void update(Contact[] newContacts, int numNewContacts) {
        Contact mergedContacts[] = new Contact[2];
        for (int i = 0; i < numNewContacts; i++) {
            Contact cNew = newContacts[i];
            int k = -1;
            for (int j = 0; j < numContacts; j++) {
                Contact cOld = contacts[j];
                if (cNew.feature.value.equals(cOld.feature.value)) {
                    k = j;
                    break;
                }
            }
            if (k > -1) {
                Contact cOld = contacts[k];
                mergedContacts[i] = cNew;

                mergedContacts[i].pn = cOld.pn;
                mergedContacts[i].pt = cOld.pt;
                mergedContacts[i].pnb = cOld.pnb;
            } else {
                mergedContacts[i] = newContacts[i];
            }
        }
        for (int i = 0; i < numNewContacts; i++) {
            contacts[i] = mergedContacts[i];
        }
        numContacts = numNewContacts;
    }

    public void preStep(float inv_dt) {
        float k_allowedPenetration = 0.01f;
        float k_biasFactor = 0.2f;

        for (int i = 0; i < numContacts; ++i)
        {
            Contact c = contacts[i];

            Vec2 r1 = subtract(c.position, body1.position);
            Vec2 r2 = subtract(c.position, body2.position);

            float rn1 = dot(r1, c.normal);
            float rn2 = dot(r2, c.normal);
            float kNormal = body1.invMass + body2.invMass;
            kNormal += body1.invI * (dot(r1, r1) - rn1 * rn1) + body2.invI * (dot(r2, r2) - rn2 * rn2);
            c.massNormal = 1.0f / kNormal;

            Vec2 tangent = cross(c.normal, 1.0f);
            float rt1 = dot(r1, tangent);
            float rt2 = dot(r2, tangent);
            float kTangent = body1.invMass + body2.invMass;
            kTangent += body1.invI * (dot(r1, r1) - rt1 * rt1) + body2.invI * (dot(r2, r2) - rt2 * rt2);
            c.massTangent = 1.0f /  kTangent;

            c.bias = -k_biasFactor * inv_dt * min(0.0f, c.separation + k_allowedPenetration);

            Vec2 P = add(multiply(c.pn, c.normal) , multiply(c.pt , tangent));

            body1.velocity.subtract(multiply(body1.invMass , P));
            body1.angularVelocity -= body1.invI * cross(r1, P);

            body2.velocity.add(multiply(body2.invMass , P));
            body2.angularVelocity += body2.invI * cross(r2, P);
        }
    }

    public void applyImpulse() {
        Body b1 = body1;
        Body b2 = body2;

        for (int i = 0; i < numContacts; ++i)
        {
            Contact c = contacts[i];
            c.r1 = subtract(c.position , b1.position);
            c.r2 = subtract(c.position , b2.position);

            // Relative velocity at contact
            Vec2 dv = subtract(subtract(add(b2.velocity , cross(b2.angularVelocity, c.r2)) , b1.velocity) , cross(b1.angularVelocity, c.r1));

            // Compute normal impulse
            float vn = dot(dv, c.normal);

            float dPn = c.massNormal * (-vn + c.bias);


                // Clamp the accumulated impulse
                float Pn0 = c.pn;
                c.pn = max(Pn0 + dPn, 0.0f);
                dPn = c.pn - Pn0;

            // Apply contact impulse
            Vec2 Pn = multiply(dPn , c.normal);

            b1.velocity.subtract(multiply(b1.invMass , Pn));
            b1.angularVelocity -= b1.invI * cross(c.r1, Pn);

            b2.velocity.add(multiply(b2.invMass , Pn));
            b2.angularVelocity += b2.invI * cross(c.r2, Pn);

            dv = subtract(subtract(add(b2.velocity , cross(b2.angularVelocity, c.r2)) , b1.velocity) , cross(b1.angularVelocity, c.r1));

            Vec2 tangent = cross(c.normal, 1.0f);
            float vt = dot(dv, tangent);
            float dPt = c.massTangent * (-vt);

            float maxPt = friction * c.pn;
            
            float oldTangentImpulse = c.pt;
            c.pt = clamp(oldTangentImpulse + dPt, -maxPt, maxPt);
            dPt = c.pt - oldTangentImpulse;



        // Apply contact impulse
            Vec2 Pt = multiply(dPt , tangent);

            b1.velocity.subtract(multiply(b1.invMass , Pt));
            b1.angularVelocity -= b1.invI * cross(c.r1, Pt);

            b2.velocity.add(multiply(b2.invMass , Pt));
            b2.angularVelocity += b2.invI * cross(c.r2, Pt);
        }
    }
}
