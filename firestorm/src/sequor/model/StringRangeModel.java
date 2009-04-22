/*
 * ComboRangeModel.java
 * Created on Sep 7, 2007, 1:55:21 PM
 */

package sequor.model;

import sequor.editor.ComboBoxEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class extends the standard combo box with an underlying IntegerRangeModel to handle
 * which property is selected.
 * <br><br>
 * @author Elisha Peterson
 */
@XmlRootElement(name="stringRangeModel")
public class StringRangeModel extends IntegerRangeModel{
    String[] s;
    
    
    // CONSTRUCTORS
    
    public StringRangeModel(){
        String[] test={"test1","test2","test3"};
        s=test;
        setRangeProperties(1,0,2);
    }
    public StringRangeModel(String[] s){super();setStrings(s);}
    public StringRangeModel(String[] s, int newValue) {super();setStrings(s);setValue(newValue);}
    public StringRangeModel(String[] s,int newValue,int newMin,int newMax){this.s=s;setRangeProperties(newValue,newMin,newMax);}

    
    // BEAN PATTERNS
        
    public void setString(int i,String str){s[i]=str;}
    public String getString(int i){return s[i];}
    
    @XmlElement
    public void setStrings(String[] s){this.s=s;setRangeProperties(0,0,s.length-1);}
    public String[] getStrings(){return s;}
    
    @Override
    public String toString(){return s[getValue()];}
    
    @Override
    public void setValue(String sv){
        for(int i=0;i<s.length;i++){
            if(sv.equals(s[i])){
                setValue(i);
            }
        }
    }
    
    public void cycle() {super.increment(true);}

    
    // GUI GENERATING METHODS
    
    /** Generates a combo box given a string list. */
    public JComboBox getComboBox() {return new JComboBox(new ComboBoxEditor(this));}

    /** Generates a submenu with this list of options. */
    public Vector<JMenuItem> getMenuItems(){
        Vector<JMenuItem> result=new Vector<JMenuItem>();
        ButtonGroup group=new ButtonGroup();
        for(int i=getMinimum();i<=getMaximum();i++){
            final int j=i;
            JRadioButtonMenuItem item=new JRadioButtonMenuItem(getString(i));
            item.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {setValue(j);}
            });
            result.add(item);
            group.add(item);
            if(i==getValue()){item.setSelected(true);}
        }
        return result;
    }
    
    /** Appends the items to a specified menu. */
    public JMenu appendToMenu(JMenu menu){
        if(menu==null){return null;}
        menu.addSeparator();
        for(JMenuItem mi:getMenuItems()){
            menu.add(mi);
        }
        return menu;
    }
}
