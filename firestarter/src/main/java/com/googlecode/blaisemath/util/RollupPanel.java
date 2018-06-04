/**
 * RollupPanel.java
 * Created on Jun 25, 2009
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * <p>
 *   Combines several collapsible components into a single panel. Each component
 *   is wrapped by an {@link MPanel} so that it has a displayed title bar and
 *   expand/collapse buttons.
 * </p>
 *
 * @author Elisha Peterson
 */
public class RollupPanel extends JPanel implements Scrollable {

    public RollupPanel() {
        VerticalLayout layout = new VerticalLayout();
        setLayout(layout);
        super.addImpl(layout.getVerticalSpacer(), null, -1);
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof MPanel) {
            super.addImpl(comp, constraints, index);
        } else if (comp != null && constraints instanceof String) {
            super.addImpl(new MPanel((String) constraints, comp), constraints, index);
        } else {
            super.addImpl(new MPanel(comp), constraints, index);
        }
    }

    @Override
    public void remove(Component comp) {
        Component[] component = this.getComponents();
        for (int i = component.length; --i >= 0;) {
            if (component[i] == comp || (component[i] instanceof MPanel 
                    && ((MPanel) component[i]).getPrimaryComponent() == comp)) {
                super.remove(i);
            }
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
