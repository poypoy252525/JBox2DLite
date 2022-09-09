package box2dlite.arbiters;

public class FeaturePair {

    public String value;
    public Edges e = new Edges();

    public class Edges {
        public char inEdge1;
        public char outEdge1;
        public char inEdge2;
        public char outEdge2;
    }
}
