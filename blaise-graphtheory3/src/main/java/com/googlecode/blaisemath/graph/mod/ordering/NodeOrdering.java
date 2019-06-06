package com.googlecode.blaisemath.graph.mod.ordering;

import com.googlecode.blaisemath.graph.Graph;
import java.util.List;

/**
 * Provides an ordering of nodes in a graph.
 * @author petereb1
 */
public interface NodeOrdering<C> {

    /**
     * Compute node order in a graph.
     * @param graph the graph
     * @return ordered list of (all) nodes in the graph
     */
    List<C> order(Graph<C> graph);
    
}
