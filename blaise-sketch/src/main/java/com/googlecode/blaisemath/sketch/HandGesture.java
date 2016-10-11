/*
 * Copyright 2015 elisha.
 *
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
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseGestures
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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


import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.MouseGestureSupport;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

/**
 * Controls pan and zoom on the graphic canvas. Activates only when the user
 * has the space bar depressed.
 * 
 * TODO - add space bar controls
 * TODO - add zoom controls
 * 
 * @author elisha
 */
public class HandGesture extends MouseGestureSupport {
    
    public HandGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Pan and zoom canvas", "Allows panning and zooming of the background canvas.");
    }
    
    @Override
    public String toString() {
        return "pan and zoom";
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        orchestrator.setComponentCursor(Cursor.MOVE_CURSOR);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isConsumed()) {
            return;
        }
        JGraphicComponent component = (JGraphicComponent) e.getComponent();
        Point2D.Double mouseLoc = new Point2D.Double(e.getPoint().x, e.getPoint().y);

        // ensure the point is within the window
        RectangularShape bounds = component.getBounds();
        mouseLoc.x = Math.max(mouseLoc.x, bounds.getMinX());
        mouseLoc.x = Math.min(mouseLoc.x, bounds.getMaxX());
        mouseLoc.y = Math.max(mouseLoc.y, bounds.getMinY());
        mouseLoc.y = Math.min(mouseLoc.y, bounds.getMaxY());

        PanAndZoomHandler.zoomPoint(component, component.toGraphicCoordinate(mouseLoc),
                (e.getWheelRotation() > 0) ? 1.05 : 0.95);
    }
    
    
}
