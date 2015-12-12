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
    private static final int MIN_TABLE_WIDTH = 200;
    /** Determines minimum height of cells in the table. */
    private static final int MIN_CELL_HEIGHT = 20;
    /** Minimum cell width */
    private static final int MIN_CELL_WIDTH = 40;
    /** Maximum width of a cell */
    private static final int MAX_CELL_WIDTH = 400;
    /** Preferred width for custom editor buttons. */
    private static final int PREF_BUTTON_WIDTH = 100;

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

    /** Initialize sheet with an empty model. */
    public PropertySheet() {
        initComponents(new PropertyModel.Empty());
    }
    
    /**
     * Initialize sheet with specified model.
     * @param pm property model for editing
     */
    public PropertySheet(PropertyModel pm) {
        initComponents(pm);
    }
    
    /**
     * Create a property sheet that uses the supplied bean object for editing components.
     * @param bean a bean object
     * @return new property sheet for editing the bean's properties
     */
    public static PropertySheet forBean(Object bean) {
        PropertySheet res = new PropertySheet();
        res.initComponents(new BeanPropertyModel(bean));
        return res;
    }
    
    /**
     * Create a property sheet with a custom model
     * @param model property model
     * @return property sheet
     */
    public static PropertySheet forModel(PropertyModel model) {
        return new PropertySheet(model);
    }

    protected final void initComponents(PropertyModel model) {
        initTable(model);
        initToolbar();

        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);
        if (toolsVisible) {
            add(toolPanel, BorderLayout.NORTH);
        }
    }
    
    /**
     * Initialize table, setting up components, column sizes, etc.
     * @param pm property model
     */
    protected void initTable(PropertyModel pm) {
        table = new JTable();
        table.setGridColor(new Color(192, 192, 192));
        model = new PropertySheetModel(new PropertyEditorModel(pm));
        model.addTableModelListener(new TableModelListener(){
            @Override
            public void tableChanged(TableModelEvent e) {
                handleTableChange();
            }
        });
        table.setModel(model);
        table.getTableHeader().setReorderingAllowed(false);
        updateRowHeights();

        TableColumn column = table.getColumnModel().getColumn(1);
        column.setCellRenderer(new ValueColEditor());
        column.setCellEditor(new ValueColEditor());
        
        // set up column sizes
        for (int col = 0; col < 2; col++) {
            int prefWidth = MIN_CELL_WIDTH;
            for (int row = 0; row < model.getRowCount(); row++) {
                Object val = model.getValueAt(row, col);
                Component comp = table.getCellRenderer(row, col)
                        .getTableCellRendererComponent(table, val, false, false, row, col);
                int wid = comp instanceof DefaultPropertyComponent 
                        ? PREF_BUTTON_WIDTH
                        : comp.getPreferredSize().width;
                prefWidth = Math.max(prefWidth, wid);
            }
            prefWidth = Math.min(prefWidth, MAX_CELL_WIDTH);
            table.getColumnModel().getColumn(col).setPreferredWidth(prefWidth);
            if (col == 0) {
                table.getColumnModel().getColumn(col).setMinWidth(Math.min(prefWidth, MIN_CELL_WIDTH));
            }
        }
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
     */
    protected void handleTableChange() {
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

