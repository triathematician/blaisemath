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

import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.AbstractAction;

/**
 *
 * @author Elisha Peterson
 */
@SuppressWarnings("FieldCanBeLocal")
public class ContextMenuTestFrame extends javax.swing.JFrame {

    /**
     * Creates new form TestTooltips
     */
    public ContextMenuTestFrame() {
        initComponents();
        PrimitiveGraphic g1 = JGraphics.shape(new Ellipse2D.Double(50,50,100,100));
        g1.addContextMenuInitializer((menu, src, point, focus, selection) -> {
            menu.add(""+point);
            menu.add(new AbstractAction("press me"){
                public void actionPerformed(ActionEvent e) { System.out.println("pressed"); }
            });
        });
        g1.setSelectionEnabled(true);
        gc.addGraphic(g1);
        
        PrimitiveGraphic g2 = JGraphics.shape(new Rectangle2D.Double(60,90,100,100));
        g2.setSelectionEnabled(true);
        gc.addGraphic(g2);
        
        gc.setSelectionEnabled(true);
        gc.getGraphicRoot().addContextMenuInitializer((menu, src, point, focus, selection) -> {
            menu.setLabel("root label");
            menu.add((selection == null ? 0 : selection.size()) + " graphics selected");
            if (menu.getComponentCount() > 0)
                menu.addSeparator();
            menu.add("Root menu option");
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(gc, java.awt.BorderLayout.CENTER);

        jLabel1.setText("<html>Should be up to 3 parts of the context menu: one for the circle, one for the selection, and one for the root graphics.");
        getContentPane().add(jLabel1, java.awt.BorderLayout.PAGE_START);

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
        } catch (ClassNotFoundException | javax.swing.UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex) {
            java.util.logging.Logger.getLogger(ContextMenuTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //endregion

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ContextMenuTestFrame().setVisible(true));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent gc;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
