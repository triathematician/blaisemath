package com.googlecode.blaisemath.util.swing;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Utilities for handling {@link MouseEvent}s.
 * 
 * @author Elisha Peterson
 */
public class MouseEvents {

    // utility class
    private MouseEvents() {
    }
    
    /**
     * Delegate a mouse event by type to the provided listener.
     * @param e mouse event
     * @param l listener to delegate to
     */
    public static void delegateEvent(MouseEvent e, MouseListener l) {
        switch(e.getID()) {
            case MouseEvent.MOUSE_ENTERED:
                l.mouseEntered(e);
                break;
            case MouseEvent.MOUSE_EXITED:
                l.mouseExited(e);
                break;
            case MouseEvent.MOUSE_PRESSED:
                l.mousePressed(e);
                break;
            case MouseEvent.MOUSE_RELEASED:
                l.mouseReleased(e);
                break;
            case MouseEvent.MOUSE_CLICKED:
                l.mouseClicked(e);
                break;
            default:
                // do nothing
                break;
        }
    }
    
    /**
     * Delegate a mouse motion event by type to the provided listener.
     * @param e mouse event
     * @param l listener to delegate to
     */
    public static void delegateMotionEvent(MouseEvent e, MouseMotionListener l) {
        switch(e.getID()) {
            case MouseEvent.MOUSE_DRAGGED:
                l.mouseDragged(e);
                break;
            case MouseEvent.MOUSE_MOVED:
                l.mouseMoved(e);
                break;
            default:
                // do nothing
                break;
        }
    }
    
    /**
     * Delegate a mouse wheel event by type to the provided listener.
     * @param e mouse event
     * @param l listener to delegate to
     */
    public static void delegateWheelEvent(MouseWheelEvent e, MouseWheelListener l) {
        if (e.getID() == MouseEvent.MOUSE_WHEEL) {
            l.mouseWheelMoved(e);
        }
    }
    
}
