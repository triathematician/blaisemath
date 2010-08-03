/*
 * TestPlaneVisometry.java
 *
 * Created on Jul 30, 2009, 3:15:03 PM
 */
package org.bm.blaise.testing.plane;

import data.propertysheet.PropertySheet;
import data.propertysheet.editor.EditorRegistration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.Graphs;
import org.bm.blaise.scio.graph.RandomGraph;
import org.bm.blaise.scio.graph.layout.EnergyLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;
import org.bm.blaise.specto.plane.graph.PlaneGraph;
import stormtimer.BetterTimer;
import visometry.plane.PlanePlotComponent;
import visometry.plottable.Plottable;

/**
 *
 * @author ae3263
 */
public class TestGraph extends javax.swing.JFrame {

    PlanePlotComponent graphPlot;
    PlaneGraph pg;
    /** Flag for when el needs points updated */
    boolean updateEL = true;
    EnergyLayout energyLayout;

    /** Creates new form TestPlaneVisometry */
    public TestGraph() {
        EditorRegistration.registerEditors();
        initComponents();
        graphPlot = new PlanePlotComponent();
        tabPane.add("Graph Plot", graphPlot);

        // BASIC ELEMENTS

        graphPlot.add(pg = new PlaneGraph(RandomGraph.getInstance(70, 100, false)));
        graphPlot.setDesiredRange(-5.0, -5.0, 5.0, 5.0);

        // PANELS

        rollupPanel1.add("Visometry", new PropertySheet(graphPlot.getVisometry()));
        rollupPanel1.add("Energy Layout", new PropertySheet(energyLayout = new EnergyLayout(pg.getPositionMap())));
        for (Plottable p : graphPlot.getPlottableArray()) {
            rollupPanel1.add(p.toString(), new PropertySheet(p));
        }
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
        rollupPanel1 = new gui.RollupPanel();
        tabPane = new javax.swing.JTabbedPane();

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

        tabPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabPaneStateChanged(evt);
            }
        });
        getContentPane().add(tabPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void randomLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomLBActionPerformed
        updateEL = true;
        pg.setPositionMap(StaticGraphLayout.RANDOM.layout(pg.getGraph(), 5.0));
    }//GEN-LAST:event_randomLBActionPerformed

    private void tabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPaneStateChanged
        rollupPanel1.removeAll();
        PlanePlotComponent ppc = (PlanePlotComponent) tabPane.getSelectedComponent();
        rollupPanel1.add("Visometry", new PropertySheet(ppc.getVisometry()));
        for (Plottable p : ppc.getPlottableArray())
            rollupPanel1.add(p.toString(), new PropertySheet(p));
}//GEN-LAST:event_tabPaneStateChanged

    private void circleLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_circleLBActionPerformed
        updateEL = true;
        pg.setPositionMap(StaticGraphLayout.CIRCLE.layout(pg.getGraph(), 5.0));
    }//GEN-LAST:event_circleLBActionPerformed

    private void energyIBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energyIBActionPerformed
        Graph graph = pg.getGraph();
        if (energyLayout == null)
            energyLayout = new EnergyLayout(pg.getPositionMap());
        else if (updateEL)
            energyLayout.reset(pg.getPositionMap());
        energyLayout.iterate(graph);
        pg.setPositionMap(energyLayout.getPositions());
        updateEL = false;
    }//GEN-LAST:event_energyIBActionPerformed

    BetterTimer timer;

    private void energyABActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energyABActionPerformed
        final Graph graph = pg.getGraph();
        if (energyLayout == null)
            energyLayout = new EnergyLayout(pg.getPositionMap());
        else if (updateEL)
            energyLayout.reset(pg.getPositionMap());
        timer = new BetterTimer(100);
        timer.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (updateEL)
                    energyLayout.reset(pg.getPositionMap());
                energyLayout.iterate(graph);
                pg.setPositionMap(energyLayout.getPositions());
            }
        });
        timer.start();
    }//GEN-LAST:event_energyABActionPerformed

    private void energySBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energySBActionPerformed
        if (timer != null)
            timer.stop();
        timer = null;
    }//GEN-LAST:event_energySBActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TestGraph().setVisible(true);
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
    private javax.swing.JButton randomLB;
    private gui.RollupPanel rollupPanel1;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables
}
