/*
 * NodeInGraph.java
 * Created on May 17, 2013
 */

package com.googlecode.blaisemath.graph;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;

/**
 * Data structure describing a node in a graph. This is a useful way to pass
 * a reference to a graph's node, providing the target object with access to
 * both the node and the graph.
 *
 * @author petereb1
 */
public final class NodeInGraph<E> {

    private final E node;
    private final Graph<E> graph;

    public NodeInGraph(E node, @Nullable Graph<E> graph) {
        this.node = checkNotNull(node);
        this.graph = graph;
        checkArgument(graph == null || graph.contains(node));
    }

    public static <E> NodeInGraph<E> create(E node) {
        return new NodeInGraph<E>(node, null);
    }

    public E getNode() {
        return node;
    }

    @Nullable 
    public Graph<E> getGraph() {
        return graph;
    }
}
