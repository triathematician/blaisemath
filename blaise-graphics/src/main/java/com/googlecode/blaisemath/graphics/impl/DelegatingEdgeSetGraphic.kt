package com.googlecode.blaisemath.graphics.impl

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Queues
import com.google.common.collect.Sets
import com.google.common.graph.EndpointPair
import com.googlecode.blaisemath.annotation.InvokedFromThread
import com.googlecode.blaisemath.coordinate.CoordinateChangeEvent
import com.googlecode.blaisemath.coordinate.CoordinateListener
import com.googlecode.blaisemath.coordinate.CoordinateManager
import com.googlecode.blaisemath.graphics.DelegatingPrimitiveGraphic
import com.googlecode.blaisemath.graphics.Graphic
import com.googlecode.blaisemath.graphics.GraphicComposite
import com.googlecode.blaisemath.graphics.Renderer
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.ObjectStyler
import com.googlecode.blaisemath.util.swing.MoreSwingUtilities
import junit.framework.TestCase
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
*/ /**
 * A collection of edges backed by a common set of points.
 *
 * @param <S> source object type
 * @param <E> edge type
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
</G></E></S> */
class DelegatingEdgeSetGraphic<S, E : EndpointPair<S?>?, G> @JvmOverloads constructor(mgr: CoordinateManager<S?, Point2D.Double?>? = CoordinateManager.create(DEFAULT_MAX_CACHE_SIZE), edgeRenderer: Renderer<Shape?, G?>? = null) : GraphicComposite<G?>() {
    /** The edges in the graphic.  */
    protected val edges: MutableMap<E?, DelegatingPrimitiveGraphic<E?, Shape?, G?>?>? = Maps.newHashMap()

    /** Styler for edges  */
    protected var edgeStyler: ObjectStyler<E?>? = ObjectStyler.create()

    /** Renderer for edges  */
    protected var edgeRenderer: Renderer<Shape?, G?>? = null

    /** Point manager. Maintains objects and their locations, and enables mouse dragging.  */
    protected var pointManager: CoordinateManager<S?, Point2D.Double?>? = null

    /** Listener for changes to coordinates  */
    private val coordListener: CoordinateListener<S?, Point2D.Double?>?

    /** Flag that indicates points are being updated, and no notification events should be sent.  */
    protected var updating = false

    /** Queue of updates to be processed  */
    private val updateQueue: Queue<CoordinateChangeEvent<*, *>?>? = Queues.newConcurrentLinkedQueue()

    //region EVENTS
    @InvokedFromThread("unknown")
    private fun handleCoordinateChange(evt: CoordinateChangeEvent<S?, Point2D.Double?>?) {
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
            updateEdgeGraphics(pointManager.getActiveLocationCopy(), Lists.newArrayList(), true)
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
    private fun updateEdgeGraphics(locMap: MutableMap<S?, Point2D.Double?>?, removeMe: MutableList<Graphic<G?>?>?, notify: Boolean) {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.log(Level.WARNING, "updateEdgeGraphics() called from non-EDT")
        }
        updating = true
        var change = false
        val addMe: MutableList<Graphic<G?>?> = Lists.newArrayList()
        for (edge in Sets.newLinkedHashSet(edges.keys)) {
            var dsg = edges.get(edge)
            val p1: Point2D? = locMap.get(edge.nodeU())
            val p2: Point2D? = locMap.get(edge.nodeV())
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
    fun getCoordinateManager(): CoordinateManager<S?, Point2D.Double?>? {
        return pointManager
    }

    /**
     * Set manager responsible for tracking point locations
     * @param mgr manager
     */
    fun setCoordinateManager(mgr: CoordinateManager<S?, Point2D.Double?>?) {
        if (pointManager != Preconditions.checkNotNull(mgr)) {
            if (pointManager != null) {
                pointManager.removeCoordinateListener(coordListener)
            }
            pointManager = null
            clearPendingUpdates()

            // lock to ensure that no changes are made until after the listener has been setup
            synchronized(mgr) {
                pointManager = mgr
                updateEdgeGraphics(mgr.getActiveLocationCopy(), Lists.newArrayList(), false)
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
                .collect(Collectors.toCollection { Sets.newLinkedHashSet() })
        val removeMe = edges.keys.stream().filter { e: E? -> !newEdges.contains(e) }
                .collect(Collectors.toSet())
        if (!removeMe.isEmpty() || !addMe.isEmpty()) {
            val remove: MutableList<Graphic<G?>?>? = removeMe.stream().map { key: E? -> edges.remove(key) }.collect(Collectors.toList())
            addMe.forEach(Consumer { e: E? -> edges[e] = null })
            updateEdgeGraphics(pointManager.getActiveLocationCopy(), remove, true)
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

    fun getEdgeRenderer(): Renderer<Shape?, G?>? {
        return edgeRenderer
    }

    fun setEdgeRenderer(renderer: Renderer<Shape?, G?>?) {
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
        coordListener = CoordinateListener { evt: CoordinateChangeEvent<S?, Point2D.Double?>? -> handleCoordinateChange(evt) }
        setCoordinateManager(mgr)
        setEdgeRenderer(edgeRenderer)
    }
}