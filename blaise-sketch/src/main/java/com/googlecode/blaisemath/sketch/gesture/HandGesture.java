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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Controls panning the canvas.
 * 
 * @author elisha
 */
public class HandGesture extends ActivatingMouseGesture {
    
    private static final int ACTIVE_KEY = KeyEvent.VK_SPACE;
    
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
        super(orchestrator, "Hand", "Allows panning of the background canvas (activated by pressing SPACE).");
        checkArgument(orchestrator.getComponent() instanceof JGraphicComponent);

        eventsHandled = Arrays.asList(MouseEvent.MOUSE_MOVED, MouseEvent.MOUSE_PRESSED,
                MouseEvent.MOUSE_DRAGGED, MouseEvent.MOUSE_RELEASED);
        eventFilter = (Predicate<InputEvent>) (e -> active && eventsHandled.contains(e.getID()));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        setDesiredCursor(Cursor.MOVE_CURSOR);
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
        AffineTransform at = component.getInverseTransform();
        double dx = (winPt.x - pressedAt.x) * (at == null ? 1 : at.getScaleX());
        double dy = (winPt.y - pressedAt.y) * (at == null ? 1 : at.getScaleY());

        PanAndZoomHandler.setDesiredLocalBounds(component,
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
    
}
