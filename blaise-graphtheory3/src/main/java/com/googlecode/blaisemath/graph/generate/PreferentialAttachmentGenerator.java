package com.googlecode.blaisemath.graph.generate;

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
import com.googlecode.blaisemath.graph.generate.PreferentialAttachmentGenerator.PreferentialAttachmentParameters;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Provides static utility methods for generating graphs using preferential
 * attachment.
 *
 * @author Elisha Peterson
 */
public final class PreferentialAttachmentGenerator implements GraphGenerator<PreferentialAttachmentParameters,Integer> {

    @Override
    public String toString() {
        return "Preferential Attachment Graph";
    }

    @Override
    public PreferentialAttachmentParameters createParameters() {
        return new PreferentialAttachmentParameters();
    }

    @Override
    public Graph<Integer> apply(PreferentialAttachmentParameters parm) {
        if (parm.getConnectProbabilities() == null) {
            return generate(parm.generateSeedGraph(), parm.getNodeCount(), parm.getEdgesPerStep());
        } else {
            return generate(parm.generateSeedGraph(), parm.getNodeCount(), parm.getConnectProbabilities());
        }
    }

    //region ALGORITHM
    
    /**
     * Common method for preferential attachment algorithm
     */
    private static Graph<Integer> generate(Graph<Integer> seedGraph, final int nVertices, Object edgesPerStep) {
        // prepare parameters for graph to be created
        List<Integer> nodes = new ArrayList<Integer>(seedGraph.nodes());
        List<Integer[]> edges = new ArrayList<Integer[]>();
        int[] degrees = new int[nVertices];
        Arrays.fill(degrees, 0);
        int degreeSum = 0;

        // initialize with values from seed graph
        for (Integer i1 : nodes) {
            for (Integer i2 : nodes) {
                if (seedGraph.hasEdgeConnecting(i1, i2)) {
                    degreeSum += addEdge(edges, degrees, i1, i2);
                }
            }
        }

        int cur = 0;
        boolean variableEdgeNumber = edgesPerStep instanceof float[];
        int numberEdgesToAdd = variableEdgeNumber ? 0 : (Integer) edgesPerStep;
        float[] connectionProbs = variableEdgeNumber ? (float[]) edgesPerStep : new float[]{};

        while (nodes.size() < nVertices) {
            while (nodes.contains(cur)) {
                cur++;
            }
            nodes.add(cur);
            if (variableEdgeNumber) {
                numberEdgesToAdd = sampleRandom(connectionProbs);
            }
            degreeSum += addEdge(edges, degrees, cur, weightedRandomVertex(degrees, degreeSum, numberEdgesToAdd));
        }
        return GraphUtils.createFromArrayEdges(false, nodes, edges);
    }

    /**
     * Utility to add specified vertices to the edge set and increment the
     * corresponding degrees.
     *
     * @param edges current list of edges
     * @param degrees current list of degrees
     * @param v1 first vertex of edge to add
     * @param attachments second vertex (vertices) of edges to add
     * @return number of new degrees added
     */
    static int addEdge(List<Integer[]> edges, int[] degrees, int v1, int... attachments) {
        for (int v : attachments) {
            edges.add(new Integer[]{v1, v});
            degrees[v]++;
        }
        degrees[v1] += attachments.length;
        return attachments.length * 2;
    }

    /**
     * Utility to return random vertices in a graph, whose weights are specified by the given array.
     *
     * @param weights array describing the weights of vertices in the graph
     * @param sumWeights the sum of weights
     * @param num the number of results to return
     * @return indices of randomly chosen vertex; will be distinct
     */
    public static int[] weightedRandomVertex(int[] weights, int sumWeights, int num) {
        if (num < 0) {
            throw new IllegalArgumentException("weightedRandomVertex: requires positive # of results: " + num);
        }
        if (num == 0) {
            return new int[]{};
        }

        int[] result = new int[num];
        int nFound = 0;
        double[] random = new double[num];
        for (int i = 0; i < num; i++) {
            random[i] = Math.random() * sumWeights;
        }
        double partialSum = 0;
        for (int i = 0; i < weights.length; i++) {
            partialSum += weights[i];
            for (int j = 0; j < num; j++) {
                if (partialSum > random[j]) {
                    result[j] = i;
                    if (++nFound == num) {
                        return result;
                    }
                }
            }
        }
        throw new IllegalStateException("weightedRandomVertex: should not be here since sum random is less than total degree\n"
                + "weights = " + Arrays.toString(weights) + ", sumWeights = " + sumWeights + ", num = " + num);
    }

