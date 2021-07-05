package com.googlecode.blaisemath.graphics.swing
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
import com.google.common.base.Preconditions.checkArgument
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
import com.googlecode.blaisemath.graphics.core.GMouseEvent
import com.googlecode.blaisemath.graphics.core.Graphic
import com.googlecode.blaisemath.graphics.core.GraphicUtils
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
import com.googlecode.blaisemath.style.StyleContext
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
import com.googlecode.blaisemath.util.SetSelectionModel
import junit.framework.TestCase
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.geom.AffineTransform
import java.awt.geom.NoninvertibleTransformException
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.function.Consumer
import javax.swing.JComponent

/**
 * Swing component that collects and draws shapes on a screen.
 * The shapes and their styles are enclosed within a [JGraphicRoot] class,
 * which also sets up the requisite mouse handling and manages the drawing.
 *
 * @see JGraphicRoot
 *
 * @author Elisha Peterson
 */
open class JGraphicComponent : JComponent(), TransformedCoordinateSpace {

    /** The visible shapes.  */
    val graphicRoot = JGraphicRoot(this)
    /** Style manager. */
    var styleContext
        get() = graphicRoot.styleContext
        set(value) { graphicRoot.styleContext = value }

    /** Affine transform applied to graphics canvas before drawing (enables pan and zoom).  */
    var transform: AffineTransform? = null
        private set

    /** Store inverse transform  */
    var inverseTransform: AffineTransform? = null
        private set

    /** Underlay painters  */
    val underlays = mutableListOf<CanvasPainter<Graphics2D>>()

    /** Overlay painters  */
    val overlays = mutableListOf<CanvasPainter<Graphics2D>>()

    /** Used for selecting graphics  */
    protected val selector = JGraphicSelectionHandler(this)
    /** Whether mouse selection is enabled. */
    val isSelectionEnabled
        get() = selector.isSelectionEnabled()
        set(value) { selector.setSelectionEnabled(value) }
    val selectionModel
        get() = selector.getSelectionModel()

    /** Whether antialias is enabled  */
    var isAntialiasOn = true
        set(value) {
            field = value
            repaint()
        }

    //region GRAPHICS MUTATORS

    /**
     * Add graphics to the component
     * @param add graphics to add
     */
    fun addGraphics(add: Iterable<out Graphic<Graphics2D?>?>?) {
        graphicRoot.addGraphics(add)
    }

    /**
     * Add a single graphic to the component
     * @param gfc graphic to add
     */
    fun addGraphic(gfc: Graphic<Graphics2D?>?) {
        graphicRoot.addGraphic(gfc)
    }

    /**
     * Remove graphics from the component
     * @param remove graphics to remove
     */
    fun removeGraphics(remove: Iterable<out Graphic<Graphics2D?>?>?) {
        graphicRoot.removeGraphics(remove)
    }

    /**
     * Remove a single graphic from the component
     * @param gfc graphic to remove
     */
    fun removeGraphic(gfc: Graphic<Graphics2D?>?) {
        graphicRoot.removeGraphic(gfc)
    }

    /**
     * Remove all graphics from the component.
     */
    fun clearGraphics() {
        graphicRoot.clearGraphics()
    }

    //endregion
    //region CANVAS TRANSFORM

    @Nullable
    override fun getTransform() = transform

    @Nullable
    override fun getInverseTransform() = inverseTransform

    /**
     * Set the transform used for drawing objects on the canvas.
     * @param at the transform (null for identity transform)
     * @throws IllegalArgumentException if the transform is non-null but not invertible
     */
    override fun setTransform(@Nullable at: AffineTransform?) {
        checkArgument(at == null || at.determinant != 0.0)
        val old = transform
        if (old !== at) {
            transform = at
            inverseTransform = try {
                at?.createInverse()
            } catch (ex: NoninvertibleTransformException) {
                throw IllegalStateException("Already checked that the argument is invertible...", ex)
            }
            firePropertyChange(P_TRANSFORM, old, at)
            repaint()
        }
    }

