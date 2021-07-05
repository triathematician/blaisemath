package com.googlecode.blaisemath.graph.view

import com.google.common.graph.EndpointPair
import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.app.PresetMenuInitializer
import com.googlecode.blaisemath.app.PropertyActionPanel
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager
import com.googlecode.blaisemath.graph.test.DynamicGraphTestFrame
import com.googlecode.blaisemath.graph.test.GraphTestFrame
import com.googlecode.blaisemath.graph.view.VisualGraph
import com.googlecode.blaisemath.graphics.DelegatingPrimitiveGraphic
import com.googlecode.blaisemath.graphics.Graphic
import com.googlecode.blaisemath.graphics.impl.DelegatingNodeLinkGraphic
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.graphics.swing.JGraphics
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler
import com.googlecode.blaisemath.style.ObjectStyler
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.event.HierarchyEvent
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors

/*
* #%L
* BlaiseGraphTheory
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
 * Provides a view of a graph, using a [GraphLayoutManager] for positions/layout and a [VisualGraph] for appearance.
 * The layout manager supports executing long-running layout algorithms in a background thread, and the visual graph
 * shares a [CoordinateManager] that is used for updating locations from the layout manager. The coordinate manager is thread-safe.
 *
 * @author Elisha Peterson
 */
open class GraphComponent @JvmOverloads constructor(gm: GraphLayoutManager<*>? = GraphLayoutManager<Any?>()) : JGraphicComponent() {
    /** Manages the visual elements of the underlying graph  */
    protected val adapter: VisualGraph<Graphics2D?>?

    /**
     * Construct with specified graph.
     * @param graph the graph to initialize with
     */
    constructor(graph: Graph<*>?) : this(GraphLayoutManager.create<Any?>(graph)) {}
    //region PROPERTIES
    /**
     * Return the adapter that contains the graph manager and the graph, responsible for handling the visual appearance.
     * @return the adapter
     */
    fun getAdapter(): VisualGraph<*>? {
        return adapter
    }

    //endregion
    //region DELEGATING PROPERTIES
    fun getEdgeStyler(): ObjectStyler<EndpointPair<Any?>?>? {
        return adapter.getEdgeStyler()
    }

    fun setEdgeStyler(edgeStyler: ObjectStyler<EndpointPair<Any?>?>?) {
        adapter.setEdgeStyler(edgeStyler)
    }

    fun getNodeStyler(): ObjectStyler<Any?>? {
        return adapter.getNodeStyler()
    }

    fun setNodeStyler(nodeStyler: ObjectStyler<Any?>?) {
        adapter.setNodeStyler(nodeStyler)
    }

    /**
     * Return the graph manager underlying the component, responsible for handling the graph and node locations.
     * @return the manager
     */
    fun getLayoutManager(): GraphLayoutManager<*>? {
        return adapter.getLayoutManager()
    }

    fun setLayoutManager(gm: GraphLayoutManager<*>?) {
        adapter.setLayoutManager(gm)
    }

    fun getGraph(): Graph<*>? {
        return adapter.getGraph()
    }

    open fun setGraph(graph: Graph<*>?) {
        adapter.setGraph(graph)
    }

    fun isLayoutTaskActive(): Boolean {
        return getLayoutManager().isLayoutTaskActive()
    }

    fun setLayoutTaskActive(`val`: Boolean) {
        getLayoutManager().setLayoutTaskActive(`val`)
    }

    fun getNodeLabelFilter(): Predicate<Any?>? {
        return getNodeStyler().getLabelFilter()
    }

    fun setNodeLabelFilter(nodeLabelFilter: Predicate<Any?>?) {
        val old: Any? = getNodeStyler().getLabelFilter()
        if (old !== nodeLabelFilter) {
            getNodeStyler().setLabelFilter(nodeLabelFilter)
            repaint()
        }
    }

    fun getNodeLabelDelegate(): Function<*, String?>? {
        return adapter.getNodeStyler().labelDelegate
    }

