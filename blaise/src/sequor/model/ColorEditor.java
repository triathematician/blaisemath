/*
 * ColorEditor.java
 * Created on Sep 7, 2007, 2:40:03 PM
 */

package sequor.model;


import java.awt.event.ActionEvent;
import javax.swing.JColorChooser;
import javax.swing.JMenuItem;

/**
 * This class is able to return a button which opens a dialog to edit a color model upon being pressed.
 * 
 * @author Elisha Peterson
 */
public class ColorEditor extends ButtonPropertyEditor<ColorModel> {
    JColorChooser colorChooser;
    
    public ColorEditor(){this(new ColorModel());}
    public ColorEditor(ColorModel cm){initModel(cm);initButton();initDialog();}
    
    @Override
    public void initButton(){
        super.initButton();
        button.setBorderPainted(false);
        button.setBackground(model.getValue());
    }
    @Override
    public void initDialog(){
        colorChooser=new JColorChooser();
        dialog=JColorChooser.createDialog(button,"Pick a Color",true,colorChooser,this,null);
    }
    @Override
    public void editPressed() {
        colorChooser.setColor(model.getValue());
    }
    @Override
    public void otherAction(ActionEvent e){
        if(e.getActionCommand().equals("OK")){
            model.setValue(colorChooser.getColor());
            button.setBackground(model.getValue());
        }
    }
}
