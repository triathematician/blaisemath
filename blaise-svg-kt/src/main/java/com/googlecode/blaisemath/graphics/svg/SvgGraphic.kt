package com.googlecode.blaisemath.graphics.svg
/*
 * #%L
 * BlaiseSvg
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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
 */
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
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
import com.googlecode.blaisemath.graphics.core.Graphic
import com.googlecode.blaisemath.graphics.core.GraphicComposite
import com.googlecode.blaisemath.graphics.core.GraphicUtils
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.graphics.swing.AnchorTestFrame
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
import com.googlecode.blaisemath.svg.HelloWorldSvg
import com.googlecode.blaisemath.svg.SvgElement
import com.googlecode.blaisemath.svg.SvgPathTest
import com.googlecode.blaisemath.svg.SvgRoot
import com.googlecode.blaisemath.svg.SvgRootTest
import com.googlecode.blaisemath.svg.SvgTool
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.ui.PropertyActionPanel
import junit.framework.TestCase
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.geom.NoninvertibleTransformException
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.JPopupMenu

/**
 * Uses an [SvgElement] as a primitive to be rendered on a [JGraphicComponent].
 * Allows setting bounding boxes, so that the Svg element can be scaled or moved on the canvas.
 *
 * @author elisha
 */
class SvgGraphic : GraphicComposite<Graphics2D?>() {
    /** Source Svg element to be drawn  */
    private var element: SvgElement? = null

    /** Describes where on the canvas the element will be drawn  */
    private var graphicBounds: Rectangle2D? = null

