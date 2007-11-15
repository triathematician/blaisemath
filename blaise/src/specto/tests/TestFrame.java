/*
 * TestFrame.java
 * Created on September 15, 2007, 1:00 PM
 */

package specto.tests;

import specto.gridplottable.Grid2D;
import specto.plottable.VectorField2D;
import specto.plottable.PlaneFunction2D;
import specto.PlotPanel;
import specto.dynamicplottable.*;
import specto.visometry.Euclidean2;

/**
 *
 * @author  ae3263
 */
public class TestFrame extends javax.swing.JFrame {
    
    PlotPanel pp;
    
    /** Creates new form TestFrame */
    public TestFrame() {
        initComponents();
    }
    
    @SuppressWarnings("unchecked")
    private void initComponents() {
        pp=new PlotPanel(new Euclidean2());
        pp.add(new Grid2D());     
        //pp.add(new PolarGrid2D());
        //pp.add(new PlaneFunction2D());
        pp.add(new VectorField2D());
        pp.add(new DESolution2D());
        pp.add(new DESolution2D());
        //pp.add(new Function2D());
        //pp.add(new Parametric2D());
        //pp.add(new Point2D());
        //pp.add(new Point2D());
        add(pp);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
        pp.getVisometry().computeTransformation();
    }// </editor-fold>
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify
    // End of variables declaration
    
}
