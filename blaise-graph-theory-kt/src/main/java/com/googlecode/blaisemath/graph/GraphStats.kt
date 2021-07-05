package com.googlecode.blaisemath.graph

import com.google.common.collect.Maps
import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graph.ContractedGraphTest
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
import org.apache.commons.math.stat.descriptive.SummaryStatistics
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
 * In the case of `GraphNodeMetric`s, the node values are computed along with a [SummaryStatistics] object
 * that describes their values.
 *
 * @author Elisha Peterson
 */
class GraphStats(graph: Graph?) {
    /** The base graph.  */
    private val graph: Graph?

    /** The node metrics that have been computed.  */
    private val nodeStats: MutableMap<GraphNodeMetric<*>?, GraphNodeStats?>? = Maps.newHashMap()

    /** The global metrics that have been computed.  */
    private val globalStats: MutableMap<GraphMetric<*>?, Any?>? = Maps.newHashMap()

    /**
     * The graph object.
     * @return graph
     */
    fun graph(): Graph? {
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
    } //region INNER CLASSES
    //endregion
    /**
     * Construct graph stats object.
     * @param graph graph for computations
     */
    init {
        this.graph = graph
    }
}