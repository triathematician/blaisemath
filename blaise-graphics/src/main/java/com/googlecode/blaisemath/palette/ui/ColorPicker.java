package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * ******************************* UNCLASSIFIED *******************************
 * ColorPicker.java
 * edu.jhuapl.swing:eswing-utils
 * %%
 * Copyright (C) 2011 - 2019 Johns Hopkins University Applied Physics Laboratory
 * %%
 * (c) Johns Hopkins University Applied Physics Laboratory. All Rights Reserved.
 * JHU-APL Proprietary Information. Do not disseminate without prior approval.
 * #L%
 */

import com.google.common.base.Objects;
import com.googlecode.blaisemath.editor.ColorEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/**
 * Displays editable color.
 * @author petereb1
 */
public class ColorPicker extends JPanel {

    ColorEditor ed;
    
    public ColorPicker() {
        ed = new ColorEditor();
        setLayout(new BorderLayout());
        Component customEditor = ed.getCustomEditor();
        add(customEditor);
        setPreferredSize(new Dimension(150,30));
        ed.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("color", evt.getOldValue(), evt.getNewValue());
            }
        });
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
