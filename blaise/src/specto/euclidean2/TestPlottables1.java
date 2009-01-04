package specto.euclidean2;

/*
 * BlaisePlotter.java
 *
 * Created on November 9, 2007, 1:36 PM
 */


import scio.coordinate.R2;
import sequor.control.ArcSlider;
import sequor.control.SliderBox;
import sequor.control.BoundedRangeSliderBox;
import sequor.control.NumberSlider;
import sequor.model.DoubleRangeModel2;
import specto.euclidean2.CirclePoint2D;
import specto.euclidean2.Clock2D;
import specto.euclidean2.DESolution2D;
import specto.euclidean2.FunctionSampleSet;
import specto.euclidean2.Segment2D;
import specto.euclidean2.DynamicPointSet2D;
import specto.euclidean2.FractalShape2D;
import specto.euclidean2.Point2D;
import specto.euclidean2.RandomFlame2D;
import specto.euclidean2.RandomPoint2D;
import specto.euclidean2.RandomWalk2D;
import specto.euclidean2.Triangle2D;
import specto.euclidean2.Voronoi2D;
import specto.euclidean2.StandardGrid2D;
import specto.euclidean2.Function2D;
import specto.euclidean2.HiddenText2D;
import specto.euclidean2.Parametric2D;
import specto.euclidean2.PlaneFunction2D;
import specto.euclidean2.Rectangle2D;
import specto.euclidean2.VectorField2D;

/**
 *
 * @author  ae3263
 */
public class TestPlottables1 extends javax.swing.JFrame {
    
