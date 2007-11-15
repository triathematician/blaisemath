/*
 * FiresChangeEvents.java
 * 
 * Created on Nov 12, 2007, 8:46:21 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    /** Basic support for string manipulation. */
    public abstract void setValue(String s);
    public abstract String toLongString();
    
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
