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
import com.google.common.base.Objects
import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkNotNull
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
import com.googlecode.blaisemath.graphics.core.GraphicComposite
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
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.Point2D
import javax.swing.JPopupMenu
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener

/**
 * Manages the entries on a [JGraphicComponent].
 * The primary additional behavior implemented by `GraphicRoot`, beyond that of its parent
 * `GraphicComposite`, is listening to mouse events on the component and
 * generating [GMouseEvent]s from them.
 *
 *
 * Subclasses might provide additional behavior such as (i) caching the shapes to be drawn
 * to avoid expensive recomputation, or (ii) sorting the shapes into an alternate draw order
 * (e.g. for projections from 3D to 2D).
 *
 * @author Elisha Peterson
 */
class JGraphicRoot(component: JGraphicComponent?) : GraphicComposite<Graphics2D?>() {
    /** Parent component upon which the graphics are drawn.  */
    protected val owner: JGraphicComponent?

    /** Context menu for actions on the graphics  */
    protected val popup: JPopupMenu? = JPopupMenu()

    /** Provides a pluggable way to generate mouse events  */
    protected var mouseFactory: GMouseEvent.Factory? = GMouseEvent.Factory()

    /** Current owner of mouse events. Gets first priority for mouse events that occur.  */
    private var mouseGraphic: Graphic<*>? = null

    /** Tracks current mouse location  */
    private var mouseLoc: Point2D? = null

    //region PROPERTIES
    override fun setParent(p: GraphicComposite<*>?) {
        checkArgument(p == null, "GraphicRoot cannot be added to another GraphicComposite")
    }

    override fun setStyleContext(rend: StyleContext?) {
        checkArgument(rend != null, "GraphicRoot must have a non-null StyleProvider!")
        super.setStyleContext(rend)
    }

    /**
     * Return current object used to generate mouse events.
     * @return mouse event factory
     */
    fun getMouseEventFactory(): GMouseEvent.Factory? {
        return mouseFactory
    }

    /**
     * Modifies how mouse events are created.
     * @param factory responsible for generating mouse events
     */
    fun setMouseEventFactory(factory: GMouseEvent.Factory?) {
        if (mouseFactory !== factory) {
            mouseFactory = checkNotNull(factory)
        }
    }

    //endregion
    //region EVENTS
    override fun fireGraphicChanged() {
        graphicChanged(this)
    }

    override fun graphicChanged(source: Graphic<*>?) {
        owner?.repaint()
    }

    /**
     * Create GraphicMouseEvent from given event.
     * @param e mouse event
     * @return associated graphic mouse event
     */
    private fun graphicMouseEvent(e: MouseEvent?): GMouseEvent? {
        var localPoint: Point2D? = e.getPoint()
        if (owner.getInverseTransform() != null) {
            localPoint = owner.getInverseTransform().transform(localPoint, null)
        }
        return mouseFactory.createEvent(e, localPoint, this)
    }

    /**
     * Change current owner of mouse events.
     * @param gme graphic mouse event
     * @param keepCurrent whether to maintain current selection even if it's behind another graphic
     * @param canvas target canvas
     */
    private fun updateMouseGraphic(gme: GMouseEvent?, keepCurrent: Boolean, canvas: Graphics2D?) {
        if (keepCurrent && mouseGraphic != null && GraphicUtils.isFunctional(mouseGraphic)
                && mouseGraphic.contains(gme.getGraphicLocation(), canvas)) {
            return
        }
        val nue: Graphic<*>? = mouseGraphicAt(gme.getGraphicLocation(), canvas)
        if (!Objects.equal(mouseGraphic, nue)) {
            mouseExit(mouseGraphic, gme)
            mouseGraphic = nue
            mouseEnter(mouseGraphic, gme)
        }
    }

    private fun mouseEnter(mouseGraphic: Graphic<*>?, event: GMouseEvent?) {
        if (mouseGraphic != null) {
            event.setGraphicSource(mouseGraphic)
            for (l in mouseGraphic.mouseListeners) {
                l.mouseEntered(event)
                if (event.isConsumed()) {
                    return
                }
            }
        }
    }