    /**
     * Generate a random index based on a probability array.
     * @param probs the probability array
     * @return index of a randomly chosen # in provided array of probabilities
     */
    public static int sampleRandom(float[] probs) {
        double rand = Math.random();
        float sum = 0f;
        for (int i = 0; i < probs.length; i++) {
            sum += probs[i];
            if (sum > rand) {
                return i;
            }
        }
        throw new IllegalStateException("Should not be here since sum random is less than total");
    }
    
    //endregion
    
    //region PARAMETERS CLASS
    
    /** Parameters for preferential attachment */
    public static final class PreferentialAttachmentParameters extends DefaultGeneratorParameters {

        /** If using edge count generator, the default seed graph generator */
        private @Nullable ExtendedGeneratorParameters seedParameters;
        /** If specifying a seed graph directly */
        private @Nullable Graph<Integer> seedGraph;
        
        /** If using fixed # of edges per step */
        private int edgesPerStep = 1;
        /** If using probability-based # of connections per step */
        private @Nullable float[] connectProbs = null;

        public PreferentialAttachmentParameters() {
            seedParameters = new ExtendedGeneratorParameters(false, 10, 10);
        }

        public PreferentialAttachmentParameters(ExtendedGeneratorParameters p) {
            setSeedParameters(p);
        }

        public PreferentialAttachmentParameters(ExtendedGeneratorParameters p, int nodeCount, int edgesPerStep) {
            super(p.isDirected(), nodeCount);
            setSeedParameters(p);
            setEdgesPerStep(edgesPerStep);
        }

        public PreferentialAttachmentParameters(ExtendedGeneratorParameters p, int nodeCount, float[] probs) {
            super(p.isDirected(), nodeCount);
            setSeedParameters(p);
            setConnectProbabilities(probs);
        }

        public PreferentialAttachmentParameters(Graph<Integer> seed) {
            setSeedGraph(seed);
        }

        public PreferentialAttachmentParameters(Graph<Integer> seed, int nodeCount, int edgesPerStep) {
            super(seed.isDirected(), nodeCount);
            setSeedGraph(seed);
            setEdgesPerStep(edgesPerStep);
        }

        public PreferentialAttachmentParameters(Graph<Integer> seed, int nodeCount, float[] probs) {
            super(seed.isDirected(), nodeCount);
            setSeedGraph(seed);
            setConnectProbabilities(probs);
        }

        public Graph<Integer> generateSeedGraph() {
            return seedGraph != null ? seedGraph : EdgeCountGenerator.getInstance().apply(seedParameters);
        }
        
        //region PROPERTIES


        public @Nullable ExtendedGeneratorParameters getSeedGraphParameters() {
            return seedParameters;
        }

        public void setSeedParameters(@Nullable ExtendedGeneratorParameters seedParameters) {
            this.seedParameters = seedParameters;
        }

        public @Nullable Graph<Integer> getSeedGraph() {
            return seedGraph;
        }

        public void setSeedGraph(@Nullable Graph<Integer> seed) {
            requireNonNull(seed);
            checkArgument(seed.edges().size() > 0, "PreferentialAttachment seed must be non-empty: " + seed);
            checkArgument(!seed.isDirected(), "Algorithm not supported for directed graphs: " + seed);
            this.seedGraph = seed;
        }

        public int getEdgesPerStep() {
            return edgesPerStep;
        }

        public void setEdgesPerStep(int edgesPerStep) {
            checkArgument(edgesPerStep >= 0);
            this.edgesPerStep = edgesPerStep;
        }

        public @Nullable float[] getConnectProbabilities() {
            return connectProbs;
        }

        /**
         * Probabilities of initial #s of connections; the i'th entry is the probability that a new node will have i connections, starting at 0.
         * @param connectProbs array describing probabilities of connections by degree
         */
        public void setConnectProbabilities(@Nullable float[] connectProbs) {
            this.connectProbs = connectProbs == null ? null : Arrays.copyOf(connectProbs, connectProbs.length);
        }
        
        //endregion

    }
    
    //endregion
    
}
