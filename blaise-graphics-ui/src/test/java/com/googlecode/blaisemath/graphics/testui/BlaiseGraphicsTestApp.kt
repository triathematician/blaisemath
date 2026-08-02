package com.googlecode.blaisemath.graphics.testui

import com.google.common.base.Functions
import com.google.common.collect.Maps
import com.google.common.graph.EndpointPair
import com.googlecode.blaisemath.geom.Points
import com.googlecode.blaisemath.graphics.Graphic
import com.googlecode.blaisemath.graphics.PrimitiveGraphic
import com.googlecode.blaisemath.graphics.impl.*
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.graphics.swing.JGraphicRoot
import com.googlecode.blaisemath.graphics.swing.JGraphics
import com.googlecode.blaisemath.graphics.swing.LabeledShapeGraphic
import com.googlecode.blaisemath.graphics.swing.render.*
import com.googlecode.blaisemath.primitive.Anchor
import com.googlecode.blaisemath.primitive.AnchoredText
import com.googlecode.blaisemath.primitive.Markers
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.ObjectStyler
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.style.ui.BasicPointStyleEditor
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer
import org.jdesktop.application.Action
import org.jdesktop.application.SingleFrameApplication
import java.awt.*
import java.awt.geom.*
import java.beans.PropertyChangeEvent
import java.util.*
import javax.swing.JOptionPane
import javax.swing.JPopupMenu

