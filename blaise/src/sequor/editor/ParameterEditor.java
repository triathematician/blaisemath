/**
 * ParameterEditor.java
 * Created on Feb 20, 2008
 */

package sequor.editor;

import sequor.model.*;
import sequor.editor.ButtonPropertyEditor;
import java.awt.event.ActionEvent;
import sequor.component.ParameterDialog;

/**
 * This class tells a button to open a dialog allowing the user to name and enter the value of a parameter.
 * 
 * @author Elisha Peterson
 */
public class ParameterEditor extends ButtonPropertyEditor<ParameterListModel>{
    public ParameterEditor(){this(new ParameterListModel());}
    public ParameterEditor(ParameterListModel plm){initModel(plm);initButton();initDialog();}
    
    @Override
    public void initButton(){
        super.initButton();
        button.setText("add");
    }
    @Override
    public void initDialog(){
        dialog=new ParameterDialog(null,false,this);
    }
    @Override
    public void editPressed() {}
    @Override
    public void otherAction(ActionEvent e) {
        if(e.getActionCommand().equals("OK")){
            ParameterDialog d=(ParameterDialog)dialog;
            model.addModel(d.getParameterName(),ParameterListModel.defaultDoubleRangeModel(d.getParameterValue()));
        }
    }
}