    private fun mouseExit(mouseGraphic: Graphic<*>?, event: GMouseEvent?) {
        if (mouseGraphic != null) {
            event.setGraphicSource(mouseGraphic)
            for (l in mouseGraphic.mouseListeners) {
                l.mouseExited(event)
                if (event.isConsumed()) {
                    return
                }
            }
        }
    }

    /** Delegate for mouse events  */
    private inner class MouseHandler : MouseListener, MouseMotionListener {
        override fun mouseClicked(e: MouseEvent?) {
            val gme = graphicMouseEvent(e)
            updateMouseGraphic(gme, false, owner.canvas())
            if (mouseGraphic != null) {
                for (l in mouseGraphic.getMouseListeners()) {
                    l.mouseClicked(gme)
                    if (gme.isConsumed()) {
                        return
                    }
                }
            }
        }

        override fun mouseMoved(e: MouseEvent?) {
            val gme = graphicMouseEvent(e)
            mouseLoc = gme.getGraphicLocation()
            updateMouseGraphic(gme, false, owner.canvas())
            if (mouseGraphic != null) {
                gme.setGraphicSource(mouseGraphic)
                for (l in mouseGraphic.getMouseMotionListeners()) {
                    l.mouseMoved(gme)
                    if (gme.isConsumed()) {
                        return
                    }
                }
            }
        }

        override fun mousePressed(e: MouseEvent?) {
            val gme = graphicMouseEvent(e)
            updateMouseGraphic(gme, false, owner.canvas())
            if (mouseGraphic != null) {
                gme.setGraphicSource(mouseGraphic)
                for (l in mouseGraphic.getMouseListeners()) {
                    l.mousePressed(gme)
                    if (gme.isConsumed()) {
                        return
                    }
                }
            }
        }

        override fun mouseDragged(e: MouseEvent?) {
            if (mouseGraphic != null) {
                val gme = graphicMouseEvent(e)
                gme.setGraphicSource(mouseGraphic)
                for (l in mouseGraphic.getMouseMotionListeners()) {
                    l.mouseDragged(gme)
                    if (gme.isConsumed()) {
                        return
                    }
                }
            }
        }

        override fun mouseReleased(e: MouseEvent?) {
            if (mouseGraphic != null) {
                val gme = graphicMouseEvent(e)
                gme.setGraphicSource(mouseGraphic)
                for (l in mouseGraphic.getMouseListeners()) {
                    l.mouseReleased(gme)
                    if (gme.isConsumed()) {
                        return
                    }
                }
            }
        }

        override fun mouseEntered(e: MouseEvent?) {
            // no behavior desired
        }

        override fun mouseExited(e: MouseEvent?) {
            if (mouseGraphic != null) {
                val gme = graphicMouseEvent(e)
                mouseExit(mouseGraphic, gme)
            }
        }
    } //endregion

    /**
     * Construct a default instance
     * @param component the graphic root's component
     */
    init {
        owner = checkNotNull(component)
        val mh: MouseHandler = MouseHandler()
        owner.addMouseListener(mh)
        owner.addMouseMotionListener(mh)
        owner.componentPopupMenu = popup

        // set up style
        setStyleContext(Styles.defaultStyleContext())
        style.put(Styles.FILL, Color.lightGray)
        style.put(Styles.STROKE, Color.black)

        // set up popup menu
        popup.addPopupMenuListener(object : PopupMenuListener {
            override fun popupMenuWillBecomeVisible(e: PopupMenuEvent?) {
                if (mouseLoc != null) {
                    popup.removeAll()
                    val selected: MutableSet<Graphic<Graphics2D?>?>? = if (owner.isSelectionEnabled) owner.selectionModel.selection else null
                    initContextMenu(popup, null, mouseLoc, null, selected, owner.canvas())
                }
            }

            override fun popupMenuWillBecomeInvisible(e: PopupMenuEvent?) {
                popup.removeAll()
            }

            override fun popupMenuCanceled(e: PopupMenuEvent?) {
                popup.removeAll()
            }
        })
    }
}