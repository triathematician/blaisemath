package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2019 Elisha Peterson
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
import com.googlecode.blaisemath.palette.ColorSchemes;
import com.googlecode.blaisemath.palette.MutablePalette;
import com.googlecode.blaisemath.palette.Palettes;
import com.googlecode.blaisemath.firestarter.editor.EditorRegistration;

import javax.swing.*;

@SuppressWarnings({"FieldCanBeLocal", "OptionalGetWithoutIsPresent"})
public class PaletteEditorTestUi extends javax.swing.JFrame {

    public PaletteEditorTestUi() {
        initComponents();
        
        EditorRegistration.registerEditors();
        
        MutablePalette pal = Palettes.defaultPalette().mutableCopy();
        editor.setPalette(pal);
        preview.setPalette(pal);
        preview.setColors(editor.getColorListModel());
        editor.addColorListPropertyChangeListener(evt -> {
            jLabel1.repaint();
            jLabel2.repaint();
            jLabel3.repaint();
        });
        
        ColorScheme grad = ColorSchemes.scheme("BLUES").get();
        gradientEditor.setScheme(grad);
        gradientPreview.setPalette(pal);
        gradientPreview.setScheme(grad);
        gradientEditor.addColorListPropertyChangeListener(evt -> jLabel2.repaint());
        
        ColorScheme sch2 = ColorSchemes.scheme("CATEGORY20").get();
        schemeEditor.setScheme(sch2);
        schemePreview.setPalette(pal);
        schemePreview.setColors(schemeEditor.getColorListModel());
        schemePreview.setShowNames(false);
        schemeEditor.addColorListPropertyChangeListener(evt -> jLabel3.repaint());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        preview = new com.googlecode.blaisemath.palette.ui.ColorListIcon();
        palette = new com.googlecode.blaisemath.palette.MapPalette();
        gradientPreview = new com.googlecode.blaisemath.palette.ui.ColorSchemeGradientIcon();
        schemePreview = new com.googlecode.blaisemath.palette.ui.ColorListIcon();
        editor = new com.googlecode.blaisemath.palette.ui.PaletteEditor();
        jLabel1 = new javax.swing.JLabel();
        gradientEditor = new com.googlecode.blaisemath.palette.ui.ColorSchemeEditor();
        jLabel2 = new javax.swing.JLabel();
        schemeEditor = new com.googlecode.blaisemath.palette.ui.ColorSchemeEditor();
        jLabel3 = new javax.swing.JLabel();

        preview.setPalette(palette);

        schemePreview.setPalette(palette);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 2));

        editor.setPalette(palette);
        getContentPane().add(editor);

        jLabel1.setIcon(preview);
        getContentPane().add(jLabel1);
        getContentPane().add(gradientEditor);

        jLabel2.setIcon(gradientPreview);
        getContentPane().add(jLabel2);
        getContentPane().add(schemeEditor);

        jLabel3.setIcon(schemePreview);
        getContentPane().add(jLabel3);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex) {
            java.util.logging.Logger.getLogger(PaletteEditorTestUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new PaletteEditorTestUi().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.googlecode.blaisemath.palette.ui.PaletteEditor editor;
    private com.googlecode.blaisemath.palette.ui.ColorSchemeEditor gradientEditor;
    private com.googlecode.blaisemath.palette.ui.ColorSchemeGradientIcon gradientPreview;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private com.googlecode.blaisemath.palette.MapPalette palette;
    private com.googlecode.blaisemath.palette.ui.ColorListIcon preview;
    private com.googlecode.blaisemath.palette.ui.ColorSchemeEditor schemeEditor;
    private com.googlecode.blaisemath.palette.ui.ColorListIcon schemePreview;
    // End of variables declaration//GEN-END:variables
}
