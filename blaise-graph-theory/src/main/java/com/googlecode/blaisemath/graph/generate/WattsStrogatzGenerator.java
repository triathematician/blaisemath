package com.googlecode.blaisemath.graph.generate;

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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphGenerator;
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator.WattsStrogatzParameters;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Generates a Watts-Strogatz Random Graph.
 *
 * @author Elisha Peterson
 */
public final class WattsStrogatzGenerator implements GraphGenerator<WattsStrogatzParameters,Integer> {

    private final Random seed;

    public WattsStrogatzGenerator() {
        this(null);
    }
    
    public WattsStrogatzGenerator(Random seed) {
        this.seed = seed;
    }

    @Override
    public String toString() {
        return "Watts-Strogatz Graph";
    }
    
    @Override
    public WattsStrogatzParameters createParameters() {
        return new WattsStrogatzParameters();
    }

    @Override
    public Graph<Integer> apply(WattsStrogatzParameters parameters) {
        int nodeCount = parameters.getNodeCount();
        int deg = parameters.getInitialDegree();
        double rewire = parameters.getRewiringProbability();
        
        List<Integer[]> edges = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            for (int off = 1; off <= (deg / 2); off++) {
                edges.add(new Integer[]{i, (i + off) % nodeCount});
            }
        }
        // generate list of edges to rewire
        Random r = seed == null ? new Random() : seed;
        for (Integer[] e : edges) {
            if (r.nextDouble() < rewire) {
                randomlyRewire(r, edges, e, nodeCount);
            }
        }

        return GraphGenerators.createGraphWithEdges(parameters, edges);
    }

    //region ALGORITHM

    /**
     * Randomly rewires the specified edge, by randomly moving one of the edge's
     * endpoints, provided the resulting edge does not already exist in the set.
     * @param random random seed
     * @param edges current list of edges
     * @param e the edge to rewire
     * @param n total # of nodes
     */
    private static void randomlyRewire(Random random, List<Integer[]> edges, Integer[] e, int n) {
        if (n <= 1) {
            return;
        }
        Integer[] potential = new Integer[]{e[0], e[1]};
        Set<Integer[]> edgeTree = new TreeSet<>(EdgeCountGenerator.PAIR_COMPARE_UNDIRECTED);
        edgeTree.addAll(edges);
        while (edgeTree.contains(potential)) {
            if (random.nextBoolean()) {
                potential = new Integer[]{e[0], randomNot(random, e[0], n)};
            } else {
                potential = new Integer[]{randomNot(random, e[1], n), e[1]};
            }
        }
        e[0] = potential[0];
        e[1] = potential[1];
    }

    /**
     * Get a random value between 0 and n-1, other than exclude.
     */
    private static int randomNot(Random seed, int exclude, int n) {
        int result;
        do {
            result = seed.nextInt(n);
        } while (result == exclude);
        return result;
    }

    //endregion

    //region PARAMETERS CLASS
    
    /** Parameters for Watts-Strogatz algorithm */
    @SuppressWarnings("WeakerAccess")
    public static final class WattsStrogatzParameters extends DefaultGeneratorParameters {
        private int deg = 4;
        private float rewire = .5f;

        public WattsStrogatzParameters() {
        }

        public WattsStrogatzParameters(boolean directed, int nodes, int deg, float rewiring) {
            super(directed, nodes);
            setInitialDegree(deg);
            setRewiringProbability(rewiring);
        }

        public int getInitialDegree() {
            return deg;
        }

        public void setInitialDegree(int deg) {
            checkArgument(deg >= 0 && deg <= nodeCount-1, "Degree outside of range [0, " + (nodeCount - 1) + "]");
            if (deg % 2 != 0) {
                Logger.getLogger(WattsStrogatzGenerator.class.getName()).log(Level.WARNING, 
                        "Degree must be an even integer: changing from {0} to {1}", new Object[]{deg, deg - 1});
                this.deg = deg-1;
            } else {
                this.deg = deg;
            }
        }

        public float getRewiringProbability() {
            return rewire;
        }

        public void setRewiringProbability(float rewire) {
            checkArgument(rewire >= 0 && rewire <= 1, "Invalid rewiring parameter = " + rewire + " (should be between 0 and 1)");
            this.rewire = rewire;
        }
        
    }
    
    //endregion
}
