/*
 * ComboRangeModel.java
 * Created on Sep 7, 2007, 1:55:21 PM
 */

package sequor.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * This class extends the standard combo box with an underlying IntegerRangeModel to handle
 * which property is selected.
 * <br><br>
 * @author Elisha Peterson
 */
public class ComboBoxRangeModel extends IntegerRangeModel{
    String[] s;
    public ComboBoxRangeModel(){
        String[] test={"test1","test2","test3"};
        s=test;
        setRangeProperties(1,0,2);
    }
    public ComboBoxRangeModel(String[] s,int newValue,int newMin,int newMax){this.s=s;setRangeProperties(newValue,newMin,newMax);}
    public String[] getStrings(){return s;}
    @Override
    public String toString(){return s[getValue()];}
    public String getString(int i){return s[i];}
    @Override
    public void setValue(String sv){
        for(int i=0;i<s.length;i++){
            if(sv.equals(s[i])){
                setValue(i);
            }
        }
    }

    // GUI GENERATING METHODS
    
    /** Generates a combo box given a string list. */
    public JComboBox getComboBox() {return new JComboBox(new ComboBoxEditor(this));}

    /** Generates a submenu with this list of options. */
    public JMenuItem getSubMenu(String name){
        // TODO test this method; really not sure if it works
        JMenu subMenu=new JMenu(name);
        ButtonGroup group=new ButtonGroup();
        for(int i=getMinimum();i<=getMaximum();i++){
            final int j=i;
            JRadioButtonMenuItem item=new JRadioButtonMenuItem(getString(i));
            item.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {setValue(j);}
            });
            subMenu.add(item);
            group.add(item);
            if(i==getValue()){item.setSelected(true);}
        }
        return subMenu;
    }
}
