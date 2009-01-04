/**
 * ParameterEditor.java
 * Created on Feb 20, 2008
 */

// TODO check to ensure parameter is not a special character like "e" or "pi"

package sequor.editor;

import sequor.model.*;
import java.awt.event.ActionEvent;
import sequor.component.ParameterDialog;

/**
 * <p>
 * This class tells a button to open a dialog allowing the user to name and enter the value of a parameter.
 * </p>
 * @author Elisha Peterson
 */
public class ParameterEditor extends ButtonPropertyEditor<ParameterListModel>{
    public ParameterEditor(){this(new ParameterListModel());}
    public ParameterEditor(ParameterListModel plm){initModel(plm);initButton();initDialog();}
    
    @Override
    public void initButton(){
        super.initButton();
        button.setText("Add parameter");
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
