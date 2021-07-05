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
import java.awt.geom.Rectangle2D
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
 * Position nodes at random locations in a box.
 *
 * @author Elisha Peterson
 */
class RandomBoxLayout : StaticGraphLayout<RandomBoxLayout.BoxLayoutParameters?> {
    override fun toString(): String {
        return "Position nodes randomly in a rectangle"
    }

    override fun createParameters(): BoxLayoutParameters? {
        return BoxLayoutParameters()
    }

    override fun <N> layout(g: Graph<N?>?, ic: MutableMap<N?, Point2D.Double?>?, parameters: BoxLayoutParameters?): MutableMap<N?, Point2D.Double?>? {
        val r = Random()
        val result: MutableMap<N?, Point2D.Double?> = Maps.newHashMap()
        val minX = parameters.getBounds().getMinX()
        val minY = parameters.getBounds().getMinY()
        val maxX = parameters.getBounds().getMaxX()
        val maxY = parameters.getBounds().getMaxY()
        for (v in g.nodes()) {
            val x = r.nextDouble()
            val y = r.nextDouble()
            result[v] = Point2D.Double(x * minX + (1 - x) * maxX, y * minY + (1 - y) * maxY)
        }
        return result
    }
    //region INNER CLASSES
    /** Parameters associated with circle layout  */
    class BoxLayoutParameters {
        private var bounds: Rectangle2D.Double? = Rectangle2D.Double(-100, -100, 200, 200)

        constructor() {}
        constructor(bounds: Rectangle2D.Double?) {
            this.bounds = bounds
        }

        fun getBounds(): Rectangle2D.Double? {
            return bounds
        }

        fun setBounds(bounds: Rectangle2D.Double?) {
            this.bounds = bounds
        }
    } //endregion

    companion object {
        private val INST: RandomBoxLayout? = RandomBoxLayout()
        fun getInstance(): RandomBoxLayout? {
            return INST
        }
    }
}