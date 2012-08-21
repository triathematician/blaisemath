/*
 * AParameterPlotter.java
 *
 * Created on November 7, 2008, 12:11 PM
 */

package curro;

import sequor.model.FunctionTreeModel;
import sequor.model.ParameterListModel;
import specto.euclidean2.Axes2D;
import specto.euclidean2.DESolution2D;
import specto.euclidean2.Point2D;
import specto.euclidean2.VectorField2D;
import specto.euclidean2.VectorFieldTimed2D;
import specto.euclidean3.DESolution3D;
import specto.euclidean3.Point3D;
import specto.euclidean3.VectorField3D;

/**
 *
 * @author  ae3263
 */
public class PADiffEq2Plotter extends javax.swing.JApplet {

    String[][] functions = { { "x'(x,y,t)=" , "a*x+b*y", "t", "x", "y" } ,
                             { "y'(x,y,t)=" , "c*x+d*y", "t", "x", "y" } };
    Object[][] parameters = { { "a", 0 }, { "b", -1 }, {"c", 1}, {"d", 0} };

    /** Initializes the applet AParameterPlotter */
    public void init() {
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
       
                    parameterListModel1=new ParameterListModel(parameters, settingsPanel1, functionPanel1);
                    VectorFieldTimed2D vf1 = new VectorFieldTimed2D(
                            functionPanel1.getFunctionModel(0), functionPanel1.getFunctionModel(1),
                            "t", "x", "y");
                    plot2D1.add(vf1);
                    plot2D1.add(vf1.getStyleSlider("Field style",0,0),5,5);

                    VectorField3D vf3 = new VectorField3D(functionPanel1.getFunctionModel(0), functionPanel1.getFunctionModel(1), new FunctionTreeModel("1"), "x", "y", "t");
                    plot3D1.add(vf3);
                    plot3D1.add(vf3.getStyleSlider("Field style",0,0),5,5);
                    Point2D p1 = new Point2D(1,1);
                    Point2D p2 = new Point2D(1,-1);
                    DESolution3D sol3a = new DESolution3D(vf3, p1);
                    DESolution3D sol3b = new DESolution3D(vf3, p2);

                    plot2D1.add(p1);
                    plot2D1.add(sol3a.getPathPhase());
                    plot2D2.add(sol3a.getPathX());
                    plot2D3.add(sol3a.getPathY());
                    plot3D1.add(sol3a);

                    plot2D1.add(p2);
                    plot2D1.add(sol3b.getPathPhase());
                    plot2D2.add(sol3b.getPathX());
                    plot2D3.add(sol3b.getPathY());
                    plot3D1.add(sol3b);

                    plot2D2.getVisometry().setDesiredBounds(-1, -5, 10, 5);
                    plot2D3.getVisometry().setDesiredBounds(-1, -5, 10, 5);
                    plot2D2.synchronizeTimerWith(plot2D1);
                    plot2D3.synchronizeTimerWith(plot2D1);

                    plot2D1.repaint();
                    plot2D2.repaint();
                    plot2D3.repaint();
                    plot3D1.repaint();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parameterListModel1 = new sequor.model.ParameterListModel();
        settingsPanel1 = new sequor.component.SettingsPanel();
        functionPanel1 = new sequor.component.FunctionPanel(functions);
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        plot2D1 = new specto.euclidean2.Plot2D();
        jSplitPane2 = new javax.swing.JSplitPane();
        plot2D3 = new specto.euclidean2.Plot2D();
        plot2D2 = new specto.euclidean2.Plot2D();
        plot3D1 = new specto.euclidean3.Plot3D();

        settingsPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        settingsPanel1.setPreferredSize(new java.awt.Dimension(250, 332));
        getContentPane().add(settingsPanel1, java.awt.BorderLayout.LINE_END);
        getContentPane().add(functionPanel1, java.awt.BorderLayout.PAGE_END);

        jSplitPane1.setDividerSize(15);
        jSplitPane1.setResizeWeight(0.5);

        plot2D1.setAxisStyle(1);
        plot2D1.setMarkerBoxVisible(false);

        javax.swing.GroupLayout plot2D1Layout = new javax.swing.GroupLayout(plot2D1);
        plot2D1.setLayout(plot2D1Layout);
        plot2D1Layout.setHorizontalGroup(
            plot2D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 231, Short.MAX_VALUE)
        );
        plot2D1Layout.setVerticalGroup(
            plot2D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 287, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(plot2D1);

        jSplitPane2.setDividerSize(10);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(0.5);

        plot2D3.setXLabel("t");
        plot2D3.setAnimatorVisible(false);
        plot2D3.setAxisStyle(2);
        plot2D3.setMarkerBoxVisible(false);

        javax.swing.GroupLayout plot2D3Layout = new javax.swing.GroupLayout(plot2D3);
        plot2D3.setLayout(plot2D3Layout);
        plot2D3Layout.setHorizontalGroup(
            plot2D3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 233, Short.MAX_VALUE)
        );
        plot2D3Layout.setVerticalGroup(
            plot2D3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 137, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(plot2D3);

        plot2D2.setXLabel("t");
        plot2D2.setYLabel("x");
        plot2D2.setAnimatorVisible(false);
        plot2D2.setAxisStyle(2);
        plot2D2.setMarkerBoxVisible(false);

        javax.swing.GroupLayout plot2D2Layout = new javax.swing.GroupLayout(plot2D2);
        plot2D2.setLayout(plot2D2Layout);
        plot2D2Layout.setHorizontalGroup(
            plot2D2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 233, Short.MAX_VALUE)
        );
        plot2D2Layout.setVerticalGroup(
            plot2D2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );

        jSplitPane2.setLeftComponent(plot2D2);

        jSplitPane1.setRightComponent(jSplitPane2);

        jTabbedPane1.addTab("2D", jSplitPane1);

        plot3D1.setZLabel("t");
        plot3D1.setAxisStyle(3);
        plot3D1.setMarkerBoxVisible(false);

        javax.swing.GroupLayout plot3D1Layout = new javax.swing.GroupLayout(plot3D1);
        plot3D1.setLayout(plot3D1Layout);
        plot3D1Layout.setHorizontalGroup(
            plot3D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 483, Short.MAX_VALUE)
        );
        plot3D1Layout.setVerticalGroup(
            plot3D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 289, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("3D", plot3D1);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private sequor.component.FunctionPanel functionPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private sequor.model.ParameterListModel parameterListModel1;
    private specto.euclidean2.Plot2D plot2D1;
    private specto.euclidean2.Plot2D plot2D2;
    private specto.euclidean2.Plot2D plot2D3;
    private specto.euclidean3.Plot3D plot3D1;
    private sequor.component.SettingsPanel settingsPanel1;
    // End of variables declaration//GEN-END:variables

}
