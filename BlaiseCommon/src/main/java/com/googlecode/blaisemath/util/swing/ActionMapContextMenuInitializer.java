/**
 * ActionMapContextMenuInitializer.java
 * Created on Sep 23, 2014
 */
package com.googlecode.blaisemath.util.swing;

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

import static com.google.common.base.Preconditions.checkNotNull;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 * Initializes a context menu using specified keys in an {@link ActionMap}.
 * @author petereb1
 */
public final class ActionMapContextMenuInitializer<S> implements ContextMenuInitializer<S> {

    /** Optional key for submenu heading. If null, no submenu is created. */
    @Nullable
    private final String submenu;
    private final ActionMap am;
    private final String[] actions;

    public ActionMapContextMenuInitializer(ActionMap am, String... actions) {
        this(null, am, actions);
    }

    public ActionMapContextMenuInitializer(String submenu, ActionMap am, String... actions) {
        this.am = checkNotNull(am);
        this.actions = actions;
        this.submenu = submenu;
    }
    
    public void initContextMenu(JPopupMenu popup, S src, Point2D point,
            Object focus, Set selection) {
        if (popup.getComponentCount() > 0) {
            popup.addSeparator();
        }
        JMenu addTo = submenu == null ? null : new JMenu(submenu);
        for (String a : actions) {
            if (a == null) {
                if (addTo == null) {
                    popup.addSeparator();
                } else {
                    addTo.add(new JSeparator());
                }
            } else {
                Action action = am.get(a);
                if (action == null) {
                    Logger.getLogger(ActionMapContextMenuInitializer.class.getName())
                            .log(Level.WARNING, "Action not found: {0}", a);
                } else {
                    ActionWithSource act = new ActionWithSource(action, src);
                    if (addTo == null) {
                        popup.add(act);
                    } else {
                        addTo.add(act);
                    }
                }
            }
        }
        if (addTo != null) {
            popup.add(addTo);
        }
    }
    
    private static class ActionWithSource implements Action {
        private final Object source;
        private final Action delegate;

        private ActionWithSource(Action delegate, Object source) {
            this.source = source;
            this.delegate = delegate;
        }
        
        public void actionPerformed(ActionEvent e) {
            e.setSource(source);
            delegate.actionPerformed(e);
        }

        public Object getValue(String key) {
            return delegate.getValue(key);
        }

        public void putValue(String key, Object value) {
            delegate.putValue(key, value);
        }

        public void setEnabled(boolean b) {
            delegate.setEnabled(b);
        }

        public boolean isEnabled() {
            return delegate.isEnabled();
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            // not supported
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            // not supported
        }
    }

}
