/*
 * ColorEditor.java
 * 
 * Created on Sep 7, 2007, 2:40:03 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sequor.model;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class ColorEditor implements ChangeListener,ActionListener{
    ColorModel cm;
    public ColorEditor(){initializeModels();initColorPanel();}
    public ColorEditor(ColorModel cm){initializeModels(cm);initColorPanel();}
    public void initializeModels(){if(cm==null){cm=new ColorModel();}cm.addChangeListener(this);}
    public void initializeModels(ColorModel cm){this.cm=cm;cm.addChangeListener(this);}
    
    JButton button;
    JColorChooser colorChooser;
    JDialog dialog;
    protected static final String EDIT = "edit";

    public JButton getButton(){return button;}
    
    public void initColorPanel(){
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setBackground(cm.getValue());
        button.setText("click to edit");
        colorChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(button,"Pick a Color",true,colorChooser,this,null);
    }

    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())){colorChooser.setColor(cm.getValue());dialog.setVisible(true);}
        else {cm.setValue(colorChooser.getColor());}
        button.setBackground(cm.getValue());
    }

    public void stateChanged(ChangeEvent e){}
}
