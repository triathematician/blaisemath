/**
 * HopGrowthRule.java
 * Created on Jun 8, 2012
 */
package com.googlecode.blaisemath.graph.query;

import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import java.util.HashSet;
import java.util.Set;

/**
 * Construct larger graph by hops.
 * @author Elisha Peterson
 */
public class HopGrowthRule implements GraphGrowthRule {
    private int n;
    private boolean directed;

    public HopGrowthRule() {
        this(2);
    }

    public HopGrowthRule(int n) {
        this.n = n;
    }

    @Override
    public String getName() {
        return n + "-Hop";
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = Math.max(0, n);
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    @Override
    public Set grow(Graph graph, Set seed) {
        return grow(directed || !graph.isDirected() ? graph : GraphUtils.copyAsUndirectedSparseGraph(graph), seed, n);
    }

    /**
     * Grows the seed set by n hops
     * @param graph graph
     * @param seed seed nodes
     * @param n # of steps to grow
     * @return nodes in grown set
     */
    public Set grow(Graph graph, Set seed, int n) {
        if (n == 0) {
            return seed;
        }
        Set grown = grow1(graph, seed);
        if (grown.containsAll(seed) && seed.containsAll(grown)) {
            return seed;
        } else {
            return grow(graph, grown, n - 1);
        }
    }

    /**
     * Grows the seed set by 1 hop
     * @param graph graph
     * @param seed seed nodes
     * @return nodes in grown set
     */
    public Set grow1(Graph graph, Set seed) {
        Set result = new HashSet();
        result.addAll(seed);
        for (Object o : seed) {
            result.addAll(graph.neighbors(o));
        }
        return result;
    }
    
}