/*
 * TestPlaneVisometry.java
 *
 * Created on Jul 30, 2009, 3:15:03 PM
 */
package org.bm.blaise.testing;

import data.propertysheet.PropertySheet;
import data.propertysheet.editor.FunctionEditor;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bm.blaise.scio.function.ParsedUnivariateRealFunction;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.specto.plane.PlaneAxes;
import org.bm.blaise.specto.plane.PlaneGrid;
import org.bm.blaise.specto.plane.function.PlaneFunctionGraph;
import org.bm.blaise.specto.plane.geometry.PlaneBezierCurve;
import org.bm.blaise.specto.plane.geometry.PlaneEllipse;
import org.bm.blaise.specto.plane.geometry.PlaneTriangle;
import org.bm.blaise.specto.plottable.*;
import org.bm.blaise.specto.visometry.PlotComponent;
import org.bm.blaise.specto.visometry.Plottable;

/**
 *
 * @author ae3263
 */
public class TestBasicPlane extends javax.swing.JFrame {

    /** Creates new form TestPlaneVisometry */
    public TestBasicPlane() {
        data.propertysheet.editor.EditorRegistration.registerEditors();
        FunctionEditor.register();
        initComponents();

        // BASIC ELEMENTS

        testPlot.addPlottable(new VClock<Point2D.Double>(new Point2D.Double(2,3)));
        PlaneBezierCurve bezier = new PlaneBezierCurve(new Point2D.Double(1,0), new Point2D.Double(2,0));
        testPlot.addPlottable(bezier);
        testPlot.setDefaultCoordinateHandler(bezier);

        VPointSet<Point2D.Double> vps = new VPointSet<Point2D.Double>(new Point2D.Double[]{
            new Point2D.Double(-3.0,1.0), new Point2D.Double(-2.8,1.2), new Point2D.Double(-2.5, 1.5)
        });


        genericPlot.addPlottable(new VPoint<Point2D.Double>(new Point2D.Double(.2, -.2)));
        genericPlot.addPlottable(vps);
        genericPlot.addPlottable(new VRectangle<Point2D.Double>(new Point2D.Double(.5, -2), new Point2D.Double(1, -1)));
        genericPlot.addPlottable(new VPath<Point2D.Double>(new Point2D.Double[]{
                    new Point2D.Double(-.5, 1.1), new Point2D.Double(-.5, 2.1), new Point2D.Double(-1.0, 0.7)
                }));
        genericPlot.addPlottable(new VLine<Point2D.Double>(new Point2D.Double(1, 0), new Point2D.Double(0, 1)));
        genericPlot.addPlottable(new VLabel<Point2D.Double>(new Point2D.Double(2.5, 1.0), "Label!"));
        genericPlot.addPlottable(new VPolygon<Point2D.Double>(new Point2D.Double[]{
            new Point2D.Double(1.0,1.0), new Point2D.Double(1.2,1.2), new Point2D.Double(1.0,1.4), new Point2D.Double(0.8,1.2)
        }));
        genericPlot.setDesiredRange(-5.0, -5.0, 5.0, 5.0);
        genericPlot.setDefaultCoordinateHandler(vps);

        geometryPlot.addPlottable(new PlaneAxes());
        geometryPlot.addPlottable(new PlaneTriangle());
        geometryPlot.addPlottable(new PlaneEllipse());

        // FUNCTIONS

        funPlot.addPlottable(new PlaneGrid());
        funPlot.addPlottable(new PlaneAxes());
        try {
            funPlot.addPlottable(new PlaneFunctionGraph(new ParsedUnivariateRealFunction("cos(x)", "x")));
        } catch (ParseException ex) {
            Logger.getLogger(TestBasicPlane.class.getName()).log(Level.SEVERE, null, ex);
        }
        funPlot.setDesiredRange(-5.0, -1.0, 5.0, 1.0);

        // PANELS
        
        rollupPanel1.removeAll();
        rollupPanel1.add("Visometry", new PropertySheet(testPlot.getVisometry()));
        for (Plottable p : testPlot.getPlottables())
            rollupPanel1.add(p.toString(), new PropertySheet(p));
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
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        rollupPanel1 = new gui.RollupPanel();
        tabPane = new javax.swing.JTabbedPane();
        testPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        genericPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        geometryPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        funPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));

        jToolBar1.setRollover(true);

        jButton1.setText("jButton1");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setViewportView(rollupPanel1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.EAST);

        tabPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabPaneStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout testPlotLayout = new org.jdesktop.layout.GroupLayout(testPlot);
        testPlot.setLayout(testPlotLayout);
        testPlotLayout.setHorizontalGroup(
            testPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 567, Short.MAX_VALUE)
        );
        testPlotLayout.setVerticalGroup(
            testPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 366, Short.MAX_VALUE)
        );

        tabPane.addTab("Testing", testPlot);

        org.jdesktop.layout.GroupLayout genericPlotLayout = new org.jdesktop.layout.GroupLayout(genericPlot);
        genericPlot.setLayout(genericPlotLayout);
        genericPlotLayout.setHorizontalGroup(
            genericPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 567, Short.MAX_VALUE)
        );
        genericPlotLayout.setVerticalGroup(
            genericPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 366, Short.MAX_VALUE)
        );

        tabPane.addTab("Generic Elements", genericPlot);

        org.jdesktop.layout.GroupLayout geometryPlotLayout = new org.jdesktop.layout.GroupLayout(geometryPlot);
        geometryPlot.setLayout(geometryPlotLayout);
        geometryPlotLayout.setHorizontalGroup(
            geometryPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 567, Short.MAX_VALUE)
        );
        geometryPlotLayout.setVerticalGroup(
            geometryPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 366, Short.MAX_VALUE)
        );

        tabPane.addTab("Geometric Elements", geometryPlot);

        org.jdesktop.layout.GroupLayout funPlotLayout = new org.jdesktop.layout.GroupLayout(funPlot);
        funPlot.setLayout(funPlotLayout);
        funPlotLayout.setHorizontalGroup(
            funPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 567, Short.MAX_VALUE)
        );
        funPlotLayout.setVerticalGroup(
            funPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 366, Short.MAX_VALUE)
        );

        tabPane.addTab("Basic Function", funPlot);

        getContentPane().add(tabPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.out.println("Here");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPaneStateChanged
        if (tabPane.getSelectedComponent() instanceof PlotComponent) {
            rollupPanel1.removeAll();
            PlotComponent ppc = (PlotComponent) tabPane.getSelectedComponent();
            rollupPanel1.add("Visometry", new PropertySheet(ppc.getVisometry()));
            for (Object p : ppc.getPlottables()) {
                System.out.println("Adding plottable " + p);
                rollupPanel1.add(p.toString(), new PropertySheet(p));
            }
        }
}//GEN-LAST:event_tabPaneStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TestBasicPlane().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.bm.blaise.specto.plane.PlanePlotComponent funPlot;
    private org.bm.blaise.specto.plane.PlanePlotComponent genericPlot;
    private org.bm.blaise.specto.plane.PlanePlotComponent geometryPlot;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private gui.RollupPanel rollupPanel1;
    private javax.swing.JTabbedPane tabPane;
    private org.bm.blaise.specto.plane.PlanePlotComponent testPlot;
    // End of variables declaration//GEN-END:variables
}