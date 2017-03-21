/*
 * Copyright 2016 elisha.
 *
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
 */
package com.googlecode.blaisemath.graphics.swing;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.googlecode.blaisemath.editor.EditorRegistration;
import com.googlecode.blaisemath.firestarter.PropertySheet;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.style.editor.AttributeSetPropertyModel;
import com.googlecode.blaisemath.util.AnchoredText;
import com.googlecode.blaisemath.util.MPanel;
import com.googlecode.blaisemath.util.OrientedPoint2D;
import com.googlecode.blaisemath.util.RollupPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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

/**
 *
 * @author elisha
 */
public class MultilineTextRendererTestFrame extends javax.swing.JFrame {

    private AttributeSet textStyle = Styles.defaultTextStyle().copy()
            .and(Styles.OFFSET, new Point());
    
    /**
     * Creates new form MultilineTextRendererTestFrame
     */
    public MultilineTextRendererTestFrame() {
        initComponents();
        PanAndZoomHandler.install(canvas);
        canvas.addGraphic(JGraphics.path(new Line2D.Double(0, 100, 200, 100),
                Styles.strokeWidth(new Color(128, 128, 255, 64), 1f)));
        canvas.addGraphic(JGraphics.path(new Line2D.Double(0, 200, 200, 200), 
                Styles.strokeWidth(new Color(128, 128, 255, 64), 1f)));
        canvas.addGraphic(JGraphics.path(new Line2D.Double(100, 0, 100, 300),
                Styles.strokeWidth(new Color(128, 128, 255, 64), 1f)));
        
        canvas.addGraphic(JGraphics.marker(new OrientedPoint2D(100, 100), 
                Styles.fillStroke(new Color(255, 128, 128, 64), null).and(Styles.MARKER_RADIUS, 12)));
        canvas.addGraphic(new PrimitiveGraphic<AnchoredText,Graphics2D>(
                new AnchoredText(100, 100, "Here is some sample text that is a single line"),
                textStyle,
                TextRenderer.getInstance()));
        
        canvas.addGraphic(JGraphics.marker(new OrientedPoint2D(100, 200), 
                Styles.fillStroke(new Color(255, 128, 128, 64), null)));
        canvas.addGraphic(new PrimitiveGraphic<AnchoredText,Graphics2D>(
                new AnchoredText(100, 200, "Here is some\nsample text\nthat is wrapped\nonto multiple\nlines"),
                textStyle,
                mlRend));
        
        ObjectStyler os = new ObjectStyler();
        LabeledShapeGraphic lsg = new LabeledShapeGraphic("Here is some sample text that will be automatically wrapped onto multiple lines", 
                new Rectangle(200, 50, 100, 200), os);
        os.setLabelDelegate(new Function<Object,String>(){
            @Override
            public String apply(Object input) {
                return input+"";
            }
        });
        os.setLabelStyleConstant(textStyle);
        lsg.setDragEnabled(true);
        canvas.addGraphic(lsg);
        
        EditorRegistration.registerEditors();
        
        RollupPanel rp = new RollupPanel();
        AttributeSetPropertyModel apm = new AttributeSetPropertyModel(textStyle,
                ImmutableMap.<String,Class<?>>of(Styles.FONT, String.class, Styles.FONT_SIZE, Float.class,
                        Styles.OFFSET, Point.class));
        rp.add(new MPanel("Font", PropertySheet.forModel(apm)));
        jScrollPane1.setViewportView(rp);
        revalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mlRend = new com.googlecode.blaisemath.graphics.swing.MultilineTextRenderer();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        canvas = new com.googlecode.blaisemath.graphics.swing.JGraphicComponent();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel1.setText("text-anchor:");
        jToolBar1.add(jLabel1);

        jButton1.setText("Start");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jButton2.setText("Middle");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton3.setText("End");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);
        jToolBar1.add(jSeparator1);

        jLabel2.setText("alignement-baseline:");
        jToolBar1.add(jLabel2);

        jButton4.setText("Baseline");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        jButton5.setText("Middle");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton5);

        jButton6.setText("Hanging");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton6);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(canvas, java.awt.BorderLayout.CENTER);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(200, 22));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 22));
        getContentPane().add(jScrollPane1, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        textStyle.put(Styles.TEXT_ANCHOR, Styles.TEXT_ANCHOR_START);
        canvas.repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        textStyle.put(Styles.TEXT_ANCHOR, Styles.TEXT_ANCHOR_MIDDLE);
        canvas.repaint();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        textStyle.put(Styles.TEXT_ANCHOR, Styles.TEXT_ANCHOR_END);
        canvas.repaint();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        textStyle.put(Styles.ALIGN_BASELINE, Styles.ALIGN_BASELINE_BASELINE);
        canvas.repaint();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        textStyle.put(Styles.ALIGN_BASELINE, Styles.ALIGN_BASELINE_MIDDLE);
        canvas.repaint();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        textStyle.put(Styles.ALIGN_BASELINE, Styles.ALIGN_BASELINE_HANGING);
        canvas.repaint();
    }//GEN-LAST:event_jButton6ActionPerformed

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
            java.util.logging.Logger.getLogger(MultilineTextRendererTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MultilineTextRendererTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MultilineTextRendererTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MultilineTextRendererTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MultilineTextRendererTestFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent canvas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private com.googlecode.blaisemath.graphics.swing.MultilineTextRenderer mlRend;
    // End of variables declaration//GEN-END:variables
}
