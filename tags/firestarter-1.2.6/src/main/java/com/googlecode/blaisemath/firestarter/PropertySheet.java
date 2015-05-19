/**
 * PropertySheet.java
 * Created on Jun 29, 2009
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * <p>
 *   Provides a table used for editing a list of properties.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PropertySheet extends JPanel {

    /** This static variable determines whether the filter panel is on or off by default. */
    static boolean TOOLBAR_VISIBLE_DEFAULT = false;
    /** Minimum width of the table */
    public static final int MIN_WIDTH = 200;
    /** Determines minimum height of cells in the table. */
    public static final int MIN_CELL_HEIGHT = 20;

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
    /** The underlying table model. */
    protected PropertySheetModel model;

    public PropertySheet() {
        initComponents(new PropertyModel.Empty());
    }
    
    public PropertySheet(PropertyModel pm) {
        initComponents(pm);
    }
    
    /**
     * Create a property sheet that uses the supplied bean object for editing components.
     * @param bean a bean object
     * @return newly forBeand property sheet for editing the bean's properties
     */
    public static PropertySheet forBean(Object bean) {
        PropertySheet res = new PropertySheet();
        res.initComponents(new BeanPropertyModel(bean));
        return res;
    }

    protected void initComponents(PropertyModel model) {
        initTable(model);
        initToolbar();

        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);
        if (toolsVisible) {
            add(toolPanel, BorderLayout.NORTH);
        }
    }
    
    protected void initTable(PropertyModel pm) {
        table = new JTable();
        table.setGridColor(new Color(192, 192, 192));
        model = new PropertySheetModel(new PropertyEditorModel(pm));
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
        column.setPreferredWidth(defaultNameColWidth);

        // set up second column
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(MIN_WIDTH - defaultNameColWidth);
        column.setCellRenderer(new ValueColEditor());
        column.setCellEditor(new ValueColEditor());
    }
    
    protected void initToolbar() {
        // set up filter
        filterCombo = new JComboBox(new DefaultComboBoxModel(BeanPropertyFilter.values()));
        Font font = filterCombo.getFont().deriveFont( (float) filterCombo.getFont().getSize() - 2);
        filterCombo.setFont(font);
        filterCombo.setSelectedItem(BeanPropertyFilter.STANDARD);
        filterCombo.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getPropertyModel() instanceof BeanPropertyModel) {
                    ((BeanPropertyModel)getPropertyModel()).setFilter(
                            (BeanPropertyFilter) filterCombo.getSelectedItem());
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
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    /** 
     * Get the core property model used by the property sheet.
     * @return property model
     */
    public PropertyModel getPropertyModel() {
        return model.getPropertyModel();
    }

    /** 
     * Return toolbar status.
     * @return true if toolbar is visible 
     */
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
    
    //</editor-fold>

    
    //
    // EVENT HANDLING
    //

    /** Updates the size of the table. */
    void updateRowHeights() {
        Component comp;
        for (int i = 0; i < model.getRowCount(); i++) {
            comp = model.getPropertyEditorModel().getElementAt(i);
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
        if (table.getCellEditor() != null) {
            table.getCellEditor().stopCellEditing();
        }
        updateRowHeights();
        firePropertyChange("size", null, null);
        repaint();
    }

    public void removeBeanChangeListener(PropertyChangeListener listener) {
        getPropertyModel().removePropertyChangeListener(listener);
    }

    public void removeBeanChangeListener(String propertyName, PropertyChangeListener listener) {
        getPropertyModel().removePropertyChangeListener(propertyName, listener);
    }

    public void addBeanChangeListener(PropertyChangeListener listener) {
        getPropertyModel().addPropertyChangeListener(listener);
    }

    public void addBeanChangeListener(String propertyName, PropertyChangeListener listener) {
        getPropertyModel().addPropertyChangeListener(propertyName, listener);
    }

    
    // <editor-fold defaultstate="collapsed" desc="Inner Classes">
    //
    //                  INNER CLASSES
    //

    /** Provides support for editing properties. */
    class ValueColEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        /** Current row being edited. */
        int row = -1;

        @Override
        public Object getCellEditorValue() {
            return row == -1 ? null : getPropertyModel().getPropertyValue(row);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return model.getPropertyEditorModel().getElementAt(row);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return model.getPropertyEditorModel().getElementAt(row);
        }
    }
    
    // </editor-fold>
    
}

