package com.googlecode.blaisemath.util.swing

import java.awt.event.*
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

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
*/

/** Executes the given runnable now, if the current thread is the swing event dispatch thread, or later on the EDT, if not. */
fun invokeOnEventDispatchThread(r: Runnable) {
    when {
        SwingUtilities.isEventDispatchThread() -> r.run()
        else -> SwingUtilities.invokeLater(r)
    }
}

/** Add listener to component that requests focus when it is shown. */
fun JComponent.requestFocusWhenShown() {
    addAncestorListener(object : AncestorListener {
        override fun ancestorAdded(event: AncestorEvent) {
            requestFocusInWindow()
            removeAncestorListener(this)
        }
        override fun ancestorRemoved(event: AncestorEvent) {}
        override fun ancestorMoved(event: AncestorEvent) {}
    })
}

//region MOUSE EVENTS

/** Delegate a mouse event by type to the provided listener. */
fun delegateEvent(e: MouseEvent, l: MouseListener) {
    when (e.id) {
        MouseEvent.MOUSE_ENTERED -> l.mouseEntered(e)
        MouseEvent.MOUSE_EXITED -> l.mouseExited(e)
        MouseEvent.MOUSE_PRESSED -> l.mousePressed(e)
        MouseEvent.MOUSE_RELEASED -> l.mouseReleased(e)
        MouseEvent.MOUSE_CLICKED -> l.mouseClicked(e)
        else -> {}
    }
}

/** Delegate a mouse motion event by type to the provided listener. */
fun delegateMotionEvent(e: MouseEvent, l: MouseMotionListener) {
    when (e.id) {
        MouseEvent.MOUSE_DRAGGED -> l.mouseDragged(e)
        MouseEvent.MOUSE_MOVED -> l.mouseMoved(e)
        else -> {}
    }
}

/** Delegate a mouse wheel event by type to the provided listener. */
fun delegateWheelEvent(e: MouseWheelEvent, l: MouseWheelListener) {
    when (e.id) {
        MouseEvent.MOUSE_WHEEL -> l.mouseWheelMoved(e)
        else -> {}
    }
}

//endregion