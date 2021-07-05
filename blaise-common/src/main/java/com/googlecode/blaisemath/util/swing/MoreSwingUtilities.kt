package com.googlecode.blaisemath.util.swing

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
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
*/ /**
 * Utilities for swing.
 * @see SwingUtilities
 *
 * @author Elisha Peterson
 */
object MoreSwingUtilities {
    /**
     * Executes the given runnable now, if the current thread is the swing event
     * dispatch thread, or later on the EDT, if not.
     * @param r job to run
     */
    fun invokeOnEventDispatchThread(r: Runnable?) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run()
        } else {
            SwingUtilities.invokeLater(r)
        }
    }

    /**
     * Registers a listener for the component to request focus when it is shown
     * @param c the component to focus
     */
    fun requestFocusWhenShown(c: JComponent?) {
        val listener: AncestorListener = object : AncestorListener {
            override fun ancestorAdded(event: AncestorEvent?) {
                c.requestFocusInWindow()
                c.removeAncestorListener(this)
            }

            override fun ancestorRemoved(event: AncestorEvent?) {}
            override fun ancestorMoved(event: AncestorEvent?) {}
        }
        c.addAncestorListener(listener)
    }
}