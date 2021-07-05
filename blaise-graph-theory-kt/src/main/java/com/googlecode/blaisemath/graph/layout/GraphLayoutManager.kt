package com.googlecode.blaisemath.graph.layout

import com.google.common.collect.Sets
import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.coordinate.CoordinateManager
import com.googlecode.blaisemath.graph.GraphUtils
import com.googlecode.blaisemath.graph.IterativeGraphLayout
import com.googlecode.blaisemath.graph.StaticGraphLayout
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.layout.CircleLayout.CircleLayoutParameters
import com.googlecode.blaisemath.graph.layout.SpringLayoutPerformanceTest
import com.googlecode.blaisemath.graph.metrics.AdditiveSubsetMetricTest
import com.googlecode.blaisemath.graph.metrics.BetweenCentralityTest
import com.googlecode.blaisemath.graph.metrics.ClosenessCentralityTest
import com.googlecode.blaisemath.graph.metrics.CooperationMetric
import com.googlecode.blaisemath.graph.metrics.EigenCentralityTest
import com.googlecode.blaisemath.graph.metrics.GraphCentralityTest
import com.googlecode.blaisemath.graph.metrics.SubsetMetricsTest
import com.googlecode.blaisemath.graph.test.DynamicGraphTestFrame
import com.googlecode.blaisemath.graph.test.GraphTestFrame
import com.googlecode.blaisemath.graph.test.MyTestGraph
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
import org.junit.BeforeClass
import java.awt.geom.Point2D
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.logging.Level
import java.util.logging.Logger

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
 * Manages graph layout within a background thread, in situations where the graph or node locations might be
 * simultaneously modified from other threads. Executes a graph layout algorithm in a background thread. Uses an
 * [IterativeGraphLayout] algorithm, whose results are supplied to the [CoordinateManager].
 *
 *
 * This class is not thread-safe, so all of its methods should be accessed from a single thread. However, coordinate
 * locations can be accessed or updated in the `CoordinateManager` from any thread.
 *
 * @param <N> type of node in graph
 * @author Elisha Peterson
</N> */
class GraphLayoutManager<N> {
    //endregion
    /** Graph  */
    private var graph: Graph<N?>? = null

    /** Locates nodes in the graph  */
    private val coordinateManager: CoordinateManager<N?, Point2D.Double?>? = CoordinateManager.create(NODE_CACHE_SIZE)

    /** The initial layout scheme  */
    private val initialLayout: StaticGraphLayout<CircleLayoutParameters?>? = CircleLayout.Companion.getInstance()

    /** The initial layout parameters  */
    private val initialLayoutParameters: CircleLayoutParameters? = CircleLayoutParameters(50)

    /** The layout scheme for adding nodes  */
    private val addingLayout: StaticGraphLayout<CircleLayoutParameters?>? = PositionalAddingLayout()

    /** The initial layout parameters  */
    private val addingLayoutParameters: CircleLayoutParameters? = CircleLayoutParameters(100)

    /** Manager for iterative graph layout algorithm  */
    private val iterativeLayoutManager: IterativeGraphLayoutManager? = IterativeGraphLayoutManager()

    /** Service that manages iterative graph layout on background thread.  */
    private var iterativeLayoutService: IterativeGraphLayoutService? = null

    /** Handles property change events  */
    private val pcs: PropertyChangeSupport? = PropertyChangeSupport(this)
    //endregion
    //region PROPERTIES
    /**
     * Object providing node locations.
     * @return point manager
     */
    fun getCoordinateManager(): CoordinateManager<N?, Point2D.Double?>? {
        return coordinateManager
    }

    /**
     * Return copy of the locations of objects in the graph.
     * @return locations, as a copy of the map provided in the point manager
     */
    fun getNodeLocationCopy(): MutableMap<N?, Point2D.Double?>? {
        return coordinateManager.activeLocationCopy
    }

    /**
     * Return the graph.
     * @return the layout manager's graph
     */
    fun getGraph(): Graph<N?>? {
        return graph
    }

