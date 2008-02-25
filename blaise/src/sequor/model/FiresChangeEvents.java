/*
 * FiresChangeEvents.java
 * Created on Nov 12, 2007, 8:46:21 AM
 */

package sequor.model;

import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 *
 * @author ae3263
 */
public abstract class FiresChangeEvents {

    /** Clones this object. Should create a copy of the underlying models. */
    @Override
    public abstract FiresChangeEvents clone();
    /** Copies all model values from another FiresChangeEvents class. */
    public abstract void copyValuesFrom(FiresChangeEvents parent);
    /** Listens for changes from a class of the same type, and copies properties from the other model. */
    public FiresChangeEvents initListening(final FiresChangeEvents parent){
        parent.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                copyValuesFrom(parent);
            }
        });
        return this;
    }

    /** Generates a copy of this class which listens for changes from this one. */
    public FiresChangeEvents getDescendant() {return clone().initListening(this);}
    
    /** Basic support for string manipulation. */
    public abstract void setValue(String s);
    public String toLongString(){return toString();}
    
    public abstract PropertyChangeEvent getChangeEvent(String s);
    /** Event handling code copied from DefaultBoundedRangeModel. */      
    protected ChangeEvent changeEvent=null;
    protected EventListenerList listenerList=new EventListenerList();    
    public void addChangeListener(ChangeListener l){listenerList.add(ChangeListener.class,l);}
    public void removeChangeListener(ChangeListener l){listenerList.remove(ChangeListener.class,l);}
    protected void fireStateChanged(){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ChangeListener.class){
                if(changeEvent==null){changeEvent=new ChangeEvent(this);}
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }    
}
