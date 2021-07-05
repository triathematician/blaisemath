/*
 * BlaiseGraphicsTestApp.java
 */
package com.googlecode.blaisemath.svg
/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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
import com.google.common.base.Function
import com.google.common.base.Functions
import com.google.common.collect.Maps
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
import com.googlecode.blaisemath.graphics.AnchoredIcon
import com.googlecode.blaisemath.graphics.AnchoredText
import com.googlecode.blaisemath.graphics.core.*
import com.googlecode.blaisemath.graphics.editor.BasicPointStyleEditor
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.swing.*
import com.googlecode.blaisemath.graphics.swing.render.ArrowPathRenderer
import com.googlecode.blaisemath.graphics.swing.render.ArrowPathRenderer.ArrowLocation
import com.googlecode.blaisemath.graphics.swing.render.MarkerRenderer
import com.googlecode.blaisemath.graphics.swing.render.MarkerRendererToClip
import com.googlecode.blaisemath.graphics.swing.render.TextRenderer
import com.googlecode.blaisemath.style.Anchor
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Markers.availableMarkers
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.ui.PropertyActionPanel
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.Images
import com.googlecode.blaisemath.util.geom.Points
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.jdesktop.application.Action
import org.jdesktop.application.SingleFrameApplication
import java.awt.*
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.*
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JOptionPane
import javax.swing.JPopupMenu
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.Marshaller

/**
 * The main class of the application.
 */
class BlaiseGraphicsTestApp : SingleFrameApplication() {
    var root1: JGraphicRoot? = null
    var canvas1: JGraphicComponent? = null
    val pointsetStyle = RandomStyles.point()
    @Action
    @Throws(JAXBException::class)
    fun printSvg() {
        val root: SvgRoot = SvgElementGraphicConverter.Companion.componentToSvg(canvas1)
        val jc: JAXBContext = JAXBContext.newInstance(SvgRoot::class.java)
        val m: Marshaller = jc.createMarshaller()
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        m.marshal(root, System.out)
    }

    //<editor-fold defaultstate="collapsed" desc="GENERAL">
    @Action
    fun clear1() {
        root1.clearGraphics()
    }

    private fun randomPoint(): Point2D? {
        return Point2D.Double(Math.random() * canvas1.getWidth(), Math.random() * canvas1.getHeight())
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="BASIC">
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
        val line = Line2D.Double(randomPoint(), randomPoint())
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
        bg.setDragEnabled(true)
        root1.addGraphic(bg)
    }

    @Action
    fun addIcon() {
        val pt = randomPoint()
        for (i in 0..2) {
            for (j in 0..2) {
                val icon = AnchoredIcon(pt.getX() + i * 50, pt.getY() + j * 50, randomIcon())
                val bp: PrimitiveGraphic<*, *>? = JGraphics.icon(icon)
                bp.setStyle(AttributeSet.of(Styles.TEXT_ANCHOR, ANCHORS.get(i), Styles.ALIGN_BASELINE, BASELINES.get(j)))
                bp.setDefaultTooltip("<html><b>Icon</b>: <i> $pt</i>")
                bp.setDragEnabled(true)
                root1.addGraphic(bp)
                root1.addGraphic(JGraphics.path(Line2D.Double(icon.getX() - 20, icon.getY(), icon.getX() + 20, icon.getY()),
                        Styles.strokeWidth(Color(128, 128, 255, 64), 1f)))
                root1.addGraphic(JGraphics.path(Line2D.Double(icon.getX(), icon.getY() - 20, icon.getX(), icon.getY() + 20),
                        Styles.strokeWidth(Color(128, 128, 255, 64), 1f)))
            }
        }
    }

