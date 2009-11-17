/*
 * TestPlaneVisometry.java
 *
 * Created on Jul 30, 2009, 3:15:03 PM
 */
package org.bm.blaise.testing.plane;

import org.bm.blaise.specto.plane.function.PlaneRandomPointsImplicitFunction;
import data.propertysheet.PropertySheet;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.sequor.component.BClock;
import org.bm.blaise.specto.plane.*;
import org.bm.blaise.specto.plane.function.PlaneParametricFunction;
import org.bm.blaise.specto.plane.function.PlaneSurfaceFunction;
import org.bm.blaise.specto.visometry.Plottable;

/**
 *
 * @author ae3263
 */
public class TestSurfacePlots extends javax.swing.JFrame {

    BClock timer;

    /** Creates new form TestPlaneVisometry */
    public TestSurfacePlots() {
        data.beans.EditorRegistration.registerEditors();
        timer = new BClock();
        initComponents();


        // FUNCTIONS

        sfcPlot.addPlottable(new PlaneGrid());
        sfcPlot.addPlottable(new PlaneAxes());
        sfcPlot.setClockTimer(timer);

        MultivariateRealFunction func = new MultivariateRealFunction(){
            public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                return point[0] + Math.sin(point[1]); // f(x,y) = x + sin(y)
            // return Math.cos(point[0]) * Math.sin(point[1]);
            }
        };

        UnivariateVectorialFunction curve = new UnivariateVectorialFunction(){
            public double[] value(double x) throws FunctionEvaluationException {
                return new double[]{ 3 * Math.cos(x), 3 * Math.sin(x) };
            }
        };

        PlaneParametricFunction ppf = new PlaneParametricFunction(curve, 0.0, 6.28, 0.1);

        sfcPlot.addPlottable(ppf);
        sfcPlot.addPlottable(new PlaneSurfaceFunction(func, sfcPlot.getPlotSampleSetGenerator()));

//        sfcPlot.addPlottable(new PlaneParticleVectorField(
//                new MultivariateVectorialFunction() {
//                    public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
//                        return new double[]{Math.cos(point[1]), -1};
//                    }
//                }));

                
//        sfcPlot.addPlottable(new PlaneDE2Solution(
//                new Point2D.Double(0.2, -0.2),
//                new MultivariateRealFunction() {
//                    public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
//                        return Math.cos(point[2]); // point is (t, x, y), so this is saying x' = y.
//                    }
//                },
//                new MultivariateRealFunction() {
//                    public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
//                        return -1.0;
//                    }
//                },
//                0.0, 10.0));
//        sfcPlot.addPlottable(new PlaneDE2Solution(
//                new Point2D.Double(0.2, -0.2),
//                new MultivariateRealFunction() {
//                    public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
//                        return Math.cos(point[2]); // point is (t, x, y), so this is saying x' = y.
//                    }
//                },
//                new MultivariateRealFunction() {
//                    public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
//                        return -1.0;
//                    }
//                },
//                0.0, 10.0));


        // FUNCTIONS - IMPLICIT

        sfcPlot2.addPlottable(new PlaneAxes());
        sfcPlot2.addPlottable(new PlaneRandomPointsImplicitFunction(new MultivariateRealFunction() {

            public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                return Math.sin(point[0]) * Math.cos(point[1]) + .5;
            }
        }, new double[]{0}));

        
        // PANELS

        rollupPanel1.add("Timer", new PropertySheet(timer));
        rollupPanel1.add("Visometry", new PropertySheet(sfcPlot.getVisometry()));
        for (Plottable p : sfcPlot.getPlottables()) {
            rollupPanel1.add(p.toString(), new PropertySheet(p));
        }
//        rollupPanel1.add("Point Style", new PropertySheet(planePlotComponent1.getVisometryGraphics().getPointStyle()));
//        rollupPanel1.add("Stroke Style", new PropertySheet(planePlotComponent1.getVisometryGraphics().getPathStyle()));
//        rollupPanel1.add("Text Style", new PropertySheet(planePlotComponent1.getVisometryGraphics().getTextStyle()));
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
        sfcPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        sfcPlot2 = new org.bm.blaise.specto.plane.PlanePlotComponent();

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

        org.jdesktop.layout.GroupLayout sfcPlotLayout = new org.jdesktop.layout.GroupLayout(sfcPlot);
        sfcPlot.setLayout(sfcPlotLayout);
        sfcPlotLayout.setHorizontalGroup(
            sfcPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 567, Short.MAX_VALUE)
        );
        sfcPlotLayout.setVerticalGroup(
            sfcPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 370, Short.MAX_VALUE)
        );

        tabPane.addTab("Surface", sfcPlot);

        org.jdesktop.layout.GroupLayout sfcPlot2Layout = new org.jdesktop.layout.GroupLayout(sfcPlot2);
        sfcPlot2.setLayout(sfcPlot2Layout);
        sfcPlot2Layout.setHorizontalGroup(
            sfcPlot2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 567, Short.MAX_VALUE)
        );
        sfcPlot2Layout.setVerticalGroup(
            sfcPlot2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 370, Short.MAX_VALUE)
        );

        tabPane.addTab("Implicit", sfcPlot2);

        getContentPane().add(tabPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.out.println("Here");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPaneStateChanged
        rollupPanel1.removeAll();
        PlanePlotComponent ppc = (PlanePlotComponent) tabPane.getSelectedComponent();
        rollupPanel1.add("Timer", new PropertySheet(timer));
        rollupPanel1.add("Visometry", new PropertySheet(ppc.getVisometry()));
        for (Plottable p : ppc.getPlottables()) {
            rollupPanel1.add(p.toString(), new PropertySheet(p));
        }
}//GEN-LAST:event_tabPaneStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TestSurfacePlots().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private gui.RollupPanel rollupPanel1;
    private org.bm.blaise.specto.plane.PlanePlotComponent sfcPlot;
    private org.bm.blaise.specto.plane.PlanePlotComponent sfcPlot2;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables

}
