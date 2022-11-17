package com.googlecode.blaisemath.graph.test;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
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

import com.google.common.base.Functions;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.geom.Points;
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator;
import com.googlecode.blaisemath.graph.generate.EdgeLikelihoodGenerator.EdgeLikelihoodParameters;
import com.googlecode.blaisemath.graph.layout.CircleLayout;
import com.googlecode.blaisemath.graph.layout.CircleLayout.CircleLayoutParameters;
import com.googlecode.blaisemath.graph.layout.RandomBoxLayout;
import com.googlecode.blaisemath.graph.layout.RandomBoxLayout.BoxLayoutParameters;
import com.googlecode.blaisemath.graph.layout.SpringLayout;
import com.googlecode.blaisemath.graph.layout.SpringLayoutParameters;
import com.googlecode.blaisemath.graph.view.GraphComponent;
import com.googlecode.blaisemath.graph.view.VisualGraph;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.Instrument;
import com.googlecode.blaisemath.util.swing.CanvasPainter;
import com.googlecode.blaisemath.firestarter.editor.EditorRegistration;
import com.googlecode.blaisemath.firestarter.property.PropertySheet;
import com.googlecode.blaisemath.firestarter.swing.RollupPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

@SuppressWarnings("ALL")
public class GraphTestFrame extends javax.swing.JFrame {

    VisualGraph pga;
    /** Flag for when el needs points updated */
    private boolean updateEL = true;
    private SpringLayout energyLayout;
    private final SpringLayoutParameters layoutParams;

    /** Creates new form TestPlaneVisometry */
    private GraphTestFrame() {
        EditorRegistration.registerEditors();
        initComponents();

        // BASIC ELEMENTS

        final Graph<Integer> graph = new EdgeLikelihoodGenerator().apply(new EdgeLikelihoodParameters(false, 50, .05f));
        plot.setGraph(graph);
        plot.getAdapter().getViewGraph().setPointSelectionEnabled(true);
        plot.getAdapter().getViewGraph().setDragEnabled(true);
        plot.getLayoutManager().applyLayout(CircleLayout.getInstance(), null, new CircleLayoutParameters(100.0));
        PanAndZoomHandler.zoomBoxAnimated(plot, Points.boundingBox(plot.getLayoutManager().getNodeLocationCopy().values(), 5));
        plot.getAdapter().getNodeStyler().setStyleDelegate(o -> Styles.DEFAULT_POINT_STYLE.copy()
                    .and(Styles.FILL, Color.lightGray)
                    .and(Styles.STROKE, Color.gray)
                    .and(Styles.STROKE_WIDTH, .5f)
                    .and(Styles.MARKER_RADIUS, 2+Math.sqrt((Integer) o)));

        plot.getAdapter().getNodeStyler().setLabelDelegate(Functions.toStringFunction());
        
        plot.addContextMenuInitializer("Graph", (menu, src, point, focus, selection) -> {
            if (menu.getComponentCount() > 0) {
                menu.addSeparator();
            }
            if (focus != null) {
                menu.add("Graph Focus: " + focus);
            }
            menu.add("Selection: " + (selection == null ? 0 : selection.size()) + " selected items");
        });
        plot.addContextMenuInitializer("Node", (menu, src, point, focus, selection) -> {
            if (menu.getComponentCount() > 0) {
                menu.addSeparator();
            }
            if (focus != null) {
                menu.add("Node: " + focus);
            }
        });
        plot.addContextMenuInitializer("Edge", (menu, src, point, focus, selection) -> {
            if (menu.getComponentCount() > 0) {
                menu.addSeparator();
            }
            if (focus != null) {
                menu.add("Edge: " + focus);
            }
        });


        // PANELS

        energyLayout = new SpringLayout();
        layoutParams = energyLayout.createParameters();
        rollupPanel1.add("Energy Layout", PropertySheet.forBean(layoutParams));
        for (Graphic p : plot.getGraphicRoot().getGraphics()) {
            rollupPanel1.add(p.toString(), PropertySheet.forBean(p));
        }
        
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Instrument.print(System.out, 50);
            }
        });
        
        // OVERLAY
        
        plot.getOverlays().add((CanvasPainter<Graphics2D>) (component, canvas) -> {
            canvas.setColor(Color.black);
            canvas.drawLine(50, 50, 150, 50);
            canvas.drawLine(50, 40, 50, 60);
            canvas.drawLine(150, 40, 150, 60);
        });
        
        plot.getSelectionModel().addPropertyChangeListener(evt -> selectionL.setText(String.format("<html><b>Selection:</b> %s", evt.getNewValue())));
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
        selectionL = new JLabel();

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
        getContentPane().add(selectionL, BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void randomLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomLBActionPerformed
        updateEL = true;
        double d = SpringLayoutParameters.DEFAULT_DIST_SCALE*2;
        plot.getLayoutManager().applyLayout(RandomBoxLayout.getInstance(), null, new BoxLayoutParameters(new Rectangle2D.Double(-d, -d, 2*d, 2*d)));
    }//GEN-LAST:event_randomLBActionPerformed

    private void circleLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_circleLBActionPerformed
        updateEL = true;
        plot.getLayoutManager().applyLayout(CircleLayout.getInstance(), null, new CircleLayoutParameters(SpringLayoutParameters.DEFAULT_DIST_SCALE*2));
    }//GEN-LAST:event_circleLBActionPerformed

    private void energyIBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energyIBActionPerformed
        if (energyLayout == null) {
            energyLayout = new SpringLayout();
        }
        plot.getLayoutManager().setLayoutAlgorithm(energyLayout);
        plot.getLayoutManager().setLayoutParameters(layoutParams);
        plot.getLayoutManager().iterateLayout();
        updateEL = false;
    }//GEN-LAST:event_energyIBActionPerformed

    private void energyABActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energyABActionPerformed
        if (energyLayout == null) {
            energyLayout = new SpringLayout();
        }
        plot.getLayoutManager().setLayoutAlgorithm(energyLayout);
        plot.getLayoutManager().setLayoutParameters(layoutParams);
        plot.getLayoutManager().setLayoutTaskActive(true);
    }//GEN-LAST:event_energyABActionPerformed

    private void energySBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energySBActionPerformed
        plot.getLayoutManager().setLayoutTaskActive(false);
    }//GEN-LAST:event_energySBActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new GraphTestFrame().setVisible(true));
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
    private JLabel selectionL;
    // End of variables declaration//GEN-END:variables
}
