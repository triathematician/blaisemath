package specto;

/*
 * BlaisePlotter.java
 *
 * Created on November 9, 2007, 1:36 PM
 */


import scio.coordinate.R2;
import sequor.VisualControl;
import sequor.control.ArcSlider;
import sequor.control.SliderBox;
import sequor.control.BoundedRangeSliderBox;
import sequor.control.DrawnPath;
import sequor.control.NumberSlider;
import sequor.control.SnapRule;
import sequor.model.DoubleRangeModel;
import specto.dynamicplottable.CirclePoint2D;
import specto.dynamicplottable.Clock2D;
import specto.decoration.DESolution2D;
import specto.decoration.FunctionSampleSet;
import specto.dynamicplottable.Segment2D;
import specto.dynamicplottable.DynamicPointSet2D;
import specto.dynamicplottable.FractalEdge2D;
import specto.dynamicplottable.Point2D;
import specto.dynamicplottable.RandomPoint2D;
import specto.dynamicplottable.RandomWalk2D;
import specto.dynamicplottable.Triangle2D;
import specto.plottable.Function2D;
import specto.plottable.HiddenText2D;
import specto.plottable.Parametric2D;
import specto.plottable.PlaneFunction2D;
import specto.plottable.Rectangle2D;
import specto.plottable.VectorField2D;

/**
 *
 * @author  ae3263
 */
public class TestPlottables extends javax.swing.JFrame {
    
