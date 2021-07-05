package com.googlecode.blaisemath.graph.metrics

import com.google.common.graph.Graph
import com.google.common.graph.Graphs
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.SubgraphTest
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.layout.SpringLayoutPerformanceTest
import com.googlecode.blaisemath.graph.metrics.AdditiveSubsetMetricTest
import com.googlecode.blaisemath.graph.metrics.BetweenCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClosenessCentralityTest
import com.googlecode.blaisemath.graph.metrics.CooperationMetric
import com.googlecode.blaisemath.graph.metrics.EigenCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphCentralityTest
import com.googlecode.blaisemath.graph.metrics.SubsetMetricsTest
import com.googlecode.blaisemath.test.AssertUtils
import org.junit.BeforeClass

/*
* #%L
* BlaiseGraphTheory
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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
*/ /**
 * Global metric describing the clustering coefficient of the graph; in the
 * directed case, measures "transitivity", i.e. when a-%gt;b,b-%gt;c implies
 * a-%gt;c
 *
 * @author Elisha Peterson
 */
class ClusteringCoefficient : AbstractGraphMetric<Double?>("Clustering coefficient", "Computes the clustering coefficient:"
        + " Out of all triples of nodes with at least two edges, how many have three edges?", true) {
    override fun apply(graph: Graph<*>?): Double? {
        val tri = triples<Any?>(graph)
        var triangles = tri.get(0)
        var triples = tri.get(1)
        if (!graph.isDirected()) {
            triangles /= 3
            triples -= 2 * triangles
        }
        return triangles / triples as Double
    }

    companion object {
        /**
         * Computes triple characteristics of a graph.
         *
         * @param <N> coordinate type of graph
         * @param graph the graph
         * @return int[] array where first entry is number of triangles and second
         * is number of path triples (i.e., when three nodes are connected together)
        </N> */
        fun <N> triples(graph: Graph<N?>?): IntArray? {
            var triangles = 0
            var triples = 0
            for (node in graph.nodes()) {
                val g1 = graph.adjacentNodes(node)
                val dist1 = g1.size
                val aDist1 = Graphs.inducedSubgraph(graph, g1).edges().size
                val g2 = GraphUtils.neighborhood(graph, node, 2)
                val dist2 = g2.size - 1 - g1.size
                if (graph.isDirected()) {
                    // in the directed case, potential triples are connected nodes at distance 1 and nodes at distance 2
                    // ... each node at distance 2 contributes a triple, but no triangle
                    triples += aDist1 + dist2
                    triangles += aDist1
                } else {
                    // in copyUndirected case, each pair of nodes @ distance 1 contributes to a triple
                    // ... each edge in this neighborhood indicates a triangle
                    // corrections for later: each triangle is counted 3 times
                    triples += dist1 * (dist1 - 1) / 2
                    triangles += aDist1
                }
            }
            return intArrayOf(triangles, triples)
        }
    }
}