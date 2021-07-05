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
 * Encapsulate number of nodes and directed flag.
 *
 * @author Elisha Peterson
 */
open class DefaultGeneratorParameters {
    protected var directed = false
    protected var nodeCount = 1

    constructor() {}
    constructor(directed: Boolean, nodes: Int) {
        setDirected(directed)
        setNodeCount(nodes)
    }

    //region PROPERTIES
    fun isDirected(): Boolean {
        return directed
    }

    fun setDirected(directed: Boolean) {
        this.directed = directed
    }

    fun getNodeCount(): Int {
        return nodeCount
    }

    fun setNodeCount(nodes: Int) {
        Preconditions.checkArgument(nodes >= 0)
        nodeCount = nodes
    } //endregion
}