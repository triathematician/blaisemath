/*
 * GraphGrowthRule.java
 * Created on Jun 8, 2012
 */
package org.bm.blaise.scio.graph.query;

import java.util.HashSet;
import java.util.Set;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphUtils;

/**
 * A "growth rule" to make a (small) subgraph larger.
 *
 * @author petereb1
 */
public interface GraphGrowthRule {

    /**
     * Name of rule for display
     * @return name
     */
    public String getName();

    /**
     * Grows a subset of a graph based on some rule. While the resulting set is
     * generally expected to be a superset of the input set, this is not enforced.
     * @param graph the entire graph
     * @param seed the seed input set, a subset of nodes of the graph
     * @return larger subset of the graph
     */
    public Set grow(Graph graph, Set seed);



    //<editor-fold defaultstate="collapsed" desc="IMPLEMENTATIONS">
    //
    // IMPLEMENTATIONS
    //

    /**
     * Construct larger graph by hops.
     */
    public static class HopGrowthRule implements GraphGrowthRule {
        private int n;
        private boolean directed;
        public HopGrowthRule() { this(2); }
        public HopGrowthRule(int n) { this.n = n; }
        public String getName() { return n+"-Hop"; }
        public int getN() { return n; }
        public void setN(int n) { this.n = Math.max(0,n); }
        public boolean isDirected() { return directed; }
        public void setDirected(boolean directed) { this.directed = directed; }
        public Set grow(Graph graph, Set seed) {
            return grow(directed || !graph.isDirected() ? graph : GraphUtils.undirectedCopy(graph), seed, n);
        }
        /** Grows the seed set by n hops */
        public Set grow(Graph graph, Set seed, int n) {
            if (n == 0)
                return seed;
            Set grown = grow1(graph, seed);
            if (grown.containsAll(seed) && seed.containsAll(grown))
                return seed;
            else
                return grow(graph, grown, n-1);
        }
        /** Grows the seed set by 1 hop */
        public Set grow1(Graph graph, Set seed) {
            Set result = new HashSet();
            result.addAll(seed);
            for (Object o : seed)
                result.addAll(graph.neighbors(o));
            return result;
        }
    } // INNER CLASS HopGrowthRule


    /**
     * Construct larger graph by some kind of node similarity metric.
     */
    public static class NodeSimilarityGrowthRule implements GraphGrowthRule {
        public String getName() { return "Node Similarity (TBI)"; }
        public Set grow(Graph graph, Set seed) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }


    /**
     * Construct larger graph using some kind of edge ordering
     */
    public static class OrderedEdgeGrowthRule implements GraphGrowthRule {
        public String getName() { return "Downstream Edges (TBI)"; }
        public Set grow(Graph graph, Set seed) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    //</editor-fold>

}
