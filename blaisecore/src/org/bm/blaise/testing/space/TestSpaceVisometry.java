/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TestSpaceVisometry.java
 *
 * Created on Oct 20, 2009, 9:21:37 PM
 */
package org.bm.blaise.testing.space;

import data.propertysheet.PropertySheet;
import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.specto.plane.PlanePlotComponent;
import org.bm.blaise.specto.plane.PlaneVisometry;
import org.bm.blaise.specto.plane.function.PlaneParametricCurve;
import org.bm.blaise.specto.space.*;
import org.bm.blaise.specto.space.basic.SpaceBox;
import org.bm.blaise.specto.space.diffeq.*;
import org.bm.blaise.specto.space.function.SpaceFunctionGraph;
import org.bm.blaise.specto.space.function.SpaceParametricCurve;
import org.bm.blaise.specto.space.function.SpaceParametricSurface;
import org.bm.blaise.specto.visometry.PlotComponent;
import org.bm.blaise.specto.visometry.Plottable;
import org.bm.utils.Curve3DSampleSet;
import org.bm.utils.Surface3DSampleSet;

/**
 *
 * @author ae3263
 */
public class TestSpaceVisometry extends javax.swing.JFrame {

    PlaneGraphicsInSpace pgis;
    PlaneVisometry pv;
    PlanePlotComponent ppc;

    /** Creates new form TestSpaceVisometry */
    public TestSpaceVisometry() {
        initComponents();

        stdPlot.addPlottable(new SpaceAxes());
        stdPlot.addPlottable(new SpaceBox());
        miniPlot.addPlottable(new SpaceAxes());
        miniPlot.addPlottable(new SpaceViewObjects(((SpaceVisometry) stdPlot.getVisometry()).getProj()));

        funPlot.addPlottable(new SpaceAxes());
        funPlot.addPlottable(new SpaceFunctionGraph());

        MultivariateVectorialFunction field1 =
                new MultivariateVectorialFunction() {
                    public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                        return new double[]{point[1], -Math.cos(point[0]*point[1]), -point[2]};
                    }
                };
        SpaceParametricCurve spc = new SpaceParametricCurve(new UnivariateVectorialFunction(){
            public double[] value(double x) throws FunctionEvaluationException {
                return new double[] { 2*Math.cos(x), 2*Math.sin(x), .5 };
            }
        }, 0.0, 2*Math.PI, 100);
        MultivariateVectorialFunction spcDer =
                new MultivariateVectorialFunction() {
                    public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                        return new double[] { -point[1], point[0], 0 };
                    }
                };
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

