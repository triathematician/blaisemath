package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2019 Elisha Peterson
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

import com.google.common.base.Objects;
import com.googlecode.firestarter.editor.ColorEditor;

import javax.swing.*;
import java.awt.*;

/**
 * Displays editable color.
 * @author Elisha Peterson
 */
public class ColorPicker extends JPanel {

    final ColorEditor ed;
    
    public ColorPicker() {
        ed = new ColorEditor();
        setLayout(new BorderLayout());
        Component customEditor = ed.getCustomEditor();
        add(customEditor);
        setPreferredSize(new Dimension(150,30));
        ed.addPropertyChangeListener(evt -> firePropertyChange("color", evt.getOldValue(), evt.getNewValue()));
    }

    public Color getColor() {
        return (Color) ed.getNewValue();
    }
    
    public void setColor(Color c) {
        Object old = getColor();
        if (!Objects.equal(old, c)) {
            ed.setValue(c);
            firePropertyChange("color", old, c);
        }
    }
    
}
