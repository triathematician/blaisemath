/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.graphics.testui

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
import com.googlecode.blaisemath.graphics.core.Graphic
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic
import com.googlecode.blaisemath.graphics.svg.SvgElementGraphicConverter
import com.googlecode.blaisemath.graphics.svg.SvgGraphic
import com.googlecode.blaisemath.graphics.svg.SvgGraphicComponent
import com.googlecode.blaisemath.graphics.svg.SvgUtils
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.graphics.swing.JGraphics
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
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.Graphics2D
import java.awt.event.ActionEvent
import java.awt.geom.Ellipse2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.*

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
 */   class ContextMenuTestFrame : JFrame() {
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private fun initComponents() {
        gc = JGraphicComponent()
        jLabel1 = JLabel()
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.add(gc, BorderLayout.CENTER)
        jLabel1.setText("<html>Should be up to 3 parts of the context menu: one for the circle, one for the selection, and one for the root graphics.")
        contentPane.add(jLabel1, BorderLayout.PAGE_START)
        pack()
    } // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private var gc: JGraphicComponent? = null
    private var jLabel1: JLabel? = null // End of variables declaration//GEN-END:variables

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
                Logger.getLogger(ContextMenuTestFrame::class.java.name).log(Level.SEVERE, null, ex)
            } catch (ex: UnsupportedLookAndFeelException) {
                Logger.getLogger(ContextMenuTestFrame::class.java.name).log(Level.SEVERE, null, ex)
            } catch (ex: IllegalAccessException) {
                Logger.getLogger(ContextMenuTestFrame::class.java.name).log(Level.SEVERE, null, ex)
            } catch (ex: InstantiationException) {
                Logger.getLogger(ContextMenuTestFrame::class.java.name).log(Level.SEVERE, null, ex)
            }
            //endregion

            /* Create and display the form */EventQueue.invokeLater { ContextMenuTestFrame().isVisible = true }
        }
    }

    /**
     * Creates new form TestTooltips
     */
    init {
        initComponents()
        val g1: PrimitiveGraphic<*, *>? = JGraphics.shape(Ellipse2D.Double(50, 50, 100, 100))
        g1.addContextMenuInitializer(ContextMenuInitializer<*> { menu: JPopupMenu?, src: Any?, point: Point2D?, focus: Any?, selection: MutableSet<*>? ->
            menu.add("" + point)
            menu.add(object : AbstractAction("press me") {
                override fun actionPerformed(e: ActionEvent?) {
                    println("pressed")
                }
            })
        })
        g1.setSelectionEnabled(true)
        gc.addGraphic(g1)
        val g2: PrimitiveGraphic<*, *>? = JGraphics.shape(Rectangle2D.Double(60, 90, 100, 100))
        g2.setSelectionEnabled(true)
        gc.addGraphic(g2)
        gc.setSelectionEnabled(true)
        gc.getGraphicRoot().addContextMenuInitializer(ContextMenuInitializer<Graphic<Graphics2D?>?> { menu: JPopupMenu?, src: Graphic<Graphics2D?>?, point: Point2D?, focus: Any?, selection: MutableSet<*>? ->
            menu.setLabel("root label")
            menu.add((selection?.size ?: 0).toString() + " graphics selected")
            if (menu.getComponentCount() > 0) menu.addSeparator()
            menu.add("Root menu option")
        })
    }
}