/*
 * PreferentialAttachmentGenerator.java
 * Created May 27, 2010
 */
package com.googlecode.blaisemath.graph.lon;

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
import com.googlecode.blaisemath.graph.Graph;
import static com.googlecode.blaisemath.graph.GraphUtils.nodes;
import com.googlecode.blaisemath.graph.mod.generators.PreferentialAttachmentGenerator;
import com.googlecode.blaisemath.graph.mod.generators.PreferentialAttachmentGenerator.PreferentialAttachmentParameters;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides static utility methods for generating graphs using preferential
 * attachment.
 *
 * @author Elisha Peterson
 */
public final class PreferentialAttachmentLonGraphGenerator implements LonGraphGenerator<PreferentialAttachmentParameters, Integer> {

    @Override
    public PreferentialAttachmentParameters createParameters() {
        return new PreferentialAttachmentParameters();
    }

    @Override
    public LonGraph<Integer> generate(PreferentialAttachmentParameters parm) {
        if (parm.getConnectProbabilities() == null) {
            return generateLongitudinal(parm.generateSeedGraph(), parm.getNodeCount(), parm.getEdgesPerStep());
        } else {
            return generateLongitudinal(parm.generateSeedGraph(), parm.getNodeCount(), parm.getConnectProbabilities());
        }
    }

    /**
     * Common method to return longitudinal version of the randomly generated
     * graph.
     */
    private static LonGraph<Integer> generateLongitudinal(Graph<Integer> seedGraph,
            final int nVertices, Object edgesPerStep) {
        // prepare parameters for graph to be created
        int nSeed = seedGraph.nodeCount();
        Map<Integer, double[]> nodeTimes = new TreeMap<Integer, double[]>();
        Map<Integer, Map<Integer, double[]>> edgeTimes = new TreeMap<Integer, Map<Integer, double[]>>();
        int[] degrees = new int[nVertices];
        Arrays.fill(degrees, 0);
        int degreeSum = 0;
        double time = 0;

        // initialize with values from seed graph
        final double timeMax = nVertices - nSeed + 1;
        final double[] allTime = new double[]{0, timeMax};
        for (Integer i : seedGraph.nodes()) {
            nodeTimes.put(i, allTime);
        }

        for (Integer i1 : nodeTimes.keySet()) {
            for (Integer i2 : nodeTimes.keySet()) {
                if (seedGraph.adjacent(i1, i2)) {
                    degreeSum += addEdge(edgeTimes, allTime, degrees, i1, i2);
                }
            }
        }

        int cur = 0;
        boolean variableEdgeNumber = edgesPerStep instanceof float[];
        int numberEdgesToAdd = variableEdgeNumber ? 0 : (Integer) edgesPerStep;
        float[] connectionProbs = variableEdgeNumber ? (float[]) edgesPerStep : new float[]{};

        while (nodeTimes.size() < nVertices) {
            time++;
            while (nodeTimes.containsKey(cur)) {
                cur++;
            }
            nodeTimes.put(cur, new double[]{time, timeMax});
            if (variableEdgeNumber) {
                numberEdgesToAdd = PreferentialAttachmentGenerator.sampleRandom(connectionProbs);
            }
            degreeSum += addEdge(edgeTimes, new double[]{time, timeMax}, degrees, cur,
                    PreferentialAttachmentGenerator.weightedRandomVertex(degrees, degreeSum, numberEdgesToAdd));
        }

        return IntervalLonGraph.getInstance(false, (int) (timeMax), nodeTimes, edgeTimes);
    }

    /**
     * Utility to add specified vertices to the edge set and increment the
     * corresponding degrees.
     *
     * @param edges current list of edges
     * @param timeInterval time interval to use for the edge
     * @param degrees current list of degrees
     * @param v1 first vertex of edge to add
     * @param attachments second vertex (vertices) of edges to add
     * @return number of new degrees added
     */
    static int addEdge(Map<Integer, Map<Integer, double[]>> edges,
            double[] timeInterval, int[] degrees, int v1, int... attachments) {
        for (int node : attachments) {
            if (!edges.containsKey(v1)) {
                edges.put(v1, new TreeMap<Integer, double[]>());
            }
            edges.get(v1).put(node, timeInterval);
            degrees[node]++;
        }
        degrees[v1] += attachments.length;
        return attachments.length * 2;
    }

}
