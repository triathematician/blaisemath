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
import java.awt.Shape
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors
import javax.swing.JPopupMenu
import javax.swing.SwingUtilities

/**
 * A collection of edges backed by a common set of points.
 *
 * @param <S> source object type
 * @param <E> edge type
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
</G></E></S> */
class DelegatingEdgeSetGraphic<S, E : EndpointPair<S?>?, G> @JvmOverloads constructor(mgr: CoordinateManager<S?, Point2D?>? = CoordinateManager.create(DEFAULT_MAX_CACHE_SIZE), edgeRenderer: Renderer<Shape?, G?>? = null) : GraphicComposite<G?>() {
    /** The edges in the graphic.  */
    protected val edges: MutableMap<E?, DelegatingPrimitiveGraphic<E?, Shape?, G?>?>? = Maps.newHashMap()

    /** Styler for edges  */
    protected var edgeStyler: ObjectStyler<E?>? = ObjectStyler.Companion.create()

    /** Renderer for edges  */
    protected var edgeRenderer: Renderer<Shape?, G?>? = null

    /** Point manager. Maintains objects and their locations, and enables mouse dragging.  */
    protected var pointManager: CoordinateManager<S?, Point2D?>? = null

    /** Listener for changes to coordinates  */
    private val coordListener: CoordinateListener<S?, Point2D?>?

    /** Flag that indicates points are being updated, and no notification events should be sent.  */
    protected var updating = false

    /** Queue of updates to be processed  */
    private val updateQueue: Queue<CoordinateChangeEvent<*, *>?>? = Queues.newConcurrentLinkedQueue()

    //region EVENTS
    @InvokedFromThread("unknown")
    private fun handleCoordinateChange(evt: CoordinateChangeEvent<S?, Point2D?>?) {
        updateQueue.add(evt)
        MoreSwingUtilities.invokeOnEventDispatchThread { processNextCoordinateChangeEvent() }
    }

    @InvokedFromThread("EDT")
    private fun processNextCoordinateChangeEvent() {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.log(Level.WARNING, "processNextCoordinateChangeEvent() called from non-EDT")
        }
        val evt = updateQueue.poll()
        if (evt != null && evt.source === pointManager) {
            updateEdgeGraphics(pointManager.activeLocationCopy, Lists.newArrayList(), true)
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
    private fun updateEdgeGraphics(locMap: MutableMap<S?, Point2D?>?, removeMe: MutableList<Graphic<G?>?>?, notify: Boolean) {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.log(Level.WARNING, "updateEdgeGraphics() called from non-EDT")
        }
        updating = true
        var change = false
        val addMe: MutableList<Graphic<G?>?> = Lists.newArrayList()
        for (edge in Sets.newLinkedHashSet(edges.keys)) {
            var dsg = edges.get(edge)
            val p1 = locMap.get(edge.nodeU())
            val p2 = locMap.get(edge.nodeV())
            if (p1 == null || p2 == null) {
                if (dsg != null) {
                    removeMe.add(dsg)
                    edges[edge] = null
                }
            } else {
                val line = Line2D.Double(p1, p2)
                if (dsg == null) {
                    dsg = DelegatingPrimitiveGraphic(edge, line,
                            edgeStyler, edgeRenderer)
                    edges[edge] = dsg
                    dsg.setObjectStyler(edgeStyler)
                    addMe.add(dsg)
                } else {
                    dsg.setPrimitive(line)
                    change = true
                }
            }
        }
        change = replaceGraphics(removeMe, addMe) || change
        updating = false
        if (change && notify) {
            fireGraphicChanged()
        }
    }

    //endregion
    //region PROPERTIES
    fun getCoordinateManager(): CoordinateManager<S?, Point2D?>? {
        return pointManager
    }

    /**
     * Set manager responsible for tracking point locations
     * @param mgr manager
     */
    fun setCoordinateManager(mgr: CoordinateManager<S?, Point2D?>?) {
        if (pointManager !== checkNotNull(mgr)) {
            if (pointManager != null) {
                pointManager.removeCoordinateListener(coordListener)
            }
            pointManager = null
            clearPendingUpdates()

            // lock to ensure that no changes are made until after the listener has been setup
            synchronized(mgr) {
                pointManager = mgr
                updateEdgeGraphics(mgr.activeLocationCopy, Lists.newArrayList(), false)
                pointManager.addCoordinateListener(coordListener)
            }
            super.graphicChanged(this)
        }
    }

