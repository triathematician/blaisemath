/*
 * BooleanModelGroup.java
 * Created on Mar 23, 2008
 */

package sequor.model;

import java.beans.PropertyChangeEvent;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.FiresChangeEvents;

/**
 * BooleanModelGroup is a group of BooleanModel's which can be made to be mutually exclusive.
 * 
 * @author Elisha Peterson
 */
public class BooleanModelGroup extends FiresChangeEvents implements ChangeListener {
    
    Vector<BooleanModel> models;
    boolean exclusive;
    boolean forceSelect; 
    BooleanModel lastSelected;
    
    
    // CONSTRUCTORS
    
    public BooleanModelGroup(){this(false,false);}
    public BooleanModelGroup(boolean exclusive,boolean forceSelect){
        models=new Vector<BooleanModel>();
        this.exclusive=exclusive;
        this.forceSelect=forceSelect;
    }
    
    
    // ADD & REMOVE METHODS
    
    public void add(BooleanModel bm){
        models.add(bm);
        bm.addChangeListener(this);
    }
    
    public void remove(BooleanModel bm){
        models.remove(bm);
        bm.removeChangeListener(this);
    }

    
    // QUERY METHODS
    
    public boolean isExclusive(){return exclusive;}
    public boolean isForceSelect(){return forceSelect;}
        
    /** Returns the list of models. */
    public Vector<BooleanModel> getModels() {return models;}
    /** Returns the first active/true model. */
    public BooleanModel getActiveModel(){
        for(BooleanModel bm:models){
            if(bm.isTrue()){return bm;}
        }
        return null;
    }
    /** Returns the last selected model. */
    public BooleanModel getLastSelected(){return lastSelected;}
    
    /** Determines whether any of the underlying models are active/true. */
    public boolean isActive(){return getActiveModel()!=null;}
    
    /** Sets all to be off. */
    public void clearSelection() {
        if(forceSelect){return;}
        adjusting=true;
        for(BooleanModel bm:models){bm.setValue(false);}
        adjusting=false;
        fireStateChanged();                
    }
    
    // EVENT HANDLING
    
    boolean adjusting=false;    
    
    /** Force adjustmenets to underlying models based on exclusive and forceSelect flags. */
    public void stateChanged(ChangeEvent e) {
        if(adjusting || !(e.getSource() instanceof BooleanModel)){return;}
        BooleanModel ebm = (BooleanModel) e.getSource();
        if(!models.contains(ebm)){return;}
        adjusting=true;
        if(ebm.isTrue() && exclusive){
            lastSelected=ebm;
            for(BooleanModel bm:models){
                if(!bm.equals(e.getSource())){
                    bm.setValue(false);
                }
            }
        }else if(!ebm.isTrue() && forceSelect){
            models.firstElement().setValue(true);
            lastSelected=models.firstElement();
        }
        adjusting=false;
        fireStateChanged();
    }
    
    
    // FIRESCHANGEEVENTS elements

    @Override
    public FiresChangeEvents clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PropertyChangeEvent getChangeEvent(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
