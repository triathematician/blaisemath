/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TestSpaceVisometry.java
 *
 * Created on Oct 20, 2009, 9:21:37 PM
 */

package org.bm.blaise.application;

import data.propertysheet.PropertySheet;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.specto.plane.PlaneAxes;
import org.bm.blaise.specto.plane.PlaneAxes.AxisStyle;
import org.bm.blaise.specto.plane.function.*;
import org.bm.blaise.specto.plane.diffeq.*;
import org.bm.blaise.specto.visometry.PlotComponent;
import org.bm.blaise.specto.visometry.Plottable;

/**
 *
 * @author ae3263
 */
public class VectorCalc extends javax.swing.JFrame {

    UnivariateRealFunction ftcFunc = new UnivariateRealFunction(){
        public double value(double x) throws FunctionEvaluationException {
            return x*x*x/20-x*x/2+x;
        }
    };

    UnivariateVectorialFunction curve1 = new UnivariateVectorialFunction(){
        public double[] value(double x) throws FunctionEvaluationException {
            return new double[]{x-1, .5*x};
        }
    };
    UnivariateVectorialFunction curve2 = new UnivariateVectorialFunction(){
        public double[] value(double x) throws FunctionEvaluationException {
            return new double[]{Math.cos(x), Math.sin(x)};
        }
    };
    UnivariateVectorialFunction curve3 = new UnivariateVectorialFunction(){
        public double[] value(double x) throws FunctionEvaluationException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    MultivariateRealFunction surf1 = new MultivariateRealFunction() {
        public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            return Math.sin(point[0] * point[1]);
        }
    };
    MultivariateVectorialFunction surf1grad = new MultivariateVectorialFunction(){
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            return new double[]{ point[1] * Math.cos(point[0] * point[1]), point[0] * Math.cos(point[0] * point[1]) };
        }
    };

    MultivariateVectorialFunction sfc1 = new MultivariateVectorialFunction(){
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };
    MultivariateVectorialFunction sfc2 = new MultivariateVectorialFunction(){
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };
    MultivariateVectorialFunction sfc3 = new MultivariateVectorialFunction(){
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    MultivariateVectorialFunction vecField1 = new MultivariateVectorialFunction(){
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            return new double[]{point[0], point[1]};
        }
    };
    MultivariateVectorialFunction vecField2 = new MultivariateVectorialFunction(){
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            return new double[]{point[1], -point[0]};
        }
    };
    MultivariateVectorialFunction vecField3 = new MultivariateVectorialFunction(){
        public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };


    /** Creates new form TestSpaceVisometry */
    public VectorCalc() {
        initComponents();

        PlaneAxes axes = PlaneAxes.instance(AxisStyle.CROSS, "x", "y");
        PlaneVectorField vec1 = new PlaneVectorField(vecField1, ftcPlot.getSSG());
        PlaneSurfaceFunction psf = new PlaneSurfaceFunction(surf1, gradientPlot.getSSG());
        PlaneVectorField psfgrad = new PlaneVectorField(surf1grad, gradientPlot.getSSG());

        ftcPlot.addPlottable(axes);
        ftcPlot.addPlottable(new PlaneFunctionGraph(ftcFunc));

        gradientPlot.addPlottable(axes);
        gradientPlot.addPlottable(psf);
        gradientPlot.addPlottable(psfgrad);

        lineIntegralPlot.addPlottable(axes);
        lineIntegralPlot.addPlottable(new PlaneParametricFunction(curve1, -2.0, 5.0, 0.5));
        lineIntegralPlot.addPlottable(psf);
        lineIntegralPlot.addPlottable(psfgrad);

        divCurlPlot.addPlottable(axes);
        divCurlPlot.addPlottable(vec1);

        data.beans.EditorRegistration.registerEditors();
        rollupPanel1.add("Visometry", new PropertySheet(ftcPlot.getVisometry()));
        for (Plottable p : ftcPlot.getPlottables()) {
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        ftcPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        gradientPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        lineIntegralPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        divCurlPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        greensPlot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        div2Plot = new org.bm.blaise.specto.plane.PlanePlotComponent();
        stokesPlot = new org.bm.blaise.specto.space.SpacePlotComponent();
        div3Plot = new org.bm.blaise.specto.space.SpacePlotComponent();
        jScrollPane1 = new javax.swing.JScrollPane();
        rollupPanel1 = new gui.RollupPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout ftcPlotLayout = new org.jdesktop.layout.GroupLayout(ftcPlot);
        ftcPlot.setLayout(ftcPlotLayout);
        ftcPlotLayout.setHorizontalGroup(
            ftcPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        ftcPlotLayout.setVerticalGroup(
            ftcPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 630, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("FTC", ftcPlot);

        org.jdesktop.layout.GroupLayout gradientPlotLayout = new org.jdesktop.layout.GroupLayout(gradientPlot);
        gradientPlot.setLayout(gradientPlotLayout);
        gradientPlotLayout.setHorizontalGroup(
            gradientPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        gradientPlotLayout.setVerticalGroup(
            gradientPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 630, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Gradients", gradientPlot);

        org.jdesktop.layout.GroupLayout lineIntegralPlotLayout = new org.jdesktop.layout.GroupLayout(lineIntegralPlot);
        lineIntegralPlot.setLayout(lineIntegralPlotLayout);
        lineIntegralPlotLayout.setHorizontalGroup(
            lineIntegralPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        lineIntegralPlotLayout.setVerticalGroup(
            lineIntegralPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 630, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Line Integrals", lineIntegralPlot);

        org.jdesktop.layout.GroupLayout divCurlPlotLayout = new org.jdesktop.layout.GroupLayout(divCurlPlot);
        divCurlPlot.setLayout(divCurlPlotLayout);
        divCurlPlotLayout.setHorizontalGroup(
            divCurlPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        divCurlPlotLayout.setVerticalGroup(
            divCurlPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 630, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Divergence/Curl", divCurlPlot);

        org.jdesktop.layout.GroupLayout greensPlotLayout = new org.jdesktop.layout.GroupLayout(greensPlot);
        greensPlot.setLayout(greensPlotLayout);
        greensPlotLayout.setHorizontalGroup(
            greensPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        greensPlotLayout.setVerticalGroup(
            greensPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 630, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Green's Theorem", greensPlot);

        org.jdesktop.layout.GroupLayout div2PlotLayout = new org.jdesktop.layout.GroupLayout(div2Plot);
        div2Plot.setLayout(div2PlotLayout);
        div2PlotLayout.setHorizontalGroup(
            div2PlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        div2PlotLayout.setVerticalGroup(
            div2PlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 630, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Divergence (2D)", div2Plot);

        org.jdesktop.layout.GroupLayout stokesPlotLayout = new org.jdesktop.layout.GroupLayout(stokesPlot);
        stokesPlot.setLayout(stokesPlotLayout);
        stokesPlotLayout.setHorizontalGroup(
            stokesPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        stokesPlotLayout.setVerticalGroup(
            stokesPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 630, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Stoke's", stokesPlot);

        org.jdesktop.layout.GroupLayout div3PlotLayout = new org.jdesktop.layout.GroupLayout(div3Plot);
        div3Plot.setLayout(div3PlotLayout);
        div3PlotLayout.setHorizontalGroup(
            div3PlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        div3PlotLayout.setVerticalGroup(
            div3PlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 630, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Divergence (3D)", div3Plot);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jScrollPane1.setViewportView(rollupPanel1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        rollupPanel1.removeAll();
        PlotComponent ppc = (PlotComponent) jTabbedPane1.getSelectedComponent();
        rollupPanel1.add("Visometry", new PropertySheet(ppc.getVisometry()));
        for (Object p : ppc.getPlottables()) {
            rollupPanel1.add(p.toString(), new PropertySheet(p));
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jTabbedPane1StateChanged

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VectorCalc().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.bm.blaise.specto.plane.PlanePlotComponent div2Plot;
    private org.bm.blaise.specto.space.SpacePlotComponent div3Plot;
    private org.bm.blaise.specto.plane.PlanePlotComponent divCurlPlot;
    private org.bm.blaise.specto.plane.PlanePlotComponent ftcPlot;
    private org.bm.blaise.specto.plane.PlanePlotComponent gradientPlot;
    private org.bm.blaise.specto.plane.PlanePlotComponent greensPlot;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private org.bm.blaise.specto.plane.PlanePlotComponent lineIntegralPlot;
    private gui.RollupPanel rollupPanel1;
    private org.bm.blaise.specto.space.SpacePlotComponent stokesPlot;
    // End of variables declaration//GEN-END:variables

}
