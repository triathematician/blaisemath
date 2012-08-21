/*
 * ColorModel.java
 * 
 * Created on Sep 7, 2007, 2:18:25 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sequor.model;

import sequor.FiresChangeEvents;
import sequor.editor.ColorEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
@XmlRootElement(name="color")
public class ColorModel extends FiresChangeEvents {
    private Color c;
    
    // CONSTRUCTORS
    public ColorModel(){}
    public ColorModel(Color c){setValue(c);}
    
    // BEAN PATTERNS
    
    @XmlTransient
    public Color getValue(){return c;}
    public void setValue(Color newValue){if(newValue!=c){c=newValue;fireStateChanged();}}

    @Override
    public void setValue(String s){throw new UnsupportedOperationException("Not supported yet.");}

    //@XmlAttribute
    public int getRed(){return c.getRed();}
    public void setRed(int red){setValue(new Color(red, getGreen(), getBlue(), getAlpha()));}
    
    //@XmlAttribute
    public int getGreen(){return c.getGreen();}
    public void setGreen(int green){setValue(new Color(getRed(), green, getBlue(), getAlpha()));}
    
    //@XmlAttribute
    public int getBlue(){return c.getBlue();}
    public void setBlue(int blue){setValue(new Color(getRed(), getGreen(), blue, getAlpha()));}
    
    //@XmlAttribute
    public int getAlpha(){return c.getAlpha();}
    public void setAlpha(int alpha){setValue(new Color(getRed(), getGreen(), getBlue(), alpha));}
    
    @XmlAttribute
    public String getHexString(){
        String rgb = Integer.toHexString(c.getRGB());
        return rgb.substring(2,rgb.length());
    }
    public void setHexString(String s){c = Color.decode("#"+s);}

    
    @Override
    public String toLongString(){throw new UnsupportedOperationException("Not supported yet.");}
    @Override
    public PropertyChangeEvent getChangeEvent(String s){return new PropertyChangeEvent(this,s,null,c);}
    @Override
    public FiresChangeEvents clone(){return new ColorModel(c);}
    @Override
    public void copyValuesFrom(FiresChangeEvents parent){c=((ColorModel)parent).c;}
    
    public JButton getButton(){
        return new ColorEditor(this).getButton();
    }
    public JMenuItem getMenuItem(){
        final JMenuItem jmi=new JMenuItem("Change Color");
        jmi.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { // if menu item is selected
                final JColorChooser jcc=new JColorChooser();
                JColorChooser.createDialog(jmi,"Pick a Color",true,jcc,new ActionListener(){
                    public void actionPerformed(ActionEvent e) { // if OK is pressed
                        jmi.setForeground(jcc.getColor());
                        setValue(jcc.getColor());
                    }
                },null).setVisible(true);                
            }
        });
        jmi.setForeground(c);
        return jmi;
    }
}
