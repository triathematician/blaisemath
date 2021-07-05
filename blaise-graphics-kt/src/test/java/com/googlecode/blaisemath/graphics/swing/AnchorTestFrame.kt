package com.googlecode.blaisemath.graphics.swing

import com.google.common.collect.ImmutableMap
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import com.googlecode.blaisemath.coordinate.OrientedPoint2D
import com.googlecode.blaisemath.editor.EditorRegistration
import com.googlecode.blaisemath.firestarter.PropertySheet
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
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModel
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.render.MultilineTextRenderer
import com.googlecode.blaisemath.style.Anchor
import com.googlecode.blaisemath.style.AttributeSet
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
import com.googlecode.blaisemath.util.Images
import com.googlecode.blaisemath.util.MPanel
import com.googlecode.blaisemath.util.RollupPanel
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.geom.Line2D
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import javax.imageio.ImageIO
import javax.swing.*

/*
* #%L
* blaise-graphics
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
class AnchorTestFrame : JFrame() {
    private val textStyle: AttributeSet? = Styles.DEFAULT_TEXT_STYLE.copy()
            .and(Styles.OFFSET, Point())

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private fun initComponents() {
        mlRend = MultilineTextRenderer()
        jToolBar1 = JToolBar()
        jLabel1 = JLabel()
        jButton1 = JButton()
        jButton2 = JButton()
        jButton3 = JButton()
        jSeparator1 = JToolBar.Separator()
        jLabel2 = JLabel()
        jButton4 = JButton()
        jButton5 = JButton()
        jButton6 = JButton()
        jSeparator2 = JToolBar.Separator()
        jComboBox1 = JComboBox<Any?>()
        jButton7 = JButton()
        canvas = JGraphicComponent()
        jScrollPane1 = JScrollPane()
        defaultCloseOperation = EXIT_ON_CLOSE
        jToolBar1.setFloatable(false)
        jToolBar1.setRollover(true)
        jLabel1.setText("text-anchor:")
        jToolBar1.add(jLabel1)
        jButton1.setText("Start")
        jButton1.setFocusable(false)
        jButton1.setHorizontalTextPosition(SwingConstants.CENTER)
        jButton1.setVerticalTextPosition(SwingConstants.BOTTOM)
        jButton1.addActionListener(ActionListener { evt: ActionEvent? -> jButton1ActionPerformed(evt) })
        jToolBar1.add(jButton1)
        jButton2.setText("Middle")
        jButton2.setFocusable(false)
        jButton2.setHorizontalTextPosition(SwingConstants.CENTER)
        jButton2.setVerticalTextPosition(SwingConstants.BOTTOM)
        jButton2.addActionListener(ActionListener { evt: ActionEvent? -> jButton2ActionPerformed(evt) })
        jToolBar1.add(jButton2)
        jButton3.setText("End")
        jButton3.setFocusable(false)
        jButton3.setHorizontalTextPosition(SwingConstants.CENTER)
        jButton3.setVerticalTextPosition(SwingConstants.BOTTOM)
        jButton3.addActionListener(ActionListener { evt: ActionEvent? -> jButton3ActionPerformed(evt) })
        jToolBar1.add(jButton3)
        jToolBar1.add(jSeparator1)
        jLabel2.setText("alignment-baseline:")
        jToolBar1.add(jLabel2)
        jButton4.setText("Baseline")
        jButton4.setFocusable(false)
        jButton4.setHorizontalTextPosition(SwingConstants.CENTER)
        jButton4.setVerticalTextPosition(SwingConstants.BOTTOM)
        jButton4.addActionListener(ActionListener { evt: ActionEvent? -> jButton4ActionPerformed(evt) })
        jToolBar1.add(jButton4)
        jButton5.setText("Middle")
        jButton5.setFocusable(false)
        jButton5.setHorizontalTextPosition(SwingConstants.CENTER)
        jButton5.setVerticalTextPosition(SwingConstants.BOTTOM)
        jButton5.addActionListener(ActionListener { evt: ActionEvent? -> jButton5ActionPerformed(evt) })
        jToolBar1.add(jButton5)
        jButton6.setText("Hanging")
        jButton6.setFocusable(false)
        jButton6.setHorizontalTextPosition(SwingConstants.CENTER)
        jButton6.setVerticalTextPosition(SwingConstants.BOTTOM)
        jButton6.addActionListener(ActionListener { evt: ActionEvent? -> jButton6ActionPerformed(evt) })
        jToolBar1.add(jButton6)
        jToolBar1.add(jSeparator2)
        jComboBox1.setModel(DefaultComboBoxModel(arrayOf<String?>("Item 1", "Item 2", "Item 3", "Item 4")))
        jComboBox1.addActionListener(ActionListener { evt: ActionEvent? -> jComboBox1ActionPerformed(evt) })
        jToolBar1.add(jComboBox1)
        jButton7.setText("Clear anchors")
        jButton7.setFocusable(false)
        jButton7.setHorizontalTextPosition(SwingConstants.CENTER)
        jButton7.setVerticalTextPosition(SwingConstants.BOTTOM)
        jButton7.addActionListener(ActionListener { evt: ActionEvent? -> jButton7ActionPerformed(evt) })
        jToolBar1.add(jButton7)
        contentPane.add(jToolBar1, BorderLayout.PAGE_START)
        contentPane.add(canvas, BorderLayout.CENTER)
        jScrollPane1.setMinimumSize(Dimension(200, 22))
        jScrollPane1.setPreferredSize(Dimension(200, 22))
        contentPane.add(jScrollPane1, BorderLayout.WEST)
        pack()
    } // </editor-fold>//GEN-END:initComponents

    private fun jButton1ActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_jButton1ActionPerformed
        textStyle.put(Styles.TEXT_ANCHOR, Styles.TEXT_ANCHOR_START)
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null))
        canvas.repaint()
    } //GEN-LAST:event_jButton1ActionPerformed

    private fun jButton2ActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_jButton2ActionPerformed
        textStyle.put(Styles.TEXT_ANCHOR, Styles.TEXT_ANCHOR_MIDDLE)
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null))
        canvas.repaint()
    } //GEN-LAST:event_jButton2ActionPerformed

    private fun jButton3ActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_jButton3ActionPerformed
        textStyle.put(Styles.TEXT_ANCHOR, Styles.TEXT_ANCHOR_END)
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null))
        canvas.repaint()
    } //GEN-LAST:event_jButton3ActionPerformed

    private fun jButton4ActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_jButton4ActionPerformed
        textStyle.put(Styles.ALIGN_BASELINE, Styles.ALIGN_BASELINE_BASELINE)
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null))
        canvas.repaint()
    } //GEN-LAST:event_jButton4ActionPerformed

    private fun jButton5ActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_jButton5ActionPerformed
        textStyle.put(Styles.ALIGN_BASELINE, Styles.ALIGN_BASELINE_MIDDLE)
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null))
        canvas.repaint()
    } //GEN-LAST:event_jButton5ActionPerformed

    private fun jButton6ActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_jButton6ActionPerformed
        textStyle.put(Styles.ALIGN_BASELINE, Styles.ALIGN_BASELINE_HANGING)
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null))
        canvas.repaint()
    } //GEN-LAST:event_jButton6ActionPerformed

    private fun jComboBox1ActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_jComboBox1ActionPerformed
        val anchor = jComboBox1.getSelectedItem() as Anchor
        textStyle.put(Styles.TEXT_ANCHOR, Styles.toTextAnchor(anchor))
        textStyle.put(Styles.ALIGN_BASELINE, Styles.toAlignBaseline(anchor))
        canvas.repaint()
    } //GEN-LAST:event_jComboBox1ActionPerformed

    private fun jButton7ActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_jButton7ActionPerformed
        textStyle.remove(Styles.TEXT_ANCHOR)
        textStyle.remove(Styles.ALIGN_BASELINE)
        canvas.repaint()
    } //GEN-LAST:event_jButton7ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private var canvas: JGraphicComponent? = null
    private var jButton1: JButton? = null
    private var jButton2: JButton? = null
    private var jButton3: JButton? = null
    private var jButton4: JButton? = null
    private var jButton5: JButton? = null
    private var jButton6: JButton? = null
    private var jButton7: JButton? = null
    private var jComboBox1: JComboBox<*>? = null
    private var jLabel1: JLabel? = null
    private var jLabel2: JLabel? = null
    private var jScrollPane1: JScrollPane? = null
    private var jSeparator1: JToolBar.Separator? = null
    private var jSeparator2: JToolBar.Separator? = null
    private var jToolBar1: JToolBar? = null
    private var mlRend: MultilineTextRenderer? = null // End of variables declaration//GEN-END:variables

    companion object {
        private fun testIcon(): Icon? {
            return object : Icon {
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

        private fun testImage(): Image? {
            return try {
                ImageIO.read(AnchorTestFrame::class.java.getResource("resources/cherries.png"))
            } catch (ex: IOException) {
                Logger.getLogger(AnchorTestFrame::class.java.name).log(Level.SEVERE, null, ex)
                null
            }
        }

        /**
         * @param args the command line arguments
         */
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                for (info in UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus" == info.name) {
                        UIManager.setLookAndFeel(info.className)
                        break
                    }
                }
            } catch (ex: ClassNotFoundException) {
                Logger.getLogger(AnchorTestFrame::class.java.name).log(Level.SEVERE, null, ex)
            } catch (ex: UnsupportedLookAndFeelException) {
                Logger.getLogger(AnchorTestFrame::class.java.name).log(Level.SEVERE, null, ex)
            } catch (ex: IllegalAccessException) {
                Logger.getLogger(AnchorTestFrame::class.java.name).log(Level.SEVERE, null, ex)
            } catch (ex: InstantiationException) {
                Logger.getLogger(AnchorTestFrame::class.java.name).log(Level.SEVERE, null, ex)
            }

            /* Create and display the form */EventQueue.invokeLater { AnchorTestFrame().isVisible = true }
        }
    }

    /**
     * Creates new form MultilineTextRendererTestFrame
     */
    init {
        initComponents()
        jComboBox1.setModel(DefaultComboBoxModel<Any?>(Anchor.values()))
        jComboBox1.setSelectedItem(Anchor.SOUTHWEST)
        PanAndZoomHandler.Companion.install(canvas)
        canvas.addGraphic(JGraphics.path(Line2D.Double(0, 100, 200, 100),
                Styles.strokeWidth(Color(128, 128, 255, 64), 1f)))
        canvas.addGraphic(JGraphics.path(Line2D.Double(0, 200, 200, 200),
                Styles.strokeWidth(Color(128, 128, 255, 64), 1f)))
        canvas.addGraphic(JGraphics.path(Line2D.Double(0, 300, 200, 300),
                Styles.strokeWidth(Color(128, 128, 255, 64), 1f)))
        canvas.addGraphic(JGraphics.path(Line2D.Double(0, 400, 200, 400),
                Styles.strokeWidth(Color(128, 128, 255, 64), 1f)))
        canvas.addGraphic(JGraphics.path(Line2D.Double(100, 0, 100, 500),
                Styles.strokeWidth(Color(128, 128, 255, 64), 1f)))
        canvas.addGraphic(JGraphics.marker(OrientedPoint2D(100, 100),
                Styles.fillStroke(Color(255, 128, 128, 64), null).and(Styles.MARKER_RADIUS, 12)))
        canvas.addGraphic(JGraphics.text(AnchoredText(100, 100, "Here is some sample text that is a single line"), textStyle))
        canvas.addGraphic(JGraphics.marker(OrientedPoint2D(100, 200),
                Styles.fillStroke(Color(255, 128, 128, 64), null).and(Styles.MARKER_RADIUS, 12)))
        canvas.addGraphic(PrimitiveGraphic(
                AnchoredText(100, 200, "Here is some\nsample text\nthat is wrapped\nonto multiple\nlines"),
                textStyle,
                mlRend))
        canvas.addGraphic(JGraphics.marker(OrientedPoint2D(100, 300),
                Styles.fillStroke(Color(255, 128, 128, 64), null).and(Styles.MARKER_RADIUS, 12)))
        val icon = JGraphics.icon(testIcon(), 100.0, 300.0)
        icon.style = textStyle
        canvas.addGraphic(icon)
        canvas.addGraphic(JGraphics.marker(OrientedPoint2D(100, 400),
                Styles.fillStroke(Color(255, 128, 128, 64), null).and(Styles.MARKER_RADIUS, 12)))
        val image = JGraphics.image(100.0, 400.0, 48.0, 48.0, testImage(), null)
        image.style = textStyle
        canvas.addGraphic(image)
        val os: ObjectStyler<*> = ObjectStyler<Any?>()
        val lsg: LabeledShapeGraphic<*> = LabeledShapeGraphic<Any?>("Here is some sample text that will be automatically wrapped onto multiple lines",
                Rectangle(200, 50, 100, 200), os)
        os.setLabelDelegate { input: Any? -> input.toString() + "" }
        os.setLabelStyle(textStyle)
        lsg.isDragEnabled = true
        canvas.addGraphic(lsg)
        EditorRegistration.registerEditors()
        val rp = RollupPanel()
        val apm = AttributeSetPropertyModel(textStyle,
                ImmutableMap.of<String?, Class<*>?>(Styles.FONT, String::class.java, Styles.FONT_SIZE, Float::class.java,
                        Styles.OFFSET, Point::class.java))
        rp.add(MPanel("Font", PropertySheet.forModel(apm)))
        jScrollPane1.setViewportView(rp)
        revalidate()
    }
}