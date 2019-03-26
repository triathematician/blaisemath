/*
 * EigenCentrality.java
 * Created Jul 12, 2010
 */

package com.googlecode.blaisemath.graph.mod.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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
import com.googlecode.blaisemath.util.GAInstrument;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.linear.Matrices;

/**
 * Implementation of the eigenvalue centrality calculation.
 *
 * @author Elisha Peterson
 */
public class EigenCentrality extends AbstractGraphNodeMetric<Double> {

    private static final Logger LOG = Logger.getLogger(EigenCentrality.class.getName());
    
    public EigenCentrality() {
        super("Eigenvalue centrality (estimated)");
    }

    @Override
    public <V> Double apply(Graph<V> graph, V node) {
        return apply(graph).get(node);
    }

    @Override
    public <V> Map<V,Double> apply(Graph<V> graph) {
        int id = GAInstrument.start("EigenCentrality.allValues", graph.nodeCount()+" nodes", graph.edgeCount()+" edges");

        // computes eigenvalue centrality via repeated powers of the adjacency matrix
        // (this finds the largest-magnitude eigenvector)

        List<V> nodes = new ArrayList<V>();
        boolean[][] adj0 = GraphUtils.adjacencyMatrix(graph, nodes);
        int n = nodes.size();
        int[][] mx = new int[adj0.length][adj0.length];
        for (int i = 0; i < mx.length; i++) {
            for (int j = 0; j < mx.length; j++) {
                mx[i][j] = adj0[i][j] ? 1 : 0;
            }
        }
        double[][] mx2 = new double[n][n]; 
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                mx2[i][j]=mx[i][j];
            }
        }
        double[][] dmx = Matrices.matrixProduct(mx2, mx2);
        for (int i = 0; i < 10; i++) {
          dmx = Matrices.matrixProduct(dmx, dmx);
          normalize(dmx);
        }

        // compute 256 and 257th power vecs
        double[] vec0 = new double[n];
        Arrays.fill(vec0, 1.0 / n);
        double[] vecf1 = Matrices.matrixProduct(dmx, vec0);
        double[] vecf2 = Matrices.matrixProduct(mx2, vecf1);

        // estimate eigenvalue for testing purposes
        double[] div = new double[n];
        for (int i = 0; i < div.length; i++) {
            div[i] = vecf2[i] / vecf1[i];
        }
        GAInstrument.middle(id, "EigenCentrality.allValues", "eigenvalues="+Arrays.toString(div));

        Matrices.normalize(vecf2);
        for (int i = 0; i < n-1; i++) {
            if (!(vecf2[i]*vecf2[i]>0)) {
                // should not happen
                LOG.log(Level.SEVERE, "WARNING -- eigenvector has inconsistent signs");
                break;
            }
        }
        double sign = Math.signum(vecf2[0]);

        Map<V,Double> result = new HashMap<V,Double>(n);
        for (int i = 0; i < div.length; i++) {
            result.put(nodes.get(i), sign*vecf2[i]);
        }
        GAInstrument.end(id);
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
