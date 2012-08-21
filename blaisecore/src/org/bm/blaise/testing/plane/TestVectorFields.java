package org.bm.blaise.testing.plane;

/*
 * TestPlaneVisometry.java
 *
 * Created on Jul 30, 2009, 3:15:03 PM
 */


import org.bm.blaise.specto.plane.diffeq.PlaneVectorField;
import data.propertysheet.PropertySheet;
import java.awt.geom.Point2D;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.sequor.timer.BetterTimeClock;
import org.bm.blaise.specto.plane.*;
import org.bm.blaise.specto.plane.diffeq.*;
import org.bm.blaise.specto.plane.function.PlaneParametricCurve;
import org.bm.blaise.specto.visometry.Plottable;
import org.bm.utils.Curve2DSampleSet;
import scio.function.utils.VectorFieldUtils;

/**
 *
 * @author ae3263
 */
public class TestVectorFields extends javax.swing.JFrame {

    BetterTimeClock timer;

    /** Creates new form TestPlaneVisometry */
    public TestVectorFields() {
        data.propertysheet.editor.EditorRegistration.registerEditors();
        timer = new BetterTimeClock();
        initComponents();

        // FUNCTIONS

        MultivariateVectorialFunction func6 = // field
                new MultivariateVectorialFunction() {
                    public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                        return new double[]{point[1], -point[0]};
//                        return new double[]{Math.cos(point[1]) + point[0], Math.sin(point[0]) + point[1]};
                    }
                };

        UnivariateVectorialFunction func7 = new UnivariateVectorialFunction() { // quadratic
                    public double[] value(double x) throws FunctionEvaluationException {
                        return new double[]{x, x*x};
                    }
                };
        MultivariateVectorialFunction func7der = new MultivariateVectorialFunction() { // quadratic parallel field
            public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                return new double[]{1, 2*point[0]};
            }
        };
        UnivariateVectorialFunction func8 = new UnivariateVectorialFunction() {
                    public double[] value(double x) throws FunctionEvaluationException {
                        return new double[]{2-3*x, 4-3*x};
                    }
                };
        MultivariateVectorialFunction func8der = new MultivariateVectorialFunction() { // quadratic parallel field
            public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                return new double[]{-1, -1};
            }
        };




        // DIFF EQ'S

        PlaneParametricCurve ppf = new PlaneParametricCurve(func7, -1.0, 2.0);
        PlaneParametricCurve ppf2 = new PlaneParametricCurve(func8 , 0.0, 1.0);


        CurveDEPlot.addPlottable(new PlaneAxes());
        CurveDEPlot.addPlottable(new PlaneVectorField(func6, CurveDEPlot.getPlotSampleSetGenerator()));
        PlanePaddlePoint ppp = new PlanePaddlePoint(func6);
        for (double i = -4; i <= 4; i+=1) {
            for (double j = -4; j <= 4; j+=1) {
                ppp.addValue(new Point2D.Double(i,j));
            }
        }
        CurveDEPlot.addPlottable(ppp);
        CurveDEPlot.setTimeClock(timer);

        CurveDEPlot2.addPlottable(new PlaneAxes());
        CurveDEPlot2.addPlottable(ppf);
        CurveDEPlot2.addPlottable(ppf2);
        CurveDEPlot2.addPlottable(new PlaneVectorField(func6, CurveDEPlot2.getPlotSampleSetGenerator()));
        CurveDEPlot2.addPlottable(new PlaneVectorField(func6, new Curve2DSampleSet(ppf.getFunction(), ppf.getDomain())));
        CurveDEPlot2.addPlottable(new PlaneVectorField(
                VectorFieldUtils.parallelField(func6, func7der),
                new Curve2DSampleSet(ppf.getFunction(), ppf.getDomain())));
        CurveDEPlot2.addPlottable(new PlaneVectorField(func6, new Curve2DSampleSet(ppf2.getFunction(), ppf2.getDomain())));
        CurveDEPlot2.addPlottable(new PlaneVectorField(
                VectorFieldUtils.parallelField(func6, func8der),
                new Curve2DSampleSet(ppf2.getFunction(), ppf2.getDomain())));

        // PANELS

        rollupPanel1.add("Timer", new PropertySheet(timer));
        rollupPanel1.add("Visometry", new PropertySheet(CurveDEPlot.getVisometry()));
        for (Plottable p : CurveDEPlot.getPlottables()) {
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
        CurveDEPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        CurveDEPlot2 = new org.bm.blaise.specto.plane.PlanePlotComponent();

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

        org.jdesktop.layout.GroupLayout CurveDEPlotLayout = new org.jdesktop.layout.GroupLayout(CurveDEPlot);
        CurveDEPlot.setLayout(CurveDEPlotLayout);
        CurveDEPlotLayout.setHorizontalGroup(
            CurveDEPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 567, Short.MAX_VALUE)
        );
        CurveDEPlotLayout.setVerticalGroup(
            CurveDEPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 370, Short.MAX_VALUE)
        );

        tabPane.addTab("Curve (Flow)", CurveDEPlot);

        org.jdesktop.layout.GroupLayout CurveDEPlot2Layout = new org.jdesktop.layout.GroupLayout(CurveDEPlot2);
        CurveDEPlot2.setLayout(CurveDEPlot2Layout);
        CurveDEPlot2Layout.setHorizontalGroup(
            CurveDEPlot2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 567, Short.MAX_VALUE)
        );
        CurveDEPlot2Layout.setVerticalGroup(
            CurveDEPlot2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 370, Short.MAX_VALUE)
        );

        tabPane.addTab("Curve (Field)", CurveDEPlot2);

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
                new TestVectorFields().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.bm.blaise.specto.plane.PlanePlotComponent CurveDEPlot;
    private org.bm.blaise.specto.plane.PlanePlotComponent CurveDEPlot2;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private gui.RollupPanel rollupPanel1;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables
}
