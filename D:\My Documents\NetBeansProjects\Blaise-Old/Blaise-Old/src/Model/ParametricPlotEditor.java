/*
 * ParametricPlotEditor.java
 * 
 * Created on Sep 10, 2007, 3:13:39 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Model;

import Blaise.BParametricFunctionPanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class ParametricPlotEditor implements ActionListener{
    ParametricModel pm;
    ParametricPlotEditor(){initializeModels();initEditPanel();}
    ParametricPlotEditor(ParametricModel pm){initializeModels(pm);initEditPanel();}
    public void initializeModels(){if(pm==null){pm=new ParametricModel();}}
    public void initializeModels(ParametricModel pm){this.pm=pm;}
    
    JButton button;
   // JDialog dialog;
    protected static final String EDIT = "edit";

    public void initEditPanel(){
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBackground(Color.YELLOW);
        button.setText(pm.toString());
        //dialog = JColorChooser.createDialog(button,"Pick a Color",true,colorChooser,this,null);
    }

    public void actionPerformed(ActionEvent e) {
        if(EDIT.equals(e.getActionCommand())){
            Object[] options={new BParametricFunctionPanel(pm),"OK","Cancel"};
            int n=JOptionPane.showOptionDialog(
                    null,
                    "Enter your parametric function below.",
                    "Parametric Function",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0]); //default button title
        }
        button.setText(pm.toString());
    }

    public void stateChanged(ChangeEvent e){}
}
