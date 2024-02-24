package com.googlecode.blaisemath.graphics.swing;

/*-
 * #%L
 * blaise-graphics
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
import com.googlecode.blaisemath.graphics.swing.render.ClippedImageRenderer;
import com.googlecode.blaisemath.primitive.ClippedImage;
import com.googlecode.blaisemath.style.Styles;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClippedImageTestFrame extends javax.swing.JFrame {

    public ClippedImageTestFrame() {
        try {
            JGraphicComponent canvas = new JGraphicComponent();
            canvas.setBackground(Color.black);
            PanAndZoomHandler.install(canvas);
            ClippedImage image = new ClippedImage(getClass().getResource("resources/torus.png"), new Ellipse2D.Double(5, 5, 300, 300));
            PrimitiveGraphic<ClippedImage, Graphics2D> pg1 = new PrimitiveGraphic<>(image, Styles.strokeWidth(Color.red, 2f),
                    new ClippedImageRenderer());
            canvas.addGraphic(pg1);
            setContentPane(canvas);

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            pack();
        } catch (IOException ex) {
            Logger.getLogger(ClippedImageTestFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        // expect a torus image to show up inside a circle, with black empty region on top and below
        EventQueue.invokeLater(() -> new ClippedImageTestFrame().setVisible(true));
    }

}
