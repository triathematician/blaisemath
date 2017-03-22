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
package com.googlecode.blaisemath.sketch.gesture;

/*
 * #%L
 * BlaiseGestures
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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


import static com.google.common.base.Preconditions.checkArgument;
import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.ActivatingMouseGesture;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Arrays;

/**
 * Controls zoom on the graphics canvas.
 * 
 * @author elisha
 */
public class ZoomGesture extends ActivatingMouseGesture {
    
    /**
     * Initialize gesture with given orchestrator
     * @param orchestrator the gesture orchestrator
     */
    public ZoomGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Zoom canvas", "Allows zooming of the background canvas using the mouse wheel.");
        checkArgument(orchestrator.getComponent() instanceof JGraphicComponent);
        
        eventsHandled = Arrays.asList(MouseEvent.MOUSE_WHEEL);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isConsumed()) {
            return;
        }
        JGraphicComponent component = (JGraphicComponent) orchestrator.getComponent();
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
    
    //<editor-fold defaultstate="collapsed" desc="OTHER MOUSE HANDLERS">
    
    @Override
    public void mouseMoved(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // do nothing
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        // do nothing
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        // do nothing
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        // do nothing
    }
    
    //</editor-fold>
    
}
