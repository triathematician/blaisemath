/*
 * AdjacencyUtils.java
 * Created May 12, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;

/**
 *
 * @author Elisha Peterson
 */
public class AdjacencyUtils {

    /**
     * Computes adjacency matrix of a graph
     * @param <V> type of vertex in the graph
     * @param graph the input graph
     * @return matrix of integers describing adjacencies... contains 0's and 1's...
     *      it is symmetric when the graph is undirected, otherwise it may not be symmetric
     */
    public <V> int[][] adjacency(GraphInterface<V> graph) {
        boolean directed = graph.isDirected();
        ArrayList vertices = new ArrayList<V>();
        vertices.addAll(graph.getVertices());
        int[][] result = new int[vertices.size()][vertices.size()];
        for (Edge e : graph.getEdges()) {
            result[e.source][e.sink] = 1;
            if (!directed)
                result[e.sink][e.source] = 1;
        }
        return result;
    }

    /**
     * Computes several powers of the adjacency matrix
     * @param <V> type of vertex in the graph
     * @param graph the input graph
     * @param maxPower maximum power of the adjacency matrix to include in result
     * @return matrix of integers describing adjacencies... contains 0's and 1's...
     *      it is symmetric when the graph is undirected, otherwise it may not be symmetric
     */
    public <V> int[][][] adjacencyPowers(GraphInterface<V> graph, int maxPower) {
        int[][] adj1 = adjacency(graph);
        int[][][] result = new int[maxPower][adj1.length][adj1[0].length];
        result[0] = adj1;
        int cur = 2;
        while (cur <= maxPower) {
            result[cur - 1] = matrixProduct(result[cur - 2], adj1);
            cur++;
        }
        return result;
    }

    /**
     * Computes product of two matrices
     * First entry is row, second is column.
     * Requires # columns in m1 equal to number of rows in m2
     */
    private int[][] matrixProduct(int[][] m1, int[][] m2) {
        int rows1 = m1.length, cols1 = m1[0].length;
        int rows2 = m2.length, cols2 = m2[0].length;
        if (cols1 != rows2)
            throw new IllegalArgumentException("matrixProduct: incompatible matrix sizes");
        int[][] result = new int[rows1][cols2];
        for (int i = 0; i < rows1; i++)
            for (int j = 0; j < rows2; j++) {
                int sum = 0;
                for (int k = 0; k < rows1; k++)
                    sum += m1[i][k]*m2[k][j];
                result[i][j] = sum;
            }
        return result;
    }

}
