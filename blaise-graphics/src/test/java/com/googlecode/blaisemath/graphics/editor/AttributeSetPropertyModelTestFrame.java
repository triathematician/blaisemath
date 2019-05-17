package com.googlecode.blaisemath.graphics.editor;

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


import com.google.common.collect.ImmutableMap;
import com.googlecode.blaisemath.editor.EditorRegistration;
import com.googlecode.blaisemath.firestarter.PropertySheet;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import java.awt.Point;

public class AttributeSetPropertyModelTestFrame extends javax.swing.JFrame {

    /**
     * Creates new form AttributeSetPropertyModelTestFrame
     */
    public AttributeSetPropertyModelTestFrame() {
        initComponents();
        
        
        EditorRegistration.registerEditors();
        AttributeSet as = Styles.fillStroke(Color.white, Color.red);
        AttributeSetPropertyModel m = new AttributeSetPropertyModel(as,
                ImmutableMap.of(Styles.FILL, Color.class, Styles.STROKE, Color.class));
        rollupPanel1.add("AS Test", PropertySheet.forModel(m));
        
        jGraphicComponent1.addGraphic(JGraphics.point(new Point(20, 20), as));
        
        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rollupPanel1 = new com.googlecode.blaisemath.util.RollupPanel();
        jGraphicComponent1 = new com.googlecode.blaisemath.graphics.swing.JGraphicComponent();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(rollupPanel1, java.awt.BorderLayout.WEST);
        getContentPane().add(jGraphicComponent1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new AttributeSetPropertyModelTestFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent jGraphicComponent1;
    private com.googlecode.blaisemath.util.RollupPanel rollupPanel1;
    // End of variables declaration//GEN-END:variables
}
