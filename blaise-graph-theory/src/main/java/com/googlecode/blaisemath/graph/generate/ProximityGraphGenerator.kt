package com.googlecode.blaisemath.graph.generate

import com.google.common.graph.Graph
import com.googlecode.blaisemath.geom.Rectangles
import com.googlecode.blaisemath.graph.GraphGenerator
import com.googlecode.blaisemath.graph.GraphUtils
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
 * Generates a graph in specified bounding box, where edges are added for points that are within a certain distance.
 *
 * @author Elisha Peterson
 */
class ProximityGraphGenerator : GraphGenerator<ProximityGraphGenerator.ProximityGraphParameters?, Point2D.Double?> {
    override fun toString(): String {
        return "Proximity Graph"
    }

    override fun createParameters(): ProximityGraphParameters? {
        return ProximityGraphParameters()
    }

    override fun apply(parameters: ProximityGraphParameters?): Graph<Point2D.Double?>? {
        val nodes = parameters.getNodeCount()
        val x0 = parameters.getBounds().getMinX()
        val x1 = parameters.getBounds().getMaxX()
        val y0 = parameters.getBounds().getMinY()
        val y1 = parameters.getBounds().getMaxY()
        val connectDistance = parameters.getConnectDistance()
        val pts: MutableList<Point2D.Double?> = ArrayList()
        for (i in 0 until nodes) {
            pts.add(Point2D.Double(x0 + (x1 - x0) * Math.random(), y0 + (y1 - y0) * Math.random()))
        }
        val edges: MutableList<Array<Point2D.Double?>?> = ArrayList()
        for (i0 in pts.indices) {
            for (i1 in i0 + 1 until pts.size) {
                if (pts[i0].distance(pts[i1]) > connectDistance) {
                    continue
                }
                edges.add(arrayOf(pts[i0], pts[i1]))
            }
        }
        return GraphUtils.createFromArrayEdges(false, pts, edges)
    }
    //region PARAMETERS CLASS
    /** Parameters for proximity graph.  */
    class ProximityGraphParameters : DefaultGeneratorParameters {
        private var bounds: Rectangle2D.Double? = Rectangle2D.Double()
        private var connectDistance = 1.0

        constructor() {}
        constructor(directed: Boolean, nodes: Int, bounds: Rectangle2D?, dst: Double) : super(directed, nodes) {
            this.bounds = Rectangles.toDouble(bounds)
            connectDistance = dst
        }

        fun getBounds(): Rectangle2D.Double? {
            return bounds
        }

        fun setBounds(bounds: Rectangle2D.Double?) {
            this.bounds = bounds
        }

        fun getConnectDistance(): Double {
            return connectDistance
        }

        fun setConnectDistance(connectDistance: Double) {
            this.connectDistance = connectDistance
        }
    } //endregion
}