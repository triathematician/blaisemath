/**
 * AbstractGraphicDragger.java
 * Created Jul 31, 2012
 */
package com.googlecode.blaisemath.graphics.core;

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

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * <p>
 *  Provides hooks for drag mouse gestures. Instead of working with all six mouse methods, subclasses can work with
 *  two or three (dragInitiated, dragInProgress, and optionally dragCompleted).
 * </p>
 * @author elisha
 */
public abstract class GMouseDragHandler extends MouseAdapter {
    
    /** Stores the starting point of the drag */
    protected Point2D start;

    /**
     * Called when the mouse is pressed, starting the drag
     * @param e the source event
     * @param start the initial drag point
     */
    public abstract void mouseDragInitiated(GMouseEvent e, Point2D start);

    /**
     * Called as mouse drag is in progress
     * @param e the source event (stores the point and graphic for the event)
     * @param start the initial drag point
     */
    public abstract void mouseDragInProgress(GMouseEvent e, Point2D start);

    /**
     * Called when the mouse is released, finishing the drag. Does nothing by default.
     * @param e the source event (stores the point and graphic for the event)
     * @param start the initial drag point
     */
    public void mouseDragCompleted(GMouseEvent e, Point2D start) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    @Override
    public final void mousePressed(MouseEvent e) {
        GMouseEvent gme = (GMouseEvent) e;
        start = gme.getGraphicLocation();
        mouseDragInitiated(gme, start);
        e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        e.consume();
    }

    @Override
    public final void mouseDragged(MouseEvent e) {
        if (start == null) {
            mousePressed(e);
        }
        mouseDragInProgress((GMouseEvent) e, start);
        e.consume();
    }

    @Override
    public final void mouseReleased(MouseEvent e) {
        if (start != null) {
            mouseDragCompleted((GMouseEvent) e, start);
            e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            start = null;
        }
    }

    @Override
    public final void mouseExited(MouseEvent e) {
        if (start != null) {
            mouseReleased(e);
        }
    }
    
}
