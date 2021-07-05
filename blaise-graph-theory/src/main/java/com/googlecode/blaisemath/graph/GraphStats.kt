package com.googlecode.blaisemath.graph

import com.google.common.collect.Maps
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
 * Caches computations of metrics on a graph. Both [GraphNodeMetric]s and [GraphMetric]s are captured here.
 *
 * @author Elisha Peterson
 */
class GraphStats
/**
 * Construct graph stats object.
 * @param graph graph for computations
 */(
        /** The base graph.  */
        private val graph: Graph<*>?
) {
    /** The node metrics that have been computed.  */
    private val nodeStats: MutableMap<GraphNodeMetric<*>?, GraphNodeStats?>? = Maps.newHashMap()

    /** The global metrics that have been computed.  */
    private val globalStats: MutableMap<GraphMetric<*>?, Any?>? = Maps.newHashMap()

    /**
     * The graph object.
     * @return graph
     */
    fun graph(): Graph<*>? {
        return graph
    }

    /**
     * Returns whether stats have been computed for specified metric.
     * @param metric the metric
     * @return true if these stats have been computed
     */
    fun containsNodeStats(metric: GraphNodeMetric<*>?): Boolean {
        return nodeStats.containsKey(metric)
    }

    /**
     * Retrieve stats associated with a node metric. If there are none, the stats will be computed (which may take a
     * while) and the results cached.
     *
     * @param metric the metric
     * @return associated stats
     */
    fun nodeStatsOf(metric: GraphNodeMetric<*>?): GraphNodeStats? {
        if (!nodeStats.containsKey(metric)) {
            nodeStats[metric] = GraphNodeStats(graph, metric)
        }
        return nodeStats.get(metric)
    }

    /**
     * Returns whether stats have been computed for specified metric.
     * @param metric the metric
     * @return true if these stats have been computed
     */
    fun containsGlobalStats(metric: GraphMetric<*>?): Boolean {
        return globalStats.containsKey(metric)
    }

    /**
     * Retrieve stats associated with a node metric. If there are none, the stats will be computed (which may take a
     * while) and the results cached.
     *
     * @param metric the metric
     * @return associated stats
     */
    fun globalStatsOf(metric: GraphMetric<*>?): Any? {
        if (!globalStats.containsKey(metric)) {
            globalStats[metric] = metric.apply(graph)
        }
        return globalStats.get(metric)
    }
}