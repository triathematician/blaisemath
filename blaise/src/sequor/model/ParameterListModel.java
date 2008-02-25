/*
 * ParameterListModel.java
 * Created on Feb 19, 2008
 */

package sequor.model;

import java.beans.PropertyChangeEvent;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scribo.tree.Variable;
import sequor.component.Settings;

/**
 * This class is a data model for a list of parameters (doubles). It maps Strings (or Variables) to DoubleRangeModel's which control their values,
 * and will fire ChangeEvents if any of the values is changed. It can be used in conjunction with ParameterPanel to allow for automatically controlling
 * parameter inputs to a function.
 * 
 * @author ae3263
 */
public class ParameterListModel extends FiresChangeEvents implements ChangeListener {
    TreeMap<String,DoubleRangeModel> values;
    boolean added=false;
    
    public ParameterListModel(){
        values=new TreeMap<String,DoubleRangeModel>();
    }
    public ParameterListModel(TreeMap<String,DoubleRangeModel> values){
        this.values=new TreeMap<String,DoubleRangeModel>();
        for(Entry<String,DoubleRangeModel> e:values.entrySet()){
            this.values.put(e.getKey(),e.getValue());
        }
    }
    
    
    // PARAMETER METHODS

    /** Adds a model with a particular range of values. */
    public void addModel(String s,DoubleRangeModel drm){
        if(drm!=null){
            values.put(s,drm);
            drm.addChangeListener(this);
            added=true;
            fireStateChanged();
        }
        added=false;
    }
    
    public boolean isAdded(){return added;}

    /** Populates a list of parameters according to the specified values. */
    public TreeMap<String,Double> getParameterList(){
        TreeMap<String,Double> result=new TreeMap<String,Double>();
        for(String v:values.keySet()){
            result.put(v,values.get(v).getValue());
        }
        return result;
    }
    /** Sets a parameter value. */
    public boolean setParameterValue(String s,Double d){
        if(values.containsKey(s)){
            if(values.get(s).getValue()==d){
                return false;
            }
            values.get(s).setValue(d);
        } else {
            addModel(s, defaultDoubleRangeModel(d));
        }
        return true;
    }
    /** Removes a particular variable. */
    public void removeParameter(String s){
        if(values.containsKey(s)){
            values.get(s).removeChangeListener(this);
            values.remove(s);
        }
    }        
    
    
    // GUI INPUT/OUTPUTS
    
    /** Returns panel containing all the parameters. */
    public JPanel getPanel(){return updatePanel(new JPanel());}
    
    /** Updates a panel to contain the settings here. */
    public JPanel updatePanel(JPanel jp){
        Settings s=new Settings();
        for(Entry<String,DoubleRangeModel> e:values.entrySet()){
            s.addProperty(e.getKey(),e.getValue(),Settings.EDIT_DOUBLE);
        }
        s.addProperty("",this,Settings.EDIT_PARAMETER);
        s.initPanel(jp);
        return jp;
    }
    // INTERFACE METHODS

    @Override
    public FiresChangeEvents clone() {return new ParameterListModel(values);}
    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {        
        values.clear();
        for(String v:((ParameterListModel)parent).values.keySet()){
            this.values.put(v,((ParameterListModel)parent).values.get(v));
        }
    }
    @Override
    public void setValue(String s) {throw new UnsupportedOperationException("Not supported yet.");}
    @Override
    public String toLongString() {throw new UnsupportedOperationException("Not supported yet.");}
    @Override
    public PropertyChangeEvent getChangeEvent(String s) {return new PropertyChangeEvent(this,s,null,null);}

    @Override
    public void stateChanged(ChangeEvent e) {fireStateChanged();}

    
    // UTILITY METHODS
    
    /** Returns a default DoubleRangeModel */
    public static DoubleRangeModel defaultDoubleRangeModel(double d){
        return new DoubleRangeModel(d,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,.1);
    }
}
