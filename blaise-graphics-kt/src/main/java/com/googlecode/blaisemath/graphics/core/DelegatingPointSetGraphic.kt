package com.googlecode.blaisemath.graphics.core
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
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Queues
import com.google.common.collect.Sets
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import com.googlecode.blaisemath.annotation.InvokedFromThread
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.coordinate.CoordinateChangeEvent
import com.googlecode.blaisemath.coordinate.CoordinateListener
import com.googlecode.blaisemath.coordinate.CoordinateManager
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
import com.googlecode.blaisemath.style.ObjectStyler
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
import com.googlecode.blaisemath.util.swing.MoreSwingUtilities
import junit.framework.TestCase
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.geom.Point2D
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.JPopupMenu
import javax.swing.SwingUtilities

/**
 * Manages a collection of points that are maintained as separate [Graphic]s,
 * and therefore fully customizable. Points and their locations are handled by a [CoordinateManager],
 * which allows their locations to be safely modified from other threads.
 *
 * @param <S> the type of object being displayed
 * @param <G> type of canvas to render to
 *
 * @see BasicPointSetGraphic
 *
 *
 * @author Elisha Peterson
</G></S> */
class DelegatingPointSetGraphic<S, G>(
        crdManager: CoordinateManager<S?, Point2D?>?,
        @Nullable renderer: Renderer<Point2D?, G?>?,
        @Nullable labelRenderer: Renderer<AnchoredText?, G?>?
) : GraphicComposite<G?>() {
    /** Graphic objects for individual points  */
    protected val points: MutableMap<S?, DelegatingPrimitiveGraphic<S?, Point2D?, G?>?>? = Maps.newHashMap()

    /** Whether points can be dragged  */
    protected var dragEnabled = false

    /** Manages locations of points  */
    protected var manager: CoordinateManager<S?, Point2D?>? = null

    /** Responds to coordinate update events. Also used as a lock object for updates.  */
    private val coordListener: CoordinateListener<*, *>?

    /** Flag that indicates points are being updated, and no notification events should be sent.  */
    protected var updating = false

    /** Queue of updates to be processed  */
    private val updateQueue: Queue<CoordinateChangeEvent<*, *>?>? = Queues.newConcurrentLinkedQueue()

    /** Selects styles for graphics  */
    protected var styler: ObjectStyler<S?>? = ObjectStyler.Companion.create()

    /** Selects renderer for points  */
    protected var renderer: Renderer<Point2D?, G?>? = null

    /** Renderer for point labels  */
    protected var textRenderer: Renderer<AnchoredText?, G?>? = null
    /**
     * Construct with no points.
     * @param renderer draws points
     * @param labelRenderer draws labels
     */
    //region CONSTRUCTORS
    /**
     * Construct with no points.
     */
    @JvmOverloads
    constructor(
            @Nullable renderer: Renderer<Point2D?, G?>? = null,
            @Nullable labelRenderer: Renderer<AnchoredText?, G?>? = null
    ) : this(CoordinateManager.create(DEFAULT_NODE_CACHE_SIZE), renderer, labelRenderer) {
    }
    //endregion
    //region PROPERTIES
    /**
     * Returns true if individual points can be selected.
     * @return true if points can be selected
     */
    fun isPointSelectionEnabled(): Boolean {
        return styleHints.contains(POINT_SELECTION_ENABLED)
    }

    fun setPointSelectionEnabled(`val`: Boolean) {
        if (isPointSelectionEnabled() != `val`) {
            setStyleHint(POINT_SELECTION_ENABLED, `val`)
            points.values.forEach(Consumer { p: DelegatingPrimitiveGraphic<S?, Point2D?, G?>? -> p.setSelectionEnabled(`val`) })
        }
    }

    /**
     * Manager responsible for tracking point locations
     * @return manager
     */
    fun getCoordinateManager(): CoordinateManager<S?, Point2D?>? {
        return manager
    }

    /**
     * Set manager responsible for tracking point locations
     * @param mgr manager
     */
    fun setCoordinateManager(mgr: CoordinateManager<S?, Point2D?>?) {
        if (manager !== checkNotNull(mgr)) {
            if (manager != null) {
                manager.removeCoordinateListener(coordListener)
            }
            manager = null
            clearPendingUpdates()
            val oldPoints = points.keys
            val toRemove: MutableSet<S?> = Sets.newHashSet(oldPoints)
            // lock to ensure that no changes are made until after the listener has been setup
            synchronized(mgr) {
                manager = mgr
                val activePoints: MutableMap<S?, Point2D?> = manager.activeLocationCopy
                toRemove.removeAll(activePoints.keys)
                updatePointGraphics(activePoints, toRemove, false)
                manager.addCoordinateListener(coordListener)
            }
            super.graphicChanged(this)
        }
    }

    /**
     * Returns object used to style points
     * @return styler object styler
     */
    fun getStyler(): ObjectStyler<S?>? {
        return styler
    }

    /**
     * Sets object used to style points
     * @param styler object styler
     */
    fun setStyler(styler: ObjectStyler<S?>?) {
        if (this.styler != checkNotNull(styler)) {
            this.styler = styler
            fireGraphicChanged()
        }
    }

    @Nullable
    fun getRenderer(): Renderer<Point2D?, G?>? {
        return renderer
    }

    fun setRenderer(@Nullable renderer: Renderer<Point2D?, G?>?) {
        if (this.renderer !== renderer) {
            val old: Any? = this.renderer
            this.renderer = renderer
            updating = true
            for (dpg in points.values) {
                dpg.setRenderer(renderer)
            }
            updating = false
            fireGraphicChanged()
            pcs.firePropertyChange(PrimitiveGraphicSupport.Companion.P_RENDERER, old, renderer)
        }
    }

    @Nullable
    fun getLabelRenderer(): Renderer<AnchoredText?, G?>? {
        return textRenderer
    }

    fun setLabelRenderer(@Nullable renderer: Renderer<AnchoredText?, G?>?) {
        if (textRenderer !== renderer) {
            val old: Any? = this.renderer
            textRenderer = renderer
            fireGraphicChanged()
            pcs.firePropertyChange(LabeledPointGraphic.Companion.P_LABEL_RENDERER, old, renderer)
        }
    }

    fun isDragEnabled(): Boolean {
        return dragEnabled
    }

    fun setDragEnabled(`val`: Boolean) {
        if (dragEnabled != `val`) {
            dragEnabled = `val`
            for (dpg in points.values) {
                dpg.setDragEnabled(`val`)
            }
        }
    }

    /**
     * Return source objects.
     * @return source objects
     */
    fun getObjects(): MutableSet<S?>? {
        return manager.active
    }
    //endregion
    //region MUTATORS
    /**
     * Adds objects to the graphic
     * @param obj objects to put
     */
    fun addObjects(obj: MutableMap<S?, Point2D?>?) {
        manager.putAll(obj)
    }

    //endregion
    //region LOOKUPS
    @Nullable
    fun getPointGraphic(source: S?): DelegatingPrimitiveGraphic<S?, Point2D?, G?>? {
        return points.get(source)
    }

    override fun initContextMenu(menu: JPopupMenu?, src: Graphic<G?>?, point: Point2D?, focus: Any?, selection: MutableSet<Graphic<G?>?>?, canvas: G?) {
        val gfc: Graphic<*>? = graphicAt(point, canvas)
        super.initContextMenu(menu, this, point,
                if (gfc is DelegatingPrimitiveGraphic<*, *, *>) (gfc as DelegatingPrimitiveGraphic<*, *, *>?).getSourceObject() else focus,
                selection, canvas)
    }

    //endregion
    //region EVENTS
    @InvokedFromThread("unknown")
    private fun handleCoordinateChange(evt: CoordinateChangeEvent<*, *>?) {
        updateQueue.add(evt)
        MoreSwingUtilities.invokeOnEventDispatchThread { processNextCoordinateChangeEvent() }
    }

    @InvokedFromThread("EDT")
    private fun processNextCoordinateChangeEvent() {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.log(Level.WARNING, "processNextCoordinateChangeEvent() called from non-EDT")
        }
        val evt = updateQueue.poll()
        if (evt != null && evt.source === manager) {
            updatePointGraphics(evt.added, evt.removed, true)
        }
    }

    @InvokedFromThread("EDT")
    private fun clearPendingUpdates() {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.log(Level.WARNING, "clearPendingUpdates() called from non-EDT")
        }
        updateQueue.clear()
    }

    @InvokedFromThread("EDT")
    private fun updatePointGraphics(added: MutableMap<S?, Point2D?>?, removed: MutableSet<S?>?, notify: Boolean) {
        updating = true
        var change = false
        val addMe: MutableList<Graphic<G?>?> = Lists.newArrayList()
        if (added != null) {
            for ((src, value) in added) {
                val dpg = points.get(src)
                if (dpg == null) {
                    val lpg = LabeledPointGraphic<S?, G?>(src, value, styler)
                    lpg.setRenderer(renderer)
                    lpg.setLabelRenderer(textRenderer)
                    lpg.isDragEnabled = dragEnabled
                    lpg.isSelectionEnabled = isPointSelectionEnabled()
                    points[src] = lpg
                    addMe.add(lpg)
                } else {
                    dpg.setPrimitive(value)
                    change = true
                }
            }
        }
        val removeMe: MutableSet<DelegatingPrimitiveGraphic<S?, Point2D?, G?>?> = Sets.newHashSet()
        if (removed != null) {
            for (s in removed) {
                removeMe.add(points.get(s))
                points.remove(s)
            }
        }
        change = replaceGraphics(removeMe, addMe) || change
        updating = false
        if (change && notify) {
            fireGraphicChanged()
        }
    }

    override fun fireGraphicChanged() {
        if (!updating) {
            super.fireGraphicChanged()
        }
    }

    override fun graphicChanged(source: Graphic<G?>?) {
        if (!updating && source is LabeledPointGraphic<*, *>) {
            val dpg = source as LabeledPointGraphic<S?, G?>?
            manager.put(dpg.getSourceObject(), dpg.getPrimitive())
        }
        if (!updating) {
            super.graphicChanged(source)
        }
    } //endregion

    companion object {
        private val LOG = Logger.getLogger(DelegatingPointSetGraphic::class.java.name)
        private const val DEFAULT_NODE_CACHE_SIZE = 20000

        /** Key for flag allowing individual points to be selected  */
        val POINT_SELECTION_ENABLED: String? = "point-selection-enabled"
    }

    /**
     * Construct with given set of coordinate locations.
     * @param crdManager manages point locations
     * @param renderer used for drawing the points
     * @param labelRenderer draws labels
     */
    init {
        setRenderer(renderer)
        setLabelRenderer(labelRenderer)
        styler.setStyle(Styles.DEFAULT_POINT_STYLE)
        styler.setTipDelegate(Function { o: S? -> Objects.toString(o) })
        coordListener = CoordinateListener<*, *> { evt: CoordinateChangeEvent<*, *>? -> handleCoordinateChange(evt) }
        setCoordinateManager(crdManager)
    }
}