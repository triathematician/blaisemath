/**
 * PropertySheetTableModel.java
 * Created Sep 23, 2014
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

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

/**
 * <p>
 *   Model for a table of editable properties, based on a {@link PropertyModel}
 *   and a corresponding {@link PropertyEditorModel}.
 * </p>
 * @author elisha
 */
class PropertySheetModel extends AbstractTableModel {

    /** Column containing property names */
    public static final int NAME_COL = 0;
    /** Column containing property values */
    public static final int VALUE_COL = 1;
    
    /** Names of header columns */
    protected String[] headers = {"Name", "Value"};
    
    private final PropertyModel propModel;
    private final PropertyEditorModel editorModel;

    PropertySheetModel(PropertyEditorModel editorModel) {
        this.editorModel = editorModel;
        this.propModel = editorModel.getPropertyModel();
        
        editorModel.addListDataListener(new ListDataListener() {
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
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public PropertyModel getPropertyModel() {
        return propModel;
    }

    public PropertyEditorModel getPropertyEditorModel() {
        return editorModel;
    }
    
    //</editor-fold>

    @Override
    public int getRowCount() {
        return editorModel.getSize();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int col) {
        return headers[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return col == 0 ? String.class : Object.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        switch (col) {
            case NAME_COL:
                return false;
            case VALUE_COL:
                return editorModel.getElementAt(row).isEnabled();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case NAME_COL:
                return propModel.getElementAt(row);
            case VALUE_COL:
                return propModel.getPropertyValue(row);
            default:
                throw new IllegalStateException();
        }
    }

}
