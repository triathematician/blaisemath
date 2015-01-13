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
    private final PropertyModel parent;
    /** Index (when the descriptor is an indexed property) */
    private final int row;
    
    DefaultPropertyComponent(PropertyModel parent, int row) {
        super(parent.getElementAt(row));
        this.parent = parent;
        this.row = row;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProperty();
            }
        });
        setEnabled(false);
        Object value = parent.getPropertyValue(row);
        if (value != null) {
            if (value.getClass().isArray()) {
                String txt = Arrays.deepToString(new Object[]{value});
                setText(txt.substring(1, txt.length()-1));
            } else {
                setText(value.toString());
            }
            setEnabled(true);
        }
    }

    /** Called whenever the button is pressed. Calls up a dialog box with the new bean's properties. */
    void updateProperty() {
        Object value = parent.getPropertyValue(row);
        if (value != null) {
            JDialog dialog;
            if (parent instanceof BeanPropertyModel 
                    && ((BeanPropertyModel)parent).getPropertyDescriptor(row) instanceof IndexedPropertyDescriptor) {
                dialog = new PropertySheetDialog(null, false, 
                        ((BeanPropertyModel)parent).getBean(), 
                        (IndexedPropertyDescriptor) ((BeanPropertyModel)parent).getPropertyDescriptor(row));
            } else {
                dialog = new PropertySheetDialog(null, false, value);
            }
            dialog.setVisible(true);
        }
    }
}
