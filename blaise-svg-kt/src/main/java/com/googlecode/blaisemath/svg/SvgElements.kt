/**
 * SvgObjectFactory.java
 * Created on Sep 26, 2014
 */
package com.googlecode.blaisemath.svg
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
import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.coordinate.OrientedPoint2D
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
import com.googlecode.blaisemath.graphics.AnchoredIcon
import com.googlecode.blaisemath.graphics.AnchoredImage
import com.googlecode.blaisemath.graphics.AnchoredText
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.graphics.swing.AnchorTestFrame
import com.googlecode.blaisemath.graphics.swing.render.WrappedTextRenderer
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
import com.googlecode.blaisemath.style.*
import com.googlecode.blaisemath.svg.HelloWorldSvg
import com.googlecode.blaisemath.svg.SvgPathTest
import com.googlecode.blaisemath.svg.SvgRootTest
import com.googlecode.blaisemath.svg.SvgTool
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.ui.PropertyActionPanel
import com.googlecode.blaisemath.util.Images
import junit.framework.TestCase
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.geom.*
import java.awt.image.BufferedImage
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.Icon

/**
 * Factory methods for converting to/from Svg Objects.
 * @author petereb1
 */
object SvgElements {
    private val LOG = Logger.getLogger(SvgElements::class.java.name)

    /**
     * Create new svg object from given id, shape, and style. Supports shapes
     * [Rectangle2D], [RoundRectangle2D], [Ellipse2D], and
     * [Line2D].
     * @param id object id
     * @param shape object's shape
     * @param style object's style
     * @return svg object
     */
    fun create(id: String?, shape: Shape?, style: AttributeSet?): SvgElement? {
        val res: SvgElement
        res = if (shape is Rectangle2D || shape is RoundRectangle2D) {
            SvgRectangle.Companion.shapeConverter().reverse().convert(shape as RectangularShape?)
        } else if (shape is Ellipse2D) {
            val ell = shape as Ellipse2D?
            if (ell.getWidth() == ell.getHeight()) {
                SvgCircle.Companion.shapeConverter().reverse().convert(ell)
            } else {
                SvgEllipse.Companion.shapeConverter().reverse().convert(ell)
            }
        } else if (shape is Line2D) {
            SvgLine.Companion.shapeConverter().reverse().convert(shape as Line2D?)
        } else if (shape is Path2D) {
            SvgPath.Companion.shapeConverter().reverse().convert(shape as Path2D?)
        } else if (shape is Area) {
            SvgPath.Companion.create(shape.getPathIterator(null))
        } else {
            Logger.getLogger(SvgElements::class.java.name).log(Level.WARNING, "Shapes of type {0} are not yet supported.", shape.javaClass)
            return null
        }
        res.setId(id)
        res.style = AttributeSet.create(style.attributeMap)
        if (style.get(Styles.FILL) == null) {
            res.style.put(Styles.FILL, null)
        }
        if (style.get(Styles.STROKE) == null) {
            res.style.put(Styles.STROKE, null)
        }
        return res
    }

    /**
     * Create new svg object from a provided point/style.
     * @param id object id
     * @param point the point
     * @param style object's style
     * @return svg object
     */
    fun create(id: String?, point: Point2D?, style: AttributeSet?): SvgElement? {
        var m = style.get(Styles.MARKER) as Marker?
        if (m == null) {
            m = Markers.CIRCLE
        }
        val ort = style.getFloat(Styles.MARKER_ORIENT, if (point is OrientedPoint2D) (point as OrientedPoint2D?).angle as Float else 0f)
        val rad = style.getFloat(Styles.MARKER_RADIUS, 4f)
        val shape = m.create(point, ort.toDouble(), rad)
        return create(id, shape, style)
    }

    /**
     * Create new svg text object from given id, anchored text, and style.
     * @param id object id
     * @param text object text
     * @param style object's style
     * @param rend the text renderer
     * @return svg text object
     */
    fun create(id: String?, text: AnchoredText?, style: AttributeSet?, @Nullable rend: Renderer<*, *>?): SvgElement? {
        if (rend is WrappedTextRenderer) {
            val res = createWrappedText(text.getText(), style, (rend as WrappedTextRenderer?).getTextBounds())
            res.setId(id)
            return res
        }
        val res: SvgText = SvgText.Companion.textConverter().reverse().convert(text)
        res.setId(id)
        val ta = style.get(Styles.TEXT_ANCHOR)
        val copy: AttributeSet = AttributeSet.create(style.attributeMap)
        if (ta is Anchor) {
            copy.put(Styles.TEXT_ANCHOR, Styles.toTextAnchor(ta as Anchor?))
            copy.put(Styles.ALIGN_BASELINE, Styles.toAlignBaseline(ta as Anchor?))
        } else if (ta is String && Styles.isAnchorName(ta)) {
            copy.put(Styles.TEXT_ANCHOR, Styles.toTextAnchor(ta as String?))
            copy.put(Styles.ALIGN_BASELINE, Styles.toAlignBaseline(ta as String?))
        }
        res.style = copy
        return res
    }

