package com.googlecode.blaisemath.graphics.testui
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
import com.google.common.base.Functions
import com.google.common.collect.Maps
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
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
import com.googlecode.blaisemath.graphics.core.*
import com.googlecode.blaisemath.graphics.editor.BasicPointStyleEditor
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.*
import com.googlecode.blaisemath.graphics.swing.render.*
import com.googlecode.blaisemath.graphics.swing.render.ArrowPathRenderer.ArrowLocation
import com.googlecode.blaisemath.style.Anchor
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Markers.availableMarkers
import com.googlecode.blaisemath.style.ObjectStyler
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
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.Images
import com.googlecode.blaisemath.util.geom.Points
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.jdesktop.application.Action
import org.jdesktop.application.SingleFrameApplication
import java.awt.*
import java.awt.geom.*
import java.beans.PropertyChangeEvent
import java.util.*
import javax.swing.JOptionPane
import javax.swing.JPopupMenu

class BlaiseGraphicsTestApp : SingleFrameApplication() {
    var root1: JGraphicRoot? = null
    var canvas1: JGraphicComponent? = null
    val pointSetStyle = RandomStyles.point()

    //region GENERAL ACTIONS
    @Action
    fun clear1() {
        root1.clearGraphics()
    }

    private fun randomX(): Double {
        return Math.random() * canvas1.getWidth()
    }

    private fun randomY(): Double {
        return Math.random() * canvas1.getHeight()
    }

    private fun randomPoint(): Point2D? {
        return Point2D.Double(Math.random() * canvas1.getWidth(), Math.random() * canvas1.getHeight())
    }

    @Action
    fun zoomAll() {
        canvas1.zoomToAll()
    }

    @Action
    fun zoomSelected() {
        canvas1.zoomToSelected()
    }

    @Action
    fun zoomAllOutsets() {
        canvas1.zoomToAll(Insets(50, 50, 50, 50))
    }

    @Action
    fun zoomSelectedOutsets() {
        canvas1.zoomToSelected(Insets(50, 50, 50, 50))
    }

    //endregion
    //region BASIC GRAPHICS
    @Action
    fun addPoint() {
        val pt = randomPoint()
        val bp: PrimitiveGraphic<*, *>? = JGraphics.point(pt, RandomStyles.point())
        bp.setDefaultTooltip("<html><b>Point</b>: <i> $pt</i>")
        bp.setDragEnabled(true)
        root1.addGraphic(bp)
    }

    @Action
    fun addSegment() {
        var line: Shape? = Line2D.Double(randomPoint(), randomPoint())
        if (Math.random() < .3) {
            val gp = GeneralPath()
            val x = randomX()
            gp.moveTo(x, randomY())
            gp.lineTo(x, randomY())
            gp.lineTo(x, randomY())
            line = gp
        } else if (Math.random() < .3) {
            val line2 = Line2D.Double(randomPoint(), randomPoint())
            line = Line2D.Double(line2.getX1(), line2.getY1(), line2.getX2(), line2.getY1())
        } else if (Math.random() < .3) {
            line = Ellipse2D.Double()
            val line2 = Line2D.Double(randomPoint(), randomPoint())
            //            ((Ellipse2D.Double) line).setFrameFromDiagonal(randomPoint(), randomPoint());
            (line as Ellipse2D.Double?).setFrameFromDiagonal(line2.getX1(), line2.getY1(), line2.getX2(), line2.getY1())
        }
        val bs: PrimitiveGraphic<*, *>? = JGraphics.path(line, RandomStyles.path())
        bs.setDefaultTooltip("<html><b>Segment</b>: <i>$line</i>")
        root1.addGraphic(bs)
    }

    @Action
    fun addRectangle() {
        val rect = Rectangle2D.Double()
        rect.setFrameFromDiagonal(randomPoint(), randomPoint())
        val bs: PrimitiveGraphic<*, *>? = JGraphics.shape(rect, RandomStyles.shape())
        bs.setDefaultTooltip("<html><b>Rectangle</b>: <i>$rect</i>")
        root1.addGraphic(bs)
    }

