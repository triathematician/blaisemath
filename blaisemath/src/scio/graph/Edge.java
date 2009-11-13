/*
 * Graph.java
 * Created on Oct 16, 2007, 1:12:27 PM
 */
package scio.graph;

/**
 * Represents an edge of the given type of vertex.
 *
 * @author Elisha Peterson
 */
public class Edge<V> implements EdgeInterface<V> {

    V source, sink;
    double weight = 1;
    boolean directed = false, weighted = true;

    //
    //
    // CONSTRUCTOR
    //
    //

    public Edge(V v1, V v2) {
        super();
        this.source = v1;
        this.sink = v2;
    }

    //
    //
    // GET/SET
    //
    //

    public V getSource() {
        return source;
    }

    public V getSink() {
        return sink;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double newValue) {
        this.weight = newValue;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public boolean isWeighted() {
        return weighted;
    }

    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }


    //
    //
    // GENERAL METHODS
    //
    //

    public boolean isAdjacent(V... vs) {
        for (int i = 0; i < vs.length; i++) {
            if (! (source.equals(vs[i]) || sink.equals(vs[i])) ) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(EdgeInterface e2) {
        if (isWeighted() && weight != e2.getWeight())
            return false;
        if (isDirected())
            return source.equals(e2.getSource()) && sink.equals(e2.getSink());
        else
            return source.equals(e2.getSource()) && sink.equals(e2.getSink()) || source.equals(e2.getSink()) && sink.equals(e2.getSource());
    }

    @Override
    public String toString() {
        String result = "{";
        if (isWeighted())
            result += "" + weight + " @ " + source;
        if (isDirected())
            result += " -> " + sink + "}";
        else
            result += ", " + sink + "}";
        return result;
    }
}
