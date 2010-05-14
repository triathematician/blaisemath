/*
 * Graph.java
 * Created on Oct 16, 2007, 1:12:27 PM
 */
package org.bm.blaise.scio.graph;

/**
 * Represents an edge between two vertices (identified by integers).
 *
 * @author Elisha Peterson
 */
public class Edge implements Comparable<Edge> {

    int source, sink;
    double weight = 1;

    /**
     * Constructs with specified source and sink
     * @param source the source vertex
     * @param sink the sink vertex
     */
    public Edge(int source, int sink) {
        super();
        this.source = source;
        this.sink = sink;
    }

    /**
     * Constructs with specified source, sink, and weight
     * @param source the source vertex
     * @param sink the sink vertex
     * @param weight weight of edge
     */
    public Edge(int source, int sink, double weight) {
        super();
        this.source = source;
        this.sink = sink;
        this.weight = weight;
    }

    public int getSource() { return source; }
    public int getSink() { return sink; }
    public double getWeight() { return weight; }
    public void setWeight(double newValue) { this.weight = newValue; }
    public boolean isDirected() { return false; }
    public boolean isWeighted() { return false; }


    //
    // GENERAL METHODS
    //

    public boolean adjacentTo(int v) {
        return source == v || sink == v;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Edge))
            return false;
        Edge e2 = (Edge) o;
        if (isWeighted() != e2.isWeighted() || isDirected() != e2.isDirected())
            return false;
        if (isDirected())
            return source == e2.source && sink == e2.sink;
        else
            return (source == e2.source && sink == e2.sink) || (source == e2.sink && sink == e2.source);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.source;
        hash = 17 * hash + this.sink;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.weight) ^ (Double.doubleToLongBits(this.weight) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        String result = "{";
        if (isWeighted())
            result += "" + weight + " @ " + source;
        else
            result += source;
        if (isDirected())
            result += " -> " + sink + "}";
        else
            result += ", " + sink + "}";
        return result;
    }

    @Override
    public Edge clone() {
        return new Edge(source, sink, weight);
    }

    public int compareTo(Edge e2) {
        if (equals(e2))
            return 0;
        return source == e2.source ? sink - e2.sink : source - e2.source;
    }

}