    @Action
    fun addString() {
        val pt = randomPoint()
        val txt = AnchoredText(pt, String.format("[%.4f, %.4f]", pt.getX(), pt.getY()))
        val bg: PrimitiveGraphic<*, *>? = JGraphics.text(txt, RandomStyles.string())
        if (Math.random() < .3) {
            bg.setRenderer(SlopedTextRenderer(Math.random()))
        }
        bg.setDragEnabled(true)
        root1.addGraphic(bg)
    }

    @Action
    fun addPointSet() {
        val bp: BasicPointSetGraphic<*> = BasicPointSetGraphic<Any?>(arrayOf(randomPoint(), randomPoint(), randomPoint()),
                pointSetStyle, MarkerRenderer.Companion.getInstance())
        bp.addContextMenuInitializer(ContextMenuInitializer<Graphic<Graphics2D?>?> { menu: JPopupMenu?, src: Graphic<Graphics2D?>?, point: Point2D?, focus: Any?, selection: MutableSet<*>? ->
            val pt = bp.getPoint(bp.indexOf(point, null))
            menu.add(Points.format(pt, 2))
            menu.add(context.actionMap["editPointSetStyle"])
        } as ContextMenuInitializer<Graphic<Graphics2D?>?>?)
        root1.addGraphic(bp)
    }

    @Action
    fun editPointSetStyle() {
        val ed = BasicPointStyleEditor(pointSetStyle)
        ed.addPropertyChangeListener("style") { evt: PropertyChangeEvent? -> canvas1.repaint() }
        JOptionPane.showMessageDialog(mainFrame, ed)
    }

    //endregion
    //region GRAPHICS WITH DELEGATION
    @Action
    fun addWrappedText() {
        if (Math.random() < .33) {
            addWrappedTextEndChar()
        } else if (Math.random() < .5) {
            addWrappedTextRandom()
        } else {
            addWrappedTextSmall()
        }
    }

    private fun addWrappedTextRandom() {
        val rect = Rectangle2D.Double()
        rect.setFrameFromDiagonal(randomPoint(), randomPoint())
        val gfc: LabeledShapeGraphic<*> = LabeledShapeGraphic<Any?>()
        gfc.primitive = rect
        gfc.objectStyler.setLabel("""
    this is a long label for a rectangle that should get wrapped, since it needs to be really big so we can adequately test something with a long label
    and new line characters
    x
    """.trimIndent())
        gfc.objectStyler.setLabelStyle(Styles.text(RandomStyles.color(), RandomStyles.fontSize().toFloat(), Anchor.NORTHWEST))
        root1.addGraphic(gfc)
    }

    private fun addWrappedTextSmall() {
        val gfc: LabeledShapeGraphic<*> = LabeledShapeGraphic<Any?>()
        val r = Random()
        gfc.primitive = Rectangle2D.Double(r.nextInt(100) + 100, r.nextInt(100) + 100, r.nextInt(20) + 5, r.nextInt(20) + 5)
        gfc.objectStyler.setLabel(if (r.nextBoolean()) "ab" else "a")
        gfc.objectStyler.setLabelStyle(Styles.text(RandomStyles.color(), RandomStyles.fontSize().toFloat(), Anchor.NORTHWEST))
        root1.addGraphic(gfc)
    }

    private fun addWrappedTextEndChar() {
        val gfc: LabeledShapeGraphic<*> = LabeledShapeGraphic<Any?>()
        val r = Random()
        gfc.primitive = Rectangle2D.Double(r.nextInt(100) + 100, r.nextInt(100) + 100, r.nextInt(100) + 5, r.nextInt(100) + 5)
        gfc.objectStyler.setLabel("a\nb\nc")
        gfc.objectStyler.setLabelStyle(Styles.text(RandomStyles.color(), RandomStyles.fontSize().toFloat(), Anchor.NORTHWEST))
        root1.addGraphic(gfc)
    }

