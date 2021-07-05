package com.googlecode.blaisemath.graph.metrics

import com.google.common.collect.HashMultimap
import com.google.common.collect.HashMultiset
import com.google.common.collect.Maps
import com.google.common.collect.Ordering
import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.GraphMetrics
import com.googlecode.blaisemath.graph.GraphNodeMetric
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
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
import com.googlecode.blaisemath.graph.test.DynamicGraphTestFrame
import com.googlecode.blaisemath.graph.test.GraphTestFrame
import com.googlecode.blaisemath.graph.test.MyTestGraph
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.AnchorTestFrame
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
import com.googlecode.blaisemath.style.xml.AttributeSetAdapter
import com.googlecode.blaisemath.svg.HelloWorldSvg
import com.googlecode.blaisemath.svg.SvgCircle
import com.googlecode.blaisemath.svg.SvgCircle.CircleConverter
import com.googlecode.blaisemath.svg.SvgElement
import com.googlecode.blaisemath.svg.SvgElements
import com.googlecode.blaisemath.svg.SvgEllipse
import com.googlecode.blaisemath.svg.SvgEllipse.EllipseConverter
import com.googlecode.blaisemath.svg.SvgGroup
import com.googlecode.blaisemath.svg.SvgImage
import com.googlecode.blaisemath.svg.SvgImage.ImageConverter
import com.googlecode.blaisemath.svg.SvgIo
import com.googlecode.blaisemath.svg.SvgLine
import com.googlecode.blaisemath.svg.SvgLine.LineConverter
import com.googlecode.blaisemath.svg.SvgNamespaceFilter
import com.googlecode.blaisemath.svg.SvgPath
import com.googlecode.blaisemath.svg.SvgPath.SvgPathOperator
import com.googlecode.blaisemath.svg.SvgPathTest
import com.googlecode.blaisemath.svg.SvgPolygon
import com.googlecode.blaisemath.svg.SvgPolygon.PolygonConverter
import com.googlecode.blaisemath.svg.SvgPolyline
import com.googlecode.blaisemath.svg.SvgPolyline.PolylineConverter
import com.googlecode.blaisemath.svg.SvgRectangle
import com.googlecode.blaisemath.svg.SvgRectangle.RectangleConverter
import com.googlecode.blaisemath.svg.SvgRoot
import com.googlecode.blaisemath.svg.SvgRootTest
import com.googlecode.blaisemath.svg.SvgText
import com.googlecode.blaisemath.svg.SvgText.TextConverter
import com.googlecode.blaisemath.svg.SvgTool
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.ui.PropertyActionPanel
import com.googlecode.blaisemath.util.Images
import com.googlecode.blaisemath.util.Instrument.end
import com.googlecode.blaisemath.util.Instrument.start
import junit.framework.TestCase
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
 * Implements closeness centrality (Sabidussi 1966), the inverse sum of
 * distances from one node to other nodes. The same calculation can be used to
 * compute the "eccentricity" of the node, the max distance from this node to
 * any other node, termed graph centrality by Hage/Harary 1995. Instances
 * of both metrics are provided.
 *
 * @author Elisha Peterson
 */
class GraphCentrality : GraphNodeMetric<Double?> {
    override fun toString(): String {
        return "Graph centrality"
    }

    override fun <N> apply(graph: Graph<N?>?, node: N?): Double? {
        // TODO - compare to Closeness Centrality, see if we can merge some code
        val n = graph.nodes().size
        val lengths: MutableMap<N?, Int?> = HashMap()
        GraphUtils.breadthFirstSearch(graph, node, HashMultiset.create(), lengths, ArrayDeque(), HashMultimap.create())
        val cptSize = lengths.size.toDouble()
        val max = Ordering.natural<Comparable<*>?>().max(lengths.values)
        return cptSize / (n * max as Double)
    }

    override fun <N> apply(graph: Graph<N?>?): MutableMap<N?, Double?>? {
        val id = start("GraphCentrality.allValues", graph.nodes().size.toString() + " nodes", graph.edges().size.toString() + " edges")
        val res = GraphMetrics.applyToComponents(graph, ApplyConnected())
        end(id)
        return res
    }

    /** Computes values for a connected portion of a graph  */
    private class ApplyConnected internal constructor() : AbstractGraphNodeMetric<Double?>("") {
        override fun <N> apply(graph: Graph<N?>?, node: N?): Double? {
            // not used
            return null
        }

        override fun <N> apply(graph: Graph<N?>?): MutableMap<N?, Double?>? {
            val res: MutableMap<N?, Double?> = Maps.newHashMap()
            val nodes = graph.nodes()
            for (start in nodes) {
                val lengths: MutableMap<N?, Int?> = HashMap()
                GraphUtils.breadthFirstSearch(graph, start, HashMultiset.create(), lengths, ArrayDeque(), HashMultimap.create())
                val max: Double = Ordering.natural<Comparable<*>?>().max(lengths.values)
                res[start] = 1.0 / max
            }
            return res
        }
    }
}