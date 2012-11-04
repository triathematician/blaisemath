/**
 * CoordinateChangeEvent.java
 * Created Aug 31, 2012
 */

package org.blaise.util;

import java.util.EventObject;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  Event describing a change to a collection of coordinates (specifically to
 *  a {@link CoordinateManager} instance).
 * </p>
 * @author elisha
 */
public class CoordinateChangeEvent extends EventObject {

    /** Creates add event */
    public static CoordinateChangeEvent createAddEvent(Object src, Map added) {
        CoordinateChangeEvent evt = new CoordinateChangeEvent(src);
        evt.added = added;
        return evt;
    }

    /** Creates remove event */
    public static CoordinateChangeEvent createRemoveEvent(Object src, Set removed) {
        CoordinateChangeEvent evt = new CoordinateChangeEvent(src);
        evt.removed = removed;
        return evt;
    }

    /** Creates add/remove event */
    public static CoordinateChangeEvent createAddRemoveEvent(Object src, Map added, Set removed) {
        CoordinateChangeEvent evt = new CoordinateChangeEvent(src);
        evt.added = added;
        evt.removed = removed;
        return evt;
    }

    /** Added coords */
    Map added = null;
    /** Removed coords */
    Set removed = null;

    /** Initialize without arguments */
    public CoordinateChangeEvent(Object src) {
        super(src);
    }

    @Override
    public String toString() {
        return String.format("CoordinateChangeEvent[%d added,%d removed,source=%s]",
                added==null?0:added.size(), removed==null?0:removed.size(), source);
    }

    /** Whether event indicates added coords */
    public boolean isAddEvent() {
        return added != null;
    }

    /** Whether event indicates removed coords */
    public boolean isRemoveEvent() {
        return removed != null;
    }

    public Map getAdded() {
        return added;
    }

    public Set getRemoved() {
        return removed;
    }

}
