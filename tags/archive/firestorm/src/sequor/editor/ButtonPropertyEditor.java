/*
 * ButtonPropertyEditor.java
 * Created on Feb 19, 2008
 */

package sequor.editor;

import sequor.SettingsProperty;
import sequor.FiresChangeEvents;
import sequor.model.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * This abstract class contains functionality for pressing a button to open a dialog which can change a particular model. The extending class must
 * specify actions to take upon pressing the button and closing the dialog, as well as describing the dialog itself. The class is implemented
 * as a cell editor to allow it to be added to a table as well as added to a panel on its own.
 * 
 * @author Elisha Peterson
 */
public abstract class ButtonPropertyEditor<F extends FiresChangeEvents> extends AbstractCellEditor 
        implements ActionListener,TableCellEditor {   
    
    /** Button which brings up a dialog. */
    JButton button;    
    /** Dialog box which is altered. */
    JDialog dialog;    
    /** Underlying model containing properties to be edited. */
    F model;
    
    /** Constant for editing action. */    
    protected static final String EDIT = "EDIT";
    
    /** Initializes the button. */
    public void initButton(){
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setText(EDIT);
    }
    /** Initializes to a particular model. */
    public void initModel(F model){this.model=model;}
    /** Creates the dialog. */
    public abstract void initDialog();
    /** Handles action. */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())){
            editPressed();
            dialog.setVisible(true);
            fireEditingStopped();
        }else{
            otherAction(e);
        }
    }
    /** Called when button is pressed. */
    public abstract void editPressed();
    /** Called when another action is performed. */
    public abstract void otherAction(ActionEvent e);

    /** Returns the button. */
    public JButton getButton(){return button;}
    
    /** Implements the one CellEditor method that AbstractCellEditor doesn't. */
    @Override
    public Object getCellEditorValue() {return model;}    
    /** Implements the one method defined by TableCellEditor. */
    @Override
    public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column) {
        model=(F)((SettingsProperty)value).getModel();
        return button;
    }
}
