/*
 * PreferentialAttachment.java
 * Created May 27, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides static utility methods for generating graphs using preferential attachment.
 *
 * @author Elisha Peterson
 */
public class PreferentialAttachment {

    // no instantiation
    private PreferentialAttachment() {}

    /**
     * Returns random undirected graph generated with a preferential attachment algorithm,
     * starting with a specified seed graph.
     *
     * @param seedGraph a graph used to seed the algorithm
     * @param nVertices number of vertices in final graph
     * @param nEdgesPerStep number of edges to attach at each step
     * @return the randomly generated graph
     */
    public static Graph<Integer> getSeededInstance(
            Graph<Integer> seedGraph,
            int nVertices, int nEdgesPerStep) {
        return generate(seedGraph, nVertices, (Integer) nEdgesPerStep);
    }

    /**
     * Returns random undirected graph generated via preferential attachment,
     * starting with a specified seed graph.
     *
     * @param seedGraph a graph used to seed the algorithm
     * @param nVertices number of vertices in final graph
     * @param connectionProbs probabilities of initial #s of connections; the i'th
     *   entry is the probability that a new node will have i connections, starting at 0
     * @return the randomly generated graph
     */
    public static Graph<Integer> getSeededInstance(
            Graph<Integer> seedGraph,
            int nVertices, float[] connectionProbs) {
        return generate(seedGraph, nVertices, connectionProbs);
    }

    /**
     * Returns random undirected graph generated with a preferential attachment algorithm,
     * starting with a specified seed graph, as a longitudinal graph.
     *
     * @param seedGraph a graph used to seed the algorithm
     * @param nVertices number of vertices in final graph
     * @param nEdgesPerStep number of edges to attach at each step
     * @return the randomly generated graph
     */
    public static LongitudinalGraph<Integer> getLongitudinalSeededInstance(
            Graph<Integer> seedGraph,
            int nVertices, int nEdgesPerStep) {
        return generateLongitudinal(seedGraph, nVertices, (Integer) nEdgesPerStep);
    }

    /**
     * Returns random undirected graph generated via preferential attachment,
     * starting with a specified seed graph, as a longitudinal graph.
     * 
     * @param seedGraph a graph used to seed the algorithm
     * @param nVertices number of vertices in final graph
     * @param connectionProbs probabilities of initial #s of connections; the i'th
     *   entry is the probability that a new node will have i connections, starting at 0
     * @return the randomly generated graph
     */
    public static LongitudinalGraph<Integer> getLongitudinalSeededInstance(
            Graph<Integer> seedGraph,
            int nVertices, float[] connectionProbs) {
        return generateLongitudinal(seedGraph, nVertices, connectionProbs);
    }

    /** Common method for preferential attachment algorithm */
    private static Graph<Integer> generate(Graph<Integer> seedGraph, final int nVertices, Object edgesPerStep) {

        if (seedGraph.isDirected())
            throw new IllegalArgumentException("getRandomInstance: preferential attachment algorithm requires an undirected seed graph.");
        if (seedGraph.edgeNumber() == 0)
            throw new IllegalArgumentException("getRandomInstance: preferential attachment algorithm requires a seed graph with at least one edge.");
        if (!(edgesPerStep instanceof Integer || edgesPerStep instanceof float[]))
            throw new IllegalStateException();

        // prepare parameters for graph to be created
        ArrayList<Integer> nodes = new ArrayList<Integer>(seedGraph.nodes());
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        int[] degrees = new int[nVertices]; Arrays.fill(degrees, 0);
        int degreeSum = 0;

        // initialize with values from seed graph
        for (Integer i1 : nodes)
            for (Integer i2 : nodes)
                if (seedGraph.adjacent(i1, i2))
                    degreeSum += addEdge(edges, degrees, i1, i2);
        
        int cur = 0;
        boolean variableEdgeNumber = edgesPerStep instanceof float[];
        int numberEdgesToAdd = variableEdgeNumber ? 0 : (Integer) edgesPerStep;
        float[] connectionProbs = variableEdgeNumber ? (float[]) edgesPerStep : new float[]{};

        while (nodes.size() < nVertices) {
            while (nodes.contains(cur)) cur++;
            nodes.add(cur);
            if (variableEdgeNumber)
                numberEdgesToAdd = sampleRandom(connectionProbs);
            degreeSum += addEdge(edges, degrees, cur, weightedRandomVertex(degrees, degreeSum, numberEdgesToAdd));
        }
        return GraphFactory.getGraph(false, nodes, edges);
    }

