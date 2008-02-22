/*
 * ButtonPropertyEditor.java
 * Created on Feb 19, 2008
 */

package sequor.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * This abstract class contains functionality for pressing a button to open a dialog which can change a particular model. The extending class must
 * specify actions to take upon pressing the button and closing the dialog, as well as describing the dialog itself.
 * 
 * @author Elisha Peterson
 */
public abstract class ButtonPropertyEditor<F extends FiresChangeEvents> implements ActionListener,WindowListener {   
    
    /** Button which brings up a dialog. */
    JButton button;    
    /** Dialog box which is altered. */
    JDialog dialog;    
    /** Underlying model containing properties to be edited. */
    F model;
    
    /** Constant for editing action. */    
    protected static final String EDIT = "edit";
    
    /** Initializes the button. */
    public void initButton(){
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setText("click to edit");
    }
    /** Initializes to a particular model. */
    public void initModel(F model){this.model=model;}
    /** Creates the dialog. */
    public abstract void initDialog();
    /** Handles action. */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())){editPressed();dialog.setVisible(true);}
    }
    /** Handles dialog close action. */
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowClosing(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowOpened(WindowEvent e) {}
    
    /** Called when button is pressed. */
    public abstract void editPressed();

    /** Returns the button. */
    public JButton getButton(){return button;}
}
