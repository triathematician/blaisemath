/*
 * SimpleGraph.java
 * Created on Oct 16, 2007, 1:12:27 PM
 */
package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *   Represents an undirected, unweighted graph, whose vertices are identified as integers.
 *   Vertices are stored with names in a TreeMap (sorted by integer), and edges are stored in a HashSet.
 * </p>
 *
 * @author Elisha Peterson
 */
public class SimpleGraph implements GraphInterface<Object> {

    //
    // PROPERTIES
    //

    /** Vertices */
    ArrayList<Object> vertices;
    /** Names */
    ArrayList<String> labels;
    /** Stores the set of edges. */
    HashSet<Edge> edges;

    //
    // CONSTRUCTORS
    //

    /** Constructs with an empty graph, no edges. */
    public SimpleGraph() {
        super();
        vertices = new ArrayList<Object>();
        labels = new ArrayList<String>();
        edges = new HashSet<Edge>();
    }

    //
    // GET/SET PATTERNS
    //

    public int size() {
        return vertices.size();
    }

    public boolean isDirected() {
        return false;
    }

    public boolean isMultiEdge() {
        return false;
    }

    public boolean isWeighted() {
        return false;
    }


    //
    // VERTICES
    //

    public Iterator<Object> iterator() {
        return vertices.iterator();
    }

    public List<Object> getVertices() {
        return vertices;
    }

    public void addVertex(Object v1) {
        addVertex(v1, v1.toString());
    }

    public void addVertex(Object v1, String name) {
        vertices.add(v1);
        labels.add(name);
    }

    public boolean containsVertex(Object v1) {
        return vertices.contains(v1);
    }

    /**
     * Finds vertex corresponding to given object and returns the number associated with it.
     * @param vertex the vertex's object
     * @return the index of the vertex in the list of vertices
     */
    public int indexOf(Object vertex) {
        return vertices.indexOf(vertex);
    }

    /**
     * Finds vertex corresponding to given label and returns the number associated with it.
     * @param vertex the vertex's object
     * @return the index of the vertex in the list of vertices
     */
    public int indexOfLabel(String label) {
        return labels.indexOf(label);
    }

    public boolean removeVertex(Object v1) {
        int i = vertices.indexOf(v1);
        if (i != -1) {
            vertices.remove(i);
            labels.remove(i);
            return true;
        }
        return false;
    }

    public Object getObject(int index) {
        return vertices.get(index);
    }

    public String getLabel(int index) {
        return labels.get(index);
    }

    /** Sets label associated with given index. */
    public void setLabel(int index, String newLabel) {
        labels.set(index, newLabel);
    }
    
    void extendVerticesTo(int max) {
        while (size() <= max)
            addVertex(size());
    }

    //
    // EDGES
    //

    public Set<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Collection<Edge> edges) {
        this.edges.clear();
        for (Edge e : edges)
            addEdge(e);
    }

    public void addEdge(Edge edge) {
        if (findEdge(edge.source, edge.sink) != null && !isMultiEdge())
            return;
        edges.add(edge);
        extendVerticesTo(Math.max(edge.source, edge.sink));
    }
    
    public void addEdge(int src, int sink) {
        addEdge(new Edge(src, sink));
    }

    public void addEdge(Object source, Object sink) {
        if (!vertices.contains(source))
            addVertex(source);
        if (!vertices.contains(sink))
            addVertex(sink);
        addEdge(indexOf(source), indexOf(sink));
    }

    public boolean removeEdge(Edge edge) {
        return edges.remove(edge);
    }

    public Edge findEdge(int src, int sink) {
        for (Edge e : edges) {
            if (e.isDirected() && e.source == src && e.sink == sink
                    || !e.isDirected() && e.adjacentTo(src) && e.adjacentTo(sink))
                return e;
        }
        return null;
    }

    public Edge findEdge(Object v1, Object v2) {
        return findEdge(indexOf(v1), indexOf(v2));
    }

    public double getEdgeWeight(Object v1, Object v2) {
        return findEdge(v1, v2).weight;
    }

    public boolean containsEdge(Edge edge) {
        return edges.contains(edge);
    }

    public boolean adjacent(int i1, int i2) {
        return findEdge(i1, i2) != null;
    }

    
    //
    // GENERAL METHODS
    //



    @Override
    public Object clone() {
        SimpleGraph g = new SimpleGraph();
        g.vertices = new ArrayList<Object>();
        g.vertices.addAll(vertices);
        g.labels = new ArrayList<String>();
        g.labels.addAll(labels);
        g.edges = new HashSet<Edge>();
        g.edges.addAll(edges);
        return g;
    }

    @Override
    public String toString() {
        return "SimpleGraph("+size() + " vertices)\n\t" + vertices.toString() + "\n\t" + edges.toString();
    }
}