    /**
     * Reset transform to the default.
     */
    fun resetTransform() {
        setTransform(null)
    }
    //endregion
    //region ZOOM OPERATIONS
    /**
     * Set transform to include all components in the graphic tree. Does nothing
     * if there are no graphics. Animates zoom operation.
     */
    open fun zoomToAll() {
        zoomToAll(Insets(0, 0, 0, 0), true)
    }
    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The insets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     *
     * @param outsets additional space to leave around the graphics
     * @param animate if true, zoom operation will animate
     */
    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The insets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     * Animates zoom operation.
     *
     * @param outsets additional space to leave around the graphics
     */
    @JvmOverloads
    fun zoomToAll(outsets: Insets?, animate: Boolean = true) {
        val bounds = getGraphicRoot().boundingBox(canvas())
        if (bounds != null && animate) {
            animatedZoomWithOutsets(bounds, outsets)
        } else bounds?.let { instantZoomWithOutsets(it, outsets) }
    }

    /**
     * Zooms in in to the graphics canvas (animated).
     */
    open fun zoomIn() {
        PanAndZoomHandler.Companion.zoomIn(this, true)
    }

    /**
     * Zooms in in to the graphics canvas.
     * @param animate if true, zoom operation will animate
     */
    fun zoomIn(animate: Boolean) {
        PanAndZoomHandler.Companion.zoomIn(this, animate)
    }

    /**
     * Zooms out of the graphics canvas (animated).
     */
    open fun zoomOut() {
        PanAndZoomHandler.Companion.zoomOut(this, true)
    }

    /**
     * Zooms out of the graphics canvas.
     * @param animate if true, zoom operation will animate
     */
    fun zoomOut(animate: Boolean) {
        PanAndZoomHandler.Companion.zoomOut(this, animate)
    }

    /**
     * Set transform to include all selected components. Does nothing if nothing
     * is selected. Zoom is animated.
     */
    open fun zoomToSelected() {
        zoomToSelected(Insets(0, 0, 0, 0), true)
    }
    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The outsets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     * Zoom is animated.
     *
     * @param locCoordOutsets additional space to leave around the graphics (in local coordinate space)
     * @param animate if true, zoom operation will animate
     */
    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The outsets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     * Zoom is animated.
     *
     * @param locCoordOutsets additional space to leave around the graphics (in local coordinate space)
     */
    @JvmOverloads
    fun zoomToSelected(locCoordOutsets: Insets?, animate: Boolean = true) {
        val bounds = GraphicUtils.boundingBox(getSelectionModel().selection, canvas())
        if (bounds != null && animate) {
            animatedZoomWithOutsets(bounds, locCoordOutsets)
        } else bounds?.let { instantZoomWithOutsets(it, locCoordOutsets) }
    }

    /**
     * Utility method to animate the zoom operation to the target local bounds.
     * @param bounds local bounds
     * @param locCoordOutsets outsets beyond the local bounds
     */
    private fun animatedZoomWithOutsets(bounds: Rectangle2D?, locCoordOutsets: Insets?) {
        val minX = bounds.getMinX() - locCoordOutsets.left
        val maxX = Math.max(minX, bounds.getMaxX() + locCoordOutsets.right)
        val minY = bounds.getMinY() - locCoordOutsets.top
        val maxY = Math.max(minY, bounds.getMaxY() + locCoordOutsets.bottom)
        PanAndZoomHandler.Companion.zoomCoordBoxAnimated(this,
                Point2D.Double(minX, minY),
                Point2D.Double(maxX, maxY))
    }

