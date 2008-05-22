/*
 * SettingsTable.java
 * Created on Feb 23, 2008
 */

package sequor.component;

import sequor.editor.FunctionTextComboBox;
import sequor.Settings;
import java.awt.Color;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import sequor.editor.ColorEditor;
import sequor.model.ColorModel;
import sequor.model.StringRangeModel;
import sequor.model.DoubleRangeModel;
import sequor.FiresChangeEvents;
import sequor.model.FunctionTreeModel;
import sequor.model.IntegerRangeModel;
import sequor.editor.ParameterEditor;
import sequor.model.ParameterListModel;
import sequor.SettingsProperty;
import sequor.model.BooleanModel;
import sequor.model.ParametricModel;

/**
 * SettingsTable is a JTable with the capability of editing anything contained in a Settings class.
 *
 * @author Elisha Peterson
 */
public class SettingsTable extends JTable {
    
    public static final int NAME_COLUMN=0;
    public static final int VALUE_COLUMN=1;
    
    public SettingsTable() {
        this(new Settings.Default());
    }
    public SettingsTable(Settings settings){
        super();
        setModel(new SettingsTableModel(settings));
        setRowHeight(25);
        setAutoCreateRowSorter(true);
    }
    
    public void addProperty(SettingsProperty sp){((SettingsTableModel)getModel()).settings.add(sp);}

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if(column!=VALUE_COLUMN){return super.getCellEditor(row,column);}
        SettingsProperty sp=(SettingsProperty) ((SettingsTableModel)getModel()).getValueAt(row,column);
        switch(sp.getEditorType()){
            case Settings.EDIT_BOOLEAN: return new DefaultCellEditor(((BooleanModel)sp.getModel()).getCheckBox());
            case Settings.EDIT_COLOR: return new ColorEditor((ColorModel)sp.getModel());
            case Settings.EDIT_COMBO: return new DefaultCellEditor(((StringRangeModel)sp.getModel()).getComboBox());
            // TODO write custom double cell editor
            case Settings.EDIT_DOUBLE_SLIDER:
            case Settings.EDIT_DOUBLE: return new TableSpinner((DoubleRangeModel)sp.getModel());
            case Settings.EDIT_FUNCTION: return new DefaultCellEditor(new FunctionTextComboBox((FunctionTreeModel)sp.getModel()));
            // TODO write custom integer cell editor
            case Settings.EDIT_INTEGER_SLIDER:
            case Settings.EDIT_INTEGER: return new TableSpinner((IntegerRangeModel)sp.getModel());
            // TODO ensure events are handled properly
            case Settings.EDIT_PARAMETER: return new ParameterEditor((ParameterListModel)sp.getModel());
            // TODO write custom parametric function editor
            case Settings.EDIT_PARAMETRIC: return new DefaultCellEditor(new JTextField());
            case Settings.EDIT_SEPARATOR: return null;
            case Settings.EDIT_STRING: return new DefaultCellEditor(new JTextField());
        }
        return null;
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if(column!=VALUE_COLUMN){
            return super.getCellRenderer(row,column);
        }
        return new SettingsRenderer();
    }
    
    
    
    
    // INNER CLASSES
    
    /** Custom cell renderer for the table. */    
    public class SettingsRenderer extends DefaultTableCellRenderer {        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object settingsProperty, boolean isSelected, boolean hasFocus, int row, int column) {
            SettingsProperty sp=(SettingsProperty)settingsProperty;
            setForeground(Color.BLACK);
            setHorizontalAlignment(CENTER);
            switch(sp.getEditorType()){
                case Settings.EDIT_BOOLEAN:
                    return super.getTableCellRendererComponent(table, ((BooleanModel)sp.getModel()).getValue(), isSelected, hasFocus, row, column);
                case Settings.EDIT_COLOR:
                    setText("EDIT");
                    setBackground(((ColorModel)sp.getModel()).getValue());
                    break;
                case Settings.EDIT_COMBO:
                    setText(((StringRangeModel)sp.getModel()).toString());
                    break;
                case Settings.EDIT_DOUBLE_SLIDER:
                case Settings.EDIT_DOUBLE:
                    setText(((DoubleRangeModel)sp.getModel()).toString());
                    break;
                case Settings.EDIT_INTEGER_SLIDER:
                case Settings.EDIT_INTEGER:
                    setText(((IntegerRangeModel)sp.getModel()).toString());
                    break;
                case Settings.EDIT_FUNCTION:
                    setText(((FunctionTreeModel)sp.getModel()).toString());
                    break;
                case Settings.EDIT_PARAMETRIC:
                    setText(((ParametricModel)sp.getModel()).toString());
                    break;
            }
            return this;
        }
    } // CLASS SettingsRenderer //    
   
    /** Model class for accessing the table's data. Stores reference to Settings class. */
    public class SettingsTableModel extends AbstractTableModel {
        private String[] columnNames={"Name","Value"};
        private Settings settings;
        private boolean namesEditable=true;

        public SettingsTableModel(Settings settings){
            this.settings=settings;
        }
    
        @Override
        public int getRowCount() {return settings.size();}
        @Override
        public int getColumnCount() {return 2;}

        @Override
        public String getColumnName(int column) {
            if(column<2){return columnNames[column];}
            return null;
        }
    
        @Override
        public Class getColumnClass(int col){
            if(col==NAME_COLUMN){return String.class;}
            return FiresChangeEvents.class;
        }
        @Override
        public boolean isCellEditable(int row,int col){
            if(col==VALUE_COLUMN){
                if(((SettingsProperty)getValueAt(row,col)).getEditorType()==Settings.EDIT_SEPARATOR){return false;}
                return true;
            }
            if(col==NAME_COLUMN){return namesEditable;}
            return false;
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if(columnIndex==NAME_COLUMN){return settings.get(rowIndex).getName();}
            return settings.get(rowIndex);
        }
    } // CLASS SettingsTableModel //
    
    /** Custom spinner editor element. */
    public class TableSpinner extends AbstractCellEditor implements TableCellEditor {
        
        FiresChangeEvents model;
        
        public TableSpinner(FiresChangeEvents e){
            model=e;
        }
        
        @Override
        public Object getCellEditorValue() {
            return model;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(model instanceof DoubleRangeModel){
                return Settings.getSpinner((DoubleRangeModel)model);
            }else if(model instanceof IntegerRangeModel){
                return Settings.getSpinner((IntegerRangeModel)model);
            }
            return null;
        }
    }
}