    /**
     * Change the graph. Uses the default initial position layout to position nodes if the current graph was null,
     * otherwise uses the adding layout for any nodes that do not have current positions.
     *
     * @param g the graph
     */
    fun setGraph(g: Graph<N?>?) {
        Objects.requireNonNull(g)
        val old = graph
        if (old !== g) {
            val active = isLayoutTaskActive()
            setLayoutTaskActive(false)
            graph = g
            iterativeLayoutManager.setGraph(g)
            initializeNodeLocations(old, g)
            setLayoutTaskActive(active)
            pcs.firePropertyChange(P_GRAPH, old, g)
        }
    }

    /**
     * Get layout algorithm.
     * @return current iterative layout algorithm
     */
    fun getLayoutAlgorithm(): IterativeGraphLayout<*, *>? {
        return iterativeLayoutManager.getLayout()
    }

    /**
     * Set a new iterative graph layout. Cancels any ongoing layout, and does not start a new one.
     * @param layout the layout algorithm
     */
    fun setLayoutAlgorithm(layout: IterativeGraphLayout<*, *>?) {
        val old: Any? = iterativeLayoutManager.getLayout()
        if (layout !== old) {
            setLayoutTaskActive(false)
            iterativeLayoutService = IterativeGraphLayoutService(iterativeLayoutManager)
            iterativeLayoutManager.setLayout(layout)
            pcs.firePropertyChange(P_LAYOUT, old, layout)
        }
    }

    /**
     * Get parameters associated with the current layout.
     * @return parameters
     */
    fun getLayoutParameters(): Any? {
        return iterativeLayoutManager.getParameters()
    }

    /**
     * Set parameters for the current layout.
     * @param params new parameters
     */
    fun setLayoutParameters(params: Any?) {
        iterativeLayoutManager.setParameters(params)
    }

    /**
     * Return whether layout task is currently active.
     * @return true if an iterative layout is active
     */
    fun isLayoutTaskActive(): Boolean {
        return iterativeLayoutService != null &&
                iterativeLayoutService.isLayoutActive()
    }

    /**
     * Change the status of the layout task, either starting or stopping it.
     * @param on true to animate, false to stop animating
     */
    fun setLayoutTaskActive(on: Boolean) {
        val old = isLayoutTaskActive()
        if (on == old) {
            return
        } else if (on) {
            stopLayoutTaskNow()
            iterativeLayoutService = IterativeGraphLayoutService(iterativeLayoutManager)
            iterativeLayoutService.startAsync()
        } else {
            stopLayoutTaskNow()
        }
        pcs.firePropertyChange(P_LAYOUT_ACTIVE, !on, on)
    }
    //endregion
    //region LAYOUT OPERATIONS
    /**
     * When the graph is changes, call this method to set up initial positions for nodes in the graph. Will attempt to
     * use cached nodes if possible. Otherwise, it may execute the "initial layout" algorithm or the "adding layout"
     * algorithm.
     *
     * TODO - may take some time to execute if the graph is large, consider improving this class's design by running the
     * initial layout in a background thread; also, locking on the CM may be problematic if the layout takes a long time
     */
    private fun initializeNodeLocations(old: Graph<N?>?, g: Graph<N?>?) {
        synchronized(coordinateManager) {
            val oldNodes: MutableSet<N?>? = Sets.difference(coordinateManager.active, g.nodes())
            coordinateManager.deactivate(oldNodes)
            // defer to existing locations if possible
            if (coordinateManager.locatesAll(g.nodes())) {
                coordinateManager.reactivate(g.nodes())
            } else {
                // lays out new graph entirely
                val newLoc: MutableMap<N?, Point2D.Double?>?
                newLoc = if (old == null) {
                    initialLayout.layout(g, null, initialLayoutParameters)
                } else {
                    val curLoc: MutableMap<N?, Point2D.Double?> = coordinateManager.activeLocationCopy
                    addingLayout.layout(g, curLoc, addingLayoutParameters)
                }
                // remove objects that are already in coordinate manager
                newLoc.keys.removeAll(coordinateManager.active)
                newLoc.keys.removeAll(coordinateManager.inactive)
                coordinateManager.reactivate(g.nodes())
                coordinateManager.putAll(newLoc)
            }

            // log size mismatches to help with debugging
            val sz = coordinateManager.active.size
            val check = sz == g.nodes().size
            if (!check) {
                LOG.log(Level.WARNING, "Object sizes don''t match: {0} locations, but {1} nodes!", arrayOf<Any?>(sz, g.nodes().size))
            }
        }
    }

