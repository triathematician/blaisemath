package com.googlecode.blaisemath.graph.generate;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator.EdgeLikelihoodParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Generate random graph with specified edge probability.
 *
 * @author Elisha Peterson
 */
public final class EdgeLikelihoodGenerator implements GraphGenerator<EdgeLikelihoodParameters,Integer> {
    
    private Random seed = null;

    public EdgeLikelihoodGenerator() {
    }
    
    public EdgeLikelihoodGenerator(Random seed) {
        this.seed = seed;
    }

    @Override
    public String toString() {
        return "Random Graph (fixed Edge Probability)";
    }

    @Override
    public EdgeLikelihoodParameters createParameters() {
        return new EdgeLikelihoodParameters();
    }

    @Override
    public Graph<Integer> apply(EdgeLikelihoodParameters p) {
        boolean directed = p.isDirected();
        Random r = seed == null ? new Random() : seed;
        List<Integer> nn = GraphGenerators.intList(0, p.getNodeCount());
        List<Integer[]> edges = new ArrayList<>();
        for (int i = 0; i < p.getNodeCount(); i++) {
            for (int j = directed ? 0 : i + 1; j < p.getNodeCount(); j++) {
                if (r.nextDouble() < p.getProbability()) {
                    edges.add(new Integer[]{nn.get(i), nn.get(j)});
                }
            }
        }
        return GraphUtils.createFromArrayEdges(directed, nn, edges);
    }

    //region PARAMETERS CLASS
    
    /** Parameters for edge probability generator */
    public static final class EdgeLikelihoodParameters extends DefaultGeneratorParameters {
        private float probability = .1f;

        public EdgeLikelihoodParameters() {
        }

        public EdgeLikelihoodParameters(boolean directed, int nodes, float prob) {
            super(directed, nodes);
            setProbability(prob);
        }

        public float getProbability() {
            return probability;
        }

        public void setProbability(float probability) {
            checkArgument(probability >= 0 && probability <= 1);
            this.probability = probability;
        }
        
    }
    
    //endregion
}
