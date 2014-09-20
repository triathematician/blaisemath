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
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import javax.swing.AbstractAction;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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
public final class IndexedPropertySheet extends PropertySheet {

    /** Stores the property descriptors for eached indexed element. */
    private final IndexedPropertyDescriptor ipd;

    public IndexedPropertySheet(Object bean, String propName) {
        this(bean, indexedPropertyDescriptor(bean, propName));
    }

    /** 
     * Construct for provided bean and provided property descriptor (which must be indexed)
     * @param bean 
     * @param ipd 
     */
    public IndexedPropertySheet(Object bean, IndexedPropertyDescriptor ipd) {
        this.ipd = ipd;
        beanModel = new IndexedBeanEditorModel(bean, ipd);
        headers = new String[] {"Index", "Value"};
        defaultNameColWidth = 35;
        initComponents();
    }
    
    private static IndexedPropertyDescriptor indexedPropertyDescriptor(Object bean, String propName) {
        BeanInfo info = DefaultBeanEditorModel.getBeanInfo(bean.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (pd.getName().equals(propName) && pd instanceof IndexedPropertyDescriptor) {
                return (IndexedPropertyDescriptor) pd;
            }
        }
        throw new IllegalArgumentException("Unable to find property " + propName + " in the class " + bean.getClass());
    }

    @Override
    protected void initComponents() {
        if (ipd == null) {
            return;
        }
        
        // set up table model
        table = new JTable();
        table.setGridColor(new Color(192, 192, 192));
        model = new PropertySheetModel();
        model.addTableModelListener(new TableModelListener(){
            @Override
            public void tableChanged(TableModelEvent e) {
                handleTableChange(e);
            }
        });
        table.setModel(model);
        table.getTableHeader().setReorderingAllowed(false);
        updateRowHeights();

        // set up first column
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setCellRenderer(new IndexColRenderer());
        column.setPreferredWidth(defaultNameColWidth);

        // set up second column
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(MIN_WIDTH - defaultNameColWidth);
        column.setCellRenderer(new ValueColEditor());
        column.setCellEditor(new ValueColEditor());
        
        // set up tool panel
        AbstractAction aa = new AbstractAction("+") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((IndexedBeanEditorModel)beanModel).addNewValue();
            }
        };
        aa.putValue(SHORT_DESCRIPTION, "Add a new element to the end of the list.");
        JButton addB = new JButton(aa);
        addB.setMargin(new Insets(2,4,2,4));
        
        AbstractAction remove = new AbstractAction("-") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((IndexedBeanEditorModel)beanModel).removeValues(table.getSelectedRows());
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

        // Set up final components
        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);
        add(toolPanel, BorderLayout.NORTH);
    }

    @Override
    protected void handleTableChange(TableModelEvent e) {
        updateRowHeights();
        firePropertyChange("size", null, null);
        repaint();
    }

    /** Renders name columns. Currently only changed by adding a tooltip. */
    class IndexColRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            super.setText(""+row);
            return this;
        }
    }
}

