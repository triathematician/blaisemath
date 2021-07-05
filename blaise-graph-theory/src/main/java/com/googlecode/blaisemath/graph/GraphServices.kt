package com.googlecode.blaisemath.graph

import com.google.common.collect.Lists
import com.google.common.collect.Maps
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
 * Provides instances of registered services of various types.
 *
 * @author Elisha Peterson
 */
object GraphServices {
    private val SERVICE_CACHE: MutableMap<Class<*>?, ServiceLoader<*>?>? = Maps.newHashMap()
    //region SERVICE PROVIDERS
    /**
     * Locate, initialize, and return the list of registered [GraphGenerator] implementations via the
     * [ServiceLoader] class.
     * @return list of `GraphNodeMetric`s
     */
    fun generators(): MutableList<GraphGenerator<*, *>?>? {
        return services<GraphGenerator<*, *>?>(GraphGenerator::class.java)
    }

    /**
     * Locate, initialize, and return the list of registered [StaticGraphLayout] implementations via the
     * [ServiceLoader] class.
     * @return list of `StaticGraphLayout`s
     */
    fun staticLayouts(): MutableList<StaticGraphLayout<*>?>? {
        return services<StaticGraphLayout<*>?>(StaticGraphLayout::class.java)
    }

    /**
     * Locate, initialize, and return the list of registered [IterativeGraphLayout] implementations via the
     * [ServiceLoader] class.
     * @return list of `IterativeGraphLayout`s
     */
    fun iterativeLayouts(): MutableList<IterativeGraphLayout<*, *>?>? {
        return services<IterativeGraphLayout<*, *>?>(IterativeGraphLayout::class.java)
    }

    /**
     * Locate, initialize, and return the list of registered [GraphMetric] implementations via the
     * [ServiceLoader] class.
     * @return list of `GraphMetric`s
     */
    fun globalMetrics(): MutableList<GraphMetric<*>?>? {
        return services<GraphMetric<*>?>(GraphMetric::class.java)
    }

    /**
     * Locate, initialize, and return the list of registered [GraphNodeMetric]
     * implementations via the [ServiceLoader] class.
     * @return list of `GraphNodeMetric`s
     */
    fun nodeMetrics(): MutableList<GraphNodeMetric<*>?>? {
        return services<GraphNodeMetric<*>?>(GraphNodeMetric::class.java)
    }

    /**
     * Locate, initialize, and return the list of registered [GraphSubsetMetric] implementations via the
     * [ServiceLoader] class.
     * @return list of `GraphSubsetMetric`s
     */
    fun subsetMetrics(): MutableList<GraphSubsetMetric<*>?>? {
        return services<GraphSubsetMetric<*>?>(GraphSubsetMetric::class.java)
    }
    //endregion
    /** Utility method to dynamically get list of services.  */
    private fun <X> services(type: Class<X?>?): MutableList<X?>? {
        if (SERVICE_CACHE.get(type) == null) {
            SERVICE_CACHE[type] = ServiceLoader.load(type)
        }
        return Lists.newArrayList(SERVICE_CACHE.get(type) as Iterable<X?>?)
    }
}