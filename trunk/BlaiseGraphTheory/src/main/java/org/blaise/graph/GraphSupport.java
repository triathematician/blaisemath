/*
 * GraphSupport.java
 * Created Oct 29, 2011
 */
package org.blaise.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.blaise.graphics.Edge;

/**
 * Implements the methods of {@link Graph} that can be inferred from other methods.
 * 
 * @author Elisha Peterson
 */
public abstract class GraphSupport<V> implements Graph<V> {

    /** Whether graph is directed */
    protected final boolean directed;
    /** The nodes of the graph */
    protected final Set<V> nodes;
    
    /** 
     * Constructs with the set of nodes.
     * @param nodes graph's nodes
     * @param directed if graph is directed
     */
    public GraphSupport(V[] nodes, boolean directed) {
        this.nodes = new HashSet<V>(Arrays.asList(nodes));
        this.directed = directed;
    }
    
    /** 
     * Constructs with the set of nodes.
     * @param nodes graph's nodes
     * @param directed if graph is directed
     */
    public GraphSupport(Collection<V> nodes, boolean directed) {
        this.nodes = new HashSet<V>(nodes);
        this.directed = directed;
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
            if (e.opposite(x) == y) {
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
                if (e.getNode1() == x) {
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
                if (e.getNode2() == x) {
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
                if (e.getNode1() == x) {
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
                if (e.getNode2() == x) {
                    result++;
                }
            }
            return result;
        }
    }

    public int degree(V x) {
        int result = 0;
        for (Edge<V> e : edgesAdjacentTo(x)) {
            if (e.getNode1() == x) {
                result++;
            }
            if (e.getNode2() == x) {
                result++;
            }
        }
        return result;
    }
    
}
