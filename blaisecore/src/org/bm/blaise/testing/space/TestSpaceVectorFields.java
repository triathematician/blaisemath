/*
 * TestSpaceVisometry.java
 * Created on Oct 20, 2009, 9:21:37 PM
 */
package org.bm.blaise.testing.space;

import data.propertysheet.PropertySheet;
import java.awt.geom.Point2D;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.scio.function.FlexSpaceCurve;
import org.bm.blaise.scio.function.FlexSpaceField;
import org.bm.blaise.sequor.timer.BetterTimeClock;
import org.bm.blaise.specto.plane.PlanePlotComponent;
import org.bm.blaise.specto.plane.PlaneVisometry;
import org.bm.blaise.specto.space.*;
import org.bm.blaise.specto.space.diffeq.*;
import org.bm.blaise.specto.space.function.*;
import org.bm.blaise.specto.visometry.PlotComponent;
import org.bm.utils.Curve3DSampleSet;
import org.bm.utils.Surface3DSampleSet;
import scio.function.utils.VectorFieldUtils;

/**
 *
 * @author ae3263
 */
public class TestSpaceVectorFields extends javax.swing.JFrame {

    PlaneGraphicsInSpace pgis;
    PlaneVisometry pv;
    PlanePlotComponent ppc;
    BetterTimeClock timer;

    /** Creates new form TestSpaceVisometry */
    public TestSpaceVectorFields() {
        data.propertysheet.editor.EditorRegistration.registerEditors();
        timer = new BetterTimeClock();
        initComponents();

        // 0. The base field to use
        FlexSpaceField field = new FlexSpaceField(
                new MultivariateVectorialFunction() {
                    public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                        return new double[]{point[1], -Math.cos(point[0]*point[1]), -point[2]};
                    }
                });
        SpaceVectorField svf = new SpaceVectorField(field, vecPlot.getSSG());

        // 1. Just a basic vector field and particle flows associated with it
        vecPlot.addPlottable(new SpaceAxes());
        vecPlot.addPlottable(svf);
        vecPlot.setTimeClock(timer);

        // 2. Curve-based field
        FlexSpaceCurve curve = new FlexSpaceCurve(new UnivariateVectorialFunction(){
            public double[] value(double x) throws FunctionEvaluationException {
                return new double[] { 2*Math.cos(x), 2*Math.sin(x), .5 };
            }});
        SpaceParametricCurve spc = new SpaceParametricCurve(curve, 0.0, 2*Math.PI, 100);
        curvePlot.addPlottable(spc);
        spc.setVisible(false);
        SpaceVectorField scfield = new SpaceVectorField(field, new Curve3DSampleSet(curve, spc.getDomain()));
        curve.addChangeListener(scfield);
        curvePlot.addPlottable(scfield);
        curvePlot.setTimeClock(timer);

        MultivariateVectorialFunction spcDer =
                new MultivariateVectorialFunction() {
                    public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                        return new double[] { -point[1], point[0], 0 };
                    }
                };
        curvePlot.addPlottable(new DerivedSpaceField(
                field, spcDer, null,
                new Curve3DSampleSet(spc.getFunction(), spc.getDomain())));

        // 3. Surface-based field
        SpaceParametricSurface sps = new SpaceParametricSurface(
                new MultivariateVectorialFunction() {
                    public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                        return new double[] {
                            Math.cos(point[1]) * Math.cos(point[0]),
                            Math.sin(point[1]) * Math.cos(point[0]),
                            Math.sin(point[0])
                        };
                    }
                },
                new Point2D.Double(-Math.PI/2, 0.0),
                new Point2D.Double(Math.PI/2, 3*Math.PI/2));
        surfacePlot.addPlottable(sps);
        sps.setVisible(false);
        surfacePlot.addPlottable(new SpaceVectorField(field, new Surface3DSampleSet(sps.getFunction(), sps.getDomainU(), sps.getDomainV())));
        surfacePlot.setTimeClock(timer);

        MultivariateVectorialFunction spsDer1 =
                new MultivariateVectorialFunction() {
                    public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                        return new double[] { -point[1], point[0], 0 };
                    }
                };
        MultivariateVectorialFunction spsDer2 =
                new MultivariateVectorialFunction() {
                    public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                        return new double[] {
                            -point[0]*point[2]/Math.sqrt(Math.abs(1-point[2]*point[2])),
                            -point[1]*point[2]/Math.sqrt(Math.abs(1-point[2]*point[2])),
                            Math.sqrt(point[0]*point[0]+point[1]*point[1])
                        };
                    }
                };
