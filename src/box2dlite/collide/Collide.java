package box2dlite.collide;

import box2dlite.Body;
import box2dlite.arbiters.Contact;
import box2dlite.arbiters.FeaturePair;
import static box2dlite.mathutils.JMath.*;
import box2dlite.mathutils.Mat22;
import box2dlite.mathutils.Vec2;

import static box2dlite.collide.Axis.*;
import static box2dlite.collide.EdgeNumbers.*;

public class Collide {

    public static void flip(FeaturePair fp) {
        swap(fp.e.inEdge1, fp.e.inEdge2);
        swap(fp.e.outEdge1, fp.e.outEdge2);
    }

    public static int clipSegmentToLine(ClipVertex[] vOut, ClipVertex[] vIn,
                                 Vec2 normal, float offset, char clipEdge) {
        int numOut = 0;

        float distance0 = dot(normal, vIn[0].v) - offset;
        float distance1 = dot(normal, vIn[1].v) - offset;

        if (distance0 <= 0.0f) vOut[numOut++] = vIn[0];
        if (distance1 <= 0.0f) vOut[numOut++] = vIn[1];

        if (distance0 * distance1 < 0.0f) {
            float interp = distance0 / (distance0 - distance1);
            vOut[numOut].v = add(vIn[0].v, multiply(interp, subtract(vIn[1].v, vIn[0].v)));
            if (distance0 > 0.0f) {
                vOut[numOut].fp = vIn[0].fp;
                vOut[numOut].fp.e.inEdge1 = clipEdge;
                vOut[numOut].fp.e.inEdge2 = EdgeNumbers.NO_EDGE;
            } else {
                vOut[numOut].fp = vIn[1].fp;
                vOut[numOut].fp.e.outEdge1 = clipEdge;
                vOut[numOut].fp.e.outEdge2 = EdgeNumbers.NO_EDGE;
            }
            ++numOut;
        }
        return numOut;
    }

    public static void computeIncidentEdge(ClipVertex[] c, Vec2 h, Vec2 pos,
                                           Mat22 Rot, Vec2 normal) {
        Mat22 RotT = Rot.transpose();
        Vec2 n = multiply(RotT, normal).negate();
        Vec2 nAbs = abs(n);

        if (nAbs.x > nAbs.y) {
            if (sign(n.x) > 0.0f) {
                c[0].v.set(h.x, -h.y);
                c[0].fp.e.inEdge2 = EDGE3;
                c[0].fp.e.outEdge2 = EDGE4;

                c[1].v.set(h.x, h.y);
                c[1].fp.e.inEdge2 = EDGE4;
                c[1].fp.e.outEdge2 = EDGE1;
            } else {
                c[0].v.set(-h.x, h.y);
                c[0].fp.e.inEdge2 = EDGE1;
                c[0].fp.e.outEdge2 = EDGE2;

                c[1].v.set(-h.x, -h.y);
                c[1].fp.e.inEdge2 = EDGE2;
                c[1].fp.e.outEdge2 = EDGE3;
            }
        } else {
            if (sign(n.y) > 0.0f) {
                c[0].v.set(h.x, h.y);
                c[0].fp.e.inEdge2 = EDGE4;
                c[0].fp.e.outEdge2 = EDGE1;

                c[1].v.set(-h.x, h.y);
                c[1].fp.e.inEdge2 = EDGE1;
                c[1].fp.e.outEdge2 = EDGE2;
            } else {
                c[0].v.set(-h.x, -h.y);
                c[0].fp.e.inEdge2 = EDGE2;
                c[0].fp.e.outEdge2 = EDGE3;

                c[1].v.set(h.x, -h.y);
                c[1].fp.e.inEdge2 = EDGE3;
                c[1].fp.e.outEdge2 = EDGE4;
            }
        }
        c[0].v = add(pos, multiply(Rot, c[0].v));
        c[1].v = add(pos, multiply(Rot, c[1].v));
    }

