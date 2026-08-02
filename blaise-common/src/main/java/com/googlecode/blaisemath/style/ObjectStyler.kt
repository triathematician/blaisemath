package com.googlecode.blaisemath.style

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates

/*
* #%L
* BlaiseGraphics
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

/**
 * Provides delegates for draw style, label, label visibility, label style,
 * and tooltip text. It is intended to be used with objects that combine display
 * of a primitive/graphics object and an accompanying label. The same styler can
 * be used for many different graphic objects.
 *
 * @param <S> the type of source object
 */
open class ObjectStyler<S> {

    /** Delegate for point rendering. */
    var styleDelegate: ((S) -> AttributeSet)? by notifying(null)

    /** Delegate for point labels (only used if the styler returns a label style). */
    var labelDelegate: ((S) -> String)? by notifying(null)
    /** Delegate for point label styles. */
    var labelStyleDelegate: ((S) -> AttributeSet)? by notifying(null)
    /** Filter for selecting when labels are shown (true). If missing, all labels are shown. */
    var labelFilter: ((S) -> Boolean)? by notifying(null)

    /** Delegate for tooltips (with default using [#toString]). */
    var tooltipDelegate: ((S) -> String)? by notifying { it.toString() }

    //region CONSTANT SETTERS

    fun setStyleFixed(fixedStyle: AttributeSet) { styleDelegate = { fixedStyle } }
    fun setLabelFixed(fixedLabel: String) { labelDelegate = { fixedLabel } }
    fun setLabelStyleFixed(fixedStyle: AttributeSet) { styleDelegate = { fixedStyle } }
    fun setTooltipFixed(fixedTooltip: String) { tooltipDelegate = { fixedTooltip } }

    //endregion

    //region DELEGATE OPERATORS

    /** Get object style. */
    fun style(obj: S) = styleDelegate?.invoke(obj)

    /** Get object label. */
    fun label(obj: S): String? {
        val show = labelFilter?.invoke(obj) ?: true
        return when {
            show -> labelDelegate?.invoke(obj)
            else -> null
        }
    }

    /** Get object label style. */
    fun labelStyle(obj: S) = labelStyleDelegate?.invoke(obj)

    /** Get object tooltip. */
    fun tooltip(obj: S) = tooltipDelegate?.invoke(obj)

    //endregion

    //region PROPERTY EVENTS

    protected val pcs = PropertyChangeSupport(this)

    protected fun <X> notifying(initialValue: X) = Delegates.observable(initialValue) { property, oldValue: X, newValue: X ->
        pcs.firePropertyChange(property.name, oldValue, newValue)
    }

    fun removePropertyChangeListener(propertyName: String, listener: PropertyChangeListener) { pcs.removePropertyChangeListener(propertyName, listener) }
    fun removePropertyChangeListener(listener: PropertyChangeListener) { pcs.removePropertyChangeListener(listener) }
    fun addPropertyChangeListener(propertyName: String, listener: PropertyChangeListener) { pcs.addPropertyChangeListener(propertyName, listener) }
    fun addPropertyChangeListener(listener: PropertyChangeListener) { pcs.addPropertyChangeListener(listener) }

    //endregion

}