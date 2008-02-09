/*
 * ParametricModel.java
 *
 * Created on Sep 10, 2007, 3:49:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Model;

import Blaise.BParser;
import Euclidean.PPoint;
import Interface.BModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class ParametricModel extends BModel implements ActionListener{
    private BParser px;
    private String sx="cos(t)";
    private BParser py;
    private String sy="sin(t)";
    public ParametricModel(){px=new BParser("t");py=new BParser("t");px.addActionListener(this);py.addActionListener(this);}
    public ParametricModel(BParser px,BParser py){this.px=px;this.py=py;px.addActionListener(this);py.addActionListener(this);}
    public void setValue(String s){throw new UnsupportedOperationException("Not supported yet.");}
    public void setValue(BParser px,BParser py){if(this.px!=px){this.px=px;sx=null;fireStateChanged();}if(this.py!=py){this.py=py;sy=null;fireStateChanged();}}
    public void setXString(String s){sx=s;px.setExpressionString(s);}
    public void setYString(String s){sy=s;py.setExpressionString(s);}
    public BParser getParserX(){return px;}
    public BParser getParserY(){return py;}
    public String getStringX(){return sx;}
    public String getStringY(){return sy;}
    public PPoint getValue(double t){return new PPoint(px.getValue(t),py.getValue(t));}
    public String toLongString(){throw new UnsupportedOperationException("Not supported yet.");}
    public PropertyChangeEvent getChangeEvent(String s){return new PropertyChangeEvent(this,s,null,null);}
    public String toString(){return "("+sx+","+sy+")";}
    public void actionPerformed(ActionEvent e){fireStateChanged();}
}
