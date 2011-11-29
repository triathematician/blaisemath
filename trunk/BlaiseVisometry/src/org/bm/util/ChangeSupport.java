/*
 * DefaultChangeBroadcaster.java
 * Created Apr 7, 2010
 */

package org.bm.util;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * <p>
 *  Contains default code to broadcast {@link ChangeEvent}s.
 * </p>
 * @see PropertyChangeSupport
 * @author Elisha Peterson
 */
public class ChangeSupport {

    protected Object source;
    protected ChangeEvent changeEvent;
    protected EventListenerList listenerList = new EventListenerList();

    public ChangeSupport() { changeEvent = new ChangeEvent(this); }
    public ChangeSupport(Object source) { changeEvent = new ChangeEvent(source); }

    public synchronized void addChangeListener(ChangeListener l) { 
        listenerList.add(ChangeListener.class, l);
    }
    
    public synchronized void removeChangeListener(ChangeListener l) { 
        listenerList.remove(ChangeListener.class, l); 
    }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    public synchronized void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null)
                    return;
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
    }
}
