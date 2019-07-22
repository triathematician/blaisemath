package com.googlecode.blaisemath.util;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;

/**
 * Controls layout vertically. The width of components is fixed by the parent's
 * size, but the height is determined by the internal components.
 */
public final class VerticalLayout extends GridBagLayout {

    private static final int X_MARGIN = 4;
    private static final int Y_MARGIN = 4;
    private static final int Y_SPACING = 4;
    
    private final GridBagConstraints dc;
    private final Component vSpacer;    

    public VerticalLayout() {
        dc = new GridBagConstraints();
        dc.fill = GridBagConstraints.HORIZONTAL;
        dc.weightx = 1.0;
        dc.gridx = 0;
        dc.gridy = 0;
        dc.ipadx = 0;
        dc.ipady = 0;
        vSpacer = Box.createVerticalGlue();
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        super.addLayoutComponent(comp, dc);
        dc.insets = new Insets(dc.gridy == 0 ? Y_MARGIN : Y_SPACING,
                X_MARGIN, 0, X_MARGIN);
        dc.gridy ++;
        dc.weighty = 1.0;
        super.setConstraints(vSpacer, dc);
        dc.weighty = 0.0;
    }

    public Component getVerticalSpacer() {
        return vSpacer;
    }
}
