/*
 * GraphNodeFilter.java
 * Created on Feb 15, 2012
 */
package org.blaise.graph;

import java.util.Set;

/**
 * Retrieves a subset (of nodes) of a graph that matches the query parameters.
 *
 * @author petereb1
 */
public interface GraphNodeFilter {

    /**
     * Apply filter to a graph to produce a set of nodes
     * @param graph a graph
     * @return set of nodes comprising the result of the query
     */
    public <V> Set<V> execute(Graph<V> graph);

}
