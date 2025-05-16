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
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer;
import com.googlecode.blaisemath.style.Styles;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class TooltipTestFrame extends javax.swing.JFrame {

    private com.googlecode.blaisemath.graphics.swing.JGraphicComponent gc;
    private javax.swing.JLabel jLabel1;

    public TooltipTestFrame() {
        initComponents();
        PrimitiveGraphic g1 = JGraphics.shape(new Ellipse2D.Double(50,50,100,100));
        g1.setDefaultTooltip("Ellipse");
        gc.addGraphic(g1);
        
        PrimitiveGraphic g2 = new PrimitiveGraphic(new Rectangle2D.Double(60,90,100,100), 
                Styles.DEFAULT_SHAPE_STYLE, ShapeRenderer.getInstance()) {
            @Override
            public String getTooltip(Point2D p, Object canvas) {
                return ""+p;
            }
        };
        g2.setTooltipEnabled(true);
        gc.addGraphic(g2);
    }

    private void initComponents() {
        gc = new com.googlecode.blaisemath.graphics.swing.JGraphicComponent();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(gc, java.awt.BorderLayout.CENTER);

        jLabel1.setText("<html>Test that the square gives a tooltip that depends on where you're at, while the circle gives just one tooltip.");
        getContentPane().add(jLabel1, java.awt.BorderLayout.PAGE_START);

        pack();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new TooltipTestFrame().setVisible(true));
    }
}
