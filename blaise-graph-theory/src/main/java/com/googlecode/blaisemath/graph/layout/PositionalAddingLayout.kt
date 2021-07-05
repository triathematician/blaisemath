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
 * Position graph nodes nearby connected nodes. The first layout parameter is the approximate distance between a new
 * node's position and the position of connected nodes. Nodes with existing locations are not affected.
 *
 * @author Elisha Peterson
 */
class PositionalAddingLayout : StaticGraphLayout<CircleLayout.CircleLayoutParameters?> {
    override fun toString(): String {
        return "Position nodes near existing adjacent nodes"
    }

    override fun createParameters(): CircleLayout.CircleLayoutParameters? {
        return CircleLayout.CircleLayoutParameters()
    }

    override fun <N> layout(g: Graph<N?>?, curLocations: MutableMap<N?, Point2D.Double?>?, parameters: CircleLayout.CircleLayoutParameters?): MutableMap<N?, Point2D.Double?>? {
        val len = parameters.getRadius()
        val res: MutableMap<N?, Point2D.Double?> = Maps.newHashMap()
        for (node in g.nodes()) {
            if (curLocations != null && curLocations.containsKey(node)) {
                res[node] = curLocations[node]
            } else {
                var sx = 0.0
                var sy = 0.0
                var n = 0
                for (o in g.adjacentNodes(node)) {
                    val p = curLocations.get(o)
                    if (p != null) {
                        sx += p.x
                        sy += p.y
                        n++
                    }
                }
                if (n == 0) {
                    val theta = 2 * Math.PI * Math.random()
                    res[node] = Point2D.Double(sx + 2 * len * Math.cos(theta), sy + 2 * len * Math.sin(theta))
                } else if (n == 1) {
                    val theta = 2 * Math.PI * Math.random()
                    res[node] = Point2D.Double(sx + len * Math.cos(theta), sy + len * Math.sin(theta))
                } else {
                    res[node] = Point2D.Double(sx / n, sy / n)
                }
            }
        }
        return res
    }
}