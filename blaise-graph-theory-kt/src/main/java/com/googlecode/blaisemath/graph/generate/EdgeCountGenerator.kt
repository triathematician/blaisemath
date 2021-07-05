package com.googlecode.blaisemath.graph.generate

import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphGenerator
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.SubgraphTest
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
 * Generate random graph with specified edge count.
 *
 * @author Elisha Peterson
 */
class EdgeCountGenerator : GraphGenerator<ExtendedGeneratorParameters?, Int?> {
    override fun toString(): String {
        return "Random Graph (fixed Edge Count)"
    }

    override fun createParameters(): ExtendedGeneratorParameters? {
        return ExtendedGeneratorParameters()
    }

    override fun apply(p: ExtendedGeneratorParameters?): Graph<Int?>? {
        val directed = p.isDirected()
        val nodes = p.getNodeCount()
        val edgeSet: MutableSet<Array<Int?>?> = TreeSet(if (directed) PAIR_COMPARE else PAIR_COMPARE_UNDIRECTED)
        var potential: Array<Int?>
        for (i in 0 until p.edgeCountBounded()) {
            do {
                potential = arrayOf((nodes * Math.random()) as Int, (nodes * Math.random()) as Int)
            } while (!directed && potential[0] == potential[1] || edgeSet.contains(potential))
            edgeSet.add(potential)
        }
        return GraphGenerators.createGraphWithEdges(p, edgeSet)
    }

    companion object {
        //region COMPARATORS
        /**
         * Used to sort pairs of integers when order of the two matters.
         */
        val PAIR_COMPARE: Comparator<Array<Int?>?>? = Comparator { o1: Array<Int?>?, o2: Array<Int?>? ->
            check(!(o1.size != 2 || o2.size != 2)) { "This object only compares integer pairs." }
            if (o1.get(0) == o2.get(0)) o1.get(1) - o2.get(1) else o1.get(0) - o2.get(0)
        }

        /**
         * Used to sort pairs of integers when order of the two does not matter.
         */
        val PAIR_COMPARE_UNDIRECTED: Comparator<Array<Int?>?>? = Comparator { o1: Array<Int?>?, o2: Array<Int?>? ->
            check(!(o1.size != 2 || o2.size != 2)) { "This object only compares integer pairs." }
            val min1 = Math.min(o1.get(0), o1.get(1))
            val min2 = Math.min(o2.get(0), o2.get(1))
            if (min1 == min2) Math.max(o1.get(0), o1.get(1)) - Math.max(o2.get(0), o2.get(1)) else min1 - min2
        }

        //endregion
        private val INST: EdgeCountGenerator? = EdgeCountGenerator()
        fun getInstance(): EdgeCountGenerator? {
            return INST
        }
    }
}