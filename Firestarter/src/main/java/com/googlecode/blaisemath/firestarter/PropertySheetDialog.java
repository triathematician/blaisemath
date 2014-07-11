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

import com.google.common.base.Predicate;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.beans.IndexedPropertyDescriptor;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

/**
 * <p>
 *   Shows a property sheet for a specified bean wihtin a dialog box.
 * </p>
 * @author ae3263
 */
public class PropertySheetDialog extends javax.swing.JDialog
        implements ActionListener {

    /** Stores the property sheet; may be a regular sheet or an indexed sheet. */
    private PropertySheet propertySheet;

    /** Stores the button for the "OK" action. */
    private JButton okButton;
    /** Stores the button for the "Cancel" action. */
    // TODO - implement cancel button on property sheet dialog
    // private JButton cancelButton;

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

        propertySheet = new PropertySheet(bean);
        propertySheet.setFilter(propertyFilter == null ? BeanFilterRule.STANDARD : BeanFilterRule.byName(propertyFilter));
        add(new JScrollPane(propertySheet,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                ), java.awt.BorderLayout.CENTER);

        okButton = new javax.swing.JButton("OK");
        okButton.addActionListener(this);
        add(okButton, java.awt.BorderLayout.SOUTH);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        pack();
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

        propertySheet = new IndexedPropertySheet(bean, ipd);
        add(new JScrollPane(propertySheet), BorderLayout.CENTER);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        add(okButton, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }

    public Object getBean() {
        return propertySheet.getBean();
    }

    public void setBean(Object bean) {
        propertySheet.setBean(bean);
    }

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == okButton) {
            setVisible(false);
            dispose();
        }
    }
}
