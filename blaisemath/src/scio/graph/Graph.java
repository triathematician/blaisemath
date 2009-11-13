/*
 * Graph.java
 * Created on Oct 16, 2007, 1:12:27 PM
 */
package scio.graph;

import java.util.Collection;
import java.util.HashSet;

/**
 * <p>
 *   Represents a graph, as an extension of a collection of vertex objects.
 * </p>
 *
 * @author Elisha Peterson
 */
public class Graph<V> extends HashSet<V> implements GraphInterface<V> {

    //
    //
    // PROPERTIES
    //
    //

    /** Stores the set of edges. */
    HashSet<EdgeInterface<V>> edges;

    /** Whether graph allows multiple edges between vertices. */
    boolean multiEdge = false;

    /** Whether the graph is directed. */
    boolean directed = false;

    /** Whether the graph is weighted. */
    boolean weighted = true;

    //
    //
    // CONSTRUCTORS
    //
    //

    /** Constructs with an empty graph, no edges. */
    public Graph() {
        super();
        edges = new HashSet<EdgeInterface<V>>();
    }

    //
    //
    // GET/SET PATTERNS
    //
    //

    public int getSize() {
        return super.size();
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public boolean isMultiEdge() {
        return multiEdge;
    }
    
    public void setMultiEdge(boolean multiEdge) {
        this.multiEdge = multiEdge;
    }

    public boolean isWeighted() {
        return weighted;
    }

    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }



    //
    //
    // COMPOSITE PATTERNS
    //
    //

    public HashSet<V> getVertices() {
        return this;
    }

    public void addVertex(V v1) {
        super.add(v1);
    }

    public boolean removeVertex(V v1) {
        return super.remove(v1);
    }

    public boolean containsVertex(V v1) {
        return super.contains(v1);
    }

    public HashSet<EdgeInterface<V>> getEdges() {
        return edges;
    }

    public void setEdges(Collection<EdgeInterface<V>> edges) {
        this.edges.clear();
        this.edges.addAll(edges);
    }

    public void addEdge(EdgeInterface<V> edge) {
        edges.add(edge);
    }

    public boolean removeEdge(EdgeInterface<V> edge) {
        return edges.remove(edge);
    }

    public boolean containsEdge(EdgeInterface<V> edge) {
        if (multiEdge)
            return edges.contains(edge);

        for (EdgeInterface<V> e : edges) {
            if (e.equals(edge))
                return true;
        }

        return false;
    }

    //
    //
    // EDGE INFO METHODS
    //
    //

    public boolean adjacent(V v1, V v2) {
        return getEdge(v1, v2) != null;
    }

    public void addEdge(V v1, V v2) {
        addEdge(new Edge<V>(v1, v2));
    }

    public EdgeInterface<V> getEdge(V v1, V v2) {
        for (EdgeInterface<V> e : edges) {
            if (e.getSource().equals(v1) && e.getSink().equals(v2)) {
                return e;
            }
            if (!directed && e.getSource().equals(v2) && e.getSink().equals(v1)) {
                return e;
            }
        }
        return null;
    }

    public double getEdgeWeight(V v1, V v2) {
        EdgeInterface<V> edge = getEdge(v1, v2);
        return (edge == null) ? Double.NaN : edge.getWeight();
    }
    
    //
    //
    // GENERAL METHODS
    //
    //

    @Override
    public Object clone() {
        Graph<V> g = new Graph<V>();
        for (V v : this) {
            g.addVertex(v);
        }
        for (EdgeInterface<V> e : edges) {
            g.addEdge(e);
        }
        return g;
    }

    @Override
    public String toString() {
        return "{ " + super.toString() + "; " + edges.toString() + " }";
    }
}
