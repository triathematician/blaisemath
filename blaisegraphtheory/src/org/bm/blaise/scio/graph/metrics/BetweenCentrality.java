/*
 * BetweenCentrality.java
 * Created Jul 3, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.ArrayList;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.Graphs;
import util.Matrices;

/**
 * <p>
 * Provides a metric describing the betweenness centrality of a vertex in a
 * CONNECTED graph. Returns infinity if the graph is not connected. May take a
 * long time for large graphs.
 * </p>
 * <p>
 * Computationally, the centrality measures the probability that a given node
 * lies on a randomly chosen geodesic.
 * </p>
 *
 * @author Elisha Peterson
 */
public class BetweenCentrality implements NodeMetric<Double> {

    private BetweenCentrality() {}
    private static final BetweenCentrality INSTANCE = new BetweenCentrality();

    /** Factory method to return instance of between centrality */
    public static BetweenCentrality getInstance() { return INSTANCE; }

    /**
     * Computes matrix of distances and #s of shortest paths between any 2 vertices
     * @param n size of matrix/graph
     * @param adj adjacency matrix of graph
     * @param dists n x n matrix to set up with distances (return value)
     * @param nPaths n x n matrix to set up with shortest paths (return value)
     */
    private static void computeShortestPaths(int n, int[][] adj, int[][] dists, int[][] nPaths) {
        int[][] curAdj = new int[n][n];
        int power = 1;
        for (int i = 0; i < n; i++) {
            dists[i][i] = 0;
            nPaths[i][i] = 1;
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                curAdj[i][j] = adj[i][j];
                if (adj[i][j] == 0) {
                    dists[i][j] = nPaths[i][j] = -1;
                } else {
                    dists[i][j] = nPaths[i][j] = 1;
                }
            }
        }
        int nFound = -1;
        while (nFound != 0) {
            nFound = 0;
            curAdj = Matrices.matrixProduct(curAdj, adj);
            power++;
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) {
                    if (i == j) continue;
                    if (dists[i][j] == -1 && curAdj[i][j] != 0) {
                        dists[i][j] = power;
                        nPaths[i][j] = curAdj[i][j];
                        nFound++;
                    }
                }
        }
    } // computeShortestPaths

    /**
     * Computes the betweenness of a specified vertex
     * @param n size of matrix/graph
     * @param dists the matrix of distances between vertices
     * @param nPaths the matrix of # of shortest paths between any two vertices
     * @param v0 the index of the node whose betweenness is to be computed
     * @param directed whether the graph for the computation is directed or not
     * @return the betweenness of the vertex
     */
    private static double computeBetweenness(int n, int[][] dists, int[][] nPaths, int v0, boolean directed) {
        double result = 0.0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j)
                    continue;
                else if ((i == v0 || j == v0) && dists[i][j] != -1)
                    result++;
                else if (dists[i][v0] != -1 && dists[v0][j] != -1 && dists[i][j] != -1 && dists[i][v0] + dists[v0][j] == dists[i][j])
                    result += nPaths[i][v0] * nPaths[v0][j] / (double) nPaths[i][j];
            }
        }
        result /= n*(n-1);
        if (directed) result /= 2;
        return result;
    }

    public <V> Double getValue(Graph<V> graph, V node) {
        List<V> nodes = graph.nodes();
        int n = nodes.size();
        int[][] dists = new int[n][n];
        int[][] nPaths = new int[n][n];
        computeShortestPaths(n, Graphs.adjacencyMatrix(graph), dists, nPaths);
        return computeBetweenness(n, dists, nPaths, nodes.indexOf(node), graph.isDirected());
    }

    public <V> List<Double> getAllValues(Graph<V> graph) {
        List<V> nodes = graph.nodes();
        int n = nodes.size();
        int[][] dists = new int[n][n];
        int[][] nPaths = new int[n][n];
        computeShortestPaths(n, Graphs.adjacencyMatrix(graph), dists, nPaths);
        List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < n; i++)
            result.add(computeBetweenness(n, dists, nPaths, i, graph.isDirected()));
        return result;
    }

}
