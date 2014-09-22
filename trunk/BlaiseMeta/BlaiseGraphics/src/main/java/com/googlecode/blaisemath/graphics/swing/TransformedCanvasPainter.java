/**
 * TransformedCanvasPainter.java
 * Created on Sep 22, 2014
 */
package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
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

import static com.google.common.base.Preconditions.checkState;
import com.googlecode.blaisemath.util.CanvasPainter;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * Draws on a {@link JGraphicComponent} using the component's transform.
 * 
 * @author petereb1
 */
public abstract class TransformedCanvasPainter implements CanvasPainter<Graphics2D> {

    @Override
    public void paint(Component component, Graphics2D canvas) {
        checkState(component instanceof JGraphicComponent,
                "Attempted to paint on a non-JGraphicComponent");
        AffineTransform oldTransform = canvas.getTransform();
        JGraphicComponent gc = (JGraphicComponent) component;
        AffineTransform tr = gc.getTransform();
        if (tr != null) {
            canvas.transform(tr);
        }
        paintTransformed(gc, canvas);
        canvas.setTransform(oldTransform);
    }
    
    /**
     * Paint the canvas in its transformed state, rather than its window state.
     * @param comp the component
     * @param canvas the canvas
     */
    public abstract void paintTransformed(JGraphicComponent comp, Graphics2D canvas);

}