    /** Creates new form BlaisePlotter */
    public TestPlottables1() {
        initComponents();
        
        Function2D f1=new Function2D();
        FunctionSampleSet fss=new FunctionSampleSet(f1);
        functionPlot.add(fss);
        functionPlot.add(f1);
        Point2D cp0=f1.getConstrainedPoint();
        cp0.setPoint(new R2(3,0));
        Point2D cp1=f1.getConstrainedPoint();
        cp1.setPoint(new R2(2,0));
        functionPlot.add(new Segment2D.Line(cp0, cp1));
        functionPlot.add(cp0);
        functionPlot.add(cp1);
        Point2D cp2=f1.getPointSlope();
        functionPlot.add(cp2);
        functionPlot.add(new RandomFlame2D(cp2.getModel(),80,20));
        DoubleRangeModel2 drm1=fss.getModel();
        BoundedRangeSliderBox nra1=new BoundedRangeSliderBox(210,10,drm1);
        functionPlot.add(nra1,3,5);
        functionPlot.add(new StandardGrid2D());
        functionPlot.add(SliderBox.getStyleAdjusters(100,10,15,functionPlot.getPlottables(),functionPlot));
        
        VectorField2D vf=new VectorField2D();
        vectorFieldPlot.add(vf.getDivergence());
        vectorFieldPlot.add(vf.getScalarCurl());
        vectorFieldPlot.add(vf);
        vectorFieldPlot.add(vf.getFlowCurve());
        vectorFieldPlot.add(vf.getFlowCurve());
        vectorFieldPlot.add(SliderBox.getStyleAdjusters(100,10,15,vectorFieldPlot.getPlottables(),vectorFieldPlot));        

        PlaneFunction2D pf1=new PlaneFunction2D();
        surfacePlot.add(pf1);    
        VectorField2D vf1=new VectorField2D(pf1.getGradientFunction());
        surfacePlot.add(vf1);
        surfacePlot.add(new DESolution2D(vf1));
        surfacePlot.add(new PlaneFunctionVector2D(pf1));    
        surfacePlot.add(SliderBox.getStyleAdjusters(100,10,15,surfacePlot.getPlottables(),surfacePlot));

        Segment2D s1=new Segment2D(-8,5,2,-1);
        CirclePoint2D cirp1=new CirclePoint2D(s1.getConstraintModel());
        cirp1.addRadius(1.0);
        cirp1.addRadius(1.5);
        cirp1.addRadius(2.5);
        pointSetPlot.add(s1);
        pointSetPlot.add(cirp1);
        Clock2D clock1=new Clock2D(8,5,50);
        pointSetPlot.add(clock1);
        Rectangle2D rect1=new Rectangle2D(3,-2,9,-8);
        pointSetPlot.add(rect1);
        Triangle2D tri1=new Triangle2D(-10,-2,-8,-8,-3,-5);
        pointSetPlot.add(tri1);
        pointSetPlot.add(new HiddenText2D());
        pointSetPlot.add(SliderBox.getStyleAdjusters(100,10,15,pointSetPlot.getPlottables(),pointSetPlot));
        
        DynamicPointSet2D dps1=new DynamicPointSet2D();
        for(double x=0;x<20;x+=.5){
            dps1.add(3+.2*x*Math.cos(x/3.0),-3+.2*x*Math.sin(x/3.0));
        }
        fractalPlot.add(dps1);
        FractalShape2D.Edges fe1=new FractalShape2D.Edges(new R2(-3,3),new R2(-2-2/3.,3));
        fe1.add(-2-2/3.,3+1/3.);
        fe1.add(-2-1/3.,3+1/3.);
        fe1.add(-2-1/3.,3);
        fe1.add(-2,3);
        fractalPlot.add(fe1); 
        FractalShape2D.SpaceFilling sfc1=new FractalShape2D.SpaceFilling(new R2(4,4),new R2(4,5));
        sfc1.add(5,5);sfc1.add(5,4);        
        fractalPlot.add(sfc1);
        FractalShape2D.Sierpinski fsp1=new FractalShape2D.Sierpinski(new R2(-4,-4),new R2(-3,-4+Math.sqrt(3)),new R2(-2,-4));
        fractalPlot.add(fsp1);
        fractalPlot.add(new Voronoi2D());
        SliderBox nab5=new SliderBox();
        nab5.add(new NumberSlider(210,10,fe1.getIterModel()));
        nab5.add(new NumberSlider(210,10,sfc1.getIterModel()));
        nab5.add(new NumberSlider(210,10,fsp1.getIterModel()));
        fractalPlot.add(nab5,3,1);
        fractalPlot.add(SliderBox.getStyleAdjusters(100,10,15,fractalPlot.getPlottables(),fractalPlot));
        
        final Parametric2D par1=new Parametric2D();
        Parametric2D.ParametricPoint cp5=(Parametric2D.ParametricPoint) par1.getPointSlope();
        polarPlot2D1.add(cp5);
        polarPlot2D1.add(par1);
        ArcSlider da1=new ArcSlider(10,70,cp5.getTimeModel());
        //da1.getStyle().setValue(NumberSlider.STYLE_DOTS);
        polarPlot2D1.add(da1,3,1);
        SliderBox nab1=new SliderBox();
        //nab1.add(new NumberSlider(0,0,da1.getStyle()));
        nab1.add(new NumberSlider(0,0,da1.getSizeModel()));
        nab1.add(new NumberSlider(0,0,da1.getHandleSizeModel()));
        polarPlot2D1.add(nab1,3,8);
        DoubleRangeModel2 drm2=par1.getModel();
        polarPlot2D1.add(new BoundedRangeSliderBox(320,10,drm2),3,4);
        polarPlot2D1.add(SliderBox.getStyleAdjusters(100,10,15,polarPlot2D1.getPlottables(),polarPlot2D1));
        
        RandomFlame2D rf5=new RandomFlame2D(-12,0,100,100);
        RandomFlame2D rf2=new RandomFlame2D(-18,0,90,20);
        RandomFlame2D rf4=new RandomFlame2D(-14,0,70,60);
        RandomFlame2D rf3=new RandomFlame2D(-16,0,60,5);
        RandomFlame2D rf1=new RandomFlame2D(-20,0,50,10);
        randomPlot.add(rf5);
        randomPlot.add(rf2);
        randomPlot.add(rf4);
        randomPlot.add(rf3);
        randomPlot.add(rf1);
        RandomPoint2D rp1=new RandomPoint2D();
        randomPlot.add(rp1);
        RandomWalk2D rw1=new RandomWalk2D();
        randomPlot.add(rw1);
        SliderBox nab2=new SliderBox();
        nab2.add(new NumberSlider(210,10,rp1.getNumPointsModel()));
        nab2.add(new NumberSlider(210,30,rp1.getParameterModel()));
        randomPlot.add(nab2,3,1);
        SliderBox nab3=new SliderBox();
        nab3.add(new NumberSlider(320,10,rw1.getLengthModel()));
        nab3.add(new NumberSlider(320,30,rw1.getAngleParameterModel()));
        nab3.add(new NumberSlider(320,50,rw1.getDistancePerTimeModel()));
        randomPlot.add(nab3,3,5);
        randomPlot.add(SliderBox.getStyleAdjusters(100,10,15,randomPlot.getPlottables(),randomPlot));
        
        graphPlot.add(new Graph2D());
        graphPlot.add(SliderBox.getStyleAdjusters(100,10,15,graphPlot.getPlottables(),graphPlot));
        
        dynamicVectorFieldPlot.add(new VectorFieldTimed2D());
     }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        functionPlot = new specto.euclidean2.Plot2D();
        polarPlot2D1 = new specto.euclidean2.PolarPlot2D();
        surfacePlot = new specto.euclidean2.Plot2D();
        vectorFieldPlot = new specto.euclidean2.Plot2D();
        randomPlot = new specto.euclidean2.Plot2D();
        pointSetPlot = new specto.euclidean2.Plot2D();
        fractalPlot = new specto.euclidean2.Plot2D();
        graphPlot = new specto.euclidean2.Plot2D();
        dynamicVectorFieldPlot = new specto.euclidean2.Plot2D();
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