    private fun randomIcon(): Icon? {
        val img = Math.random() > .5
        return if (img) {
            val iconUrl = Images::class.java.getResource("resources/cherries.png")
            ImageIcon(iconUrl)
        } else {
            object : Icon {
                override fun getIconWidth(): Int {
                    return 30
                }

                override fun getIconHeight(): Int {
                    return 30
                }

                override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
                    val g2 = g as Graphics2D?
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                    g2.setColor(Color.red)
                    g2.draw(Line2D.Double(x, y, x + 30, y + 30))
                    g2.draw(Line2D.Double(x + 30, y, x, y + 30))
                }
            }
        }
    }

    @Action
    fun addPointSet() {
        val bp: BasicPointSetGraphic<*> = BasicPointSetGraphic<Any?>(arrayOf(randomPoint(), randomPoint(), randomPoint()),
                pointsetStyle, MarkerRenderer.Companion.getInstance())
        bp.addContextMenuInitializer(object : ContextMenuInitializer<Graphic<Graphics2D?>?> {
            fun initContextMenu(menu: JPopupMenu?, src: Graphic<Graphics2D?>?, point: Point2D?, focus: Any?, selection: MutableSet<*>?) {
                val pt = bp.getPoint(bp.indexOf(point, null))
                menu.add(Points.format(pt, 2))
                menu.add(context.actionMap["editPointSetStyle"])
            }
        })
        root1.addGraphic(bp)
    }

    @Action
    fun editPointSetStyle() {
        val ed = BasicPointStyleEditor(pointsetStyle)
        ed.addPropertyChangeListener("style") { canvas1.repaint() }
        JOptionPane.showMessageDialog(mainFrame, ed)
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="GRAPHICS WITH DELEGATORS">
    @Action
    fun addDelegatingPointSet() {
        val list: MutableSet<String?> = HashSet(Arrays.asList(
                "Africa", "Indiana Jones", "Micah Andrew Peterson", "Chrysanthemum",
                "Sequoia", "Asher Matthew Peterson", "Elisha", "Bob the Builder"))
        val crds: MutableMap<String?, Point2D?> = Maps.newLinkedHashMap()
        for (s in list) {
            crds[s] = Point(10 * s.length, 50 + 10 * s.indexOf(" "))
        }
        val bp = DelegatingPointSetGraphic<String?, Graphics2D?>(
                MarkerRenderer.Companion.getInstance(), TextRenderer.Companion.getInstance())
        bp.addObjects(crds)
        bp.isDragEnabled = true
        bp.styler.setLabelDelegate(Functions.toStringFunction())
        bp.styler.setLabelStyle(Styles.DEFAULT_TEXT_STYLE)
        bp.styler.setStyleDelegate(object : Function<String?, AttributeSet?>() {
            var r: AttributeSet? = AttributeSet()
            fun apply(src: String?): AttributeSet? {
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
        val points2: MutableMap<Int?, Point2D?> = Maps.newLinkedHashMap()
        for (i in 1..10) {
            points2[i] = randomPoint()
        }
        val bp = DelegatingPointSetGraphic<Int?, Graphics2D?>(
                MarkerRenderer.Companion.getInstance(), TextRenderer.Companion.getInstance())
        bp.addObjects(points2)
        bp.isDragEnabled = true
        bp.styler.setLabelDelegate(Functions.toStringFunction())
        bp.styler.setLabelStyleDelegate(object : Function<Int?, AttributeSet?>() {
            var r: AttributeSet? = AttributeSet()
            fun apply(src: Int?): AttributeSet? {
                r.put(Styles.TEXT_ANCHOR, Anchor.CENTER)
                r.put(Styles.FONT_SIZE, 5 + src.toFloat())
                return r
            }
        })
        bp.styler.setStyleDelegate(object : Function<Int?, AttributeSet?>() {
            var r: AttributeSet? = AttributeSet()
            fun apply(src: Int?): AttributeSet? {
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
        val pts: MutableMap<Int?, Point2D?> = Maps.newLinkedHashMap()
        for (i in 0..14) {
            pts[i] = randomPoint()
        }
        val edges: MutableSet<EndpointPair<Int?>?> = HashSet<EndpointPair<Int?>?>()
        for (i in 0 until pts.size) {
            val n = (Math.random() * 6) as Int
            for (j in 0 until n) {
                edges.add(EndpointPair.ordered(i, (Math.random() * pts.size) as Int))
            }
        }
        // create graphic
        val gr: DelegatingNodeLinkGraphic<Int?, EndpointPair<Int?>?, Graphics2D?>? = JGraphics.nodeLink()
        gr.setDragEnabled(true)
        gr.setNodeLocations(pts)
        gr.getNodeStyler().setStyleDelegate(object : Function<Int?, AttributeSet?>() {
            fun apply(src: Int?): AttributeSet? {
                val pt = pts[src]
                val yy = Math.min(pt.getX() / 3, 255.0) as Int
                return AttributeSet.of(Styles.FILL, Color(yy, 0, 255 - yy))
                        .and(Styles.MARKER_RADIUS, Math.sqrt(pt.getY()) as Float)
            }
        })
        gr.getNodeStyler().setLabelDelegate(object : Function<Int?, String?>() {
            fun apply(src: Int?): String? {
                val pt = pts[src]
                return String.format("(%.1f,%.1f)", pt.getX(), pt.getY())
            }
        })
        gr.getNodeStyler().setLabelStyleDelegate(object : Function<Int?, AttributeSet?>() {
            var bss = Styles.DEFAULT_TEXT_STYLE
            fun apply(src: Int?): AttributeSet? {
                return bss
            }
        })
        gr.setEdgeSet(edges)
        gr.getEdgeStyler().setStyleDelegate(object : Function<EndpointPair<Int?>?, AttributeSet?>() {
            fun apply(src: EndpointPair<Int?>?): AttributeSet? {
                val src0 = pts[src.nodeU()]
                val src1 = pts[src.nodeV()]
                var dx = (src0.getX() - src1.getX()) as Int
                dx = Math.min(Math.abs(dx / 2), 255)
                var dy = (src0.getY() - src1.getY()) as Int
                dy = Math.min(Math.abs(dy / 3), 255)
                return AttributeSet.of(Styles.STROKE, Color(dx, dy, 255 - dy))
                        .and(Styles.STROKE_WIDTH, Math.sqrt(dx * dx + dy * dy.toDouble()) as Float / 50)
            }
        })
        root1.addGraphic(gr)
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="COMPOSITES">
    @Action
    fun addLabeledShape() {
        val rect = Rectangle2D.Double()
        rect.setFrameFromDiagonal(randomPoint(), randomPoint())
        val gfc: LabeledShapeGraphic<*> = LabeledShapeGraphic<Any?>()
        gfc.primitive = rect
        gfc.isDragEnabled = true
        gfc.objectStyler.setStyle(RandomStyles.shape())
        gfc.objectStyler.setLabel("""
    this is a long label for a rectangle that should get wrapped, since it needs to be really big so we can adequately test something with a long label
    and new line characters
    """.trimIndent())
        gfc.objectStyler.setLabelStyle(RandomStyles.anchoredString())
        root1.addGraphic(gfc)
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="COOL STUFF USING SPECIAL STYLES">
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
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="APP CODE">
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
        private val ANCHORS: Array<String?>? = arrayOf(Styles.TEXT_ANCHOR_END, Styles.TEXT_ANCHOR_MIDDLE, Styles.TEXT_ANCHOR_START)
        private val BASELINES: Array<String?>? = arrayOf(Styles.ALIGN_BASELINE_BASELINE, Styles.ALIGN_BASELINE_MIDDLE, Styles.ALIGN_BASELINE_HANGING)

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
        } //</editor-fold>
    }
}