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
 * Position nodes in a circle.
 *
 * @author Elisha Peterson
 */
class CircleLayout : StaticGraphLayout<CircleLayout.CircleLayoutParameters?> {
    override fun toString(): String {
        return "Position nodes in a circle"
    }

    override fun createParameters(): CircleLayoutParameters? {
        return CircleLayoutParameters()
    }

    override fun <N> layout(g: Graph<N?>?, ic: MutableMap<N?, Point2D.Double?>?, p: CircleLayoutParameters?): MutableMap<N?, Point2D.Double?>? {
        val radius = p.radius
        val n = g.nodes().size
        var i = 0
        val result: MutableMap<N?, Point2D.Double?> = Maps.newHashMap()
        for (v in g.nodes()) {
            result[v] = Point2D.Double(radius * Math.cos(2 * Math.PI * i / n), radius * Math.sin(2 * Math.PI * i / n))
            i++
        }
        return result
    }
    //region PARAMETERS
    /** Parameters associated with circle layout  */
    class CircleLayoutParameters {
        private var radius = 100.0

        constructor() {}
        constructor(rad: Double) {
            radius = rad
        }

        fun getRadius(): Double {
            return radius
        }

        fun setRadius(radius: Double) {
            this.radius = radius
        }
    } //endregion

    companion object {
        private val INST: CircleLayout? = CircleLayout()
        fun getInstance(): CircleLayout? {
            return INST
        }
    }
}