package com.googlecode.blaisemath.graphics.swing;

import com.google.common.collect.ImmutableMap;
import com.googlecode.blaisemath.editor.EditorRegistration;
import com.googlecode.blaisemath.firestarter.PropertySheet;
import com.googlecode.blaisemath.graphics.AnchoredIcon;
import com.googlecode.blaisemath.graphics.AnchoredImage;
import com.googlecode.blaisemath.graphics.AnchoredText;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.style.Anchor;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.graphics.editor.AttributeSetPropertyModel;
import com.googlecode.blaisemath.util.MPanel;
import com.googlecode.blaisemath.coordinate.OrientedPoint2D;
import com.googlecode.blaisemath.util.RollupPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;

/*
 * #%L
 * blaise-graphics
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

/**
 *
 * @author Elisha Peterson
 */
@SuppressWarnings("FieldCanBeLocal")
public class AnchorTestFrame extends javax.swing.JFrame {

    private final AttributeSet textStyle = Styles.DEFAULT_TEXT_STYLE.copy()
            .and(Styles.OFFSET, new Point());
    
    /**
     * Creates new form MultilineTextRendererTestFrame
     */
    public AnchorTestFrame() {
        initComponents();
        jComboBox1.setModel(new DefaultComboBoxModel(Anchor.values()));
        jComboBox1.setSelectedItem(Anchor.SOUTHWEST);
        
        PanAndZoomHandler.install(canvas);
        canvas.addGraphic(JGraphics.path(new Line2D.Double(0, 100, 200, 100),
                Styles.strokeWidth(new Color(128, 128, 255, 64), 1f)));
        canvas.addGraphic(JGraphics.path(new Line2D.Double(0, 200, 200, 200), 
                Styles.strokeWidth(new Color(128, 128, 255, 64), 1f)));
        canvas.addGraphic(JGraphics.path(new Line2D.Double(0, 300, 200, 300), 
                Styles.strokeWidth(new Color(128, 128, 255, 64), 1f)));
        canvas.addGraphic(JGraphics.path(new Line2D.Double(0, 400, 200, 400), 
                Styles.strokeWidth(new Color(128, 128, 255, 64), 1f)));
        canvas.addGraphic(JGraphics.path(new Line2D.Double(100, 0, 100, 500),
                Styles.strokeWidth(new Color(128, 128, 255, 64), 1f)));
        
        canvas.addGraphic(JGraphics.marker(new OrientedPoint2D(100, 100), 
                Styles.fillStroke(new Color(255, 128, 128, 64), null).and(Styles.MARKER_RADIUS, 12)));
        canvas.addGraphic(JGraphics.text(new AnchoredText(100, 100, "Here is some sample text that is a single line"), textStyle));
        
        canvas.addGraphic(JGraphics.marker(new OrientedPoint2D(100, 200), 
                Styles.fillStroke(new Color(255, 128, 128, 64), null).and(Styles.MARKER_RADIUS, 12)));
        canvas.addGraphic(new PrimitiveGraphic<>(
                new AnchoredText(100, 200, "Here is some\nsample text\nthat is wrapped\nonto multiple\nlines"),
                textStyle,
                mlRend));
        
        canvas.addGraphic(JGraphics.marker(new OrientedPoint2D(100, 300), 
                Styles.fillStroke(new Color(255, 128, 128, 64), null).and(Styles.MARKER_RADIUS, 12)));
        PrimitiveGraphic<AnchoredIcon, Graphics2D> icon = JGraphics.icon(testIcon(), 100, 300);
        icon.setStyle(textStyle);
        canvas.addGraphic(icon);
        
        canvas.addGraphic(JGraphics.marker(new OrientedPoint2D(100, 400), 
                Styles.fillStroke(new Color(255, 128, 128, 64), null).and(Styles.MARKER_RADIUS, 12)));
        PrimitiveGraphic<AnchoredImage, Graphics2D> image = JGraphics.image(100, 400, 48, 48, testImage(), null);
        image.setStyle(textStyle);
        canvas.addGraphic(image);
        
        ObjectStyler os = new ObjectStyler();
        LabeledShapeGraphic lsg = new LabeledShapeGraphic("Here is some sample text that will be automatically wrapped onto multiple lines", 
                new Rectangle(200, 50, 100, 200), os);
        os.setLabelDelegate(input -> input+"");
        os.setLabelStyle(textStyle);
        lsg.setDragEnabled(true);
        canvas.addGraphic(lsg);
        
        EditorRegistration.registerEditors();
        
        RollupPanel rp = new RollupPanel();
        AttributeSetPropertyModel apm = new AttributeSetPropertyModel(textStyle,
                ImmutableMap.of(Styles.FONT, String.class, Styles.FONT_SIZE, Float.class,
                        Styles.OFFSET, Point.class));
        rp.add(new MPanel("Font", PropertySheet.forModel(apm)));
        jScrollPane1.setViewportView(rp);
        revalidate();
    }
    
