/*
 * ContractedGraph.java
 * Created May 26, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Provides a wrapper implementation of a graph that contracts a specified subset
 * of nodes to a single node. All of its methods use the "parent" graph and the
 * subset to compute results when the methods are called, without storing any
 * additional data about the graph.
 *
 * @param <V> the type of the nodes
 * @see Subgraph
 * @author Elisha Peterson
 */
public class ContractedGraph<V> implements Graph<V> {

    Graph<V> parent;
    Collection<V> subset;
    V vertex;

    /**
     * Construct a contraction view of another graph, found by "shrinking" the specified subset to a point.
     * @param parent the parent graph
     * @param subset the subset of vertices to include in the subgraph
     * @param vertex the object for the vertex representing the contracted points
     */
    public ContractedGraph(Graph<V> parent, Collection<V> subset, V vertex) {
        if (vertex == null || parent == null || subset == null)
            throw new IllegalArgumentException("Called constructor with null values!");
        this.parent = parent;
        this.subset = subset;
        this.vertex = vertex;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        List<V> nodes = nodes();
        result.append("Nodes: ").append(nodes);
        result.append("; Edges: ");
        for (int i = 0; i < nodes.size(); i++)
            for (int j = (isDirected() ? 0 : i); j < nodes.size(); j++)
                if (adjacent(nodes.get(i), nodes.get(j)))
                    result.append("[" + nodes.get(i) + ", " + nodes.get(j) + "], ");
        result.delete(result.length()-2, result.length());
        return result.toString();
    }

    public int order() { return parent.order() - subset.size() + 1; }
    public List<V> nodes() {
        ArrayList<V> result = new ArrayList<V>();
        result.addAll(parent.nodes());
        result.removeAll(subset);
        result.add(0, vertex);
        return result;
    }
    public boolean contains(V x) { return (parent.contains(x) && !subset.contains(x)) || vertex.equals(x); }
    public boolean isDirected() { return parent.isDirected(); }
    public boolean adjacent(V x, V y) {
        if (!(contains(x) && contains(y)))
            return false;
        else if (vertex.equals(x) && vertex.equals(y)) {
            for (V v1 : subset)
                for (V v2 : subset)
                    if (parent.adjacent(v1, v2)) return true;
            return false;
        } else if (vertex.equals(x)) {
            for (V v : subset)
                if (parent.adjacent(v, y)) return true;
            return false;
        } else if (vertex.equals(y)) {
            for (V v : subset)
                if (parent.adjacent(x, v)) return true;
            return false;
        } else
            return parent.adjacent(x, y);
    }
    public int degree(V x) {
        List<V> nbhd = neighbors(x);
        return nbhd.size() + (!isDirected() && adjacent(x,x) ? 1 : 0);
    }
    public List<V> neighbors(V x) {
        if (!contains(x)) return Collections.emptyList();
        ArrayList<V> result = new ArrayList<V>();
        if (vertex.equals(x)) {
            for (V v : subset)
                result.addAll(parent.neighbors(v));
            if (result.removeAll(subset))
                result.add(x);
            return result;
        } else {
            result.addAll(parent.neighbors(x));
            int size = result.size();
            result.removeAll(subset);
            if (result.size() < size)
                result.add(vertex);
            return result;
        }
    }
    public int edgeNumber() {
        int sum = 0;
        List<V> nodes = nodes();
        for (V v1 : nodes)
            for (V v2 : nodes)
                if (adjacent(v1, v2))
                    sum += v1==v2 && !isDirected() ? 2 : 1;
        return isDirected() ? sum : sum/2;
    }

}
