/*
 * WattsStrogatzBuilder.java
 * Created Aug 6, 2010
 */
package org.blaise.graph.modules;

import java.util.ArrayList;
import java.util.TreeSet;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphBuilder;
import org.blaise.graph.GraphBuilders;
import org.blaise.graph.SparseGraph;

/**
 * Provides methods for generating a Watts-Strogatz Random Graph
 *
 * @author Elisha Peterson
 */
public class WattsStrogatzBuilder extends GraphBuilder.Support<Integer> {

    int deg = 4;
    float rewire = .5f;

    public WattsStrogatzBuilder() {
    }

    public WattsStrogatzBuilder(boolean directed, int nodes, int deg, float rewiring) {
        super(directed, nodes);
        if (deg < 0 || deg > nodes - 1) {
            throw new IllegalArgumentException("Degree outside of range [0, " + (nodes - 1) + "]");
        }
        if (rewiring < 0 || rewiring > 1) {
            throw new IllegalArgumentException("Invalid rewiring parameter = " + rewiring + " (should be between 0 and 1)");
        }
        if (deg % 2 != 0) {
            System.err.println("Degree must be an even integer: changing from " + deg + " to " + (deg - 1));
            deg = deg - 1;
        }
        this.deg = deg;
        this.rewire = rewiring;
    }

    public int getInitialDegree() {
        return deg;
    }

    public void setInitialDegree(int deg) {
        this.deg = deg;
    }

    public float getRewiringProbability() {
        return rewire;
    }

    public void setRewiringProbability(float rewire) {
        this.rewire = rewire;
    }

    public Graph<Integer> createGraph() {
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        for (int i = 0; i < nodes; i++) {
            for (int off = 1; off <= (deg / 2); off++) {
                edges.add(new Integer[]{i, (i + off) % nodes});
            }
        }
        // could stop here for a regular ring lattice graph

        // generate list of edges to rewire
        for (Integer[] e : edges) {
            if (Math.random() < rewire) {
                randomlyRewire(edges, e, nodes);
            }
        }

        return new SparseGraph(false, GraphBuilders.intSet(nodes), edges);
    }

    /**
     * Randomly rewires the specified edge, by randomly moving one of the edge's
     * endpoints, provided the resulting edge does not already exist in the set.
     *
     * @param edges current list of edges
     * @param e the edge to rewire
     * @param n total # of vertices
     * @return new edge.
     */
    private static void randomlyRewire(ArrayList<Integer[]> edges, Integer[] e, int n) {
        Integer[] potential = new Integer[]{e[0], e[1]};
        TreeSet<Integer[]> edgeTree = new TreeSet<Integer[]>(EdgeCountBuilder.PAIR_COMPARE_UNDIRECTED);
        edgeTree.addAll(edges);
        while (edgeTree.contains(potential)) {
            if (Math.random() < .5) {
                potential = new Integer[]{e[0], randomNot(e[0], n)};
            } else {
                potential = new Integer[]{randomNot(e[1], n), e[1]};
            }
        }
        e[0] = potential[0];
        e[1] = potential[1];
    }

    /**
     * @returns a random value between 0 and n-1, not including exclude
     */
    private static int randomNot(int exclude, int n) {
        int result = exclude;
        while (result == exclude || result == n) {
            result = (int) Math.floor(n * Math.random());
        }
        return result;
    }
}
