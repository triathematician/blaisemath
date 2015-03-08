/**
 * CreateMarkerGesture.java Created Oct 3, 2014
 */
package com.googlecode.blaisemath.gesture.swing;

import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredImage;
import com.googlecode.blaisemath.util.OrientedPoint2D;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

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
 * Gesture for adding a point to the canvas.
 * 
 * @author Elisha
 */
public class CreateMarkerGesture extends JGraphicCreatorGesture {
    
    public CreateMarkerGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Place point", "Click where you want to create a point.");
    }

    @Override
    protected Graphic<Graphics2D> createGraphic() {
        Point2D pt = locPoint == null ? movePoint : locPoint;
        return pt == null ? null
                : JGraphics.marker(new OrientedPoint2D(pt), Styles.DEFAULT_POINT_STYLE.copy());
    }
    
}
