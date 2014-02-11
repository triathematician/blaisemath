/*
 * VisibilityHints.java
 * Created May 31, 2013
 */
package org.blaise.style;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Maintains a collection of visibility hints that can be used by renderers to change
 * how an object is drawn.
 * 
 * @author Elisha
 */
public final class VisibilityHintSet {
    
    private final Set<VisibilityHint> hints = Sets.newHashSet();
    
    @Override
    public String toString() {
        return hints.toString();
    }
    
    /**
     * Adds a hint to the set
     * @param hint hint to add
     * @return true if add changed set
     */
    public boolean add(VisibilityHint hint) {
        if (hints.add(hint)) {
            fireStateChanged();
            return true;
        }
        return false;
    }
    
    /**
     * Removes a hint from the set
     * @param hint hint to remove
     * @return true if remove changed set
     */
    public boolean remove(VisibilityHint hint) {
        if (hints.remove(hint)) {
            fireStateChanged();
            return true;
        }
        return false;
    }
    
    /**
     * Return status of given hint.
     * @param hint a hint to test
     * @return true if contained in the set of hints of this class, false otherwise 
     */
    public boolean contains(VisibilityHint hint) {
        return hints.contains(hint);
    }

    
    
    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //
    
    protected ChangeEvent changeEvent = new ChangeEvent(this);
    protected EventListenerList listenerList = new EventListenerList();

    public void addChangeListener(ChangeListener l) { 
        listenerList.add(ChangeListener.class, l);
    }
    
    public void removeChangeListener(ChangeListener l) { 
        listenerList.remove(ChangeListener.class, l); 
    }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    public void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    return;
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }
    
    //</editor-fold>
    
}
