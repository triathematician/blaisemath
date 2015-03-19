/**
 * SetSelectionModel.java
 * Created Aug 1, 2012
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2015 Elisha Peterson
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

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * <p>
 *   Model maintaining a collection of selected objects, and notifying listeners
 *   when that set changes.
 * </p>
 * @param <G> type of object that can be selected
 * @author elisha
 */
public class SetSelectionModel<G> {
    
    public static final String SELECTION_PROPERTY = "selection";

    private final Set<G> selected = Sets.newHashSet();

    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /** Initialize without arguments */
    public SetSelectionModel() {
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public boolean isEmpty() {
        return selected.isEmpty();
    }

    public void clearSelection() {
        setSelection(Collections.EMPTY_SET);
    }

    /**
     * Return a copy of the selection.
     * @return copy of selected
     */
    public Set<G> getSelection() {
        return ImmutableSet.copyOf(selected);
    }

    public void setSelection(Set<G> selection) {
        if (!selection.containsAll(selected) || !selected.containsAll(selection)) {
            Set<G> old = getSelection();
            selected.clear();
            selected.addAll(selection);
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    public void select(G g) {
        if (g != null && !selected.contains(g)) {
            Set<G> old = getSelection();
            selected.add(g);
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    public boolean isSelected(G g) {
        return selected.contains(g);
    }

    /**
     * Adds all to the current selection.
     * @param g elements to add
     */
    public void selectAll(Collection<G> g) {
        checkNotNull(g);
        if (!selected.containsAll(g)) {
            Set<G> old = getSelection();
            Iterables.addAll(selected, g);
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    public void deselect(G g) {
        Set<G> old = getSelection();
        if (g != null && selected.remove(g)) {
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    public void deselectAll(Collection<G> g) {
        checkNotNull(g);
        Set<G> old = getSelection();
        if (selected.removeAll(g)) {
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    /**
     * Toggle selection status of g
     * @param g object to toggle
     */
    public void toggleSelection(G g) {
        if (g != null) {
            Set<G> old = getSelection();
            if (selected.contains(g)) {
                selected.remove(g);
            } else {
                selected.add(g);
            }
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    //</editor-fold>

}
