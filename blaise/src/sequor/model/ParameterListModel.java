/*
 * ParameterListModel.java
 * Created on Feb 19, 2008
 */

package sequor.model;

import sequor.FiresChangeEvents;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.annotation.XmlRootElement;
import sequor.Settings;
import sequor.component.SettingsPanel;

/**
 * This class is a data model for a list of parameters (doubles). It maps Strings (or Variables) to DoubleRangeModel's which control their values,
 * and will fire ChangeEvents if any of the values is changed. It can be used in conjunction with ParameterPanel to allow for automatically controlling
 * parameter inputs to a function.
 * 
 * @author ae3263
 */
@XmlRootElement(name="parameterList")
public class ParameterListModel extends FiresChangeEvents implements ChangeListener {
    TreeMap<String,DoubleRangeModel> values;
    boolean added=false;
    boolean useSliders=true;
    
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
        
    /** Updates a panel to contain the settings here. */
    public JPanel updatePanel(final SettingsPanel jp){
        Settings s=new Settings();
        if(!useSliders){
            for(Entry<String,DoubleRangeModel> e:values.entrySet()){
                s.addProperty(e.getKey(),e.getValue(),Settings.EDIT_DOUBLE);
            }
        }else{
            for(Entry<String,DoubleRangeModel> e:values.entrySet()){
                s.addProperty(e.getKey(),e.getValue(),Settings.EDIT_DOUBLE_SLIDER);
            }
        }
        s.addProperty("",this,Settings.EDIT_PARAMETER);
        jp.setSettings(s);
        jp.updatePanel();
        JPopupMenu context=new JPopupMenu();
        JMenuItem mi;
        if(useSliders){
            mi=new JMenuItem("Use spinners");
            mi.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {useSliders=false;updatePanel(jp);jp.setPreferredSize(new Dimension(80,150));fireStateChanged();}
            });
        }else{
            mi=new JMenuItem("Use sliders");
            mi.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {useSliders=true;updatePanel(jp);jp.setPreferredSize(new Dimension(200,150));fireStateChanged();}
            });            
        }
        context.add(mi);
        jp.setComponentPopupMenu(context);
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
        return new DoubleRangeModel(d,-10,10,.1);
    }
}
