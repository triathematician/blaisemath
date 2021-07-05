package com.googlecode.blaisemath.graph.layout

import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.coordinate.CoordinateChangeEvent
import com.googlecode.blaisemath.coordinate.CoordinateListener
import com.googlecode.blaisemath.coordinate.CoordinateManager
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.IterativeGraphLayout
import com.googlecode.blaisemath.graph.IterativeGraphLayoutState
import com.googlecode.blaisemath.graph.SubgraphTest
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
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
import java.util.function.Function

/*
* #%L
* BlaiseGraphTheory (v3)
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
 * Manages an iterative graph layout, including its state and parameters.
 * This class is not thread safe.
 *
 * @author Elisha Peterson
 */
class IterativeGraphLayoutManager {
    /** The graph for layouts  */
    private var graph: Graph? = null

    /** The coordinate manager to update after layout  */
    private var coordinateManager: CoordinateManager<*, *>? = null

    /** Listener for coordinate updates  */
    private val coordinateListener: CoordinateListener<*, *>?

    /** Contains the algorithm for iterating graph layouts.  */
    private var layout: IterativeGraphLayout<*, *>? = null

    /** State for the iterative layout  */
    private var state: IterativeGraphLayoutState<*>? = null

    /** Parameters for the iterative layout  */
    private var params: Any? = null

    /** Layout iteration number.  */
    private var iteration = 0

    /** # of iterations per loop  */
    private var iterationsPerLoop = DEFAULT_ITERATIONS_PER_LOOP

    //region PROPERTIES
    fun getLayout(): IterativeGraphLayout<*, *>? {
        return layout
    }

    fun setLayout(layout: IterativeGraphLayout<*, *>?) {
        if (this.layout !== layout) {
            this.layout = layout
            reset()
        }
    }

    fun getGraph(): Graph? {
        return graph
    }

    fun setGraph(graph: Graph?) {
        this.graph = graph
    }

    fun getCoordinateManager(): CoordinateManager<*, *>? {
        return coordinateManager
    }

    fun setCoordinateManager(manager: CoordinateManager<*, *>?) {
        val old = coordinateManager
        if (old !== manager) {
            old?.removeCoordinateListener(coordinateListener)
            coordinateManager = manager
            manager.addCoordinateListener(coordinateListener)
        }
    }

    fun getState(): IterativeGraphLayoutState<*>? {
        return state
    }

    /**
     * Get parameters associated with the current layout.
     * @return parameters
     */
    fun getParameters(): Any? {
        return params
    }

    /**
     * Set parameters for the current layout
     * @param params new parameters
     */
    fun setParameters(params: Any?) {
        this.params = params
    }

    /**
     * Get the # of algorithm iterations per update loop
     * @return iterations
     */
    fun getIterationsPerLoop(): Int {
        return iterationsPerLoop
    }

    /**
     * Set the # of algorithm iterations per update loop
     * @param n # of iterations
     */
    fun setIterationsPerLoop(n: Int) {
        iterationsPerLoop = n
    }
    //endregion
    /**
     * Re-initialize the graph layout, resetting iteration, state, and params.
     * If the type of params is still valid for the new layout, it is not changed.
     */
    fun reset() {
        if (layout == null) {
            state = null
            params = null
        } else {
            state = layout.createState()
            state.requestPositions(coordinateManager.activeLocationCopy, true)
            val newParams = layout.createParameters()
            val oldParamsType: Class<*>? = if (params == null) null else params.javaClass
            val newParamsType: Class<*>? = newParams?.javaClass
            if (newParamsType != oldParamsType) {
                params = newParams
            }
        }
        iteration = 0
    }

    /**
     * Perform a single layout loop. Depending on the manager's settings, this
     * may invoke the background graph layout 1 or more times.
     * @return energy of last loop
     * @throws InterruptedException if layout interrupted after an intermediate iteration
     */
    @Throws(InterruptedException::class)
    fun runOneLoop(): Double {
        var energy = 0.0
        for (i in 0 until iterationsPerLoop) {
            energy = layout.iterate(graph, state, params)
            checkInterrupt()
        }
        coordinateManager.setCoordinateMap(state.getPositionsCopy())
        iteration += iterationsPerLoop
        state.setCoolingParameter(COOLING_CURVE.apply(Math.max(0, iteration - 100)))
        checkInterrupt()
        return energy
    }

    @Throws(InterruptedException::class)
    private fun checkInterrupt() {
        if (Thread.interrupted()) {
            throw InterruptedException("Layout canceled")
        }
    }

    //region ThreadSafe PROPERTIES
    fun requestPositions(loc: MutableMap<*, Point2D.Double?>?, resetNodes: Boolean) {
        if (state != null) {
            state.requestPositions(loc, resetNodes)
        }
    } //endregion

    companion object {
        /** Default # iterations per layout step  */
        private const val DEFAULT_ITERATIONS_PER_LOOP = 2

        /** Cooling curve. Determines the cooling parameter at each step, as a product of initial cooling parameter.  */
        private val COOLING_CURVE: Function<Int?, Double?>? = Function { x: Int? -> .1 + .9 / Math.log10(x + 10.0) }
    }

    init {
        coordinateListener = CoordinateListener<*, *> { evt: CoordinateChangeEvent<*, *>? -> requestPositions((evt.getSource() as CoordinateManager<*, *>).activeLocationCopy, true) }
        setCoordinateManager(CoordinateManager.create(GraphLayoutManager.Companion.NODE_CACHE_SIZE))
    }
}