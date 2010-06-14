/*
 * PreferentialAttachment.java
 * Created May 27, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Provides static utility methods for generating graphs using preferential attachment.
 *
 * @author Elisha Peterson
 */
public class PreferentialAttachment {

    // no instantiation
    private PreferentialAttachment() {}


    /**
     * Returns random graph generated with a preferential attachment algorithm,
     * starting with a specified seed graph.
     *
     * @param seedGraph a graph used to seed the algorithm
     * @param nVertices number of vertices in final graph
     * @param nEdgesPerStep number of edges to attach at each step
     */
    public static Graph<Integer> getRandomInstance(
            Graph<Integer> seedGraph,
            final int nVertices, int nEdgesPerStep) {

        if (seedGraph.isDirected())
            throw new IllegalArgumentException("getRandomInstance: preferential attachment algorithm requires an undirected seed graph.");
        if (seedGraph.edgeNumber() == 0)
            throw new IllegalArgumentException("getRandomInstance: preferential attachment algorithm requires a seed graph with at least one edge.");

        // prepare parameters for graph to be created
        ArrayList<Integer> vv = new ArrayList<Integer>(seedGraph.nodes());
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        int[] degrees = new int[nVertices];
        int degreeSum = 0;
        Arrays.fill(degrees, 0);

        // initialize with values from seed graph
        for (Integer i1 : seedGraph.nodes())
            for (Integer i2 : seedGraph.nodes())
                if (seedGraph.adjacent(i1, i2))  {
                    edges.add(new Integer[]{i1, i2});
                    degrees[i1]++;
                    degrees[i2]++;
                    degreeSum += 2;
                }
        
        int[] newEdges = new int[nEdgesPerStep];
        int cur = 0;
        while (vv.size() < nVertices) {
            while (vv.contains(cur)) cur++;
            newEdges = weightedRandomVertex(degrees, degreeSum, nEdgesPerStep);
            for (int i = 0; i < newEdges.length; i++) {
                edges.add(new Integer[] {cur, newEdges[i]});
                degrees[cur]++;
                degrees[newEdges[i]]++;
                degreeSum += 2;
            }
            vv.add(cur);
        }
        return Graphs.getInstance(false, vv, edges);
    }

    /**
     * Returns random graph generated via preferential attachment.
     * @param seedGraph a graph used to seed the algorithm
     * @param nVertices number of vertices in final graph
     * @param connectionProbs probabilities of initial #s of connections
     */
    public static Graph<Integer> getRandomInstance(
            Graph<Integer> seedGraph,
            int nVertices, float[] connectionProbs) {

        if (seedGraph.isDirected())
            throw new IllegalArgumentException("getRandomInstance: preferential attachment algorithm requires an undirected seed graph.");
        if (seedGraph.edgeNumber() == 0)
            throw new IllegalArgumentException("getRandomInstance: preferential attachment algorithm requires a seed graph with at least one edge.");
        
        // prepare parameters for graph to be created
        ArrayList<Integer> vv = new ArrayList<Integer>(seedGraph.nodes());
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        int[] degrees = new int[nVertices];
        int degreeSum = 0;
        Arrays.fill(degrees, 0);

        // initialize with values from seed graph
        for (Integer i1 : seedGraph.nodes())
            for (Integer i2 : seedGraph.nodes())
                if (seedGraph.adjacent(i1, i2))  {
                    edges.add(new Integer[]{i1, i2});
                    degrees[i1]++;
                    degrees[i2]++;
                    degreeSum += 2;
                }

        int cur = 0;
        while (vv.size() < nVertices) {
            int nEdgesThisStep = sampleRandom(connectionProbs);
            int[] newEdges = new int[nEdgesThisStep];
            while (vv.contains(cur)) cur++;
            newEdges = weightedRandomVertex(degrees, degreeSum, nEdgesThisStep);
            for (int i = 0; i < newEdges.length; i++) {
                edges.add(new Integer[] {cur, newEdges[i]});
                degrees[cur]++;
                degrees[newEdges[i]]++;
                degreeSum += 2;
            }
            vv.add(cur);
        }
        return Graphs.getInstance(false, vv, edges);
    }



    /**
     * Utility to return random vertices in a graph,
     * whose weights are specified by the given array
     * @param weights array describing the weights of vertices in the graph
     * @param sumWeights the sum of weights
     * @param num the number of results to return
     * @return indices of randomly chosen vertex; will be distinct
     */
    private static int[] weightedRandomVertex(int[] weights, int sumWeights, int num) {
        if (num < 0) throw new IllegalArgumentException("weightedRandomVertex: requires positive # of results: " + num);
        if (num == 0) return new int[]{};

        int[] result = new int[num];
        int nFound = 0;
        double[] random = new double[num];
        for (int i = 0; i < num; i++)
            random[i] = Math.random() * sumWeights;
        double partialSum = 0;
        for (int i = 0; i < weights.length; i++) {
            partialSum += weights[i];
            for (int j = 0; j < num; j++)
                if (partialSum > random[j]) {
                    result[j] = i;
                    if (++nFound == num)
                        return result;
                }
        }
        throw new IllegalStateException("weightedRandomVertex: should not be here since sum random is less than total degree\n" +
                "weights = " + Arrays.toString(weights) + ", sumWeights = " + sumWeights + ", num = " + num);
    }
    
    /** @return index of a randomly chosen # in provided array of probabilities */
    private static int sampleRandom(float[] probs) {
        double rand = Math.random();
        float sum = 0f;
        for (int i = 0; i < probs.length; i++) {
            sum += probs[i];
            if (sum > rand) return i;
        }
        throw new IllegalStateException("Should not be here since sum random is less than total");
    }
}
