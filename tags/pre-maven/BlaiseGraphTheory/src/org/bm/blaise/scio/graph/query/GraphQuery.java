/*
 * GraphQuery.java
 * Created on Feb 15, 2012
 */
package org.bm.blaise.scio.graph.query;

import java.util.Set;
import org.bm.blaise.scio.graph.Graph;

/**
 * Retrieves a subset (of nodes) of a graph that matches the query parameters.
 *
 * @author petereb1
 */
public interface GraphQuery {

    /**
     * Executes the query against the specified graph.
     * @param graph a graph
     * @return set of nodes comprising the result of the query
     */
    public Set execute(Graph graph);

}
