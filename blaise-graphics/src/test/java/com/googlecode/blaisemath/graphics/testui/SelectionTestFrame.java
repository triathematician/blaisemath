/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.graphics.testui;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import com.googlecode.blaisemath.style.Styles;

/**
 *
 * @author Elisha
 */
public class SelectionTestFrame extends javax.swing.JFrame {

    /**
     * Creates new form TestTooltips
     */
    public SelectionTestFrame() {
        initComponents();
        PrimitiveGraphic g1 = JGraphics.shape(new Ellipse2D.Double(50,50,100,100),
                Styles.fillStroke(Color.blue, Color.red));
        g1.setSelectionEnabled(true);
        gc.addGraphic(g1);
        
        PrimitiveGraphic g2 = JGraphics.shape(new Rectangle2D.Double(60,90,100,100));
        g2.setSelectionEnabled(true);
        gc.addGraphic(g2);
        
        // all it takes to add selection capability!
        gc.setSelectionEnabled(true);
        gc.getSelectionModel().addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                resultL.setText(String.format("<html><b>Selection:</b> %s", evt.getNewValue()));
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gc = new com.googlecode.blaisemath.graphics.swing.JGraphicComponent();
        jLabel1 = new javax.swing.JLabel();
        resultL = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(gc, java.awt.BorderLayout.CENTER);

        jLabel1.setText("<html>Test that ctrl+drag creates a selection box, ctrl+click toggles selection, ctrl+shift+drag unselects from box. Test that selected items change appearance.");
        getContentPane().add(jLabel1, java.awt.BorderLayout.PAGE_START);

        resultL.setText("Selection: ");
        getContentPane().add(resultL, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SelectionTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SelectionTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SelectionTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SelectionTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SelectionTestFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent gc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel resultL;
    // End of variables declaration//GEN-END:variables
}
