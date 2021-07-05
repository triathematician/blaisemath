package com.googlecode.blaisemath.graphics.core

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphComponents
import com.googlecode.blaisemath.graph.GraphGenerator
import com.googlecode.blaisemath.graph.GraphMetric
import com.googlecode.blaisemath.graph.GraphMetrics
import com.googlecode.blaisemath.graph.GraphNodeMetric
import com.googlecode.blaisemath.graph.GraphNodeStats
import com.googlecode.blaisemath.graph.GraphServices
import com.googlecode.blaisemath.graph.GraphSubsetMetric
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.IterativeGraphLayout
import com.googlecode.blaisemath.graph.IterativeGraphLayoutState
import com.googlecode.blaisemath.graph.NodeInGraph
import com.googlecode.blaisemath.graph.OptimizedGraph
import com.googlecode.blaisemath.graph.StaticGraphLayout
import com.googlecode.blaisemath.graph.SubgraphTest
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.generate.AbstractGraphGenerator
import com.googlecode.blaisemath.graph.generate.CompleteGraphGenerator
import com.googlecode.blaisemath.graph.generate.CycleGraphGenerator
import com.googlecode.blaisemath.graph.generate.DefaultGeneratorParameters
import com.googlecode.blaisemath.graph.generate.DegreeDistributionGenerator
import com.googlecode.blaisemath.graph.generate.DegreeDistributionGenerator.DegreeDistributionParameters
import com.googlecode.blaisemath.graph.generate.EdgeCountGenerator
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator.EdgeLikelihoodParameters
import com.googlecode.blaisemath.graph.generate.EmptyGraphGenerator
import com.googlecode.blaisemath.graph.generate.ExtendedGeneratorParameters
import com.googlecode.blaisemath.graph.generate.GraphGenerators
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.generate.PreferentialAttachmentGenerator
import com.googlecode.blaisemath.graph.generate.PreferentialAttachmentGenerator.PreferentialAttachmentParameters
import com.googlecode.blaisemath.graph.generate.ProximityGraphGenerator.ProximityGraphParameters
import com.googlecode.blaisemath.graph.generate.StarGraphGenerator
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator
import com.googlecode.blaisemath.graph.generate.WattsStrogatzGenerator.WattsStrogatzParameters
import com.googlecode.blaisemath.graph.generate.WheelGraphGenerator
import com.googlecode.blaisemath.graph.layout.CircleLayout
import com.googlecode.blaisemath.graph.layout.CircleLayout.CircleLayoutParameters
import com.googlecode.blaisemath.graph.layout.GraphLayoutConstraints
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager
import com.googlecode.blaisemath.graph.layout.IterativeGraphLayoutManager
import com.googlecode.blaisemath.graph.layout.IterativeGraphLayoutService
import com.googlecode.blaisemath.graph.layout.LayoutRegion
import com.googlecode.blaisemath.graph.layout.PositionalAddingLayout
import com.googlecode.blaisemath.graph.layout.RandomBoxLayout
import com.googlecode.blaisemath.graph.layout.RandomBoxLayout.BoxLayoutParameters
import com.googlecode.blaisemath.graph.layout.SpringLayoutParameters
import com.googlecode.blaisemath.graph.layout.SpringLayoutPerformanceTest
import com.googlecode.blaisemath.graph.layout.SpringLayoutState
import com.googlecode.blaisemath.graph.layout.StaticSpringLayout
import com.googlecode.blaisemath.graph.layout.StaticSpringLayout.StaticSpringLayoutParameters
import com.googlecode.blaisemath.graph.metrics.AbstractGraphMetric
import com.googlecode.blaisemath.graph.metrics.AbstractGraphNodeMetric
import com.googlecode.blaisemath.graph.metrics.AdditiveSubsetMetricTest
import com.googlecode.blaisemath.graph.metrics.BetweenCentrality
import com.googlecode.blaisemath.graph.metrics.BetweenCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClosenessCentrality
import com.googlecode.blaisemath.graph.metrics.ClosenessCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClusteringCoefficient
import com.googlecode.blaisemath.graph.metrics.CooperationMetric
import com.googlecode.blaisemath.graph.metrics.DecayCentrality
import com.googlecode.blaisemath.graph.metrics.EigenCentrality
import com.googlecode.blaisemath.graph.metrics.EigenCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphCentrality
import com.googlecode.blaisemath.graph.metrics.GraphCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphDiameter
import com.googlecode.blaisemath.graph.metrics.SubsetMetrics
import com.googlecode.blaisemath.graph.metrics.SubsetMetrics.AdditiveSubsetMetric
import com.googlecode.blaisemath.graph.metrics.SubsetMetrics.ContractiveSubsetMetric
import com.googlecode.blaisemath.graph.metrics.SubsetMetricsTest
import com.googlecode.blaisemath.graph.test.DynamicGraphTestFrame
import com.googlecode.blaisemath.graph.test.GraphTestFrame
import com.googlecode.blaisemath.graph.test.MyTestGraph
import com.googlecode.blaisemath.graph.util.Matrices
import com.googlecode.blaisemath.graph.view.GraphComponent
import com.googlecode.blaisemath.graph.view.VisualGraph
import com.googlecode.blaisemath.graph.view.WeightedEdgeStyler
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.JGraphics
import com.googlecode.blaisemath.style.StyleContext
import com.googlecode.blaisemath.style.StyleHints
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
import com.googlecode.blaisemath.util.Images
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.Assert
import org.junit.Test
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.*
import javax.swing.JPopupMenu

