/**
 * OptimizedGraph.java
 * Created Aug 18, 2012
 */

package org.blaise.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *   A storage-optimized graph component that caches degrees and divides nodes into
 *   <i>isolates</i>, <i>leaf nodes</i>, <i>connector nodes</i> (degree 2), and
 *   <i>core nodes</i> (degree > 2).
 *   This maximizes speed for algorithms that make large numbers of calls to
 *   graph API methods.
 * </p>
 * @author elisha
 */
public class OptimizedGraph<V> extends SparseGraph<V> {

    /** Degree cache */
    private final Map<V, Integer> degrees = new HashMap<V, Integer>();
    /** Isolate nodes (deg = 0) */
    private final Set<V> isolates = new HashSet<V>();
    /** Leaf nodes (deg = 1) */
    private final Set<V> leafNodes = new HashSet<V>();
    /** Connector nodes (deg = 2) */
    private final Set<V> connectorNodes = new HashSet<V>();
    /** Non-leaf nodes (deg >= 3) */
    private final Set<V> coreNodes = new HashSet<V>();
    /** General objects adjacent to each node */
    private final Map<V, Set<V>> neighbors = new HashMap<V, Set<V>>();
    /**
     * Leaf objects adjacent to each node. Values consist of objects that
     * have degree 1 ONLY.
     */
    private final Map<V, Set<V>> adjLeaves = new HashMap<V, Set<V>>();


    //
    // CONSTRUCTORS
    //

    /**
     * Construct graph with specific nodes and edges
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     */
    public OptimizedGraph(boolean directed, V[] nodes, Iterable<V[]> edges) {
        super(directed, nodes, edges);
        initCachedElements();
    }

    /**
     * Construct graph with specific nodes and edges
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     */
    public OptimizedGraph(boolean directed, Collection<V> nodes, Iterable<V[]> edges) {
        super(directed, nodes, edges);
        initCachedElements();
    }
    
    /** 
     * Construct graph with a sparse adjacency representation.
     * @param directed if graph is directed
     * @param adjacencies map with adjacency info
     */
    public OptimizedGraph(boolean directed, Map<V, Set<V>> adjacencies) {
        super(directed, adjacencies);
        initCachedElements();
    }
    
    private void initCachedElements() {
        for (V v : nodes) {
            int deg = super.degree(v);
            degrees.put(v, deg);
            switch (deg) {
                case 0: isolates.add(v); break;
                case 1: leafNodes.add(v); break;
                case 2: connectorNodes.add(v); break;
                default: coreNodes.add(v); break;
            }
            neighbors.put(v, super.neighbors(v));
            adjLeaves.put(v, new HashSet<V>());
        }
        for (V v : nodes) {
            for (V y : neighbors.get(v)) {
                if (degrees.get(y) == 1) {
                    adjLeaves.get(v).add(y);
                }
            }
        }
    }    

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public Set<V> getIsolates() {
        return isolates;
    }

    public Set<V> getLeafNodes() {
        return leafNodes;
    }

    public Set<V> getConnectorNodes() {
        return connectorNodes;
    }

    public Set<V> getCoreNodes() {
        return coreNodes;
    }

    public Map<V, Set<V>> getNeighbors() {
        return neighbors;
    }

    //</editor-fold>

    /**
     * Return leaf nodes adjacent to specified node
     * @param v node to check
     * @return leaf ndoes
     */
    public Set<V> getAdjacentLeafNodes(V v) {
        return adjLeaves.get(v);
    }
    

    //
    // OVERRIDES
    //

    @Override
    public boolean adjacent(V x, V y) {
        return neighbors.get(x).contains(y);
    }

    @Override
    public Set<V> neighbors(V x) {
        return neighbors.get(x);
    }

    @Override
    public int degree(V x) {
        return degrees.get(x);
    }
    
    

}
