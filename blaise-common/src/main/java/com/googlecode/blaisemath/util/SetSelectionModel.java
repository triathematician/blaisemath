package com.googlecode.blaisemath.util;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;

/**
 * Tracks a set of objects, with convenience methods to adjust the set, and
 * notifies listeners on changes.
 * 
 * @param <G> type of object that can be selected
 * @author Elisha Peterson
 */
public class SetSelectionModel<G> {
    
    public static final String SELECTION_PROPERTY = "selection";

    private final Set<G> selected = Sets.newHashSet();

    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    //region PROPERTIES

    /**
     * Check if selection is empty.
     * @return true if empty
     */
    public boolean isEmpty() {
        return selected.isEmpty();
    }
    
    /**
     * Return a copy of the selection.
     * @return copy of selected
     */
    public Set<G> getSelection() {
        return ImmutableSet.copyOf(selected);
    }

    /**
     * Replace entire selection with argument.
     * @param selection new selection
     */
    public void setSelection(Set<G> selection) {
        if (!selection.containsAll(selected) || !selected.containsAll(selection)) {
            Set<G> old = getSelection();
            selected.clear();
            selected.addAll(selection);
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    //endregion

    /**
     * Clear the selection.
     */
    public void clearSelection() {
        setSelection(emptySet());
    }

    /**
     * Add the given item to the selection.
     * @param g item to select
     */
    public void select(G g) {
        if (g != null && !selected.contains(g)) {
            Set<G> old = getSelection();
            selected.add(g);
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    /**
     * Check if the given item is selected.
     * @param g item
     * @return true if selected
     */
    public boolean isSelected(G g) {
        return selected.contains(g);
    }

    /**
     * Adds all arguments to the current selection.
     * @param g elements to add
     */
    public void selectAll(Collection<G> g) {
        requireNonNull(g);
        if (!selected.containsAll(g)) {
            Set<G> old = getSelection();
            Iterables.addAll(selected, g);
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    /**
     * Remove an item from the selection.
     * @param g item to remove
     */
    public void deselect(G g) {
        Set<G> old = getSelection();
        if (g != null && selected.remove(g)) {
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    /**
     * Removes all arguments from the current selection.
     * @param g elements to remove
     */
    public void deselectAll(Collection<G> g) {
        requireNonNull(g);
        Set<G> old = getSelection();
        if (selected.removeAll(g)) {
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    /**
     * Toggle selection status of argument.
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

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">

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

    //endregion

}
