package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.PrimitiveGraphic;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import com.googlecode.blaisemath.style.Styles;
import java.awt.EventQueue;

public class PanAndZoomTestFrame extends javax.swing.JFrame {

    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent gc;
    private javax.swing.JLabel jLabel1;

    public PanAndZoomTestFrame() {
        initComponents();
        PrimitiveGraphic g1 = JGraphics.shape(new Ellipse2D.Double(50,50,100,100),
                Styles.fillStroke(Color.blue, Color.red));
        g1.setSelectionEnabled(true);
        gc.addGraphic(g1);
        
        PrimitiveGraphic g2 = JGraphics.shape(new Rectangle2D.Double(60,90,100,100));
        gc.addGraphic(g2);
        
        // all it takes to add selection capability!
        gc.setSelectionEnabled(true);
        gc.getSelectionModel().addPropertyChangeListener(evt -> System.out.println(evt.getPropertyName()+" : "+evt.getNewValue()));

        // init pan and zoom
        PanAndZoomHandler.install(gc);
    }

    private void initComponents() {

        gc = new com.googlecode.blaisemath.graphics.swing.JGraphicComponent();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(gc, java.awt.BorderLayout.CENTER);

        jLabel1.setText("<html>Test the mouse wheel, drag to pan, and alt+drag to create a zoom box. Should be restricted to mouse button 1. Shift+drag should restrict movement to x or y direction.");
        getContentPane().add(jLabel1, java.awt.BorderLayout.PAGE_START);

        pack();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new PanAndZoomTestFrame().setVisible(true));
    }
    
}
