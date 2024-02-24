package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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
import java.awt.EventQueue;

import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.AbstractAction;

public class ContextMenuTestFrame extends javax.swing.JFrame {

    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent gc;
    private javax.swing.JLabel jLabel1;

    public ContextMenuTestFrame() {
        initComponents();
        PrimitiveGraphic g1 = JGraphics.shape(new Ellipse2D.Double(50,50,100,100));
        g1.addContextMenuInitializer((menu, src, point, focus, selection) -> {
            menu.add(""+point);
            menu.add(new AbstractAction("press me"){
                @Override
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

    private void initComponents() {
        gc = new com.googlecode.blaisemath.graphics.swing.JGraphicComponent();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(gc, java.awt.BorderLayout.CENTER);

        jLabel1.setText("<html>Should be up to 3 parts of the context menu: one for the circle, one for the selection, and one for the root graphics.");
        getContentPane().add(jLabel1, java.awt.BorderLayout.PAGE_START);

        pack();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new ContextMenuTestFrame().setVisible(true));
    }
}
