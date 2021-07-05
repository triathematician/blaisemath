package com.googlecode.blaisemath.graphics.swing.render
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
 */
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
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
import com.googlecode.blaisemath.graphics.AnchoredText
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
import com.googlecode.blaisemath.style.Anchor
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Renderer
import com.googlecode.blaisemath.style.Styles
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
import java.awt.*
import java.awt.font.FontRenderContext
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Renders a string of text on a canvas. An anchor is used to position the text relative to a point. The default anchor
 * is SOUTHWEST, with the text drawn to the right/above the point.
 *
 * @author Elisha Peterson
 */
open class TextRenderer : Renderer<AnchoredText?, Graphics2D?> {
    override fun render(primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?) {
        render(setOf(primitive), style, canvas)
    }

    /**
     * Render a collection of text primitives at one time.
     * @param primitives the primitives to render
     * @param style the style used for rendering
     * @param canvas where to render it
     */
    fun render(primitives: Iterable<AnchoredText?>?, style: AttributeSet?, canvas: Graphics2D?) {
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        canvas.setColor(style.getColor(Styles.FILL, Color.black))
        canvas.setFont(Styles.fontOf(style))
        for (at in primitives) {
            if (!Strings.isNullOrEmpty(at.getText())) {
                val bounds = boundingBox(at, style, canvas)
                canvas.drawString(at.getText(), bounds.getX() as Float, bounds.getMaxY() as Float)
            }
        }
    }

    override fun contains(point: Point2D?, primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        val bounds = boundingBox(primitive, style, canvas)
        return bounds != null && bounds.contains(point)
    }

    override fun intersects(rect: Rectangle2D?, primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Boolean {
        val bounds = boundingBox(primitive, style, canvas)
        return bounds != null && bounds.intersects(rect)
    }

    /**
     * Get the bounding box for the given text/style to be rendered on the given canvas. This computation can be expensive, so the
     * results are cached so that if the text, font, and render context do not change, the cached results are used.
     * @param primitive text/location
     * @param style desired style
     * @param canvas where to render
     * @return bounding box for the result
     */
    @Nullable
    override fun boundingBox(primitive: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Rectangle2D? {
        if (Strings.isNullOrEmpty(primitive.getText())) {
            return null
        }
        val font = Styles.fontOf(style)
        val frc = if (canvas == null) FontRenderContext(font.transform, true, false) else canvas.fontRenderContext
        val info = TextBoundsInfo(primitive.getText(), font, frc)
        val dimensions: Rectangle2D.Double?
        dimensions = try {
            CACHE.get(info)
        } catch (e: ExecutionException) {
            LOG.log(Level.FINE, "Unexpected", e)
            textDimensions(info)
        }
        val textAnchor = Styles.anchorOf(style, Anchor.SOUTHWEST)
        val offset = style.getPoint2D(Styles.OFFSET, Point())!!
        return textAnchor.rectangleAnchoredAt(
                primitive.getX() + offset.x,
                primitive.getY() + offset.y - dimensions.height,
                dimensions.width, dimensions.height)
    }

    /** Info required for most expensive font computation.  */
    private class TextBoundsInfo private constructor(private val text: String?, private val font: Font?, private val context: FontRenderContext?) {
        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val that = o as TextBoundsInfo?
            return Objects.equal(text, that.text) &&
                    Objects.equal(font, that.font) &&
                    Objects.equal(context, that.context)
        }

        override fun hashCode(): Int {
            return Objects.hashCode(text, font, context)
        }
    }

    companion object {
        /** Assumed monitor resolution, used in bounding box calculations  */
        private const val DOTS_PER_INCH = 72

        /** Logging  */
        private val LOG = Logger.getLogger(TextRenderer::class.java.name)

        /** Static instance  */
        private val INST: TextRenderer? = TextRenderer()

        /** Caches expensive computation of font bounds  */
        private val CACHE: LoadingCache<TextBoundsInfo?, Rectangle2D.Double?>? = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(object : CacheLoader<TextBoundsInfo?, Rectangle2D.Double?>() {
                    fun load(textBoundsInfo: TextBoundsInfo?): Rectangle2D.Double? {
                        return textDimensions(textBoundsInfo)
                    }
                })

        /**
         * Get default static instance of the renderer.
         * @return renderer
         */
        fun getInstance(): TextRenderer? {
            return INST
        }

        private fun textDimensions(info: TextBoundsInfo?): Rectangle2D.Double? {
            return textDimensions(info.text, info.font, info.context)
        }

        private fun textDimensions(text: String?, font: Font?, context: FontRenderContext?): Rectangle2D.Double? {
            val width = font.getStringBounds(text, context).width
            val height = font.getSize() * DOTS_PER_INCH / Toolkit.getDefaultToolkit().screenResolution.toDouble()
            return Rectangle2D.Double(0, 0, width, height)
        }
    }
}