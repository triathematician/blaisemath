package com.googlecode.blaisemath.graph.metrics

import com.google.common.collect.*
import com.google.common.graph.Graph
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
import com.googlecode.blaisemath.util.Instrument
import org.junit.BeforeClass
import java.util.*
import java.util.function.Consumer

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
 * Provides a metric describing the betweenness centrality of a node in a
 * CONNECTED graph. Returns infinity if the graph is not connected. May take a
 * long time for large graphs. Computationally, the centrality measures
 * the probability that a given node lies on a randomly chosen geodesic.
 *
 * @author Elisha Peterson
 */
class BetweenCentrality : AbstractGraphNodeMetric<Double?>("Betweenness centrality") {
    override fun <N> apply(graph: Graph<N?>?, node: N?): Double? {
        return apply(graph).get(node)
    }

    override fun <N> apply(graph: Graph<N?>?): MutableMap<N?, Double?>? {
        val id = Instrument.start("BetweenCentrality.allValues", graph.nodes().size.toString() + " nodes", graph.edges().size.toString() + " edges")
        val between: MutableMap<N?, Double?> = HashMap()
        graph.nodes().forEach(Consumer { n: N? -> between[n] = 0.0 })
        graph.nodes().forEach(Consumer { n: N? -> applyBrandes(graph, n, between, if (graph.isDirected()) 1.0 else 0.5) })
        Instrument.end(id)
        return between
    }

    companion object {
        /**
         * Breadth-first search algorithm for an unweighted graph to generate betweenness scores, with specified starting
         * node. From *Brandes*, "A Faster Algorithm for Betweenness Centrality".
         *
         * @param graph the graph
         * @param start the start node
         * @param result data structure storing existing betweenness centrality values
         * @param multiplier applied to all elements of resulting map
         */
        private fun <N> applyBrandes(graph: Graph<N?>?, start: N?, result: MutableMap<N?, Double?>?, multiplier: Double) {
            val nodes = graph.nodes()
            if (!nodes.contains(start)) {
                return
            }

            // number of shortest paths to each node
            val numShortest: Multiset<N?> = HashMultiset.create()
            // length of shortest paths to each node
            val lengths: MutableMap<N?, Int?> = HashMap()
            // tracks elements in non-increasing order for later use
            val deque: Deque<N?> = Queues.newArrayDeque()
            // tracks node predecessors in resulting tree
            val predecessors: Multimap<N?, N?> = HashMultimap.create()
            GraphUtils.breadthFirstSearch(graph, start, numShortest, lengths, deque, predecessors)

            // compute betweenness
            val dependencies: MutableMap<N?, Double?> = HashMap()
            for (n in nodes) {
                dependencies[n] = 0.0
            }
            while (!deque.isEmpty()) {
                val w = deque.pollLast()
                for (n in predecessors[w]) {
                    dependencies[n] = dependencies[n] + numShortest.count(n) as Double / numShortest.count(w) * (1 + dependencies[w])
                }
                if (w !== start) {
                    result[w] = result.get(w) + multiplier * dependencies[w]
                }
            }
        }
    }
}