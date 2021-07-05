package com.googlecode.blaisemath.util.swing

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.event.*

/*
* #%L
* BlaiseCommon
* --
* Copyright (C) 2014 - 2021 Elisha Peterson
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
*/ /**
 * Utilities for handling [MouseEvent]s.
 *
 * @author Elisha Peterson
 */
object MouseEvents {
    /**
     * Delegate a mouse event by type to the provided listener.
     * @param e mouse event
     * @param l listener to delegate to
     */
    fun delegateEvent(e: MouseEvent?, l: MouseListener?) {
        when (e.getID()) {
            MouseEvent.MOUSE_ENTERED -> l.mouseEntered(e)
            MouseEvent.MOUSE_EXITED -> l.mouseExited(e)
            MouseEvent.MOUSE_PRESSED -> l.mousePressed(e)
            MouseEvent.MOUSE_RELEASED -> l.mouseReleased(e)
            MouseEvent.MOUSE_CLICKED -> l.mouseClicked(e)
            else -> {
            }
        }
    }

    /**
     * Delegate a mouse motion event by type to the provided listener.
     * @param e mouse event
     * @param l listener to delegate to
     */
    fun delegateMotionEvent(e: MouseEvent?, l: MouseMotionListener?) {
        when (e.getID()) {
            MouseEvent.MOUSE_DRAGGED -> l.mouseDragged(e)
            MouseEvent.MOUSE_MOVED -> l.mouseMoved(e)
            else -> {
            }
        }
    }

    /**
     * Delegate a mouse wheel event by type to the provided listener.
     * @param e mouse event
     * @param l listener to delegate to
     */
    fun delegateWheelEvent(e: MouseWheelEvent?, l: MouseWheelListener?) {
        if (e.getID() == MouseEvent.MOUSE_WHEEL) {
            l.mouseWheelMoved(e)
        }
    }
}