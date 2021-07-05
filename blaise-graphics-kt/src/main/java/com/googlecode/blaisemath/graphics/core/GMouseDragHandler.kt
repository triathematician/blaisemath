package com.googlecode.blaisemath.graphics.core

import java.awt.Cursor
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Point2D

/*
* #%L
* BlaiseGraphics
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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
 * Provides hooks for drag mouse gestures. Instead of working with all six mouse methods, subclasses can work with
 * two or three (dragInitiated, dragInProgress, and optionally dragCompleted).
 */
abstract class GMouseDragHandler : MouseAdapter() {

    /** Stores the starting point of the drag  */
    protected var start: Point2D? = null

    /** Called when the mouse is pressed, starting the drag */
    abstract fun mouseDragInitiated(e: GMouseEvent, start: Point2D?)
    /** Called as mouse drag is in progress */
    abstract fun mouseDragInProgress(e: GMouseEvent, start: Point2D)
    /** Called when the mouse is released, finishing the drag. */
    abstract fun mouseDragCompleted(e: GMouseEvent, start: Point2D)

    override fun mouseMoved(e: MouseEvent) {
        e.component.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
    }

    override fun mousePressed(e: MouseEvent) {
        val gme = e as GMouseEvent
        start = gme.graphicLocation
        mouseDragInitiated(gme, start)
        e.getComponent().cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        e.consume()
    }

    override fun mouseDragged(e: MouseEvent) {
        if (start == null) mousePressed(e)
        mouseDragInProgress(e as GMouseEvent, start!!)
        e.consume()
    }

    override fun mouseReleased(e: MouseEvent) {
        if (start != null) {
            mouseDragCompleted(e as GMouseEvent, start!!)
            e.component.cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
            start = null
        }
    }

    override fun mouseExited(e: MouseEvent) {
        if (start != null) mouseReleased(e)
    }
}