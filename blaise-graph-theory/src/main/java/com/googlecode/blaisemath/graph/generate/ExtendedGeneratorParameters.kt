package com.googlecode.blaisemath.graph.generate

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.graph.ContractedGraphTest
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
 * Graph parameters with directed flag, node count, and edge count.
 *
 * @author Elisha Peterson
 */
class ExtendedGeneratorParameters : DefaultGeneratorParameters {
    protected var edgeCount = 0

    constructor() {}
    constructor(directed: Boolean, nodes: Int, edges: Int) : super(directed, nodes) {
        setEdgeCount(edges)
    }

    //region PROPERTIES
    fun getEdgeCount(): Int {
        return edgeCount
    }

    fun setEdgeCount(edges: Int) {
        Preconditions.checkArgument(edges >= 0)
        edgeCount = edges
    }
    //endregion
    /**
     * Get the number of edges, limited to the maximum possible based on the current node count.
     * @return edge count
     */
    fun edgeCountBounded(): Int {
        val max = if (directed) nodeCount * (nodeCount - 1) / 2 else nodeCount * nodeCount
        return Math.min(edgeCount, max)
    }
}