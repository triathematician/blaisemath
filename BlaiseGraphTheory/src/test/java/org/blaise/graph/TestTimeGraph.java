/*
 * TestPlaneVisometry.java
 * Created on Jul 30, 2009, 3:15:03 PM
 */
package org.blaise.graph;

import org.blaise.firestarter.PropertySheet;
import org.blaise.firestarter.editor.EditorRegistration;
import org.blaise.graph.dynamic.TimeGraph;
import org.blaise.graph.dynamic.view.MultiGraphComponent;
import org.blaise.graph.dynamic.view.TimeGraphComponent;
import org.blaise.graph.layout.SpringLayout;
import org.blaise.graph.layout.StaticGraphLayout;
import org.blaise.graph.modules.EdgeCountBuilder;
import org.blaise.graph.modules.PreferentialAttachment;
import org.blaise.graph.view.PlaneGraphAdapter;
import org.blaise.util.gui.RollupPanel;

/**
 *
 * @author ae3263
 */
public class TestTimeGraph extends javax.swing.JFrame {

    PlaneGraphAdapter pga;
    /** Flag for when el needs points updated */
    boolean updateEL = true;
    SpringLayout energyLayout;
    
    MultiGraphComponent multi;

    /** Creates new form TestPlaneVisometry */
    public TestTimeGraph() {
        EditorRegistration.registerEditors();
        initComponents();
        // BASIC ELEMENTS
        TimeGraph<Integer> g = new PreferentialAttachment(
                new EdgeCountBuilder(false, 10, 10).createGraph(),
                100, new float[]{0,.95f,.05f}).createTimeGraph();
        plot.setTimeGraph(g);
        plot.getManager().initLayoutAlgorithm();

        multi = new MultiGraphComponent(plot.getManager());
        jScrollPane2.setViewportView(multi);
        
        // PANELS

        rollupPanel1.add("Visometry", new PropertySheet(plot.getGraphComponent().getVisometry()));
        rollupPanel1.add("Nodes", new PropertySheet(plot.getGraphComponent().getAdapter().getNodeStyler()));
        rollupPanel1.add("Edges", new PropertySheet(plot.getGraphComponent().getAdapter().getEdgeStyler()));
        
        rollupPanel1.add("Simultaneous Layout", new PropertySheet(plot.getManager().getLayoutAlgorithm().getParameters()));
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
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        timeEnergyIB = new javax.swing.JButton();
        timeEnergyAB = new javax.swing.JButton();
        timeEnergySB = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        rollupPanel1 = new RollupPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        plot = new TimeGraphComponent();
        jScrollPane2 = new javax.swing.JScrollPane();

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
        jToolBar1.add(jSeparator2);

        jLabel2.setText("SIMUL:");
        jToolBar1.add(jLabel2);

        timeEnergyIB.setText("iterate");
        timeEnergyIB.setFocusable(false);
        timeEnergyIB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        timeEnergyIB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        timeEnergyIB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeEnergyIBActionPerformed(evt);
            }
        });
        jToolBar1.add(timeEnergyIB);

        timeEnergyAB.setText("animate");
        timeEnergyAB.setFocusable(false);
        timeEnergyAB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        timeEnergyAB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        timeEnergyAB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeEnergyABActionPerformed(evt);
            }
        });
        jToolBar1.add(timeEnergyAB);

        timeEnergySB.setText("stop");
        timeEnergySB.setFocusable(false);
        timeEnergySB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        timeEnergySB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        timeEnergySB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeEnergySBActionPerformed(evt);
            }
        });
        jToolBar1.add(timeEnergySB);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setViewportView(rollupPanel1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.EAST);

        jTabbedPane1.addTab("TimeGraphComponent", plot);
        jTabbedPane1.addTab("MultiGraphComponent", jScrollPane2);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void randomLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomLBActionPerformed
        updateEL = true;
        plot.getGraphManager().applyLayout(StaticGraphLayout.RANDOM, 5.0);
    }//GEN-LAST:event_randomLBActionPerformed

    private void circleLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_circleLBActionPerformed
        updateEL = true;
        plot.getGraphManager().applyLayout(StaticGraphLayout.CIRCLE, 5.0);
    }//GEN-LAST:event_circleLBActionPerformed

    private void energyIBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energyIBActionPerformed
        if (energyLayout == null)
            energyLayout = new SpringLayout(plot.getGraphManager().getLocations());
        plot.getGraphManager().setLayoutAlgorithm(energyLayout);
        plot.getManager().iterateLayout();
        updateEL = false;
    }//GEN-LAST:event_energyIBActionPerformed

    private void energyABActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energyABActionPerformed
        if (energyLayout == null)
            energyLayout = new SpringLayout(plot.getGraphManager().getLocations());
        plot.getGraphManager().setLayoutAlgorithm(energyLayout);
        plot.getManager().startLayoutTask(10, 2);
    }//GEN-LAST:event_energyABActionPerformed

    private void energySBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_energySBActionPerformed
        plot.getManager().stopLayoutTask();
    }//GEN-LAST:event_energySBActionPerformed

    private void timeEnergyIBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeEnergyIBActionPerformed
        if (energyLayout == null)
            energyLayout = new SpringLayout(plot.getGraphManager().getLocations());
        plot.getManager().iterateLayout();
        updateEL = false;
    }//GEN-LAST:event_timeEnergyIBActionPerformed

    private void timeEnergyABActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeEnergyABActionPerformed
        if (energyLayout == null)
            energyLayout = new SpringLayout(plot.getGraphManager().getLocations());
        plot.getManager().startLayoutTask(10, 2);
    }//GEN-LAST:event_timeEnergyABActionPerformed

    private void timeEnergySBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeEnergySBActionPerformed
        plot.getManager().stopLayoutTask();
    }//GEN-LAST:event_timeEnergySBActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestTimeGraph().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton circleLB;
    private javax.swing.JButton energyAB;
    private javax.swing.JButton energyIB;
    private javax.swing.JButton energySB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private TimeGraphComponent plot;
    private javax.swing.JButton randomLB;
    private RollupPanel rollupPanel1;
    private javax.swing.JButton timeEnergyAB;
    private javax.swing.JButton timeEnergyIB;
    private javax.swing.JButton timeEnergySB;
    // End of variables declaration//GEN-END:variables
}
