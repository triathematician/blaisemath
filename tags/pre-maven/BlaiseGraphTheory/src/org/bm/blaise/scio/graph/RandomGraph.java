/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 * @author elisha
 */
public class RandomGraph {

    /**
     * Returns graph with random number of connections between vertices
     * @param n number of vertices
     * @param p probability of each edge
     * @param directed whether resulting graph is directed
     * @return directed or undirected graph with randomly chosen edges
     */
    public static Graph<Integer> getInstance(int n, float p, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be positive! n="+n);
        checkProbability(p);
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        for (int i = 0; i < n; i++)
            for (int j = (directed ? 0 : i+1); j < n; j++)
                if (Math.random() < p)
                    edges.add(new Integer[]{i, j});
        return GraphFactory.getGraph(directed, GraphFactory.intList(n), edges);
    }

    /**
     * Returns a graph with specified number of vertices and edges
     * @param nVertices number of vertices
     * @param nEdges number of edges
     * @param directed whether resulting graph is directed
     * @return directed or undirected graph with randomly chosen edges
     */
    public static Graph<Integer> getInstance(int nVertices, int nEdges, boolean directed) {
        // TODO - check for appropriate number of edges
        if (nVertices < 0 || nEdges < 0) throw new IllegalArgumentException("Numbers must be positive! (n,e)=("+nVertices+","+nEdges+")");
        if ((!directed && nEdges > nVertices*(nVertices-1)/2) || (directed && nEdges > nVertices*nVertices)) throw new IllegalArgumentException("Too many edges! (n,e)=("+nVertices+","+nEdges+")");
        TreeSet<Integer[]> edges = new TreeSet<Integer[]>(directed ? PAIR_COMPARE : PAIR_COMPARE_UNDIRECTED);
        Integer[] potential;
        for (int i = 0; i < nEdges; i++) {
            do {
                potential = new Integer[] { (int)(nVertices * Math.random()), (int)(nVertices * Math.random()) };
            } while ((!directed && potential[0]==potential[1]) || edges.contains(potential));
            edges.add(potential);
        }
        return GraphFactory.getGraph(directed, GraphFactory.intList(nVertices), edges);
    }

    /** Ensures probability is valid. */
    private static void checkProbability(float p) {
        if (p < 0 || p > 1) throw new IllegalArgumentException("Probalities must be between 0 and 1: (" + p + " was used.");
    }

    /** Used to sort pairs of integers when order of the two matters. */
    static final Comparator<Integer[]> PAIR_COMPARE = new Comparator<Integer[]>() {
        public int compare(Integer[] o1, Integer[] o2) {
            if (o1.length != 2 || o2.length != 2)
                throw new IllegalStateException("This object only compares integer pairs.");
            return o1[0]==o2[0] ? o1[1]-o2[1] : o1[0]-o2[0];
        }
    };

    /** Used to sort pairs of integers when order of the two does not matter. */
    static final Comparator<Integer[]> PAIR_COMPARE_UNDIRECTED = new Comparator<Integer[]>() {
        public int compare(Integer[] o1, Integer[] o2) {
            if (o1.length != 2 || o2.length != 2)
                throw new IllegalStateException("This object only compares integer pairs.");
            int min1 = Math.min(o1[0], o1[1]);
            int min2 = Math.min(o2[0], o2[1]);
            return min1==min2 ? Math.max(o1[0],o1[1])-Math.max(o2[0],o2[1]) : min1-min2;
        }
    };
}
