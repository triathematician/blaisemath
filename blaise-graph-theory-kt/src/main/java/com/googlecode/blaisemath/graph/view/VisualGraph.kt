package com.googlecode.blaisemath.graph.view

import com.google.common.base.Functions
import com.google.common.graph.EndpointPair
import com.google.common.graph.Graph
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.coordinate.CoordinateManager
import com.googlecode.blaisemath.graph.ContractedGraphTest
import com.googlecode.blaisemath.graph.GraphUtilsTest
import com.googlecode.blaisemath.graph.SubgraphTest
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.generate.GraphGrowthRule
import com.googlecode.blaisemath.graph.generate.GraphSeedRule
import com.googlecode.blaisemath.graph.generate.HopGrowthRule
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager
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
import com.googlecode.blaisemath.graphics.AnchoredText
import com.googlecode.blaisemath.graphics.core.DelegatingNodeLinkGraphic
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
import com.googlecode.blaisemath.style.AttributeSet
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
import junit.framework.TestCase
import org.junit.BeforeClass
import java.awt.Color
import java.awt.Shape
import java.awt.geom.Point2D
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.function.Supplier

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
 * Combines a [GraphLayoutManager] and a [DelegatingNodeLinkGraphic]
 * to manage a graph and its node locations. The graph is maintained by the manager,
 * and the visual elements by the graphic.
 *
 * @param <G> graphics canvas type
 * @author Elisha Peterson
