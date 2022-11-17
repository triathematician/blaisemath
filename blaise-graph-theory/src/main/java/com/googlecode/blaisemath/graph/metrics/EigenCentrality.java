package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.util.Instrument;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.internal.Matrices;

/**
 * Implementation of the eigenvalue centrality calculation. Uses an approximation method to compute the largest eigenvector
 * for the adjacency matrix.
 *
 * @author Elisha Peterson
 */
public class EigenCentrality extends AbstractGraphNodeMetric<Double> {

    private static final Logger LOG = Logger.getLogger(EigenCentrality.class.getName());
    
    public EigenCentrality() {
        super("Eigenvalue centrality (estimated)");
    }

    @Override
    public <N> Double apply(Graph<N> graph, N node) {
        return apply(graph).get(node);
    }

    @Override
    public <N> Map<N, Double> apply(Graph<N> graph) {
        int id = Instrument.start("EigenCentrality.allValues", graph.nodes().size() + " nodes", graph.edges().size() + " edges");

        // computes eigenvalue centrality via repeated powers of the adjacency matrix
        // (this finds the largest-magnitude eigenvector)

        List<N> nodes = new ArrayList<>();
        boolean[][] adj0 = GraphUtils.adjacencyMatrix(graph, nodes);
        int n = nodes.size();
        double[][] mx = new double[n][n];
        for (int i = 0; i < mx.length; i++) {
            for (int j = 0; j < mx.length; j++) {
                mx[i][j] = adj0[i][j] ? 1 : 0;
                mx[i][j] = mx[i][j];
            }
        }
        double[][] powerMatrix = Matrices.matrixProduct(mx, mx);
        for (int i = 0; i < 10; i++) {
          powerMatrix = Matrices.matrixProduct(powerMatrix, powerMatrix);
          normalize(powerMatrix);
        }

        // compute 256 and 257th power vectors
        double[] vec0 = new double[n];
        Arrays.fill(vec0, 1.0 / n);
        double[] powerVector1 = Matrices.matrixProduct(powerMatrix, vec0);
        double[] powerVector2 = Matrices.matrixProduct(mx, powerVector1);

        // estimate eigenvalue for testing purposes
        double[] div = new double[n];
        for (int i = 0; i < n; i++) {
            div[i] = powerVector2[i] / powerVector1[i];
        }
        Instrument.middle(id, "EigenCentrality.allValues", "eigenvalues="+Arrays.toString(div));

        Matrices.normalize(powerVector2);
        for (int i = 0; i < n - 1; i++) {
            if (!(powerVector2[i] * powerVector2[i] > 0)) {
                // should not happen
                LOG.log(Level.SEVERE, "WARNING -- eigenvector has inconsistent signs");
                break;
            }
        }
        double sign = Math.signum(powerVector2[0]);

        Map<N, Double> result = new HashMap<>(n);
        for (int i = 0; i < n; i++) {
            result.put(nodes.get(i), sign * powerVector2[i]);
        }
        Instrument.end(id);
        return result;
    }

    /** Normalize a matrix by dividing by max value */
    private static void normalize(double[][] mx) {
        double max = -Double.MAX_VALUE;
        for (double[] mx1 : mx) {
            for (int j = 0; j < mx.length; j++) {
                max = Math.max(max, mx1[j]);
            }
        }
        for (double[] mx1 : mx) {
            for (int j = 0; j < mx.length; j++) {
                mx1[j] /= max;
            }
        }
    }
}
