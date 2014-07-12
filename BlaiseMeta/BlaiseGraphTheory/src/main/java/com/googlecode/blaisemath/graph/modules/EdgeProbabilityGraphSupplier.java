/**
 * EdgeProbabilityBuilder.java
 * Created Aug 18, 2012
 */

package com.googlecode.blaisemath.graph.modules;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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
import java.util.List;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphSuppliers;
import com.googlecode.blaisemath.graph.GraphSuppliers.GraphSupplierSupport;
import com.googlecode.blaisemath.graph.SparseGraph;

/**
 * Generate random graph with specified edge probability.
 */
public class EdgeProbabilityGraphSupplier extends GraphSupplierSupport<Integer> {

    private float probability;

    public EdgeProbabilityGraphSupplier() {}

    public EdgeProbabilityGraphSupplier(boolean directed, int nodes, float probability) {
        super(directed, nodes);
        checkProbability(probability);
        this.probability = probability;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public Graph<Integer> get() {
        // integer pointers must be unique
        List<Integer> nn = GraphSuppliers.intList(nodes);
        // create edges
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        for (int i = 0; i < nodes; i++) {
            for (int j = directed ? 0 : i + 1; j < nodes; j++) {
                if (Math.random() < probability) {
                    edges.add(new Integer[]{nn.get(i), nn.get(j)});
                }
            }
        }
        return new SparseGraph<Integer>(directed, nn, edges);
    }

    /**
     * Ensures probability is valid.
     */
    static void checkProbability(float p) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("Probalities must be between 0 and 1: (" + p + " was used.");
        }
    }

}
