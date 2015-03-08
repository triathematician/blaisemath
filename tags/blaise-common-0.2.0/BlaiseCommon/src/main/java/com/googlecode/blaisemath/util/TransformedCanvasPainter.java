/**
 * TransformedCanvasPainter.java
 * Created on Sep 22, 2014
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2015 Elisha Peterson
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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Draws on a {@link Graphics2D} using the component's transform.
 * 
 * @author petereb1
 */
public abstract class TransformedCanvasPainter implements CanvasPainter<Graphics2D> {

    @Override
    public void paint(Component component, Graphics2D canvas) {
        if (!(component instanceof TransformedCoordinateSpace)) {
            Logger.getLogger(TransformedCanvasPainter.class.getName()).log(Level.FINE,
                    "Painting on a component that is not a TransformedCoordinateSpace");
            paintTransformed(component, canvas);
        } else {
            AffineTransform oldTransform = canvas.getTransform();
            TransformedCoordinateSpace gc = (TransformedCoordinateSpace) component;
            AffineTransform tr = gc.getTransform();
            if (tr != null) {
                canvas.transform(tr);
            }
            paintTransformed(component, canvas);
            canvas.setTransform(oldTransform);
        }
    }
    
    /**
     * Paint the canvas in its transformed state, rather than its window state.
     * @param comp the component
     * @param canvas the canvas
     */
    public abstract void paintTransformed(Component comp, Graphics2D canvas);

}
