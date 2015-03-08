/**
 * CreateLineGesture.java Created Oct 10, 2014
 */
package com.googlecode.blaisemath.gesture.swing;

import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2015 Elisha Peterson
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
 * Gesture for adding a line to the canvas.
 * 
 * @author Elisha
 */
public class CreateLineGesture extends JGraphicCreatorGesture {
    
    public CreateLineGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Draw line", "Drag from one end of the line to the other.");
    }

    @Override
    protected Graphic<Graphics2D> createGraphic() {
        if (pressPoint != null && locPoint != null) {
            Line2D.Double line = new Line2D.Double(pressPoint, locPoint);
            return JGraphics.path(line, Styles.DEFAULT_PATH_STYLE.copy());
        } else {
            return null;
        }
    }
    
}