    fun setNodeLabelDelegate(labeler: Function<Any?, String?>?) {
        val old: Any? = getNodeStyler().getLabelDelegate()
        if (old !== labeler) {
            getNodeStyler().setLabelDelegate(labeler)
            repaint()
        }
    }

    fun getSelectedNodes(): MutableSet<*>? {
        return selectionModel.selection.stream()
                .filter { s: Graphic<Graphics2D?>? -> s is DelegatingPrimitiveGraphic<*, *, *> }
                .map { s: Graphic<Graphics2D?>? -> (s as DelegatingPrimitiveGraphic<*, *, *>?).getSourceObject() }
                .collect(Collectors.toSet())
    }

    fun setSelectedNodes(nodes: MutableCollection<*>?) {
        val newSelection = adapter.getViewGraph().pointGraphic.graphics.stream()
                .filter { g: Any? -> nodes.contains((g as DelegatingPrimitiveGraphic<*, *, *>?).getSourceObject()) }
                .collect(Collectors.toSet<Any?>()) as MutableSet<*>
        selector.selectionModel.setSelection(newSelection)
    }
    //endregion
    /**
     * Adds context menu element to specified object.
     * @param key either "graph", "node", or "edge"
     * @param init used to initialize the context menu
     */
    fun addContextMenuInitializer(key: String?, init: ContextMenuInitializer<*>?) {
        val win = adapter.getViewGraph()
        if (MENU_KEY_GRAPH.equals(key, ignoreCase = true)) {
            graphicRoot.addContextMenuInitializer(init)
        } else if (MENU_KEY_NODE.equals(key, ignoreCase = true)) {
            win.pointGraphic.addContextMenuInitializer(init)
        } else if (MENU_KEY_EDGE.equals(key, ignoreCase = true)) {
            win.edgeGraphic.addContextMenuInitializer(init)
        } else {
            LOG.log(Level.WARNING, "Unsupported context menu key: {0}", key)
        }
    }

    /**
     * Removes context menu element from specified object
     * @param key either "graph", "node", or "edge"
     * @param init used to initialize the context menu
     */
    fun removeContextMenuInitializer(key: String?, init: ContextMenuInitializer<*>?) {
        val win = adapter.getViewGraph()
        if (MENU_KEY_GRAPH == key) {
            graphicRoot.removeContextMenuInitializer(init)
        } else if (MENU_KEY_NODE == key) {
            win.pointGraphic.removeContextMenuInitializer(init)
        } else if (MENU_KEY_EDGE == key) {
            win.edgeGraphic.removeContextMenuInitializer(init)
        } else {
            LOG.log(Level.WARNING, "Unsupported context menu key: {0}", key)
        }
    }

    companion object {
        private val LOG = Logger.getLogger(GraphComponent::class.java.name)
        val MENU_KEY_GRAPH: String? = "graph"
        val MENU_KEY_EDGE: String? = "edge"
        val MENU_KEY_NODE: String? = "node"

        /** This mechanism is used to create the initial view graph for swing components, by setting up appropriate renderers.  */
        private val SWING_GRAPH_SUPPLIER: Supplier<DelegatingNodeLinkGraphic<Any?, EndpointPair<Any?>?, Graphics2D?>?>? = Supplier {
            val res = JGraphics.nodeLink<Any?>()
            res.nodeStyler.setStyle(VisualGraph.Companion.DEFAULT_NODE_STYLE)
            res
        }
    }
    /**
     * Construct with specified graph manager (contains graph and positions).
     * @param gm graph manager to initialize with
     */
    /**
     * Construct with an empty graph.
     */
    init {
        adapter = VisualGraph(gm, SWING_GRAPH_SUPPLIER)
        addGraphic(adapter.getViewGraph())
        preferredSize = Dimension(400, 400)
        isSelectionEnabled = true
        PanAndZoomHandler.install(this)

        // turn off animation if component hierarchy changes
        addHierarchyListener { e: HierarchyEvent? ->
            if (e.getChangeFlags() == HierarchyEvent.PARENT_CHANGED.toLong()) {
                setLayoutTaskActive(false)
            }
        }
    }
}