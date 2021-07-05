package com.googlecode.blaisemath.graph

import com.google.common.graph.Graph
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
 * Returns a value for a single node in a graph.
 * @param <T> the type of value returned (usually a number)
 *
 * @author Elisha Peterson
</T> */
interface GraphNodeMetric<T> {
    /**
     * Computes the value of the metric for the given graph and node.
     *
     * @param <N>   graph node type
     * @param graph the graph
     * @param node  a node in the graph
     * @return value of the metric
     * @throws IllegalArgumentException if the value cannot be computed for specified graph (e.g. graph is null, or graph
     * is directed, but the metric only applies to undirected graphs)
    </N> */
    open fun <N> apply(graph: Graph<N?>?, node: N?): T?

    /**
     * Computes the value of the metric for the given graph and all nodes in the graph. This is provided as a separate
     * interface method to allow for optimization.
     *
     * @param <N>   graph node type
     * @param graph the graph
     * @return value of the metric for each node
     * @throws IllegalArgumentException if the value cannot be computed for specified graph (e.g. graph is null, or graph
     * is directed, but the metric only applies to undirected graphs)
    </N> */
    open fun <N> apply(graph: Graph<N?>?): MutableMap<N?, T?>?
}