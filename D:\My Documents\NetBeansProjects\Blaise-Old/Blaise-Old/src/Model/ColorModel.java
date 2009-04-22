/*
 * ColorModel.java
 * 
 * Created on Sep 7, 2007, 2:18:25 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Model;

import Interface.BModel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class ColorModel extends BModel {
    private Color c;
    public ColorModel(){}
    public ColorModel(Color c){setValue(c);}
    public void setValue(String s){throw new UnsupportedOperationException("Not supported yet.");}
    public Color getValue(){return c;}
    public void setValue(Color newValue){if(newValue!=c){c=newValue;fireStateChanged();}}
    public String toLongString(){throw new UnsupportedOperationException("Not supported yet.");}
    public PropertyChangeEvent getChangeEvent(String s){return new PropertyChangeEvent(this,s,null,c);}
}