//        vecPlot2.addPlottable(new DerivedSpaceField(
//                field1, spsDer1, spsDer2,
//                new Surface3DSampleSet(sps.getFunction(), sps.getDomainU(), sps.getDomainV())));

         MultivariateVectorialFunction tgtFlow = VectorFieldUtils.parallelField(field, spcDer);
         MultivariateVectorialFunction tgtFlow2 = VectorFieldUtils.parallelField(field, spsDer1, spsDer2);
         constrainPlot.addPlottable(spc);
         constrainPlot.addPlottable(sps);
         constrainPlot.setTimeClock(timer);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        vecPlot = new org.bm.blaise.specto.space.SpacePlotComponent();
        curvePlot = new org.bm.blaise.specto.space.SpacePlotComponent();
        surfacePlot = new org.bm.blaise.specto.space.SpacePlotComponent();
        constrainPlot = new org.bm.blaise.specto.space.SpacePlotComponent();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        rollupPanel1 = new gui.RollupPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout vecPlotLayout = new org.jdesktop.layout.GroupLayout(vecPlot);
        vecPlot.setLayout(vecPlotLayout);
        vecPlotLayout.setHorizontalGroup(
            vecPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        vecPlotLayout.setVerticalGroup(
            vecPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 607, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Vector Field", vecPlot);

        org.jdesktop.layout.GroupLayout curvePlotLayout = new org.jdesktop.layout.GroupLayout(curvePlot);
        curvePlot.setLayout(curvePlotLayout);
        curvePlotLayout.setHorizontalGroup(
            curvePlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        curvePlotLayout.setVerticalGroup(
            curvePlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 607, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Curve", curvePlot);

        org.jdesktop.layout.GroupLayout surfacePlotLayout = new org.jdesktop.layout.GroupLayout(surfacePlot);
        surfacePlot.setLayout(surfacePlotLayout);
        surfacePlotLayout.setHorizontalGroup(
            surfacePlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        surfacePlotLayout.setVerticalGroup(
            surfacePlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 607, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Surface", surfacePlot);

        org.jdesktop.layout.GroupLayout constrainPlotLayout = new org.jdesktop.layout.GroupLayout(constrainPlot);
        constrainPlot.setLayout(constrainPlotLayout);
        constrainPlotLayout.setHorizontalGroup(
            constrainPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        constrainPlotLayout.setVerticalGroup(
            constrainPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 607, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Constrained Flow", constrainPlot);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jToolBar1.setRollover(true);

        jButton1.setText("Toggle anaglyph view");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(rollupPanel1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        rollupPanel1.removeAll();
        PlotComponent pc = (PlotComponent) jTabbedPane1.getSelectedComponent();
        rollupPanel1.add("Visometry", new PropertySheet(pc.getVisometry()));
        rollupPanel1.add("Timer", new PropertySheet(timer));
        for (Object p : pc.getPlottables()) {
            rollupPanel1.add(p.toString(), new PropertySheet(p));
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SpacePlotComponent spc = (SpacePlotComponent) jTabbedPane1.getSelectedComponent();
        spc.setAnaglyph(!spc.isAnaglyph());
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TestSpaceVectorFields().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.bm.blaise.specto.space.SpacePlotComponent constrainPlot;
    private org.bm.blaise.specto.space.SpacePlotComponent curvePlot;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private gui.RollupPanel rollupPanel1;
    private org.bm.blaise.specto.space.SpacePlotComponent surfacePlot;
    private org.bm.blaise.specto.space.SpacePlotComponent vecPlot;
    // End of variables declaration//GEN-END:variables
}
