package com.googlecode.blaisemath.graph.layout

import com.google.common.collect.Maps
import com.google.common.graph.Graph
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.StaticGraphLayout
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
import java.awt.geom.Point2D
import java.util.*

/*-
* #%L
* blaise-graph-theory
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
 * Uses set positions if available to position nodes; otherwise uses a static layout.
 *
 * @param <P> object describing layout parameters
 *
 * @author John An, Elisha Peterson
</P> */
class FixableStaticLayout<P> : StaticGraphLayout<P?> {
    private var parent: StaticGraphLayout<P?>?
    private var positions: MutableMap<*, Point2D.Double?>?

    /**
     * Construct layout with given parent layout.
     * @param parent parent layout, used as a default
     */
    constructor(parent: StaticGraphLayout<P?>?) {
        this.parent = Objects.requireNonNull(parent)
        positions = Maps.newHashMap<Any?, Point2D.Double?>()
    }

    /**
     * Construct layout with given parent layout and fixed positions.
     * @param parent parent layout, used as a default
     * @param positions fixed positions
     */
    constructor(parent: StaticGraphLayout<P?>?, positions: MutableMap<*, Point2D.Double?>?) {
        this.parent = Objects.requireNonNull(parent)
        this.positions = positions
    }

    override fun createParameters(): P? {
        return parent.createParameters()
    }

    override fun <C> layout(g: Graph<C?>?, ic: MutableMap<C?, Point2D.Double?>?, parameters: P?): MutableMap<C?, Point2D.Double?>? {
        if (!positions.keys.containsAll(g.nodes())) {
            positions = parent.layout(g, ic, parameters)
        }
        return positions as MutableMap<C?, Point2D.Double?>?
    }

    fun getPositions(): MutableMap<*, Point2D.Double?>? {
        return positions
    }

    fun setPositions(positions: MutableMap<*, Point2D.Double?>?) {
        this.positions = positions
    }
}