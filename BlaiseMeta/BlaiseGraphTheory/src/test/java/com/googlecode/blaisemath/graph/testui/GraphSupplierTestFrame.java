/*
 * Copyright 2014 Elisha.
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

package com.googlecode.blaisemath.graph.testui;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.google.common.base.Supplier;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.modules.layout.ComponentCircleLayout;
import com.googlecode.blaisemath.graph.modules.layout.StaticSpringLayout;
import com.googlecode.blaisemath.graph.modules.suppliers.EdgeCountGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.EdgeProbabilityGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.CompleteGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.CycleGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.EmptyGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.GraphSupplierSupport;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.StarGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.WheelGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.PreferentialAttachmentGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.ProximityGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.WattsStrogatzGraphSupplier;
import java.awt.Rectangle;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Elisha
 */
public class GraphSupplierTestFrame extends javax.swing.JFrame {

    /**
     * Creates new form GraphSupplierTestFrame
     */
    public GraphSupplierTestFrame() {
        initComponents();
        supplierChooser.setModel(new DefaultComboBoxModel(new Object[] {
                new EmptyGraphSupplier(),
                new CompleteGraphSupplier(),
                new StarGraphSupplier(),
                new CycleGraphSupplier(),
                new WheelGraphSupplier(),
                new EdgeCountGraphSupplier(false, 50, 50),
                new EdgeProbabilityGraphSupplier(false, 50, .1f),
                new PreferentialAttachmentGraphSupplier(new StarGraphSupplier(false, 6).get()),
                new PreferentialAttachmentGraphSupplier(new StarGraphSupplier(false, 6).get(), 50, 2),
                new PreferentialAttachmentGraphSupplier(new StarGraphSupplier(false, 6).get(), 50, new float[]{.1f, .8f, .1f}),
                new WattsStrogatzGraphSupplier(false, 50, 2, .2f),
                new WattsStrogatzGraphSupplier(false, 50, 4, .1f),
                new ProximityGraphSupplier(false, 50, new Rectangle(0, 0, 100, 100), 5),
                new ProximityGraphSupplier(false, 50, new Rectangle(0, 0, 100, 100), 10),
                new ProximityGraphSupplier(false, 50, new Rectangle(0, 0, 100, 100), 20)
            }));
        layoutChooser.setModel(new DefaultComboBoxModel(new Object[] {
            StaticGraphLayout.ORIGIN,
            StaticGraphLayout.RANDOM,
            StaticGraphLayout.CIRCLE,
            new StaticSpringLayout(),
            new ComponentCircleLayout()
        }));
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
        jLabel1 = new javax.swing.JLabel();
        supplierChooser = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        nodeSpinner = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel3 = new javax.swing.JLabel();
        layoutChooser = new javax.swing.JComboBox();
        grapher = new com.googlecode.blaisemath.graph.view.GraphComponent();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        jLabel1.setText("Graph Supplier: ");
        jToolBar1.add(jLabel1);

        supplierChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supplierChooserActionPerformed(evt);
            }
        });
        jToolBar1.add(supplierChooser);

        jLabel4.setText(" for ");
        jToolBar1.add(jLabel4);

        nodeSpinner.setModel(new javax.swing.SpinnerNumberModel(20, 0, 100000, 10));
        nodeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                nodeSpinnerStateChanged(evt);
            }
        });
        jToolBar1.add(nodeSpinner);

        jLabel2.setText(" vertices");
        jToolBar1.add(jLabel2);
        jToolBar1.add(jSeparator1);

        jLabel3.setText(" Graph Layout: ");
        jToolBar1.add(jLabel3);

        layoutChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layoutChooserActionPerformed(evt);
            }
        });
        jToolBar1.add(layoutChooser);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(grapher, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void supplierChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supplierChooserActionPerformed
        updateGraph();
    }//GEN-LAST:event_supplierChooserActionPerformed

    private void nodeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_nodeSpinnerStateChanged
        updateGraph();
    }//GEN-LAST:event_nodeSpinnerStateChanged

    private void layoutChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layoutChooserActionPerformed
        StaticGraphLayout l = (StaticGraphLayout) layoutChooser.getSelectedItem();
        grapher.getLayoutManager().applyLayout(l, 50);
    }//GEN-LAST:event_layoutChooserActionPerformed

    private void updateGraph() {
        Supplier<Graph> s = (Supplier<Graph>) supplierChooser.getSelectedItem();
        int n = (Integer) nodeSpinner.getValue();
        if (s instanceof GraphSupplierSupport) {
            ((GraphSupplierSupport)s).setNodes(n);
        }
        grapher.setGraph(s.get());
        StaticGraphLayout l = (StaticGraphLayout) layoutChooser.getSelectedItem();
        grapher.getLayoutManager().applyLayout(l, 50);
    }
    
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
            java.util.logging.Logger.getLogger(GraphSupplierTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphSupplierTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphSupplierTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphSupplierTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GraphSupplierTestFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.googlecode.blaisemath.graph.view.GraphComponent grapher;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JComboBox layoutChooser;
    private javax.swing.JSpinner nodeSpinner;
    private javax.swing.JComboBox supplierChooser;
    // End of variables declaration//GEN-END:variables
}
