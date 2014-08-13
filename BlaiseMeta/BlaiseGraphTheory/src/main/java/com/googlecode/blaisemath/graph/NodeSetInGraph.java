/*
 * NodeSetInGraph.java
 * Created on May 23, 2013
 */

package com.googlecode.blaisemath.graph;


import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Data structure describing a collection of nodes in a graph. This is a useful way to pass
 * a reference to both the collection of nodes and the graph.
 * 
 * @author petereb1
 */
public class NodeSetInGraph<E> {
    
    public static <E> NodeSetInGraph<E> create(E... nodes) {
        return new NodeSetInGraph<E>(Sets.newHashSet(nodes), null);
    }
    

    private final Set<E> nodes;
    private final Graph<E> graph;

    public NodeSetInGraph(Set<E> nodes, @Nullable Graph<E> graph) {
        checkNotNull(nodes);
        this.graph = graph;
        // allow for nodes not in graph, but remove them
        this.nodes = Sets.newLinkedHashSet(nodes);
        if (graph != null) {
            this.nodes.retainAll(graph.nodes());
        }
    }

    public Set<E> getNodes() {
        return nodes;
    }

    @Nullable 
    public Graph<E> getGraph() {
        return graph;
    }
}