    @Action
    fun addDelegatingPointSet() {
        val list: MutableSet<String?> = HashSet(Arrays.asList(
                "Africa", "Indiana Jones", "Micah Andrew Peterson", "Chrysanthemum",
                "Sequoia", "Asher Matthew Peterson", "Elisha Peterson", "Bob the Builder"))
        val crds: MutableMap<String?, Point2D?>? = Maps.newLinkedHashMap()
        for (s in list) {
            crds[s] = Point(10 * s.length, 50 + 10 * s.indexOf(" "))
        }
        val bp = DelegatingPointSetGraphic<String?, Graphics2D?>(
                MarkerRenderer.Companion.getInstance(), TextRenderer.Companion.getInstance())
        bp.addObjects(crds)
        bp.isDragEnabled = true
        bp.styler.setLabelDelegate(Functions.toStringFunction())
        bp.styler.setLabelStyle(Styles.DEFAULT_TEXT_STYLE)
        bp.styler.setStyleDelegate(object : com.google.common.base.Function<String?, AttributeSet?> {
            val r: AttributeSet? = AttributeSet()
            override fun apply(src: String?): AttributeSet? {
                val i1 = src.indexOf("a")
                val i2 = src.indexOf("e")
                val i3 = src.indexOf("i")
                val i4 = src.indexOf("o")
                r.put(Styles.MARKER_RADIUS, i1 + 5)
                r.put(Styles.MARKER, availableMarkers[i2 + 3])
                r.put(Styles.STROKE, Color.BLACK)
                r.put(Styles.STROKE_WIDTH, 2 + i3 / 3f)
                r.put(Styles.FILL, Color((i4 * 10 + 10) % 255, (i4 * 20 + 25) % 255, (i4 * 30 + 50) % 255))
                return r
            }
        })
        bp.isPointSelectionEnabled = true
        root1.addGraphic(bp)
    }

    @Action
    fun addDelegatingPointSet2() {
        val points2: MutableMap<Int?, Point2D?>? = Maps.newLinkedHashMap()
        for (i in 1..10) {
            points2[i] = randomPoint()
        }
        val bp = DelegatingPointSetGraphic<Int?, Graphics2D?>(
                MarkerRenderer.Companion.getInstance(), TextRenderer.Companion.getInstance())
        bp.addObjects(points2)
        bp.isDragEnabled = true
        bp.styler.setLabelDelegate(Functions.toStringFunction())
        bp.styler.setLabelStyleDelegate(object : com.google.common.base.Function<Int?, AttributeSet?> {
            val r: AttributeSet? = AttributeSet()
            override fun apply(src: Int?): AttributeSet? {
                r.put(Styles.TEXT_ANCHOR, Anchor.CENTER)
                r.put(Styles.FONT_SIZE, 5 + src.toFloat())
                return r
            }
        })
        bp.styler.setStyleDelegate(object : com.google.common.base.Function<Int?, AttributeSet?> {
            val r: AttributeSet? = AttributeSet()
            override fun apply(src: Int?): AttributeSet? {
                r.put(Styles.MARKER_RADIUS, src + 2)
                r.put(Styles.FILL, Color((src * 10 + 10) % 255, (src * 20 + 25) % 255, (src * 30 + 50) % 255))
                r.put(Styles.STROKE, Colors.lighterThan(r.getColor(Styles.FILL)))
                return r
            }
        })
        root1.addGraphic(bp)
    }

    @Action
    fun addDelegatingGraph() {
        // initialize graph object
        val pts: MutableMap<Int?, Point2D?>? = Maps.newLinkedHashMap()
        for (i in 0..14) {
            pts[i] = randomPoint()
        }
        val edges: MutableSet<EndpointPair<Int?>?> = HashSet<EndpointPair<Int?>?>()
        for (i in 0 until pts.size) {
            val n = (Math.random() * 6) as Int
            for (j in 0 until n) {
                edges.add(EndpointPair.unordered(i, (Math.random() * pts.size) as Int))
            }
        }
        // create graphic
        val gr: DelegatingNodeLinkGraphic<Int?, EndpointPair<Int?>?, Graphics2D?>? = JGraphics.nodeLink()
        gr.setDragEnabled(true)
        gr.setNodeLocations(pts)
        gr.getNodeStyler().setStyleDelegate(java.util.function.Function { src: Int? ->
            val pt = pts.get(src)
            val yy = Math.min(pt.getX() / 3, 255.0) as Int
            AttributeSet.of(Styles.FILL, Color(yy, 0, 255 - yy),
                    Styles.MARKER_RADIUS, Math.sqrt(pt.getY()) as Float)
        })
        gr.getNodeStyler().setLabelDelegate { src: Int? ->
            val pt = pts.get(src)
            String.format("(%.1f,%.1f)", pt.getX(), pt.getY())
        }
        gr.getNodeStyler().setLabelStyle(Styles.DEFAULT_TEXT_STYLE)
        gr.setEdgeSet(edges)
        gr.getEdgeStyler().setStyleDelegate { src: EndpointPair<Int?>? ->
            val src0 = pts.get(src.nodeU())
            val src1 = pts.get(src.nodeV())
            var dx = (src0.getX() - src1.getX()) as Int
            dx = Math.min(Math.abs(dx / 2), 255)
            var dy = (src0.getY() - src1.getY()) as Int
            dy = Math.min(Math.abs(dy / 3), 255)
            AttributeSet.of(Styles.STROKE, Color(dx, dy, 255 - dy),
                    Styles.STROKE_WIDTH, Math.sqrt(dx * dx + dy * dy.toDouble()) as Float / 50)
        }
        root1.addGraphic(gr)
    }

