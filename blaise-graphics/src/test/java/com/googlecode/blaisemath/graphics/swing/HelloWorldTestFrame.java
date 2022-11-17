package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
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
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.SetSelectionModel;

import java.awt.*;
import java.awt.geom.Point2D;

public class HelloWorldTestFrame extends javax.swing.JFrame {

    public HelloWorldTestFrame() {
        JGraphicComponent canvas = new JGraphicComponent();
        canvas.setBackground(Color.black);
        PanAndZoomHandler.install(canvas);
        canvas.setSelectionEnabled(true);
        PrimitiveGraphic<Point2D, Graphics2D> pg1 = JGraphics.point(new Point(50, 50));
        pg1.setSelectionEnabled(true);
        canvas.addGraphic(pg1);
        canvas.addGraphic(JGraphics.point(new Point(100, 50), Styles.fillStroke(Color.yellow, Color.red)));
        canvas.addGraphic(JGraphics.path(new Rectangle(25, 25, 100, 50), Styles.strokeWidth(Color.blue, 2f)));
        setContentPane(canvas);
        canvas.getSelectionModel().addPropertyChangeListener(
                SetSelectionModel.SELECTION_PROPERTY, evt -> System.out.println(evt.getNewValue()));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    public static void main(String[] args) {
        // expect two colored dots inside a blue rectangle
        EventQueue.invokeLater(() -> new HelloWorldTestFrame().setVisible(true));
    }
}
