/**
 * CreateImageGesture.java Created Oct 3, 2014
 */
package com.googlecode.blaisemath.gesture.swing;

import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.util.AnchoredImage;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
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
 * Copyright (C) 2015 - 2016 Elisha Peterson
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
public class CreateImageGesture extends CreateGraphicGesture {

    private static final Logger LOG = Logger.getLogger(CreateImageGesture.class.getName());
    
    private static final JFileChooser CHOOSER = new JFileChooser();
    
    private File imageFile;
    private Image image;
    
    public CreateImageGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Place image", "Click where you want to place an image.");
    }

    @Override
    public boolean activate() {
        if (JFileChooser.APPROVE_OPTION == CHOOSER.showOpenDialog(null)) {
            imageFile = CHOOSER.getSelectedFile();
            if (imageFile != null) {
                try {
                    image = ImageIO.read(imageFile);
                    active = true;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to load image.", "Error", JOptionPane.ERROR_MESSAGE);
                    LOG.log(Level.FINE, "Invalid image location", ex);
                }
            }
        }
        return active;
    }

    @Override
    protected Graphic<Graphics2D> createGraphic() {
        if (image != null && (movePoint != null || locPoint != null)) {
            URI uri = imageFile.toURI();
            Point2D pt = locPoint == null ? movePoint : locPoint;
            return JGraphics.image(new AnchoredImage(pt.getX(), pt.getY(), image, uri.toString()));
        } else {
            return null;
        }
    }
    
}