</G> */
class VisualGraph<G>(
        manager: GraphLayoutManager<*>?,
        /** Responsible for instantiating the view graph  */
        private val viewGraphSupplier: Supplier<DelegatingNodeLinkGraphic<Any?, EndpointPair<Any?>?, G?>?>?
) {
    /** Stores the visible graph  */
    private var viewGraph: DelegatingNodeLinkGraphic<Any?, EndpointPair<Any?>?, G?>? = null

    /** Manages graph and node locations  */
    private var layoutManager: GraphLayoutManager<*>? = null

    /** Listens for changes from the layout  */
    protected val layoutListener: PropertyChangeListener?

    /**
     * Construct adapter with the specified graph.
     * @param graph the graph to display
     */
    constructor(graph: Graph?) : this(GraphLayoutManager.Companion.create<Any?>(graph), null) {}
    //region INITIALIZATION
    /**
     * Initializes view graph for the graph in the graph manager. This includes
     * setting up any styling appropriate for the graph, as well as updating
     * the nodes and edges in the view graph.
     */
    protected fun initViewGraph() {
        if (viewGraph == null) {
            if (viewGraphSupplier != null) {
                viewGraph = viewGraphSupplier.get()
            } else {
                viewGraph = DelegatingNodeLinkGraphic<Any?, EndpointPair<Any?>?, G?>(
                        layoutManager.getCoordinateManager(), null, null, null)
                viewGraph.getNodeStyler().setStyle(DEFAULT_NODE_STYLE)
            }

            // set up default styles, in case the graph isn't visible by default
            if (viewGraph.getNodeStyler().styleDelegate == null) {
                viewGraph.getNodeStyler().setStyle(DEFAULT_NODE_STYLE)
            }
            if (viewGraph.getNodeStyler().labelDelegate == null) {
                viewGraph.getNodeStyler().setLabelDelegate(Functions.toStringFunction())
            }
            if (viewGraph.getNodeStyler().tipDelegate == null) {
                viewGraph.getNodeStyler().setTipDelegate(Functions.toStringFunction())
            }
            if (viewGraph.getEdgeStyler().styleDelegate == null) {
                viewGraph.getEdgeStyler().setStyle(DEFAULT_EDGE_STYLE)
            }
        } else {
            viewGraph.setCoordinateManager(layoutManager.getCoordinateManager())
        }
        viewGraph.setEdgeSet(layoutManager.getGraph().edges())
    }
    //endregion
    //region PROPERTIES
    /**
     * Return the graphic used for display.
     * @return the graphic
     */
    fun getViewGraph(): DelegatingNodeLinkGraphic<*, *, *>? {
        return viewGraph
    }

    /**
     * Return the layout manager responsible for updating positions.
     * @return layout manager
     */
    fun getLayoutManager(): GraphLayoutManager<*>? {
        return layoutManager
    }

    /**
     * Change the layout manager responsible for updating positions.
     * @param manager layout manager
     */
    fun setLayoutManager(manager: GraphLayoutManager<*>?) {
        if (layoutManager != manager) {
            if (layoutManager != null) {
                layoutManager.removePropertyChangeListener(GraphLayoutManager.Companion.P_GRAPH, layoutListener)
            }
            layoutManager = manager
            initViewGraph()
            layoutManager.addPropertyChangeListener(GraphLayoutManager.Companion.P_GRAPH, layoutListener)
        }
    }
    //endregion
    //region DELEGATE PROPERTIES
    /**
     * Return the coordinate manager responsible for node locations.
     * @return coordinate manager
     */
    fun getCoordinateManager(): CoordinateManager<*, *>? {
        return layoutManager.getCoordinateManager()
    }

    /**
     * Return the graph instance being visualized.
     * @return the graph
     */
    fun getGraph(): Graph? {
        return layoutManager.getGraph()
    }

    /**
     * Set the graph instance being visualized.
     * @param g the graph
     */
    fun setGraph(g: Graph?) {
        layoutManager.setGraph(g)
    }

    /**
     * Return node styler.
     * @return styler
     */
    fun getNodeStyler(): ObjectStyler<Any?>? {
        return viewGraph.getNodeStyler()
    }

    /**
     * Set node styler.
     * @param styler object styler for nodes
     */
    fun setNodeStyler(styler: ObjectStyler<Any?>?) {
        viewGraph.setNodeStyler(styler)
    }

    /**
     * Return node renderer.
     * @return renderer
     */
    fun getNodeRenderer(): Renderer<Point2D?, G?>? {
        return viewGraph.getNodeRenderer()
    }

    /**
     * Set node renderer.
     * @param renderer new renderer
     */
    fun setNodeRenderer(renderer: Renderer<Point2D?, G?>?) {
        viewGraph.setNodeRenderer(renderer)
    }

    /**
     * Return label renderer.
     * @return renderer
     */
    fun getLabelRenderer(): Renderer<AnchoredText?, G?>? {
        return viewGraph.getLabelRenderer()
    }

    /**
     * Set label renderer.
     * @param renderer new renderer
     */
    fun setLabelRenderer(renderer: Renderer<AnchoredText?, G?>?) {
        viewGraph.setLabelRenderer(renderer)
    }

    /**
     * Return edge styler.
     * @return edge styler
     */
    fun getEdgeStyler(): ObjectStyler<EndpointPair<Any?>?>? {
        return viewGraph.getEdgeStyler()
    }

    /**
     * Set edge styler.
     * @param styler edge styler
     */
    fun setEdgeStyler(styler: ObjectStyler<EndpointPair<Any?>?>?) {
        viewGraph.setEdgeStyler(styler)
    }

    /**
     * Return edge renderer.
     * @return renderer
     */
    fun getEdgeRenderer(): Renderer<Shape?, G?>? {
        return viewGraph.getEdgeRenderer()
    }

    /**
     * Set edge renderer.
     * @param renderer new renderer
     */
    fun setEdgeRenderer(renderer: Renderer<Shape?, G?>?) {
        viewGraph.setEdgeRenderer(renderer)
    } //endregion

    companion object {
        /** Default graph node style  */
        val DEFAULT_NODE_STYLE: AttributeSet? = Styles.fillStroke(
                Color(0, 0, 128, 128), Color(0, 0, 128, 192), .5f)
                .and(Styles.MARKER_RADIUS, 3f)

        /** Default graph edge style  */
        val DEFAULT_EDGE_STYLE = Styles.strokeWidth(
                Color(0, 128, 0, 128), 1f)
    }

    /**
     * Construct adapter with the specified manager.
     * @param manager a GraphLayoutManager with the graph to display
     * @param graphicSupplier optional, provides a way to override default creation of the view graph
     */
    init {
        layoutListener = PropertyChangeListener { evt: PropertyChangeEvent? -> initViewGraph() }
        setLayoutManager(manager)
    }
}