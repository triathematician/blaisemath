/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import com.google.common.graph.ElementOrder
import com.google.common.graph.EndpointPair
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import com.google.common.io.Files
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
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.graphics.swing.JGraphics
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.test.AssertUtils
import com.googlecode.blaisemath.ui.PropertyActionPanel
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.geom.Rectangle2D
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.*

/**
 *
 * @author elisha
 */
class SvgTool : JFrame() {
    private val gsvg: SvgGraphic?

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private fun initComponents() {
        buttonGroup1 = ButtonGroup()
        jToolBar1 = JToolBar()
        loadB = JButton()
        saveB = JButton()
        jSeparator1 = JToolBar.Separator()
        jButton3 = JButton()
        jButton1 = JButton()
        jSplitPane1 = JSplitPane()
        canvas = JGraphicComponent()
        jPanel1 = JPanel()
        jScrollPane2 = JScrollPane()
        text = JTextArea()
        jToolBar2 = JToolBar()
        pathTB = JRadioButton()
        xmlTB = JRadioButton()
        defaultCloseOperation = EXIT_ON_CLOSE
        jToolBar1.setRollover(true)
        loadB.setText("Load")
        loadB.setFocusable(false)
        loadB.setHorizontalTextPosition(SwingConstants.CENTER)
        loadB.setVerticalTextPosition(SwingConstants.BOTTOM)
        loadB.addActionListener(ActionListener { evt -> loadBActionPerformed(evt) })
        jToolBar1.add(loadB)
        saveB.setText("Save")
        saveB.setFocusable(false)
        saveB.setHorizontalTextPosition(SwingConstants.CENTER)
        saveB.setVerticalTextPosition(SwingConstants.BOTTOM)
        saveB.addActionListener(ActionListener { evt -> saveBActionPerformed(evt) })
        jToolBar1.add(saveB)
        jToolBar1.add(jSeparator1)
        jButton3.setText("Draw")
        jButton3.setFocusable(false)
        jButton3.setHorizontalTextPosition(SwingConstants.CENTER)
        jButton3.setVerticalTextPosition(SwingConstants.BOTTOM)
        jButton3.addActionListener(ActionListener { evt -> jButton3ActionPerformed(evt) })
        jToolBar1.add(jButton3)
        jButton1.setText("Move")
        jButton1.setFocusable(false)
        jButton1.setHorizontalTextPosition(SwingConstants.CENTER)
        jButton1.setVerticalTextPosition(SwingConstants.BOTTOM)
        jButton1.addActionListener(ActionListener { evt -> jButton1ActionPerformed(evt) })
        jToolBar1.add(jButton1)
        contentPane.add(jToolBar1, BorderLayout.PAGE_START)
        jSplitPane1.setResizeWeight(0.7)
        jSplitPane1.setLeftComponent(canvas)
        jPanel1.setLayout(BorderLayout())
        text.setColumns(20)
        text.setRows(5)
        jScrollPane2.setViewportView(text)
        jPanel1.add(jScrollPane2, BorderLayout.CENTER)
        jToolBar2.setRollover(true)
        buttonGroup1.add(pathTB)
        pathTB.setSelected(true)
        pathTB.setText("Path")
        pathTB.setFocusable(false)
        pathTB.setVerticalTextPosition(SwingConstants.BOTTOM)
        jToolBar2.add(pathTB)
        buttonGroup1.add(xmlTB)
        xmlTB.setText("SVG XML")
        xmlTB.setFocusable(false)
        xmlTB.setVerticalTextPosition(SwingConstants.BOTTOM)
        jToolBar2.add(xmlTB)
        jPanel1.add(jToolBar2, BorderLayout.PAGE_START)
        jSplitPane1.setRightComponent(jPanel1)
        contentPane.add(jSplitPane1, BorderLayout.CENTER)
        pack()
    } // </editor-fold>//GEN-END:initComponents

