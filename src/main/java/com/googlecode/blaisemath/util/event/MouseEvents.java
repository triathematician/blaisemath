/**
 * MouseEvents.java
 * Created Oct 3, 2014
 */
package com.googlecode.blaisemath.util.event;

/*
 * #%L
 * BlaiseCommon
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


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Utilities for handling {@link MouseEvent}s.
 * 
 * @author Elisha
 */
public class MouseEvents {

    // utility class
    private MouseEvents() {
    }
    
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
    
}
