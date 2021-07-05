package com.googlecode.blaisemath.util

import com.google.common.collect.ImmutableSet
import com.google.common.collect.Iterables
import com.google.common.collect.Sets
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.util.*

/*-
* #%L
* blaise-common
* --
* Copyright (C) 2014 - 2021 Elisha Peterson
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
*/ /**
 * Tracks a set of objects, with convenience methods to adjust the set, and
 * notifies listeners on changes.
 *
 * @param <G> type of object that can be selected
 * @author Elisha Peterson
</G> */
class SetSelectionModel<G> {
    private val selected: MutableSet<G?>? = Sets.newHashSet()
    protected val pcs: PropertyChangeSupport? = PropertyChangeSupport(this)
    //region PROPERTIES
    /**
     * Check if selection is empty.
     * @return true if empty
     */
    fun isEmpty(): Boolean {
        return selected.isEmpty()
    }

    /**
     * Return a copy of the selection.
     * @return copy of selected
     */
    fun getSelection(): MutableSet<G?>? {
        return ImmutableSet.copyOf(selected)
    }

    /**
     * Replace entire selection with argument.
     * @param selection new selection
     */
    fun setSelection(selection: MutableSet<G?>?) {
        if (!selection.containsAll(selected) || !selected.containsAll(selection)) {
            val old = getSelection()
            selected.clear()
            selected.addAll(selection)
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection())
        }
    }
    //endregion
    /**
     * Clear the selection.
     */
    fun clearSelection() {
        setSelection(emptySet())
    }

    /**
     * Add the given item to the selection.
     * @param g item to select
     */
    fun select(g: G?) {
        if (g != null && !selected.contains(g)) {
            val old = getSelection()
            selected.add(g)
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection())
        }
    }

    /**
     * Check if the given item is selected.
     * @param g item
     * @return true if selected
     */
    fun isSelected(g: G?): Boolean {
        return selected.contains(g)
    }

    /**
     * Adds all arguments to the current selection.
     * @param g elements to add
     */
    fun selectAll(g: MutableCollection<G?>?) {
        Objects.requireNonNull(g)
        if (!selected.containsAll(g)) {
            val old = getSelection()
            Iterables.addAll(selected, g)
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection())
        }
    }

    /**
     * Remove an item from the selection.
     * @param g item to remove
     */
    fun deselect(g: G?) {
        val old = getSelection()
        if (g != null && selected.remove(g)) {
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection())
        }
    }

    /**
     * Removes all arguments from the current selection.
     * @param g elements to remove
     */
    fun deselectAll(g: MutableCollection<G?>?) {
        Objects.requireNonNull(g)
        val old = getSelection()
        if (selected.removeAll(g)) {
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection())
        }
    }

    /**
     * Toggle selection status of argument.
     * @param g object to toggle
     */
    fun toggleSelection(g: G?) {
        if (g != null) {
            val old = getSelection()
            if (selected.contains(g)) {
                selected.remove(g)
            } else {
                selected.add(g)
            }
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection())
        }
    }

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    fun addPropertyChangeListener(listener: PropertyChangeListener?) {
        pcs.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener?) {
        pcs.removePropertyChangeListener(listener)
    }

    fun addPropertyChangeListener(propertyName: String?, listener: PropertyChangeListener?) {
        pcs.addPropertyChangeListener(propertyName, listener)
    }

    fun removePropertyChangeListener(propertyName: String?, listener: PropertyChangeListener?) {
        pcs.removePropertyChangeListener(propertyName, listener)
    } //endregion

    companion object {
        val SELECTION_PROPERTY: String? = "selection"
    }
}