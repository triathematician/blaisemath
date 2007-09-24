/*
 * TestFrame.java
 *
 * Created on September 15, 2007, 1:00 PM
 */

package specto.tests;

import specto.PlotPanel;
import specto.dynamicplottable.Grid2D;
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
    
    private void initComponents() {
        pp=new PlotPanel(new Euclidean2());
        pp.add(new Grid2D());        
        add(pp);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
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
