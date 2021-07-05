package com.googlecode.blaisemath.graphics.impl

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Queues
import com.google.common.collect.Sets
import com.googlecode.blaisemath.annotation.InvokedFromThread
import com.googlecode.blaisemath.coordinate.CoordinateChangeEvent
import com.googlecode.blaisemath.coordinate.CoordinateListener
import com.googlecode.blaisemath.coordinate.CoordinateManager
import com.googlecode.blaisemath.graphics.*
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.primitive.AnchoredText
import com.googlecode.blaisemath.style.ObjectStyler
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.swing.MoreSwingUtilities
import junit.framework.TestCase
import java.awt.geom.Point2D
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.logging.Level
import java.util.logging.Logger
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
        crdManager: CoordinateManager<S?, Point2D.Double?>?,
        renderer: Renderer<Point2D?, G?>?,
        labelRenderer: Renderer<AnchoredText?, G?>?
) : GraphicComposite<G?>() {
    /** Graphic objects for individual points  */
    protected val points: MutableMap<S?, DelegatingPrimitiveGraphic<S?, Point2D?, G?>?>? = Maps.newHashMap()

    /** Whether points can be dragged  */
    protected var dragEnabled = false

    /** Manages locations of points  */
    protected var manager: CoordinateManager<S?, Point2D.Double?>? = null

    /** Responds to coordinate update events. Also used as a lock object for updates.  */
    private val coordListener: CoordinateListener<*, *>?

    /** Flag that indicates points are being updated, and no notification events should be sent.  */
    protected var updating = false

    /** Queue of updates to be processed  */
    private val updateQueue: Queue<CoordinateChangeEvent<*, *>?>? = Queues.newConcurrentLinkedQueue()

    /** Selects styles for graphics  */
    protected var styler: ObjectStyler<S?>? = ObjectStyler.create()

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
            renderer: Renderer<Point2D?, G?>? = null,
            labelRenderer: Renderer<AnchoredText?, G?>? = null
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
    fun getCoordinateManager(): CoordinateManager<S?, Point2D.Double?>? {
        return manager
    }

    /**
     * Set manager responsible for tracking point locations
     * @param mgr manager
     */
    fun setCoordinateManager(mgr: CoordinateManager<S?, Point2D.Double?>?) {
        if (manager != Preconditions.checkNotNull(mgr)) {
            if (manager != null) {
                manager.removeCoordinateListener(coordListener)
            }
            manager = null
            clearPendingUpdates()
            val oldPoints = points.keys
            val toRemove: MutableSet<S?>? = Sets.newHashSet(oldPoints)
            // lock to ensure that no changes are made until after the listener has been setup
            synchronized(mgr) {
                manager = mgr
                val activePoints = manager.getActiveLocationCopy()
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
        if (this.styler != Preconditions.checkNotNull(styler)) {
            this.styler = styler
            fireGraphicChanged()
        }
    }

    fun getRenderer(): Renderer<Point2D?, G?>? {
        return renderer
    }

    fun setRenderer(renderer: Renderer<Point2D?, G?>?) {
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

    fun getLabelRenderer(): Renderer<AnchoredText?, G?>? {
        return textRenderer
    }

    fun setLabelRenderer(renderer: Renderer<AnchoredText?, G?>?) {
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
        return manager.getActive()
    }
    //endregion
    //region MUTATORS
    /**
     * Adds objects to the graphic
     * @param obj objects to put
     */
    fun addObjects(obj: MutableMap<S?, Point2D.Double?>?) {
        manager.putAll(obj)
    }

    //endregion
    //region LOOKUPS
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
            updatePointGraphics(evt.getAdded(), evt.getRemoved(), true)
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
    private fun updatePointGraphics(added: MutableMap<S?, Point2D.Double?>?, removed: MutableSet<S?>?, notify: Boolean) {
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
            val prim = dpg.getPrimitive()
            manager.put(dpg.getSourceObject(), if (prim is Point2D.Double) prim as Point2D.Double? else Point2D.Double(prim.getX(), prim.getY()))
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