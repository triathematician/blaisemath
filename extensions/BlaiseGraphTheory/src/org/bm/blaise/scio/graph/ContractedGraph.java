/*
 * ContractedGraph.java
 * Created May 26, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class ContractedGraph<V> extends GraphSupport<V> {

    protected final Graph<V> parent;
    protected final Collection<V> subset;
    protected final V vertex;
    
    /**
     * Construct a contraction view of another graph, found by "shrinking" the specified subset to a point.
     * @param parent the parent graph
     * @param subset the subset of vertices to include in the subgraph
     * @param vertex the object for the vertex representing the contracted points
     */
    public ContractedGraph(Graph<V> parent, Collection<V> subset, V vertex) {
        super(GraphUtils.contractedNodeList(parent.nodes(), subset, vertex), parent.isDirected());
        this.parent = parent;
        this.subset = subset;
        this.vertex = vertex;
    }
    
    @Override
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
    
    @Override
    public int degree(V x) {
        Set<V> nbhd = neighbors(x);
        return nbhd.size() + (!isDirected() && adjacent(x,x) ? 1 : 0);
    }
    
    public Set<V> neighbors(V x) {
        if (!contains(x)) return Collections.emptySet();
        HashSet<V> result = new HashSet<V>();
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
    
    public int edgeCount() {
        int sum = 0;
        List<V> nodes = nodes();
        for (V v1 : nodes)
            for (V v2 : nodes)
                if (adjacent(v1, v2))
                    sum += v1==v2 && !isDirected() ? 2 : 1;
        return isDirected() ? sum : sum/2;
    }
    
}
