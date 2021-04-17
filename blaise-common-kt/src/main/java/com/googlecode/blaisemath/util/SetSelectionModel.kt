package com.googlecode.blaisemath.util
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
 */

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.util.*

/**
 * Tracks a set of objects, with convenience methods to adjust the set, and notifies listeners on changes.
 * @param <G> type of object that can be selected
 */
class SetSelectionModel<G> {

    private val selected = mutableSetOf<G>()

    var selection: Set<G>
        get() = Collections.unmodifiableSet(selected)
        set(value) {
            if (selected != value) {
                val old = selection
                selected.clear()
                selected.addAll(selection)
                pcs.firePropertyChange("selection", old, selection)
            }
        }
    val isEmpty
        get() = selected.isEmpty()

    private val pcs = PropertyChangeSupport(this)

    fun isSelected(g: G) = g in selected

    fun clearSelection() { selection = emptySet() }
    fun select(g: G) { selection = selected + g }
    fun selectAll(g: Collection<G>) { selection = selected + g }
    fun deselect(g: G) { selection = selected - g }
    fun deselectAll(g: Collection<G>) { selection = selected - g }
    fun toggleSelection(g: G) { if (isSelected(g)) deselect(g) else select(g) }

    //region EVENTS

    fun addPropertyChangeListener(listener: PropertyChangeListener) = pcs.addPropertyChangeListener(listener)
    fun removePropertyChangeListener(listener: PropertyChangeListener) = pcs.removePropertyChangeListener(listener)
    fun addPropertyChangeListener(propertyName: String, listener: PropertyChangeListener) = pcs.addPropertyChangeListener(propertyName, listener)
    fun removePropertyChangeListener(propertyName: String, listener: PropertyChangeListener) = pcs.removePropertyChangeListener(propertyName, listener)

    //endregion

}