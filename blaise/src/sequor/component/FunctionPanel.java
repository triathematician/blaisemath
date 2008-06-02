/*
 * FunctionPanel.java
 * Created on Nov 13, 2007, 9:17:49 AM
 */

package sequor.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.FunctionTreeModel;
import specto.plotpanel.Plot2D;
import specto.plottable.Function2D;

/**
 * Contains several functions and function models which can be controlled at once. Displays via a SettingsPanel.
 * 
 * @author Elisha Peterson
 */
public class FunctionPanel extends SettingsPanel {
    
    /** Default constructor. */
    public FunctionPanel() { this(1); }

    /** Constructs with specified number of functions. */
    public FunctionPanel(int n){
        super(new Settings());
        if (n == 1) { 
            addFunction("f(?)=","cos(x)");
        } else {
            for(int i=1; i<=n; i++) { addFunction("f"+i+"(?)=","x^"+i); }
        }
        updatePanel();        
    }

    /** Appends a function to the panel. */
    public void addFunction(String label, String value) { 
        FunctionTreeModel ftm = new FunctionTreeModel(value);
        s.add(new SettingsProperty(label,ftm,Settings.EDIT_FUNCTION));        
    }
    
    /** Copies all functions (defaulted to real-valued functions) to a plot window. */
    public void addToPlot(Plot2D p) {
        for (SettingsProperty sp : s) {
            p.add(new Function2D((FunctionTreeModel)sp.getModel()));
        }
    }
    
    /** Returns the i'th function model contained herein. */
    public FunctionTreeModel getFunctionModel(int i) { 
        return ((FunctionTreeModel)s.get(i).getModel());
    }
    
    /** Updates the panel. */
    @Override
    public void updatePanel() {
        super.updatePanel();
        setComponentPopupMenu(getMenu());
    }
    
    public JPopupMenu getMenu() {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem mi = new JMenuItem("Add Function");
        mi.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                addFunction("g(x)=","x");
                FunctionPanel.super.updatePanel();
            }
        });
        contextMenu.add(mi);
        contextMenu.addSeparator();
        contextMenu.add(getDeleteMenu());        
        return contextMenu;
    }
}
