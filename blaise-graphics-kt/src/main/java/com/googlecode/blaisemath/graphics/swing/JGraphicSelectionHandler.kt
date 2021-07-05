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
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.Sets
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
import com.googlecode.blaisemath.graphics.core.Graphic
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.AnchorTestFrame
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer
import com.googlecode.blaisemath.graphics.testui.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.testui.HelloWorldTest
import com.googlecode.blaisemath.graphics.testui.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.testui.SelectionTestFrame
import com.googlecode.blaisemath.graphics.testui.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.StyleHints
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
import com.googlecode.blaisemath.util.SetSelectionModel
import junit.framework.TestCase
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.junit.BeforeClass
import java.awt.*
import java.awt.event.InputEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Rectangle2D
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

/**
 * Mouse handler that enables selection on a composite graphic object. Control must be down for any selection capability.
 * @author Elisha Peterson
 */
class JGraphicSelectionHandler(private val owner: JGraphicComponent) : MouseAdapter(), CanvasPainter<Graphics2D?> {

    /** Whether selector is enabled  */
    private var enabled = true

    /** Model of selected items  */
    private val selection: SetSelectionModel<Graphic<Graphics2D?>?>? = SetSelectionModel()

    /** Style for drawing selection box  */
    private var selectionBoxStyle = Styles.fillStroke(
            Color(128, 128, 255, 32), Color(0, 0, 128, 64))
    private var pressPt: Point? = null
    private var dragPt: Point? = null
    private var selectionBox: Rectangle2D.Double? = null

    //region PROPERTIES
    fun getSelectionModel(): SetSelectionModel<Graphic<Graphics2D?>?>? {
        return selection
    }

    fun isSelectionEnabled(): Boolean {
        return enabled
    }

    fun setSelectionEnabled(enabled: Boolean) {
        if (this.enabled != enabled) {
            this.enabled = enabled
            if (!enabled) {
                selection.selection = emptySet()
            }
        }
    }

    fun getStyle(): AttributeSet? {
        return selectionBoxStyle
    }

    fun setStyle(style: AttributeSet?) {
        selectionBoxStyle = checkNotNull(style)
    }

    //endregion
    override fun paint(component: Component?, canvas: Graphics2D?) {
        if (enabled && selectionBox != null && selectionBox.width > 0 && selectionBox.height > 0) {
            ShapeRenderer.Companion.getInstance().render(selectionBox, selectionBoxStyle, canvas)
        }
    }

    //region EVENTS
    override fun mouseMoved(e: MouseEvent?) {
        if (e.isConsumed()) {
            return
        }
        val g = owner.selectableGraphicAt(e.getPoint())
        val gAll = owner.functionalGraphicAt(e.getPoint())
        if (gAll == null) {
            // reset to default if there is no active mouse graphic
            owner.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR))
        } else if (g != null) {
            // identify selectable graphics when you mouse over them
            owner.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        }
    }

    override fun mouseClicked(e: MouseEvent?) {
        if (!enabled || e.getButton() != MouseEvent.BUTTON1 || e.isConsumed()) {
            return
        }
        if (!isSelectionEvent(e)) {
            selection.selection = emptySet()
            return
        }
        val g: Graphic<Graphics2D?>? = owner.selectableGraphicAt(e.getPoint())
        if (g == null) {
            selection.selection = emptySet()
        } else if (e.isShiftDown()) {
            selection.deselect(g)
        } else if (e.isAltDown()) {
            selection.select(g)
        } else {
            selection.toggleSelection(g)
        }
    }

    override fun mousePressed(e: MouseEvent?) {
        if (!enabled || e.isConsumed() || e.getButton() != MouseEvent.BUTTON1 || !isSelectionEvent(e)) {
            return
        }
        pressPt = e.getPoint()
        if (selectionBox == null) {
            selectionBox = Rectangle2D.Double()
        }
        selectionBox.setFrameFromDiagonal(pressPt, pressPt)
        e.consume()
    }

    override fun mouseDragged(e: MouseEvent?) {
        if (!enabled || e.isConsumed() || selectionBox == null || pressPt == null) {
            return
        }
        dragPt = e.getPoint()
        selectionBox.setFrameFromDiagonal(pressPt, dragPt)
        if (e.getSource() is Component) {
            (e.getSource() as Component).repaint()
        }
        e.consume()
    }

    override fun mouseReleased(e: MouseEvent?) {
        if (!enabled || e.isConsumed() || selectionBox == null || pressPt == null) {
            return
        }
        val releasePt = e.getPoint()
        if (owner.getInverseTransform() == null) {
            selectionBox.setFrameFromDiagonal(pressPt, releasePt)
        } else {
            selectionBox.setFrameFromDiagonal(
                    owner.toGraphicCoordinate(pressPt),
                    owner.toGraphicCoordinate(releasePt))
        }
        if (selectionBox.getWidth() > 0 && selectionBox.getHeight() > 0) {
            var gg = owner.getGraphicRoot().selectableGraphicsIn(selectionBox, owner.canvas())
            if (e.isShiftDown()) {
                val res: MutableSet<Graphic<Graphics2D?>?> = Sets.newHashSet(selection.selection)
                res.removeAll(gg)
                gg = res
            } else if (e.isAltDown()) {
                gg.addAll(selection.selection)
            }
            selection.selection = gg
        }
        selectionBox = null
        pressPt = null
        dragPt = null
        owner.repaint()
        e.consume()
    }

    companion object {
        private var MAC = false

        //endregion
        private fun detectMac() {
            val os = System.getProperty("os.name").toLowerCase()
            MAC = os.contains("mac")
        }

        private fun isSelectionEvent(e: InputEvent?): Boolean {
            return if (MAC) e.isMetaDown() else e.isControlDown()
        }
    }

    /**
     * Initialize for specified component
     * @param owner the component for handling
     */
    init {

        // highlight updates
        selection.addPropertyChangeListener(PropertyChangeListener { evt: PropertyChangeEvent? ->
            val old = evt.getOldValue() as MutableSet<Graphic<*>?>
            val nue = evt.getNewValue() as MutableSet<Graphic<*>?>
            Sets.difference(old, nue).forEach { g -> g.setStyleHint(StyleHints.SELECTED_HINT, false) }
            Sets.difference(nue, old).forEach { g -> g.setStyleHint(StyleHints.SELECTED_HINT, true) }
        })
        detectMac()
    }
}