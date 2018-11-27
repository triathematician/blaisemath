package com.googlecode.blaisemath.graph.mod.metrics;

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
import com.google.common.graph.Graphs;
import com.googlecode.blaisemath.graph.GraphUtils;

import java.util.Set;

/**
 * Global metric describing the clustering coefficient of the graph; in the
 * directed case, measures "transitivity", i.e. when a-%gt;b,b-%gt;c implies
 * a-%gt;c
 *
 * @author Elisha Peterson
 */
public class ClusteringCoefficient extends AbstractGraphMetric<Double> {

    public ClusteringCoefficient() {
        super("Clustering coefficient", "Computes the clustering coefficient:"
                + " Out of all triples of vertices with at least two edges, how many have three edges?", true);
    }

    @Override
    public Double apply(Graph graph) {
        int[] tri = triples(graph);
        int triangles = tri[0], triples = tri[1];
        if (!graph.isDirected()) {
            triangles /= 3;
            triples -= 2 * triangles;
        }
        return triangles / (double) triples;
    }

    /**
     * Computes triple characteristics of a graph.
     *
     * @param <V> coordinate type of graph
     * @param graph the graph
     * @return int[] array where first entry is number of triangles and second
     *      is number of path triples (i.e., when three nodes are connected together)
     */
    static <V> int[] triples(Graph<V> graph) {
        int triangles = 0;
        int triples = 0;
        for (V node : graph.nodes()) {
            Set<V> g1 = graph.adjacentNodes(node);
            int dist1 = g1.size();
            int aDist1 = Graphs.inducedSubgraph(graph, g1).edges().size();
            Set<V> g2 = GraphUtils.neighborhood(graph, node, 2);
            int dist2 = g2.size() - 1 - g1.size();

            if (graph.isDirected()) {
                // in the directed case, potential triples are connected nodes at distance 1 and nodes at distance 2
                // ... each node at distance 2 contributes a triple, but no triangle
                triples += aDist1 + dist2;
                triangles += aDist1;
            } else {
                // in copyUndirected case, each pair of nodes @ distance 1 contributes to a triple
                // ... each edge in this neighborhood indicates a triangle
                // corrections for later: each triangle is counted 3 times
                triples += dist1 * (dist1 - 1) / 2;
                triangles += aDist1;
            }
        }
        return new int[]{triangles, triples};
    }
}