    /**
     * Generates group of text elements formed by wrapping text
     * @param text text
     * @param style text style
     * @param bounds text bounds
     * @return group element with several lines (or text element if a single line)
     */
    fun createWrappedText(text: String?, style: AttributeSet?, bounds: RectangularShape?): SvgElement? {
        val testCanvas = BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).createGraphics()
        val rend = WrappedTextRenderer()
        rend.minWidthFactor = 0
        rend.maxReduceFontSize = 0
        val lines = rend.computeLines(text, style, bounds, WrappedTextRenderer.Companion.defaultInsets(), testCanvas)
        val grp = SvgGroup()
        for (st in lines) {
            grp.addElement(create(null, st.text, st.style, null))
        }
        val res = if (grp.elements.size == 1) grp.elements[0] else grp
        res.style = AttributeSet.create(style.attributeMap)
        return res
    }

    /**
     * Create new svg image object from given id, anchored image, and style.
     * @param id object id
     * @param img image object
     * @param style object's style
     * @return svg image object
     */
    fun create(id: String?, img: AnchoredImage?, style: AttributeSet?): SvgImage? {
        val anchor = Styles.anchorOf(style, Anchor.NORTHWEST)
        val offset: Point2D = anchor.offsetForRectangle(img.getWidth(), img.getHeight())
        val adjustedImage = AnchoredImage(
                img.getX() + offset.x,
                img.getY() - img.getHeight() + offset.y,
                img.getWidth() as Double, img.getHeight() as Double,
                img.getOriginalImage(), img.getReference())
        val res: SvgImage = SvgImage.Companion.imageConverter().reverse().convert(adjustedImage)
        res.setId(id)
        res.style = AttributeSet.create(style.attributeMap)
        return res
    }

    /**
     * Create new svg image object from given id, anchored icon, and style.
     * @param id object id
     * @param icon icon object
     * @param style object's style
     * @return svg image object
     */
    fun create(id: String?, icon: AnchoredIcon?, style: AttributeSet?): SvgImage? {
        val anchor = Styles.anchorOf(style, Anchor.NORTHWEST)
        val offset: Point2D = anchor.offsetForRectangle(icon.getIconWidth().toDouble(), icon.getIconHeight().toDouble())
        val res = SvgImage(
                icon.getX() + offset.x,
                icon.getY() - icon.getIconHeight() + offset.y,
                icon.getIconWidth() as Double, icon.getIconHeight() as Double,
                encodeAsUri(icon.getIcon()))
        res.setId(id)
        val sty: AttributeSet = AttributeSet.create(style.attributeMap)
        sty.remove(Styles.TEXT_ANCHOR)
        sty.remove(Styles.ALIGN_BASELINE)
        res.style = sty
        return res
    }

    private fun encodeAsUri(icon: Icon?): String? {
        return try {
            val bi = BufferedImage(2 * icon.getIconWidth(), 2 * icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB)
            val g2 = bi.createGraphics()
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.transform = AffineTransform.getScaleInstance(2.0, 2.0)
            icon.paintIcon(null, g2, 0, 0)
            Images.encodeDataUriBase64(bi, "png")
        } catch (ex: IOException) {
            LOG.log(Level.SEVERE, "Encoding error", ex)
            ""
        }
    }

    /**
     * Return true if element is a path type (i.e. no fill expected)
     * @param el an element
     * @return true if its a path
     */
    fun isPath(el: SvgElement?): Boolean {
        return el is SvgLine || el is SvgPolyline
    }

    /**
     * Return an iterator for an element. Will iterate over this element, and if
     * a group, all nested children of this element. Groups are added before their
     * children.
     * @param element the element to start with
     * @return iteration over this element, and all of its child elements
     */
    fun shapeIterator(element: SvgElement?): Iterable<SvgElement?>? {
        val res: MutableList<SvgElement?> = Lists.newArrayList()
        res.add(element)
        if (element is SvgGroup) {
            for (el in (element as SvgGroup?).getElements()) {
                Iterables.addAll(res, shapeIterator(el))
            }
        }
        return res
    }
}