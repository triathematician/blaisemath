/**
 * CreateImageGesture.java Created Oct 3, 2014
 */
package com.googlecode.blaisemath.gesture;

import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.ImageRenderer;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.TextRenderer;
import com.googlecode.blaisemath.graphics.swing.TransformedCanvasPainter;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredImage;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/*
 * #%L
 * BlaiseSketch
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

/**
 * Gesture for adding a linked image to the canvas.
 * 
 * @author Elisha
 */
public class CreateImageGesture extends SketchGesture<JGraphicComponent> {
    
    private final JFileChooser chooser = new JFileChooser();
    
    private static final Renderer<AnchoredImage, Graphics2D> REND = ImageRenderer.getInstance();
    
    public CreateImageGesture() {
        super("Place image", "Click where you want to place an image.");
    }

    @Override
    public void finish(JGraphicComponent view) {
        if (locPoint != null) {
            File file = JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(null)
                    ? chooser.getSelectedFile() : null;
            Image image = null;
            if (file != null) {
                try {
                    image = ImageIO.read(file);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to load image.", "Error", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(CreateImageGesture.class.getName()).log(Level.FINE, "Invalid image location", ex);
                }
            }
            if (image != null) {
                URI uri = file.toURI();
                PrimitiveGraphic<AnchoredImage, Graphics2D> gfc 
                        = JGraphics.image(new AnchoredImage(locPoint.getX(), locPoint.getY(), image, uri.toString()));
                gfc.setSelectionEnabled(true);
                gfc.setDragEnabled(true);
                view.addGraphic(gfc);
            }
        }
    }

    @Override
    public void paint(Graphics g, JGraphicComponent view) {
        new Painter().paint(view, (Graphics2D) g);
    }
    
    private final class Painter extends TransformedCanvasPainter {
        @Override
        public void paintTransformed(JGraphicComponent jgc, Graphics2D gd) {
            if (locPoint != null) {
                TextRenderer.getInstance().render(new AnchoredText(locPoint, "image goes here"), 
                        Styles.DEFAULT_TEXT_STYLE, (Graphics2D) gd);
            }
        }
    }
    
}
