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
import com.google.common.base.Strings
import com.google.common.collect.Lists
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
import com.googlecode.blaisemath.graphics.swing.StyledText
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
import com.googlecode.blaisemath.style.Anchor
import com.googlecode.blaisemath.style.AttributeSet
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
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import java.awt.geom.RectangularShape
import java.awt.image.BufferedImage
import java.util.*

/**
 * Draws a string within the boundaries of a given clip. The string is wrapped at word breaks as needed to stay within
 * the clip. It is truncated if necessary, and ellipsis (...) used to indicate truncation. When providing an anchor with
 * the style, the anchor positions the text inside the clip path relative to the anchor. So if the text is drawn in
 * a rectangle and the anchor is CENTER, the text will be drawn at the center of the rectangle; if the anchor is WEST,
 * the text will be drawn centered vertically and right-aligned next to the right boundary of the rectangle.
 *
 * @author Elisha Peterson
 */
class WrappedTextRenderer : TextRenderer() {
    /** Provides clip boundaries  */
    protected var clipPath: RectangularShape? = null

    /** Insets used for text anchoring.  */
    private var insets = defaultInsets()

    /** Flag to show text in a circle/ellipse all on a single line (no wrapping) if not enough space  */
    private var allowFullTextOnCircle = false

    /** Minimum width factor (multiple of font size) at which to abbreviate attempt to render text, and just show first character  */
    private var minWidthFactor = 2

    /** Maximum number of points to reduce font size by for smaller rectangles. Set to 0 to keep font size the same. Defaults to 2.  */
    private var maxReduceFontSize = 2
    override fun toString(): String {
        return String.format("WrappedTextRenderer[clip=%s]", clipPath)
    }
    //region BUILDER PATTERNS
    /**
     * Sets clip and returns pointer to object
     * @param clip the clip to use
     * @return this
     */
    fun clipPath(clip: RectangularShape?): WrappedTextRenderer? {
        setTextBounds(clip)
        return this
    }

    /**
     * Sets insets and returns pointer to object
     * @param insets the insets to use
     * @return this
     */
    fun insets(insets: Insets?): WrappedTextRenderer? {
        this.insets = insets
        return this
    }

    //endregion
    //region PROPERTIES
    fun getTextBounds(): RectangularShape? {
        return clipPath
    }

    fun setTextBounds(clip: RectangularShape?) {
        clipPath = clip
    }

    fun getInsets(): Insets? {
        return insets
    }

    fun setInsets(insets: Insets?) {
        this.insets = insets
    }

    fun isAllowFullTextOnCircle(): Boolean {
        return allowFullTextOnCircle
    }

    fun setAllowFullTextOnCircle(allowFullTextOnCircle: Boolean) {
        this.allowFullTextOnCircle = allowFullTextOnCircle
    }

    fun getMaxReduceFontSize(): Int {
        return maxReduceFontSize
    }

    fun setMaxReduceFontSize(maxReduceFontSize: Int) {
        this.maxReduceFontSize = maxReduceFontSize
    }

    fun getMinWidthFactor(): Int {
        return minWidthFactor
    }

    fun setMinWidthFactor(minWidthFactor: Int) {
        this.minWidthFactor = minWidthFactor
    }

    //endregion
    override fun render(text: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?) {
        if (Strings.isNullOrEmpty(text.getText())) {
            return
        }
        val lines = computeLines(text.getText(), style, clipPath, insets, canvas)
        for (t in lines) {
            getInstance().render(t.getText(), t.getStyle(), canvas)
        }
    }

    override fun boundingBox(text: AnchoredText?, style: AttributeSet?, canvas: Graphics2D?): Rectangle2D? {
        if (Strings.isNullOrEmpty(text.getText())) {
            return null
        }
        val lines = computeLines(text.getText(), style, clipPath, getInsets(), canvas)
        var res: Rectangle2D? = null
        for (t in lines) {
            val box: Rectangle2D = getInstance().boundingBox(t.getText(), t.getStyle(), canvas)
            res = if (res == null) box else res.createUnion(box)
        }
        return res
    }

