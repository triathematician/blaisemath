/*
 * FunctionPanel.java
 * Created on Nov 13, 2007, 9:17:49 AM
 */

package sequor.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.FunctionTreeModel;

/**
 * Contains several functions and function models which can be controlled at once. Displays via a SettingsPanel.
 * 
 * @author Elisha Peterson
 */
public class FunctionPanel extends SettingsPanel implements ChangeListener {
    
    /** Default constructor. */
    public FunctionPanel() { this(1); }

    /** Initializes with specified list of function/variable pairs. If there is only a single element in
     * one of the arrays, the function is created with default variables (found by parsing the function). If
     * the array has two elements, a label is given to the function, e.g. f(x)=. If the array has three or
     * more elements, the remaining elements are all treated as variables.
     * @param functions
     */
    public FunctionPanel(String[][] functions) {
        super(new Settings());
        for (int i = 0; i < functions.length; i++) {
            switch (functions[i].length) {
                case 0: break;
                case 1: addFunction(functions[i][0]); break;
                case 2: addFunction(functions[i][0],functions[i][1]); break;
                case 3: addFunction(functions[i][0],functions[i][1],functions[i][2]); break;
                default:
                    String[] vars = new String[functions[i].length-2];
                    for (int j = 2; j < functions[i].length; j++) { vars[j-2] = functions[i][j]; }
                    addFunction(functions[i][0],functions[i][1],vars); break;
            }
        }
        updatePanel();
    }

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
    public void addFunction(String value) { 
        FunctionTreeModel ftm = new FunctionTreeModel(value);
        String label = "f("+ftm.getRoot().getVariables().toString()+")=";
        s.add(new SettingsProperty(label,ftm,Settings.EDIT_FUNCTION));        
    }
    /** Appends a function to the panel. */
    public void addFunction(String label, String value) { 
        FunctionTreeModel ftm = new FunctionTreeModel(value);
        s.add(new SettingsProperty(label,ftm,Settings.EDIT_FUNCTION));        
    }
    /** Appends a function to the panel. */
    public void addFunction(String label, String value, String var) {
        FunctionTreeModel ftm = new FunctionTreeModel(value, var);
        s.add(new SettingsProperty(label,ftm,Settings.EDIT_FUNCTION));        
    }
    /** Appends a function to the panel. */
    public void addFunction(String label, String value, String[] vars) {
        FunctionTreeModel ftm = new FunctionTreeModel(value, vars);
        s.add(new SettingsProperty(label,ftm,Settings.EDIT_FUNCTION));        
    }
    
//    /** Copies all functions (defaulted to real-valued functions) to a plot window. */
//    public void addToPlot(Plot2D p) {
//        for (SettingsProperty sp : s) {
//            p.add(new Function2D((FunctionTreeModel)sp.getModel()));
//        }
//    }
        
    /** Returns the i'th function model contained herein. */
    public FunctionTreeModel getFunctionModel(int i) {
        return ((FunctionTreeModel)s.get(i).getModel());
    }

    /** Sets the i'th function model. */
    public void setFunctionModel(int i,FunctionTreeModel ftm){
        if (i < s.size()) {
            s.get(i).setModel(ftm); updatePanel();
        }
    }

    /** Returns the i'th function name. */
    public String getFunctionName(int i){
        return s.get(i).getName();
    }

    /** Sets the i'th function name. */
    public void setFunctionName(int i, String name){
        if (i < s.size()) {
            s.get(i).setName(name); updatePanel();
        }
    }

    /** Sets the i'th function by string. */
    public void setFunction(int i, String newFunction, String[] vars){
        if (i < s.size()) {
            getFunctionModel(i).setValue(newFunction, vars);
        }
    }

    /** Sets the i'th function name and value. */
    public void setFunction(int i, String name, String newFunction, String[] vars){ 
        setFunctionName(i, name);
        setFunction(i, newFunction, vars);
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

    /** Called a change is made that should be forwarded to the underlying functions. */
    public void stateChanged(ChangeEvent e) {
        for (int i = 0; i < s.size(); i++) {
            ((FunctionTreeModel)s.get(i).getModel()).stateChanged(e);
        }
    }
}