    /**
     * Utility method to instantly change the zoom to the target local bounds.
     * @param bounds local bounds
     * @param locCoordOutsets outsets beyond the local bounds
     */
    private fun instantZoomWithOutsets(bounds: Rectangle2D?, locCoordOutsets: Insets?) {
        val minX = bounds.getMinX() - locCoordOutsets.left
        val maxX = Math.max(minX, bounds.getMaxX() + locCoordOutsets.right)
        val minY = bounds.getMinY() - locCoordOutsets.top
        val maxY = Math.max(minY, bounds.getMaxY() + locCoordOutsets.bottom)
        PanAndZoomHandler.Companion.setDesiredLocalBounds(this,
                Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY))
    }
    //endregion
    //region GRAPHICS QUERIES
    /**
     * Return the tooltip associated with the mouse event's point.
     * This will look for the topmost [Graphic] beneath the mouse and return that.
     * @param event the event with the point for the tooltip
     * @return tooltip for the point
     */
    override fun getToolTipText(event: MouseEvent?): String? {
        val ct = graphicRoot.getTooltip(toGraphicCoordinate(event.getPoint()), null)
        return ct ?: if ("" == super.getToolTipText()) null else super.getToolTipText()
    }

    /**
     * Convert window point location to graphic root location
     * @param winLoc window location
     * @return graphic coordinate system location
     */
    override fun toGraphicCoordinate(winLoc: Point2D) = inverseTransform?.transform(winLoc, null) ?: winLoc

    /** Convert mouse event to local coordinate space */
    fun toGraphicCoordinateSpace(winEvent: MouseEvent) = GMouseEvent(winEvent, toGraphicCoordinate(winEvent.point), null)

    /** First graphic at the given window location */
    fun graphicAt(winLoc: Point) = graphicRoot.graphicAt(toGraphicCoordinate(winLoc), canvas())
    /** First functional graphic at the given window location */
    fun functionalGraphicAt(winLoc: Point) = graphicRoot.mouseGraphicAt(toGraphicCoordinate(winLoc), canvas())
    /** First selectable graphic at the given window location */
    fun selectableGraphicAt(winLoc: Point) = graphicRoot.selectableGraphicAt(toGraphicCoordinate(winLoc), canvas())

    //endregion

    //region PAINT

    /**
     * Get instance of canvas to use for style location checking.
     * @return canvas
     */
    fun canvas(): Graphics2D? {
        // TODO
        return null
    }

    /**
     * Paints the graphics to the specified canvas.
     * @param g graphics object
     */
    override fun paintChildren(g: Graphics?) {
        renderTo(g as Graphics2D?)
        super.paintChildren(g)
    }

    /**
     * Renders all shapes in root to specified graphics object.
     * @param canvas graphics canvas to render to
     */
    fun renderTo(canvas: Graphics2D?) {
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                if (antialias) RenderingHints.VALUE_ANTIALIAS_ON else RenderingHints.VALUE_ANTIALIAS_OFF)
        if (isOpaque) {
            canvas.setColor(background)
            canvas.fillRect(0, 0, width, height)
        }
        renderUnderlay(canvas)
        if (transform == null) {
            graphicRoot.renderTo(canvas)
        } else {
            val priorTransform = canvas.getTransform()
            canvas.transform(transform)
            graphicRoot.renderTo(canvas)
            canvas.setTransform(priorTransform)
        }
        renderOverlay(canvas)
    }

    /**
     * Hook to render underlay elements. Called after the background is drawn,
     * but before anything else.
     * @param canvas the canvas to render to
     */
    protected fun renderUnderlay(canvas: Graphics2D?) {
        underlays.forEach(Consumer { p: CanvasPainter<*>? -> p.paint(this, canvas) })
    }

    /**
     * Hook to render overlay elements. Called after everything else is drawn.
     * @param canvas the canvas to render to
     */
    protected fun renderOverlay(canvas: Graphics2D?) {
        overlays.forEach(Consumer { p: CanvasPainter<*>? -> p.paint(this, canvas) })
    } //endregion

    companion object {
        val P_TRANSFORM: String? = "transform"
    }

    /**
     * Construction of a generic graphics view component.
     */
    init {
        graphicRoot = JGraphicRoot(this)
        selector.setSelectionEnabled(false)
        addMouseListener(selector)
        addMouseMotionListener(selector)
        overlays.add(selector)
        isDoubleBuffered = true
        background = Color.WHITE
        isOpaque = true
        preferredSize = Dimension(300, 200)
        // this line enables tooltips
        toolTipText = ""
    }
}