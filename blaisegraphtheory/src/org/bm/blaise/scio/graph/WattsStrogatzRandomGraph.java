/*
 * WattsStrogatzRandomGraph.java
 * Created Aug 6, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.TreeSet;

/**
 * Provides methods for generating a Watts-Strogatz Random Graph
 * @author Elisha Peterson
 */
public class WattsStrogatzRandomGraph {

    /**
     * @param n number of nodes in resulting graph
     * @param deg average degree of resulting graph
     * @param rewiring probability of edge-rewiring in resulting graph
     * @return new instance of a Watts-Strogatz random graph
     */
    public static Graph<Integer> getInstance(int n, int deg, float rewiring) {
        if (n < 1)
            throw new IllegalArgumentException("Invalid order = " + n);
        if (deg % 2 != 0) {
            System.out.println("Degree must be an even integer: changing from " + deg + " to " + (deg-1));
            deg = deg-1;
        }
        if (deg < 0 || deg > n-1)
            throw new IllegalArgumentException("Degree outside of range [0, " + (n-1) + "]");
        if (rewiring < 0 || rewiring > 1)
            throw new IllegalArgumentException("Invalid rewiring parameter = " + rewiring + " (should be between 0 and 1)");

        TreeSet<Integer[]> edges = new TreeSet<Integer[]>(RandomGraph.PAIR_COMPARE_UNDIRECTED);
        for (int i = 0; i < n; i++)
            for (int off = 1; off <= (deg/2); off++)
                edges.add(new Integer[]{i, (i+off)%n});
        // could stop here for a regular ring lattice graph

        // generate list of edges to rewire
        for (Integer[] e : edges)
            if (Math.random() < rewiring)
                randomlyRewire(edges, e, n);

        return GraphFactory.getGraph(false, GraphFactory.intList(n), edges);
    }

    /** 
     * Randomly rewires the specified  edge. This replaces the edge with
     * another edge found by randomly moving one of its endpoints, provided the
     * resulting edge does not already exist.
     * @param edges current list of edges
     * @param e the edge to rewire
     * @param n total # of vertices
     */
    private static void randomlyRewire(TreeSet<Integer[]> edges, Integer[] e, int n) {
        Integer[] potential = new Integer[] {e[0], e[1]};
        while (edges.contains(potential)) {
            if (Math.random() < .5)
                potential = new Integer[] {e[0], randomNot(e[0], n)};
            else
                potential = new Integer[] {randomNot(e[1], n), e[1]};
        }
        e[0] = potential[0];
        e[1] = potential[1];
    }

    /** @returns a random value between 0 and n-1, not including exclude */
    private static int randomNot(int exclude, int n) {
        int result = exclude;
        while (result == exclude || result == n)
            result = (int) Math.floor(n*Math.random());
        return result;
    }

}