    /** The graphic object that will be rendered  */
    private var primitiveElement: Graphic<Graphics2D?>? = null
    private fun updateGraphics() {
        val nue: Graphic<Graphics2D?> = SvgElementGraphicConverter.Companion.getInstance()
                .convert(element)
        if (primitiveElement == null) {
            addGraphic(nue)
        } else {
            replaceGraphics(setOf(primitiveElement), setOf(nue))
        }
        primitiveElement = nue
        if (element is SvgRoot) {
            val wid = (element as SvgRoot?).getWidth()
            val ht = (element as SvgRoot?).getHeight()
            graphicBounds = Rectangle2D.Double(0, 0, wid, ht)
        }
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    fun getElement(): SvgElement? {
        return element
    }

    fun setElement(el: SvgElement?) {
        val old: Any? = element
        if (old !== el) {
            element = el
            updateGraphics()
        }
    }

    fun getGraphicBounds(): Rectangle2D? {
        return graphicBounds
    }

    fun setGraphicBounds(graphicBounds: Rectangle2D?) {
        this.graphicBounds = graphicBounds
        fireGraphicChanged()
    }
    //</editor-fold>
    /** Generate transform used to scale/translate the Svg. Transforms the view box to within the graphic bounds.  */
    private fun transform(): AffineTransform? {
        if (graphicBounds == null || element !is SvgRoot) {
            return null
        }
        val viewBox = (element as SvgRoot?).getViewBoxAsRectangle()
        return if (viewBox == null) null else PanAndZoomHandler.Companion.scaleRectTransform(graphicBounds, viewBox)
    }

    /** Inverse transform. Transforms the graphic bounds to the view box.  */
    private fun inverseTransform(): AffineTransform? {
        if (graphicBounds == null || element !is SvgRoot) {
            return null
        }
        val t = transform()
        return try {
            if (t == null || t.determinant == 0.0) null else t.createInverse()
        } catch (ex: NoninvertibleTransformException) {
            LOG.log(Level.SEVERE, "Unexpected", ex)
            null
        }
    }

    override fun boundingBox(canvas: Graphics2D?): Rectangle2D? {
        val tx = transform()
        val norm = GraphicUtils.boundingBox(entries, canvas)
        return if (tx == null) norm else tx.createTransformedShape(norm).bounds2D
    }

    override fun contains(point: Point2D?, canvas: Graphics2D?): Boolean {
        val tp = transform(point)
        return inViewBox(tp) && super.contains(tp, canvas)
    }

    override fun intersects(box: Rectangle2D?, canvas: Graphics2D?): Boolean {
        val vb = viewBox()
        val tbox = if (vb == null) transform(box) else transform(box).createIntersection(vb)
        return tbox.getWidth() >= 0 && tbox.getHeight() >= 0 && super.intersects(tbox, canvas)
    }

    override fun graphicAt(point: Point2D?, canvas: Graphics2D?): Graphic<Graphics2D?>? {
        val tp = transform(point)
        return if (!inViewBox(tp)) null else super.graphicAt(tp, canvas)
    }

    override fun selectableGraphicAt(point: Point2D?, canvas: Graphics2D?): Graphic<Graphics2D?>? {
        val tp = transform(point)
        return if (!inViewBox(tp)) null else super.selectableGraphicAt(tp, canvas)
    }

    override fun selectableGraphicsIn(box: Rectangle2D?, canvas: Graphics2D?): MutableSet<Graphic<Graphics2D?>?>? {
        val vb = viewBox()
        val tp = if (vb == null) transform(box) else transform(box).createIntersection(vb)
        return if (tp.getWidth() <= 0 || tp.getHeight() <= 0) emptySet() else super.selectableGraphicsIn(tp, canvas)
    }

    override fun mouseGraphicAt(point: Point2D?, canvas: Graphics2D?): Graphic<Graphics2D?>? {
        val tp = transform(point)
        return if (!inViewBox(tp)) null else super.mouseGraphicAt(tp, canvas)
    }

    override fun getTooltip(p: Point2D?, canvas: Graphics2D?): String? {
        val tp = transform(p)
        return if (!inViewBox(tp)) null else super.getTooltip(tp, canvas)
    }

    override fun initContextMenu(menu: JPopupMenu?, src: Graphic<Graphics2D?>?, point: Point2D?, focus: Any?, selection: MutableSet<Graphic<Graphics2D?>?>?, canvas: Graphics2D?) {
        val tp = transform(point)
        if (inViewBox(tp)) {
            super.initContextMenu(menu, src, tp, focus, selection, canvas)
        }
    }

    override fun renderTo(canvas: Graphics2D?) {
        val tx = transform()
        if (RENDER_BOUNDS && graphicBounds != null) {
            canvas.setColor(Color.red)
            canvas.draw(graphicBounds)
        }
        if (tx == null) {
            super.renderTo(canvas)
        } else {
            val original = canvas.getTransform()
            val oldClip = canvas.getClip()
            canvas.transform(tx)
            val viewBox = viewBox()
            if (oldClip != null) {
                canvas.setClip(viewBox.createIntersection(transform(oldClip.bounds2D)))
            }
            if (RENDER_VIEW_BOX) {
                canvas.setColor(Color.blue)
                canvas.draw(viewBox)
            }
            super.renderTo(canvas)
            canvas.setTransform(original)
            canvas.setClip(oldClip)
        }
    }

    private fun transform(pt: Point2D?): Point2D? {
        val tx = inverseTransform()
        return if (tx == null) pt else tx.transform(pt, null)
    }

    private fun transform(box: Rectangle2D?): Rectangle2D? {
        val tx = inverseTransform()
        return if (tx == null) box else tx.createTransformedShape(box).bounds2D
    }

    private fun viewBox(): Rectangle2D? {
        return if (element is SvgRoot) (element as SvgRoot?).getViewBoxAsRectangle() else null
    }

    /** Test if point is in view box, where point is in svg coords  */
    private fun inViewBox(pt: Point2D?): Boolean {
        val box = viewBox()
        return box == null || box.contains(pt)
    }

    companion object {
        private val LOG = Logger.getLogger(SvgGraphic::class.java.name)
        private const val RENDER_BOUNDS = false
        private const val RENDER_VIEW_BOX = false

        /**
         * Create a new instance for the provided Svg.
         * @param element Svg to draw
         * @return graphic
         */
        fun create(element: SvgElement?): SvgGraphic? {
            checkNotNull(element)
            val res = SvgGraphic()
            res.setElement(element)
            return res
        }

        /**
         * Create a new instance for the provided Svg.
         * @param svg svg as a string
         * @return graphic
         */
        fun create(svg: String?): SvgGraphic? {
            return try {
                create(SvgRoot.Companion.load(svg))
            } catch (ex: IOException) {
                LOG.log(Level.WARNING, "Invalid Svg", ex)
                SvgGraphic()
            }
        }
    }
}