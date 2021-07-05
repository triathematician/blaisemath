package com.googlecode.blaisemath.coordinate

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
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
 * Tracks a change to a set of coordinate locations, in the form of a set of added
 * locations and a set of removed objects. These collections will be propagated as
 * received to listeners; this class makes no guarantees of collection safety.
 *
 * @param <S> type of object owning the coordinates
 * @param <C> coordinate type
 * @author Elisha Peterson
</C></S> */
class CoordinateChangeEvent<S, C>
/**
 * Initialize with given source object
 * @param src source of event
 */
(src: Any?) : EventObject(src) {
    /** Added coordinates  */
    private var added: MutableMap<S?, C?>? = null

    /** Removed coordinates  */
    private var removed: MutableSet<S?>? = null

    //endregion
    override fun toString(): String {
        return String.format("CoordinateChangeEvent[%d added,%d removed,source=%s]",
                if (added == null) 0 else added.size, if (removed == null) 0 else removed.size, source)
    }

    /**
     * Whether event indicates added coords
     * @return true if coordinates were added
     */
    fun isAddEvent(): Boolean {
        return added != null
    }

    /**
     * Whether event indicates removed coords
     * @return true if coordinates were removed
     */
    fun isRemoveEvent(): Boolean {
        return removed != null
    }

    /**
     * Get the collection of coordinate that were added
     * @return map whose keys are the objects and values are their coordinates
     */
    fun getAdded(): MutableMap<S?, C?>? {
        return added
    }

    /**
     * Get the collection of objects whose coordinates were removed
     * @return set of objects removed
     */
    fun getRemoved(): MutableSet<S?>? {
        return removed
    }

    companion object {
        //region FACTORY METHODS
        /**
         * Creates add event
         * @param <S> type of object owning the coordinates
         * @param <C> coordinate type
         * @param src source of event
         * @param added map of added objects, keys are objects/values are coordinates
         * @return add event
        </C></S> */
        fun <S, C> createAddEvent(src: Any?, added: MutableMap<S?, C?>?): CoordinateChangeEvent<S?, C?>? {
            val evt = CoordinateChangeEvent<S?, C?>(src)
            evt.added = added
            return evt
        }

        /**
         * Creates remove event
         * @param <S> type of object owning the coordinates
         * @param <C> coordinate type
         * @param src source of event
         * @param removed set of removed objects
         * @return remove event
        </C></S> */
        fun <S, C> createRemoveEvent(src: Any?, removed: MutableSet<S?>?): CoordinateChangeEvent<*, *>? {
            val evt = CoordinateChangeEvent<S?, C?>(src)
            evt.removed = removed
            return evt
        }

        /**
         * Creates add/remove event
         * @param <S> type of object owning the coordinates
         * @param <C> coordinate type
         * @param src source of event
         * @param added map of added objects, keys are objects/values are coordinates
         * @param removed set of removed objects
         * @return add/remove event
        </C></S> */
        fun <S, C> createAddRemoveEvent(src: Any?, added: MutableMap<S?, C?>?, removed: MutableSet<S?>?): CoordinateChangeEvent<S?, C?>? {
            val evt = CoordinateChangeEvent<S?, C?>(src)
            evt.added = added
            evt.removed = removed
            return evt
        }
    }
}