    /** Creates new form BlaisePlotter */
    public TestPlottables() {
        initComponents();
        
        Function2D f1=new Function2D();
        FunctionSampleSet fss=new FunctionSampleSet(f1);
        plot2D1.add(fss);
        plot2D1.add(f1);
        Point2D cp0=f1.getConstrainedPoint();
        cp0.setPoint(new R2(3,0));
        Point2D cp1=f1.getConstrainedPoint();
        cp1.setPoint(new R2(2,0));
        plot2D1.add(new Segment2D.Line(cp0, cp1));
        plot2D1.add(cp0);
        plot2D1.add(cp1);
        Point2D cp2=f1.getPointSlope();
        plot2D1.add(cp2);
        DoubleRangeModel drm1=fss.getModel();
        plot2D1.add(SliderBox.getStyleAdjusters(100,10,15,plot2D1.getPlottables(),plot2D1));
        BoundedRangeSliderBox nra1=new BoundedRangeSliderBox(210,10,drm1);
        plot2D1.add(nra1,3,5);

        VectorField2D vf=new VectorField2D();
        plot2D3.add(vf);
        DESolution2D de1=new DESolution2D(vf);
        DESolution2D de2=new DESolution2D(vf);
        plot2D3.add(de1);
        plot2D3.add(de2);
        plot2D3.add(SliderBox.getStyleAdjusters(100,10,15,plot2D3.getPlottables(),plot2D3));

        PlaneFunction2D pf1=new PlaneFunction2D();
        plot2D4.add(pf1);
        plot2D4.add(SliderBox.getStyleAdjusters(100,10,15,plot2D4.getPlottables(),plot2D4));

        Segment2D s1=new Segment2D(-8,5,2,-1);
        CirclePoint2D cirp1=new CirclePoint2D(s1.getConstraintModel());
        cirp1.addRadius(1.0);
        cirp1.addRadius(1.5);
        cirp1.addRadius(2.5);
        plot2D5.add(s1);
        plot2D5.add(cirp1);
        Clock2D clock1=new Clock2D(8,5,50);
        plot2D5.add(clock1);
        Rectangle2D rect1=new Rectangle2D(3,-2,9,-8);
        plot2D5.add(rect1);
        Triangle2D tri1=new Triangle2D(-10,-2,-8,-8,-3,-5);
        plot2D5.add(tri1);
        plot2D5.add(SliderBox.getStyleAdjusters(100,10,15,plot2D5.getPlottables(),plot2D5));
        plot2D5.add(new HiddenText2D());
        
        DynamicPointSet2D dps1=new DynamicPointSet2D();
        for(double x=0;x<20;x+=.5){
            dps1.add(.2*x*Math.cos(x/3.0),.2*x*Math.sin(x/3.0));
        }
        FractalEdge2D fe1=new FractalEdge2D(new R2(-3,3),new R2(-2,3));
        fe1.add(-2-1/3.,3);
        fe1.add(-2-1/3.,3+1/3.);
        fe1.add(-2-2/3.,3+1/3.);
        fe1.add(-2-2/3.,3);
        plot2D6.add(dps1);
        plot2D6.add(fe1);
        plot2D6.add(SliderBox.getStyleAdjusters(100,10,15,plot2D6.getPlottables(),plot2D6));
        
        final Parametric2D par1=new Parametric2D();
        Parametric2D.ParametricPoint cp5=(Parametric2D.ParametricPoint) par1.getPointSlope();
        polarPlot2D1.add(cp5);
        polarPlot2D1.add(par1);
        ArcSlider da1=new ArcSlider(10,70,cp5.getModel());
        //da1.getStyle().setValue(NumberSlider.STYLE_DOTS);
        polarPlot2D1.add(da1,3,1);
        //polarPlot2D1.add(SliderBox.getStyleAdjusters(100,10,15,polarPlot2D1.getPlottables(),polarPlot2D1));
        SliderBox nab1=new SliderBox();
        //nab1.add(new NumberSlider(0,0,da1.getStyle()));
        nab1.add(new NumberSlider(0,0,da1.getSizeModel()));
        nab1.add(new NumberSlider(0,0,da1.getHandleSizeModel()));
        polarPlot2D1.add(nab1,3,8);
        DoubleRangeModel drm2=par1.getModel();
        polarPlot2D1.add(new BoundedRangeSliderBox(320,10,drm2),3,4);
        
        
        RandomPoint2D rp1=new RandomPoint2D();
        plot2D2.add(rp1);
        RandomWalk2D rw1=new RandomWalk2D();
        plot2D2.add(rw1);
        plot2D2.add(SliderBox.getStyleAdjusters(100,10,15,plot2D2.getPlottables(),plot2D2));
        SliderBox nab2=new SliderBox();
        nab2.add(new NumberSlider(210,10,rp1.getNumPointsModel()));
        nab2.add(new NumberSlider(210,30,rp1.getParameterModel()));
        plot2D2.add(nab2,3,1);
        SliderBox nab3=new SliderBox();
        nab3.add(new NumberSlider(320,10,rw1.getLengthModel()));
        nab3.add(new NumberSlider(320,30,rw1.getAngleParameterModel()));
        nab3.add(new NumberSlider(320,50,rw1.getDistancePerTimeModel()));
        plot2D2.add(nab3,3,5);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        plot2D1 = new specto.plotpanel.Plot2D();
        plot2D3 = new specto.plotpanel.Plot2D();
        plot2D4 = new specto.plotpanel.Plot2D();
        plot2D5 = new specto.plotpanel.Plot2D();
        plot2D6 = new specto.plotpanel.Plot2D();
        polarPlot2D1 = new specto.plotpanel.PolarPlot2D();
        plot2D2 = new specto.plotpanel.Plot2D();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();
        menuBar1 = new javax.swing.JMenuBar();
        fileMenu1 = new javax.swing.JMenu();
        openMenuItem1 = new javax.swing.JMenuItem();
        saveMenuItem1 = new javax.swing.JMenuItem();
        saveAsMenuItem1 = new javax.swing.JMenuItem();
        exitMenuItem1 = new javax.swing.JMenuItem();
        editMenu1 = new javax.swing.JMenu();
        cutMenuItem1 = new javax.swing.JMenuItem();
        copyMenuItem1 = new javax.swing.JMenuItem();
        pasteMenuItem1 = new javax.swing.JMenuItem();
        deleteMenuItem1 = new javax.swing.JMenuItem();
        helpMenu1 = new javax.swing.JMenu();
        contentsMenuItem1 = new javax.swing.JMenuItem();
        aboutMenuItem1 = new javax.swing.JMenuItem();
        menuBar2 = new javax.swing.JMenuBar();
        fileMenu2 = new javax.swing.JMenu();
        openMenuItem2 = new javax.swing.JMenuItem();
        saveMenuItem2 = new javax.swing.JMenuItem();
        saveAsMenuItem2 = new javax.swing.JMenuItem();
        exitMenuItem2 = new javax.swing.JMenuItem();
        editMenu2 = new javax.swing.JMenu();
        cutMenuItem2 = new javax.swing.JMenuItem();
        copyMenuItem2 = new javax.swing.JMenuItem();
        pasteMenuItem2 = new javax.swing.JMenuItem();
        deleteMenuItem2 = new javax.swing.JMenuItem();
        helpMenu2 = new javax.swing.JMenu();
        contentsMenuItem2 = new javax.swing.JMenuItem();
        aboutMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Demo of 2D Plottables");

        javax.swing.GroupLayout plot2D1Layout = new javax.swing.GroupLayout(plot2D1);
        plot2D1.setLayout(plot2D1Layout);
        plot2D1Layout.setHorizontalGroup(
            plot2D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 468, Short.MAX_VALUE)
        );
        plot2D1Layout.setVerticalGroup(
            plot2D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 289, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Basic", plot2D1);

        javax.swing.GroupLayout plot2D3Layout = new javax.swing.GroupLayout(plot2D3);
        plot2D3.setLayout(plot2D3Layout);
        plot2D3Layout.setHorizontalGroup(
            plot2D3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 468, Short.MAX_VALUE)
        );
        plot2D3Layout.setVerticalGroup(
            plot2D3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 289, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Field", plot2D3);

        javax.swing.GroupLayout plot2D4Layout = new javax.swing.GroupLayout(plot2D4);
        plot2D4.setLayout(plot2D4Layout);
        plot2D4Layout.setHorizontalGroup(
            plot2D4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 468, Short.MAX_VALUE)
        );
        plot2D4Layout.setVerticalGroup(
            plot2D4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 289, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Surface", plot2D4);
        jTabbedPane1.addTab("Points", plot2D5);
        jTabbedPane1.addTab("Point Set", plot2D6);

        javax.swing.GroupLayout polarPlot2D1Layout = new javax.swing.GroupLayout(polarPlot2D1);
        polarPlot2D1.setLayout(polarPlot2D1Layout);
        polarPlot2D1Layout.setHorizontalGroup(
            polarPlot2D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 468, Short.MAX_VALUE)
        );
        polarPlot2D1Layout.setVerticalGroup(
            polarPlot2D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 289, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Polar", polarPlot2D1);

        javax.swing.GroupLayout plot2D2Layout = new javax.swing.GroupLayout(plot2D2);
        plot2D2.setLayout(plot2D2Layout);
        plot2D2Layout.setHorizontalGroup(
            plot2D2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 468, Short.MAX_VALUE)
        );
        plot2D2Layout.setVerticalGroup(
            plot2D2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 289, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Random", plot2D2);

        fileMenu.setText("File"); // NOI18N

        openMenuItem.setText("Open"); // NOI18N
        fileMenu.add(openMenuItem);

        saveMenuItem.setText("Save"); // NOI18N
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setText("Save As ..."); // NOI18N
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setText("Exit"); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setText("Edit"); // NOI18N

        cutMenuItem.setText("Cut"); // NOI18N
        editMenu.add(cutMenuItem);

        copyMenuItem.setText("Copy"); // NOI18N
        editMenu.add(copyMenuItem);

        pasteMenuItem.setText("Paste"); // NOI18N
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setText("Delete"); // NOI18N
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setText("Help"); // NOI18N

        contentsMenuItem.setText("Contents"); // NOI18N
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setText("About"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        fileMenu1.setText("File"); // NOI18N

        openMenuItem1.setText("Open"); // NOI18N
        fileMenu1.add(openMenuItem1);

        saveMenuItem1.setText("Save"); // NOI18N
        fileMenu1.add(saveMenuItem1);

        saveAsMenuItem1.setText("Save As ..."); // NOI18N
        fileMenu1.add(saveAsMenuItem1);

        exitMenuItem1.setText("Exit"); // NOI18N
        exitMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu1.add(exitMenuItem1);

        menuBar1.add(fileMenu1);

        editMenu1.setText("Edit"); // NOI18N

        cutMenuItem1.setText("Cut"); // NOI18N
        editMenu1.add(cutMenuItem1);

        copyMenuItem1.setText("Copy"); // NOI18N
        editMenu1.add(copyMenuItem1);

        pasteMenuItem1.setText("Paste"); // NOI18N
        editMenu1.add(pasteMenuItem1);

        deleteMenuItem1.setText("Delete"); // NOI18N
        editMenu1.add(deleteMenuItem1);

        menuBar1.add(editMenu1);

        helpMenu1.setText("Help"); // NOI18N

        contentsMenuItem1.setText("Contents"); // NOI18N
        helpMenu1.add(contentsMenuItem1);

        aboutMenuItem1.setText("About"); // NOI18N
        helpMenu1.add(aboutMenuItem1);

        menuBar1.add(helpMenu1);

        fileMenu2.setText("File"); // NOI18N

        openMenuItem2.setText("Open"); // NOI18N
        fileMenu2.add(openMenuItem2);

        saveMenuItem2.setText("Save"); // NOI18N
        fileMenu2.add(saveMenuItem2);

        saveAsMenuItem2.setText("Save As ..."); // NOI18N
        fileMenu2.add(saveAsMenuItem2);

        exitMenuItem2.setText("Exit"); // NOI18N
        exitMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu2.add(exitMenuItem2);

        menuBar2.add(fileMenu2);

        editMenu2.setText("Edit"); // NOI18N

        cutMenuItem2.setText("Cut"); // NOI18N
        editMenu2.add(cutMenuItem2);

        copyMenuItem2.setText("Copy"); // NOI18N
        editMenu2.add(copyMenuItem2);

        pasteMenuItem2.setText("Paste"); // NOI18N
        editMenu2.add(pasteMenuItem2);

        deleteMenuItem2.setText("Delete"); // NOI18N
        editMenu2.add(deleteMenuItem2);

        menuBar2.add(editMenu2);

        helpMenu2.setText("Help"); // NOI18N

        contentsMenuItem2.setText("Contents"); // NOI18N
        helpMenu2.add(contentsMenuItem2);

        aboutMenuItem2.setText("About"); // NOI18N
        helpMenu2.add(aboutMenuItem2);

        menuBar2.add(helpMenu2);

        setJMenuBar(menuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestPlottables().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem aboutMenuItem1;
    private javax.swing.JMenuItem aboutMenuItem2;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem contentsMenuItem1;
    private javax.swing.JMenuItem contentsMenuItem2;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem copyMenuItem1;
    private javax.swing.JMenuItem copyMenuItem2;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem cutMenuItem1;
    private javax.swing.JMenuItem cutMenuItem2;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenuItem deleteMenuItem1;
    private javax.swing.JMenuItem deleteMenuItem2;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu editMenu1;
    private javax.swing.JMenu editMenu2;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem exitMenuItem1;
    private javax.swing.JMenuItem exitMenuItem2;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu fileMenu1;
    private javax.swing.JMenu fileMenu2;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenu helpMenu1;
    private javax.swing.JMenu helpMenu2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuBar menuBar1;
    private javax.swing.JMenuBar menuBar2;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem openMenuItem1;
    private javax.swing.JMenuItem openMenuItem2;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem pasteMenuItem1;
    private javax.swing.JMenuItem pasteMenuItem2;
    private specto.plotpanel.Plot2D plot2D1;
    private specto.plotpanel.Plot2D plot2D2;
    private specto.plotpanel.Plot2D plot2D3;
    private specto.plotpanel.Plot2D plot2D4;
    private specto.plotpanel.Plot2D plot2D5;
    private specto.plotpanel.Plot2D plot2D6;
    private specto.plotpanel.PolarPlot2D polarPlot2D1;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem1;
    private javax.swing.JMenuItem saveAsMenuItem2;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem saveMenuItem1;
    private javax.swing.JMenuItem saveMenuItem2;
    // End of variables declaration//GEN-END:variables
    
}
