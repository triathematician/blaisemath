/*
 * VisibilityHints.java
 * Created May 31, 2013
 */
package org.blaise.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
    
    private final ChangeEvent changeEvent = new ChangeEvent(this);
    private final EventListenerList listenerList = new EventListenerList();
    
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
