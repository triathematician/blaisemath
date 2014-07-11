/**
 * PropertySheet.java
 * Created on Jun 29, 2009
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * <p>
 *   <code>PropertySheet</code> is a table-formatted list of bean properties for an
 *   underlying object.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PropertySheet extends JPanel
        implements TableModelListener {

    /** This static variable determines whether the filter panel is on or off by default. */
    public static boolean toolsVisibleByDefault = false;
    
    //
    // APPEARANCE/STYLE CONSTANTS
    //

    /** Column containing property names */
    protected int NAME_COL = 0;
    /** Column containing property values */
    protected int VALUE_COL = 1;
    /** Minimum width of the table */
    protected int MIN_WIDTH = 200;
    /** Determines minimum height of cells in the table. */
    protected int MIN_CELL_HEIGHT = 20;

    /** Names of header columns */
    protected String[] HEADERS = {"Name", "Value"};
    /** Default width of first column */
    protected int DEFAULT_NAME_COL_WIDTH = 70;

    //
    // PROPERTIES
    //

    /** The table displayed. */
    protected JTable table;
    /** Flag for showing/hiding extra panels */
    protected boolean toolsVisible = toolsVisibleByDefault;
    /** The panel with the filter box. */
    protected JPanel toolPanel;
    /** The combo box for filtering. */
    protected JComboBox filterCombo;
    /** The underlying model. */
    PropertySheetModel model;
    /** Information on the underlying object. */
    protected BeanEditorSupport beanSupport;

    //
    // CONSTRUCTORS
    //

    public PropertySheet() {
        this(new Object());
    }

    public PropertySheet(Object bean) {
        setBean(bean);
        initComponents();
    }

    //
    // INITIALIZERS
    //

    public void initComponents() {
        // set up table model
        table = new JTable();
        table.setGridColor(new Color(192, 192, 192));
        model = new PropertySheetModel();
        model.addTableModelListener(this);
        table.setModel(model);
        table.getTableHeader().setReorderingAllowed(false);
        updateRowHeights();

        // set up first column
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setCellRenderer(new NameColRenderer());
        column.setPreferredWidth(DEFAULT_NAME_COL_WIDTH);

        // set up second column
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(MIN_WIDTH - DEFAULT_NAME_COL_WIDTH);
        column.setCellRenderer(new ValueColEditor());
        column.setCellEditor(new ValueColEditor());

        // set up filter
        filterCombo = new JComboBox(new DefaultComboBoxModel(BeanFilterRule.values()));
        Font font = filterCombo.getFont().deriveFont( (float) filterCombo.getFont().getSize() - 2);
        filterCombo.setFont(font);
        filterCombo.setSelectedItem(BeanFilterRule.STANDARD);
        filterCombo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                beanSupport.setFilter((BeanFilterRule) filterCombo.getSelectedItem());
            }
        });

        JLabel filterLabel = new JLabel("Filter: ");
        filterLabel.setFont(font.deriveFont(Font.ITALIC));
        filterLabel.setOpaque(false);

        toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.LINE_AXIS));
        toolPanel.setBackground(table.getGridColor());
        toolPanel.add(Box.createGlue());
        toolPanel.add(filterLabel);
        toolPanel.add(filterCombo);

        // Set up final components
        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);
        if (toolsVisible)
            add(toolPanel, BorderLayout.NORTH);
    } // initComponents

    //
    // GETTERS & SETTERS
    //

    /** @return the value of bean
     */
    public Object getBean() {
        return beanSupport.getBean();
    }

    /** Set the value of bean
     * @param bean new value of bean
     */
    public void setBean(Object bean) {
        if (beanSupport == null || getBean() != bean)
            beanSupport = new BeanEditorSupport(bean);
        if (model != null)
            model.fireTableDataChanged();
    }

    /** @return true if toolbar is visible */
    public boolean isToolbarVisible() {
        return toolsVisible;
    }
    
    /** Sets toolbar visibility */
    public void setToolbarVisible(boolean val) {
        if (val != toolsVisible) {
            if (val == true) {
                toolsVisible = true;
                add(toolPanel, BorderLayout.NORTH);
                validate();
            } else {
                toolsVisible = false;
                remove(toolPanel);
                validate();
            }
        }
    }

    /** @return current filter restricting displayed properties */
    public Predicate<PropertyDescriptor> getFilter() {
        return beanSupport.getFilter();
    }

    /** Sets the filter that chooses which properties to display */
    public void setFilter(Predicate<PropertyDescriptor> filter) {
        beanSupport.setFilter(filter);
    }

    
    //
    // EVENT HANDLING
    //

    /** Updates the size of the table. */
    void updateRowHeights() {
        Component comp;
        for (int i = 0; i < beanSupport.getSize(); i++) {
            comp = beanSupport.getComponent(i);
            if (comp != null)
                table.setRowHeight(i, Math.max(comp.getPreferredSize().height, MIN_CELL_HEIGHT));
        }
        table.setPreferredScrollableViewportSize(new Dimension(
                getPreferredSize().width,
                getPreferredSize().height + (table.getTableHeader() != null ? table.getTableHeader().getHeight() : 0)));
    }

    /** Update row heights when the underlying data changes. */
    public void tableChanged(TableModelEvent e) {
        table.getSelectionModel().clearSelection();
        table.setEditingRow(-1);
        table.setEditingColumn(-1);
        updateRowHeights();
        firePropertyChange("size", null, null);
        repaint();
    }

    public synchronized void removeBeanChangeListener(PropertyChangeListener listener) {
        beanSupport.removePropertyChangeListener(listener);
    }

    public synchronized void removeBeanChangeListener(String propertyName, PropertyChangeListener listener) {
        beanSupport.removePropertyChangeListener(propertyName, listener);
    }

    public synchronized void addBeanChangeListener(PropertyChangeListener listener) {
        beanSupport.addPropertyChangeListener(listener);
    }

    public synchronized void addBeanChangeListener(String propertyName, PropertyChangeListener listener) {
        beanSupport.addPropertyChangeListener(propertyName, listener);
    }

    
    // <editor-fold defaultstate="collapsed" desc="Inner Classes">
    //
    //                  INNER CLASSES
    //

    /** Model for the property sheet class. */
    class PropertySheetModel extends AbstractTableModel {
        public PropertySheetModel() {
            beanSupport.addListDataListener(new ListDataListener(){
                public void intervalAdded(ListDataEvent e) { fireTableDataChanged(); }
                public void intervalRemoved(ListDataEvent e) { fireTableDataChanged(); }
                public void contentsChanged(ListDataEvent e) { fireTableDataChanged(); }
            });
        }

        public int getColumnCount() {
            return 2;
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return col == 0 ? String.class : Object.class;
        }

        @Override
        public String getColumnName(int col) {
            return HEADERS[col];
        }

        public int getRowCount() {
            return beanSupport.getSize();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == VALUE_COL 
                    && beanSupport.getElementAt(row).getWriteMethod() != null
                    && beanSupport.getElementAt(row).getReadMethod() != null;
        }

        public Object getValueAt(int row, int col) {
            return col == NAME_COL ? beanSupport.getElementAt(row).getDisplayName() : beanSupport.getValue(row);
        }

    } // INNER CLASS PropertySheetModel



    /** Provides support for editing properties. */
    class ValueColEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        /** Current row being edited. */
        int row = -1;

        public Object getCellEditorValue() {
            return row == -1 ? null : beanSupport.getValue(row);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return beanSupport.getComponent(row);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return beanSupport.getComponent(row);
        }
    } // INNER CLASS ValueColumnEditor



    /** Renders name columns. Currently only changed by adding a tooltip. */
    class NameColRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String descrip = beanSupport.getElementAt(row).getShortDescription();
            Class type = beanSupport.getPropertyType(row);
            setToolTipText(String.format("%s (%s)", descrip, type));
            return this;
        }
    } // INNER CLASS NameColumnRenderer
    
    // </editor-fold>
    
}