    //endregion
    //region COMPOSITE GRAPHICS
    @Action
    fun addLabeledPoint() {
        val p1 = randomPoint()
        val lpg: LabeledPointGraphic<*, *> = LabeledPointGraphic<Any?, Any?>(String.format("(%.2f,%.2f)", p1.getX(), p1.getY()), p1,
                ObjectStyler())
        lpg.defaultTooltip = "<html><b>Labeled Point</b>: <i> $p1</i>"
        lpg.isDragEnabled = true
        root1.addGraphic(lpg)
    }

    @Action
    fun add2Point() {
        val p1 = randomPoint()
        val p2 = randomPoint()
        val ag = TwoPointGraphic(p1, p2)
        ag.defaultTooltip = "<html><b>Two Points</b>: <i>$p1, $p2</i>"
        ag.isDragEnabled = true
        root1.addGraphic(ag)
    }

    @Action
    fun addDraggableSegment() {
        val p1 = randomPoint()
        val p2 = randomPoint()
        val ag = SegmentGraphic(p1, p2, ArrowLocation.NONE)
        ag.defaultTooltip = "<html><b>Segment</b>: <i>$p1, $p2</i>"
        ag.isDragEnabled = true
        root1.addGraphic(ag)
    }

    @Action
    fun addArrow() {
        val p1 = randomPoint()
        val p2 = randomPoint()
        val ag = SegmentGraphic(p1, p2, ArrowLocation.END)
        ag.defaultTooltip = "<html><b>Arrow</b>: <i>$p1, $p2</i>"
        ag.isDragEnabled = true
        root1.addGraphic(ag)
    }

    //endregion
    //region SPECIAL STYLES
    @Action
    fun addRay() {
        val p1 = randomPoint()
        val p2 = randomPoint()
        val ag = TwoPointGraphic(p1, p2)
        val rend = MarkerRendererToClip()
        rend.rayRenderer = ArrowPathRenderer(ArrowLocation.END)
        ag.startGraphic.setRenderer(rend)
        ag.defaultTooltip = "<html><b>Ray</b>: <i>$p1, $p2</i>"
        ag.isDragEnabled = true
        root1.addGraphic(ag)
    }

    @Action
    fun addLine() {
        val p1 = randomPoint()
        val p2 = randomPoint()
        val ag = TwoPointGraphic(p1, p2)
        val rend = MarkerRendererToClip()
        rend.rayRenderer = ArrowPathRenderer(ArrowLocation.BOTH)
        rend.isExtendBothDirections = true
        ag.startGraphic.setRenderer(rend)
        ag.defaultTooltip = "<html><b>Line</b>: <i>$p1, $p2</i>"
        ag.isDragEnabled = true
        root1.addGraphic(ag)
    }
    //endregion
    //region APP CODE
    /**
     * At startup create and show the main frame of the application.
     */
    override fun startup() {
        val view = BlaiseGraphicsTestFrameView(this)
        canvas1 = view.canvas1
        root1 = view.canvas1.graphicRoot
        canvas1.setSelectionEnabled(true)
        show(view)
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    override fun configureWindow(root: Window?) {}

    companion object {
        /**
         * A convenient static getter for the application instance.
         * @return the instance of BlaiseGraphicsTestApp
         */
        fun getApplication(): BlaiseGraphicsTestApp? {
            return getInstance(BlaiseGraphicsTestApp::class.java)
        }

        /**
         * Main method launching the application.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            launch(BlaiseGraphicsTestApp::class.java, args)
        } //endregion
    }
}