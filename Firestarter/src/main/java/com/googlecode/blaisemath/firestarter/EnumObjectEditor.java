/**
 * EnumObjectEditor.java
 * Created on Jul 2, 2009
 */
package com.googlecode.blaisemath.firestarter;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.googlecode.blaisemath.editor.MPanelEditorSupport;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * <p>
 *  This class implements a customized editor for components that do not have a default
 *  assigned editor, but have an associated "getInstance" method, with argument taking
 *  an enum value, such that each enum returns a value of the object. When the enum value is
 *  selected in a ComboBox, the underlying value is updated.
 * </p>
 * 
 * @author Elisha Peterson
 */
public final class EnumObjectEditor extends MPanelEditorSupport {

    /** Method used to retrieve new instances. */
    private Method instanceMethod;
    /** Options for selecting different object types */
    private JComboBox combo;
    /** String representing the custom object selected in the combo box model */
    private String custom;

    public EnumObjectEditor () {
        initCustomizer();
    }

    @Override
    protected void initCustomizer() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        combo = new JComboBox();
        combo.setEditable(false);
        combo.setBackground(panel.getBackground());
        combo.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                handleSelectionChange(e);
            }
        });
        panel.add(combo, BorderLayout.CENTER);

        JButton button = new JButton("...");
        button.setMargin(new Insets(3, 3, 3, 2));
        button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new PropertySheetDialog(null, false, newValue);
                dialog.setVisible(true);
            }
        });
        panel.add(button, BorderLayout.EAST);
    }

    public Class getEnumClass() {
        return instanceMethod.getParameterTypes()[0];
    }

    public static boolean validMethod(Method m, Class type) {
        return "getInstance".equals(m.getName()) && m.getReturnType().isAssignableFrom(type)
                && m.getParameterTypes().length == 1 && m.getParameterTypes()[0].isEnum();
    }

    @Override
    protected void initEditorValue () {
        Method[] mm = newValue.getClass().getMethods();
        for (Method m : mm) {
            if (validMethod(m, newValue.getClass())) {
                instanceMethod = m;
                break;
            }
        }
        if (panel != null) {
            Object[] c1 = getEnumClass().getEnumConstants();
            DefaultComboBoxModel dcbm = new DefaultComboBoxModel(c1);
            custom = "Custom " + getValue();
            dcbm.addElement(custom);
            dcbm.setSelectedItem(custom);
            combo.setModel(dcbm);
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

    private void handleSelectionChange(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getItem() == custom) {
                setNewValue( super.getValue() );
            } else {
                try {
                    setNewValue( instanceMethod.invoke(null, e.getItem()) );
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(EnumObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(EnumObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}