/*
* #%L
* BlaiseGraphics
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
*/   class GraphicCompositeTest {
    val pt: PrimitiveGraphic<Point2D?, Graphics2D?>?
    val gc: GraphicComposite<Graphics2D?>?
    @Test
    fun testAddGraphic() {
        gc.addGraphic(pt)
        Assert.assertEquals(1, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(gc.addGraphic(pt))
        Assert.assertEquals(1, Iterables.size(gc.getGraphics()).toLong())
    }

    @Test
    fun testRemoveGraphic() {
        Assert.assertTrue(gc.addGraphic(pt))
        Assert.assertTrue(gc.removeGraphic(pt))
        Assert.assertEquals(0, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(gc.removeGraphic(pt))
    }

    @Test
    fun testAddGraphics() {
        val gfx = Lists.newArrayList(pt)
        Assert.assertTrue(gc.addGraphics(gfx))
        Assert.assertEquals(1, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(gc.addGraphics(gfx))
    }

    @Test
    fun testRemoveGraphics() {
        val gfx = Lists.newArrayList(pt)
        Assert.assertTrue(gc.addGraphics(gfx))
        Assert.assertTrue(gc.removeGraphics(gfx))
        Assert.assertFalse(gc.removeGraphics(gfx))
        Assert.assertEquals(0, Iterables.size(gc.getGraphics()).toLong())
    }

    @Test
    fun testReplaceGraphics() {
        val gfx = Lists.newArrayList(pt)
        Assert.assertFalse(gc.replaceGraphics(gfx, Collections.EMPTY_LIST))
        gc.addGraphics(gfx)
        Assert.assertTrue(gc.replaceGraphics(gfx, listOf(pt)))
        Assert.assertTrue(gc.replaceGraphics(gfx, listOf(JGraphics.point(Point2D.Double()))))
        Assert.assertEquals(1, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(Iterables.contains(gc.getGraphics(), pt))
    }

    @Test
    fun testGetGraphics() {
        val gfx = Lists.newArrayList(pt)
        gc.setGraphics(gfx)
        Assert.assertTrue(Iterables.elementsEqual(gfx, gc.getGraphics()))
    }

    @Test
    fun testSetGraphics() {
        val gfx = Lists.newArrayList(pt)
        gc.addGraphics(gfx)
        gc.setGraphics(Lists.newArrayList(JGraphics.point(Point2D.Double())))
        Assert.assertEquals(1, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(Iterables.contains(gc.getGraphics(), pt))
    }

    @Test
    fun testClearGraphics() {
        val gfx = Lists.newArrayList(pt)
        gc.addGraphics(gfx)
        Assert.assertTrue(gc.clearGraphics())
        Assert.assertEquals(0, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(gc.clearGraphics())
    }

    @Test
    fun testGraphicAt() {
        gc.addGraphic(pt)
        Assert.assertEquals(pt, gc.graphicAt(Point(), null))
        Assert.assertEquals(pt, gc.graphicAt(Point(1, 0), null))
        Assert.assertNull(gc.graphicAt(Point(10, 10), null))
    }

    @Test
    fun testContains() {
        gc.addGraphic(pt)
        Assert.assertTrue(gc.contains(Point(), null))
        Assert.assertTrue(gc.contains(Point(1, 0), null))
        Assert.assertFalse(gc.contains(Point(10, 10), null))
    }

    @Test
    fun testInitContextMenu() {
        val menu = JPopupMenu()
        gc.initContextMenu(menu, null, Point(), null, null, null)
    }

    @Test
    fun testGraphicChanged() {
        val instance: GraphicComposite<*> = GraphicComposite<Any?>()
        instance.graphicChanged(pt)
    }

    @Test
    fun testGetStyleContext() {
        Assert.assertNotNull(gc.getStyleContext())
        try {
            gc.setStyleContext(null)
            Assert.fail("Composites must have style contexts.")
        } catch (x: IllegalStateException) {
            // expected
        }
    }

    @Test
    fun testSetStyleContext() {
        gc.setStyleContext(StyleContext())
    }

    @Test
    fun testIntersects() {
        gc.addGraphic(pt)
        Assert.assertTrue(gc.intersects(Rectangle(0, 0, 10, 10), null))
        Assert.assertFalse(gc.intersects(Rectangle(5, 5, 10, 10), null))
    }

    @Test
    fun testVisibleEntries() {
        gc.addGraphic(pt)
        Assert.assertTrue(Iterables.elementsEqual(Lists.newArrayList(pt), gc.visibleEntries()))
        pt.setStyleHint(StyleHints.HIDDEN_HINT, true)
        Assert.assertTrue(Iterables.isEmpty(gc.visibleEntries()))
    }

    @Test
    fun testVisibleEntriesInReverse() {
        gc.addGraphic(pt)
        val p2: PrimitiveGraphic<*, *>? = JGraphics.point(Point())
        gc.addGraphic(p2)
        Assert.assertTrue(Iterables.elementsEqual(Lists.newArrayList(pt, p2), gc.visibleEntries()))
        Assert.assertTrue(Iterables.elementsEqual(Lists.newArrayList(p2, pt), gc.visibleEntriesInReverse()))
    }

    @Test
    fun testGetTooltip() {
        gc.addGraphic(pt)
        Assert.assertNull(gc.getTooltip(Point(), null))
        pt.setTooltipEnabled(true)
        Assert.assertNull(gc.getTooltip(Point(), null))
        pt.setDefaultTooltip("test")
        Assert.assertEquals("test", gc.getTooltip(Point(), null))
    }

    @Test
    fun testMouseGraphicAt() {
        gc.addGraphic(pt)
        Assert.assertEquals(pt, gc.mouseGraphicAt(Point(), null))
        pt.setMouseDisabled(true)
        Assert.assertNull(gc.mouseGraphicAt(Point(), null))
    }

    @Test
    fun testSelectableGraphicAt() {
        gc.addGraphic(pt)
        Assert.assertNull(gc.selectableGraphicAt(Point(), null))
        pt.setSelectionEnabled(true)
        Assert.assertEquals(pt, gc.selectableGraphicAt(Point(), null))
        pt.setSelectionEnabled(false)
        Assert.assertNull(gc.selectableGraphicAt(Point(), null))
    }

    @Test
    fun testSelectableGraphicsIn() {
        val box: Rectangle2D = Rectangle(0, 0, 5, 5)
        gc.addGraphic(pt)
        pt.setSelectionEnabled(true)
        Assert.assertTrue(Iterables.elementsEqual(listOf(pt), gc.selectableGraphicsIn(box, null)))
        pt.setSelectionEnabled(false)
        Assert.assertTrue(Iterables.isEmpty(gc.selectableGraphicsIn(box, null)))
    }

    init {
        gc = GraphicComposite()
        gc.setStyleContext(StyleContext())
        pt = JGraphics.point(Point2D.Double())
    }
}