    public static int collide(Contact[] contacts, Body bodyA, Body bodyB) {
        Vec2 hA = multiply(0.5f, bodyA.width);
        Vec2 hB = multiply(0.5f, bodyB.width);

        Vec2 posA = bodyA.position;
        Vec2 posB = bodyB.position;

        Mat22 RotA = new Mat22(bodyA.rotation), RotB = new Mat22(bodyB.rotation);

        Mat22 RotAT = RotA.transpose();
        Mat22 RotBT = RotB.transpose();

        Vec2 dp = subtract(posB, posA);
        Vec2 dA = multiply(RotAT, dp);
        Vec2 dB = multiply(RotBT, dp);

        Mat22 C = multiply(RotAT, RotB);
        Mat22 absC = abs(C);
        Mat22 absCT = absC.transpose();

        Vec2 faceA = subtract(subtract(abs(dA), hA), multiply(absC, hB));
        if (faceA.x > 0.0f || faceA.y > 0.0f) return 0;

        Vec2 faceB = subtract(subtract(abs(dB), multiply(absCT, hA)), hB);
        if (faceB.x > 0.0f || faceB.y > 0.0f) return 0;

        Axis axis;
        float separation;
        Vec2 normal;

        axis = FACE_A_X;
        separation = faceA.x;
        normal = dA.x > 0.0f ? RotA.col1 : RotA.col1.negate();

        float relativeTol = 0.95f;
        float absoluteTol = 0.01f;

        if (faceA.y > relativeTol * separation + absoluteTol * hA.y) {
            axis = FACE_A_Y;
            separation = faceA.y;
            normal = dA.y > 0.0f ? RotA.col2 : RotA.col2.negate();
        }

        if (faceB.x > relativeTol * separation + absoluteTol * hB.x) {
            axis = FACE_B_X;
            separation = faceB.x;
            normal = dB.x > 0.0f ? RotB.col1 : RotB.col1.negate();
        }

        if (faceB.y > relativeTol * separation + absoluteTol * hB.y) {
            axis = FACE_B_Y;
            separation = faceB.y;
            normal = dB.y > 0.0f ? RotB.col2 : RotB.col2.negate();
        }

        Vec2 frontNormal = null, sideNormal = null;
        ClipVertex incidentEdge[] = new ClipVertex[2];
        for (int i = 0; i < incidentEdge.length; i++) {
            incidentEdge[i]  = new ClipVertex();
        }
        float front = 0.0f, negSide = 0.0f, posSide = 0.0f;
        char negEdge = 0, posEdge = 0;

        switch (axis) {
            case FACE_A_X: {
                frontNormal = normal;
                front = dot(posA, frontNormal) + hA.x;
                sideNormal = RotA.col2;
                float side = dot(posA, sideNormal);
                negSide = -side + hA.y;
                posSide =  side + hA.y;
                negEdge = EDGE3;
                posEdge = EDGE1;
                computeIncidentEdge(incidentEdge, hB, posB, RotB, frontNormal);
            }
            break;
            case FACE_A_Y: {
                frontNormal = normal;
                front = dot(posA, frontNormal) + hA.y;
                sideNormal = RotA.col1;
                float side = dot(posA, sideNormal);
                negSide = -side + hA.x;
                posSide =  side + hA.x;
                negEdge = EDGE2;
                posEdge = EDGE4;
                computeIncidentEdge(incidentEdge, hB, posB, RotB, frontNormal);
            }
            break;
            case FACE_B_X: {
                frontNormal = normal.negate();
                front = dot(posB, frontNormal) + hB.x;
                sideNormal = RotB.col2;
                float side = dot(posB, sideNormal);
                negSide = -side + hB.y;
                posSide =  side + hB.y;
                negEdge = EDGE3;
                posEdge = EDGE1;
                computeIncidentEdge(incidentEdge, hA, posA, RotA, frontNormal);
            }
            break;
            case FACE_B_Y: {
                frontNormal = normal.negate();
                front = dot(posB, frontNormal) + hB.y;
                sideNormal = RotB.col1;
                float side = dot(posB, sideNormal);
                negSide = -side + hB.x;
                posSide =  side + hB.x;
                negEdge = EDGE2;
                posEdge = EDGE4;
                computeIncidentEdge(incidentEdge, hA, posA, RotA, frontNormal);
            }
            break;
        }

        ClipVertex[] clipPoints1 = new ClipVertex[2];
        for (int i = 0; i < clipPoints1.length; i++) {
            clipPoints1[i] = new ClipVertex();
        }
        ClipVertex[] clipPoints2 = new ClipVertex[2];
        for (int i = 0; i < clipPoints2.length; i++) {
            clipPoints2[i] = new ClipVertex();
        }
        int np = clipSegmentToLine(clipPoints1, incidentEdge, sideNormal.negate(), negSide, negEdge);
        if (np < 2)
            return 0;

        np = clipSegmentToLine(clipPoints2, clipPoints1,  sideNormal, posSide, posEdge);
        if (np < 2)
            return 0;

        int numContacts = 0;
        for (int i = 0; i < 2; ++i)
        {
            separation = dot(frontNormal, clipPoints2[i].v) - front;

            if (separation <= 0)
            {
                contacts[numContacts] = new Contact();
                contacts[numContacts].separation = separation;
                contacts[numContacts].normal = normal;
                contacts[numContacts].position = subtract(clipPoints2[i].v, multiply(separation, frontNormal));
                contacts[numContacts].feature = clipPoints2[i].fp;
                if (axis == FACE_B_X || axis == FACE_B_Y)
                    flip(contacts[numContacts].feature);
                FeaturePair.Edges fpEdge = contacts[numContacts].feature.e;
                contacts[numContacts].feature.value = fpEdge.inEdge1 + ":" + fpEdge.outEdge1 + ":" +
                        fpEdge.inEdge2 + ":" + fpEdge.outEdge2;
                ++numContacts;
            }
        }
        return numContacts;
    }
}
