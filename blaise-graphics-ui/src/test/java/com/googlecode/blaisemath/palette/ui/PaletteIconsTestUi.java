package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2019 - 2024 Elisha Peterson
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

import com.googlecode.blaisemath.palette.ColorScheme;
import com.googlecode.blaisemath.palette.Palette;
import com.googlecode.blaisemath.palette.Palettes;

import javax.swing.*;
import java.awt.Color;

@SuppressWarnings("FieldCanBeLocal")
public class PaletteIconsTestUi extends javax.swing.JFrame {

    public PaletteIconsTestUi() {
        initComponents();
        Palette p1 = Palettes.defaultPalette().mutableCopy().and("no color", null);
        Palette p2 = Palettes.lafPalette().mutableCopy().and("no color", null);
        icon1.setColors(ColorListModel.create(p1));
        icon2.setColors(ColorListModel.create(p2));
        icon3.setColors(ColorListModel.create(p1));
        icon4.setColors(ColorListModel.create(p2));
        icon1.setPalette(p1);
        icon2.setPalette(p2);
        icon3.setPalette(p1);
        icon4.setPalette(p2);
        icon5.setScheme(ColorScheme.createGradient("", Color.red, Color.blue, Color.green));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        icon1 = new com.googlecode.blaisemath.palette.ui.ColorListIcon();
        icon2 = new com.googlecode.blaisemath.palette.ui.ColorListIcon();
        icon3 = new com.googlecode.blaisemath.palette.ui.ColorListIcon();
        icon4 = new com.googlecode.blaisemath.palette.ui.ColorListIcon();
        icon5 = new com.googlecode.blaisemath.palette.ui.ColorSchemeGradientIcon();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        icon1.setShowNames(false);

        icon2.setShowNames(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        jLabel1.setIcon(icon1);
        getContentPane().add(jLabel1);

        jLabel2.setIcon(icon2);
        getContentPane().add(jLabel2);

        jLabel3.setIcon(icon3);
        getContentPane().add(jLabel3);

        jLabel4.setIcon(icon4);
        getContentPane().add(jLabel4);

        jLabel5.setIcon(icon5);
        getContentPane().add(jLabel5);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
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
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex) {
            java.util.logging.Logger.getLogger(PaletteIconsTestUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        java.awt.EventQueue.invokeLater(() -> new PaletteIconsTestUi().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.googlecode.blaisemath.palette.ui.ColorListIcon icon1;
    private com.googlecode.blaisemath.palette.ui.ColorListIcon icon2;
    private com.googlecode.blaisemath.palette.ui.ColorListIcon icon3;
    private com.googlecode.blaisemath.palette.ui.ColorListIcon icon4;
    private com.googlecode.blaisemath.palette.ui.ColorSchemeGradientIcon icon5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    // End of variables declaration//GEN-END:variables
}