        javax.swing.GroupLayout functionPlotLayout = new javax.swing.GroupLayout(functionPlot);
        functionPlot.setLayout(functionPlotLayout);
        functionPlotLayout.setHorizontalGroup(
            functionPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        functionPlotLayout.setVerticalGroup(
            functionPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );

        tabs.addTab("Functions", functionPlot);

        javax.swing.GroupLayout polarPlot2D1Layout = new javax.swing.GroupLayout(polarPlot2D1);
        polarPlot2D1.setLayout(polarPlot2D1Layout);
        polarPlot2D1Layout.setHorizontalGroup(
            polarPlot2D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        polarPlot2D1Layout.setVerticalGroup(
            polarPlot2D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );

        tabs.addTab("Polar", polarPlot2D1);

        javax.swing.GroupLayout surfacePlotLayout = new javax.swing.GroupLayout(surfacePlot);
        surfacePlot.setLayout(surfacePlotLayout);
        surfacePlotLayout.setHorizontalGroup(
            surfacePlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        surfacePlotLayout.setVerticalGroup(
            surfacePlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );

        tabs.addTab("Surfaces", surfacePlot);

        vectorFieldPlot.setAxisStyle(1);

        javax.swing.GroupLayout vectorFieldPlotLayout = new javax.swing.GroupLayout(vectorFieldPlot);
        vectorFieldPlot.setLayout(vectorFieldPlotLayout);
        vectorFieldPlotLayout.setHorizontalGroup(
            vectorFieldPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        vectorFieldPlotLayout.setVerticalGroup(
            vectorFieldPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );

        tabs.addTab("Vector Fields", vectorFieldPlot);

        randomPlot.setAxisVisible(false);
        randomPlot.setGridVisible(false);

        javax.swing.GroupLayout randomPlotLayout = new javax.swing.GroupLayout(randomPlot);
        randomPlot.setLayout(randomPlotLayout);
        randomPlotLayout.setHorizontalGroup(
            randomPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        randomPlotLayout.setVerticalGroup(
            randomPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );

        tabs.addTab("Random Functions", randomPlot);

        javax.swing.GroupLayout pointSetPlotLayout = new javax.swing.GroupLayout(pointSetPlot);
        pointSetPlot.setLayout(pointSetPlotLayout);
        pointSetPlotLayout.setHorizontalGroup(
            pointSetPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        pointSetPlotLayout.setVerticalGroup(
            pointSetPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );

        tabs.addTab("Point Sets", pointSetPlot);

        javax.swing.GroupLayout fractalPlotLayout = new javax.swing.GroupLayout(fractalPlot);
        fractalPlot.setLayout(fractalPlotLayout);
        fractalPlotLayout.setHorizontalGroup(
            fractalPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        fractalPlotLayout.setVerticalGroup(
            fractalPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );

        tabs.addTab("Fractal Stuff", fractalPlot);

        graphPlot.setAnimatorVisible(false);
        graphPlot.setAxisVisible(false);
        graphPlot.setGridVisible(false);

        javax.swing.GroupLayout graphPlotLayout = new javax.swing.GroupLayout(graphPlot);
        graphPlot.setLayout(graphPlotLayout);
        graphPlotLayout.setHorizontalGroup(
            graphPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        graphPlotLayout.setVerticalGroup(
            graphPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );

        tabs.addTab("Graphs", graphPlot);

        javax.swing.GroupLayout dynamicVectorFieldPlotLayout = new javax.swing.GroupLayout(dynamicVectorFieldPlot);
        dynamicVectorFieldPlot.setLayout(dynamicVectorFieldPlotLayout);
        dynamicVectorFieldPlotLayout.setHorizontalGroup(
            dynamicVectorFieldPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        dynamicVectorFieldPlotLayout.setVerticalGroup(
            dynamicVectorFieldPlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );

        tabs.addTab("Dynamic Vector Fields", dynamicVectorFieldPlot);

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
            .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
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
                new TestPlottables1().setVisible(true);
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
    private specto.euclidean2.Plot2D dynamicVectorFieldPlot;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu editMenu1;
    private javax.swing.JMenu editMenu2;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem exitMenuItem1;
    private javax.swing.JMenuItem exitMenuItem2;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu fileMenu1;
    private javax.swing.JMenu fileMenu2;
    private specto.euclidean2.Plot2D fractalPlot;
    private specto.euclidean2.Plot2D functionPlot;
    private specto.euclidean2.Plot2D graphPlot;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenu helpMenu1;
    private javax.swing.JMenu helpMenu2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuBar menuBar1;
    private javax.swing.JMenuBar menuBar2;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem openMenuItem1;
    private javax.swing.JMenuItem openMenuItem2;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem pasteMenuItem1;
    private javax.swing.JMenuItem pasteMenuItem2;
    private specto.euclidean2.Plot2D pointSetPlot;
    private specto.euclidean2.PolarPlot2D polarPlot2D1;
    private specto.euclidean2.Plot2D randomPlot;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem1;
    private javax.swing.JMenuItem saveAsMenuItem2;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem saveMenuItem1;
    private javax.swing.JMenuItem saveMenuItem2;
    private specto.euclidean2.Plot2D surfacePlot;
    private javax.swing.JTabbedPane tabs;
    private specto.euclidean2.Plot2D vectorFieldPlot;
    // End of variables declaration//GEN-END:variables
    
}
