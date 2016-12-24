/*
 * Copyright 2015 elisha.
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
package com.googlecode.blaisemath.graphics.testui;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicUpdater;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.core.UpdatingGraphicComposite;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.LabeledShapeGraphic;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredText;
import com.googlecode.blaisemath.util.SetSelectionModel;
import com.googlecode.blaisemath.util.swing.AnimationStep;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.Timer;

/**
 *
 * @author elisha
 */
public class UpdatingGraphicCompositeTestFrame extends javax.swing.JFrame {
    
    static final Set<String> WORDS = words();
    UpdatingGraphicComposite<Graphics2D,String> ug1 = UpdatingGraphicComposite.create(updater1());
    
    /**
     * Creates new form HelloWorldTest
     */
    public UpdatingGraphicCompositeTestFrame() {
        initComponents();
//        canvas.setBackground(Color.black);
        PanAndZoomHandler.install(canvas);
        
        ug1.setObjects(WORDS, loc1());
        canvas.addGraphic(ug1.getGraphic());
        
        canvas.getSelectionModel().addPropertyChangeListener(
                SetSelectionModel.SELECTION_PROPERTY, new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println(evt.getNewValue());
            }
        });
    }
    
    private static Set<String> words() {
        Random r = new Random();
        Set<String> res = Sets.newLinkedHashSet();
        for (int i = 0; i < 100; i++) {
            res.add(""+(char)('a'+r.nextInt(26))+(char)('a'+r.nextInt(26))+(char)('a'+r.nextInt(26)));
        }
        return res;
    }
    
    private static Function<String,Rectangle2D> loc1() {
        final Random r = new Random();
        return new Function<String,Rectangle2D>(){
            @Override
            public Rectangle2D apply(String input) {
                char c1 = input.charAt(0);
                char c2 = input.charAt(1);
                return new Rectangle2D.Double((c1-'a')*50+r.nextInt(50), (c2-'a')*50+r.nextInt(30), 40, 15);
            }
        };
    }
    
    private static Function<String,Rectangle2D> loc2() {
        final Random r = new Random();
        return new Function<String,Rectangle2D>(){
            @Override
            public Rectangle2D apply(String input) {
                char c1 = input.charAt(0);
                char c2 = input.charAt(1);
                return new Rectangle2D.Double((c2-'a')*50+r.nextInt(50), (c1-'a')*50+r.nextInt(30), 40, 15);
            }
        };
    }
    
    private static GraphicUpdater<Graphics2D,String> updater1() {
        return new GraphicUpdater<Graphics2D,String>(){
            @Override
            public Graphic<Graphics2D> update(String e, Rectangle2D bounds, Graphic<Graphics2D> existing) {
                if (existing == null || !(existing instanceof PrimitiveGraphic)) {
                    return JGraphics.text(new AnchoredText(bounds.getX(), bounds.getY(), e));
                } else {
                    PrimitiveGraphic<AnchoredText,Graphics2D> gfc = (PrimitiveGraphic<AnchoredText,Graphics2D>) existing;
                    gfc.getPrimitive().setLocation(bounds.getX(), bounds.getY());
                    return gfc;
                }
            }
        };
    }
    
    private static GraphicUpdater<Graphics2D,String> updater2() {
        return new GraphicUpdater<Graphics2D,String>(){
            @Override
            public Graphic<Graphics2D> update(String e, Rectangle2D bounds, Graphic<Graphics2D> existing) {
                if (existing == null || !(existing instanceof LabeledShapeGraphic)) {
                    ObjectStyler<String> styler = new ObjectStyler<String>();
                    styler.setStyleConstant(Styles.defaultShapeStyle());
                    styler.setLabelDelegate(Functions.toStringFunction());
                    styler.setLabelStyleConstant(Styles.defaultTextStyle());
                    return new LabeledShapeGraphic<String>(e, bounds, styler);
                } else {
                    LabeledShapeGraphic<String> gfc = (LabeledShapeGraphic<String>) existing;
                    gfc.setPrimitive(bounds);
                    return gfc;
                }
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        canvas = new com.googlecode.blaisemath.graphics.swing.JGraphicComponent();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        jButton1.setText("Loc1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jButton2.setText("Loc2");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton3.setText("Updater1");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton4.setText("Updater2");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);
        getContentPane().add(canvas, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        final Map<String, Rectangle2D> cur = Maps.newHashMap(ug1.getObjectBounds());
        final Map<String, Rectangle2D> nue = Maps.newHashMap(Maps.asMap(WORDS, loc1()));
        
        AnimationStep.animate(0, 10, 10, new AnimationStep(){
            @Override
            public void run(int i, double pct) {
                ug1.setObjects(WORDS, interpolate(cur, nue, pct));
            }
        }).start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private Function<String, Rectangle2D> interpolate(final Map<String, Rectangle2D> cur, final Map<String, Rectangle2D> nue, final double pct) {
        return new Function<String, Rectangle2D>(){
            @Override
            public Rectangle2D apply(String input) {
                double cpct = 1-pct;
                Rectangle2D r1 = cur.get(input);
                Rectangle2D r2 = nue.get(input);
                return new Rectangle2D.Double(
                        r1.getX()*cpct + r2.getX()*pct, 
                        r1.getY()*cpct + r2.getY()*pct,
                        r1.getWidth()*cpct + r2.getWidth()*pct,
                        r1.getHeight()*cpct + r2.getHeight()*pct);
            }
        };
    }
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        final Map<String, Rectangle2D> cur = Maps.newHashMap(ug1.getObjectBounds());
        final Map<String, Rectangle2D> nue = Maps.newHashMap(Maps.asMap(WORDS, loc2()));
        
        AnimationStep.animate(0, 10, 10, new AnimationStep(){
            @Override
            public void run(int i, double pct) {
                ug1.setObjects(WORDS, interpolate(cur, nue, pct));
            }
        }).start();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        ug1.setUpdater(updater1());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        ug1.setUpdater(updater2());
    }//GEN-LAST:event_jButton4ActionPerformed

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
            java.util.logging.Logger.getLogger(UpdatingGraphicCompositeTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UpdatingGraphicCompositeTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UpdatingGraphicCompositeTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UpdatingGraphicCompositeTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UpdatingGraphicCompositeTestFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent canvas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
