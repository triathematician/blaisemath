/*
 * ParametricModel.java
 *
 * Created on Sep 10, 2007, 3:49:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sequor.model;

import java.util.Vector;
import sequor.FiresChangeEvents;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import scio.function.FunctionValueException;
import scribo.parser.FunctionSyntaxException;
import scribo.parser.FunctionTreeFactory;
import scribo.tree.FunctionTreeRoot;
import scio.coordinate.R2;
import scio.function.BoundedFunction;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class ParametricModel extends FiresChangeEvents implements ActionListener{
    FunctionTreeRoot fx,fy;
    private String sx="cos(t)";
    private String sy="sin(t)";
    public ParametricModel(){
        try {
            fx = (FunctionTreeRoot) FunctionTreeFactory.getFunction(sx);
            fy = (FunctionTreeRoot) FunctionTreeFactory.getFunction(sy);
        } catch (FunctionSyntaxException ex) {            
        }
    }
    public ParametricModel(String sx,String sy){
        setXString(sx);
        setYString(sy);
    }
    public void setValue(String s){throw new UnsupportedOperationException("Not supported yet.");}
    public void setXString(String s){
        try {
            fx=(FunctionTreeRoot) FunctionTreeFactory.getFunction(s);
            sx=s;
        } catch (FunctionSyntaxException ex){}
    }
    public void setYString(String s){
        try {
            fy=(FunctionTreeRoot) FunctionTreeFactory.getFunction(s);
            sy=s;
        } catch (FunctionSyntaxException ex){}
    }
    public FunctionTreeRoot getTreeX(){return fx;}
    public FunctionTreeRoot getTreeY(){return fy;}
    public String getStringX(){return sx;}
    public String getStringY(){return sy;}
    public R2 getValue(double t) throws FunctionValueException{return new R2(fx.getValue(t),fy.getValue(t));}
    public BoundedFunction<Double,R2> getFunction(){
        return new BoundedFunction<Double,R2>(){
            public R2 getValue(Double x){try {
                    return ParametricModel.this.getValue(x);
                } catch (FunctionValueException ex) {
                    return null;
                }
}
            @Override
            public Vector<R2> getValue(Vector<Double> x) {
                Vector<R2> result=new Vector<R2>(x.size());
                for(Double d:x){result.add(getValue(d));}
                return result;
            }
            public R2 minValue(){return null;}
            public R2 maxValue(){return null;}
        };
    }
    public String toLongString(){throw new UnsupportedOperationException("Not supported yet.");}
    public PropertyChangeEvent getChangeEvent(String s){return new PropertyChangeEvent(this,s,null,null);}
    public String toString(){return "( "+sx+" , "+sy+" )";}
    public void actionPerformed(ActionEvent e){fireStateChanged();}

    @Override
    public FiresChangeEvents clone() {return new ParametricModel(sx,sy);}
    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {
        setXString(((ParametricModel)parent).sx);
        setYString(((ParametricModel)parent).sy);
    }
}