    /**
     * Update the locations of the specified nodes with the specified values. If an iterative layout is currently active,
     * locations are updated at the layout. Otherwise, locations are updated by the point manager. Nodes that are in the
     * graph but whose positions are not in the provided map will not be moved.
     *
     * @param locations new locations for objects
     */
    fun requestLocations(locations: MutableMap<N?, Point2D.Double?>?) {
        Objects.requireNonNull(locations)
        if (isLayoutTaskActive()) {
            iterativeLayoutManager.requestPositions(locations, false)
        } else {
            coordinateManager.putAll(locations)
        }
    }

    /**
     * Update positions of current using specified layout algorithm. This method will replace the coordinates of objects
     * in the graph.
     *
     * @param layout static layout algorithm
     * @param ic initial conditions for static layout algorithm
     * @param parameters layout parameters
     * @param <P> parameters type
    </P> */
    fun <P> applyLayout(layout: StaticGraphLayout<P?>?, ic: MutableMap<N?, Point2D.Double?>?, parameters: P?) {
        requestLocations(layout.layout(graph, ic, parameters))
    }

    /**
     * Manually iterate layout, if an iterative layout has been provided.
     */
    fun iterateLayout() {
        iterativeLayoutService.runOneIteration()
    }

    /**
     * Stop the layout timer.
     */
    private fun stopLayoutTaskNow() {
        if (iterativeLayoutService != null) {
            iterativeLayoutService.stopAsync()
            iterativeLayoutManager.reset()
            try {
                iterativeLayoutService.awaitTerminated(100, TimeUnit.MILLISECONDS)
            } catch (ex: TimeoutException) {
                LOG.log(Level.WARNING, "Layout service was not terminated", ex)
            }
        }
    }

    //endregion
    //region EVENTS
    fun removePropertyChangeListener(propertyName: String?, listener: PropertyChangeListener?) {
        pcs.removePropertyChangeListener(propertyName, listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener?) {
        pcs.removePropertyChangeListener(listener)
    }

    fun addPropertyChangeListener(propertyName: String?, listener: PropertyChangeListener?) {
        pcs.addPropertyChangeListener(propertyName, listener)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener?) {
        pcs.addPropertyChangeListener(listener)
    } //endregion

    companion object {
        private val LOG = Logger.getLogger(GraphLayoutManager::class.java.name)

        //region CONSTANTS
        const val NODE_CACHE_SIZE = 20000

        /** Graph property  */
        val P_GRAPH: String? = "graph"

        /** Layout property  */
        val P_LAYOUT: String? = "layoutAlgorithm"

        /** Whether layout is active  */
        val P_LAYOUT_ACTIVE: String? = "layoutTaskActive"

        /**
         * Initializes with a given graph.
         * @param <N> graph node type
         * @param graph graph for layout
         * @return manager instance
        </N> */
        fun <N> create(graph: Graph<N?>?): GraphLayoutManager<N?>? {
            val res = GraphLayoutManager<N?>()
            res.setGraph(graph)
            return res
        }
    }
    //region CONSTRUCTOR and FACTORY
    /** Initializes with an empty graph  */
    init {
        iterativeLayoutManager.setCoordinateManager(coordinateManager)
        setGraph(GraphUtils.emptyGraph(false))
    }
}