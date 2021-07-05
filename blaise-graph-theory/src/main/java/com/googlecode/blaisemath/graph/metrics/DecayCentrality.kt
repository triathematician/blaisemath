package com.googlecode.blaisemath.graph.metrics

import com.google.common.base.Preconditions
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
 * Provides a metric describing the decay centrality of a node in a graph.
 * This computation can be slow for large graphs since it uses all nodes in a
 * component. The computation depends on a single decay
 * parameter... a node at distance 1 contributes this parameter, at distance
 * 2 the parameter squared, and so on. As the parameter approaches 1, the
 * metric's value approaches the size of the node's component. As the parameter
 * approaches 0, the metric's value also approaches the parameter times the size
 * of the node's neighborhood.
 *
 * @author Elisha Peterson
 */
class DecayCentrality @JvmOverloads constructor(parameter: Double = 0.5) : AbstractGraphNodeMetric<Double?>("Decay centrality") {
    /** Decay parameter  */
    var parameter = 0.5
    override fun toString(): String {
        return "DecayCentrality ($parameter)"
    }
    //region PROPERTIES
    /**
     * Get decay parameter.
     * @return value of decay parameter
     */
    fun getParameter(): Double {
        return parameter
    }

    /**
     * Set new decay parameter.
     * @param newValue new value of decay parameter
     */
    fun setParameter(newValue: Double) {
        Preconditions.checkArgument(newValue >= 0 && newValue <= 1,
                "Parameter for DecayCentrality must be between 0 and 1: $newValue")
        parameter = newValue
    }

    //endregion
    override fun <N> apply(graph: Graph<N?>?, node: N?): Double? {
        val nvg = GraphUtils.geodesicTree(graph, node)
        return nvg.values.stream().mapToDouble { i: Int? -> Math.pow(parameter, i.toDouble()) }.sum()
    }
    /**
     * Construct with specified parameter.
     * @param parameter value of decay parameter
     * @throws IllegalArgumentException if value is outside of the range [0,1]
     */
    /** Construct with default decay parameter of 0.5  */
    init {
        setParameter(parameter)
    }
}