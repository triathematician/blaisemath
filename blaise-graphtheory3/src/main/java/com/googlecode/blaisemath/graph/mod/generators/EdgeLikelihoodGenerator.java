package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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
import com.googlecode.blaisemath.graph.mod.generators.EdgeLikelihoodGenerator.EdgeLikelihoodParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Generate random graph with specified edge probability.
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
    public Graph<Integer> apply(EdgeLikelihoodParameters parm) {
        boolean directed = parm.isDirected();
        Random r = seed == null ? new Random() : seed;
        List<Integer> nn = DefaultGeneratorParameters.intList(parm.getNodeCount());
        List<Integer[]> edges = new ArrayList<Integer[]>();
        for (int i = 0; i < parm.getNodeCount(); i++) {
            for (int j = directed ? 0 : i + 1; j < parm.getNodeCount(); j++) {
                if (r.nextDouble() < parm.getProbability()) {
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
