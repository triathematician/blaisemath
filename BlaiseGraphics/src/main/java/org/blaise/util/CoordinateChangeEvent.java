/**
 * CoordinateChangeEvent.java
 * Created Aug 31, 2012
 */

package org.blaise.util;

import java.util.EventObject;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * <p>
 *  Event describing a change to a collection of coordinates (specifically to
 *  a {@link CoordinateManager} instance).
 * </p>
 * @author elisha
 */
public final class CoordinateChangeEvent extends EventObject {
    
    //<editor-fold defaultstate="collapsed" desc="FACTORY METHODS">
    //
    // FACTORY METHODS
    //

    /** 
     * Creates add event 
     * @param src source of event
     * @param added map of added objects, keys are objects/values are coordinates
     */
    public static CoordinateChangeEvent createAddEvent(Object src, Map added) {
        CoordinateChangeEvent evt = new CoordinateChangeEvent(src);
        evt.added = added;
        return evt;
    }

    /** 
     * Creates remove event 
     * @param src source of event
     * @param removed set of removed objects
     */
    public static CoordinateChangeEvent createRemoveEvent(Object src, Set removed) {
        CoordinateChangeEvent evt = new CoordinateChangeEvent(src);
        evt.removed = removed;
        return evt;
    }

    /** 
     * Creates add/remove event
     * @param src source of event
     * @param added map of added objects, keys are objects/values are coordinates
     * @param removed set of removed objects
     */
    public static CoordinateChangeEvent createAddRemoveEvent(Object src, Map added, Set removed) {
        CoordinateChangeEvent evt = new CoordinateChangeEvent(src);
        evt.added = added;
        evt.removed = removed;
        return evt;
    }
    
    //</editor-fold>
    

    /** Added coords */
    private Map added = null;
    /** Removed coords */
    private Set removed = null;

    /** 
     * Initialize with given source object
     * @param src source of event
     */
    public CoordinateChangeEvent(Object src) {
        super(src);
    }

    @Override
    public String toString() {
        return String.format("CoordinateChangeEvent[%d added,%d removed,source=%s]",
                added==null?0:added.size(), removed==null?0:removed.size(), source);
    }

    /**
     * Whether event indicates added coords 
     * @return true if coordinates were added
     */
    public boolean isAddEvent() {
        return added != null;
    }

    /** 
     * Whether event indicates removed coords 
     * @return true if coordinates were removed
     */
    public boolean isRemoveEvent() {
        return removed != null;
    }

    /**
     * Get the collection of coordinate that were added
     * @return map whose keys are the objects and values are their coordinates
     */
    public @Nullable Map getAdded() {
        return added;
    }

    /**
     * Get the collection of objects whose coordinates were removed
     * @return set of objects removed
     */
    public @Nullable Set getRemoved() {
        return removed;
    }

}