    private static Icon testIcon() {
        return new Icon() {
            @Override
            public int getIconWidth() {
                return 30;
            }
            @Override
            public int getIconHeight() {
                return 30;
            }
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.red);
                g2.draw(new Line2D.Double(x, y, x+30, y+30));
                g2.draw(new Line2D.Double(x+30, y, x, y+30));
            }
        };
    }
    
    private static Image testImage() {
        try {
            return ImageIO.read(AnchorTestFrame.class.getResource("resources/cherries.png"));
        } catch (IOException ex) {
            Logger.getLogger(AnchorTestFrame.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jComboBox1 = new javax.swing.JComboBox();
        jButton7 = new javax.swing.JButton();
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
        jButton1.addActionListener(this::jButton1ActionPerformed);
        jToolBar1.add(jButton1);

        jButton2.setText("Middle");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(this::jButton2ActionPerformed);
        jToolBar1.add(jButton2);

        jButton3.setText("End");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(this::jButton3ActionPerformed);
        jToolBar1.add(jButton3);
        jToolBar1.add(jSeparator1);

        jLabel2.setText("alignment-baseline:");
        jToolBar1.add(jLabel2);

        jButton4.setText("Baseline");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(this::jButton4ActionPerformed);
        jToolBar1.add(jButton4);

        jButton5.setText("Middle");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(this::jButton5ActionPerformed);
        jToolBar1.add(jButton5);

        jButton6.setText("Hanging");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(this::jButton6ActionPerformed);
        jToolBar1.add(jButton6);
        jToolBar1.add(jSeparator2);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);
        jToolBar1.add(jComboBox1);

        jButton7.setText("Clear anchors");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(this::jButton7ActionPerformed);
        jToolBar1.add(jButton7);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(canvas, java.awt.BorderLayout.CENTER);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(200, 22));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 22));
        getContentPane().add(jScrollPane1, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        textStyle.put(Styles.TEXT_ANCHOR, Styles.TEXT_ANCHOR_START);
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null));
        canvas.repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        textStyle.put(Styles.TEXT_ANCHOR, Styles.TEXT_ANCHOR_MIDDLE);
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null));
        canvas.repaint();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        textStyle.put(Styles.TEXT_ANCHOR, Styles.TEXT_ANCHOR_END);
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null));
        canvas.repaint();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        textStyle.put(Styles.ALIGN_BASELINE, Styles.ALIGN_BASELINE_BASELINE);
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null));
        canvas.repaint();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        textStyle.put(Styles.ALIGN_BASELINE, Styles.ALIGN_BASELINE_MIDDLE);
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null));
        canvas.repaint();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        textStyle.put(Styles.ALIGN_BASELINE, Styles.ALIGN_BASELINE_HANGING);
        jComboBox1.setSelectedItem(Styles.anchorOf(textStyle, null));
        canvas.repaint();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        Anchor anchor = (Anchor) jComboBox1.getSelectedItem();
        textStyle.put(Styles.TEXT_ANCHOR, Styles.toTextAnchor(anchor));
        textStyle.put(Styles.ALIGN_BASELINE, Styles.toAlignBaseline(anchor));
        canvas.repaint();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        textStyle.remove(Styles.TEXT_ANCHOR);
        textStyle.remove(Styles.ALIGN_BASELINE);
        canvas.repaint();
    }//GEN-LAST:event_jButton7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | javax.swing.UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnchorTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new AnchorTestFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent canvas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private com.googlecode.blaisemath.graphics.swing.MultilineTextRenderer mlRend;
    // End of variables declaration//GEN-END:variables
}
