/*
 * GraphSupport.java
 * Created Oct 29, 2011
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

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import com.googlecode.blaisemath.util.Edge;

/**
 * Implements the methods of {@link Graph} that can be inferred from other methods.
 * The nodes are maintained as a {@link LinkedHashSet}.
 *
 * @author Elisha Peterson
 */
public abstract class GraphSupport<V> implements Graph<V> {

    /** Whether graph is directed */
    protected final boolean directed;
    /** The nodes of the graph */
    protected final Set<V> nodes;

    /**
     * Constructs with the set of nodes. The nodes are copied from the supplied
     * collection into a new data structure.
     * @param directed if graph is directed
     * @param nodes graph's nodes
     */
    public GraphSupport(boolean directed, Iterable<V> nodes) {
        this.directed = directed;
        this.nodes = Collections.unmodifiableSet(Sets.newLinkedHashSet(nodes));
    }

    @Override
    public String toString() {
        return GraphUtils.printGraph(this);
    }

    public boolean isDirected() {
        return directed;
    }

    //
    // NODES
    //

    public Set<V> nodes() {
        return Collections.unmodifiableSet(nodes);
    }

    public int nodeCount() {
        return nodes.size();
    }

    public boolean contains(V x) {
        return nodes.contains(x);
    }

    //
    // EDGES
    //

    public int edgeCount() {
        return edges().size();
    }


    //
    // ADJACENCY
    //

    public boolean adjacent(V x, V y) {
        for (Edge<V> e : edgesAdjacentTo(x)) {
            if (y.equals(e.opposite(x))) {
                return true;
            }
        }
        return false;
    }

    public Set<V> outNeighbors(V x) {
        if (!directed) {
            return neighbors(x);
        } else {
            Set<V> result = new HashSet<V>();
            for (Edge<V> e : edgesAdjacentTo(x)) {
                if (x.equals(e.getNode1())) {
                    result.add(e.getNode2());
                }
            }
            return result;
        }
    }

    public Set<V> inNeighbors(V x) {
        if (!directed) {
            return neighbors(x);
        } else {
            Set<V> result = new HashSet<V>();
            for (Edge<V> e : edgesAdjacentTo(x)) {
                if (x.equals(e.getNode2())) {
                    result.add(e.getNode1());
                }
            }
            return result;
        }
    }

    public Set<V> neighbors(V x) {
        Set<V> result = new HashSet<V>();
        for (Edge<V> e : edgesAdjacentTo(x)) {
            result.add(e.opposite(x));
        }
        return result;
    }

    public int outDegree(V x) {
        if (!directed) {
            return degree(x);
        } else {
            int result = 0;
            for (Edge<V> e : edgesAdjacentTo(x)) {
                if (x.equals(e.getNode1())) {
                    result++;
                }
            }
            return result;
        }
    }

    public int inDegree(V x) {
        if (!directed) {
            return degree(x);
        } else {
            int result = 0;
            for (Edge<V> e : edgesAdjacentTo(x)) {
                if (x.equals(e.getNode2())) {
                    result++;
                }
            }
            return result;
        }
    }

    public int degree(V x) {
        int result = 0;
        for (Edge<V> e : edgesAdjacentTo(x)) {
            // permit double counting if both vertices of edge are x
            if (x.equals(e.getNode1())) {
                result++;
            }
            if (x.equals(e.getNode2())) {
                result++;
            }
        }
        return result;
    }

}
