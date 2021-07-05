package com.googlecode.blaisemath.graph

import com.google.common.collect.HashMultiset
import com.google.common.collect.Multiset
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
import org.junit.BeforeClass
import java.util.*
import java.util.function.BiFunction
import java.util.stream.Collectors

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
 * Utility class for working with [GraphMetric], [GraphNodeMetric],
 * and [GraphSubsetMetric] instances.
 *
 * @author Elisha Peterson
 */
object GraphMetrics {
    /**
     * Compute the distribution of the values of a particular metric, for all nodes in the graph.
     * @param <N> node type
     * @param <T> metric result type
     * @param graph the graph
     * @param metric metric used to generate values
     * @return distribution of values
    </T></N> */
    fun <N, T> distribution(graph: Graph<N?>?, metric: GraphNodeMetric<T?>?): Multiset<T?>? {
        return graph.nodes().stream()
                .map { n: N? -> metric.apply(graph, n) }
                .collect(Collectors.collectingAndThen(Collectors.toList()) { elements: MutableList<T?>? -> HashMultiset.create(elements) })
    }

    /**
     * Compute the distribution of the values of a particular metric, for all nodes in the graph.
     * @param <N> node type
     * @param <T> metric result type
     * @param graph the graph
     * @param metric metric used to generate values
     * @return distribution of values
    </T></N> */
    fun <N, T> distribution(graph: Graph<N?>?, metric: BiFunction<Graph<N?>?, N?, T?>?): Multiset<T?>? {
        return graph.nodes().stream()
                .map { n: N? -> metric.apply(graph, n) }
                .collect(Collectors.collectingAndThen(Collectors.toList()) { elements: MutableList<T?>? -> HashMultiset.create(elements) })
    }

    /**
     * Compute a metric that applies only to connected graphs, by weighting values in proportion to the size of the component
     * they are in.
     * @param <N> graph node type
     * @param graph graph
     * @param connectedGraphMetric a function that computes values for connected graphs
     * @return result
    </N> */
    fun <N> applyToComponents(graph: Graph<N?>?, connectedGraphMetric: GraphNodeMetric<Double?>?): MutableMap<N?, Double?>? {
        val n = graph.nodes().size
        if (n == 0) {
            return emptyMap()
        } else if (n == 1) {
            return Collections.singletonMap(graph.nodes().iterator().next(), 0.0)
        }
        val components = GraphUtils.componentGraphs(graph)
        val values: MutableMap<N?, Double?> = HashMap()
        for (c in components) {
            if (c.nodes().size == 1) {
                values[c.nodes().iterator().next()] = 0.0
            } else {
                values.putAll(connectedGraphMetric.apply(c))
            }
        }
        for (c in components) {
            val multiplier = c.nodes().size / n as Double
            for (v in c.nodes()) {
                values[v] = multiplier * values[v]
            }
        }
        return values
    }
}