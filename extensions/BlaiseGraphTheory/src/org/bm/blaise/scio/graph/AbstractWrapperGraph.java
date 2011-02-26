/*
 * AbstractWrapperGraph.java
 * Created May 26, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.List;
import java.util.Set;

/**
 * Provides an abstract implementation of a graph whose methods all delegate to another graph object.
 * Intended for use by classes that add functionality to an existing graph, using a wrapper pattern.
 *
 * @param <V> the type of the nodes
 *
 * @author Elisha Peterson
 */
public abstract class AbstractWrapperGraph<V> implements Graph<V> {

    Graph<V> parent;

    /**
     * Construct graph using specified graph as the base.
     * Initializes all node values to "null".
     * @param parent the base graph
     */
    public AbstractWrapperGraph(Graph<V> parent) {
        this.parent = parent;
    }

    public int order() { return parent.order(); }
    public List<V> nodes() { return parent.nodes(); }
    public boolean contains(V x) { return parent.contains(x); }
    public boolean isDirected() { return parent.isDirected(); }
    public boolean adjacent(V x, V y) { return parent.adjacent(x, y); }
    public int degree(V x) { return parent.degree(x); }
    public Set<V> neighbors(V x) { return parent.neighbors(x); }
    public int edgeNumber() { return parent.edgeNumber(); }
}
