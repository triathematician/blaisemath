package com.googlecode.blaisemath.coordinate

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
*/

/**
 * Tracks a change to a set of coordinate locations, in the form of a set of added
 * locations and a set of removed objects. These collections will be propagated as
 * received to listeners; this class makes no guarantees of collection safety.
 * @param <S> type of object owning the coordinates
 * @param <C> coordinate type
 */
class CoordinateChangeEvent<S, C>(src: Any?, val added: Map<S, C>? = null, val removed: Set<S>? = null) : EventObject(src) {
    init { require(added != null || removed != null) }

    override fun toString() = String.format("CoordinateChangeEvent[%d added,%d removed,source=%s]",
            added?.size ?: 0, removed?.size ?: 0, source)

    val isAddEvent: Boolean
        get() = added != null
    val isRemoveEvent: Boolean
        get() = removed != null
}

/**
 * Receives updates regarding the locations of a collection of objects. Handlers
 * should be aware that the update may be invoked from any thread.
 * @param <S> type of object being located
 * @param <C> type of coordinate
 */
interface CoordinateListener<S, C> {
    /**
     * Called when coordinates/points are added or changed in a [CoordinateManager].
     * This method is called from the same thread that made the change.
     * @param evt description of what coordinates were added/removed/changed
     */
    fun coordinatesChanged(evt: CoordinateChangeEvent<S, C>)
}
