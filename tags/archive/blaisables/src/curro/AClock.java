/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AClock.java
 *
 * Created on Mar 31, 2009, 11:07:47 AM
 */

package curro;

import specto.euclidean2.Clock2D;

/**
 *
 * @author ae3263
 */
public class AClock extends javax.swing.JApplet {

    /** Initializes the applet AClock */
    public void init() {
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                    plot2D1.add(new Clock2D(0,0,50));
                    plot2D1.getTimer().play();
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

        plot2D1 = new specto.euclidean2.Plot2D();

        plot2D1.setBackground(new java.awt.Color(0, 0, 102));
        plot2D1.setAnimatorVisible(false);
        plot2D1.setAxisVisible(false);
        plot2D1.setGridVisible(false);
        plot2D1.setMarkerBoxVisible(false);

        javax.swing.GroupLayout plot2D1Layout = new javax.swing.GroupLayout(plot2D1);
        plot2D1.setLayout(plot2D1Layout);
        plot2D1Layout.setHorizontalGroup(
            plot2D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        plot2D1Layout.setVerticalGroup(
            plot2D1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 125, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plot2D1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plot2D1, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private specto.euclidean2.Plot2D plot2D1;
    // End of variables declaration//GEN-END:variables

}