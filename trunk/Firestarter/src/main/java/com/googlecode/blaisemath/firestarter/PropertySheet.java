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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.FeatureDescriptor;
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
public class PropertySheet extends JPanel {

    /** This static variable determines whether the filter panel is on or off by default. */
    static boolean TOOLBAR_VISIBLE_DEFAULT = false;

    /** Column containing property names */
    public static final int NAME_COL = 0;
    /** Column containing property values */
    public static final int VALUE_COL = 1;
    /** Minimum width of the table */
    public static final int MIN_WIDTH = 200;
    /** Determines minimum height of cells in the table. */
    public static final int MIN_CELL_HEIGHT = 20;

    /** Names of header columns */
    protected String[] headers = {"Name", "Value"};
    /** Default width of first column */
    protected int defaultNameColWidth = 70;

    /** The table displayed. */
    protected JTable table;
    /** Flag for showing/hiding extra panels */
    protected boolean toolsVisible = TOOLBAR_VISIBLE_DEFAULT;
    /** The panel with the filter box. */
    protected JPanel toolPanel;
    /** The combo box for filtering. */
    protected JComboBox filterCombo;
    /** Information on the underlying object. */
    protected BeanEditorModel beanModel;
    /** The underlying model. */
    protected PropertySheetModel model;

    protected PropertySheet() {
    }

    /**
     * Create a property sheet that uses the supplied model for editing components.
     * @param supp model
     * @return newly created property sheet
     */
    public static PropertySheet forModel(BeanEditorModel supp) {
        PropertySheet res = new PropertySheet();
        res.beanModel = supp;
        res.initComponents();
        return res;
    }
    
    /**
     * Create a property sheet that uses the supplied bean object for editing components.
     * @param bean a bean object
     * @return newly created property sheet for editing the bean's properties
     */
    public static PropertySheet forBean(Object bean) {
        PropertySheet res = new PropertySheet();
        res.beanModel = new DefaultBeanEditorModel(bean);
        res.initComponents();
        return res;
    }

    protected void initComponents() {
        initTable();
        initToolbar();

        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);
        if (toolsVisible) {
            add(toolPanel, BorderLayout.NORTH);
        }
    }
    
    protected void initTable() {
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
        column.setCellRenderer(new NameColRenderer());
        column.setPreferredWidth(defaultNameColWidth);

        // set up second column
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(MIN_WIDTH - defaultNameColWidth);
        column.setCellRenderer(new ValueColEditor());
        column.setCellEditor(new ValueColEditor());
    }
    
    protected void initToolbar() {
        // set up filter
        filterCombo = new JComboBox(new DefaultComboBoxModel(BeanFilterRule.values()));
        Font font = filterCombo.getFont().deriveFont( (float) filterCombo.getFont().getSize() - 2);
        filterCombo.setFont(font);
        filterCombo.setSelectedItem(BeanFilterRule.STANDARD);
        filterCombo.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (beanModel instanceof FilteredPropertyList) {
                    ((FilteredPropertyList)beanModel)
                            .setFilter((BeanFilterRule) filterCombo.getSelectedItem());
                }
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
    }

    //
    // GETTERS & SETTERS
    //

    /** @return the value of bean
     */
    public BeanEditorModel getBeanEditorModel() {
        return beanModel;
    }

    /** @return true if toolbar is visible */
    public boolean isToolbarVisible() {
        return toolsVisible;
    }
    
    /** 
     * Sets toolbar visibility
     * @param val 
     */
    public void setToolbarVisible(boolean val) {
        if (val != toolsVisible) {
            toolsVisible = val;
            if (val) {
                add(toolPanel, BorderLayout.NORTH);
            } else {
                remove(toolPanel);
            }
            validate();
        }
    }

    
    //
    // EVENT HANDLING
    //

    /** Updates the size of the table. */
    void updateRowHeights() {
        Component comp;
        for (int i = 0; i < beanModel.getSize(); i++) {
            comp = beanModel.getEditor(i);
            if (comp != null) {
                table.setRowHeight(i, Math.max(comp.getPreferredSize().height, MIN_CELL_HEIGHT));
            }
        }
        table.setPreferredScrollableViewportSize(new Dimension(
                getPreferredSize().width,
                getPreferredSize().height + (table.getTableHeader() != null ? table.getTableHeader().getHeight() : 0)));
    }

    /** 
     * Update row heights when the underlying data changes.
     * @param e 
     */
    protected void handleTableChange(TableModelEvent e) {
        table.getSelectionModel().clearSelection();
        table.setEditingRow(-1);
        table.setEditingColumn(-1);
        updateRowHeights();
        firePropertyChange("size", null, null);
        repaint();
    }

    public void removeBeanChangeListener(PropertyChangeListener listener) {
        beanModel.removePropertyChangeListener(listener);
    }

    public void removeBeanChangeListener(String propertyName, PropertyChangeListener listener) {
        beanModel.removePropertyChangeListener(propertyName, listener);
    }

    public void addBeanChangeListener(PropertyChangeListener listener) {
        beanModel.addPropertyChangeListener(listener);
    }

    public void addBeanChangeListener(String propertyName, PropertyChangeListener listener) {
        beanModel.addPropertyChangeListener(propertyName, listener);
    }

    
    // <editor-fold defaultstate="collapsed" desc="Inner Classes">
    //
    //                  INNER CLASSES
    //

    /** Model for the property sheet class. */
    class PropertySheetModel extends AbstractTableModel {
        PropertySheetModel() {
            beanModel.addListDataListener(new ListDataListener() {
                @Override
                public void intervalAdded(ListDataEvent e) {
                    fireTableDataChanged();
                }
                @Override
                public void intervalRemoved(ListDataEvent e) {
                    fireTableDataChanged();
                }
                @Override
                public void contentsChanged(ListDataEvent e) {
                    fireTableDataChanged();
                }
            });
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return col == 0 ? String.class : Object.class;
        }

        @Override
        public String getColumnName(int col) {
            return headers[col];
        }

        @Override
        public int getRowCount() {
            return beanModel.getSize();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            switch (col) {
                case NAME_COL:
                    return false;
                case VALUE_COL:
                    Object element = beanModel.getElementAt(row);
                    return element instanceof PropertyDescriptor
                            && ((PropertyDescriptor) element).getReadMethod() != null
                            && ((PropertyDescriptor) element).getWriteMethod() != null;
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public Object getValueAt(int row, int col) {
            switch (col) {
                case NAME_COL:
                    Object element = beanModel.getElementAt(row);
                    return element instanceof FeatureDescriptor
                            ? ((FeatureDescriptor)element).getDisplayName()
                            : ("Row "+row);
                case VALUE_COL:
                    return beanModel.getValue(row);
                default:
                    throw new IllegalStateException();
            }
        }

    }



    /** Provides support for editing properties. */
    class ValueColEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        /** Current row being edited. */
        int row = -1;

        @Override
        public Object getCellEditorValue() {
            return row == -1 ? null : beanModel.getValue(row);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return beanModel.getEditor(row);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return beanModel.getEditor(row);
        }
    }



    /** Renders name columns. Currently only changed by adding a tooltip. */
    class NameColRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String descrip = "";
            if (beanModel.getElementAt(row) instanceof FeatureDescriptor) {
                descrip = ((FeatureDescriptor)beanModel.getElementAt(row)).getShortDescription();
            }
            Class type = beanModel.getValueType(row);
            setToolTipText(String.format("%s (%s)", descrip, type));
            return this;
        }
    }
    
    // </editor-fold>
    
}

