package com.googlecode.blaisemath.style

import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates.observable

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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
 * Provides delegates for draw style, label, label visibility, label style, and tooltip text. It is intended to be used
 * with objects that combine display of a primitive/graphics object and an accompanying label. The same styler can be
 * used for many different graphic objects.
 * @param <S> the type of source object
 */
class ObjectStyler<S> {

    var styler: (S) -> AttributeSet by notifyingObservable { AttributeSet() }
    var labelFilter: (S) -> Boolean by notifyingObservable { true }
    var labeler: (S) -> String by notifyingObservable { "" }
    var labelStyler: (S) -> AttributeSet by notifyingObservable { AttributeSet() }
    var tipper: (S) -> String by notifyingObservable { it.toString() }

    private val pcs = PropertyChangeSupport(this)

    //region DELEGATES

    fun style(src: S) = styler(src)
    fun label(src: S) = if (labelFilter(src)) labeler(src) else null
    fun labelStyle(src: S) = labelStyler(src)
    fun tooltip(src: S) = tipper(src)

    //endregion

    private fun <X> notifyingObservable(initialValue: X) = observable(initialValue) { prop, old, nue ->
        pcs.firePropertyChange(prop.name, old, nue)
    }
}