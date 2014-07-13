/**
 * IndexedPropertySheet.java
 * Created on Jan 28, 2010
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * <p>
 *   <code>IndexedPropertySheet</code> is a table-formatted list of individual properties
 *   within an array, useful for editing indexed properties. Almost all of the functionality
 *   is borrowed from <code>PropertySheet</code>... the underyling model here will be an
 *   <code>IndexedBeanEditorSupport</code>.
 * </p>
 *
 * @author Elisha Peterson
 */
public class IndexedPropertySheet extends PropertySheet {

    /** Stores the property descriptors for eached indexed element. */
    IndexedPropertyDescriptor ipd;

    /** Construct for provided bean and provided property descriptor (which must be indexed) */
    public IndexedPropertySheet(Object bean, IndexedPropertyDescriptor ipd) {
        this.ipd = ipd;
        setBean(bean);
        initComponents();
    }

    public IndexedPropertySheet(Object bean, String propName) {
        BeanInfo info = BeanEditorSupport.getBeanInfo(bean.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (pd.getName().equals(propName) && pd instanceof IndexedPropertyDescriptor) {
                this.ipd = (IndexedPropertyDescriptor) pd;
                setBean(bean);
                initComponents();
                break;
            }
        }
        if (ipd == null)
            throw new IllegalArgumentException("Unable to find property " + propName + " in the class " + bean.getClass());
    }

    @Override
    public void initComponents() {
        if (ipd == null)
            return;
        
        // set up table model
        table = new JTable();
        table.setGridColor(new Color(192, 192, 192));
        model = new PropertySheetModel();
        model.addTableModelListener(this);
        table.setModel(model);
        HEADERS = new String[] {"Index", "Value"};
        table.getTableHeader().setReorderingAllowed(false);
        updateRowHeights();

        // set up first column
        DEFAULT_NAME_COL_WIDTH = 35;
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setCellRenderer(new IndexColRenderer());
        column.setPreferredWidth(DEFAULT_NAME_COL_WIDTH);

        // set up second column
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(MIN_WIDTH - DEFAULT_NAME_COL_WIDTH);
        column.setCellRenderer(new ValueColEditor());
        column.setCellEditor(new ValueColEditor());
        
        // set up tool panel
        JButton addB = new JButton(((IndexedBeanEditorSupport)beanSupport).addAction);
        addB.setMargin(new Insets(2,4,2,4));
        JButton delB = new JButton(((IndexedBeanEditorSupport)beanSupport).createRemoveAction(table));
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

        // Set up final components
        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);
        add(toolPanel, BorderLayout.NORTH);
    }

    /** Set the value of bean
     * @param bean new value of bean
     */
    @Override
    public void setBean(Object bean) {
        if (ipd != null) {
            beanSupport = new IndexedBeanEditorSupport(bean, ipd);
            if (model != null)
                model.fireTableDataChanged();
        }
    }

    /** Updates the bean. */
    public void updateBean() {
        setBean(getBean());
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        updateRowHeights();
        firePropertyChange("size", null, null);
    }


    // *********************************************** //
    //                  INNER CLASSES                  //
    // *********************************************** //

    /** Renders name columns. Currently only changed by adding a tooltip. */
    class IndexColRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            super.setText(""+row);
            return this;
        }
    } // INNER CLASS NameColumnRenderer
}

