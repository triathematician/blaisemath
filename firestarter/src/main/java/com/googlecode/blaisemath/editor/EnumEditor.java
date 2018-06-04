/**
 * EnumEditor.java
 * Created on Jul 2, 2009
 */
package com.googlecode.blaisemath.editor;

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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * <p>
 *   Uses a combo box for editing enum's.
 * </p>
 *
 * @author Elisha Peterson
 */
public final class EnumEditor extends MPanelEditorSupport {

    private JComboBox combo;

    @Override
    protected void initCustomizer() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        combo = new JComboBox();
        combo.setEditable(false);
        combo.setBackground(panel.getBackground());
        combo.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(combo);

        combo.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    setNewValue(evt.getItem());
                }
            }
        });
    }

    public Class<?> getEnumClass() {
        return ((Enum) getValue()).getDeclaringClass();
    }

    @Override
    protected void initEditorValue () {
        if (panel != null) {
            combo.setModel(new DefaultComboBoxModel(getEnumClass().getEnumConstants()));
            combo.setSelectedItem(getNewValue());
            panel.repaint();
        }
    }

    @Override
    public String getJavaInitializationString() {
        return getEnumClass() + "." + ((Enum) getValue()).name();
    }

    @Override
    public String[] getTags() {
        Object[] vals = getEnumClass().getEnumConstants();
        String[] result = new String[vals.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = vals[i].toString();
        }
        return result;
    }
}