    /**
     * Return map describing graph's edges
     * @return edges
     */
    fun getEdges(): MutableSet<E?>? {
        return edges.keys
    }

    /**
     * Sets map describing graphs edges. Also updates the set of objects to be
     * the nodes within the edges. Should be called from the EDT.
     * @param newEdges new edges to put
     */
    fun setEdges(newEdges: MutableSet<out E?>?) {
        val addMe: MutableSet<E?>? = newEdges.stream().filter { e: E? -> !edges.containsKey(e) }
                .collect(Collectors.toCollection<Any?, MutableCollection<Any?>?>(Sets::newLinkedHashSet))
        val removeMe = edges.keys.stream().filter { e: E? -> !newEdges.contains(e) }
                .collect(Collectors.toSet())
        if (!removeMe.isEmpty() || !addMe.isEmpty()) {
            val remove: MutableList<Graphic<G?>?>? = removeMe.stream().map { key: E? -> edges.remove(key) }.collect(Collectors.toList())
            addMe.forEach(Consumer { e: E? -> edges[e] = null })
            updateEdgeGraphics(pointManager.activeLocationCopy, remove, true)
        }
    }

    /**
     * Returns the current style styler
     * @return style styler
     */
    fun getEdgeStyler(): ObjectStyler<E?>? {
        return edgeStyler
    }

    /**
     * Sets the current style styler. If null, will use the default style
     * provided by the parent.
     * @param styler used for custom edge styles
     */
    fun setEdgeStyler(styler: ObjectStyler<E?>?) {
        if (edgeStyler != styler) {
            edgeStyler = styler
            edges.values.stream().filter { obj: DelegatingPrimitiveGraphic<E?, Shape?, G?>? -> Objects.nonNull(obj) }.forEach { e: DelegatingPrimitiveGraphic<E?, Shape?, G?>? -> e.setObjectStyler(styler) }
            fireGraphicChanged()
        }
    }

    @Nullable
    fun getEdgeRenderer(): Renderer<Shape?, G?>? {
        return edgeRenderer
    }

    fun setEdgeRenderer(@Nullable renderer: Renderer<Shape?, G?>?) {
        if (edgeRenderer !== renderer) {
            val old: Any? = edgeRenderer
            edgeRenderer = renderer
            edges.values.forEach(Consumer { e: DelegatingPrimitiveGraphic<E?, Shape?, G?>? -> e.setRenderer(renderer) })
            fireGraphicChanged()
            pcs.firePropertyChange(P_EDGE_RENDERER, old, renderer)
        }
    }

    //endregion
    override fun initContextMenu(menu: JPopupMenu?, src: Graphic<G?>?, point: Point2D?, focus: Any?, selection: MutableSet<Graphic<G?>?>?, canvas: G?) {
        // provide additional info for context menu
        val gfc = graphicAt(point, canvas)
        super.initContextMenu(menu, this, point,
                if (gfc is DelegatingPrimitiveGraphic<*, *, *>) (gfc as DelegatingPrimitiveGraphic<*, *, *>).sourceObject else focus,
                selection, canvas)
    }

    companion object {
        private val LOG = Logger.getLogger(DelegatingEdgeSetGraphic::class.java.name)
        val P_EDGE_RENDERER: String? = "edgeRenderer"
        const val DEFAULT_MAX_CACHE_SIZE = 5000
    }
    /**
     * Initialize with given coordinate manager.
     * @param mgr manages source object loc
     * @param edgeRenderer edge renderer
     */
    /**
     * Initialize with default coordinate manager.
     */
    init {
        coordListener = CoordinateListener<S?, Point2D?> { evt: CoordinateChangeEvent<S?, Point2D?>? -> handleCoordinateChange(evt) }
        setCoordinateManager(mgr)
        setEdgeRenderer(edgeRenderer)
    }
}