/*
 * StyleHintSet.java
 * Created May 31, 2013
 */
package com.googlecode.blaisemath.style.context;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import java.util.Collections;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Maintains a collection of visibility hints that can be used to change how an
 * object is drawn. The {@link StyleContext} is responsible for switching out the
 * default style for an alternate style, as appropriate for these hints.
 * 
 * @author Elisha
 */
public final class StyleHintSet {
    
    /** Style hint indicating an invisible, non-functional element. */
    public static final String HIDDEN_HINT = "hidden";
    /** Style hint indicating an invisible but still functional element (receives mouse events) */
    public static final String HIDDEN_FUNCTIONAL_HINT = "hidden_functional";
    /** Style hint indicating a selected element. */
    public static final String SELECTED_HINT = "selected";
    /** Style hint indicating a highlighted element. */
    public static final String HILITE_HINT = "hilite";
    
    private final Set<String> hints = Sets.newHashSet();
    
    private final ChangeEvent changeEvent = new ChangeEvent(this);
    private final EventListenerList listenerList = new EventListenerList();
    
    @Override
    public String toString() {
        return hints.toString();
    }
    
    /**
     * Gets the hint set
     * @return immutable reference to hint set
     */
    public Set<String> getHints() {
        return Collections.unmodifiableSet(hints);
    }
    
    /**
     * Replaces the hint set with given hints.
     * @param hints new hints
     */
    public void setHints(Set<String> hints) {
        if (!this.hints.equals(hints)) {
            this.hints.clear();
            this.hints.addAll(hints);
            fireStateChanged();
        }
    }
    
    /**
     * Clears the hint set.
     */
    public void clearHints() {
        if (!hints.isEmpty()) {
            hints.clear();
            fireStateChanged();
        }
    }
    
    /**
     * Adds a hint to the set
     * @param hint hint to add
     * @return true if add changed set
     */
    public boolean add(String hint) {
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
    public boolean remove(String hint) {
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
    public boolean contains(String hint) {
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
    
    
    //
    // STATIC UTILITY METHODS
    //
    
    /**
     * Test whether given hints object is hidden
     * @param hints hints object
     * @return true if hints contains the hidden hint
     */
    public static boolean isInvisible(StyleHintSet hints) {
        return hints.contains(HIDDEN_HINT)
                || hints.contains(HIDDEN_FUNCTIONAL_HINT);
    }
    
    /**
     * Test whether given hints object is hidden
     * @param hints hints object
     * @return true if hints contains the hidden hint
     */
    public static boolean isFunctional(StyleHintSet hints) {
        return !hints.contains(HIDDEN_HINT);
    }
    
}