//        pv = new PlaneVisometry();
//        pgis = new PlaneGraphicsInSpace(planarPlot.getVisometryGraphics(), pv);
//        ppc = new PlanePlotComponent(pgis, pv);
//        planarPlot.getVisometry().addChangeListener(new ChangeListener(){
//            public void stateChanged(ChangeEvent e) {
//                ppc.repaint();
//                planarPlot.repaint();
//            }
//        });
//        planarPlot.addPlottable(new SpaceAxes());
//        ppc.addPlottable(new PlaneParametricCurve(new UnivariateVectorialFunction(){
//            public double[] value(double x) throws FunctionEvaluationException {
//                return new double[] { x*Math.cos(x), x*Math.sin(x) };
//            }
//        }, 0.0, 2*Math.PI, 100));

        vecPlot.addPlottable(new SpaceAxes());
        vecPlot.addPlottable(new SpaceVectorField(field1, stdPlot.getSSG()));
        vecPlot.addPlottable(spc);
        vecPlot.addPlottable(new SpaceVectorField(field1, new Curve3DSampleSet(spc.getFunction(), spc.getDomain())));
        vecPlot.addPlottable(new DerivedSpaceField(
                field1, spcDer, null,
                new Curve3DSampleSet(spc.getFunction(), spc.getDomain())));

        vecPlot2.addPlottable(sps);
        vecPlot2.addPlottable(new SpaceVectorField(field1, new Surface3DSampleSet(sps.getFunction(), sps.getDomainU(), sps.getDomainV())));
        vecPlot2.addPlottable(new DerivedSpaceField(
                field1, spsDer1, spsDer2,
                new Surface3DSampleSet(sps.getFunction(), sps.getDomainU(), sps.getDomainV())));

        data.beans.EditorRegistration.registerEditors();
        rollupPanel1.add("Visometry 1", new PropertySheet(stdPlot.getVisometry()));
        rollupPanel1.add("Proj 1", new PropertySheet(((SpaceVisometry) stdPlot.getVisometry()).getProj()));
        for (Plottable p : stdPlot.getPlottables()) {
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
        stdPlot = new org.bm.blaise.specto.space.SpacePlotComponent();
        miniPlot = new org.bm.blaise.specto.space.SpacePlotComponent();
        funPlot = new org.bm.blaise.specto.space.SpacePlotComponent();
        vecPlot = new org.bm.blaise.specto.space.SpacePlotComponent();
        vecPlot2 = new org.bm.blaise.specto.space.SpacePlotComponent();
        planarPlot = new org.bm.blaise.specto.space.SpacePlotComponent();
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

        org.jdesktop.layout.GroupLayout miniPlotLayout = new org.jdesktop.layout.GroupLayout(miniPlot);
        miniPlot.setLayout(miniPlotLayout);
        miniPlotLayout.setHorizontalGroup(
            miniPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
        miniPlotLayout.setVerticalGroup(
            miniPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 200, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout stdPlotLayout = new org.jdesktop.layout.GroupLayout(stdPlot);
        stdPlot.setLayout(stdPlotLayout);
        stdPlotLayout.setHorizontalGroup(
            stdPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, stdPlotLayout.createSequentialGroup()
                .addContainerGap(412, Short.MAX_VALUE)
                .add(miniPlot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        stdPlotLayout.setVerticalGroup(
            stdPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, stdPlotLayout.createSequentialGroup()
                .addContainerGap(396, Short.MAX_VALUE)
                .add(miniPlot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Camera & View", stdPlot);

        org.jdesktop.layout.GroupLayout funPlotLayout = new org.jdesktop.layout.GroupLayout(funPlot);
        funPlot.setLayout(funPlotLayout);
        funPlotLayout.setHorizontalGroup(
            funPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        funPlotLayout.setVerticalGroup(
            funPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 607, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Function", funPlot);

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

        org.jdesktop.layout.GroupLayout vecPlot2Layout = new org.jdesktop.layout.GroupLayout(vecPlot2);
        vecPlot2.setLayout(vecPlot2Layout);
        vecPlot2Layout.setHorizontalGroup(
            vecPlot2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        vecPlot2Layout.setVerticalGroup(
            vecPlot2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 607, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Vec Field 2 (Surface)", vecPlot2);

        org.jdesktop.layout.GroupLayout planarPlotLayout = new org.jdesktop.layout.GroupLayout(planarPlot);
        planarPlot.setLayout(planarPlotLayout);
        planarPlotLayout.setHorizontalGroup(
            planarPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 722, Short.MAX_VALUE)
        );
        planarPlotLayout.setVerticalGroup(
            planarPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 607, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Planar", planarPlot);

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
        PlotComponent ppc = (PlotComponent) jTabbedPane1.getSelectedComponent();
        rollupPanel1.add("Visometry", new PropertySheet(ppc.getVisometry()));
        for (Object p : ppc.getPlottables()) {
            rollupPanel1.add(p.toString(), new PropertySheet(p));
        }        // TODO add your handling code here:
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
                new TestSpaceVisometry().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.bm.blaise.specto.space.SpacePlotComponent funPlot;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private org.bm.blaise.specto.space.SpacePlotComponent miniPlot;
    private org.bm.blaise.specto.space.SpacePlotComponent planarPlot;
    private gui.RollupPanel rollupPanel1;
    private org.bm.blaise.specto.space.SpacePlotComponent stdPlot;
    private org.bm.blaise.specto.space.SpacePlotComponent vecPlot;
    private org.bm.blaise.specto.space.SpacePlotComponent vecPlot2;
    // End of variables declaration//GEN-END:variables
}
