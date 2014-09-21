/*
 * PropertySheetDialog.java
 * Created on Aug 4, 2009, 12:47:50 PM
 */

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

import com.google.common.base.Predicate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IndexedPropertyDescriptor;
import javax.swing.JButton;
import javax.swing.JScrollPane;

/**
 * <p>
 *   Shows a property sheet for a specified bean wihtin a dialog box.
 * </p>
 * @author ae3263
 */
public class PropertySheetDialog extends javax.swing.JDialog {

    /**
     * Creates new form PropertySheetDialog
     * @param parent the parent frame
     * @param modal whether the dialog box is modal
     * @param bean object to populate the box
     */
    public PropertySheetDialog(java.awt.Frame parent, boolean modal, Object bean) {
        this(parent, modal, bean, (Predicate) null);
    }
    
    /**
     * Creates new form PropertySheetDialog
     * @param parent the parent frame
     * @param modal whether the dialog box is modal
     * @param bean object to populate the box
     * @param propertyFilter filter used to 
     */
    public PropertySheetDialog(java.awt.Frame parent, boolean modal, Object bean, Predicate<String> propertyFilter) {
        super(parent, bean.toString(), modal);

        PropertySheet propertySheet = PropertySheet.forBean(bean);
        if (propertyFilter != null) {
            ((DefaultBeanEditorModel)propertySheet.getBeanEditorModel()).setFilter(BeanFilterRule.byName(propertyFilter));
        }
        initComponents(propertySheet);
    }

    /**
     * Creates new form PropertySheetDialog with an indexed property.
     * @param parent the parent frame
     * @param modal whether the dialog box is modal
     * @param bean object to populate the box
     * @param ipd an indexed property descriptor
     */
    public PropertySheetDialog(java.awt.Frame parent, boolean modal, Object bean, IndexedPropertyDescriptor ipd) {
        super(parent, "Indexed property [" + ipd.getDisplayName() + "] of " + bean.toString(), modal);
        
        initComponents(new IndexedPropertySheet(bean, ipd));
    }
    
    private void initComponents(PropertySheet propertySheet) {
        add(new JScrollPane(propertySheet,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                ), java.awt.BorderLayout.CENTER);

        JButton okButton = new JButton("Close");
        okButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        add(okButton, java.awt.BorderLayout.SOUTH);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }

}
