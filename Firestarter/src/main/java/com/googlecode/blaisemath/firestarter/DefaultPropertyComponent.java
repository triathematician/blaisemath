package com.googlecode.blaisemath.firestarter;

/*
 * #%L
 * Firestarter
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * <p>
 *  This class implements a customized editor for components that do not have a default
 *  assigned editor. By default, this creates a dialog box displaying the bean properties
 *  of the property itself. The button's text is displayed using the text of the property.
 * </p>
 */
final class DefaultPropertyComponent extends JButton {

    /** The property's parent object. */
    Object parent;
    /** The property descriptor. */
    PropertyDescriptor pd;
    /** Index (when the descriptor is an indexed property) */
    int index = -1;
    /** Recognize when the update is coming from the underlying property and do not forward document events. */
    boolean propertyUpdate = false;

    public DefaultPropertyComponent(Object parent, PropertyDescriptor pd) {
        super(pd.getDisplayName());
        this.parent = parent;
        this.pd = pd;
        // set up listener to track for customization... this happens when the button is pressed
        addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e) { updateProperty(); } });
        try {
            Object value = pd.getReadMethod().invoke(parent);
            if (value != null) {
                if (value.getClass().isArray()) {
                    setText(Arrays.deepToString((Object[])value));
                } else {
                    setText(value.toString());
                }
            } else
                setEnabled(false);
        } catch (Exception ex) {}
    }

    public DefaultPropertyComponent(Object parent, IndexedPropertyDescriptor pd, int index) {
        super(pd.getDisplayName());
        this.parent = parent;
        this.pd = pd;
        this.index = index;
        // set up listener to track for customization... this happens when the button is pressed
        addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e) { updateProperty(); } });
        try {
            Object value = pd.getIndexedReadMethod().invoke(parent, index);
            if (value != null) {
                if (value.getClass().isArray()) {
                    setText(Arrays.deepToString((Object[])value));
                } else {
                    setText(value.toString());
                }
            } else
                setEnabled(false);
        } catch (Exception ex) {}
    }

    /** Called whenever the button is pressed. Calls up a dialog box with the new bean's properties. */
    public void updateProperty() {
        JDialog dialog = null;
        try {
            if (pd instanceof IndexedPropertyDescriptor && index == -1) {
                dialog = new PropertySheetDialog(null, false, parent, (IndexedPropertyDescriptor) pd);
            } else if (pd instanceof IndexedPropertyDescriptor) {
                Object value = ((IndexedPropertyDescriptor)pd).getIndexedReadMethod().invoke(parent, index);
                if (value != null)
                    dialog = new PropertySheetDialog(null, false, value);
            } else {
                Object value = pd.getReadMethod().invoke(parent);
                if (value != null)
                    dialog = new PropertySheetDialog(null, false, value);
            }
            if (dialog != null)
                dialog.setVisible(true);
        } catch (IllegalAccessException ex) {
        } catch (IllegalArgumentException ex) {
        } catch (InvocationTargetException ex) {
        }
    }
}
