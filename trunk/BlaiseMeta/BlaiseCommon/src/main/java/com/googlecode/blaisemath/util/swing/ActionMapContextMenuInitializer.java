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

import java.awt.geom.Point2D;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JPopupMenu;

/**
 * Initializes a context menu using specified keys in an {@link ActionMap}.
 * @author petereb1
 */
public final class ActionMapContextMenuInitializer<S> implements ContextMenuInitializer<S> {

    private final ActionMap am;
    private final String[] actions;

    public ActionMapContextMenuInitializer(ActionMap am, String... actions) {
        this.am = am;
        this.actions = actions;
    }
    
    public void initContextMenu(JPopupMenu popup, S src, Point2D point,
            Object focus, Set selection) {
        for (String a : actions) {
            Action action = am.get(a);
            if (action == null) {
                Logger.getLogger(ActionMapContextMenuInitializer.class.getName())
                        .log(Level.WARNING, "Action not found: {0}", a);
            } else {
                popup.add(action);
            }
        }
    }

}
