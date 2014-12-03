/*
 * NodeInGraph.java
 * Created on May 17, 2013
 */

package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