    private fun jButton3ActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_jButton3ActionPerformed
        if (pathTB.isSelected()) {
            gsvg.setElement(SvgPath(text.getText()))
        } else {
            try {
                val root: SvgRoot = SvgRoot.Companion.load(text.getText())
                gsvg.setElement(root)
                val bg = root.style["background"]
                if (bg is Color) {
                    canvas.setBackground(bg as Color?)
                }
            } catch (ex: IOException) {
                Logger.getLogger(SvgTool::class.java.name).log(Level.SEVERE, null, ex)
            }
        }
    } //GEN-LAST:event_jButton3ActionPerformed

    private val chooser: JFileChooser? = JFileChooser()
    private fun saveBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_saveBActionPerformed
        if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this)) {
            try {
                FileOutputStream(chooser.getSelectedFile()).use { out ->
                    var el = gsvg.getElement()
                    if (el !is SvgRoot) {
                        val rootEl = SvgRoot()
                        rootEl.addElement(el)
                        el = rootEl
                    }
                    SvgRoot.Companion.save(el as SvgRoot, out)
                }
            } catch (x: IOException) {
                Logger.getLogger(SvgTool::class.java.name).log(Level.SEVERE, null, x)
            }
        }
    } //GEN-LAST:event_saveBActionPerformed

    private fun loadBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_loadBActionPerformed
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            try {
                FileInputStream(chooser.getSelectedFile()).use { fis ->
                    val r: SvgRoot = SvgRoot.Companion.load(fis)
                    gsvg.setElement(r)
                    val fs: String = Files.toString(chooser.getSelectedFile(), Charset.defaultCharset())
                    text.setText(fs)
                }
            } catch (x: IOException) {
                Logger.getLogger(SvgTool::class.java.name).log(Level.SEVERE, null, x)
            }
        }
    } //GEN-LAST:event_loadBActionPerformed

    private fun jButton1ActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_jButton1ActionPerformed
        gsvg.setGraphicBounds(Rectangle(20, 50, 40, 100))
    } //GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private var buttonGroup1: ButtonGroup? = null
    private var canvas: JGraphicComponent? = null
    private var jButton1: JButton? = null
    private var jButton3: JButton? = null
    private var jPanel1: JPanel? = null
    private var jScrollPane2: JScrollPane? = null
    private var jSeparator1: JToolBar.Separator? = null
    private var jSplitPane1: JSplitPane? = null
    private var jToolBar1: JToolBar? = null
    private var jToolBar2: JToolBar? = null
    private var loadB: JButton? = null
    private var pathTB: JRadioButton? = null
    private var saveB: JButton? = null
    private var text: JTextArea? = null
    private var xmlTB: JRadioButton? = null // End of variables declaration//GEN-END:variables

    companion object {
        /**
         * @param args the command line arguments
         */
        @JvmStatic
        fun main(args: Array<String>) {
            /* Set the Nimbus look and feel */
            //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
            /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
            try {
                for (info in UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus" == info.name) {
                        UIManager.setLookAndFeel(info.className)
                        break
                    }
                }
            } catch (ex: ClassNotFoundException) {
                Logger.getLogger(SvgTool::class.java.name).log(Level.SEVERE, null, ex)
            } catch (ex: InstantiationException) {
                Logger.getLogger(SvgTool::class.java.name).log(Level.SEVERE, null, ex)
            } catch (ex: IllegalAccessException) {
                Logger.getLogger(SvgTool::class.java.name).log(Level.SEVERE, null, ex)
            } catch (ex: UnsupportedLookAndFeelException) {
                Logger.getLogger(SvgTool::class.java.name).log(Level.SEVERE, null, ex)
            }
            //</editor-fold>

            /* Create and display the form */EventQueue.invokeLater { SvgTool().isVisible = true }
        }
    }

    /**
     * Creates new form SvgTool
     */
    init {
        initComponents()
        minimumSize = Dimension(400, 400)
        preferredSize = Dimension(500, 500)
        maximumSize = Dimension(600, 600)
        gsvg = SvgGraphic()
        gsvg.setStyle(Styles.strokeWidth(Color.blue, 2f))
        canvas.addGraphic(gsvg)
        canvas.addGraphic(JGraphics.path(Rectangle2D.Double(0, 0, 1000, 1000), Styles.strokeWidth(Color(128, 128, 128, 128), 1f)))
        canvas.addGraphic(JGraphics.path(Rectangle2D.Double(500, 500, 1000, 1000), Styles.strokeWidth(Color(128, 128, 128, 128), 1f)))
        canvas.addGraphic(JGraphics.path(Rectangle2D.Double(-500, -500, 1000, 1000), Styles.strokeWidth(Color(128, 128, 128, 128), 1f)))
        PanAndZoomHandler.Companion.install(canvas)
    }
}