package com.googlecode.blaisemath.firestarter;

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

import com.googlecode.blaisemath.util.ReflectionUtils;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.IndexedPropertyDescriptor;
import static java.util.Objects.requireNonNull;
import javax.swing.AbstractAction;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Table-formatted list of individual properties within an array, useful for editing 
 * indexed properties. Uses {@link BeanIndexedPropertyModel} for the underlying model.
 *
 * @author Elisha Peterson
 */
public final class IndexedPropertySheet extends PropertySheet {

    /** The model for the indexed property */
    private BeanIndexedPropertyModel beanModel;

    /**
     * Create a property sheet that uses the supplied model for editing components.
     * @param bean the object
     * @param propName name of an indexed property
     * @return newly forBeand property sheet
     */
    public static PropertySheet forIndexedProperty(Object bean, String propName) {
        requireNonNull(bean);
        return forIndexedProperty(bean, ReflectionUtils.indexedPropertyDescriptor(bean.getClass(), propName));
    }

    /**
     * Create a property sheet that uses the supplied model for editing components.
     * @param bean the object
     * @param ipd indexed property
     * @return newly forBeand property sheet
     */
    public static PropertySheet forIndexedProperty(Object bean, IndexedPropertyDescriptor ipd) {
        requireNonNull(bean);
        requireNonNull(ipd);
        
        IndexedPropertySheet res = new IndexedPropertySheet();
        res.beanModel = new BeanIndexedPropertyModel(bean, ipd);
        res.model.headers = new String[] {"Index", "Value"};
        res.defaultNameColWidth = 35;
        res.initComponents(res.beanModel);
        return res;
    }
    
    @Override
    protected void initToolbar() {
        toolsVisible = true;
        // set up tool panel
        AbstractAction aa = new AbstractAction("+") {
            @Override
            public void actionPerformed(ActionEvent e) {
                beanModel.addNewValue();
                handleTableChange();
            }
        };
        aa.putValue(SHORT_DESCRIPTION, "Add a new element to the end of the list.");
        JButton addB = new JButton(aa);
        addB.setMargin(new Insets(2,4,2,4));
        
        AbstractAction remove = new AbstractAction("-") {
            @Override
            public void actionPerformed(ActionEvent e) {
                CellEditor cellEditor = table.getCellEditor();
                if (cellEditor != null) {
                    cellEditor.stopCellEditing();
                }
                beanModel.removeValues(table.getSelectedRows());
                handleTableChange();
            }
        };
        remove.putValue(SHORT_DESCRIPTION, "Remove the selected element from the end of the list.");
        JButton delB = new JButton(remove);
        delB.setMargin(new Insets(2,4,2,4));
        Font font = addB.getFont().deriveFont( (float) addB.getFont().getSize() - 2);
        addB.setFont(font);
        delB.setFont(font);

        toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.LINE_AXIS));
        toolPanel.setBackground(table.getGridColor());
        toolPanel.add(Box.createGlue());
        toolPanel.add(addB);
        toolPanel.add(delB);
    }

    @Override
    protected void handleTableChange() {
        updateRowHeights();
        firePropertyChange("size", null, null);
        repaint();
    }
    
}

