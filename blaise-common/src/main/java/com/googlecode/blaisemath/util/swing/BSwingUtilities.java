package com.googlecode.blaisemath.util.swing;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Utilities for swing.
 * @author petereb1
 */
public class BSwingUtilities {

    // utility class
    private BSwingUtilities() {
    }
    
    /** 
     * Executes the given runnable now, if the current thread is the swing event
     * dispatch thread, or later on the EDT, if not.
     * @param r job to run
     */
    public static void invokeOnEventDispatchThread(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    
    /**
     * Registers a listener for the component to request focus when it is shown.
     * @param c the component to focus
     */
    public static void requestFocusWhenShown(final JComponent c) {
        AncestorListener listener = new AncestorListener(){
            @Override
            public void ancestorAdded(AncestorEvent event) {
                c.requestFocusInWindow();
                c.removeAncestorListener(this);
            }
            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }
            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        };
        c.addAncestorListener(listener);
    }
}
