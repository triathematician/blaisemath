/**
 * CreateRectangleGesture.java Created Oct 10, 2014
 */
package com.googlecode.blaisemath.gesture.graphics;

import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

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
 * Gesture for adding a rectangle to the canvas.
 * 
 * @author Elisha
 */
public class CreateRectangleGesture extends CreateGraphicGesture {
    
    public CreateRectangleGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Draw rectangle", "Drag from one corner of the rectangle to the other.");
    }

    @Override
    protected Graphic<Graphics2D> createGraphic() {
        if (pressPoint != null && locPoint != null) {
            Rectangle2D.Double rect = new Rectangle2D.Double();
            rect.setFrameFromDiagonal(pressPoint, locPoint);
            return JGraphics.shape(rect, Styles.DEFAULT_SHAPE_STYLE.copy());
        }  else {
            return null;
        }
    }
    
}