    /**
     * Utility to add specified vertices to the edge set and increment the corresponding degrees.
     * @param edges current list of edges
     * @param degrees current list of degrees
     * @param v1 first vertex of edge to add
     * @param attachments second vertex (vertices) of edges to add
     * @return number of new degrees added
     */
    private static int addEdge(ArrayList<Integer[]> edges, int[] degrees, int v1, int... attachments) {
        for (int v : attachments) {
            edges.add(new Integer[] { v1, v } );
            degrees[v]++;
        }
        degrees[v1] += attachments.length;
        return attachments.length*2;
    }

    /** Common method to return longitudinal version of the randomly generated graph. */
    private static LongitudinalGraph<Integer> generateLongitudinal(Graph<Integer> seedGraph, final int nVertices, Object edgesPerStep) {
        if (seedGraph.isDirected())
            throw new IllegalArgumentException("getRandomInstance: preferential attachment algorithm requires an undirected seed graph.");
        if (seedGraph.edgeNumber() == 0)
            throw new IllegalArgumentException("getRandomInstance: preferential attachment algorithm requires a seed graph with at least one edge.");
        if (!(edgesPerStep instanceof Integer || edgesPerStep instanceof float[]))
            throw new IllegalStateException();

        // prepare parameters for graph to be created
        int nSeed = seedGraph.order();
        TreeMap<Integer, double[]> nodeTimes = new TreeMap<Integer, double[]>();
        TreeMap<Integer, Map<Integer, double[]>> edgeTimes = new TreeMap<Integer, Map<Integer, double[]>>();
        int[] degrees = new int[nVertices]; Arrays.fill(degrees, 0);
        int degreeSum = 0;
        double time = 0;

        // initialize with values from seed graph
        final double timeMax = nVertices-nSeed+1;
        final double[] allTime = new double[]{0, timeMax};
        for (Integer i : seedGraph.nodes())
            nodeTimes.put(i, allTime);

        for (Integer i1 : nodeTimes.keySet())
            for (Integer i2 : nodeTimes.keySet())
                if (seedGraph.adjacent(i1, i2))
                    degreeSum += addEdge(edgeTimes, allTime, degrees, i1, i2);

        int cur = 0;
        boolean variableEdgeNumber = edgesPerStep instanceof float[];
        int numberEdgesToAdd = variableEdgeNumber ? 0 : (Integer) edgesPerStep;
        float[] connectionProbs = variableEdgeNumber ? (float[]) edgesPerStep : new float[]{};

        while (nodeTimes.size() < nVertices) {
            time++;
            while (nodeTimes.containsKey(cur)) cur++;
            nodeTimes.put(cur, new double[]{time, timeMax});
            if (variableEdgeNumber)
                numberEdgesToAdd = sampleRandom(connectionProbs);
            degreeSum += addEdge(edgeTimes, new double[]{time, timeMax}, degrees, cur,
                    weightedRandomVertex(degrees, degreeSum, numberEdgesToAdd));
        }
        return IntervalLongitudinalGraph.getInstance(false, nodeTimes, edgeTimes);
    }

    /**
     * Utility to add specified vertices to the edge set and increment the corresponding degrees.
     * @param edges current list of edges
     * @param timeInterval time interval to use for the edge
     * @param degrees current list of degrees
     * @param v1 first vertex of edge to add
     * @param attachments second vertex (vertices) of edges to add
     * @return number of new degrees added
     */
    private static int addEdge(Map<Integer, Map<Integer, double[]>> edges, 
            double[] timeInterval, int[] degrees, int v1, int... attachments) {
        for (int node : attachments) {
            if (!edges.containsKey(v1))
                edges.put(v1, new TreeMap<Integer, double[]>());
            edges.get(v1).put(node, timeInterval);
            degrees[node]++;
        }
        degrees[v1] += attachments.length;
        return attachments.length*2;
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
