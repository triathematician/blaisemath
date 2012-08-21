/*
 * BasicFunctionPlot.java
 * Created on Dec 1, 2009, 3:06:10 PM
 */

package org.bm.blaise.application;

import java.awt.Color;
import org.bm.blaise.scio.function.ParsedUnivariateRealFunction;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.specto.plane.PlaneAxes;

/**
 *
 * @author ae3263
 */
public class BasicFunctionPlot extends javax.swing.JPanel {

    /** Creates new form BasicFunctionPlot */
    public BasicFunctionPlot() {
        initComponents();
        jTextField1KeyReleased(null);
        planePlotComponent1.addPlottable(new PlaneAxes("x", "f(x)"));
        planePlotComponent1.addPlottable(planeFunctionGraph1);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        planeFunctionGraph1 = new org.bm.blaise.specto.plane.function.PlaneFunctionGraph();
        planePlotComponent1 = new org.bm.blaise.specto.plane.PlanePlotComponent();
        jTextField1 = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout planePlotComponent1Layout = new org.jdesktop.layout.GroupLayout(planePlotComponent1);
        planePlotComponent1.setLayout(planePlotComponent1Layout);
        planePlotComponent1Layout.setHorizontalGroup(
            planePlotComponent1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        planePlotComponent1Layout.setVerticalGroup(
            planePlotComponent1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 280, Short.MAX_VALUE)
        );

        add(planePlotComponent1, java.awt.BorderLayout.CENTER);

        jTextField1.setText("sin(x)");
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        add(jTextField1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        try {
            ParsedUnivariateRealFunction func = new ParsedUnivariateRealFunction(jTextField1.getText(), null);
            planeFunctionGraph1.setFunction(func);
            jTextField1.setForeground(Color.BLACK);
        } catch (ParseException ex) {
            jTextField1.setForeground(Color.DARK_GRAY);
        } catch (IllegalArgumentException ex) {
            jTextField1.setForeground(Color.RED);
        }
    }//GEN-LAST:event_jTextField1KeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jTextField1;
    private org.bm.blaise.specto.plane.function.PlaneFunctionGraph planeFunctionGraph1;
    private org.bm.blaise.specto.plane.PlanePlotComponent planePlotComponent1;
    // End of variables declaration//GEN-END:variables

}
