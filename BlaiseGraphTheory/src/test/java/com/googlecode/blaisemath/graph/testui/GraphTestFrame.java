/*
 * TestPlaneVisometry.java
 *
 * Created on Jul 30, 2009, 3:15:03 PM
 */
package com.googlecode.blaisemath.graph.testui;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.googlecode.blaisemath.editor.EditorRegistration;
import com.googlecode.blaisemath.firestarter.PropertySheet;
import com.googlecode.blaisemath.graph.GAInstrument;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.modules.layout.SpringLayout;
import com.googlecode.blaisemath.graph.modules.suppliers.EdgeProbabilityGraphSupplier;
import com.googlecode.blaisemath.graph.view.GraphComponent;
import com.googlecode.blaisemath.graph.view.VisualGraph;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.CanvasPainter;
import com.googlecode.blaisemath.util.Points;
import com.googlecode.blaisemath.util.RollupPanel;
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Set;
import javax.swing.JPopupMenu;

/**
 *
 * @author ae3263
 */
public class GraphTestFrame extends javax.swing.JFrame {

    VisualGraph pga;
    /** Flag for when el needs points updated */
    boolean updateEL = true;
    SpringLayout energyLayout;

    /** Creates new form TestPlaneVisometry */
    public GraphTestFrame() {
        EditorRegistration.registerEditors();
        initComponents();

        // BASIC ELEMENTS

        final Graph<Integer> graph = new EdgeProbabilityGraphSupplier(false, 50, .05f).get();
        plot.setGraph(graph);
        plot.getAdapter().getViewGraph().setDragEnabled(true);
        plot.getAdapter().getViewGraph().setPointSelectionEnabled(true);
        plot.getLayoutManager().applyLayout(StaticGraphLayout.CIRCLE, Collections.EMPTY_MAP, Collections.EMPTY_SET, 100.0);
        PanAndZoomHandler.zoomBoxAnimated(plot, Points.boundingBox(plot.getLayoutManager().getNodeLocationCopy().values(), 5));
        plot.getAdapter().getNodeStyler().setStyleDelegate(new Function<Object, AttributeSet>(){
            public AttributeSet apply(Object o) {
                Integer i = (Integer) o;
                return Styles.DEFAULT_POINT_STYLE.copy()
                        .and(Styles.FILL, Color.lightGray)
                        .and(Styles.STROKE, Color.gray)
                        .and(Styles.STROKE_WIDTH, .5f)
                        .and(Styles.MARKER_RADIUS, 2+Math.sqrt(i));
            }
        });

        plot.getAdapter().getNodeStyler().setLabelDelegate(Functions.toStringFunction());
        
        plot.addContextMenuInitializer("Graph", new ContextMenuInitializer<Graphic>(){
            public void initContextMenu(JPopupMenu menu, Graphic src, Point2D point, Object focus, Set selection) {
                if (menu.getComponentCount() > 0) {
                    menu.addSeparator();
                }
                if (focus != null) {
                    menu.add("Graph Focus: " + focus);
                }
                menu.add("Selection: " + (selection == null ? 0 : selection.size()) + " selected items");
            }
        });
        plot.addContextMenuInitializer("Node", new ContextMenuInitializer<Graphic>(){
            public void initContextMenu(JPopupMenu menu, Graphic src, Point2D point, Object focus, Set selection) {
                if (menu.getComponentCount() > 0) {
                    menu.addSeparator();
                }
                if (focus != null) {
                    menu.add("Node: " + focus);
                }
            }
        });
        plot.addContextMenuInitializer("Link", new ContextMenuInitializer<Graphic>(){
            public void initContextMenu(JPopupMenu menu, Graphic src, Point2D point, Object focus, Set selection) {
                if (menu.getComponentCount() > 0) {
                    menu.addSeparator();
                }
                if (focus != null) {
                    menu.add("Link: " + focus);
                }
            }
        });


        // PANELS

        rollupPanel1.add("Energy Layout", PropertySheet.forBean(energyLayout = new SpringLayout(
                plot.getLayoutManager().getNodeLocationCopy()
                )));
        for (Graphic p : plot.getGraphicRoot().getGraphics()) {
            rollupPanel1.add(p.toString(), PropertySheet.forBean(p));
        }
        
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                GAInstrument.print(System.out, 50);
            }
        });
        
        // OVERLAY
        
        plot.getOverlays().add(new CanvasPainter<Graphics2D>(){
            public void paint(Component component, Graphics2D canvas) {
                canvas.setColor(Color.black);
                canvas.drawLine(50, 50, 150, 50);
                canvas.drawLine(50, 40, 50, 60);
                canvas.drawLine(150, 40, 150, 60);
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        randomLB = new javax.swing.JButton();
        circleLB = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        energyIB = new javax.swing.JButton();
        energyAB = new javax.swing.JButton();
        energySB = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        rollupPanel1 = new RollupPanel();
        plot = new GraphComponent();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));

        jToolBar1.setRollover(true);

        randomLB.setText("Random Layout");
        randomLB.setFocusable(false);
        randomLB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        randomLB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        randomLB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomLBActionPerformed(evt);
            }
        });
        jToolBar1.add(randomLB);

        circleLB.setText("Circle Layout");
        circleLB.setFocusable(false);
        circleLB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        circleLB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        circleLB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                circleLBActionPerformed(evt);
            }
        });
        jToolBar1.add(circleLB);
        jToolBar1.add(jSeparator1);

        jLabel1.setText("ENERGY:");
        jToolBar1.add(jLabel1);

        energyIB.setText("iterate");
        energyIB.setFocusable(false);
        energyIB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        energyIB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        energyIB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                energyIBActionPerformed(evt);
            }
        });
        jToolBar1.add(energyIB);

        energyAB.setText("animate");
        energyAB.setFocusable(false);
        energyAB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        energyAB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        energyAB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                energyABActionPerformed(evt);
            }
        });
        jToolBar1.add(energyAB);

        energySB.setText("stop");
        energySB.setFocusable(false);
        energySB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        energySB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        energySB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                energySBActionPerformed(evt);
            }
        });
        jToolBar1.add(energySB);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setViewportView(rollupPanel1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.EAST);
        getContentPane().add(plot, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void randomLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomLBActionPerformed
        updateEL = true;
        plot.getLayoutManager().applyLayout(StaticGraphLayout.RANDOM, Collections.EMPTY_MAP, Collections.EMPTY_SET, SpringLayout.DEFAULT_DIST_SCALE*2);
    }//GEN-LAST:event_randomLBActionPerformed

    private void circleLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_circleLBActionPerformed
        updateEL = true;
        plot.getLayoutManager().applyLayout(StaticGraphLayout.CIRCLE, Collections.EMPTY_MAP, Collections.EMPTY_SET, SpringLayout.DEFAULT_DIST_SCALE*2);
    }//GEN-LAST:event_circleLBActionPerformed

    private void energyIBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energyIBActionPerformed
        if (energyLayout == null)
            energyLayout = new SpringLayout(plot.getLayoutManager().getNodeLocationCopy());
        plot.getLayoutManager().setLayoutAlgorithm(energyLayout);
        plot.getLayoutManager().iterateLayout();
        updateEL = false;
    }//GEN-LAST:event_energyIBActionPerformed

    private void energyABActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energyABActionPerformed
        if (energyLayout == null)
            energyLayout = new SpringLayout(plot.getLayoutManager().getNodeLocationCopy());
        plot.getLayoutManager().setLayoutAlgorithm(energyLayout);
        plot.getLayoutManager().setLayoutTaskActive(true);
    }//GEN-LAST:event_energyABActionPerformed

    private void energySBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energySBActionPerformed
        plot.getLayoutManager().setLayoutTaskActive(false);
    }//GEN-LAST:event_energySBActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GraphTestFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton circleLB;
    private javax.swing.JButton energyAB;
    private javax.swing.JButton energyIB;
    private javax.swing.JButton energySB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private GraphComponent plot;
    private javax.swing.JButton randomLB;
    private RollupPanel rollupPanel1;
    // End of variables declaration//GEN-END:variables
}
