/*
 * GraphSupport.java
 * Created Oct 29, 2011
 */
package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Implements the methods of {@link Graph} that can be inferred from other methods.
 * Also maintains the set of connected components of the graph.
 * 
 * @author Elisha Peterson
 */
public abstract class GraphSupport<V> implements Graph<V> {

    /** Whether graph is directed */
    protected final boolean directed;
    /** The nodes of the graph */
    protected final List<V> nodes;
    
    /** 
     * Constructs with the set of nodes.
     */
    public GraphSupport(Collection<V> nodes, boolean directed) {
        this.nodes = new ArrayList<V>(nodes);
        this.directed = directed;
    }
    
    @Override
    public String toString() {
        return GraphUtils.printGraph(this);
    }

    public int order() {
        return nodes.size();
    }

    public List<V> nodes() {
        return Collections.unmodifiableList(nodes);
    }

    public boolean contains(V x) {
        return nodes.contains(x);
    }

    public boolean isDirected() {
        return directed;
    }

    public boolean adjacent(V x, V y) {
        return neighbors(x).contains(y);
    }

    public int degree(V x) {
        return neighbors(x).size();
    }
    
}
