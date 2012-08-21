/*
 * EigenCentrality.java
 * Created Jul 12, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphUtils;
import org.bm.blaise.scio.matrix.Matrices;

/**
 * Implementation of the eigenvalue centrality calculation.
 *
 * @author Elisha Peterson
 */
public class EigenCentrality extends NodeMetricSupport<Double> {

    public EigenCentrality() {
        super("Approximated Eigenvalue Centrality", true, true);
    }

    public double nodeMax(boolean directed, int order) { 
        return 1.0; 
    }
    
    public double centralMax(boolean directed, int order) { 
        return Double.NaN; 
    }

    public <V> Double value(Graph<V> graph, V node) {
        return allValues(graph).get(graph.nodes().indexOf(node));
    }

    public <V> List<Double> allValues(Graph<V> graph) {
        System.out.print("EC-all (" + graph.order() + " nodes, " + graph.edgeCount() + " edges): ");
        long l0 = System.currentTimeMillis();
        // computes eigenvalue centrality via repeated powers of the adjacency matrix
        // (this finds the largest-magnitude eigenvector)

        int n = graph.nodes().size();
        boolean[][] adj0 = GraphUtils.adjacencyMatrix(graph);
        int[][] mx = new int[adj0.length][adj0.length];
        for (int i = 0; i < mx.length; i++)
            for (int j = 0; j < mx.length; j++)
                mx[i][j] = adj0[i][j] ? 1 : 0;
        double[][] mx2 = new double[n][n]; for(int i=0;i<n;i++)for(int j=0;j<n;j++) mx2[i][j]=mx[i][j];
        double[][] dmx = Matrices.matrixProduct(mx2, mx2);
        for (int i = 0; i < 10; i++) {
          dmx = Matrices.matrixProduct(dmx, dmx);
          normalize(dmx);
        }

        // compute 256 and 257th power vecs
        double[] vec0 = new double[n]; Arrays.fill(vec0, 1.0/n);
        double[] vecf1 = Matrices.matrixProduct(dmx, vec0);
        double[] vecf2 = Matrices.matrixProduct(mx2, vecf1);

        // estimate eigenvalue for testing purposes
        double[] div = new double[n];
        for (int i = 0; i < div.length; i++) div[i] = vecf2[i] / vecf1[i];
        System.err.println("WARNING -- this algorithm currently only **approximates** eigenvalue centrality and should be fixed to compute exact eigenvalues!!");
        System.err.println("  eigenCentrality.evals = " + Arrays.toString(div));

        Matrices.normalize(vecf2);
        for (int i = 0; i < n-1; i++)
            if (!(vecf2[i]*vecf2[i]>0)) {
                // should not happen
                System.out.println("WARNING -- eigenvector has inconsistent signs");
                break;
            }
        double sign = Math.signum(vecf2[0]);

        List<Double> result = new ArrayList<Double>(n);
        for (int i = 0; i < div.length; i++)
            result.add(i, sign*vecf2[i]);
        System.out.println((System.currentTimeMillis()-l0)+"ms");
        return result;
    }

    /** Normalize a matrix by dividing by max value */
    private static void normalize(double[][] mx) {
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < mx.length; i++)
            for (int j = 0; j < mx.length; j++)
                max = Math.max(max, mx[i][j]);
        for (int i = 0; i < mx.length; i++)
            for (int j = 0; j < mx.length; j++)
                mx[i][j] /= max;
    }
}
