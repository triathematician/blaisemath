package Interface;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * <b>BModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 8, 2007, 4:15 PM</i><br><br>
 * 
 * Basic methodology for creating models which have change listeners.
 */
public abstract class BModel {
    /** Basic support for string manipulation. */
    public abstract void setValue(String s);
    public abstract String getString();
    public abstract String getLongString();
    
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
