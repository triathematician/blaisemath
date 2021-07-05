package com.googlecode.blaisemath.graph.metrics

import com.google.common.collect.HashMultimap
import com.google.common.collect.HashMultiset
import com.google.common.collect.Ordering
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
import org.junit.BeforeClass
import java.util.*

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
 * Global metric describes the radius of the graph, or the largest diameter of
 * one of its subcomponents.
 *
 * @author Elisha Peterson
 */
class GraphRadius : AbstractGraphMetric<Int?>("Graph radius", "Radius of the graph (minimum number r such that all nodes are within r edges of a particular node).", true) {
    override fun apply(graph: Graph<*>?): Int? {
        if (graph.nodes().isEmpty()) {
            return 0
        }
        var minMaxLength = Int.MAX_VALUE
        val lengths: MutableMap<Any?, Int?> = HashMap()
        for (node in graph.nodes()) {
            var maxLength = 0
            GraphUtils.breadthFirstSearch<Any?>(graph, node, HashMultiset.create<Any?>(), lengths, ArrayDeque<Any?>(), HashMultimap.create<Any?, Any?>())
            maxLength = Math.max(maxLength, Ordering.natural<Comparable<*>?>().max(lengths.values))
            if (maxLength > 0) {
                minMaxLength = Math.min(maxLength, minMaxLength)
            }
        }
        return minMaxLength
    }
}