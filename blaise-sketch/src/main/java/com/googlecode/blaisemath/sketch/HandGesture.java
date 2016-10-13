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


import static com.google.common.base.Preconditions.checkArgument;
import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.MouseGestureSupport;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import static com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler.setDesiredLocalBounds;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import javax.swing.JLayer;

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
    
    /** Basic pan mode */
    private static final String PAN_MODE = "Button1";
    /** Mode for restricted movement */
    private static final String RESTRICTED_MOVEMENT_MODE = "Shift+Button1";
    /** Allow user to release mouse button and still do movement */
    private static final String RESTRICTED_MOVEMENT_MODE_ALT = "Shift";
    
    /** Window location mouse was first pressed at. */
    private transient Point pressedAt = null;
    /** Stores keyboard modifiers for mouse. */
    private transient String mode = null;
    /** Old bounds for the window. */
    private transient Rectangle2D oldLocalBounds;
    
    
    /**
     * Initialize gesture with given orchestrator
     * @param orchestrator the gesture orchestrator
     */
    public HandGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Pan and zoom canvas", "Allows panning and zooming of the background canvas.");
        checkArgument(orchestrator.getComponent() instanceof JGraphicComponent);
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
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        initMouseGesture(e);
    }
    
    private void initMouseGesture(MouseEvent e) {
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        if (PAN_MODE.equals(mode) || RESTRICTED_MOVEMENT_MODE.equals(mode)) {
            pressedAt = e.getPoint();
        }
        if (PAN_MODE.equals(mode) || RESTRICTED_MOVEMENT_MODE.equals(mode)) {
            // pan mode
            Component component = orchestrator.getComponent();
            oldLocalBounds = PanAndZoomHandler.getLocalBounds((JGraphicComponent) component);
        } else {
            // ignore
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (pressedAt == null) {
            initMouseGesture(e);
        }
        String mouseMods = MouseEvent.getModifiersExText(e.getModifiersEx());
        if (PAN_MODE.equals(mode) || RESTRICTED_MOVEMENT_MODE.equals(mode)) { 
            boolean restrictedMovementMode = RESTRICTED_MOVEMENT_MODE.equals(mouseMods) 
                    || RESTRICTED_MOVEMENT_MODE_ALT.equals(mouseMods);
            mouseDraggedPanMode(e.getPoint(), restrictedMovementMode);
        } else {
            // ignore
        }
    }
    
    private void mouseDraggedPanMode(Point winPt, boolean restrictedMovementMode) {
        JGraphicComponent component = (JGraphicComponent) orchestrator.getComponent();
        if (restrictedMovementMode) {
            if (Math.abs(winPt.y - pressedAt.y) < Math.abs(winPt.x - pressedAt.x)) {
                winPt.y = pressedAt.y;
            } else {
                winPt.x = pressedAt.x;
            }
        }
        double dx = (winPt.x - pressedAt.x) * component.getInverseTransform().getScaleX();
        double dy = (winPt.y - pressedAt.y) * component.getInverseTransform().getScaleY();

        setDesiredLocalBounds(component,
                new Rectangle2D.Double(
                        oldLocalBounds.getX() - dx, oldLocalBounds.getY() - dy, 
                        oldLocalBounds.getWidth(), oldLocalBounds.getHeight()));
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDragged(e);
        pressedAt = null;
        oldLocalBounds = null;
        mode = null;
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
    
    
}
