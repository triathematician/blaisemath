package com.googlecode.blaisemath.graph.generate

import com.google.common.collect.Lists
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
 * Constructs wheel graph with n nodes. All nodes are connected to a central hub, and all non-central vertices
 * connected in a cyclic fashion.
 *
 * @author Elisha Peterson
 */
class WheelGraphGenerator : AbstractGraphGenerator("Wheel Graph") {
    override fun apply(parameters: DefaultGeneratorParameters?): Graph<Int?>? {
        val nodes = parameters.getNodeCount()
        if (nodes == 0) {
            return GraphUtils.emptyGraph(parameters.isDirected())
        }
        val edges: MutableList<Array<Int?>?>? = Lists.newArrayList()
        for (i in 1 until nodes) {
            edges.add(arrayOf(0, i))
        }
        for (i in 1 until nodes - 1) {
            edges.add(arrayOf(i, i + 1))
            if (parameters.isDirected()) {
                edges.add(arrayOf(i + 1, i))
            }
        }
        edges.add(arrayOf(nodes - 1, 1))
        if (parameters.isDirected()) {
            edges.add(arrayOf(1, nodes - 1))
        }
        return GraphGenerators.createGraphWithEdges(parameters, edges)
    }
}