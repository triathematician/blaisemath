/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LineVisometryTest.java
 *
 * Created on May 12, 2010, 7:46:47 AM
 */

package gtest;

import data.propertysheet.PropertySheet;
import java.awt.BorderLayout;
import java.awt.geom.Point2D;
import visometry.line.LinePlotComponent;
import visometry.plane.*;
import visometry.plottable.*;

/**
 *
 * @author ae3263
 */
public class DemoPlaneVisometry extends javax.swing.JFrame {

    PlaneGrid pg1, pg2, pg3, pg4, pg5;
    PlanePolarGrid ppg4;
    PlaneAxes pa1, pa2, pa3, pa4, pa4b, pa5;

    PlaneRectangle pr;
    PlaneEllipse pe;
    PlaneTriangle pt;

    VClock vc;
    VLine vl;
    VPath vp;
    VPoint vpt;
    VPointSet vps;
    VRay vr;
    VSegment vs;
    VShape vsh;

    PlaneSurfaceFunction psf;
    PlaneFunctionGraph pfg;
    VConstrainedPoint<Point2D.Double> vcp1;

    PlaneParametricArea ppa;
    PlaneParametricCurve ppc;

    PlaneVectorField pvf;

    /** Creates new form LineVisometryTest */
    public DemoPlaneVisometry() {
        data.propertysheet.editor.EditorRegistration.registerEditors();
        
        initComponents();

        genericPlot.add(vc = new VClock<Point2D.Double>(new Point2D.Double(.8, .5)));
        genericPlot.add(vl = new VLine<Point2D.Double>(new Point2D.Double(-5,5), new Point2D.Double(2,2)));
        genericPlot.add(vp = new VPath<Point2D.Double>(new Point2D.Double(-.1,.1), new Point2D.Double(-.2,.4), new Point2D.Double(-.3,.9), new Point2D.Double(-.4,1.6)));
        genericPlot.add(vpt = new VPoint<Point2D.Double>(new Point2D.Double(.5, .5)));
        genericPlot.add(vps = new VPointSet<Point2D.Double>(new Point2D.Double(5,1), new Point2D.Double(5,1.2), new Point2D.Double(5,1.3), new Point2D.Double(5,1.4)));
        genericPlot.add(vr = new VRay<Point2D.Double>(new Point2D.Double(5,5), new Point2D.Double(-2,2)));
        genericPlot.add(vs = new VSegment<Point2D.Double>(new Point2D.Double(1,2), new Point2D.Double(-.2,.2)));
        genericPlot.add(vsh = new VShape<Point2D.Double>(new Point2D.Double(.1,-.1), new Point2D.Double(.2,-.4), new Point2D.Double(.3,-.9), new Point2D.Double(.4,-1.6)));
        props.add("Generic Clock 1", new PropertySheet(vc));
        props.add("Generic Line 1", new PropertySheet(vl));
        props.add("Generic Path 1", new PropertySheet(vp));
        props.add("Generic Point 1", new PropertySheet(vpt));
        props.add("Generic PointSet 1", new PropertySheet(vps));
        props.add("Generic Ray 1", new PropertySheet(vr));
        props.add("Generic Segment 1", new PropertySheet(vs));
        props.add("Generic Shape 1", new PropertySheet(vsh));

        geometryPlot.add(pr = new PlaneRectangle());
        geometryPlot.add(pe = new PlaneEllipse());
        geometryPlot.add(pt = new PlaneTriangle());
        props.add("Visometry 2", new PropertySheet(geometryPlot.getVisometry()));
        props.add("Rectangle 2", new PropertySheet(pr));
        props.add("Ellipse 2", new PropertySheet(pe));
        props.add("Triangle 2", new PropertySheet(pt));

        functionPlot.add(pg3 = new PlaneGrid());
        functionPlot.add(pa3 = new PlaneAxes("x", "y", PlaneAxes.AxesType.UPPER_HALF));
        functionPlot.add(psf = new PlaneSurfaceFunction());
        functionPlot.add(pfg = new PlaneFunctionGraph());
        functionPlot.add(vcp1 = new VConstrainedPoint<Point2D.Double>(new Point2D.Double(-2,2), pfg));
        props.add("Grid 3", new PropertySheet(pg3));
        props.add("Axes 3", new PropertySheet(pa3));
        props.add("Surface Function 3", new PropertySheet(psf));
        props.add("Function 3", new PropertySheet(pfg));
        props.add("Constrained Point 3", new PropertySheet(vcp1));

        parametricPlot.add(ppg4 = new PlanePolarGrid());
        parametricPlot.add(pa4 = new PlaneAxes("x", "y", PlaneAxes.AxesType.STANDARD));
        parametricPlot.add(ppa = new PlaneParametricArea());
        parametricPlot.add(ppc = new PlaneParametricCurve());
        parametricPlaneDomainPlot.add(pg4 = new PlaneGrid());
        parametricPlaneDomainPlot.add(pa4b = new PlaneAxes("r", "theta", PlaneAxes.AxesType.STANDARD));
        parametricPlaneDomainPlot.add(ppa.getDomainPlottable());
        LinePlotComponent lpc = new LinePlotComponent();
        parametricPanel.add(lpc, BorderLayout.WEST);
        lpc.add(ppc.getDomainPlottable());
        props.add("Parametric Curve 4", new PropertySheet(ppc));
        props.add("Parametric Curve Domain 4", new PropertySheet(ppc.getDomainPlottable()));
        props.add("Parametric Area 4", new PropertySheet(ppa));
        props.add("Parametric Area Domain 4", new PropertySheet(ppa.getDomainPlottable()));

        fieldPlot.add(pg5 = new PlaneGrid());
        fieldPlot.add(pa5 = new PlaneAxes("u", "v", PlaneAxes.AxesType.BOX));
        fieldPlot.add(pvf = new PlaneVectorField());
        props.add("Vector Field 5", new PropertySheet(pvf));

        xtraPlot.add(new VDimensionHandle<Point2D.Double>(new Point2D.Double[]{new Point2D.Double(-3,-3), new Point2D.Double(3,-3)}, "x"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        genericPlot = new visometry.plane.PlanePlotComponent();
        geometryPlot = new visometry.plane.PlanePlotComponent();
        functionPlot = new visometry.plane.PlanePlotComponent();
        fieldPlot = new visometry.plane.PlanePlotComponent();
        parametricPanel = new javax.swing.JPanel();
        parametricPlot = new visometry.plane.PlanePlotComponent();
        parametricPlaneDomainPlot = new visometry.plane.PlanePlotComponent();
        xtraPlot = new visometry.plane.PlanePlotComponent();
        jScrollPane1 = new javax.swing.JScrollPane();
        props = new gui.RollupPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Plane Visometry Test");

        org.jdesktop.layout.GroupLayout genericPlotLayout = new org.jdesktop.layout.GroupLayout(genericPlot);
        genericPlot.setLayout(genericPlotLayout);
        genericPlotLayout.setHorizontalGroup(
            genericPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 402, Short.MAX_VALUE)
        );
        genericPlotLayout.setVerticalGroup(
            genericPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 434, Short.MAX_VALUE)
        );

        tabs.addTab("Generic", genericPlot);

        org.jdesktop.layout.GroupLayout geometryPlotLayout = new org.jdesktop.layout.GroupLayout(geometryPlot);
        geometryPlot.setLayout(geometryPlotLayout);
        geometryPlotLayout.setHorizontalGroup(
            geometryPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 402, Short.MAX_VALUE)
        );
        geometryPlotLayout.setVerticalGroup(
            geometryPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 434, Short.MAX_VALUE)
        );

        tabs.addTab("Geometric", geometryPlot);

        org.jdesktop.layout.GroupLayout functionPlotLayout = new org.jdesktop.layout.GroupLayout(functionPlot);
        functionPlot.setLayout(functionPlotLayout);
        functionPlotLayout.setHorizontalGroup(
            functionPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 402, Short.MAX_VALUE)
        );
        functionPlotLayout.setVerticalGroup(
            functionPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 434, Short.MAX_VALUE)
        );

        tabs.addTab("Functions", functionPlot);

        org.jdesktop.layout.GroupLayout fieldPlotLayout = new org.jdesktop.layout.GroupLayout(fieldPlot);
        fieldPlot.setLayout(fieldPlotLayout);
        fieldPlotLayout.setHorizontalGroup(
            fieldPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 402, Short.MAX_VALUE)
        );
        fieldPlotLayout.setVerticalGroup(
            fieldPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 434, Short.MAX_VALUE)
        );

        tabs.addTab("Fields", fieldPlot);

        parametricPanel.setLayout(new java.awt.BorderLayout());

        parametricPlot.setPreferredSize(new java.awt.Dimension(400, 400));

        org.jdesktop.layout.GroupLayout parametricPlotLayout = new org.jdesktop.layout.GroupLayout(parametricPlot);
        parametricPlot.setLayout(parametricPlotLayout);
        parametricPlotLayout.setHorizontalGroup(
            parametricPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 402, Short.MAX_VALUE)
        );
        parametricPlotLayout.setVerticalGroup(
            parametricPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );

        parametricPanel.add(parametricPlot, java.awt.BorderLayout.NORTH);

        parametricPlaneDomainPlot.setPreferredSize(new java.awt.Dimension(300, 300));

        org.jdesktop.layout.GroupLayout parametricPlaneDomainPlotLayout = new org.jdesktop.layout.GroupLayout(parametricPlaneDomainPlot);
        parametricPlaneDomainPlot.setLayout(parametricPlaneDomainPlotLayout);
        parametricPlaneDomainPlotLayout.setHorizontalGroup(
            parametricPlaneDomainPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
        parametricPlaneDomainPlotLayout.setVerticalGroup(
            parametricPlaneDomainPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 34, Short.MAX_VALUE)
        );

        parametricPanel.add(parametricPlaneDomainPlot, java.awt.BorderLayout.EAST);

        tabs.addTab("Parametrics", parametricPanel);

        org.jdesktop.layout.GroupLayout xtraPlotLayout = new org.jdesktop.layout.GroupLayout(xtraPlot);
        xtraPlot.setLayout(xtraPlotLayout);
        xtraPlotLayout.setHorizontalGroup(
            xtraPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 402, Short.MAX_VALUE)
        );
        xtraPlotLayout.setVerticalGroup(
            xtraPlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 434, Short.MAX_VALUE)
        );

        tabs.addTab("Xtra", xtraPlot);

        jScrollPane1.setViewportView(props);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(tabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DemoPlaneVisometry().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private visometry.plane.PlanePlotComponent fieldPlot;
    private visometry.plane.PlanePlotComponent functionPlot;
    private visometry.plane.PlanePlotComponent genericPlot;
    private visometry.plane.PlanePlotComponent geometryPlot;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel parametricPanel;
    private visometry.plane.PlanePlotComponent parametricPlaneDomainPlot;
    private visometry.plane.PlanePlotComponent parametricPlot;
    private gui.RollupPanel props;
    private javax.swing.JTabbedPane tabs;
    private visometry.plane.PlanePlotComponent xtraPlot;
    // End of variables declaration//GEN-END:variables

}
