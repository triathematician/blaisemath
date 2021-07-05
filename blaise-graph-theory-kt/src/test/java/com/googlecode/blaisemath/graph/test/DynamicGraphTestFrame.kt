package com.googlecode.blaisemath.graph.test

import com.google.common.graph.Graph
import com.google.common.graph.Graphs
import com.googlecode.blaisemath.editor.EditorRegistration
import com.googlecode.blaisemath.firestarter.PropertySheet
import com.googlecode.blaisemath.graph.layout.CircleLayout
import com.googlecode.blaisemath.graph.layout.CircleLayout.CircleLayoutParameters
import com.googlecode.blaisemath.graph.layout.RandomBoxLayout
import com.googlecode.blaisemath.graph.layout.RandomBoxLayout.BoxLayoutParameters
import com.googlecode.blaisemath.graph.layout.SpringLayout
import com.googlecode.blaisemath.graph.layout.SpringLayoutParameters
import com.googlecode.blaisemath.graph.view.GraphComponent
import com.googlecode.blaisemath.graph.view.VisualGraph
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
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
import com.googlecode.blaisemath.util.Images
import com.googlecode.blaisemath.util.Instrument.print
import com.googlecode.blaisemath.util.RollupPanel
import java.awt.BorderLayout
import java.awt.Color
import java.awt.EventQueue
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.geom.Rectangle2D
import java.util.*
import java.util.Timer
import javax.swing.*

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
*/   class DynamicGraphTestFrame : JFrame() {
    var pga: VisualGraph<*>? = null

    /** Flag for when el needs points updated  */
    var updateEL = true
    var energyLayout: SpringLayout?
    val layoutParams: SpringLayoutParameters?
    val graph: MyTestGraph? = MyTestGraph()
    var graphCopy: Graph<String?>?

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private fun initComponents() {
        jToolBar1 = JToolBar()
        randomLB = JButton()
        circleLB = JButton()
        jSeparator1 = JToolBar.Separator()
        jLabel1 = JLabel()
        energyIB = JButton()
        energyAB = JButton()
        energySB = JButton()
        jSeparator2 = JToolBar.Separator()
        jLabel2 = JLabel()
        addEdgesB = JButton()
        rewireB = JButton()
        addThreadedB = JButton()
        addNodesB = JButton()
        threadStopB = JButton()
        jScrollPane1 = JScrollPane()
        rollupPanel1 = RollupPanel()
        plot = GraphComponent()
        defaultCloseOperation = EXIT_ON_CLOSE
        background = Color(0, 0, 0)
        jToolBar1.setRollover(true)
        randomLB.setText("Random Layout")
        randomLB.setFocusable(false)
        randomLB.setHorizontalTextPosition(SwingConstants.CENTER)
        randomLB.setVerticalTextPosition(SwingConstants.BOTTOM)
        randomLB.addActionListener(ActionListener { evt -> randomLBActionPerformed(evt) })
        jToolBar1.add(randomLB)
        circleLB.setText("Circle Layout")
        circleLB.setFocusable(false)
        circleLB.setHorizontalTextPosition(SwingConstants.CENTER)
        circleLB.setVerticalTextPosition(SwingConstants.BOTTOM)
        circleLB.addActionListener(ActionListener { evt -> circleLBActionPerformed(evt) })
        jToolBar1.add(circleLB)
        jToolBar1.add(jSeparator1)
        jLabel1.setText("ENERGY:")
        jToolBar1.add(jLabel1)
        energyIB.setText("iterate")
        energyIB.setFocusable(false)
        energyIB.setHorizontalTextPosition(SwingConstants.CENTER)
        energyIB.setVerticalTextPosition(SwingConstants.BOTTOM)
        energyIB.addActionListener(ActionListener { evt -> energyIBActionPerformed(evt) })
        jToolBar1.add(energyIB)
        energyAB.setText("animate")
        energyAB.setFocusable(false)
        energyAB.setHorizontalTextPosition(SwingConstants.CENTER)
        energyAB.setVerticalTextPosition(SwingConstants.BOTTOM)
        energyAB.addActionListener(ActionListener { evt -> energyABActionPerformed(evt) })
        jToolBar1.add(energyAB)
        energySB.setText("stop")
        energySB.setFocusable(false)
        energySB.setHorizontalTextPosition(SwingConstants.CENTER)
        energySB.setVerticalTextPosition(SwingConstants.BOTTOM)
        energySB.addActionListener(ActionListener { evt -> energySBActionPerformed(evt) })
        jToolBar1.add(energySB)
        jToolBar1.add(jSeparator2)
        jLabel2.setText("ADD:")
        jToolBar1.add(jLabel2)
        addNodesB.setText("nodes")
        addNodesB.setFocusable(false)
        addNodesB.setHorizontalTextPosition(SwingConstants.CENTER)
        addNodesB.setVerticalTextPosition(SwingConstants.BOTTOM)
        addNodesB.addActionListener(ActionListener { evt -> addNodesBActionPerformed(evt) })
        jToolBar1.add(addNodesB)
        addEdgesB.setText("edges")
        addEdgesB.setFocusable(false)
        addEdgesB.setHorizontalTextPosition(SwingConstants.CENTER)
        addEdgesB.setVerticalTextPosition(SwingConstants.BOTTOM)
        addEdgesB.addActionListener(ActionListener { evt -> addEdgesBActionPerformed(evt) })
        jToolBar1.add(addEdgesB)
        rewireB.setText("rewire")
        rewireB.setFocusable(false)
        rewireB.setHorizontalTextPosition(SwingConstants.CENTER)
        rewireB.setVerticalTextPosition(SwingConstants.BOTTOM)
        rewireB.addActionListener(ActionListener { evt -> rewireBActionPerformed(evt) })
        jToolBar1.add(rewireB)
        addThreadedB.setText("threaded")
        addThreadedB.setFocusable(false)
        addThreadedB.setHorizontalTextPosition(SwingConstants.CENTER)
        addThreadedB.setVerticalTextPosition(SwingConstants.BOTTOM)
        addThreadedB.addActionListener(ActionListener { evt -> addThreadedBActionPerformed(evt) })
        jToolBar1.add(addThreadedB)
        threadStopB.setText("stop")
        threadStopB.setFocusable(false)
        threadStopB.setHorizontalTextPosition(SwingConstants.CENTER)
        threadStopB.setVerticalTextPosition(SwingConstants.BOTTOM)
        threadStopB.addActionListener(ActionListener { evt -> threadStopBActionPerformed(evt) })
        jToolBar1.add(threadStopB)
        contentPane.add(jToolBar1, BorderLayout.PAGE_START)
        jScrollPane1.setViewportView(rollupPanel1)
        contentPane.add(jScrollPane1, BorderLayout.EAST)
        contentPane.add(plot, BorderLayout.CENTER)
        pack()
    } // </editor-fold>//GEN-END:initComponents

    private fun randomLBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_randomLBActionPerformed
        updateEL = true
        plot.getLayoutManager().applyLayout(RandomBoxLayout.Companion.getInstance(), null, BoxLayoutParameters(Rectangle2D.Double(-500, -500, 1000, 1000)))
    } //GEN-LAST:event_randomLBActionPerformed

    private fun circleLBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_circleLBActionPerformed
        updateEL = true
        plot.getLayoutManager().applyLayout(CircleLayout.Companion.getInstance(), null, CircleLayoutParameters(500.0))
    } //GEN-LAST:event_circleLBActionPerformed

    private fun energyIBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_energyIBActionPerformed
        if (energyLayout == null) {
            energyLayout = SpringLayout()
        }
        plot.getLayoutManager().layoutAlgorithm = energyLayout
        plot.getLayoutManager().layoutParameters = layoutParams
        plot.getLayoutManager().iterateLayout()
        updateEL = false
    } //GEN-LAST:event_energyIBActionPerformed

    private fun energyABActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_energyABActionPerformed
        if (energyLayout == null) {
            energyLayout = SpringLayout()
        }
        plot.getLayoutManager().layoutAlgorithm = energyLayout
        plot.getLayoutManager().layoutParameters = layoutParams
        plot.getLayoutManager().isLayoutTaskActive = true
    } //GEN-LAST:event_energyABActionPerformed

    private fun energySBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_energySBActionPerformed
        plot.getLayoutManager().isLayoutTaskActive = false
    } //GEN-LAST:event_energySBActionPerformed

    @Synchronized
    private fun updateGraph() {
        SwingUtilities.invokeLater {
            graphCopy = Graphs.copyOf(graph)
            plot.getLayoutManager().setGraph(graphCopy)
            plot.getAdapter().viewGraph.setEdgeSet(graphCopy.edges())
        }
    }

    private fun addNodesBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_addNodesBActionPerformed
        graph.addNodes(5)
        updateGraph()
    } //GEN-LAST:event_addNodesBActionPerformed

    private fun addEdgesBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_addEdgesBActionPerformed
        graph.addEdges(5)
        updateGraph()
    } //GEN-LAST:event_addEdgesBActionPerformed

    private fun rewireBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_rewireBActionPerformed
        graph.rewire(50, 5)
        updateGraph()
    } //GEN-LAST:event_rewireBActionPerformed

    val t: Timer? = Timer()
    var tt: TimerTask? = null
    private fun addThreadedBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_addThreadedBActionPerformed
        if (tt != null) {
            tt.cancel()
        }
        tt = object : TimerTask() {
            override fun run() {
                graph.removeEdges(10)
                graph.addNodes(1)
                graph.removeNodes(1)
                graph.addEdges(2)
                updateGraph()
            }
        }
        t.schedule(tt, 100, 500)
    } //GEN-LAST:event_addThreadedBActionPerformed

    private fun threadStopBActionPerformed(evt: ActionEvent?) { //GEN-FIRST:event_threadStopBActionPerformed
        if (tt != null) {
            tt.cancel()
        }
    } //GEN-LAST:event_threadStopBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private var addEdgesB: JButton? = null
    private var addThreadedB: JButton? = null
    private var addNodesB: JButton? = null
    private var circleLB: JButton? = null
    private var energyAB: JButton? = null
    private var energyIB: JButton? = null
    private var energySB: JButton? = null
    private var jLabel1: JLabel? = null
    private var jLabel2: JLabel? = null
    private var jScrollPane1: JScrollPane? = null
    private var jSeparator1: JToolBar.Separator? = null
    private var jSeparator2: JToolBar.Separator? = null
    private var jToolBar1: JToolBar? = null
    private var plot: GraphComponent? = null
    private var randomLB: JButton? = null
    private var rewireB: JButton? = null
    private var rollupPanel1: RollupPanel? = null
    private var threadStopB: JButton? = null // End of variables declaration//GEN-END:variables

    companion object {
        /**
         * @param args the command line arguments
         */
        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater { DynamicGraphTestFrame().isVisible = true }
        }
    }

    init {
        EditorRegistration.registerEditors()
        initComponents()
        graphCopy = Graphs.copyOf(graph)
        plot.setGraph(graphCopy)
        plot.getAdapter().viewGraph.isDragEnabled = true
        plot.getAdapter().viewGraph.isPointSelectionEnabled = true

        // PANELS
        energyLayout = SpringLayout()
        layoutParams = energyLayout.createParameters()
        rollupPanel1.add("Energy Layout", PropertySheet.forBean(layoutParams))
        for (p in plot.getGraphicRoot().graphics) {
            rollupPanel1.add(p.toString(), PropertySheet.forBean(p))
        }
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                print(System.out, 50)
            }
        })
    }
}