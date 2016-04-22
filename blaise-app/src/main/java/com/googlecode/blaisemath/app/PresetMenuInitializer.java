/**
 * PresetMenuInitializer.java
 * Created Jan 2016
 */
package com.googlecode.blaisemath.app;

/*
 * #%L
 * BlaiseApp
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Set;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Initializes a context menu with a set list of items.
 * @author elisha
 */
public final class PresetMenuInitializer implements ContextMenuInitializer {

    private final List<JMenuItem> items;
    
    public PresetMenuInitializer(List<JMenuItem> items) {
        checkNotNull(items);
        this.items = items;
    }

    @Override
    public void initContextMenu(JPopupMenu popup, Object src, Point2D point, Object focus, Set selection) {
        for (JMenuItem it : items) {
            if (it == null) {
                popup.addSeparator();
            } else {
                popup.add(it);
            }
        }
    }
    
}