    /**
     * Use the provided input text to compute locations for text by wrapping text
     * as appropriate for the given clip.
     * @param text the text to wrap
     * @param style text style
     * @param textBounds bounding box for text
     * @param insets insets for text inside box
     * @param canvas target canvas
     * @return text to render
     */
    fun computeLines(text: String?, style: AttributeSet?, textBounds: Shape?, insets: Insets?, @Nullable canvas: Graphics2D?): Iterable<StyledText?>? {
        var canvas = canvas
        if (canvas == null) {
            canvas = BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).createGraphics()
            canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        }
        val bounds = textBounds.getBounds2D()
        return if (textBounds is Ellipse2D) {
            val textClip: Ellipse2D = Ellipse2D.Double(
                    bounds.minX + insets.left, bounds.minY + insets.top,
                    bounds.width - insets.left - insets.right, bounds.height - insets.top - insets.bottom)
            computeEllipseLines(text, style, textClip, canvas)
        } else {
            val textClip: Rectangle2D = Rectangle2D.Double(
                    bounds.minX + insets.left, bounds.minY + insets.top,
                    bounds.width - insets.left - insets.right, bounds.height - insets.top - insets.bottom)
            computeRectangleLines(text, style, textClip, canvas)
        }
    }

    //region COMPUTE LINE BREAKS
    private fun computeEllipseLines(text: String?, style: AttributeSet?, ell: Ellipse2D?, canvas: Graphics2D?): MutableList<StyledText?>? {
        canvas.setFont(Styles.fontOf(style))
        val bounds = canvas.getFontMetrics().getStringBounds(text, canvas)
        val centeredStyle: AttributeSet = AttributeSet.withParent(style).and(Styles.TEXT_ANCHOR, Anchor.CENTER)
        val showOnOneLine = allowFullTextOnCircle && (bounds.width < ell.getWidth() - 8 || ell.getWidth() * .6 < 3 * canvas.getFont().size2D)
        return if (showOnOneLine) {
            listOf(StyledText(AnchoredText(ell.getCenterX(), ell.getCenterY(), text), centeredStyle))
        } else {
            computeRectangleLines(text, centeredStyle,
                    Rectangle2D.Double(
                            ell.getX() + ell.getWidth() * .15, ell.getY() + ell.getHeight() * .15,
                            ell.getWidth() * .7, ell.getHeight() * .7),
                    canvas
            )
        }
    }

    private fun computeRectangleLines(text: String?, style: AttributeSet?, rect: Rectangle2D?, canvas: Graphics2D?): MutableList<StyledText?>? {
        // make font smaller if lots of words
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        var font = Styles.fontOf(style)
        if (maxReduceFontSize > 0) {
            val fontSize = font.size
            // will reduce font size for narrow rectangles
            val narrowRectangle = rect.getWidth() < fontSize * 5
            // will reduce font size for smaller rectangles
            val areaRatio = rect.getWidth() * rect.getHeight() / (fontSize * fontSize / 1.5 * text.length)
            // reduce font size
            if (areaRatio < 1) {
                val newFontSize = Math.max(font.size2D - maxReduceFontSize.toDouble(), font.size2D * areaRatio) as Float
                font = font.deriveFont(newFontSize)
            } else if (narrowRectangle) {
                font = font.deriveFont(font.size2D - Math.min(2, maxReduceFontSize))
            }
            canvas.setFont(font)
        }
        val lines = computeLineBreaks(text, font, rect.getWidth(), rect.getHeight())
        val textAnchor = Styles.anchorOf(style, Anchor.CENTER)
        val sz = canvas.getFont().size2D
        var y0 = getInitialY(textAnchor, rect, sz, lines.size)
        val res: MutableList<StyledText?> = Lists.newArrayList()
        val plainStyle = style.flatCopy()
        plainStyle.put(Styles.FONT_SIZE, font.size2D)
        plainStyle.remove(Styles.ALIGN_BASELINE)
        plainStyle.remove(Styles.TEXT_ANCHOR)
        plainStyle.remove(Styles.OFFSET)
        for (s in lines) {
            val wid = canvas.getFontMetrics().getStringBounds(s, canvas).width
            when (textAnchor) {
                Anchor.WEST, Anchor.SOUTHWEST, Anchor.NORTHWEST -> res.add(StyledText(AnchoredText(rect.getX(), y0, s), plainStyle))
                Anchor.EAST, Anchor.SOUTHEAST, Anchor.NORTHEAST -> res.add(StyledText(AnchoredText(rect.getMaxX() - wid, y0, s), plainStyle))
                else ->                     // x-centered
                    res.add(StyledText(AnchoredText(rect.getCenterX() - wid / 2.0, y0, s), plainStyle))
            }
            y0 += sz + 2.toDouble()
        }
        return res
    }

    /**
     * Create set of lines representing the word-wrapped version of the string. Words are
     * wrapped at spaces if possible, and always wrapped at line breaks. Lines are constrained to be within given width and height.
     * If the string is too long to fit in the given space, it is truncated and "..." appended.
     * Assumes lines are separated by current font size + 2.
     * @param string initial string
     * @param font the font to be drawn in
     * @param width width of bounding box
     * @param height height of bounding box
     * @return lines
     */
    fun computeLineBreaks(string: String?, font: Font?, width: Double, height: Double): MutableList<String?>? {
        val frc = FontRenderContext(null, true, true)
        val sBounds = font.getStringBounds(string, frc)
        val lines: MutableList<String?> = ArrayList()
        val length = string.length
        if (length == 0) {
            // do nothing
        } else if (width < minWidthFactor * font.getSize()) {
            // if really small, show only first character
            lines.add((if (length <= 2) string.substring(0, length) else string.substring(0, 1) + "...").trim { it <= ' ' })
        } else if (sBounds.width <= width - 4 && !string.contains("\n")) {
            // enough to fit the entire string
            lines.add(string.trim { it <= ' ' })
        } else {
            // need to wrap string
            var totHt = font.getSize() as Double + 2
            var pos0 = 0
            var pos1 = 1
            while (pos1 <= string.length) {
                while (pos1 <= string.length && string.get(pos1 - 1) != '\n' && font.getStringBounds(string.substring(pos0, pos1), frc).width < width - 4) {
                    pos1++
                }
                if (pos1 >= string.length) {
                    pos1 = string.length + 1
                } else if (string.get(pos1 - 1) == '\n') {
                    // wrap at the line break
                } else {
                    // wrap at the previous space
                    val idx = string.lastIndexOf(' ', pos1 - 1)
                    if (idx > pos0) {
                        pos1 = idx + 2
                    }
                }
                var s = string.substring(pos0, pos1 - 1)
                totHt += font.getSize() + 2.toDouble()
                if (totHt >= height - 2) {
                    // will be the last line, may need to truncate
                    if (pos1 - 1 < string.length) {
                        s += "..."
                    }
                    while (s.length >= 4
                            && font.getStringBounds(s, frc).width > width - 4) {
                        s = s.substring(0, s.length - 4) + "..."
                    }
                    lines.add(s.trim { it <= ' ' })
                    break
                } else {
                    lines.add(s.trim { it <= ' ' })
                }
                pos0 = pos1 - 1
                if (pos0 < string.length && string.get(pos0) == '\n') {
                    pos0++
                }
                pos1 = pos0 + 1
            }
        }
        return lines
    } //endregion

    companion object {
        /**
         * Default insets used for wrapping text.
         * @return insets
         */
        fun defaultInsets(): Insets? {
            return Insets(2, 2, 2, 2)
        }

        /** Computes the starting y location for subsequent lines of text.  */
        private fun getInitialY(textAnchor: Anchor?, rect: Rectangle2D?, fontSize: Float, lineCount: Int): Double {
            return when (textAnchor) {
                Anchor.NORTH, Anchor.NORTHWEST, Anchor.NORTHEAST -> rect.getY() + fontSize
                Anchor.SOUTH, Anchor.SOUTHWEST, Anchor.SOUTHEAST -> rect.getMaxY() - (lineCount - 1) * (fontSize + 2)
                else ->                 // y-centered
                    rect.getCenterY() - lineCount / 2.0 * (fontSize + 2) + fontSize
            }
        }
    }
}