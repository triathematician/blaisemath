/*
 * FunctionInputBar.java
 * Created on Nov 13, 2007, 9:17:49 AM
 */

package sequor.component;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.model.FunctionTreeModel;

/**
 * Contains a text field for inputting a function, as well as a combo box for selecting the type
 * of function.
 * 
 * @author Elisha Peterson
 */
public class FunctionInputBar extends JPanel implements ChangeListener,ItemListener{
    public FunctionInputBar(){
        FunctionTreeModel ftm=new FunctionTreeModel();
        FunctionTextField fttf=new FunctionTextField(ftm);
        ftm.addChangeListener(this);
        fttf.setMinimumSize(new Dimension(100,20));
        fttf.setPreferredSize(new Dimension(100,20));
        JComboBox chooser=functionChoiceBox();
        chooser.addItemListener(this);
        setLayout(new FlowLayout());
        add(fttf);
        add(chooser);
    }
    
    final static String fs[]={"Simple f(x)","Parametric (x(t),y(t))","Two Inputs f(x,y)","Vector Field (x(x,y),y(x,y))"};
    
    public JComboBox functionChoiceBox(){
        return new JComboBox(new DefaultComboBoxModel(fs));
    }

    public void stateChanged(ChangeEvent e) {
    }

    public void itemStateChanged(ItemEvent e) {
    }
}