/*
* #%L
* BlaiseGraphics
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
*/   class BlaiseGraphicsTestApp : SingleFrameApplication() {
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

    private fun randomPoint(): Point2D.Double? {
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
        val pt: Point2D? = randomPoint()
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
        val pt: Point2D? = randomPoint()
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
        val bp: BasicPointSetGraphic<*> = BasicPointSetGraphic<Any?>(arrayOf<Point2D?>(randomPoint(), randomPoint(), randomPoint()),
                pointSetStyle, MarkerRenderer.getInstance())
        bp.addContextMenuInitializer(ContextMenuInitializer { menu: JPopupMenu?, src: Graphic<Graphics2D?>?, point: Point2D?, focus: Any?, selection: MutableSet<*>? ->
            val pt = bp.getPoint(bp.indexOf(point, null))
            menu.add(Points.format(pt, 2))
            menu.add(context.actionMap["editPointSetStyle"])
        } as ContextMenuInitializer<Graphic<Graphics2D?>?>)
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
        gfc.objectStyler.setLabel("""this is a long label for a rectangle that should get wrapped, since it needs to be really big so we can adequately test something with a
 long
 label
and


 new line characters
x""")
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
        val crds: MutableMap<String?, Point2D.Double?>? = Maps.newLinkedHashMap()
        for (s in list) {
            crds[s] = Point2D.Double(10 * s.length, 50 + 10 * s.indexOf(" "))
        }
        val bp = DelegatingPointSetGraphic<String?, Graphics2D?>(
                MarkerRenderer.getInstance(), TextRenderer.getInstance())
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
                r.put(Styles.MARKER, Markers.getAvailableMarkers()[i2 + 3])
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
        val points2: MutableMap<Int?, Point2D.Double?>? = Maps.newLinkedHashMap()
        for (i in 1..10) {
            points2[i] = randomPoint()
        }
        val bp = DelegatingPointSetGraphic<Int?, Graphics2D?>(
                MarkerRenderer.getInstance(), TextRenderer.getInstance())
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
        val pts: MutableMap<Int?, Point2D.Double?>? = Maps.newLinkedHashMap()
        for (i in 0..14) {
            pts[i] = randomPoint()
        }
        val edges: MutableSet<EndpointPair<Int?>?> = HashSet()
        for (i in 0 until pts.size) {
            val n = (Math.random() * 6) as Int
            for (j in 0 until n) {
                edges.add(EndpointPair.unordered(i, (Math.random() * pts.size) as Int))
            }
        }
        // create graphic
        val gr = JGraphics.nodeLink<Int?>()
        gr.isDragEnabled = true
        gr.setNodeLocations(pts)
        gr.nodeStyler.setStyleDelegate { src: Int? ->
            val pt: Point2D? = pts.get(src)
            val yy = Math.min(pt.getX() / 3, 255.0) as Int
            AttributeSet.of(Styles.FILL, Color(yy, 0, 255 - yy),
                    Styles.MARKER_RADIUS, Math.sqrt(pt.getY()) as Float)
        }
        gr.nodeStyler.setLabelDelegate { src: Int? ->
            val pt: Point2D? = pts.get(src)
            String.format("(%.1f,%.1f)", pt.getX(), pt.getY())
        }
        gr.nodeStyler.setLabelStyle(Styles.DEFAULT_TEXT_STYLE)
        gr.setEdgeSet(edges)
        gr.edgeStyler.setStyleDelegate { src: EndpointPair<Int?>? ->
            val src0: Point2D? = pts.get(src.nodeU())
            val src1: Point2D? = pts.get(src.nodeV())
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
        val p1: Point2D? = randomPoint()
        val lpg: LabeledPointGraphic<Point2D?, Graphics2D?> = LabeledPointGraphic<Any?, Any?>(p1, p1, ObjectStyler())
        lpg.setRenderer(MarkerRenderer.getInstance())
        lpg.setLabelRenderer(TextRenderer())
        lpg.objectStyler.setLabelDelegate(java.util.function.Function { p: Point2D? -> String.format("(%.2f,%.2f)", p.getX(), p.getY()) })
        lpg.objectStyler.setLabelStyle(Styles.text(Color.red, 14f, Anchor.SOUTHWEST))
        lpg.defaultTooltip = "<html><b>Labeled Point</b>: <i> $p1</i>"
        lpg.isDragEnabled = true
        root1.addGraphic(lpg)
    }

    @Action
    fun add2Point() {
        val p1: Point2D? = randomPoint()
        val p2: Point2D? = randomPoint()
        val ag: TwoPointGraphic<*> = TwoPointGraphic<Any?>(p1, p2, MarkerRenderer.getInstance())
        ag.defaultTooltip = "<html><b>Two Points</b>: <i>$p1, $p2</i>"
        ag.isDragEnabled = true
        root1.addGraphic(ag)
    }

    @Action
    fun addDraggableSegment() {
        val p1: Point2D? = randomPoint()
        val p2: Point2D? = randomPoint()
        val ag: SegmentGraphic<*> = SegmentGraphic<Any?>(p1, p2, ArrowLocation.NONE, MarkerRenderer.getInstance(), PathRenderer.getInstance())
        ag.defaultTooltip = "<html><b>Segment</b>: <i>$p1, $p2</i>"
        ag.isDragEnabled = true
        root1.addGraphic(ag)
    }

    @Action
    fun addArrow() {
        val p1: Point2D? = randomPoint()
        val p2: Point2D? = randomPoint()
        val ag: SegmentGraphic<*> = SegmentGraphic<Any?>(p1, p2, ArrowLocation.END, MarkerRenderer.getInstance(), PathRenderer.getInstance())
        ag.defaultTooltip = "<html><b>Arrow</b>: <i>$p1, $p2</i>"
        ag.isDragEnabled = true
        root1.addGraphic(ag)
    }

    //endregion
    //region SPECIAL STYLES
    @Action
    fun addRay() {
        val p1: Point2D? = randomPoint()
        val p2: Point2D? = randomPoint()
        val ag: TwoPointGraphic<*> = TwoPointGraphic<Any?>(p1, p2, MarkerRenderer.getInstance())
        val rend = MarkerRendererToClip()
        rend.rayRenderer = ArrowPathRenderer(ArrowLocation.END)
        ag.startGraphic.setRenderer(rend)
        ag.defaultTooltip = "<html><b>Ray</b>: <i>$p1, $p2</i>"
        ag.isDragEnabled = true
        root1.addGraphic(ag)
    }

    @Action
    fun addLine() {
        val p1: Point2D? = randomPoint()
        val p2: Point2D? = randomPoint()
        val ag: TwoPointGraphic<*> = TwoPointGraphic<Any?>(p1, p2, MarkerRenderer.getInstance())
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