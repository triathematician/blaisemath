/**
 * BooleanEditor.java
 * Created on Jul 1, 2009
 */
package com.googlecode.blaisemath.firestarter.editor;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <p>
 *   <code>BooleanEditor</code> ...
 * </p>
 *
 * @author Elisha Peterson
 */
public class BooleanEditor extends MPanelEditorSupport {
    JCheckBox checkbox;
    JLabel label;

    public BooleanEditor() {
        setValue(false);
        newValue = false;
    }

    public void initCustomizer() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        checkbox = new JCheckBox();
        checkbox.setBackground(panel.getBackground());
        checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkbox.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(checkbox);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));

        label = new JLabel("");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(label);

        // initialize listening
        checkbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    setNewValue(Boolean.TRUE);
                    label.setText(getValidName(true));
                } else {
                    setNewValue(Boolean.FALSE);
                    label.setText(getValidName(false));
                }
            }
        });
    }

    //
    //  OVERRIDING METHODS
    //
    @Override
    protected void initEditorValue() {
        if (panel != null) {
            checkbox.setSelected((Boolean)getNewValue());
            label.setText(getAsText());
            panel.repaint();
        }
    }

    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        return value != null ? value.toString() : "null";
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return value instanceof Boolean ? getValidName((Boolean) value) : null;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            setValue(null);
        } else if (isValidName(true, text)) {
            setValue(Boolean.TRUE);
        } else if (isValidName(false, text)) {
            setValue(Boolean.FALSE);
        } else {
            throw new IllegalArgumentException(text);
        }
    }

    @Override
    public String[] getTags() {
        return new String[]{getValidName(true), getValidName(false)};
    }

    private String getValidName(boolean value) {
        return value ? "True" : "False";
    }

    private boolean isValidName(boolean value, String text) {
        return getValidName(value).equalsIgnoreCase(text);
    }
}
