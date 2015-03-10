/*
 * PreferentialAttachmentGraphSupplier.java
 * Created May 27, 2010
 */
package com.googlecode.blaisemath.graph.modules.suppliers;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import java.util.List;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.GraphSupplierSupport;
import com.googlecode.blaisemath.graph.SparseGraph;

/**
 * Provides static utility methods for generating graphs using preferential
 * attachment.
 *
 * @author Elisha Peterson
 */
public final class PreferentialAttachmentGraphSupplier extends GraphSupplierSupport<Integer> {

    private Graph<Integer> seed;
    private int edgesPerStep = 1;
    private float[] connectProbs = null;

    public PreferentialAttachmentGraphSupplier(Graph<Integer> seed) {
        setSeed(seed);
    }

    public PreferentialAttachmentGraphSupplier(Graph<Integer> seed, int nodes, int edgesPerStep) {
        super(seed.isDirected(), nodes);
        setSeed(seed);
        setEdgesPerStep(edgesPerStep);
    }

    public PreferentialAttachmentGraphSupplier(Graph<Integer> seed, int nodes, float[] probs) {
        super(seed.isDirected(), nodes);
        setSeed(seed);
        setConnectProbabilities(probs);
    }

    @Override
    public String toString() {
        return "PreferentialAttachmentGraphSupplier{" + "edgesPerStep=" + edgesPerStep 
                + ", connectProbs=" + Arrays.toString(connectProbs) + '}';
    }

    public Graph<Integer> getSeed() {
        return seed;
    }

    public void setSeed(Graph<Integer> seed) {
        if (seed == null) {
            throw new NullPointerException();
        } else if (seed.edgeCount() == 0) {
            throw new IllegalArgumentException("PreferentialAttachment seed must be non-empty: " + seed);
        } else if (seed.isDirected()) {
            throw new UnsupportedOperationException("Algorithm not supported for directed graphs: " + seed);
        }
        this.seed = seed;
    }

    public int getEdgesPerStep() {
        return edgesPerStep;
    }

    public void setEdgesPerStep(int edgesPerStep) {
        if (edgesPerStep < 0) {
            throw new IllegalArgumentException();
        }
        this.edgesPerStep = edgesPerStep;
    }

    public float[] getConnectProbabilities() {
        return connectProbs;
    }

    /**
     * Probabilities of initial #s of connections; the i'th entry is the
     * probability that a new node will have i connections, starting at 0
     * @param connectProbs array describing probabilities of connections by degree
     */
    public void setConnectProbabilities(float[] connectProbs) {
        this.connectProbs = Arrays.copyOf(connectProbs, connectProbs.length);
    }

    @Override
    public Graph<Integer> get() {
        return connectProbs == null
                ? generate(seed, nodes, edgesPerStep)
                : generate(seed, nodes, connectProbs);
    }

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
                if (seedGraph.adjacent(i1, i2)) {
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
        return SparseGraph.createFromArrayEdges(false, nodes, edges);
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
     * Utility to return random vertices in a graph, whose weights are specified
     * by the given array
     *
     * @param weights array describing the weights of vertices in the graph
     * @param sumWeights the sum of weights
     * @param num the number of results to return
     * @return indices of randomly chosen vertex; will be distinct
     */
    static int[] weightedRandomVertex(int[] weights, int sumWeights, int num) {
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
     * @return index of a randomly chosen # in provided array of probabilities
     */
    static int sampleRandom(float[] probs) {
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
}
