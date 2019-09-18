/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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

import com.google.common.io.Files;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class SvgTool extends javax.swing.JFrame {

    private SvgGraphic gsvg;
    
    /**
     * Creates new form SvgTool
     */
    public SvgTool() {
        initComponents();
        setMinimumSize(new Dimension(400,400));
        setPreferredSize(new Dimension(500,500));
        setMaximumSize(new Dimension(600,600));
        gsvg = new SvgGraphic();
        gsvg.setStyle(Styles.strokeWidth(Color.blue, 2f));
        canvas.addGraphic(gsvg);
        canvas.addGraphic(JGraphics.path(new Rectangle2D.Double(0, 0, 1000, 1000), Styles.strokeWidth(new Color(128, 128, 128, 128), 1f)));
        canvas.addGraphic(JGraphics.path(new Rectangle2D.Double(500, 500, 1000, 1000), Styles.strokeWidth(new Color(128, 128, 128, 128), 1f)));
        canvas.addGraphic(JGraphics.path(new Rectangle2D.Double(-500, -500, 1000, 1000), Styles.strokeWidth(new Color(128, 128, 128, 128), 1f)));
        PanAndZoomHandler.install(canvas);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        loadB = new javax.swing.JButton();
        saveB = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        canvas = new com.googlecode.blaisemath.graphics.swing.JGraphicComponent();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        text = new javax.swing.JTextArea();
        jToolBar2 = new javax.swing.JToolBar();
        pathTB = new javax.swing.JRadioButton();
        xmlTB = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        loadB.setText("Load");
        loadB.setFocusable(false);
        loadB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBActionPerformed(evt);
            }
        });
        jToolBar1.add(loadB);

        saveB.setText("Save");
        saveB.setFocusable(false);
        saveB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBActionPerformed(evt);
            }
        });
        jToolBar1.add(saveB);
        jToolBar1.add(jSeparator1);

        jButton3.setText("Draw");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton1.setText("Move");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setResizeWeight(0.7);
        jSplitPane1.setLeftComponent(canvas);

        jPanel1.setLayout(new java.awt.BorderLayout());

        text.setColumns(20);
        text.setRows(5);
        jScrollPane2.setViewportView(text);

        jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jToolBar2.setRollover(true);

        buttonGroup1.add(pathTB);
        pathTB.setSelected(true);
        pathTB.setText("Path");
        pathTB.setFocusable(false);
        pathTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(pathTB);

        buttonGroup1.add(xmlTB);
        xmlTB.setText("SVG XML");
        xmlTB.setFocusable(false);
        xmlTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(xmlTB);

        jPanel1.add(jToolBar2, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setRightComponent(jPanel1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (pathTB.isSelected()) {
            gsvg.setElement(new SvgPath(text.getText()));
        } else {
            try {
                SvgRoot root = SvgRoot.load(text.getText());
                gsvg.setElement(root);
                Object bg = root.getStyle().get("background");
                if (bg instanceof Color) {
                    canvas.setBackground((Color) bg);
                }
            } catch (IOException ex) {
                Logger.getLogger(SvgTool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private final JFileChooser chooser = new JFileChooser();
    
    private void saveBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBActionPerformed
        if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this)) {
            try (FileOutputStream out = new FileOutputStream(chooser.getSelectedFile())) {
                SvgElement el = gsvg.getElement();
                if (!(el instanceof SvgRoot)) {
                    SvgRoot rootEl = new SvgRoot();
                    rootEl.addElement(el);
                    el = rootEl;
                }
                SvgRoot.save((SvgRoot) el, out);
            } catch (IOException x) {
                Logger.getLogger(SvgTool.class.getName()).log(Level.SEVERE, null, x);
            }
        }
    }//GEN-LAST:event_saveBActionPerformed

    private void loadBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBActionPerformed
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            try (FileInputStream fis = new FileInputStream(chooser.getSelectedFile())) {
                SvgRoot r = SvgRoot.load(fis);
                gsvg.setElement(r);
                String fs = Files.toString(chooser.getSelectedFile(), Charset.defaultCharset());
                text.setText(fs);
            } catch (IOException x) {
                Logger.getLogger(SvgTool.class.getName()).log(Level.SEVERE, null, x);
            }
        }
    }//GEN-LAST:event_loadBActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        gsvg.setGraphicBounds(new Rectangle(20, 50, 40, 100));
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new SvgTool().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent canvas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JButton loadB;
    private javax.swing.JRadioButton pathTB;
    private javax.swing.JButton saveB;
    private javax.swing.JTextArea text;
    private javax.swing.JRadioButton xmlTB;
    // End of variables declaration//GEN-END:variables
}
